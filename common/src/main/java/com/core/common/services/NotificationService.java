/*
 * Copyright 2007-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.core.common.services;

import com.core.datamodel.model.dbmodel.Notification;
import com.core.datamodel.model.wrapper.JSONNotificationWrapper;
import com.core.datamodel.model.wrapper.MessageBoxWrapper;
import org.hibernate.validator.constraints.Email;
import org.springframework.scheduling.annotation.Scheduled;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author allantan
 *
 */
public interface NotificationService{


	String createEmailNotification(Date notifyDate,@NotNull @Email String email, @NotNull String subject,@NotNull String message,@NotNull Notification.Medium medium,
								   @NotNull Notification.Status status, Long recipientUserId,@NotNull Long targetTypeId,@NotNull Long targetId, String replyTo,
								   String cc,String templateName,Map<String,Object> templateVariables);
	String createSmsNotification(Date notifyDate,@NotNull  String mobileNumber, @NotNull String subject,@NotNull String message,@NotNull Notification.Medium medium,
								 @NotNull Notification.Status status, Long recipientUserId,@NotNull Long targetTypeId,@NotNull Long targetId, String replyTo,
								 String cc,String templateName,Map<String,Object> templateVariables);

	//Executes the notification messages by sending either via email, SMS,
	@Scheduled
	void executeMailAndSmsNotification();

	@Scheduled
	void executePopupNotification();

	String buildMessage(String messageCode, Object[] params);
	boolean sendNotification(String topicName, String message) throws IOException ;

	List<JSONNotificationWrapper> notify(String userId);
	List<JSONNotificationWrapper> notify(String userId, int timezoneDiff);

	//Returns the total number of popup notification that is new and
	long countNewPopup(long userId);
	
	 // Clears all notifications as read.
	 void clearPopup(long userId);
	
	//Returns the message for the notification ajax call.
     Map<String, Object> getPopupNotification(long userId);
	
	//Returns the message for the notification ajax call with consideration to timezone
     Map<String, Object> getPopupNotification(long userId, int timezoneDiff);

	
	//Retrieves all the new notification to be displayed.
	List<Notification> findNewPopupNotification(long userId);



//region MessageBox
	List<MessageBoxWrapper> findMessageBoxWrapperByReceiverId(Map<String,Object> requestParams);
	Integer findNoSeenMessageBoxCountByReceiverId(Map<String, Object> requestParams);
//endregion MessageBox
}
