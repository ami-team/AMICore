package net.hep.ami;

import lombok.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.image.*;

import javax.imageio.*;

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

	private static final int CAPTCH_HEIGHT = 40;
	private static final int CAPTCHA_WIDTH = 180;

	/*----------------------------------------------------------------------------------------------------------------*/

	private CaptchaSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static String generateCaptchaImage(String text) throws Exception
	{
		Random random = new Random();

		byte[] bytes = text.getBytes();

		/*------------------------------------------------------------------------------------------------------------*/

		BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCH_HEIGHT, BufferedImage.TYPE_INT_RGB);

		/*------------------------------------------------------------------------------------------------------------*/

		Graphics2D g = image.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setColor(Color.white);
		g.setFont(new Font("Serif", Font.PLAIN, 26));
		g.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCH_HEIGHT);

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < bytes.length; i++)
		{
			g.setColor(new Color(random.nextInt(128), random.nextInt(128), random.nextInt(128)));

			g.drawString(new String(new byte[] {bytes[i]}), 10 + i * 20, (int) (1 + Math.random()) * 20);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		for(int i = 0; i < 8; i++)
		{
			g.setColor(new Color(random.nextInt(128), random.nextInt(128), random.nextInt(128)));

			g.drawOval((int) (Math.random() * 160), (int) (Math.random() * 10), 30, 30);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		g.dispose();

		/*------------------------------------------------------------------------------------------------------------*/

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(image, "png", outputStream);

		/*------------------------------------------------------------------------------------------------------------*/

		return "data:image/png;base64," + org.bouncycastle.util.encoders.Base64.toBase64String(outputStream.toByteArray());

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
