package net.hep.ami.utility;

import java.io.*;

public class TextFile
{
	/*---------------------------------------------------------------------*/

	public static String read(InputStream inputStream) throws Exception
	{
		StringBuilder result = new StringBuilder();

		/*-----------------------------------------------------------------*/

		String line;

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try
		{
			while((line = bufferedReader.readLine()) != null)
			{
				result.append(line);
				result.append('\n');
			}
		}
		finally
		{
			bufferedReader.close();
		}

		/*-----------------------------------------------------------------*/

		return result.toString();
	}

	/*---------------------------------------------------------------------*/

	public static void save(OutputStream outputStream, String content) throws Exception
	{
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

		try
		{
			bufferedWriter.write(content);
		}
		finally
		{
			bufferedWriter.close();
		}
	}

	/*---------------------------------------------------------------------*/
}
