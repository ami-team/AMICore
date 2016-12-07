package net.hep.ami.utility.shell;

import java.io.*;

import net.hep.ami.utility.*;

public class SimpleShell extends ShellAbstractClass
{
	/*---------------------------------------------------------------------*/

	@Override
	public ShellTuple exec(String[] args) throws Exception
	{
		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		Process process = Runtime.getRuntime().exec(args);

		Thread inputThread = new StreamReader(inputStringBuilder, process.getInputStream());
		Thread errorThread = new StreamReader(errorStringBuilder, process.getErrorStream());

		inputThread.start();
		errorThread.start();

		inputThread.join();
		errorThread.join();

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
		TextFile.read(stringBuilder, new FileInputStream(new File(fpath + File.separator + fname)));
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void writeTextFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception
	{
		TextFile.write(new FileOutputStream(new File(fpath + File.separator + fname)), stringBuilder);
	}

	/*---------------------------------------------------------------------*/
}
