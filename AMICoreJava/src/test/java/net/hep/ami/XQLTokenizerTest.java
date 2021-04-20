package net.hep.ami;

import net.hep.ami.jdbc.query.sql.*;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

public class XQLTokenizerTest
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@org.junit.jupiter.api.Test @Disabled
	public void test_splitXQL() throws Exception
	{
		assertEquals(Tokenizer.splitXQL("SELECT foo, bar FROM qux GROUP BY foo HAVING SUM(foo) > 10 ORDER BY bar ASC").toString(), "{\"SELECT\":[\" \",\"foo\",\",\",\" \",\"bar\",\" \"],\"FROM\":[\" \",\"qux\",\" \"],\"GROUP\":[\" \",\" \",\"foo\",\" \"],\"HAVING\":[\" \",\"SUM\",\"(\",\"foo\",\")\",\" \",\">\",\" \",\"10\",\" \"],\"ORDER\":[\" \",\" \",\"bar\",\" \"],\"WAY\":[\"ASC\"]}");

		try
		{
			System.out.println(Tokenizer.splitXQL("foo, bar FROM qux GROUP BY foo HAVING SUM(foo) > 10 ORDER BY bar ASC"));

			fail("should returns invalid SQL syntax");
		}
		catch(Exception e) { /* IGNORE */ }
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
