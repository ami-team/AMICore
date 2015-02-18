package net.hep.ami.servlet;

import java.io.*;

import javax.servlet.http.*;

public class Setup extends HttpServlet {
	/*---------------------------------------------------------------------*/

	private static final long serialVersionUID = -7394712866072360297L;

	/*---------------------------------------------------------------------*/

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doCommand(req, res);
	}

	/*---------------------------------------------------------------------*/

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		doCommand(req, res);
	}

	/*---------------------------------------------------------------------*/

	private void doCommand(HttpServletRequest req, HttpServletResponse res) throws IOException {
		/*-----------------------------------------------------------------*/
		/* SET DEFAULT ENCODING                                            */
		/*-----------------------------------------------------------------*/

		req.setCharacterEncoding("UTF-8");
		res.setCharacterEncoding("UTF-8");

		/*-----------------------------------------------------------------*/
		/* GET AND WRITE STATUS                                            */
		/*-----------------------------------------------------------------*/

		res.setContentType("text/html");

		PrintWriter writer = res.getWriter();

		writer.write(level1());
		writer.close();

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private String level1() {

		StringBuilder result = new StringBuilder();

		InputStream inputStream = Setup.class.getResourceAsStream("/html/setup_level1.html");

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try {

			String line;

			while((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}

		} catch(IOException e) {
			/* IGNORE */
		} finally {

				try {
					bufferedReader.close();

				} catch(IOException e) {
					/* IGNORE */
				}
		}

		return result.toString();
	}

	/*---------------------------------------------------------------------*/
}
