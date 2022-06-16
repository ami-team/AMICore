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

		boolean floating = arguments.containsKey("floating");

		if(catalog == null || entity == null || field == null)
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Row row = getQuerier(catalog).executeSQLQuery(entity, String.format(
			"SELECT (SELECT MIN(%s) FROM %s), (SELECT MAX(%s) FROM %s)",
			Utility.textToSqlId(field),
			Utility.textToSqlId(entity),
			Utility.textToSqlId(field),
			Utility.textToSqlId(entity)
		)).getFirst();

		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet;

		if(floating)
		{
			float min = row.getValue(0, 0.0f);
			float max = row.getValue(1, 0.0f);

			float delta = (max - min) + 1.0f;

			float multiple = delta / (sizeOfBins > 0 ? sizeOfBins : (delta > 10 ? 10 : 1));

			rowSet = getQuerier(catalog).executeSQLQuery(entity, String.format(
				"WITH `bins` AS (SELECT FLOOR(%s / %f) * %f AS `bin_floor`, COUNT(*) AS `bin_count` FROM %s GROUP BY FLOOR(%s / %f) * %f ORDER BY FLOOR(%s / %f) * %f) SELECT `bin_floor` AS `floor`, `bin_floor` + %f AS `ceiling`, `bin_count` AS `count` FROM `bins` ORDER BY `bin_floor`",
				Utility.textToSqlId(field),
				multiple, multiple,
				Utility.textToSqlId(entity),
				Utility.textToSqlId(field),
				multiple, multiple,
				Utility.textToSqlId(field),
				multiple, multiple,
				multiple
			));
		}
		else
		{
			int min = row.getValue(0, 0);
			int max = row.getValue(1, 0);

			int delta = max - min;

			if(sizeOfBins == 0)
			{
				sizeOfBins = delta > 10 ? 10 : 1;
			}

			rowSet = getQuerier(catalog).executeSQLQuery(entity, String.format(
					"WITH `bins` AS (SELECT FLOOR((%s - %d) / %d.0) AS `bin_index`, COUNT(*) AS `bin_count` FROM %s GROUP BY FLOOR((%s - %d) / %d.0) ORDER BY FLOOR((%s - %d) / %d.0) SELECT `bin_index` AS `index`, `bin_count` AS `count` FROM `bins` ORDER BY `bin_index`",
					Utility.textToSqlId(field),
					min, sizeOfBins,
					Utility.textToSqlId(entity),
					Utility.textToSqlId(field),
					min, sizeOfBins,
					Utility.textToSqlId(field),
					min, sizeOfBins
			));
		}

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
		return "-catalog=\"\" -entity=\"\" -field=\"\" (-sizeOfBins=\"\")? (-floating)?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
