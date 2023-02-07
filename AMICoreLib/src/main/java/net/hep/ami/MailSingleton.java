package net.hep.ami;

import lombok.*;

import net.hep.ami.utility.*;

import org.simplejavamail.email.*;
import org.simplejavamail.mailer.*;
import org.simplejavamail.api.email.*;
import org.simplejavamail.api.mailer.*;
import org.simplejavamail.api.mailer.config.*;

import org.jetbrains.annotations.*;

public class MailSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Getter
	@Setter
	@AllArgsConstructor
	public static final class Attachment
	{
		@NotNull private final String name;
		/*----*/ private final byte[] data;
		@NotNull private final String mime;
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
		String   host   = ConfigSingleton.getProperty(  "email_host"  );
		String   port   = ConfigSingleton.getProperty(  "email_port"  );
		String   mode   = ConfigSingleton.getProperty(  "email_mode"  );
		String username = ConfigSingleton.getProperty("email_username");
		String password = ConfigSingleton.getProperty("email_password");

		if(Empty.is(  host  , Empty.STRING_AMI_NULL | Empty.STRING_BLANK)
		   ||
		   Empty.is(  port  , Empty.STRING_AMI_NULL | Empty.STRING_BLANK)
		   ||
		   Empty.is(  mode  , Empty.STRING_AMI_NULL | Empty.STRING_BLANK)
		   ||
		   Empty.is(username, Empty.STRING_AMI_NULL | Empty.STRING_BLANK)
		   ||
		   Empty.is(password, Empty.STRING_AMI_NULL | Empty.STRING_BLANK)
		 ) {
			throw new Exception("mailer not configured");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE MAILER                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		TransportStrategy transportStrategy;

		switch(mode.trim().toUpperCase())
		{
			case "0":
			case "SMTP":
				transportStrategy = TransportStrategy.SMTP;
				break;

			case "1":
			case "SMTPS":
				transportStrategy = TransportStrategy.SMTPS;
				break;

			case "2":
			case "SMTP_TLS":
				transportStrategy = TransportStrategy.SMTP_TLS;
				break;

			default:
				throw new Exception("invalid SMTP mode");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Mailer mailer = MailerBuilder.withSMTPServer(host, Integer.parseInt(port), username, password)
		                             .withTransportStrategy(transportStrategy)
		                             .buildMailer()
		;

		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE EMAIL                                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		EmailPopulatingBuilder emailBuilder = EmailBuilder.startingBlank();

		emailBuilder.from(from);

		if(!Empty.is(to, Empty.STRING_NULL_EMPTY_BLANK)) {
			emailBuilder.to(to.trim());
		}

		if(!Empty.is(cc, Empty.STRING_NULL_EMPTY_BLANK)) {
			emailBuilder.cc(cc.trim());
		}

		emailBuilder.withSubject(subject).withPlainText(text);

		if(attachments != null)
		{
			for(Attachment attachment: attachments)
			{
				if(attachment != null)
				{
					emailBuilder.withAttachment(
						attachment.getName(),
						attachment.getData(),
						attachment.getMime()
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
			System.out.println("cause: " + e.getCause().getMessage());
			e.printStackTrace();
			throw new Exception("error sending email");
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
