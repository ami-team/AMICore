package net.hep.ami.utility;

import java.math.*;

import org.jetbrains.annotations.*;

public class Stats
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static BigDecimal median(final BigDecimal[] bigDecimalNumbers)
	{
		return median(bigDecimalNumbers, 0, 1);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static BigDecimal median(final BigDecimal[] bigDecimalNumbers, final int start, final int end)
	{
		BigDecimal result;

		final int size = end - start;

		if(size % 2 == 1)
		{
			result = bigDecimalNumbers[start + (size + 1) / 2 - 1];
		}
		else
		{
			result = /*------*/(bigDecimalNumbers[start + (size) / 2 - 1]);
			result = result.add(bigDecimalNumbers[start + (size) / 2 - 0]);
			result = result.divide(BigDecimal.valueOf(2), 3, RoundingMode.UNNECESSARY);
		}

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static BigDecimal quartile1(@NotNull BigDecimal @NotNull [] bigDecimalNumbers)
	{
		final int size = bigDecimalNumbers.length;

		if(size % 2 == 1)
		{
			if (size <= 1)
			{
				return median(bigDecimalNumbers, 0, 1);
			}
			else
			{
				return median(bigDecimalNumbers, 0, size / 2 + 1);
			}
		}
		else
		{
			return median(bigDecimalNumbers, 0, size / 2 + 0);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static BigDecimal quartile3(@NotNull BigDecimal @NotNull [] bigDecimalNumbers)
	{
		final int size = bigDecimalNumbers.length;

		if(size % 2 == 1)
		{
			if(size <= 1)
			{
				return median(bigDecimalNumbers, 0, 1);
			}
			else
			{
				return median(bigDecimalNumbers, size / 2, size + 0);
			}
		}
		else
		{
			return median(bigDecimalNumbers, size / 2, size + 0);
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
