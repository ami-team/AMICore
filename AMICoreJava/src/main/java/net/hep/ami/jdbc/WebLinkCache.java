package net.hep.ami.jdbc;

import groovy.lang.*;

public class WebLinkCache
{
	/*---------------------------------------------------------------------*/

	private final GroovyShell m_groovyShell = new GroovyShell(WebLinkCache.class.getClassLoader());

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
			return "";
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
			return new StringBuilder().append("<properties><![CDATA[").append(e.getMessage()).append("]]></properties>").toString();
		}

		/*-----------------------------------------------------------------*/
	}
}
