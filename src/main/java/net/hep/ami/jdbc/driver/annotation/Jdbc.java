package net.hep.ami.jdbc.driver.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

public @interface Jdbc {
	/*---------------------------------------------------------------------*/

	String proto();

	String clazz();

	/*---------------------------------------------------------------------*/
}
