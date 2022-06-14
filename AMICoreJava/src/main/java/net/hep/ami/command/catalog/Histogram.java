package net.hep.ami.command.catalog;

import java.util.*;

import net.hep.ami.data.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class Histogram extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public Histogram(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@Override
	public @NotNull StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");
		String field = arguments.get("field");

		int multiple;

		try {
			multiple = Integer.parseInt(arguments.get("multiple"));
		}
		catch(NumberFormatException e) {
			multiple = 0x00000000000000000000000000000000000000001;
		}

		Double sizeOfBins = arguments.containsKey("sizeOfBins") ? Double.parseDouble(arguments.get("sizeOfBins")) : null;

		if(catalog == null || entity == null || field == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet = getQuerier(catalog).executeSQLQuery(entity, String.format(
			"SELECT a.`_floor` AS `floor`, a.`_ceiling AS `ceiling`, COUNT(*) AS `count` FROM (SELECT FLOOR(%s / %d.0) * %d + %d AS `_floor`, FLOOR(%s / %d.0) * %d + %d AS `_ceiling` FROM %s) a GROUP BY 1 ORDER BY 1",
			Utility.textToSqlId(field),
			multiple, multiple, 0x000000,
			Utility.textToSqlId(field),
			multiple, multiple, multiple,
			Utility.textToSqlId(entity)
		));

		/*------------------------------------------------------------------------------------------------------------*/

		return rowSet.toStringBuilder("hist");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Generate a 1-dimension histogram.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" -field=\"\" (-multiple=\"\")? (-numberOfBins=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
