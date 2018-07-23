package net.hep.ami.utility.shell;

import java.io.*;

import net.hep.ami.utility.*;

public class SimpleShell extends AbstractShell
{
	/*---------------------------------------------------------------------*/

	@Override
	public ShellTuple exec(String[] args) throws Exception
	{
		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		Process process = Runtime.getRuntime().exec(new String[] {"bash", "-c", argsToString(args)});

		try(StreamReader inputThread = new StreamReader(inputStringBuilder, process.getInputStream());
		    StreamReader errorThread = new StreamReader(errorStringBuilder, process.getErrorStream()))
		{
			inputThread.start();
			errorThread.start();

			inputThread.join();
			errorThread.join();
		}

		return new ShellTuple(process.waitFor(), inputStringBuilder, errorStringBuilder);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void connect() throws Exception
	{
		/* N/A */
	}

	@Override
	public void disconnect() throws Exception
	{
		/* N/A */
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void readTextFile(StringBuilder stringBuilder, String fpath, String fname) throws Exception
	{
		try(InputStream inputStream = new FileInputStream(new File(fpath + File.separator + fname)))
		{
			TextFile.read(stringBuilder, inputStream);
		}
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void writeTextFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception
	{
		try(OutputStream outputStream = new FileOutputStream(new File(fpath + File.separator + fname)))
		{
			TextFile.write(outputStream, stringBuilder);
		}
	}

	/*---------------------------------------------------------------------*/
}
