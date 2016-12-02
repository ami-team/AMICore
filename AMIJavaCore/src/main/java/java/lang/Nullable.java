package java.lang;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)

@Target({ElementType.PARAMETER})

public @interface Nullable
{
}
