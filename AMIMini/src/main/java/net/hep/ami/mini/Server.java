package net.hep.ami.mini;

public class Server extends org.eclipse.jetty.server.Server
{
	/*---------------------------------------------------------------------*/

	public Server(int port, Handler handler) throws Exception
	{
		super(port);

		setStopTimeout(5000);

		setStopAtShutdown(true);

		setHandler(new JettyHandler(this, handler));
	}

	/*---------------------------------------------------------------------*/

	@Override
	public void join() throws InterruptedException
	{
		while(isRunning())
		{
			Thread.sleep(100);
		}

		while(isStopping())
		{
			Thread.sleep(100);
		}
	}

	/*---------------------------------------------------------------------*/
}
