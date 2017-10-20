package net.hep.ami.jdbc.reflexion;

import java.util.*;

public class ExtraSingleton
{
	/*---------------------------------------------------------------------*/

	public enum Mode
	{
		ADD,
		UPDATE
	}

	/*---------------------------------------------------------------------*/

	public static void patchFields(String catalog, String entity, List<String> fields, List<String> values, String AMIUser, Mode mode) throws Exception
	{
		if(fields.size() == values.size())
		{
			/*-------------------------------------------------------------*/

			Set<String> columnNames = SchemaSingleton.getColumnNames(catalog, entity);

			/*-------------------------------------------------------------*/

			if(columnNames.contains("modifiedby")) {
				patchField(fields, values, "modifiedby", AMIUser);
			}

			if(mode == Mode.ADD)
			{
				if(columnNames.contains("createdby")) {
					patchField(fields, values, "createdby", AMIUser);
				}
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void patchField(List<String> fields, List<String> values, String fieldName, String fieldValue)
	{
		/*-----------------------------------------------------------------*/

		for(int i = 0; i < fields.size(); i++)
		{
			if(fields.get(i).equalsIgnoreCase(fieldName))
			{
				values.set(i, fieldValue);

				return;
			}
		}

		/*-----------------------------------------------------------------*/

		fields.add(fieldName);
		values.add(fieldValue);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
