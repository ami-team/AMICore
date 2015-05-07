package net.hep.ami.utility;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.*;

public class ClassFinder {
	/*---------------------------------------------------------------------*/

	private final Set<String> m_classes = new HashSet<String>();

	/*---------------------------------------------------------------------*/

	private File m_base;

	private String m_filter;

	/*---------------------------------------------------------------------*/

	public ClassFinder(String filter) {

		String name = "/net/hep/ami/utility/ClassFinder.class";

		URL url = ClassFinder.class.getResource(name);

		String protocol = url.getProtocol();
		String path     = url.getPath    ();

		if(protocol.equals("jar")) {
			path = path.substring(5, path.indexOf("!"));
			m_base = new File(path).getParentFile();
		} else {
			path = path.substring(0, path.indexOf(name));
			m_base = new File(path).getAbsoluteFile();
		}

		m_filter = filter;

		dispatch(m_base);
	}

	/*---------------------------------------------------------------------*/

	private void addDirectory(File FILE) {

		for(File file: FILE.listFiles()) {

			dispatch(file);
		}
	}

	/*---------------------------------------------------------------------*/

	private void dispatch(File file) {

		if(file.isDirectory()) {
			addDirectory(file);

		} else {

			if(file.getName().toLowerCase().endsWith(".jar")) {
				addZip(file);
			} else {
				addFile(file);
			}
		}
	}

	/*---------------------------------------------------------------------*/

	private void addZip(File file) {

		try {
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

			while(entries.hasMoreElements()) {

				String className = entries.nextElement().getName();

				addClass(className);
			}

			/*-------------------------------------------------------------*/
			/* CLOSE ZIP FILE                                              */
			/*-------------------------------------------------------------*/

			zipFile.close();

			/*-------------------------------------------------------------*/
		} catch(Exception e) {
			/* IGNORE */
		}
	}

  	/*---------------------------------------------------------------------*/

	private void addFile(File file) {

		String className = m_base.toURI().relativize(file.toURI()).getPath();

		addClass(className);
	}

	/*---------------------------------------------------------------------*/

	private void addClass(String className) {

		if(className.endsWith(".class")) {

			className = className.substring(0, className.length() - 6)
			                     .replace('\\', '.')
			                     .replace('/', '.')
			;

			if(className.startsWith(m_filter)) {
				m_classes.add(className);
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public File getBase() {

		return m_base;
	}

	/*---------------------------------------------------------------------*/

	public String getFilter() {

		return m_filter;
	}

	/*---------------------------------------------------------------------*/

	public Set<String> getClasses() {

		return m_classes;
	}

	/*---------------------------------------------------------------------*/

	public static boolean extendsClass(Class<?> child, Class<?> parent) {

		boolean result = false;

		while((child = child.getSuperclass()) != null) {

			if(child == parent) {

				result = true;
				break;
			}
		}

		return result;
	}

	/*---------------------------------------------------------------------*/
}
