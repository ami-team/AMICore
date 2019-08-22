package net.hep.ami;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import net.hep.ami.utility.*;

public class MailSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@org.jetbrains.annotations.Contract(pure = true)
	private MailSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void sendMessage(String from, @Nullable String to, @Nullable String cc, @NotNull String subject, @NotNull String text) throws Exception
	{
		sendMessage(from, to, cc, subject, text, null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void sendMessage(String from, @Nullable String to, @Nullable String cc, @NotNull String subject, @NotNull String text, @Nullable BodyPart[] bodyParts) throws Exception
	{
		String host = ConfigSingleton.getProperty("email_host");
		String port = ConfigSingleton.getProperty("email_port");
		String mode = ConfigSingleton.getProperty("email_mode");
		String user = ConfigSingleton.getProperty("email_user");
		String pass = ConfigSingleton.getProperty("email_pass");

		if(host.isEmpty()
		   ||
		   port.isEmpty()
		   ||
		   mode.isEmpty()
		   ||
		   user.isEmpty()
		   ||
		   pass.isEmpty()
		 ) {
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE SESSION                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		Properties properties = new Properties();

		properties.setProperty("mail.smtp.port", port);
		properties.setProperty("mail.smtp.auth", "true");

		/**/ if("1".equalsIgnoreCase(mode))
		{
			properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		else if("2".equalsIgnoreCase(mode))
		{
			properties.setProperty(  "mail.smtp.starttls.enable"  , /*--------*/ "true" /*--------*/);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Session session = Session.getInstance(properties);

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE MESSAGE                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		MimeMessage mimeMessage = new MimeMessage(session);

		/*------------------------------------------------------------------------------------------------------------*/

		mimeMessage.setFrom(new InternetAddress(from));

		if(to != null) {
			mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		}

		if(cc != null) {
			mimeMessage.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
		}

		mimeMessage.setSubject(subject);

		/*------------------------------------------------------------------------------------------------------------*/

		BodyPart mainPart = new MimeBodyPart();

		mainPart.setText(text);

		/*------------------------------------------------------------------------------------------------------------*/

		Multipart multipart = new MimeMultipart();

		multipart.addBodyPart(mainPart);

		/*------------------------------------------------------------------------------------------------------------*/

		if(bodyParts != null)
		{
			for(BodyPart bodyPart: bodyParts)
			{
				if(bodyPart != null)
				{
					multipart.addBodyPart(bodyPart);
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		mimeMessage.setContent(multipart);

		/*------------------------------------------------------------------------------------------------------------*/
		/* SEND MESSAGE                                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		Transport transport = session.getTransport("smtp");

		transport.connect(host, user, pass);

		try
		{
			transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
		}
		finally
		{
			transport.close();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
