package net.hep.ami.cloud.driver.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

public @interface Engine {
	/*---------------------------------------------------------------------*/

	String name();

	/*---------------------------------------------------------------------*/
}
