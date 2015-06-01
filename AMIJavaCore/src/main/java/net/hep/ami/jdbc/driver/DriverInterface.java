package net.hep.ami.jdbc.driver;

public interface DriverInterface
{
	/*---------------------------------------------------------------------*/

	static class Type
	{
		String name;
		int size;

		public Type(String _name, int _size)
		{
			name = _name;
			size = _size;
		}
	}

	/*---------------------------------------------------------------------*/

	public Type jdbcTypeToAMIType(Type type) throws Exception;

	/*---------------------------------------------------------------------*/

	public Type amiTypeToJDBCType(Type type) throws Exception;

	/*---------------------------------------------------------------------*/
}
