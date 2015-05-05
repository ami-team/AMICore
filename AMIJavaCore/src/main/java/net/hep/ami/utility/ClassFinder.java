package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class ClassFinder {
	/*---------------------------------------------------------------------*/

	private final Set<String> m_classes = new HashSet<String>();

	/*---------------------------------------------------------------------*/

	private String m_filter;

	private File m_base;

	/*---------------------------------------------------------------------*/

	public ClassFinder(String filter) {

		m_filter = filter;

		String name = "/net/hep/ami/utility/ClassFinder.class";

		String path = ClassFinder.class.getResource(name).getPath();

		path = path.substring(0, path.indexOf(name));

		if(path.startsWith("file:")
		   &&
		   (
				path.endsWith(".jar!")
				||
				path.endsWith(".war!")
				||
				path.endsWith(".ear!")
		   )
		 ) {
			m_base = new File(path.substring(5, path.length() - 1)).getParentFile();
		} else {
			m_base = new File(path);
		}

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
			String fileName = file.getName().toLowerCase();

			if(fileName.endsWith(".jar")
			   ||
			   fileName.endsWith(".war")
			   ||
			   fileName.endsWith(".ear")
			 ) {
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

	public Set<String> getClasses() {

		return m_classes;
	}

	/*---------------------------------------------------------------------*/

	public String getFilter() {

		return m_filter;
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
