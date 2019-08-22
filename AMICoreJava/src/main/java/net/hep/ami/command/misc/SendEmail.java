package net.hep.ami.command.misc;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;

@CommandMetadata(role = "AMI_USER", visible = false, secured = false)
public class SendEmail extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public SendEmail(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String from = arguments.getOrDefault("from", ConfigSingleton.getProperty("admin_email"));
		String to = arguments.getOrDefault("to", ConfigSingleton.getProperty("admin_email"));
		String cc = arguments.getOrDefault("cc", "");

		String subject = arguments.getOrDefault("subject", "");
		String message = arguments.getOrDefault("message", "");

		/*------------------------------------------------------------------------------------------------------------*/

		MailSingleton.sendMessage(from, to, cc, subject, message);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String help()
	{
		return "Send an email.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@org.jetbrains.annotations.Contract(pure = true)
	public static String usage()
	{
		return "(-from=\"\")? -to=\"\" (-cc=\"\")? -subject=\"\" -message=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
