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

	private static BigInteger _toPositiveBigInteger(BigInteger bigInteger, int bitLength)
	{
		if(bigInteger.compareTo(BigInteger.ZERO) < 0)
		{
			bigInteger = bigInteger.add(BigInteger.ONE.shiftLeft(bitLength));
		}

		return bigInteger;
	}

	/*---------------------------------------------------------------------*/

	public static BigInteger ipv4ToInteger(String ip) throws UnknownHostException
	{
		return ipv4ToInteger((Inet4Address) InetAddress.getByName(ip));
	}

	/*---------------------------------------------------------------------*/

	public static BigInteger ipv6ToInteger(String ip) throws UnknownHostException
	{
		return ipv6ToInteger((Inet6Address) InetAddress.getByName(ip));
	}

	/*---------------------------------------------------------------------*/

	public static BigInteger ipv4ToInteger(Inet4Address ip) throws UnknownHostException
	{
		byte[] parts = ip.getAddress();

		BigInteger result = BigInteger.ZERO;

		for(byte part: parts)
		{
			result = result.shiftLeft(8).or(_toPositiveBigInteger(BigInteger.valueOf(part), 8));
		}

		return _toPositiveBigInteger(result, 8 * parts.length);
	}

	/*---------------------------------------------------------------------*/

	public static BigInteger ipv6ToInteger(Inet6Address ip) throws UnknownHostException
	{
		byte[] parts = ip.getAddress();

		BigInteger result = BigInteger.ZERO;

		for(byte part: parts)
		{
			result = result.shiftLeft(8).or(_toPositiveBigInteger(BigInteger.valueOf(part), 8));
		}

		return _toPositiveBigInteger(result, 8 * parts.length);
	}

	/*---------------------------------------------------------------------*/

	public static Localization getCountryIPv4(BasicQuerier basicQuerier, String ip) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CONVERT IP                                                      */
		/*-----------------------------------------------------------------*/

		String _ip = ipv4ToInteger(ip).toString();

		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		RowSet rowSet = basicQuerier.executeQuery(String.format("SELECT `L`.`continentCode` AS `continentCode`, `L`.`countryCode` AS `countryCode` FROM `router_country_blocks_ipv4` AS `B`, `router_country_locations` AS `L` WHERE `B`.`rangeBegin` <= %s AND `B`.`rangeEnd` >= %s AND `B`.`geoFK` = `L`.`id`", _ip, _ip));

		/*-----------------------------------------------------------------*/
		/* GET LOCALIZATION                                                */
		/*-----------------------------------------------------------------*/

		List<Row> rowList = rowSet.getAll();

		if(rowList.size() == 0)
		{
			throw new Exception("could not localize IPv4 `" + ip + "`");
		}

		Row row = rowList.get(0);

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

	public static Localization getCountryIPv6(BasicQuerier basicQuerier, String ip) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* CONVERT IP                                                      */
		/*-----------------------------------------------------------------*/

		String _ip = ipv6ToInteger(ip).toString();

		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		RowSet rowSet = basicQuerier.executeQuery(String.format("SELECT `L`.`continentCode` AS `continentCode`, `L`.`countryCode` AS `countryCode` FROM `router_country_blocks_ipv6` AS `B`, `router_country_locations` AS `L` WHERE `B`.`rangeBegin` <= %s AND `B`.`rangeEnd` >= %s AND `B`.`geoFK` = `L`.`id`", _ip, _ip));

		/*-----------------------------------------------------------------*/
		/* GET LOCALIZATION                                                */
		/*-----------------------------------------------------------------*/

		List<Row> rowList = rowSet.getAll();

		if(rowList.size() == 0)
		{
			throw new Exception("could not localize IPv4 `" + ip + "`");
		}

		Row row = rowList.get(0);

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
