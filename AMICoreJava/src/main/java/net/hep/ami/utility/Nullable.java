package net.hep.ami.utility;

import java.lang.annotation.*;

@Documented

@Retention(RetentionPolicy.CLASS)

@Target({
	ElementType.FIELD,
	ElementType.METHOD,
	ElementType.PARAMETER,
	ElementType.LOCAL_VARIABLE,
	ElementType.TYPE_USE,
})

public @interface Nullable
{
	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/
}
