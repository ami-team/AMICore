package net.hep.ami.utility;

import java.util.*;
import java.text.*;

import net.hep.ami.*;

import org.jetbrains.annotations.*;

public class DateTime
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private SimpleDateFormat m_datetimeFormater = null;

	private SimpleDateFormat m_dateFormater = null;

	private SimpleDateFormat m_timeFormater = null;

	/*----------------------------------------------------------------------------------------------------------------*/

	private SimpleDateFormat getDateTimeFormater()
	{
		if(m_datetimeFormater == null)
		{
			m_datetimeFormater = new SimpleDateFormat(ConfigSingleton.getProperty("datetime_format", "yyyy-MM-dd HH:mm:ss"), Locale.US);
		}

		return m_datetimeFormater;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private SimpleDateFormat getDateFormater()
	{
		if(m_dateFormater == null)
		{
			m_dateFormater = new SimpleDateFormat(ConfigSingleton.getProperty("date_format", "yyyy-MM-dd"), Locale.US);
		}

		return m_dateFormater;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private SimpleDateFormat getTimeFormater()
	{
		if(m_timeFormater == null)
		{
			m_timeFormater = new SimpleDateFormat(ConfigSingleton.getProperty("time_format", "HH:mm:ss"), Locale.US);
		}

		return m_timeFormater;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String formatTimestamp(@NotNull java.sql.Timestamp timestamp)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		final int precision = ConfigSingleton.getProperty("time_precision", 6);

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder result = new StringBuilder();

		if(precision >= 1
		   &&
		   precision <= 9
		 ) {
			String frac = String.valueOf((long) Math.round(timestamp.getNanos() / Math.pow(10, 9 - precision)));

			String pad = "0".repeat(Math.max(0, precision - frac.length()));

			result.append(".").append(pad).append(frac);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result.insert(0, getDateTimeFormater().format(timestamp)).toString();

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String formatDateTime(@NotNull java.sql.Timestamp dateTime)
	{
		return getDateTimeFormater().format(dateTime);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String formatDate(@NotNull java.sql.Date date)
	{
		return getDateFormater().format(date);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String formatTime(@NotNull java.sql.Time time)
	{
		return getDateFormater().format(time);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> new")
	public java.sql.Timestamp parseTimestamp(@NotNull String timestamp) throws ParseException
	{
		/*------------------------------------------------------------------------------------------------------------*/

		int idx = timestamp.lastIndexOf('.');

		/*------------------------------------------------------------------------------------------------------------*/

		long ns;

		if(idx > 0)
		{
			timestamp = timestamp.substring(0, idx + 0);

			int precision = ConfigSingleton.getProperty("time_precision", 6);

			if(precision >= 1
			   &&
			   precision <= 9
			 ) {
				ns = (long) (Double.parseDouble(timestamp.substring(idx + 1)) * Math.pow(10, 9 - precision));
			}
			else
			{
				ns = 0x000000000000000000000000000000000000000000000000000000000000000000000000000000000000;
			}
		}
		else
		{
			ns = 0x000000000000000000000000000000000000000000000000000000000000000000000000000000000000;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		long ms = getDateTimeFormater().parse(timestamp).getTime();

		while(ns > 1000000)
		{
			ms +=    1   ;
			ns -= 1000000;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		java.sql.Timestamp result = new java.sql.Timestamp(ms);

		result.setNanos((int) ns);

		return result;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> new")
	public java.sql.Timestamp parseDateTime(@NotNull String dateTime) throws ParseException
	{
		return new java.sql.Timestamp(getDateTimeFormater().parse(dateTime).getTime());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> new")
	public java.sql.Date parseDate(@NotNull String date) throws ParseException
	{
		return new java.sql.Date(getDateFormater().parse(date).getTime());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract("_ -> new")
	public java.sql.Time parseTime(@NotNull String time) throws ParseException
	{
		return new java.sql.Time(getTimeFormater().parse(time).getTime());
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
