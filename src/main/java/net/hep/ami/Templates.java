package net.hep.ami;

public class Templates {
	/*---------------------------------------------------------------------*/

	public static String std(String content) {

		StringBuilder result = new StringBuilder();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage>");
		result.append(content);
		result.append("</AMIMessage>");

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String help(String message) {

		StringBuilder result = new StringBuilder();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><help><![CDATA[");
		result.append(message);
		result.append("]]></help><executionTime>0.0</executionTime></AMIMessage>");

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String info(String message) {

		StringBuilder result = new StringBuilder();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><info><![CDATA[");
		result.append(message);
		result.append("]]></info><executionTime>0.0</executionTime></AMIMessage>");

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static String error(String message) {

		StringBuilder result = new StringBuilder();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AMIMessage><error><![CDATA[");
		result.append(message);
		result.append("]]></error><executionTime>0.0</executionTime></AMIMessage>");

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
