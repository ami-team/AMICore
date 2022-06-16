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

		Row row = getQuerier(catalog).executeSQLQuery(entity, String.format(
			"SELECT (SELECT COUNT(%s) FROM %s), (SELECT COUNT(%s) FROM %s)",
			Utility.textToSqlId(field),
			Utility.textToSqlId(entity),
			Utility.textToSqlId(field),
			Utility.textToSqlId(entity)
		)).getFirst();

		/*------------------------------------------------------------------------------------------------------------*/

		int min = row.getValue(0, 0);
		int max = row.getValue(1, 0);

		int delta = max - min + 1;

		int multiple = delta / (
			sizeOfBins == 0 ? (delta > 10 ? 10 : 1) : sizeOfBins
		);

		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet = getQuerier(catalog).executeSQLQuery(entity, String.format(
			"WITH `bins` AS (SELECT FLOOR(%s / %d.0) * %d AS `bin_floor`, COUNT(`id`) AS `bin_count` FROM %s GROUP BY 1 ORDER BY 1) SELECT `bin_floor`, `bin_floor` + %d, `bin_count` FROM `bins` ORDER BY 1",
			Utility.textToSqlId(field),
			multiple, multiple,
			Utility.textToSqlId(entity),
			multiple
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
		return "-catalog=\"\" -entity=\"\" -field=\"\" (-sizeOfBins=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
