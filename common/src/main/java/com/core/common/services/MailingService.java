package com.core.common.services;

import java.io.File;
import java.util.Map;

/**
 * 
 * @author AJ
 * 
 */
public interface MailingService {

	void sendEmailByBody(String[] to, String[] cc, String[] bcc, String subject,String body);

	void sendEmailByTemplate(String[] toEmail, String subject, String template,Map<String, Object> templateVariables);

	void sendEmailByBody(String[] to, String[] cc, String[] bcc, String replyTo, String subject,String body);
	void sendEmailByTemplate(String[] to, String[] cc, String[] bcc, String replyTo,String subject,String template, Map<String, Object> templateVariables);

	void sendEmailByBody(String[] to, String[] cc, String[] bcc, String replyTo, String subject, String body, File[] attachments);
	void sendEmailByTemplate(	String[] to, String[] cc, String[] bcc, String replyTo, String subject, String template, Map<String, Object> templateVariables, File[] attachments) ;
	}
