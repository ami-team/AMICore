package net.hep.ami;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import net.hep.ami.utility.*;

public class ClassSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Set<String> s_classNames = AMIMap.newSet(AMIMap.Type.CONCURENT_HASH_MAP, false, false);

	/*---------------------------------------------------------------------*/

	private ClassSingleton() {}

	/*---------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		/*-----------------------------------------------------------------*/

		s_classNames.clear();

		/*-----------------------------------------------------------------*/

		for(String path: System.getProperty("java.class.path", "").split(":"))
		{
			walk(path);
		}

		/*-----------------------------------------------------------------*/

		URL url = ClassSingleton.class.getResource("/net/hep/ami/ClassSingleton.class");

		int index = url.getFile().indexOf("!/");

		String path;

		try
		{
			if(index > 0)
			{
				/*---------------------------------------------------------*/
				/* JAR FILE                                                */
				/*---------------------------------------------------------*/

				path = new File(url.getFile().substring(0, index)).getParent();

				/*---------------------------------------------------------*/
			}
			else
			{
				/*---------------------------------------------------------*/
				/* CLASS FILE                                              */
				/*---------------------------------------------------------*/

				path = new File(url.getFile()).getParentFile()
				                              .getParentFile()
				                              .getParentFile()
				                              .getParent()
				;

				/*---------------------------------------------------------*/
			}
		}
		catch(Exception e)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "could not add classes", e);

			return;
		}

		walk(path);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void walk(@Nullable String classPath)
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

			try(ZipFile zipFile = new ZipFile(file))
			{
				for(ZipEntry entries: Misc.toIterable(zipFile.entries()))
				{
					addClass(entries.getName());
				}
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
