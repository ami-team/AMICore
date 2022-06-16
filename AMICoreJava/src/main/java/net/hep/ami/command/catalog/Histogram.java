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

		int sizeOfBins;

		try {
			sizeOfBins = Integer.parseInt(arguments.get("sizeOfBins"));
		}
		catch(NumberFormatException e) {
			sizeOfBins = 0x0000000000000000000000000000000000000000000;
		}

		if(catalog == null || entity == null || field == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		List<Row> rows = getQuerier(catalog).executeSQLQuery(entity, String.format(
			"SELECT ((SELECT COUNT(%s) FROM %s), (SELECT COUNT(%s) FROM %s)",
			Utility.textToSqlId(field),
			Utility.textToSqlId(entity),
			Utility.textToSqlId(field),
			Utility.textToSqlId(entity)
		)).getAll();

		Row row = rows.get(0);

		int min = row.getValue(0, 0);
		int max = row.getValue(1, 0);

		int multiple = sizeOfBins != 0 ? (max - min) / sizeOfBins
		                               : (max - min) / 0x0000000A
		;

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
