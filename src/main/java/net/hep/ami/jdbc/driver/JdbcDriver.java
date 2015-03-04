package net.hep.ami.jdbc.driver;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

public @interface JdbcDriver {
	/*---------------------------------------------------------------------*/

	String className();

	String prefix();

	/*---------------------------------------------------------------------*/
}
