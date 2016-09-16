package net.hep.ami.jdbc.driver.annotation;

import java.lang.annotation.*;

import net.hep.ami.jdbc.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

public @interface Jdbc
{
	/*---------------------------------------------------------------------*/

	DBType type();

	String proto();

	String clazz();

	/*---------------------------------------------------------------------*/
}
