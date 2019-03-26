package net.hep.ami.jdbc;

import groovy.lang.*;

import net.hep.ami.*;

public class WebLinkCache
{
	/*---------------------------------------------------------------------*/

	private final GroovyShell m_groovyShell = new GroovyShell(WebLinkCache.class.getClassLoader());

	/*---------------------------------------------------------------------*/

	private String error(Exception e)
	{
		LogSingleton.root.error(e.getMessage(), e);

		return new StringBuilder().append("<properties><![CDATA[").append(e.getMessage()).append("]]></properties>").toString();
	}

	/*---------------------------------------------------------------------*/

	public String processWebLink(String code, String catalog, String entity, String field, Row row)
	{
		if(code != null
		   &&
		   code.isEmpty() == false
		   &&
		   "@NULL".equalsIgnoreCase(code) == false
		 ) {
			return "";
		}

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
			return error(e);
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

		String result;

		try
		{
			result = ((WebLink) script.run()).toString();
		}
		catch(Exception e)
		{
			return error(e);
		}

		/*-----------------------------------------------------------------*/

		return result;
	}
}
