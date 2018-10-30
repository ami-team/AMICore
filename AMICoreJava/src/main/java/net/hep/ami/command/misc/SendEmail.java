package net.hep.ami.command.misc;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = false, secured = false)
public class SendEmail extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public SendEmail(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String from = arguments.containsKey("from") ? arguments.get("from")
		                                            : ConfigSingleton.getProperty("admin_email")
		;

		String to = arguments.containsKey("to") ? arguments.get("to")
		                                        : ConfigSingleton.getProperty("admin_email")
		;

		String cc = arguments.containsKey("cc") ? arguments.get("cc")
		                                        : ""
		;

		String subject = arguments.containsKey("subject") ? arguments.get("subject")
		                                                  : ""
		;

		String message = arguments.containsKey("message") ? arguments.get("message")
		                                                  : ""
		;

		/*-----------------------------------------------------------------*/

		MailSingleton.sendMessage(from, to, cc, subject, message);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Send an email.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-from=\"\")? -to=\"\" (-cc=\"\")? -subject=\"\" -message=\"\"";
	}

	/*---------------------------------------------------------------------*/
}
