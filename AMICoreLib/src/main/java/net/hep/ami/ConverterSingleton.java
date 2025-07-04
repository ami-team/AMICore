package net.hep.ami;

import java.io.*;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import net.hep.ami.data.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public class ConverterSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(ConverterSingleton.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	private record ConverterDescr(
		@NotNull String name,
		@NotNull String mime,
		@NotNull Templates templates
	) {}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<String, ConverterDescr> s_converters = new AMIMap<>(AMIMap.Type.HASH_MAP, true, false);

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private ConverterSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reload()
	{
		s_converters.clear();

		try
		{
			addConverters();
		}
		catch(Exception e)
		{
			LOG.error(LogSingleton.FATAL, "could not add converters", e);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addConverters() throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* CREATE QUERIER                                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		RouterQuerier querier = new RouterQuerier();

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* EXECUTE QUERY                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			RowSet rowSet = querier.executeSQLQuery("router_converter", "SELECT `xslt`, `mime` FROM `router_converter`");

			/*--------------------------------------------------------------------------------------------------------*/
			/* ADD CONVERTERS                                                                                         */
			/*--------------------------------------------------------------------------------------------------------*/

			for(Row row: rowSet.iterate())
			{
				try
				{
					addConverter(
						row.getValue(0),
						row.getValue(1)
					);
				}
				catch(Exception e)
				{
					LOG.error(LogSingleton.FATAL, "for converter `{}`: ", row.getValue(0), e);
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		finally
		{
			querier.rollbackAndRelease();
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addConverter(@NotNull String xslt, @NotNull String mime) throws Exception
	{
		try(InputStream inputStream = ConverterSingleton.class.getResourceAsStream(xslt))
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* PARSE XSLT FILE                                                                                        */
			/*--------------------------------------------------------------------------------------------------------*/

			Templates templates = XMLFactory.newTemplates(inputStream);

			/*--------------------------------------------------------------------------------------------------------*/
			/* ADD CONVERTER                                                                                          */
			/*--------------------------------------------------------------------------------------------------------*/

			String name = new File(xslt).getName();

			s_converters.put(
				name
				,
				new ConverterDescr(
					name,
					mime,
					templates
				)
			);

			/*--------------------------------------------------------------------------------------------------------*/
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String convert(@NotNull String converter, @NotNull Reader reader, @NotNull Writer writer) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/
		/* GET TRANSFORM                                                                                              */
		/*------------------------------------------------------------------------------------------------------------*/

		ConverterDescr converterInfo = s_converters.get(converter);

		if(converterInfo == null)
		{
			throw new Exception("converter `" + converter + "` not found");
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* APPLY TRANSFORM                                                                                            */
		/*------------------------------------------------------------------------------------------------------------*/

		Transformer transformer = converterInfo.templates().newTransformer();

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		transformer.transform(
			new StreamSource(reader),
			new StreamResult(writer)
		);

		/*------------------------------------------------------------------------------------------------------------*/
		/* RETURN MIME                                                                                                */
		/*------------------------------------------------------------------------------------------------------------*/

		return converterInfo.mime();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static StringBuilder listConverters()
	{
		StringBuilder result = new StringBuilder();

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<rowset type=\"converters\">");

		for(ConverterDescr converterInfo: s_converters.values())
		{
			result.append("<row>")
			      .append("<field name=\"xslt\"><![CDATA[").append(converterInfo.name()).append("]]></field>")
			      .append("<field name=\"mime\"><![CDATA[").append(converterInfo.mime()).append("]]></field>")
			      .append("</row>")
			;
		}

		result.append("</rowset>");

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
