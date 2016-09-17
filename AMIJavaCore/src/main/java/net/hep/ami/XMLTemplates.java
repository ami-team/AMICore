package net.hep.ami;

public class XMLTemplates
{
	/*---------------------------------------------------------------------*/

	public static String info(String message)
	{
		if(message == null)
		{
			message = "null";
		}

		return new StringBuilder()

			.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><info><![CDATA[")
			.append(message.replace("<![CDATA[", "").replace("]]>", ""))
			.append("]]></info><executionTime>0.0</executionTime></AMIMessage>")

			.toString()
		;
	}

	/*---------------------------------------------------------------------*/

	public static String error(String message)
	{
		if(message == null)
		{
			message = "null";
		}

		return new StringBuilder()

			.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><error><![CDATA[")
			.append(message.replace("<![CDATA[", "").replace("]]>", ""))
			.append("]]></error><executionTime>0.0</executionTime></AMIMessage>")

			.toString()
		;
	}

	/*---------------------------------------------------------------------*/
}
