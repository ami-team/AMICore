package net.hep.ami.jdbc.driver;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

public @interface DriverMetadata
{
	/*---------------------------------------------------------------------*/

	/**
	 * Database type. For NoSQL databases, MQL is disabled.
	 *
	 * @return The database type.
	 */

	public enum Type
	{
		SQL,	/**< SQL database */
		NoSQL	/**< NoSQL database */
	}

	/*---------------------------------------------------------------------*/

	Type type();

	String proto();

	String clazz();

	boolean backslashEscapes();

	/*---------------------------------------------------------------------*/
}
