package net.hep.ami.jdbc.driver;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

public @interface Jdbc {
	/*---------------------------------------------------------------------*/

	String className();

	String prefix();

	/*---------------------------------------------------------------------*/
}
