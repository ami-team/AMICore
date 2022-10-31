package net.hep.ami;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import java.util.stream.*;

import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public class ClassSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Set<String> s_classNames = AMIMap.newSet(AMIMap.Type.CONCURRENT_HASH_MAP, false, false);

	/*----------------------------------------------------------------------------------------------------------------*/

	private static ClassLoader s_classLoader = ClassSingleton.class.getClassLoader();

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private ClassSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	static
	{
		reload();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void reload()
	{
		/*------------------------------------------------------------------------------------------------------------*/

		s_classNames.clear();

		/*------------------------------------------------------------------------------------------------------------*/

		String path = Objects.requireNonNull(ClassSingleton.class.getResource("/net/hep/ami/ClassSingleton.class")).getPath();

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/

		Set<URL> jars = new HashSet<>();

		for(String PATH: ConfigSingleton.getSystemProperty("java.class.path").split(":", -1)) {
			walk(PATH, null);
		}

		for(String PATH: ConfigSingleton.getProperty("class_path").split(":", -1)) {
			walk(PATH, jars);
		}

		walk(path, null);

		/*------------------------------------------------------------------------------------------------------------*/

		s_classLoader = new URLClassLoader(jars.toArray(new URL[0]), ClassSingleton.class.getClassLoader());

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void walk(@NotNull String classPath, @Nullable Set<URL> jars)
	{
		File file = new File(classPath);

		walk(file, file, jars);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void walk(@NotNull File base, @NotNull File file, @Nullable Set<URL> jars)
	{
		if(file.isDirectory())
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* DIRECTORY                                                                                              */
			/*--------------------------------------------------------------------------------------------------------*/

			File[] files = file.listFiles();

			/*--------------------------------------------------------------------------------------------------------*/

			if(files != null)
			{
				for(File FILE : files)
				{
					walk(base, FILE, jars);
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		else
		{
			/*--------------------------------------------------------------------------------------------------------*/
			/* FILE                                                                                                   */
			/*--------------------------------------------------------------------------------------------------------*/

			if(file.getName().toLowerCase().endsWith(".jar"))
			{
				addJarFile(file, jars);
			}
			else
			{
				addClassFile(base, file);
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addJarFile(@NotNull File file, @Nullable Set<URL> jars)
	{
		try(ZipFile zipFile = new ZipFile(file))
		{
			zipFile.stream().forEach(x -> addClass(x.getName()));

			if(jars != null)
			{
				jars.add(file.toURI().toURL());
			}
		}
		catch(Exception e)
		{
			/* IGNORE */
		}
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addClassFile(@NotNull File base, @NotNull File file)
	{
		addClass(base.toURI().relativize(file.toURI()).getPath());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	private static void addClass(@NotNull String classFile)
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

	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Set<String> findClassNames(@NotNull String prefix)
	{
		return s_classNames.stream().filter(x -> x.startsWith(prefix)).collect(Collectors.toSet());
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	public static Class<?> forName(@NotNull String name) throws ClassNotFoundException
	{
		return Class.forName(name, true, s_classLoader);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static boolean extendsClass(@NotNull Class<?> child, @Nullable Class<?> parent)
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

	/*----------------------------------------------------------------------------------------------------------------*/

	public static boolean implementsInterface(@NotNull Class<?> child, @Nullable Class<?> parent)
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

	/*----------------------------------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------------------------------*/
}
