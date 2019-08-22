package net.hep.ami;

import net.hep.ami.utility.parser.*;

import static org.junit.jupiter.api.Assertions.*;

public class UtilityTest
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@org.junit.jupiter.api.Test
	public void test_object2json()
	{
		/* TODO */
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@org.junit.jupiter.api.Test
	public void test_escapeJSONString()
	{
		assertNull(Utility.escapeJSONString(null, true));

		assertEquals(Utility.escapeJSONString("\n", true), "\\n");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@org.junit.jupiter.api.Test
	public void test_unescapeJSONString()
	{
		assertNull(Utility.unescapeJSONString(null, true));

		assertEquals(Utility.unescapeJSONString("\\n", true), "\n");

		assertEquals(Utility.unescapeJSONString("\\u2619", true), "☙");

		try
		{
			Utility.unescapeJSONString("\\u261", false);

			fail("should returns invalid escape sequence");
		}
		catch(RuntimeException e) { /* IGNORE */ }
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@org.junit.jupiter.api.Test
	public void test_jsonStringToText()
	{
		assertEquals(Utility.jsonStringToText("\"foo\"", false), "foo");

		assertEquals(Utility.jsonStringToText("\"foo\\\"\"", false), "foo\"");

		assertEquals(Utility.jsonStringToText("\"foo\\\'\"", true), "foo\'");

		try
		{
			Utility.jsonStringToText("\"foo\\\'\"", false);

			fail("should returns invalid escape sequence");
		}
		catch(RuntimeException e) { /* IGNORE */ }
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@org.junit.jupiter.api.Test
	public void test_textToJSONString()
	{
		assertEquals(Utility.textToJSONString("foo", false), "\"foo\"");

		assertEquals(Utility.textToJSONString("foo\"", false), "\"foo\\\"\"");

		assertEquals(Utility.textToJSONString("foo\'", true), "\"foo\\\'\"");

		assertEquals(Utility.textToJSONString("foo\'", false), "\"foo\'\"");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@org.junit.jupiter.api.Test
	public void test_sqlValToText()
	{
		assertEquals(Utility.sqlValToText(null, false), "@NULL");

		assertEquals(Utility.sqlValToText("NULL", false), "@NULL");

		assertEquals(Utility.sqlValToText("CURRENT_TIMESTAMP", false), "@CURRENT_TIMESTAMP");

		assertEquals(Utility.sqlValToText("'foo'", false), "foo");

		assertEquals(Utility.sqlValToText("'foo'''", false), "foo'");

		assertEquals(Utility.sqlValToText("'foo\\''", true), "foo'");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@org.junit.jupiter.api.Test
	public void test_textToSqlVal()
	{
		assertEquals(Utility.textToSqlVal(null, false), "NULL");

		assertEquals(Utility.textToSqlVal("@NULL", false), "NULL");

		assertEquals(Utility.textToSqlVal("@CURRENT_TIMESTAMP", false), "CURRENT_TIMESTAMP");

		assertEquals(Utility.textToSqlVal("foo", false), "'foo'");

		assertEquals(Utility.textToSqlVal("foo'", false), "'foo'''");

		assertEquals(Utility.textToSqlVal("foo'", true), "'foo\\''");
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@org.junit.jupiter.api.Test
	public void test_escapeHTML()
	{
		assertNull(Utility.escapeHTML(null));

		assertEquals(Utility.escapeHTML("<☕>test</☕>"), "&lt;&#9749;&gt;test&lt;/&#9749;&gt;");
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
