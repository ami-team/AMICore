package net.hep.ami.jdbc.driver;

public interface DriverInterface
{
	/*---------------------------------------------------------------------*/

	static class FieldType
	{
		String name;
		int size;

		public FieldType(String _name, int _size)
		{
			name = _name;
			size = _size;
		}
	}

	/*---------------------------------------------------------------------*/

	public String getInternalCatalog();

	/*---------------------------------------------------------------------*/

	public String getExternalCatalog();

	/*---------------------------------------------------------------------*/

	public FieldType jdbcTypeToAMIType(FieldType fieldType) throws Exception;

	/*---------------------------------------------------------------------*/

	public FieldType amiTypeToJDBCType(FieldType fieldType) throws Exception;

	/*---------------------------------------------------------------------*/
}
