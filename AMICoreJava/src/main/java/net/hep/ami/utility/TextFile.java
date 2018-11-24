package net.hep.ami.utility;

import java.io.*;

public class TextFile
{
	/*---------------------------------------------------------------------*/

	private TextFile() {}

	/*---------------------------------------------------------------------*/

	public static void read(StringBuilder stringBuilder, File file) throws Exception
	{
		read(stringBuilder, new FileInputStream(file));
	}

	/*---------------------------------------------------------------------*/

	public static void readLine(StringBuilder stringBuilder, File file) throws Exception
	{
		readLine(stringBuilder, new FileInputStream(file));
	}

	/*---------------------------------------------------------------------*/

	public static void write(File file, CharSequence charSequence) throws Exception
	{
		write(new FileOutputStream(file), charSequence);
	}

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

		String line;

		if((line = bufferedReader.readLine()) != null)
		{
			stringBuilder.append(line)
		//	             .append('\n')

			;
		}
	}

	/*---------------------------------------------------------------------*/

	public static void write(OutputStream outputStream, CharSequence charSequence) throws Exception
	{
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

		outputStreamWriter.write(charSequence.toString());

		outputStreamWriter.flush();
	}

	/*---------------------------------------------------------------------*/
}
