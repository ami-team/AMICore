package net.hep.ami.utility;

import java.io.*;

public class TextFile
{
	/*---------------------------------------------------------------------*/

	private TextFile() {}

	/*---------------------------------------------------------------------*/

	public static void read(StringBuilder stringBuilder, InputStream inputStream) throws Exception
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		String line;

		while((line = bufferedReader.readLine()) != null)
		{
			stringBuilder.append(line)
			             .append('\n')
			;
		}
	}

	/*---------------------------------------------------------------------*/

	public static void readLine(StringBuilder stringBuilder, InputStream inputStream) throws Exception
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		String s = bufferedReader.readLine();

		if(s != null)
		{
			stringBuilder.append(s);
		}
	}

	/*---------------------------------------------------------------------*/

	public static void write(OutputStream outputStream, StringBuilder stringBuilder) throws Exception
	{
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

		outputStreamWriter.write(stringBuilder.toString());

		outputStreamWriter.flush();
	}

	/*---------------------------------------------------------------------*/
}
