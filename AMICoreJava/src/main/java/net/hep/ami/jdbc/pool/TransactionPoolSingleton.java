package net.hep.ami.jdbc.pool;

import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;
import net.hep.ami.jdbc.driver.annotation.*;

public class TransactionPoolSingleton
{
	/*---------------------------------------------------------------------*/

	private static final java.util.concurrent.atomic.AtomicLong s_lastId = new java.util.concurrent.atomic.AtomicLong(1);

	/*---------------------------------------------------------------------*/

	private static final Map<Long, Map<String, AbstractDriver>> s_pools = new HashMap<>();

	/*---------------------------------------------------------------------*/

	private TransactionPoolSingleton() {}

	/*---------------------------------------------------------------------*/

	public static long bookNewTransactionId()
	{
		return s_lastId.getAndIncrement();
	}

	/*---------------------------------------------------------------------*/

	public static AbstractDriver getConnection(String catalog, long transactionId) throws Exception
	{
		if(transactionId <= 0x000000000000
		   ||
		   transactionId >= s_lastId.get()
		 ) {
			throw new Exception("invalid transaction identifier (" + transactionId + ")");
		}

		/*-----------------------------------------------------------------*/

		String key = CatalogSingleton.getKey(catalog);

		/*-----------------------------------------------------------------*/

		AbstractDriver result;

		Map<String, AbstractDriver> transaction;

		synchronized(TransactionPoolSingleton.class)
		{
		/**/	transaction = s_pools.get(transactionId);
		/**/
		/**/	if(transaction == null)
		/**/	{
		/**/		s_pools.put(transactionId, transaction = new HashMap<>());
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

	public static AbstractDriver getConnection(@Nullable String catalog, String jdbcUrl, String user, String pass, long transactionId) throws Exception
	{
		if(transactionId <= 0x000000000000
		   ||
		   transactionId >= s_lastId.get()
		 ) {
			throw new Exception("invalid transaction identifier (" + transactionId + ")");
		}

		/*-----------------------------------------------------------------*/

		String key = DriverSingleton.getKey(jdbcUrl, user);

		/*-----------------------------------------------------------------*/

		AbstractDriver result;

		Map<String, AbstractDriver> transaction;

		synchronized(TransactionPoolSingleton.class)
		{
		/**/	transaction = s_pools.get(transactionId);
		/**/
		/**/	if(transaction == null)
		/**/	{
		/**/		s_pools.put(transactionId, transaction = new HashMap<>());
		/**/
		/**/		transaction.put(key, result = DriverSingleton.getConnection(catalog, jdbcUrl, user, pass));
		/**/	}
		/**/	else
		/**/	{
		/**/		result = transaction.get(key);
		/**/
		/**/		if(result == null)
		/**/		{
		/**/			transaction.put(key, result = DriverSingleton.getConnection(catalog, jdbcUrl, user, pass));
		/**/		}
		/**/	}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public static void commitAndRelease(long transactionId) throws Exception
	{
		Map<String, AbstractDriver> transaction;

		/*-----------------------------------------------------------------*/
		/* REMOVE TRANSACTION FROM POOL                                    */
		/*-----------------------------------------------------------------*/

		synchronized(TransactionPoolSingleton.class)
		{
		/**/	transaction = s_pools.remove(transactionId);
		}

		/*-----------------------------------------------------------------*/

		if(transaction == null)
		{
			throw new Exception("invalid transaction identifier (" + transactionId + ")");
		}

		/*-----------------------------------------------------------------*/
		/* COMMIT AND RELEASE CONNECTIONS                                  */
		/*-----------------------------------------------------------------*/

		int flag;

		try
		{
			/*-------------------------------------------------------------*/

			for(AbstractDriver driver: transaction.values())
			{
				if(driver.getJdbcType() == Jdbc.Type.SQL)
				{
					driver.executeQuery("SELECT 1");
				}
			}

			/*-------------------------------------------------------------*/

			flag = 0;

			for(AbstractDriver driver: transaction.values())
			{
				try { driver.commitAndRelease(); } catch(Exception e2) { flag = 2; }
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e1)
		{
			/*-------------------------------------------------------------*/

			flag = 1;

			for(AbstractDriver driver: transaction.values())
			{
				try { driver.rollbackAndRelease(); } catch(Exception e2) {/* IGNORE */}
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		if(flag > 0)
		{
			if(flag > 1)
			{
				LogSingleton.root.error(LogSingleton.FATAL, "broken transaction with inconsistencies");

				throw new Exception("broken transaction with inconsistencies");
			}
			else
			{
				LogSingleton.root.error(LogSingleton.FATAL, "broken transaction without inconsistency");

				throw new Exception("broken transaction without inconsistency");
			}
		}

		/*-----------------------------------------------------------------*/
	}

	/*---------------------------------------------------------------------*/

	public static void rollbackAndRelease(long transactionId) throws Exception
	{
		Map<String, AbstractDriver> transaction;

		/*-----------------------------------------------------------------*/
		/* REMOVE TRANSACTION FROM POOL                                    */
		/*-----------------------------------------------------------------*/

		synchronized(TransactionPoolSingleton.class)
		{
		/**/	transaction = s_pools.remove(transactionId);
		}

		/*-----------------------------------------------------------------*/

		if(transaction == null)
		{
			throw new Exception("invalid transaction identifier (" + transactionId + ")");
		}

		/*-----------------------------------------------------------------*/
		/* ROLLBACK AND RELEASE CONNECTIONS                                */
		/*-----------------------------------------------------------------*/

		int flag;

		try
		{
			/*-------------------------------------------------------------*/

			for(AbstractDriver driver: transaction.values())
			{
				if(driver.getJdbcType() == Jdbc.Type.SQL)
				{
					driver.executeQuery("SELECT 1");
				}
			}

			/*-------------------------------------------------------------*/

			flag = 0;

			for(AbstractDriver driver: transaction.values())
			{
				try { driver.rollbackAndRelease(); } catch(Exception e2) { flag = 2; }
			}

			/*-------------------------------------------------------------*/
		}
		catch(Exception e1)
		{
			/*-------------------------------------------------------------*/

			flag = 1;

			for(AbstractDriver driver: transaction.values())
			{
				try { driver.rollbackAndRelease(); } catch(Exception e2) {/* IGNORE */}
			}

			/*-------------------------------------------------------------*/
		}

		/*-----------------------------------------------------------------*/

		if(flag > 0)
		{
			if(flag > 1)
			{
				LogSingleton.root.error(LogSingleton.FATAL, "broken transaction with avoided inconsistencies");

				throw new Exception("broken transaction with avoided inconsistencies");
			}
			else
			{
				LogSingleton.root.error(LogSingleton.FATAL, "broken transaction without inconsistency");

				throw new Exception("broken transaction without inconsistency");
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
		/**/	for(Map<String, AbstractDriver> transaction: s_pools.values())
		/**/	{
		/**/		for(AbstractDriver connection: transaction.values())
		/**/		{
		/**/			try
		/**/			{
		/**/				connection.rollbackAndRelease();
		/**/			}
		/**/			catch(Exception e)
		/**/			{
		/**/				LogSingleton.root.error(LogSingleton.FATAL, "broken transaction without inconsistency", e);
		/**/			}
		/**/		}
		/**/	}
		/**/
		/**/	/*---------------------------------------------------------*/
		/**/	/* CLEAR TRANSACTION POOL                                  */
		/**/	/*---------------------------------------------------------*/
		/**/
		/**/	s_pools.clear();
		/**/
		/**/	/*---------------------------------------------------------*/
		}
	}

	/*---------------------------------------------------------------------*/
}
