package net.hep.ami.utility;

import java.io.*;

public class Shell {
	/*---------------------------------------------------------------------*/

	public static class ShellTuple extends Tuple3<Integer, StringBuilder, StringBuilder> {

		public ShellTuple(int _x, StringBuilder _y, StringBuilder _z) {
			super(_x, _y, _z);
		}
	}

	/*---------------------------------------------------------------------*/

	private static class StreamReader implements Runnable {

		private StringBuilder m_stringBuilder;
		private  InputStream   m_inputStream ;

		public StreamReader(StringBuilder stringBuilder, InputStream inputStream) {

			m_stringBuilder = stringBuilder;
			 m_inputStream  =  inputStream ;
		}

		@Override
		public void run() {

			String line;

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(m_inputStream));

			try {

				while((line = bufferedReader.readLine()) != null) {
					m_stringBuilder.append(line);
					m_stringBuilder.append('\n');
				}

			} catch(IOException e) {
				/* IGNORE */
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public static ShellTuple exec(String[] args) throws Exception {

		Process p = Runtime.getRuntime().exec(args);

		StringBuilder inputStringBuilder = new StringBuilder();
		StringBuilder errorStringBuilder = new StringBuilder();

		new Thread(new StreamReader(inputStringBuilder, p.getInputStream())).start();
		new Thread(new StreamReader(errorStringBuilder, p.getErrorStream())).start();

		return new ShellTuple(p.waitFor(), inputStringBuilder, errorStringBuilder);
	}

	/*---------------------------------------------------------------------*/
}
