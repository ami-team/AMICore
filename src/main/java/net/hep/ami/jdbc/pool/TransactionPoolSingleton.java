package net.hep.ami.jdbc.pool;

import java.util.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;

public class TransactionPoolSingleton {
	/*---------------------------------------------------------------------*/

	private static Map<Integer, Map<String, DriverAbstractClass>> m_pools = new HashMap<Integer, Map<String, DriverAbstractClass>>();

	/*---------------------------------------------------------------------*/

	private static java.util.concurrent.atomic.AtomicInteger m_cnt = new java.util.concurrent.atomic.AtomicInteger(0);

	/*---------------------------------------------------------------------*/

	public static int bookNewTransactionID() {

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

		String key = catalog;

		/*-----------------------------------------------------------------*/

		Map<String, DriverAbstractClass> transaction;

		DriverAbstractClass result;

		synchronized(TransactionPoolSingleton.class) {

		/**/	transaction = m_pools.get(transactionID);
		/**/
		/**/	if(transaction == null) {
		/**/
		/**/		m_pools.put(transactionID, transaction = new HashMap<String, DriverAbstractClass>());
		/**/
		/**/		transaction.put(key, result = CatalogSingleton.getConnection(catalog));
		/**/
		/**/	} else {
		/**/
		/**/		result = transaction.get(key);
		/**/
		/**/		if(result == null) {
		/**/
		/**/			transaction.put(key, result = CatalogSingleton.getConnection(catalog));
		/**/		}
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

		String key = jdbcUrl + "@" + user;

		/*-----------------------------------------------------------------*/

		Map<String, DriverAbstractClass> transaction;

		DriverAbstractClass result;

		synchronized(TransactionPoolSingleton.class) {

		/**/	transaction = m_pools.get(transactionID);
		/**/
		/**/	if(transaction == null) {
		/**/
		/**/		m_pools.put(transactionID, transaction = new HashMap<String, DriverAbstractClass>());
		/**/
		/**/		transaction.put(key, result = DriverSingleton.getConnection(jdbcUrl, user, pass));
		/**/
		/**/	} else {
		/**/
		/**/		result = transaction.get(key);
		/**/
		/**/		if(result == null) {
		/**/
		/**/			transaction.put(key, result = DriverSingleton.getConnection(jdbcUrl, user, pass));
		/**/		}
		/**/	}

		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static void commitAndRelease(int transactionID) throws Exception {

		Map<String, DriverAbstractClass> transaction;

		/*-----------------------------------------------------------------*/
		/* REMOVE TRANSACTION FROM POOL                                    */
		/*-----------------------------------------------------------------*/

		synchronized(TransactionPoolSingleton.class) {

		/**/	transaction = m_pools.get(transactionID);
		/**/
		/**/	if(transaction != null) {
		/**/		m_pools.remove(transactionID);
		/**/	}

		}

		/*-----------------------------------------------------------------*/
		/* COMMIT AND RELEASE CONNECTIONS                                  */
		/*-----------------------------------------------------------------*/

		if(transaction != null) {

			for(DriverAbstractClass driver: transaction.values()) {

				driver.commitAndRelease();
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void rollbackAndRelease(int transactionID) throws Exception {

		Map<String, DriverAbstractClass> transaction;

		/*-----------------------------------------------------------------*/
		/* REMOVE TRANSACTION FROM POOL                                    */
		/*-----------------------------------------------------------------*/

		synchronized(TransactionPoolSingleton.class) {

		/**/	transaction = m_pools.get(transactionID);
		/**/
		/**/	if(transaction != null) {
		/**/		m_pools.remove(transactionID);
		/**/	}

		}

		/*-----------------------------------------------------------------*/
		/* ROLLBACK AND RELEASE CONNECTIONS                                */
		/*-----------------------------------------------------------------*/

		if(transaction != null) {

			for(DriverAbstractClass driver: transaction.values()) {

				driver.rollbackAndRelease();
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/
}
