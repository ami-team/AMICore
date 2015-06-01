package net.hep.ami;

public class XMLTemplates
{
	/*---------------------------------------------------------------------*/

	public static String info(String message)
	{
		StringBuilder result = new StringBuilder();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><info><![CDATA[");
		result.append(message);
		result.append("]]></info><executionTime>0.0</executionTime></AMIMessage>");

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String error(String message)
	{
		StringBuilder result = new StringBuilder();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><error><![CDATA[");
		result.append(message);
		result.append("]]></error><executionTime>0.0</executionTime></AMIMessage>");

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String help(String help, String usage)
	{
		StringBuilder result = new StringBuilder();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage>");

		result.append("<help><![CDATA[");
		result.append(help);
		result.append("]]></help>");

		result.append("<usage><![CDATA[");
		result.append(usage);
		result.append("]]></usage>");

		result.append("<executionTime>0.0</executionTime></AMIMessage>");

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
