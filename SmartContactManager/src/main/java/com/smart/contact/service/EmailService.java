package com.smart.contact.service;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class EmailService {

	public boolean sendEmail(String message, String subject, String to) {
		boolean f = false;

		String from = "learnCodeWithKD@gmail.com";

		String host = "smtp.gmail.com";

		Properties properties = System.getProperties();

		// set host

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step 1:get Session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication("learnCodeWithKD@gmail.com", "learn123@");
			}

		});

		session.setDebug(true);

		// Step 2: compose Message[text,multi media]

		MimeMessage mimeMessage = new MimeMessage(session);

		try {

			mimeMessage.setFrom(from);

			mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));

			mimeMessage.setSubject(subject);
			
			mimeMessage.setContent(message,"text/html");

			//mimeMessage.setText(message);

			// Send the message

			Transport.send(mimeMessage);

			System.out.println("Mail Send Successly");

			f = true;

		} catch (Exception e) {

			e.printStackTrace();
		}

		return f;
	}

}
