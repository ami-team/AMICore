package net.hep.ami;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import java.util.stream.*;

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

	public static void addJarToClasspath(File jar)
	{
		try
		{
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();

			Method method = classLoader.getClass().getSuperclass()
			                           .getDeclaredMethod("addURL", new Class[] {URL.class})
			;

			method.setAccessible(true);

			method.invoke(classLoader, new Object[] {jar.toURI().toURL()});
		}
		catch(Exception e)
		{
			/* IGNORE */
		}
	}

	/*---------------------------------------------------------------------*/

	public static void reload()
	{
		/*-----------------------------------------------------------------*/

		s_classNames.clear();

		/*-----------------------------------------------------------------*/

		String path = ClassSingleton.class.getResource("/net/hep/ami/ClassSingleton.class").getPath();

		/*-----------------------------------------------------------------*/

		int index0 = path.indexOf("file:"); // Check if this is a JAR file.
		int index1 = path.indexOf("!/"); // Check if this is a JAR file.

		if(index0 >= 0
		   &&
		   index1 >= 0
		 ) {
			path = new File(path.substring(index0 + 5, index1 + 0)).getParent();
		}
		else
		{
			path = new File(path).getParentFile()
			                     .getParentFile()
			                     .getParentFile()
			                     .getParent()
			;
		}

		/*-----------------------------------------------------------------*/

		for(String PATH: ConfigSingleton.getSystemProperty("java.class.path").split(":")) {
			walk(PATH, false);
		}

		for(String PATH: ConfigSingleton.getProperty("class_path").split(":")) {
			walk(PATH, false);
		}

		walk(path, false);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void walk(@Nullable String classPath, boolean keep)
	{
		File file = new File(classPath);

		walk(file, file, keep);
	}

	/*---------------------------------------------------------------------*/

	private static void walk(File base, File file, boolean keep)
	{
		if(file.isDirectory())
		{
			/*-------------------------------------------------------------*/
			/* DIRECTORY                                                   */
			/*-------------------------------------------------------------*/

			for(File FILE: file.listFiles())
			{
				walk(base, FILE, keep);
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
				addClassFile(base, file);
			}
			else
			{
				addJarFile(file, keep);
			}

			/*-------------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addClassFile(File base, File file)
	{
		addClass(base.toURI().relativize(file.toURI()).getPath());
	}

	/*---------------------------------------------------------------------*/

	private static void addJarFile(File file, boolean keep)
	{
		try(ZipFile zipFile = new ZipFile(file))
		{
			zipFile.stream().forEach(x -> addClass(x.getName()));

			if(keep) addJarToClasspath(file);
		}
		catch(Exception e)
		{
			/* IGNORE */
		}
	}

	/*---------------------------------------------------------------------*/

	private static void addClass(String classFile)
	{
		if(classFile.endsWith(".class") && classFile.indexOf('$') == -1)
		{
			String className = classFile.substring(0, classFile.length() - 6)
			                            .replace('\\', '.')
			                            .replace('/', '.')
			;

			s_classNames.add(className);
		}
	}

	/*---------------------------------------------------------------------*/
	/*---------------------------------------------------------------------*/

	public static Set<String> findClassNames(String filter)
	{
		return s_classNames.stream().filter(x -> x.startsWith(filter)).collect(Collectors.toSet());
	}

	/*---------------------------------------------------------------------*/

	public static Class<?> forName(String name) throws ClassNotFoundException
	{
		return Class.forName(name);
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
