package net.hep.ami.servlet;

import java.io.*;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(
	name = "Rest",
	urlPatterns = "/Rest/*"
)

public class Rest extends HttpServlet {
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 7493264504334290468L;

	/*---------------------------------------------------------------------*/

	@Override
	public void init() {

	}

	/*---------------------------------------------------------------------*/

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

		/*-----------------------------------------------------------------*/
		/* WRITE FORM                                                      */
		/*-----------------------------------------------------------------*/

		res.setContentType("text/html");

		PrintWriter writer = res.getWriter();

		writer.write("Hello");

		writer.close();
	}

	/*---------------------------------------------------------------------*/

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

	}

	/*---------------------------------------------------------------------*/


	/*---------------------------------------------------------------------*/
}
