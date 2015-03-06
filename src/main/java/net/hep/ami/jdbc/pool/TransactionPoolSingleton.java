package net.hep.ami.jdbc.pool;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;

public class TransactionPoolSingleton {
	/*---------------------------------------------------------------------*/

	private static Map<Long, Map<String, DriverAbstractClass>> m_pool = new HashMap<Long, Map<String, DriverAbstractClass>>();

	/*---------------------------------------------------------------------*/

	private static java.util.concurrent.atomic.AtomicLong m_cnt = new java.util.concurrent.atomic.AtomicLong(1);

	/*---------------------------------------------------------------------*/

	public static long bookTransactionID() {

		return m_cnt.getAndDecrement();
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String catalog, long transactionID) throws Exception {
		/*-----------------------------------------------------------------*/
		/* WITHOUT TRANSACTION                                             */
		/*-----------------------------------------------------------------*/

		if(transactionID < 0) {
			throw new Exception("invalid transaction identifier");
		}

		/*-----------------------------------------------------------------*/
		/* WITH TRANSACTION                                                */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass result;

		String key = "::" + catalog;

		synchronized(TransactionPoolSingleton.class) {

		/**/	/****/ if(m_pool.containsKey(transactionID) == false) {
		/**/
		/**/		result = m_pool.put(transactionID, new HashMap<String, DriverAbstractClass>()).put(key, CatalogSingleton.getConnection(catalog));
		/**/
		/**/	} else if(m_pool.get(transactionID).containsKey(key) == false) {
		/**/
		/**/		result = m_pool.get(transactionID).put(key, CatalogSingleton.getConnection(catalog));
		/**/
		/**/	} else {
		/**/		result = m_pool.get(transactionID).get(key);
		/**/	}

		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String jdbcUrl, String user, String pass, long transactionID) throws Exception {
		/*-----------------------------------------------------------------*/
		/* WITHOUT TRANSACTION                                             */
		/*-----------------------------------------------------------------*/

		if(transactionID < 0) {
			throw new Exception("invalid transaction identifier");
		}

		/*-----------------------------------------------------------------*/
		/* WITH TRANSACTION                                                */
		/*-----------------------------------------------------------------*/

		DriverAbstractClass result;

		String key = "::" + jdbcUrl + "@" + user;

		synchronized(TransactionPoolSingleton.class) {

		/**/	/****/ if(m_pool.containsKey(transactionID) == false) {
		/**/
		/**/		result = m_pool.put(transactionID, new HashMap<String, DriverAbstractClass>()).put(key, DriverSingleton.getConnection(jdbcUrl, user, pass));
		/**/
		/**/	} else if(m_pool.get(transactionID).containsKey(key) == false) {
		/**/
		/**/		result = m_pool.get(transactionID).put(key, DriverSingleton.getConnection(jdbcUrl, user, pass));
		/**/
		/**/	} else {
		/**/			result = m_pool.get(transactionID).get(key);
		/**/	}

		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static void commitAndRelease(long transactionID) throws Exception {

		synchronized(TransactionPoolSingleton.class) {

		}
	}

	/*---------------------------------------------------------------------*/

	public static void rollbackAndRelease(long transactionID) throws Exception {

		synchronized(TransactionPoolSingleton.class) {

		}
	}

	/*---------------------------------------------------------------------*/
}
