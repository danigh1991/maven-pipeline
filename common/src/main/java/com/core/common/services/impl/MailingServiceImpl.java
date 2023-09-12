/*
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.    
 */
package com.core.common.services.impl;

import com.core.common.services.CommonService;
import com.core.common.services.MailingService;
import com.core.common.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
//import org.springframework.ui.velocity.VelocityEngineUtils;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("mailingServiceImpl")
public class MailingServiceImpl  implements MailingService{
	
	private static Logger _log = LogManager.getLogger(MailingServiceImpl.class);
	
	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private CommonService commonService;

	@Value("${app.support.email}")
	private String fromEmail;
	
	@Value("${app.support.name}")
	private String fromName;

	//@Value("${app.mail.discount_after_confirmed}")
	//@Value("${app.mail.discount_after_reg}")
	//@Value("${app.mail.discount_after_changed}")
	//@Value("${app.mail.users_change_pass}")
	//@Value("${app.mail.users_welcome_after_reg}")


	@Value("${app.mail.default-template}")
	private String mailVmTemplate;

	@Value("${app.domain_url}")
	private String domain_url;
	
/*
	@Autowired
	private VelocityEngine velocityEngine;
*/

	@Autowired
	private SpringTemplateEngine templateEngine;


	private List<String> imagesPath;


	@Override
	public void sendEmailByBody(String[] to, String[] cc, String[] bcc, String subject,String body) {
		sendEmailByBody(to, cc, bcc, "", subject, body);
	}


	@Override
	public void sendEmailByTemplate(String[] to, String subject, String template, Map<String, Object> templateVariables) {
		sendEmailByTemplate(to, new String[] {}, new String[] {}, "", subject, template,templateVariables);
	}

	@Override
	public void sendEmailByBody(String[] to, String[] cc, String[] bcc, String replyTo,
						  String subject,String body) {
		sendEmailByBody(to, cc, bcc, replyTo, subject, body, null);
	}

	@Override
	public void sendEmailByTemplate(String[] to, String[] cc, String[] bcc, String replyTo,
							String subject,String template, Map<String, Object> templateVariables) {
		sendEmailByTemplate(to, cc, bcc, replyTo, subject, template,templateVariables, null);
	}

	@Override
	public void sendEmailByBody(String[] to, String[] cc, String[] bcc, String replyTo, String subject, String body, File[] attachments) {
		    Map<String, Object> templateVariables = new HashMap<String, Object>();
			templateVariables.put("messageTitle", subject);
			templateVariables.put("messageBody", body);
			templateVariables.put("applicationName", commonService.getMyBrandName());
			templateVariables.put("unsubscribe", commonService.getMySiteDomain());


			//after discount register
/*			String store_name="مرکز زیبایی آیریس";
			String main_message="تخفیف  شما با مشحصات زیر در سیستم تیجور ثبت شد و در انتظار تایید توسط مدیر تیجور می باشد.";
			String discount_title="تخفیف اولتراپی و لیفت";
			String body_message2="ایمیل تایید یا عدم تایید برای شما ارسال می گردد";
			String store_link=Utils.getDomainUrl() +"/branch/%d9%85%d8%b1%da%a9%d8%b2-%d8%b2%db%8c%d8%a8%d8%a7%db%8c%db%8c-%d8%a7%d9%93%db%8c%d8%b1%db%8c%d8%b3.p1379";
			String store_tumbnail=Utils.getDomainUrl()+"/img/s/798?v=1544479837000";
			String discount_description="تخفیف ویژه اولتراپی و لیفت 3 بعدی صورت\n" +
					"پکیج زیبایی و جوانسازی صورت\n" +
					"نسل جدید هایفوتراپی با عمق نفوذ 6 میلیمتر ساخت کشور آمریکا\n" +
					"لیفت فوری و واقعی";

			templateVariables.put("store_name", store_name);
			templateVariables.put("main_message", main_message);
			templateVariables.put("discount_title", discount_title);
			templateVariables.put("body_message2", body_message2);
			templateVariables.put("store_link", store_link);
			templateVariables.put("store_tumbnail", store_tumbnail);
			templateVariables.put("discount_description", discount_description);
			templateVariables.put("domain_url",domain_url);*/



		//after discount confirmed
/*
		String store_name="مرکز زیبایی آیریس";
		String main_message="تخفیف  شما با مشحصات زیر در سیستم تیجور تایید شد و و برای همه بازدید کنندگان سایت تیجور قابل رویت می باشد";
		String discount_title="تخفیف اولتراپی و لیفت";
		String body_message2="";
		String store_link=Utils.getDomainUrl()+"/branch/%d9%85%d8%b1%da%a9%d8%b2-%d8%b2%db%8c%d8%a8%d8%a7%db%8c%db%8c-%d8%a7%d9%93%db%8c%d8%b1%db%8c%d8%b3.p1379";
		String store_tumbnail=Utils.getDomainUrl()+"/img/s/798?v=1544479837000";
		String discount_description="تخفیف ویژه اولتراپی و لیفت 3 بعدی صورت\n" +
				"پکیج زیبایی و جوانسازی صورت\n" +
				"نسل جدید هایفوتراپی با عمق نفوذ 6 میلیمتر ساخت کشور آمریکا\n" +
				"لیفت فوری و واقعی";

		templateVariables.put("store_name", store_name);
		templateVariables.put("main_message", main_message);
		templateVariables.put("discount_title", discount_title);
		templateVariables.put("body_message2", body_message2);
		templateVariables.put("store_link", store_link);
		templateVariables.put("store_tumbnail", store_tumbnail);
		templateVariables.put("discount_description", discount_description);
		templateVariables.put("domain_url",domain_url);
*/

		//after discount changed
/*		String store_name="مرکز زیبایی آیریس";
		String main_message="تخفیف  شما با مشحصات زیر در سیستم تیجور تغییرات داشته است و لازم است تغیرات توسط مدیر تایید گردد";
		String discount_title="تخفیف اولتراپی و لیفت";
		String body_message2="ایمیل تایید یا عدم تایید برای شما ارسال می گردد";
		String store_link=Utils.getDomainUrl()+"/branch/%d9%85%d8%b1%da%a9%d8%b2-%d8%b2%db%8c%d8%a8%d8%a7%db%8c%db%8c-%d8%a7%d9%93%db%8c%d8%b1%db%8c%d8%b3.p1379";
		String store_tumbnail=Utils.getDomainUrl()+"/img/s/798?v=1544479837000";
		String discount_description="تخفیف ویژه اولتراپی و لیفت 3 بعدی صورت\n" +
				"پکیج زیبایی و جوانسازی صورت\n" +
				"نسل جدید هایفوتراپی با عمق نفوذ 6 میلیمتر ساخت کشور آمریکا\n" +
				"لیفت فوری و واقعی";

		templateVariables.put("store_name", store_name);
		templateVariables.put("main_message", main_message);
		templateVariables.put("discount_title", discount_title);
		templateVariables.put("body_message2", body_message2);
		templateVariables.put("store_link", store_link);
		templateVariables.put("store_tumbnail", store_tumbnail);
		templateVariables.put("discount_description", discount_description);
		templateVariables.put("domain_url",domain_url);*/


		//user change password request
/*		String username="گلابی";
		String main_message="شما جهت بازیابی و کلمه عبور خود در سیستم تیجور اقدام نموده اید. خواهشمند است جهت بازیابی کلمه عبور و ادامه کار از طریق کلیک بر روی لینک زیر ادامه دهید";
		String changpasslink=Utils.getDomainUrl()+"/branch/%d9%85%d8%b1%da%a9%d8%b2-%d8%b2%db%8c%d8%a8%d8%a7%db%8c%db%8c-%d8%a7%d9%93%db%8c%d8%b1%db%8c%d8%b3.p1379";
		String useravatar=Utils.getDomainUrl()+"/img/s/798?v=1544479837000";
		String changepasslinkmessage="اعتبار این لینک 24 ساعت می باشد و پس از این مدت لینک غیر قابل استفاده بوده و جهت تغییر کلمه عبور لازم است مجددا در خواست خود را از طریق بخش فراموشی کلمه عبور سایت تیجور ارسال نمایید";

		templateVariables.put("username", username);
		templateVariables.put("main_message", main_message);
		templateVariables.put("changpasslink", changpasslink);
		templateVariables.put("useravatar", useravatar);
		templateVariables.put("changepasslinkmessage", changepasslinkmessage);
		templateVariables.put("domain_url",domain_url);*/

		//user welcome after register
/*		String username="گلابی";
		String main_message="عضویت شما در سیستم تیجور با موفقیت انجام شد";
		String useravatar=Utils.getDomainUrl()+"/img/s/798?v=1544479837000";
		String offermessage="در صورتی که صاحب کسب و کاری هستید و  نسبت به ثبت اطلاعات کسب و کار خود و تخفیف های مربوط به کالا و خدمات خود در سیستم تیجور اقدام کنید. بیشتر خدمات تیجور برای صاحبان کسب و کار کاملا رایگان می باشد. در ادامه باخدمات تیجور بیشتر آشنا شوید";

		templateVariables.put("username", username);
		templateVariables.put("main_message", main_message);
		templateVariables.put("useravatar", useravatar);
		templateVariables.put("offermessage", offermessage);
		templateVariables.put("domain_url",domain_url);*/



		sendEmailByTemplate(to, cc, bcc, replyTo, subject, mailVmTemplate,templateVariables, attachments);

	}

	@Override
	public void sendEmailByTemplate(String[] to, String[] cc, String[] bcc, String replyTo,
						  String subject, String template, Map<String, Object> templateVariables, File[] attachments) {
		try {

			final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

			final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
			
			mimeMessageHelper.setTo(toInetAddress(to));	
			InternetAddress[] ccAddresses = toInetAddress(cc);
			if (ccAddresses!=null) mimeMessageHelper.setCc(ccAddresses);
			InternetAddress[] bccAddresses = toInetAddress(bcc);
			if (bccAddresses!=null) mimeMessageHelper.setBcc(bccAddresses);
			if (!Utils.isStringSafeEmpty(replyTo)) mimeMessageHelper.setReplyTo(replyTo);



			Context context = new Context();
			context.setVariables(templateVariables);
			String body = templateEngine.process(template, context);

			//String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/" + template,"UTF-8", templateVariables);
			//String body = templateVariables.toString();


			/*Map<String, Object> templateVariables = new HashMap<String, Object>();
			
			templateVariables.put("messageTitle", subject);
			templateVariables.put("messageBody",body);
			
			StringWriter writer = new StringWriter();
			VelocityEngineUtils.mergeTemplate(velocityEngine, mailVmTemplate, "UTF-8", templateVariables, writer);
*/
			mimeMessageHelper.setFrom(new InternetAddress(this.fromEmail,this.fromName));
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(body, true);
			
			// check for attachment
			if (attachments!= null && attachments.length > 0) {
				for (File attachment:attachments) {
					mimeMessageHelper.addAttachment(attachment.getName(), attachment);					
				}				
			}
			
			/**
			 * The name of the identifier should be image
			 * the number after the image name is the counter 
			 * e.g. <img src="cid:image1" />
			 */
			if(imagesPath != null && imagesPath.size() > 0) {
				int x = 1;
				for(String path : imagesPath) {
					FileSystemResource res = new FileSystemResource(new File(path));
					String imageName = "image" + x;
					mimeMessageHelper.addInline(imageName, res);
					x++;
				}
			}

			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			_log.error(e, e);
		} catch (UnsupportedEncodingException uee) {
			_log.error(uee, uee);
		}
	}
	
	public InternetAddress[] toInetAddress(String [] strings) throws AddressException {
		if (strings == null)
			return null;
		int count = 0;
		for(int x = 0; x < strings.length; x++) {
			if (Utils.isStringSafeEmpty(strings[x]) || strings[x].trim().length()==0)
				strings[x] = null;
			else
				count++;				
		}
		if (count==0) return null;
		InternetAddress[] internetAddress = new InternetAddress[count];
		for(int x = 0; x < strings.length; x++) {
			if (strings[x] != null) 
				internetAddress[x] = new InternetAddress(strings[x]);
		}		
		return internetAddress;
	}
}