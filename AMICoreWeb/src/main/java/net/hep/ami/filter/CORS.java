package net.hep.ami.filter;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class CORS implements Filter
{
	/*---------------------------------------------------------------------*/

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse res = (HttpServletResponse) servletResponse;

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

		String origin = req.getHeader("Origin");

		if(origin != null)
		{
			res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE");
			res.setHeader("Access-Control-Allow-Credentials", "true");
			res.setHeader("Access-Control-Allow-Origin", origin);
		}

		/*-----------------------------------------------------------------*/

		filterChain.doFilter(req, servletResponse);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
