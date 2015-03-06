package net.hep.ami.jdbc.pool;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;

public class TransactionPoolSingleton {
	/*---------------------------------------------------------------------*/

	private static Map<Integer, Map<String, DriverAbstractClass>> m_pool = new HashMap<Integer, Map<String, DriverAbstractClass>>();

	/*---------------------------------------------------------------------*/

	private static java.util.concurrent.atomic.AtomicInteger m_cnt = new java.util.concurrent.atomic.AtomicInteger(0);

	/*---------------------------------------------------------------------*/

	public static int getTransactionID() {

		return m_cnt.getAndDecrement();
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String catalog, int transactionID) throws Exception {
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

	public static DriverAbstractClass getConnection(String jdbcUrl, String user, String pass, int transactionID) throws Exception {
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

	public static void commitAndRelease(int transactionID) throws Exception {

		synchronized(TransactionPoolSingleton.class) {

			Map<String, DriverAbstractClass> map = m_pool.get(transactionID);

			for(DriverAbstractClass driver: map.values()) {

				driver.commitAndRelease();
			}
		}
	}

	/*---------------------------------------------------------------------*/

	public static void rollbackAndRelease(int transactionID) throws Exception {

		synchronized(TransactionPoolSingleton.class) {

			Map<String, DriverAbstractClass> map = m_pool.get(transactionID);

			for(DriverAbstractClass driver: map.values()) {

				driver.rollbackAndRelease();
			}
		}
	}

	/*---------------------------------------------------------------------*/
}
