package net.hep.ami.utility;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)

@Target({
	ElementType.FIELD,
	ElementType.METHOD,
	ElementType.PARAMETER,
})

public @interface Nullable
{
}
