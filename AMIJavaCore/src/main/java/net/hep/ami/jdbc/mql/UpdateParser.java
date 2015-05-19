package net.hep.ami.jdbc.mql;

import net.hep.ami.jdbc.*;

public class UpdateParser {
	/*---------------------------------------------------------------------*/

	/*---------------------------------------------------------------------*/

	public static String parse(String query, DriverInterface driver) throws Exception {

		return query;
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args) {

		System.out.println(CatalogSingleton.listCatalogs());

		try {
			System.out.println(parse("", null));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println("done.");

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
