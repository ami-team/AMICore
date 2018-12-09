package net.hep.ami.jdbc;

import java.util.*;

import groovy.lang.*;

import net.hep.ami.utility.*;

public class WebLinkScripts
{
	/*---------------------------------------------------------------------*/

	private final GroovyShell m_groovyShell = new GroovyShell(Row.class.getClassLoader());

	/*---------------------------------------------------------------------*/

	private final Map<String, Script> m_groovyScripts = new AMIMap<>(AMIMap.Type.CONCURENT_HASH_MAP, false, false);

	/*---------------------------------------------------------------------*/

	public String processWebLink(String code, String catalog, String entity, String field, String value)
	{
		/*-----------------------------------------------------------------*/
		/* COMPILE GROOVY SCRIPT                                           */
		/*-----------------------------------------------------------------*/

		Script script = m_groovyScripts.get(code);

		if(script == null)
		{
			script = m_groovyShell.parse(code);

			m_groovyScripts.put(code, script);
		}

		/*-----------------------------------------------------------------*/
		/* RUN GROOVY SCRIPT                                               */
		/*-----------------------------------------------------------------*/

		Binding binding = new Binding();

		binding.setVariable("catalog", catalog);
		binding.setVariable("entity", entity);
		binding.setVariable("field", field);
		binding.setVariable("value", value);

		script.setBinding(binding);

		/*-----------------------------------------------------------------*/

		try
		{
			return ((WebLink) script.run()).toString();
		}
		catch(ClassCastException e)
		{
			return "";
		}

		/*-----------------------------------------------------------------*/
	}
}
