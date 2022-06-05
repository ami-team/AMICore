package net.hep.ami.command.page;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = true, secured = false)
public class GetPageInfo extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GetPageInfo(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		List<Row> rowList;

		/*------------------------------------------------------------------------------------------------------------*/

		String id = arguments.get("id");

		if(id != null)
		{
			rowList = getQuerier("self").executeSQLQuery("router_markdown", "SELECT `id`, `name`, `title`, `body`, `archived`, `created`, `createdBy`, `modified`, `modifiedBy` FROM `router_markdown` WHERE `id` = ?0", id).getAll();

			if(rowList.size() != 1)
			{
				throw new Exception("undefined page id `" + id + "`");
			}
		}
		else
		{
			String name = arguments.get("name");

			if(name != null)
			{
				rowList = getQuerier("self").executeSQLQuery("router_markdown", "SELECT `id`, `name`, `title`, `body`, `archived`, `created`, `createdBy`, `modified`, `modifiedBy` FROM `router_markdown` WHERE `name` = ?0", name).getAll();

				if(rowList.size() != 1)
				{
					throw new Exception("undefined page name `" + name + "`");
				}
			}
			else
			{
				throw new Exception("invalid usage");
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Row row = rowList.get(0);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<rowset>")
		                          .append("<row>")
		                          .append("<field name=\"id\"><![CDATA[").append(row.getValue(0)).append("]]></field>")
		                          .append("<field name=\"name\"><![CDATA[").append(row.getValue(1)).append("]]></field>")
		                          .append("<field name=\"title\"><![CDATA[").append(row.getValue(2)).append("]]></field>")
		                          .append("<field name=\"body\"><![CDATA[").append(row.getValue(3)).append("]]></field>")
		                          .append("<field name=\"archived\"><![CDATA[").append(row.getValue(4)).append("]]></field>")
		                          .append("<field name=\"created\"><![CDATA[").append(row.getValue(5)).append("]]></field>")
		                          .append("<field name=\"createdBy\"><![CDATA[").append(row.getValue(6)).append("]]></field>")
		                          .append("<field name=\"modified\"><![CDATA[").append(row.getValue(7)).append("]]></field>")
		                          .append("<field name=\"modifiedBy\"><![CDATA[").append(row.getValue(8)).append("]]></field>")
		                          .append("</row>")
		                          .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Get the page information.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "(-id=\"\" | -name=\"\")";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
