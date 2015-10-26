package net.hep.ami;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public class MailSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Properties m_properties = new Properties();

	/*---------------------------------------------------------------------*/

	private static final Authenticator m_authenticator = new Authenticator()
	{
		@Override
		protected PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(
				ConfigSingleton.getProperty("email_user"),
				ConfigSingleton.getProperty("email_pass")
			);
		}
	};

	/*---------------------------------------------------------------------*/

	static
	{
		/*-----------------------------------------------------------------*/
		/* GET PROTOCOL, HOST AND PORT                                     */
		/*-----------------------------------------------------------------*/

		String mode = ConfigSingleton.getProperty("email_mode");
		String host = ConfigSingleton.getProperty("email_host");
		String port = ConfigSingleton.getProperty("email_port");

		/*-----------------------------------------------------------------*/
		/* CREATE PROPERTIES                                               */
		/*-----------------------------------------------------------------*/

		m_properties.setProperty("mail.smtp.host", (host));
		m_properties.setProperty("mail.smtp.port", (port));
		m_properties.setProperty("mail.smtp.auth", "true");

		/**/ if(mode.equalsIgnoreCase("ssl"))
		{
			m_properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		else if(mode.equalsIgnoreCase("tls"))
		{
			m_properties.setProperty("mail.smtp.starttls.enable", "true");
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void sendMessage(String from, String to, String cc, String subject, String text) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE SESSION                                                  */
		/*-----------------------------------------------------------------*/

		Session session = Session.getInstance(m_properties, m_authenticator);

		/*-----------------------------------------------------------------*/
		/* CREATE MESSAGE                                                  */
		/*-----------------------------------------------------------------*/

		MimeMessage mimeMessage = new MimeMessage(session);

		mimeMessage.setFrom(new InternetAddress(from));

		mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		mimeMessage.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
		mimeMessage.setSubject(subject);
		mimeMessage.setText(text);

		/*-----------------------------------------------------------------*/
		/* SEND MESSAGE                                                    */
		/*-----------------------------------------------------------------*/

		Transport.send(mimeMessage);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
