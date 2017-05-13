package net.hep.ami;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

public class ClassSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Set<String> s_classNames = new HashSet<>();

	/*---------------------------------------------------------------------*/

	private ClassSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		/*-----------------------------------------------------------------*/

		for(String path: System.getProperty("java.class.path", "").split(":"))
		{
			walk(path);
		}

		/*-----------------------------------------------------------------*/

		URL url = ClassSingleton.class.getResource("/net/hep/ami/ClassSingleton.class");

		String path;

		try
		{
			try
			{
				JarURLConnection connection = (JarURLConnection) url.openConnection();

				path = new File(connection.getJarFileURL().toURI()).getParent();
			}
			catch(ClassCastException | IOException e)
			{
				path = new File(url.toURI()).getParentFile()
				                            .getParentFile()
				                            .getParentFile()
				                            .getParent()
				;
			}

			walk(path);
		}
		catch(URISyntaxException e)
		{
			/* INGORE */
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void walk(String classPath)
	{
		File file = new File(classPath);

		walk(file, file);
	}

	/*---------------------------------------------------------------------*/

	private static void walk(File base, File file)
	{
		if(file.isDirectory())
		{
			/*-------------------------------------------------------------*/
			/* DIRECTORY                                                   */
			/*-------------------------------------------------------------*/

			for(File FILE: file.listFiles())
			{
				walk(base, FILE);
			}

			/*-------------------------------------------------------------*/
		}
		else
		{
			/*-------------------------------------------------------------*/
			/* FILE                                                        */
			/*-------------------------------------------------------------*/

			if(file.getName().toLowerCase().endsWith(".jar") == false)
			{
				addFile(base, file);
			}
			else
			{
				addJar(file);
			}

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addFile(File base, File file)
	{
		String classFile = base.toURI().relativize(file.toURI()).getPath();

		addClass(classFile);
	}

	/*---------------------------------------------------------------------*/

	private static void addJar(File file)
	{
		try
		{
			/*-------------------------------------------------------------*/
			/* OPEN ZIP FILE                                               */
			/*-------------------------------------------------------------*/

			ZipFile zipFile = new ZipFile(file);

			/*-------------------------------------------------------------*/

			try
			{
				/*---------------------------------------------------------*/
				/* READ ZIP FILE                                           */
				/*---------------------------------------------------------*/

				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				/*---------------------------------------------------------*/
				/* ADD CLASSES                                             */
				/*---------------------------------------------------------*/

				while(entries.hasMoreElements())
				{
					String classFile = entries.nextElement().getName();

					addClass(classFile);
				}

				/*---------------------------------------------------------*/
			}
			finally
			{
				/*---------------------------------------------------------*/
				/* CLOSE ZIP FILE                                          */
				/*---------------------------------------------------------*/

				zipFile.close();

				/*---------------------------------------------------------*/
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			/* IGNORE */
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addClass(String classFile)
	{
		if(classFile.endsWith(".class") && classFile.contains("$") == false)
		{
			String className = classFile.substring(0, classFile.length() - 6)
			                            .replace('\\', '.')
			                            .replace('/', '.')
			;

			s_classNames.add(className);
		}
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> findClassNames(String filter)
	{
		Set<String> result = new TreeSet<>();

		for(String className: s_classNames)
		{
			if(className.startsWith(filter))
			{
				result.add(className);
			}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static boolean extendsClass(Class<?> child, Class<?> parent)
	{
		while((child = child.getSuperclass()) != null)
		{
			if(child == parent)
			{
				return true;
			}
		}

		return false;
	}

	/*---------------------------------------------------------------------*/

	public static boolean implementsInterface(Class<?> child, Class<?> parent)
	{
		for(Class<?> clazz: child.getInterfaces())
		{
			if(clazz == parent)
			{
				return true;
			}
		}

		return false;
	}

	/*---------------------------------------------------------------------*/
}
