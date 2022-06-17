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
		String where = arguments.get("where");

		float sizeOfBins;

		try {
			sizeOfBins = Float.parseFloat(arguments.get("sizeOfBins"));
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
			"SELECT MIN(%s), MAX(%s) FROM %s WHERE %s",
			Utility.textToSqlId(field),
			Utility.textToSqlId(field),
			Utility.textToSqlId(entity),
			where != null ? where : "1 = 1"
		)).getFirst();

		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet;

		if(floating)
		{
			float min = row.getValue(0, 0.0f);
			float max = row.getValue(1, 0.0f);

			float delta = (max - min) + 1;

			if(sizeOfBins <= 0.0f)
			{
				sizeOfBins = delta > 100.0f ? 10.0000000000000000000000000f
				                            : (delta > 10.0f ? 1.0f : 0.1f)
				;
			}

			rowSet = getQuerier(catalog).executeSQLQuery(entity, String.format(Locale.US,
				"SELECT `bin_index` AS `index`, (`bin_index` + 0) * %f AS `floor`, (`bin_index` + 1) * %f AS `ceiling`, `bin_count` AS `count` FROM (SELECT FLOOR((%s - %f) / %f) AS `bin_index`, COUNT(*) AS `bin_count` FROM %s WHERE %s GROUP BY FLOOR((%s - %f) / %f) ORDER BY FLOOR((%s - %f) / %f)) `bins` ORDER BY `bin_index`",
				/*-*/ sizeOfBins,
				/*-*/ sizeOfBins,
				Utility.textToSqlId(field),
				min, /*-*/ sizeOfBins,
				Utility.textToSqlId(entity),
				where != null ? where : "1 = 1",
				Utility.textToSqlId(field),
				min, /*-*/ sizeOfBins,
				Utility.textToSqlId(field),
				min, /*-*/ sizeOfBins
			));
		}
		else
		{
			int min = row.getValue(0, 0);
			int max = row.getValue(1, 0);

			int delta = (max - min) + 1;

			if(sizeOfBins <= 0.0f)
			{
				sizeOfBins = delta > 100.0f ? 10.0f
				                            : 1.00f
				;
			}

			rowSet = getQuerier(catalog).executeSQLQuery(entity, String.format(Locale.US,
				"SELECT `bin_index` AS `index`, (`bin_index` + 0) * %d AS `floor`, (`bin_index` + 1) * %d AS `ceiling`, `bin_count` AS `count` FROM (SELECT FLOOR((%s - %d) / %d.0) AS `bin_index`, COUNT(*) AS `bin_count` FROM %s WHERE %s GROUP BY FLOOR((%s - %d) / %d.0) ORDER BY FLOOR((%s - %d) / %d.0)) `bins` ORDER BY `bin_index`",
				(int) sizeOfBins,
				(int) sizeOfBins,
				Utility.textToSqlId(field),
				min, (int) sizeOfBins,
				Utility.textToSqlId(entity),
				where != null ? where : "1 = 1",
				Utility.textToSqlId(field),
				min, (int) sizeOfBins,
				Utility.textToSqlId(field),
				min, (int) sizeOfBins
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
