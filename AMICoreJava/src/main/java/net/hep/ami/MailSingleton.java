package net.hep.ami;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public class MailSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Authenticator s_authenticator = new Authenticator()
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

	private MailSingleton() {}

	/*---------------------------------------------------------------------*/

	private static Properties getProperties()
	{
		Properties result = new Properties();

		/*-----------------------------------------------------------------*/
		/* SET PROPERTIES                                                  */
		/*-----------------------------------------------------------------*/

		String host = ConfigSingleton.getProperty("email_host");
		String port = ConfigSingleton.getProperty("email_port");
		String mode = ConfigSingleton.getProperty("email_mode");

		/*-----------------------------------------------------------------*/

		result.setProperty("mail.smtp.host", (host));
		result.setProperty("mail.smtp.port", (port));
		result.setProperty("mail.smtp.auth", "true");

		/**/ if("ssl".equalsIgnoreCase(mode))
		{
			result.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		else if("tls".equalsIgnoreCase(mode))
		{
			result.setProperty((("mail.smtp.starttls.enable")), ((((((((((((("true"))))))))))))));
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static void sendMessage(String from, String to, String cc, String subject, String text) throws Exception
	{
		sendMessage(from, to, cc, subject, text, null);
	}

	/*---------------------------------------------------------------------*/

	public static void sendMessage(String from, String to, String cc, String subject, String text, @Nullable BodyPart[] bodyParts) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CREATE SESSION                                                  */
		/*-----------------------------------------------------------------*/

		Session session = Session.getInstance(getProperties(), s_authenticator);

		/*-----------------------------------------------------------------*/
		/* CREATE MESSAGE                                                  */
		/*-----------------------------------------------------------------*/

		MimeMessage mimeMessage = new MimeMessage(session);

		mimeMessage.setFrom(new InternetAddress(from));
		mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		mimeMessage.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
		mimeMessage.setSubject(subject);

		/*-----------------------------------------------------------------*/

		if(bodyParts != null)
		{
			/*-------------------------------------------------------------*/

			BodyPart mainPart = new MimeBodyPart();

			mainPart.setText(text);

			/*-------------------------------------------------------------*/

			Multipart multipart = new MimeMultipart();

			multipart.addBodyPart(mainPart);
			for(BodyPart bodyPart: bodyParts)
			multipart.addBodyPart(bodyPart);

			mimeMessage.setContent(multipart);

			/*-------------------------------------------------------------*/
		}
		else
		{
			mimeMessage.setText(text);
		}

		/*-----------------------------------------------------------------*/
		/* SEND MESSAGE                                                    */
		/*-----------------------------------------------------------------*/

		Transport.send(mimeMessage);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
