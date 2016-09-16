package net.hep.ami.utility;

import java.io.*;

public class TextFile
{
	/*---------------------------------------------------------------------*/

	public static void read(StringBuilder stringBuilder, InputStream inputStream) throws Exception
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try
		{
			String line;

			while((line = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(line)
				             .append('\n')
				;
			}
		}
		finally
		{
			bufferedReader.close();
		}
	}

	/*---------------------------------------------------------------------*/

	public static void readLine(StringBuilder stringBuilder, InputStream inputStream) throws Exception
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try
		{
			stringBuilder.append(bufferedReader.readLine());
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
