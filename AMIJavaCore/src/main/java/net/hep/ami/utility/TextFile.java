package net.hep.ami.utility;

import java.io.*;

public class TextFile
{
	/*---------------------------------------------------------------------*/

	public static void read(StringBuilder stringBuilder, InputStream inputStream) throws Exception
	{
		String line;

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try
		{
			while((line = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(line);
				stringBuilder.append('\n');
			}
		}
		finally
		{
			bufferedReader.close();
		}
	}

	/*---------------------------------------------------------------------*/

	public static void write(OutputStream outputStream, StringBuilder stringBuilder) throws Exception
	{
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

		try
		{
			bufferedWriter.write(stringBuilder.toString());
		}
		finally
		{
			bufferedWriter.close();
		}
	}

	/*---------------------------------------------------------------------*/
}
