package com.core.common.services.impl;

import com.core.common.config.condition.MainInstanceContition;
import com.core.common.services.factory.SmsServiceFactory;
import com.core.datamodel.model.enums.ERoleType;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import com.core.datamodel.repository.MessageBoxRepository;
import com.core.exception.InvalidDataException;
import com.core.datamodel.model.dbmodel.Notification;
import com.core.datamodel.model.dbmodel.User;
import com.core.common.model.sms.SmsServiceResponse;
import com.core.datamodel.model.wrapper.JSONNotificationWrapper;
import com.core.datamodel.repository.NotificationRepository;
import com.core.common.services.*;
import com.core.common.util.Utils;
import com.core.services.CalendarService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.*;


@Service("notificationServiceImpl")
public class NotificationServiceImpl extends AbstractService implements NotificationService {


	private static Logger _log = LogManager
			.getLogger(NotificationServiceImpl.class);

	@Value("${app.notification.execute-limit}")
	private String limit;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	private CalendarService calendarService;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private MailingService mailingService;

	@Autowired
	private SmsServiceFactory smsServiceFactory;

	@Autowired
	ObjectMapper jsonMapper;


	@Autowired
	private MessageBoxRepository messageBoxRepository;


	@Override
	public String createEmailNotification(Date notifyDate, @Email String email, @NotNull String subject, @NotNull String message, @NotNull Notification.Medium medium,
										  @NotNull Notification.Status status, Long recipientUserId, @NotNull Long targetTypeId, @NotNull Long targetId,
										  String replyTo, String cc, String templateName, Map<String,Object> templateVariables){
        if (Utils.isStringSafeEmpty(email))
			return  Utils.getMessageResource("notification.email_required");
		if (recipientUserId!=null) {
			User user=userService.getUserInfo(recipientUserId);
			if (user!=null && !user.isSendEmail())
				return Utils.getMessageResource("notification.email.customerCancel");
		}

		Notification notification=new Notification();
		notification.setRecipientReference(email);
		return createNotification(notification,notifyDate,subject,message,medium,status, recipientUserId, targetTypeId,targetId,replyTo, cc,templateName,templateVariables);
	}

	@Override
	public String createSmsNotification(Date notifyDate,@NotNull  String mobileNumber, @NotNull String subject,@NotNull String message,@NotNull Notification.Medium medium,
								 @NotNull Notification.Status status, Long recipientUserId,@NotNull Long targetTypeId,@NotNull Long targetId, String replyTo,
								 String cc,String templateName,Map<String,Object> templateVariables){

		if (Utils.isStringSafeEmpty(mobileNumber))
			return  Utils.getMessageResource("notification.mobile_required");

		if (recipientUserId!=null) {
			User user=userService.getUserInfo(recipientUserId);
			if (user!=null && !user.isSendSms())
				return Utils.getMessageResource("notification.mobile.customerCancel");
		}

		Notification notification=new Notification();
		notification.setRecipientReference(mobileNumber);
		return createNotification(notification,notifyDate,subject,message,medium,status, recipientUserId, targetTypeId,targetId,replyTo, cc,templateName,templateVariables);
	}

	private String createNotification(Notification notification,Date notifyDate,@NotNull String subject,@NotNull String message,@NotNull Notification.Medium medium,
									 @NotNull Notification.Status status, Long recipientUserId,@NotNull Long targetTypeId,@NotNull Long targetId, String replyTo,
									 String cc,String templateName,Map<String,Object> templateVariables){
		notification.setCreateDate(new Date());
		notification.setNotifyDate(notifyDate);
		notification.setMedium(medium.toString());
		notification.setSubject(subject);
		notification.setMessage(message);
		notification.setStatus(status.toString());
		notification.setRecipientUserId(recipientUserId);
		notification.setTargrtTypeId(targetTypeId);
		notification.setEmailReplyTo(replyTo);
		notification.setEmailCC(cc);
		notification.setTargetId(targetId);
		notification.setTemplateName(templateName);

		if (templateVariables != null) {
			try {
				notification.setTemplateData(jsonMapper.writeValueAsString(templateVariables));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InvalidDataException("Json Mapper Error", "notification.templateVariables.save_error");
			}
		}
		notificationRepository.save(notification);
		return null;
	}


	@Scheduled(fixedDelayString = "${app.notification.delay}")
	@Transactional
	@Override
	public void executeMailAndSmsNotification() {
		if (!MainInstanceContition.mainInstance)
			return;
		int max = Utils.convertToInt(limit, 20);

		List<Notification> notifications = notificationRepository.findNewEmailAndSmsNotification(gotoPage(0,max));
		if (notifications == null || notifications.isEmpty())
			return;
		for (Notification n:notifications) {
			_log.info("Processing notification for user "
					+ n.getRecipientUserId() + " with medium "
					+ n.getMedium());

			Notification.Status status = null;
			boolean processed = false;
			StringBuffer remarks = new StringBuffer();
			try {
				if ("EMAIL".equals(n.getMedium())) {
					n.setStatus(Notification.Status.IN_PROCESS.toString());
					notificationRepository.save(n);
					// send email
					if (n.getSubject() == null)
						n.setSubject("");
					if (Utils.isStringSafeEmpty(n.getAttachment())) {
						if (!Utils.isStringSafeEmpty(n.getTemplateName()) && !Utils.isStringSafeEmpty(n.getTemplateData())) {
							Map<String, Object> tamplateData = new HashMap<String, Object>();
							tamplateData = jsonMapper.readValue(n.getTemplateData(), HashMap.class);

							mailingService.sendEmailByTemplate(
									n.getRecipientReference().split(","),
									(n.getEmailCC()==null ? null : n.getEmailCC().split(",")),
									new String[]{}, n.getEmailReplyTo(),
									n.getSubject(),  n.getTemplateName(),tamplateData );
						}
						else {
							mailingService.sendEmailByBody(
									n.getRecipientReference().split(","),
									(n.getEmailCC()==null ? null : n.getEmailCC().split(",")),
									new String[]{}, n.getEmailReplyTo(),
									n.getSubject(), n.getMessage());
						}
					} else {
						// send with attachment
						File attachment = new File(n.getAttachment());
						if (!Utils.isStringSafeEmpty(n.getTemplateName()) && !Utils.isStringSafeEmpty(n.getTemplateData())) {
							Map<String, Object> tamplateData = new HashMap<String, Object>();
							tamplateData = jsonMapper.readValue(n.getTemplateData(), HashMap.class);

							mailingService.sendEmailByTemplate(
									n.getRecipientReference().split(","),
									(n.getEmailCC()==null ? null : n.getEmailCC().split(",")),
									new String[]{}, n.getEmailReplyTo(),
									n.getSubject(),  n.getTemplateName(),tamplateData,new File[] { attachment } );
						}
						else {
							mailingService.sendEmailByBody(
									n.getRecipientReference().split(","),
									(n.getEmailCC()==null ? null : n.getEmailCC().split(",")),
									new String[] {}, n.getEmailReplyTo(),
									n.getSubject(), n.getMessage(),
									new File[] { attachment });
						}
					}

					status = Notification.Status.PROCESSED;
					processed = true;
					remarks.append("Email successfully sent to "
							+ n.getRecipientReference() + ".\n");
				}
				if ("SMS".equals(n.getMedium())) {
					n.setStatus(Notification.Status.IN_PROCESS.toString());
					notificationRepository.save(n);
					// send SMS
					SmsServiceResponse smsServiceResponse=smsServiceFactory.getSmsService().send(new String[] { n.getRecipientReference()},n.getMessage(),false);
					status = Notification.Status.PROCESSED;
					processed = true;
					remarks.append("Sms successfully sent to "
							+ n.getRecipientReference() + ".\n");
				}
				if ("BASE_SMS".equals(n.getMedium())) {
					n.setStatus(Notification.Status.IN_PROCESS.toString());
					notificationRepository.save(n);
					// send SMS
					SmsServiceResponse smsServiceResponse=smsServiceFactory.getSmsService().baseSend(n.getRecipientReference(),n.getMessage(), Utils.parsInt(n.getTemplateName()));
					status = Notification.Status.PROCESSED;
					processed = true;
					remarks.append("Sms successfully sent to "
							+ n.getRecipientReference() + ".\n");
				}

				if (processed) {
					_log.info("Processing EMAIL & SMS & BASE_SMS notification ... Setting STATUS to PROCESSED");
					if (status != null)
						n.setStatus(status.toString());
					n.setNotifyDate(new Date());
					n.setRemarks(remarks.toString());
					notificationRepository.save(n);
				}
			} catch (Exception e) {
				_log.error("Error encountered while sending notification", e);
				remarks.append(e.getMessage());
				if (remarks.length() > 2999)
					n.setRemarks(remarks.substring(0, 2999));
				else
					n.setRemarks(remarks.toString() + "\n");
				n.setStatus(Notification.Status.FAILED.toString());
				n.setNotifyDate(new Date());
				notificationRepository.save(n);
			}
		}
	}


	@Scheduled(fixedDelayString = "${app.notification.delay}")
	@Transactional
	@Override
	public void executePopupNotification() {
		if (!MainInstanceContition.mainInstance)
			return;
		int max = Utils.convertToInt(limit, 20);
		for (int i = 0; i < max; i++) {
			List<Long> recipientUserIds = notificationRepository.findNewPopupNotification(gotoPage(0,1));
			if (recipientUserIds == null || recipientUserIds.isEmpty())
				break;
			_log.info("Processing POPUP notification ... Setting STATUS to IN_PROCESS");
			for (Long userId : recipientUserIds){
				List<JSONNotificationWrapper> jsonNotificationWrappers=notify("" + userId);
				if (jsonNotificationWrappers!=null) {
					List<Long> notifyIds = new ArrayList<>();
					for (JSONNotificationWrapper jsonNotificationWrapper : jsonNotificationWrappers) {
						notifyIds.add(jsonNotificationWrapper.getId());
					}
					if (notifyIds != null && notifyIds.isEmpty())
						notificationRepository.editNotificationByIds(Notification.Status.PROCESSED.toString(), new Date(), notifyIds);
				}
			}
	    }
    }

	@Override
	public List<JSONNotificationWrapper> notify(String userId){
		return notify(userId, 0);
	}


	public boolean sendNotification(String topicName, String message) throws IOException {
		return false;
	}
	@Override
	public List<JSONNotificationWrapper> notify(String userId, int timezoneDiff) {
		_log.info("Trying to notify user with userId " + userId
				+ ". Retrieving Broadcaster...");
		return null;
	}

	@Override
	public String buildMessage(String messageCode, Object[] params) {
		return messageSource.getMessage(messageCode, params,
				Locale.getDefault());
	}

	@Override
	public long countNewPopup(long userId) {
		return notificationRepository.countNewPopup(userId);
	}

	@Override
	public void clearPopup(long userId) {
		notificationRepository.clearPopup(userId);
	}

	@Override
	public Map<String, Object> getPopupNotification(long userId) {
		return buildNotification(userId, 0, "");
	}

	@Override
	public Map<String, Object> getPopupNotification(long userId,
			int timezoneDiff) {
		return buildNotification(userId, timezoneDiff, "");
	}

	private Map<String, Object> buildNotification(Long userId,
			int timezoneDiff, String mode) {
		// Let's manually build the json
		List<Notification> notifs = null;
		long count = 0;
		notifs = this.findNewPopupNotification(userId);
		count = this.countNewPopup(userId);
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("notifyCount", count);
		List<JSONNotificationWrapper> notifications = new ArrayList<JSONNotificationWrapper>();
		Date adjusted = DateUtils.addHours(new Date(), timezoneDiff);
		//PrettyTime pt = new PrettyTime(adjusted);
		for (Notification n : notifs) {
			JSONNotificationWrapper jn = new JSONNotificationWrapper();
			jn.setId(n.getId());
			//jn.setTimeWhen(pt.format(n.getNotifyDate()));
			jn.setTimeWhen(calendarService.getNormalDateTimeFormat(adjusted));
			jn.setTargrtTypeId(n.getTargrtTypeId());
			jn.setTargetId(n.getTargetId());
			jn.setMedium(n.getMedium());
			jn.setMessage(n.getMessage());
			notifications.add(jn);

		}
		result.put("notifications", notifications);
		result.put("mode", mode);
		return result;
	}

	@Override
	public List<Notification> findNewPopupNotification(long userId) {
		return notificationRepository.findNewPopupNotification(userId);
	}




//region MessageBox

	@Override
	public List<MessageBoxWrapper> findMessageBoxWrapperByReceiverId(Map<String, Object> requestParams) {
		Long userId = Utils.getAsLongFromMap(requestParams, "userId", false,Utils.getCurrentUserId());
		Integer start = Utils.getAsIntegerFromMap(requestParams, "start", false);
		Integer count = Utils.getAsIntegerFromMap(requestParams, "count", false);
		Boolean noSeen = Utils.getAsBooleanFromMap(requestParams, "noSeen", false);


		if(!hasRoleType(ERoleType.ADMIN))
			userId=Utils.getCurrentUserId();

		if(noSeen)
		   return  messageBoxRepository.findNoSeenWrapperByReceiverId(userId,Utils.gotoPage(start,count));
		else
		   return  messageBoxRepository.findWrapperByReceiverId(userId,Utils.gotoPage(start,count));
	}

	@Override
	public Integer findNoSeenMessageBoxCountByReceiverId(Map<String, Object> requestParams) {
		Long receiverId = Utils.getAsLongFromMap(requestParams, "userId", false,Utils.getCurrentUserId());
		if(!hasRoleType(ERoleType.ADMIN))
			receiverId=Utils.getCurrentUserId();

		return  messageBoxRepository.findNoSeenCountByReceiverId(receiverId);
	}

	//endregion MessageBox

}
