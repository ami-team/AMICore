package net.hep.ami.utility;

import java.io.*;

public class SimpleShell extends ShellAbstractClass
{
	/*---------------------------------------------------------------------*/

	@Override
	public ShellTuple exec(String command) throws Exception
	{
		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		Process process = Runtime.getRuntime().exec(command);

		new Thread(new StreamReader(inputStringBuilder, process.getInputStream())).start();
		new Thread(new StreamReader(errorStringBuilder, process.getErrorStream())).start();

		return new ShellTuple(process.waitFor(), inputStringBuilder, errorStringBuilder);
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void sendFile(String fpath, String fname, StringBuilder stringBuilder) throws Exception
	{
		TextFile.save(new FileOutputStream(new File(fpath + File.separator + fname)), stringBuilder);
	}

	/*---------------------------------------------------------------------*/
}
