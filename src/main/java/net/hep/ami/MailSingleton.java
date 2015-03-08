package net.hep.ami;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public class MailSingleton {
	/*---------------------------------------------------------------------*/

	private static final String m_host = ConfigSingleton.getProperty("email_host");
	private static final String m_port = ConfigSingleton.getProperty("email_port");
	private static final String m_user = ConfigSingleton.getProperty("email_user");
	private static final String m_pass = ConfigSingleton.getProperty("email_pass");

	/*---------------------------------------------------------------------*/

	public static void sendMessage(String from, String to, String cc, String subject, String text) throws Exception {
		/*-----------------------------------------------------------------*/
		/* CREATE PROPERTIES                                               */
		/*-----------------------------------------------------------------*/

		Properties properties = new Properties(); 

		properties.setProperty("mail.transport.protocol", "smtp"); 

		properties.setProperty("mail.smtp.host", m_host); 
		properties.setProperty("mail.smtp.port", m_port);
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.starttls.enable", "true");

		/*-----------------------------------------------------------------*/
		/* CREATE AUTHENTICATOR                                            */
		/*-----------------------------------------------------------------*/

		Authenticator authenticator = new Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(m_user, m_pass);
			}
		};

		/*-----------------------------------------------------------------*/
		/* CREATE SESSION                                                  */
		/*-----------------------------------------------------------------*/

		Session session = Session.getInstance(properties, authenticator);

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
