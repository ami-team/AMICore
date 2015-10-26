package net.hep.ami;

public class XMLTemplates
{
	/*---------------------------------------------------------------------*/

	public static String info(String message)
	{
		return new StringBuilder()

			.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><info><![CDATA[")
			.append(message)
			.append("]]></info><executionTime>0.0</executionTime></AMIMessage>")

			.toString()
		;
	}

	/*---------------------------------------------------------------------*/

	public static String error(String message)
	{
		return new StringBuilder()

			.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><error><![CDATA[")
			.append(message)
			.append("]]></error><executionTime>0.0</executionTime></AMIMessage>")

			.toString()
		;
	}

	/*---------------------------------------------------------------------*/

	public static String help(String help, String usage)
	{
		return new StringBuilder()

			.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage>")

			.append("<help><![CDATA[")
			.append(help)
			.append("]]></help>")

			.append("<usage><![CDATA[")
			.append(usage)
			.append("]]></usage>")

			.append("<executionTime>0.0</executionTime></AMIMessage>")

			.toString()
		;
	}

	/*---------------------------------------------------------------------*/
}
