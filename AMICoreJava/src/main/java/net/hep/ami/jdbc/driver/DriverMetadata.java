package net.hep.ami.jdbc.driver;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

public @interface DriverMetadata
{
	/*---------------------------------------------------------------------*/

	/**
	 * Database type. For NoSQL databases, MQL is disabled.
	 */

	enum Type
	{
		/**< SQL database */
		SQL,
		/**< NoSQL database */
		NoSQL
	}

	/*---------------------------------------------------------------------*/

	int FLAG_BACKSLASH_ESCAPE = (1 << 0);
	int FLAG_QID_IN_MODIF = (1 << 1);
	int FLAG_HAS_DUAL = (1 << 2);

	/*---------------------------------------------------------------------*/

	Type type();

	String proto();

	String clazz();

	int flags();

	/*---------------------------------------------------------------------*/
}
