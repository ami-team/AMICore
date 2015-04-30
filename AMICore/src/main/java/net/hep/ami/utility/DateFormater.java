package net.hep.ami.utility;

import java.text.*;
import java.util.*;

public class DateFormater {
	/*---------------------------------------------------------------------*/

	private static final SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.US);

	private static final SimpleDateFormat m_simpleDateFormatSLS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

	/*---------------------------------------------------------------------*/

	public static String format(Date date) {

		return m_simpleDateFormat.format(date);
	}

	/*---------------------------------------------------------------------*/

	public static String formatSLS(Date date) {

		return m_simpleDateFormatSLS.format(date);
	}

	/*---------------------------------------------------------------------*/
}
