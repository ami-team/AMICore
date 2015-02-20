package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class ClassFinder {
	/*---------------------------------------------------------------------*/

	private ArrayList<String> m_classList = new ArrayList<String>();

	/*---------------------------------------------------------------------*/

	private String m_filter;

	private File m_base;

	/*---------------------------------------------------------------------*/

	public ClassFinder(String filter) {

		m_filter = filter;

		String name = "/net/hep/ami/utility/ClassFinder.class";

		String path = ClassFinder.class.getResource(name).getPath();

		path = path.substring(0, path.indexOf(name));

		dispatch(m_base = new File(path));
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

			if(fileName.endsWith(".ear")
			   ||
			   fileName.endsWith(".jar")
			   ||
			   fileName.endsWith(".war")
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
				m_classList.add(className);
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public ArrayList<String> getClassList() {
		return m_classList;
	}

	/*---------------------------------------------------------------------*/

	public String getFilter() {
		return m_filter;
	}

	/*---------------------------------------------------------------------*/
}
