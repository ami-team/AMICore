package net.hep.ami;

import lombok.*;

import java.util.*;
import java.nio.charset.*;

import org.jetbrains.annotations.*;

public class CaptchaSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	@Setter
	@Getter
	@AllArgsConstructor
	public static class Captcha
	{
		@NotNull private String image;

		@NotNull private String hash;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final int DEFAULT_CAPTCHA_WIDTH = 180;
	private static final int DEFAULT_CAPTCHA_HEIGHT = 40;

	/*----------------------------------------------------------------------------------------------------------------*/

	private CaptchaSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static String generateCaptchaImage(int width, int height, @NotNull String text) throws Exception
	{
		Random random = new Random();

		int fontSize = (int) (0.90 * width / text.length());
		int fontSpace = (int) (0.05 * width / text.length());

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder svg = new StringBuilder(String.format(Locale.US, "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"%d\" height=\"%d\">", width, height));

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < text.length(); i++)
		{
			svg.append(String.format(Locale.US, "<text x=\"%d\" y=\"%d\" style=\"fill: #%02x%02x%02x; font-size: %dpx;\">%c</text>",
				fontSpace + i * (fontSize + fontSpace),
				(int) ((height + fontSize) * 0.5 + (height - fontSize) * (Math.random() - 0.5)),
				random.nextInt(128),
				random.nextInt(128),
				random.nextInt(128),
				fontSize,
				text.charAt(i)
			));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < 8; i++)
		{
			svg.append(String.format(Locale.US, "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" style=\"fill: transparent; stroke: #%02x%02x%02x; stroke-width: %.2f;\" />",
				(int) (width * Math.random()),
				(int) (height * Math.random()),
				(int) (height * 0.375000000000),
				random.nextInt(128),
				random.nextInt(128),
				random.nextInt(128),
				Math.min(width, height) * 0.025
			));
		}

		/*------------------------------------------------------------------------------------------------------------*/

		svg.append("</svg>");

		/*------------------------------------------------------------------------------------------------------------*/

		return "data:image/svg+xml;base64," + org.bouncycastle.util.encoders.Base64.toBase64String(svg.toString().getBytes(StandardCharsets.UTF_8));

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Captcha generateCaptcha(int width, int height) throws Exception
	{
		String text = new StringTokenizer(UUID.randomUUID().toString(), "-").nextToken();

		String hash = SecuritySingleton.md5Sum(SecuritySingleton.encrypt(text));

		return new Captcha(generateCaptchaImage(width, height, text), hash);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Captcha generateCaptcha() throws Exception
	{
		return generateCaptcha(DEFAULT_CAPTCHA_WIDTH, DEFAULT_CAPTCHA_HEIGHT);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static boolean checkCaptcha(@NotNull String hash, @NotNull String text)
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
