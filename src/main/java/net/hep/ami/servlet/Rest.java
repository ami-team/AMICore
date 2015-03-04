package net.hep.ami.servlet;

import java.io.IOException;

import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(
	name = "Rest",
	urlPatterns = "/Rest*"
)

public class Rest extends HttpServlet {
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = 7493264504334290468L;

	/*---------------------------------------------------------------------*/

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

	}

	/*---------------------------------------------------------------------*/

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

	}

	/*---------------------------------------------------------------------*/


	/*---------------------------------------------------------------------*/
}
