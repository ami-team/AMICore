package net.hep.ami.jdbc.driver;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

public @interface Jdbc
{
	/*---------------------------------------------------------------------*/

	public enum Type
	{
		SQL, NoSQL
	}

	/*---------------------------------------------------------------------*/

	Type type();

	String proto();

	String clazz();

	/*---------------------------------------------------------------------*/
}
