package net.hep.ami.command.dashboard;

import java.util.*;

import net.hep.ami.command.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class GetDashboardInfo extends AbstractCommand
{
	/*---------------------------------------------------------------------*/

	public GetDashboardInfo(Set<String> userRoles, Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public StringBuilder main(Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		result.append("<rowset>");

		/*-----------------------------------------------------------------*/

		result.append("<row>");

		result.append("<field name=\"id\"><![CDATA[").append("kkk").append("]]></field>");
		result.append("<field name=\"control\"><![CDATA[").append("DomText").append("]]></field>");
		result.append("<field name=\"params\"><![CDATA[").append("[\"Test1\", \"test1\", \"thermometer-half\", \"Hello\"]").append("]]></field>");
		result.append("<field name=\"x\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"y\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"width\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"height\"><![CDATA[").append("2").append("]]></field>");

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		result.append("<row>");

		result.append("<field name=\"id\"><![CDATA[").append("kkk").append("]]></field>");
		result.append("<field name=\"control\"><![CDATA[").append("DomText").append("]]></field>");
		result.append("<field name=\"params\"><![CDATA[").append("[\"Test1\", \"test1\", \"thermometer-half\", \"Hello\"]").append("]]></field>");
		result.append("<field name=\"x\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"y\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"width\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"height\"><![CDATA[").append("2").append("]]></field>");

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		result.append("<row>");

		result.append("<field name=\"id\"><![CDATA[").append("kkk").append("]]></field>");
		result.append("<field name=\"control\"><![CDATA[").append("DomText").append("]]></field>");
		result.append("<field name=\"params\"><![CDATA[").append("[\"Test1\", \"test1\", \"thermometer-half\", \"Hello\"]").append("]]></field>");
		result.append("<field name=\"x\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"y\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"width\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"height\"><![CDATA[").append("2").append("]]></field>");

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		result.append("<row>");

		result.append("<field name=\"id\"><![CDATA[").append("kkk").append("]]></field>");
		result.append("<field name=\"control\"><![CDATA[").append("DomSwitch").append("]]></field>");
		result.append("<field name=\"params\"><![CDATA[").append("[\"Test2\", true, \"lightbulb-o\", \"Hello\"]").append("]]></field>");
		result.append("<field name=\"x\"><![CDATA[").append("1").append("]]></field>");
		result.append("<field name=\"y\"><![CDATA[").append("1").append("]]></field>");
		result.append("<field name=\"width\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"height\"><![CDATA[").append("2").append("]]></field>");

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		result.append("<row>");

		result.append("<field name=\"id\"><![CDATA[").append("kkk").append("]]></field>");
		result.append("<field name=\"control\"><![CDATA[").append("DomRange").append("]]></field>");
		result.append("<field name=\"params\"><![CDATA[").append("[\"Test3\", \"0\", \"10\", \"1\", \"0\", \"lightbulb-o\", \"Hello\"]").append("]]></field>");
		result.append("<field name=\"x\"><![CDATA[").append("1").append("]]></field>");
		result.append("<field name=\"y\"><![CDATA[").append("1").append("]]></field>");
		result.append("<field name=\"width\"><![CDATA[").append("2").append("]]></field>");
		result.append("<field name=\"height\"><![CDATA[").append("2").append("]]></field>");

		result.append("</row>");

		/*-----------------------------------------------------------------*/

		result.append("</rowset>");

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static String help()
	{
		return "Get the dashboard content information.";
	}

	/*---------------------------------------------------------------------*/
}
