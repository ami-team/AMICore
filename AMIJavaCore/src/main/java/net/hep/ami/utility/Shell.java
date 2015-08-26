package net.hep.ami.utility;

public class Shell extends ShellAbstractClass
{
	/*---------------------------------------------------------------------*/

	@Override
	public ShellTuple exec(String command) throws Exception
	{
		Process p = Runtime.getRuntime().exec(command);

		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		new Thread(new StreamReader(inputStringBuilder, p.getInputStream())).start();
		new Thread(new StreamReader(errorStringBuilder, p.getErrorStream())).start();

		return new ShellTuple(p.waitFor(), inputStringBuilder, errorStringBuilder);
	}

	/*---------------------------------------------------------------------*/
}
