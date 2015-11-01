package net.hep.ami;

import java.net.*;
import java.util.*;
import java.math.*;

import net.hep.ami.jdbc.*;

public class LocalizationSingleton
{
	/*---------------------------------------------------------------------*/

	public static class Localization
	{
		public String continentCode;
		public String countryCode;

		public Localization(String _continentCode, String _countryCode)
		{
			continentCode = _continentCode;
			countryCode = _countryCode;
		}
	}

	/*---------------------------------------------------------------------*/

	private static final String m_jdbcUrl = ConfigSingleton.getProperty("jdbc_url");
	private static final String m_routerUser = ConfigSingleton.getProperty("router_user");
	private static final String m_routerPass = ConfigSingleton.getProperty("router_pass");

	/*---------------------------------------------------------------------*/

	public static BigInteger ipv4ToInteger(String ip) throws UnknownHostException
	{
		return ipv4ToInteger((Inet4Address) InetAddress.getByName(ip));
	}

	/*---------------------------------------------------------------------*/

	public static BigInteger ipv4ToInteger(Inet4Address ip) throws UnknownHostException
	{
		byte[] parts = ip.getAddress();

		final int length = parts.length;

		BigInteger result = BigInteger.ZERO;

		for(int i = 0; i < length; i++)
		{
			result = result.shiftLeft(8).or(BigInteger.valueOf(parts[i]));
		}

		return result.compareTo(BigInteger.ZERO) < 0 ? result.add(BigInteger.ONE.shiftLeft(8 * length)) : result;
	}

	/*---------------------------------------------------------------------*/

	public static BigInteger ipv6ToInteger(String ip) throws UnknownHostException
	{
		return ipv6ToInteger((Inet6Address) InetAddress.getByName(ip));
	}

	/*---------------------------------------------------------------------*/

	public static BigInteger ipv6ToInteger(Inet6Address ip) throws UnknownHostException
	{
		byte[] parts = ip.getAddress();

		final int length = parts.length;

		BigInteger result = BigInteger.ZERO;

		for(int i = 0; i < length; i++)
		{
			result = result.shiftLeft(8).or(BigInteger.valueOf(parts[i]));
		}

		return result.compareTo(BigInteger.ZERO) < 0 ? result.add(BigInteger.ONE.shiftLeft(8 * length)) : result;
	}

	/*---------------------------------------------------------------------*/

	public static Localization getCountryIPv4(String ip) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CONVERT IP                                                      */
		/*-----------------------------------------------------------------*/

		String _ip = ipv4ToInteger(ip).toString();

		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		BasicQuerier basicQuerier = new BasicQuerier(
			m_jdbcUrl,
			m_routerUser,
			m_routerPass
		);

		/*-----------------------------------------------------------------*/

		Row row;

		try
		{
			/*-------------------------------------------------------------*/
			/* EXECUTE QUERY                                               */
			/*-------------------------------------------------------------*/

			RowSet rowSet = basicQuerier.executeSQLQuery(String.format("SELECT `L`.`continentCode` AS `continentCode`, `L`.`countryCode` AS `countryCode` FROM `router_country_blocks_ipv4` AS `B`, `router_country_locations` AS `L` WHERE `B`.`rangeBegin` <= %s AND `B`.`rangeEnd` >= %s AND `B`.`geonameFK` = `L`.`id`", _ip, _ip));

			/*-------------------------------------------------------------*/
			/* GET LOCALIZATION                                            */
			/*-------------------------------------------------------------*/

			List<Row> rowList = rowSet.getAll();

			if(rowList.size() == 0)
			{
				throw new Exception("could not localize IPv4 `" + ip + "`");
			}

			row = rowList.get(0);

			/*-------------------------------------------------------------*/
		}
		finally
		{
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* RETURN LOCALIZATION                                             */
		/*-----------------------------------------------------------------*/

		return new Localization(
			row.getValue("continentCode"),
			row.getValue( "countryCode" )
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Localization getCountryIPv6(String ip) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CONVERT IP                                                      */
		/*-----------------------------------------------------------------*/

		String _ip = ipv6ToInteger(ip).toString();

		/*-----------------------------------------------------------------*/
		/* CREATE QUERIER                                                  */
		/*-----------------------------------------------------------------*/

		BasicQuerier basicQuerier = new BasicQuerier(
			m_jdbcUrl,
			m_routerUser,
			m_routerPass
		);

		/*-----------------------------------------------------------------*/

		Row row;

		try
		{
			/*-----------------------------------------------------------------*/
			/* EXECUTE QUERY                                                   */
			/*-----------------------------------------------------------------*/

			RowSet rowSet = basicQuerier.executeSQLQuery(String.format("SELECT `L`.`continentCode` AS `continentCode`, `L`.`countryCode` AS `countryCode` FROM `router_country_blocks_ipv6` AS `B`, `router_country_locations` AS `L` WHERE `B`.`rangeBegin` <= %s AND `B`.`rangeEnd` >= %s AND `B`.`geonameFK` = `L`.`id`", _ip, _ip));

			/*-------------------------------------------------------------*/
			/* GET LOCALIZATION                                            */
			/*-------------------------------------------------------------*/

			List<Row> rowList = rowSet.getAll();

			if(rowList.size() == 0)
			{
				throw new Exception("could not localize IPv4 `" + ip + "`");
			}

			row = rowList.get(0);

			/*-------------------------------------------------------------*/
		}
		finally
		{
			basicQuerier.rollbackAndRelease();
		}

		/*-----------------------------------------------------------------*/
		/* RETURN LOCALIZATION                                             */
		/*-----------------------------------------------------------------*/

		return new Localization(
			row.getValue("continentCode"),
			row.getValue( "countryCode" )
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
