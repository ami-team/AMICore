package net.hep.ami.jdbc.mql;

import org.antlr.v4.runtime.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.mql.antlr.*;

public class UpdateParser
{
	/*---------------------------------------------------------------------*/

/*	private DriverInterface m_driver;
 */
	/*---------------------------------------------------------------------*/

	public UpdateParser(DriverInterface driver)
	{
/*		m_driver = driver;
 */	}

	/*---------------------------------------------------------------------*/

	public static String parse(String query, DriverInterface driver) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		MQLSelectLexer lexer = new MQLSelectLexer(new ANTLRInputStream(query));

		MQLSelectParser parser = new MQLSelectParser(new CommonTokenStream(lexer));

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		parser.setErrorHandler(new DefaultErrorStrategy() {
		});

		/*-----------------------------------------------------------------*/
		/*                                                                 */
		/*-----------------------------------------------------------------*/

		new UpdateParser(driver);

		return "";

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		System.out.println(CatalogSingleton.listCatalogs());
/*
		try
		{
			System.out.println(parse("", null));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		System.out.println("done.");
*/
		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
