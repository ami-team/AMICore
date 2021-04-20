package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.jdbc.query.*;
import net.hep.ami.jdbc.query.sql.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = false, secured = false)
public class AnalyzeQuery extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public AnalyzeQuery(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String xql = arguments.get("xql");

		if(xql == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Tokenizer.XQLParts partInfo = Tokenizer.splitXQL(xql);

		/*------------------------------------------------------------------------------------------------------------*/

		Tuple5<Map<QId, QId>, List<Boolean>, Map<QId, QId>, List<Boolean>, Map<QId, QId>> aliasInfo = Tokenizer.extractAliasInfo(xql);

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<rowset type=\"info\">")
		                          .append("<row>")
		                          .append("<field name=\"parts\"><![CDATA[").append(partInfo.toString()).append("]]></field>")
		                          .append("<field name=\"aliasField\"><![CDATA[").append(Utility.object2json(aliasInfo.x)).append("]]></field>")
		                          .append("<field name=\"fieldHasAlias\"><![CDATA[").append(Utility.object2json(aliasInfo.y)).append("]]></field>")
		                          .append("<field name=\"rawFieldAlias\"><![CDATA[").append(Utility.object2json(aliasInfo.z)).append("]]></field>")
		                          .append("<field name=\"tableHasAlias\"><![CDATA[").append(Utility.object2json(aliasInfo.t)).append("]]></field>")
		                          .append("<field name=\"rawTableAlias\"><![CDATA[").append(Utility.object2json(aliasInfo.u)).append("]]></field>")
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
		return "Extract information from a SQL/MQL query.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-xql=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
