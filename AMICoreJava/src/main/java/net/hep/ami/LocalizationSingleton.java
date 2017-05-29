package net.hep.ami;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.math.*;
import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.utility.parser.*;

public class LocalizationSingleton
{
	/*---------------------------------------------------------------------*/

	public static final class Localization
	{
		public final String continentCode;
		public final String countryCode;

		public Localization(String _continentCode, String _countryCode)
		{
			continentCode = _continentCode;
			countryCode = _countryCode;
		}
	}

	/*---------------------------------------------------------------------*/

	private LocalizationSingleton() {}

	/*---------------------------------------------------------------------*/

	private static BigInteger toPositiveBigInteger(BigInteger bigInteger, int bitLength)
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
			result = result.shiftLeft(8).or(toPositiveBigInteger(BigInteger.valueOf(part), 8));
		}

		return toPositiveBigInteger(result, 8 * parts.length);
	}

	/*---------------------------------------------------------------------*/

	public static BigInteger ipv6ToInteger(Inet6Address ip) throws UnknownHostException
	{
		byte[] parts = ip.getAddress();

		BigInteger result = BigInteger.ZERO;

		for(byte part: parts)
		{
			result = result.shiftLeft(8).or(toPositiveBigInteger(BigInteger.valueOf(part), 8));
		}

		return toPositiveBigInteger(result, 8 * parts.length);
	}

	/*---------------------------------------------------------------------*/

	public static void fill(Querier querier) throws Exception
	{
		final BigInteger ONE = BigInteger.valueOf(1);
		final BigInteger TWO = BigInteger.valueOf(2);

		/*-----------------------------------------------------------------*/
		/* LOCATIONS                                                       */
		/*-----------------------------------------------------------------*/

		File locationsEn = new File(
			ConfigSingleton.getConfigPathName()
			+ File.separator +
			"GeoLite2-Country-Locations-en.csv"
		);

		if(locationsEn.exists())
		{
			List<Map<String, String>> locations = CSV.parseAsMap(new FileInputStream(locationsEn));

			querier.executeUpdate("DELETE FROM `router_locations`");

			try(PreparedStatement preparedStatement = querier.sqlPrepareStatement("INSERT INTO `router_locations` (`id`, `continentCode`, `countryCode`) VALUES (?, ?, ?)"))
			{
				for(Map<String, String> location: locations)
				{
					preparedStatement.setString(1, location.get("geoname_id"));
					preparedStatement.setString(2, location.get("continent_code"));
					preparedStatement.setString(3, location.get("country_iso_code"));
					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
		}

		/*-----------------------------------------------------------------*/
		/* IPv4 BLOCKS                                                     */
		/*-----------------------------------------------------------------*/

		File blocksIPv4 = new File(
			ConfigSingleton.getConfigPathName()
			+ File.separator +
			"GeoLite2-Country-Blocks-IPv4.csv"
		);

		if(blocksIPv4.exists())
		{
			List<Map<String, String>> blocks = CSV.parseAsMap(new FileInputStream(blocksIPv4));

			querier.executeUpdate("DELETE FROM `router_ipv4_blocks`");

			try(PreparedStatement preparedStatement = querier.sqlPrepareStatement("INSERT INTO `router_ipv4_blocks` (`network`, `rangeBegin`, `rangeEnd`, `geoFK`) VALUES (?, ?, ?, ?)"))
			{
				String network;
				String geonameId;

				BigInteger base;

				int index;

				for(Map<String, String> block: blocks)
				{
					network = block.get("network");
					geonameId = block.get("geoname_id");

					index = network.indexOf('/');

					if(index > 0 && geonameId.isEmpty() == false)
					{
						base = ipv4ToInteger(network.substring(0, index));

						preparedStatement.setString(1, network);
						preparedStatement.setString(2, base.toString());
						preparedStatement.setString(3, base.add(TWO.pow(32 - Integer.parseInt(network.substring(index + 1))).subtract(ONE)).toString());
						preparedStatement.setString(4, geonameId);
						preparedStatement.addBatch();
					}
				}

				preparedStatement.executeBatch();
			}
		}

		/*-----------------------------------------------------------------*/
		/* IPv6 BLOCKS                                                     */
		/*-----------------------------------------------------------------*/

		File blocksIPv6 = new File(
			ConfigSingleton.getConfigPathName()
			+ File.separator +
			"GeoLite2-Country-Blocks-IPv6.csv"
		);

		if(blocksIPv6.exists())
		{
			List<Map<String, String>> blocks = CSV.parseAsMap(new FileInputStream(blocksIPv6));

			querier.executeUpdate("DELETE FROM `router_ipv6_blocks`");

			try(PreparedStatement preparedStatement = querier.sqlPrepareStatement("INSERT INTO `router_ipv6_blocks` (`network`, `rangeBegin`, `rangeEnd`, `geoFK`) VALUES (?, ?, ?, ?)"))
			{
				String network;
				String geonameId;

				BigInteger base;

				int index;

				for(Map<String, String> block: blocks)
				{
					network = block.get("network");
					geonameId = block.get("geoname_id");

					index = network.indexOf('/');

					if(index > 0 && geonameId.isEmpty() == false)
					{
						base = ipv6ToInteger(network.substring(0, index));

						preparedStatement.setString(1, network);
						preparedStatement.setString(2, base.toString());
						preparedStatement.setString(3, base.add(TWO.pow(128 - Integer.parseInt(network.substring(index + 1))).subtract(ONE)).toString());
						preparedStatement.setString(4, geonameId);
						preparedStatement.addBatch();
					}
				}

				preparedStatement.executeBatch();
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static Localization localizeIP(Querier querier, String ip) throws Exception
	{
		/*-----------------------------------------------------------------*/
		/* PARSE IP                                                        */
		/*-----------------------------------------------------------------*/

		String _ip;
		String _table;

		try
		{
			_ip = ipv4ToInteger(ip).toString();
			_table = "router_ipv4_blocks";
		}
		catch(UnknownHostException e1)
		{
			try
			{
				_ip = ipv6ToInteger(ip).toString();
				_table = "router_ipv6_blocks";
			}
			catch(UnknownHostException e2)
			{
				throw new Exception("invalid IP `" + ip + "`");
			}
		}

		/*-----------------------------------------------------------------*/
		/* EXECUTE QUERY                                                   */
		/*-----------------------------------------------------------------*/

		RowSet rowSet = querier.executeQuery(String.format(
			"SELECT `L`.`continentCode`, `L`.`countryCode` FROM `%s` AS `B`, `router_locations` AS `L` WHERE %s BETWEEN `B`.`rangeBegin` AND `B`.`rangeEnd` AND `B`.`geoFK` = `L`.`id`",
			_table,
			_ip
		));

		/*-----------------------------------------------------------------*/
		/* GET LOCALIZATION                                                */
		/*-----------------------------------------------------------------*/

		List<Row> rowList = rowSet.getAll();

		if(rowList.isEmpty())
		{
			throw new Exception("could not localize IP `" + ip + "`");
		}

		Row row = rowList.get(0);

		/*-----------------------------------------------------------------*/
		/* RETURN LOCALIZATION                                             */
		/*-----------------------------------------------------------------*/

		return new Localization(
			row.getValue(0),
			row.getValue(1)
		);

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
