package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class ClassFinder
{
	/*---------------------------------------------------------------------*/

	private static final Set<String> s_classNames = new HashSet<String>();

	/*---------------------------------------------------------------------*/

	static
	{
		/*-----------------------------------------------------------------*/
		/* GET PATHS                                                       */
		/*-----------------------------------------------------------------*/

		String java_class_path = System.getProperty("java.class.path");

		Set<String> classPaths = java_class_path != null ? new HashSet<String>(Arrays.asList(java_class_path.split(":")))
			                                             : new HashSet<String>(/*-------------------------------------*/)
		;

		/*-----------------------------------------------------------------*/

		String name = "/net/hep/ami/utility/ClassFinder.class";

		String path = ClassFinder.class.getResource(name).getPath();

		path = path.substring(0, path.indexOf(name));

		/**/

		if(path.startsWith("file:"))
		{
			path = new File(path.substring(5)).getParent();
		}

		/**/

		classPaths.add(path);

		/*-----------------------------------------------------------------*/
		/* WALK                                                            */
		/*-----------------------------------------------------------------*/

		File file;

		for(String classPath: classPaths)
		{
			file = new File(classPath);

			walk(file, file);
		}

		/*-----------------------------------------------------------------*/
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

	private static void addJar(File file)
	{
		try
		{
			/*-------------------------------------------------------------*/
			/* OPEN ZIP FILE                                               */
			/*-------------------------------------------------------------*/

			ZipFile zipFile = new ZipFile(file);

			/*-------------------------------------------------------------*/
			/* READ ZIP FILE                                               */
			/*-------------------------------------------------------------*/

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			/*-------------------------------------------------------------*/
			/* ADD CLASSES                                                 */
			/*-------------------------------------------------------------*/

			while(entries.hasMoreElements())
			{
				String className = entries.nextElement().getName();

				addClassName(className);
			}

			/*-------------------------------------------------------------*/
			/* CLOSE ZIP FILE                                              */
			/*-------------------------------------------------------------*/

			zipFile.close();

			/*-------------------------------------------------------------*/
		}
		catch(Exception e)
		{
			/* IGNORE */
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addFile(File base, File file)
	{
		String className = base.toURI().relativize(file.toURI()).getPath();

		addClassName(className);
	}

	/*---------------------------------------------------------------------*/

	private static void addClassName(String className)
	{
		if(className.endsWith(".class") && className.contains("$") == false)
		{
			className = className.substring(0, className.length() - 6)
			                     .replace('\\', '.')
			                     .replace('/', '.')
			;

			s_classNames.add(className);
		}
	}

	/*---------------------------------------------------------------------*/

	public static Set<String> findClassNames(String filter)
	{
		Set<String> result = new HashSet<String>();

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
