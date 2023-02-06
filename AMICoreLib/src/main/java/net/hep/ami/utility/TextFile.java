package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.nio.charset.*;

import org.jetbrains.annotations.*;

public class TextFile
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private TextFile() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void read(@NotNull StringBuilder stringBuilder, @NotNull File file) throws Exception
	{
		read(stringBuilder, new FileInputStream(file));
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void write(@NotNull File file, @NotNull CharSequence charSequence) throws Exception
	{
		write(new FileOutputStream(file), charSequence);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Iterable<String> inputStreamToIterable(@NotNull File file, boolean trim) throws Exception
	{
		return inputStreamToIterable(new FileInputStream(file), trim);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void read(@NotNull StringBuilder stringBuilder, @NotNull InputStream inputStream) throws Exception
	{
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		for(String line; (line = bufferedReader.readLine()) != null; )
		{
			stringBuilder.append(line)
			             .append('\n')
			;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void write(@NotNull OutputStream outputStream, @NotNull CharSequence charSequence) throws Exception
	{
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

		outputStreamWriter.write(charSequence.toString());

		outputStreamWriter.flush();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Iterable<String> inputStreamToIterable(@NotNull InputStream inputStream, boolean trim)
	{
		/*------------------------------------------------------------------------------------------------------------*/

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

		/*------------------------------------------------------------------------------------------------------------*/

		if(!trim)
		{
			return () -> new Iterator<>()
			{
				/*----------------------------------------------------------------------------------------------------*/

				private String m_line;

				/*----------------------------------------------------------------------------------------------------*/

				@Override
				public boolean hasNext()
				{
					try
					{
						return (m_line = bufferedReader.readLine()) != null;
					}
					catch(Exception e)
					{
						m_line = null;

						throw new RuntimeException(e);
					}
				}

				/*----------------------------------------------------------------------------------------------------*/

				@NotNull
				@Override
				public String next()
				{
					if(m_line != null)
					{
						return m_line;
					}

					throw new NoSuchElementException();
				}

				/*----------------------------------------------------------------------------------------------------*/
			};
		}
		else
		{
			return () -> new Iterator<>()
			{
				/*----------------------------------------------------------------------------------------------------*/

				private String m_line;

				/*----------------------------------------------------------------------------------------------------*/

				@Override
				public boolean hasNext()
				{
					try
					{
						return (m_line = bufferedReader.readLine()) != null;
					}
					catch(Exception e)
					{
						m_line = null;

						throw new RuntimeException(e);
					}
				}

				/*----------------------------------------------------------------------------------------------------*/

				@NotNull
				@Override
				public String next()
				{
					if(m_line != null)
					{
						return m_line.trim();
					}

					throw new NoSuchElementException();
				}

				/*----------------------------------------------------------------------------------------------------*/
			};
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
