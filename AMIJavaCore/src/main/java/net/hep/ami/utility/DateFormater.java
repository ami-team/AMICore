package net.hep.ami.utility;

import java.text.*;
import java.util.*;

public class DateFormater
{
	/*---------------------------------------------------------------------*/

	private static final SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	/*---------------------------------------------------------------------*/

	public static String format(Date date)
	{
		return m_simpleDateFormat.format(date);
	}

	/*---------------------------------------------------------------------*/
}
