package net.hep.ami.servlet;

import java.io.*;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

import net.hep.ami.*;

@WebServlet(
	name = "Test",
	urlPatterns = "/Test"
)

public class Test extends HttpServlet
{
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 5266180095821111498L;

	/*---------------------------------------------------------------------*/

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		doCommand(req, res);
	}

	/*---------------------------------------------------------------------*/

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		doCommand(req, res);
	}

	/*---------------------------------------------------------------------*/

	private void doCommand(HttpServletRequest req, HttpServletResponse res)
	{
		/*-----------------------------------------------------------------*/
		/* SET UTF-8 AS DEFAULT ENCODING                                   */
		/*-----------------------------------------------------------------*/

		try
		{
			req.setCharacterEncoding("UTF-8");
			res.setCharacterEncoding("UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			/* IGNORE */
		}

		/*-----------------------------------------------------------------*/
		/* CROSS-ORIGIN RESOURCE SHARING                                   */
		/*-----------------------------------------------------------------*/

		String origin = req.getHeader("Origin");

		if(origin != null)
		{
			res.setHeader("Access-Control-Allow-Credentials", "true");
			res.setHeader("Access-Control-Allow-Origin", origin);
		}

		/*-----------------------------------------------------------------*/
		/* WRITE RESULT                                                    */
		/*-----------------------------------------------------------------*/

		try(PrintWriter writer = res.getWriter())
		{
			res.setStatus(HttpServletResponse.SC_OK);

			writer.print(req.toString());
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);

			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
