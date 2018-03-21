package net.hep.ami.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)

@Target(ElementType.TYPE)

public @interface CommandMetadata
{
	/*---------------------------------------------------------------------*/

	String role();

	boolean secured();

	/*---------------------------------------------------------------------*/
}
