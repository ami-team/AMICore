package net.hep.ami.command.admin;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

public class SendEmail extends CommandAbstractClass
{
	/*---------------------------------------------------------------------*/

	public SendEmail(Map<String, String> arguments, long transactionId)
	{
		super(arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		String from = arguments.containsKey("from") ? arguments.get("from")
		                                            : ""
		;

		String to = arguments.containsKey("to") ? arguments.get("to")
		                                        : ""
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

		if(m_isSecure.equals("false"))
		{
			throw new Exception("HTTPS connection required"); 
		}

		/*-----------------------------------------------------------------*/

		MailSingleton.sendMessage(from, to, cc, subject, message);

		/*-----------------------------------------------------------------*/

		return new StringBuilder("<info><![CDATA[done with success]]></info>");
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Send email.";
	}

	/*---------------------------------------------------------------------*/

	public static String usage()
	{
		return "(-from=\"value\")? (-to=\"value\")? (-cc=\"value\")? -subject=\"value\" -message=\"value\"";
	}

	/*---------------------------------------------------------------------*/
}
