package net.hep.ami;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public class MailSingleton {
	/*---------------------------------------------------------------------*/

	private static final Properties m_properties = new Properties();

	private static final Authenticator m_authenticator = new Authenticator() {

		protected PasswordAuthentication getPasswordAuthentication() {

			return new PasswordAuthentication(
				ConfigSingleton.getProperty("email_user"),
				ConfigSingleton.getProperty("email_pass")
			);
		}
	};

	/*---------------------------------------------------------------------*/

	static {
		/*-----------------------------------------------------------------*/
		/* GET HOST, PORT AND MODE                                         */
		/*-----------------------------------------------------------------*/

		String host = ConfigSingleton.getProperty("email_host");
		String port = ConfigSingleton.getProperty("email_port");
		String mode = ConfigSingleton.getProperty("email_mode");

		/*-----------------------------------------------------------------*/
		/* CREATE PROPERTIES                                               */
		/*-----------------------------------------------------------------*/

		m_properties.setProperty("mail.transport.protocol", "smtp");

		m_properties.setProperty("mail.smtp.auth", "true");
		m_properties.setProperty("mail.smtp.host", (host));
		m_properties.setProperty("mail.smtp.port", (port));


		/****/ if(mode.equals("1")) {
			m_properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		} else if(mode.equals("2")) {
			m_properties.setProperty("mail.smtp.starttls.enable", "true");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void sendMessage(String from, String to, String cc, String subject, String text) throws Exception {
		/*-----------------------------------------------------------------*/
		/* CREATE SESSION                                                  */
		/*-----------------------------------------------------------------*/

		Session session = Session.getInstance(m_properties, m_authenticator);

		/*-----------------------------------------------------------------*/
		/* CREATE MESSAGE                                                  */
		/*-----------------------------------------------------------------*/

		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(from));

        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
		message.setSubject(subject);
		message.setText(text);

		/*-----------------------------------------------------------------*/
		/* SEND MESSAGE                                                    */
		/*-----------------------------------------------------------------*/

		Transport.send(message);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
