package net.hep.ami.jdbc;

import groovy.lang.*;
import net.hep.ami.LogSingleton;

public class WebLinkCache
{
	/*---------------------------------------------------------------------*/

	private final GroovyShell m_groovyShell = new GroovyShell(WebLinkCache.class.getClassLoader());

	/*---------------------------------------------------------------------*/

	private String error(String message)
	{
		return new StringBuilder().append("<properties><![CDATA[").append(message).append("]]></properties>").toString();
	}

	/*---------------------------------------------------------------------*/

	public String processWebLink(String code, String catalog, String entity, String field, Row row)
	{
		/*-----------------------------------------------------------------*/
		/* COMPILE GROOVY SCRIPT                                           */
		/*-----------------------------------------------------------------*/

		Script script;

		try
		{
			script = m_groovyShell.parse(code);
		}
		catch(Exception e)
		{
			return error(e.getMessage());
		}

		/*-----------------------------------------------------------------*/
		/* RUN GROOVY SCRIPT                                               */
		/*-----------------------------------------------------------------*/

		Binding binding = new Binding();

		binding.setVariable("catalog", catalog);
		binding.setVariable("entity", entity);
		binding.setVariable("field", field);
		binding.setVariable("row", row);

		script.setBinding(binding);

		/*-----------------------------------------------------------------*/

		try
		{
			return ((WebLink) script.run()).toString();
		}
		catch(Exception e)
		{
			LogSingleton.root.error(e.getMessage(), e);

			return error(e.getMessage());
		}

		/*-----------------------------------------------------------------*/
	}
}
