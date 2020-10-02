package com.example.imageclassifierclient.utility;

import java.io.File;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailUtility {

	private String email;
	private String password;

	public EmailUtility(String username, String password) {
		this.email = username;
		this.password = password;
	}

	public void send(File attachment, String prediction, String rating)
	{
		String host = "smtp.gmail.com";

		Properties properties = System.getProperties();

		properties.setProperty("mail.smtp.host", host);
		properties.put("mail.smtp.user", email);
		properties.put("mail.smtp.password", password);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.starttls.enable","true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(email, password);
			}
		});


		try{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(email));
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(email));

			message.setSubject("Image classifier application feedback");

			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText("A user has sent a feedback regarding the classification.\n\nThe received prediction: "
					+ prediction + "\n"
					+ "The rating given by the user: "
					+ rating + "\n"
					+ "\nThe image in question can be found in the attachment.");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attachment.getPath());
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("feedback image");
			multipart.addBodyPart(messageBodyPart);


			MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
			mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");

			message.setContent(multipart);
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
