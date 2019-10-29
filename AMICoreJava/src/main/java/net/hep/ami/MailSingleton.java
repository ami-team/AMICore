package net.hep.ami;

import org.simplejavamail.email.*;
import org.simplejavamail.mailer.*;
import org.simplejavamail.mailer.config.*;

import org.jetbrains.annotations.*;

public class MailSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public static class Attachment
	{
		protected final String m_name;
		protected final String m_mime;
		protected final byte[] m_data;

		@Contract(pure = true)
		public Attachment(String name, String mime, byte[] data)
		{
			m_name = name;
			m_mime = mime;
			m_data = data;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private MailSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void sendMessage(@NotNull String from, @Nullable String to, @Nullable String cc, @NotNull String subject, @NotNull String text) throws Exception
	{
		sendMessage(from, to, cc, subject, text, null);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void sendMessage(@NotNull String from, @Nullable String to, @Nullable String cc, @NotNull String subject, @NotNull String text, @Nullable Attachment[] attachments) throws Exception
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
		/* CREATE MAILER                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		MailerBuilder.MailerRegularBuilder mailerBuilder = MailerBuilder.withSMTPServer(host, Integer.parseInt(port), user, pass);

		/**/ if("0".equalsIgnoreCase(mode)) {
			mailerBuilder.withTransportStrategy(TransportStrategy.SMTP);
		}
		else if("1".equalsIgnoreCase(mode)) {
			mailerBuilder.withTransportStrategy(TransportStrategy.SMTPS);
		}
		else if("2".equalsIgnoreCase(mode)) {
			mailerBuilder.withTransportStrategy(TransportStrategy.SMTP_TLS);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Mailer mailer = mailerBuilder.buildMailer();

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE EMAIL                                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		EmailPopulatingBuilder emailBuilder = EmailBuilder.startingBlank();

		emailBuilder.from(from);

		if(to != null && !(to = to.trim()).isEmpty()) {
			emailBuilder.to(to);
		}

		if(cc != null && !(cc = cc.trim()).isEmpty()) {
			emailBuilder.cc(cc);
		}

		emailBuilder.withSubject(subject).withPlainText(text);

		if(attachments != null)
		{
			for(Attachment attachment: attachments)
			{
				if(attachment != null)
				{
					emailBuilder.withAttachment(
						attachment.m_name,
						attachment.m_data,
						attachment.m_mime
					);
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Email email = emailBuilder.buildEmail();

		/*------------------------------------------------------------------------------------------------------------*/
		/* SEND EMAIL                                                                                                 */
		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			mailer.sendMail(email);
		}
		catch(RuntimeException e)
		{
			throw new Exception(e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
