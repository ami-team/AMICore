package net.hep.ami.jdbc.driver.annotation;

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
