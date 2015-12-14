package net.hep.ami.jdbc.pool;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;

public class TransactionPoolSingleton
{
	/*---------------------------------------------------------------------*/

	private static final Map<Long, Map<String, DriverAbstractClass>> m_pools = new HashMap<Long, Map<String, DriverAbstractClass>>();

	/*---------------------------------------------------------------------*/

	private static final java.util.concurrent.atomic.AtomicLong m_cnt = new java.util.concurrent.atomic.AtomicLong(0);

	/*---------------------------------------------------------------------*/

	public static long bookNewTransactionId()
	{
		return m_cnt.getAndIncrement();
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String catalog, long transactionId) throws Exception
	{
		if(transactionId < 0)
		{
			throw new Exception("invalid transaction identifier (" + transactionId + ")");
		}

		/*-----------------------------------------------------------------*/

		String key = catalog;

		/*-----------------------------------------------------------------*/

		DriverAbstractClass result;

		Map<String, DriverAbstractClass> transaction;

		synchronized(TransactionPoolSingleton.class)
		{
		/**/	transaction = m_pools.get(transactionId);
		/**/
		/**/	if(transaction == null)
		/**/	{
		/**/		m_pools.put(transactionId, transaction = new HashMap<String, DriverAbstractClass>());
		/**/
		/**/		transaction.put(key, result = CatalogSingleton.getConnection(catalog));
		/**/	}
		/**/	else
		/**/	{
		/**/		result = transaction.get(key);
		/**/
		/**/		if(result == null)
		/**/		{
		/**/			transaction.put(key, result = CatalogSingleton.getConnection(catalog));
		/**/		}
		/**/	}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static DriverAbstractClass getConnection(String jdbcUrl, String user, String pass, long transactionId) throws Exception
	{
		if(transactionId < 0)
		{
			throw new Exception("invalid transaction identifier (" + transactionId + ")");
		}

		/*-----------------------------------------------------------------*/

		String key = jdbcUrl + "@" + user;

		/*-----------------------------------------------------------------*/

		DriverAbstractClass result;

		Map<String, DriverAbstractClass> transaction;

		synchronized(TransactionPoolSingleton.class)
		{
		/**/	transaction = m_pools.get(transactionId);
		/**/
		/**/	if(transaction == null)
		/**/	{
		/**/		m_pools.put(transactionId, transaction = new HashMap<String, DriverAbstractClass>());
		/**/
		/**/		transaction.put(key, result = DriverSingleton.getConnection(jdbcUrl, user, pass));
		/**/	}
		/**/	else
		/**/	{
		/**/		result = transaction.get(key);
		/**/
		/**/		if(result == null)
		/**/		{
		/**/			transaction.put(key, result = DriverSingleton.getConnection(jdbcUrl, user, pass));
		/**/		}
		/**/	}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static void commitAndRelease(long transactionId) throws Exception
	{
		Map<String, DriverAbstractClass> transaction;

		/*-----------------------------------------------------------------*/
		/* REMOVE TRANSACTION FROM POOL                                    */
		/*-----------------------------------------------------------------*/

		synchronized(TransactionPoolSingleton.class)
		{
		/**/	transaction = m_pools.get(transactionId);
		/**/
		/**/	if(transaction != null)
		/**/	{
		/**/		m_pools.remove(transactionId);
		/**/	}
		}

		/*-----------------------------------------------------------------*/
		/* COMMIT AND RELEASE CONNECTIONS                                  */
		/*-----------------------------------------------------------------*/

		if(transaction != null)
		{
			for(DriverAbstractClass driver: transaction.values())
			{
				driver.commitAndRelease();
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void rollbackAndRelease(long transactionId) throws Exception
	{
		Map<String, DriverAbstractClass> transaction;

		/*-----------------------------------------------------------------*/
		/* REMOVE TRANSACTION FROM POOL                                    */
		/*-----------------------------------------------------------------*/

		synchronized(TransactionPoolSingleton.class)
		{
		/**/	transaction = m_pools.get(transactionId);
		/**/
		/**/	if(transaction != null)
		/**/	{
		/**/		m_pools.remove(transactionId);
		/**/	}
		}

		/*-----------------------------------------------------------------*/
		/* ROLLBACK AND RELEASE CONNECTIONS                                */
		/*-----------------------------------------------------------------*/

		if(transaction != null)
		{
			for(DriverAbstractClass driver: transaction.values())
			{
				driver.rollbackAndRelease();
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void clear()
	{
		synchronized(TransactionPoolSingleton.class)
		{
		/**/	/*---------------------------------------------------------*/
		/**/	/* ROLLBACK AND RELEASE CONNECTIONS                        */
		/**/	/*---------------------------------------------------------*/
		/**/
		/**/	for(Map<String, DriverAbstractClass> transaction: m_pools.values())
		/**/	{
		/**/		for(DriverAbstractClass connection: transaction.values())
		/**/		{
		/**/			try
		/**/			{
		/**/				connection.rollbackAndRelease();
		/**/			}
		/**/			catch(Exception e)
		/**/			{
		/**/				LogSingleton.defaultLogger.error(e.getMessage());
		/**/			}
		/**/		}
		/**/	}
		/**/
		/**/	/*---------------------------------------------------------*/
		/**/	/* CLEAR TRANSACTION POOL                                  */
		/**/	/*---------------------------------------------------------*/
		/**/
		/**/	m_pools.clear();
		/**/
		/**/	/*---------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/
}
