package net.hep.ami.mini;

public class Server extends org.eclipse.jetty.server.Server
{
	/*---------------------------------------------------------------------*/

	public Server(int port, Handler handler) throws Exception
	{
		super(port);

		setStopAtShutdown(true);

		setHandler(new JettyHandler(this, handler));
	}

	/*---------------------------------------------------------------------*/

	public void gracefulStop()
	{
		/*-----------------------------------------------------------------*/
		/* CREATE THREAD                                                   */
		/*-----------------------------------------------------------------*/

		Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(500);

					Server.this.stop();
				}
				catch(Exception e)
				{
					/* IGNORE */
				}
			}
		};

		/*-----------------------------------------------------------------*/
		/* START THREAD                                                    */
		/*-----------------------------------------------------------------*/

		thread.start();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
