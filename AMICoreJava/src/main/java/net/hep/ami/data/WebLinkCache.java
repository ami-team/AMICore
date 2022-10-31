package net.hep.ami.data;

import groovy.lang.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public class WebLinkCache
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final org.slf4j.Logger LOG = LogSingleton.getLogger(WebLinkCache.class.getSimpleName());

	/*----------------------------------------------------------------------------------------------------------------*/

	private final GroovyShell m_groovyShell = new GroovyShell(WebLinkCache.class.getClassLoader());

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	private String error(@NotNull Exception e)
	{
		LOG.error(e.getMessage(), e);

		return new StringBuilder().append("<properties><![CDATA[").append(e.getMessage()).append("]]></properties>").toString();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public String processWebLink(@Nullable String code, @NotNull String catalog, @NotNull String entity, @NotNull String field, @NotNull RowSet rowSet, @NotNull Row row)
	{
		if(Empty.is(code, Empty.STRING_AMI_NULL | Empty.STRING_BLANK))
		{
			return "";
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* COMPILE GROOVY SCRIPT                                                                                      */
		/*------------------------------------------------------------------------------------------------------------*/

		Script script;

		try
		{
			script = m_groovyShell.parse(code);
		}
		catch(Exception e)
		{
			return error(e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* RUN GROOVY SCRIPT                                                                                          */
		/*------------------------------------------------------------------------------------------------------------*/

		Binding binding = new Binding();

		binding.setVariable("catalog", catalog);
		binding.setVariable("entity", entity);
		binding.setVariable("field", field);
		binding.setVariable("rowSet", rowSet);
		binding.setVariable("row", row);

		script.setBinding(binding);

		/*------------------------------------------------------------------------------------------------------------*/

		try
		{
			return script.run().toString();
		}
		catch(Exception e)
		{
			return error(e);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
