package net.hep.ami;

import lombok.*;

import java.util.*;
import java.nio.charset.*;

public class CaptchaSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Setter
	@Getter
	@AllArgsConstructor
	@ToString
	public static class Captcha
	{
		private String image;

		private String hash;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final int CAPTCHA_WIDTH = 180;
	private static final int CAPTCHA_HEIGHT = 40;

	/*----------------------------------------------------------------------------------------------------------------*/

	private CaptchaSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static String generateCaptchaImage(String text) throws Exception
	{
		Random random = new Random();

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder svg = new StringBuilder(String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"%d\" height=\"%d\">", CAPTCHA_WIDTH, CAPTCHA_HEIGHT));

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < text.length(); i++)
		{
			svg.append(String.format("<text x=\"%d\" y=\"%d\" style=\"fill: #%02x%02x%02x; font-size: 24px;\">%c</text>",
				10 + i * 20,
				10 + (int) (CAPTCHA_HEIGHT * 0.5 * (1.0 + (Math.random() - 0.5))),
				random.nextInt(128),
				random.nextInt(128),
				random.nextInt(128),
				text.charAt(i)
			));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < 8; i++)
		{
			svg.append(String.format("<circle cx=\"%d\" cy=\"%d\" r=\"%d\" style=\"fill: transparent; stroke: #%02x%02x%02x; stroke-width: 0.8;\" />",
				(int) (CAPTCHA_WIDTH * Math.random()),
				(int) (CAPTCHA_HEIGHT * Math.random()),
				(int) (CAPTCHA_HEIGHT * 0.375000000000),
				random.nextInt(128),
				random.nextInt(128),
				random.nextInt(128)
			));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		svg.append("</svg>");

		/*------------------------------------------------------------------------------------------------------------*/

		return "data:image/svg+xml;base64," + org.bouncycastle.util.encoders.Base64.toBase64String(svg.toString().getBytes(StandardCharsets.UTF_8));

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static Captcha generateCaptcha() throws Exception
	{
		String text = new StringTokenizer(UUID.randomUUID().toString(), "-").nextToken();

		String hash = SecuritySingleton.md5Sum(SecuritySingleton.encrypt(text));

		return new Captcha(generateCaptchaImage(text), hash);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static boolean checkCaptcha(String hash, String text)
	{
		try
		{
			return hash.equals(SecuritySingleton.md5Sum(SecuritySingleton.encrypt(text)));
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
