package net.hep.ami.command.user;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.command.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_GUEST", visible = false)
public class GenerateCaptcha extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public GenerateCaptcha(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		/*------------------------------------------------------------------------------------------------------------*/

		CaptchaSingleton.Captcha captcha = CaptchaSingleton.generateCaptcha();

		/*------------------------------------------------------------------------------------------------------------*/

		return new StringBuilder().append("<rowset type=\"captcha\">").append("<row>")
		                          .append("<field name=\"image\"><![CDATA[").append(captcha.getImage()).append("]]></field>")
		                          .append("<field name=\"hash\"><![CDATA[").append(captcha.getHash()).append("]]></field>")
		                          .append("</row>").append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Generate a captcha image.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
