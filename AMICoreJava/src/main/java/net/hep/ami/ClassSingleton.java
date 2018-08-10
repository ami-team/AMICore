package net.hep.ami;

import java.io.*;
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

	private static ClassLoader s_classLoader = ClassSingleton.class.getClassLoader();

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

		String path = ClassSingleton.class.getResource("/net/hep/ami/ClassSingleton.class").getPath();

		/*-----------------------------------------------------------------*/

		int index0 = path.indexOf("file:"); // Check if this is a JAR file.
		int index1 = path.indexOf("!/");

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

		Set<URL> jars = new HashSet<>();

		for(String PATH: ConfigSingleton.getSystemProperty("java.class.path").split(":")) {
			walk(PATH, null);
		}

		for(String PATH: ConfigSingleton.getProperty("class_path").split(":")) {
			walk(PATH, jars);
		}

		walk(path, null);

		/*-----------------------------------------------------------------*/

		s_classLoader = new URLClassLoader(
			jars.stream().toArray(URL[]::new),
			ClassSingleton.class.getClassLoader()
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	private static void walk(@Nullable String classPath, Set<URL> jars)
	{
		File file = new File(classPath);

		walk(file, file, jars);
	}

	/*---------------------------------------------------------------------*/

	private static void walk(File base, File file, Set<URL> jars)
	{
		if(file.isDirectory())
		{
			/*-------------------------------------------------------------*/
			/* DIRECTORY                                                   */
			/*-------------------------------------------------------------*/

			for(File FILE: file.listFiles())
			{
				walk(base, FILE, jars);
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
				addJarFile(file, jars);
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

	private static void addJarFile(File file, Set<URL> jars)
	{
		try(ZipFile zipFile = new ZipFile(file))
		{
			zipFile.stream().forEach(x -> addClass(x.getName()));

			if(jars != null) jars.add(file.toURI().toURL());
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
		return Class.forName(name, true, s_classLoader);
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
