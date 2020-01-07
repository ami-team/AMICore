package net.hep.ami.jdbc.pool;

import java.sql.*;
import java.util.*;

import net.hep.ami.*;
import net.hep.ami.jdbc.*;
import net.hep.ami.jdbc.driver.*;

import org.jetbrains.annotations.*;

public class TransactionPoolSingleton
{
	/*----------------------------------------------------------------------------------------------------------------*/

	private static final java.util.concurrent.atomic.AtomicLong s_lastId = new java.util.concurrent.atomic.AtomicLong(1);

	/*----------------------------------------------------------------------------------------------------------------*/

	private static final Map<Long, Map<String, AbstractDriver>> s_pools = new HashMap<>();

	/*----------------------------------------------------------------------------------------------------------------*/

	@Contract(pure = true)
	private TransactionPoolSingleton() {}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static long bookNewTransactionId()
	{
		return s_lastId.getAndIncrement();
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static AbstractDriver getConnection(@NotNull String catalog, @NotNull String AMIUser, @NotNull String timeZone, int flags, long transactionId) throws Exception
	{
		if(transactionId <= 0x000000000000
		   ||
		   transactionId >= s_lastId.get()
		 ) {
			throw new Exception("invalid transaction identifier (" + transactionId + " - " + s_lastId.get() + ")");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String key = CatalogSingleton.getKey(catalog);

		/*------------------------------------------------------------------------------------------------------------*/

		AbstractDriver result;

		Map<String, AbstractDriver> transaction;

		synchronized(TransactionPoolSingleton.class)
		{
		/**/		transaction = s_pools.get(transactionId);
		/**/
		/**/		if(transaction == null)
		/**/		{
		/**/			s_pools.put(transactionId, transaction = new HashMap<>());
		/**/
		/**/			transaction.put(key, result = CatalogSingleton.getConnection(catalog, AMIUser, timeZone, flags));
		/**/		}
		/**/		else
		/**/		{
		/**/			result = transaction.get(key);
		/**/
		/**/			if(result == null)
		/**/			{
		/**/				transaction.put(key, result = CatalogSingleton.getConnection(catalog, AMIUser, timeZone, flags));
		/**/			}
		/**/		}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static AbstractDriver getConnection(@Nullable String externalCatalog, @NotNull String internalCatalog,  @NotNull String jdbcUrl, @Nullable String user, @Nullable String pass, @NotNull String AMIUser, @NotNull String timeZone, int flags, long transactionId) throws Exception
	{
		if(transactionId <= 0x000000000000
		   ||
		   transactionId >= s_lastId.get()
		 ) {
			throw new Exception("invalid transaction identifier (" + transactionId + " - " + s_lastId.get() + ")");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String key = DriverSingleton.getKey(internalCatalog, jdbcUrl, user, pass);

		/*------------------------------------------------------------------------------------------------------------*/

		AbstractDriver result;

		Map<String, AbstractDriver> transaction;

		synchronized(TransactionPoolSingleton.class)
		{
		/**/		transaction = s_pools.get(transactionId);
		/**/
		/**/		if(transaction == null)
		/**/		{
		/**/			s_pools.put(transactionId, transaction = new HashMap<>());
		/**/
		/**/			transaction.put(key, result = DriverSingleton.getConnection(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags));
		/**/		}
		/**/		else
		/**/		{
		/**/			result = transaction.get(key);
		/**/
		/**/			if(result == null)
		/**/			{
		/**/				transaction.put(key, result = DriverSingleton.getConnection(externalCatalog, internalCatalog, jdbcUrl, user, pass, AMIUser, timeZone, flags));
		/**/			}
		/**/		}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void commitAndRelease(long transactionId) throws Exception
	{
		Map<String, AbstractDriver> transaction;

		/*------------------------------------------------------------------------------------------------------------*/
		/* REMOVE TRANSACTION FROM POOL                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		synchronized(TransactionPoolSingleton.class)
		{
		/**/		transaction = s_pools.remove(transactionId);
		}

		if(transaction == null)
		{
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* COMMIT AND RELEASE CONNECTIONS                                                                             */
		/*------------------------------------------------------------------------------------------------------------*/

		int flag;

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/

			for(AbstractDriver driver: transaction.values())
			{
				if(driver.getJdbcType() == DriverMetadata.Type.SQL)
				{
					try(Statement statement = driver.getConnection().createStatement())
					{
						statement.executeQuery(driver.patchSQL("SELECT 1"));
					}
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			flag = 0;

			for(AbstractDriver driver: transaction.values())
			{
				try { driver.commitAndRelease(); } catch(Exception e2) { flag = 2; }
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e1)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			flag = 1;

			for(AbstractDriver driver: transaction.values())
			{
				try { driver.rollbackAndRelease(); } catch(Exception e2) {/* IGNORE */}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void rollbackAndRelease(long transactionId) throws Exception
	{
		Map<String, AbstractDriver> transaction;

		/*------------------------------------------------------------------------------------------------------------*/
		/* REMOVE TRANSACTION FROM POOL                                                                               */
		/*------------------------------------------------------------------------------------------------------------*/

		synchronized(TransactionPoolSingleton.class)
		{
		/**/		transaction = s_pools.remove(transactionId);
		}

		if(transaction == null)
		{
			return;
		}

		/*------------------------------------------------------------------------------------------------------------*/
		/* ROLLBACK AND RELEASE CONNECTIONS                                                                           */
		/*------------------------------------------------------------------------------------------------------------*/

		int flag;

		try
		{
			/*--------------------------------------------------------------------------------------------------------*/

			for(AbstractDriver driver: transaction.values())
			{
				if(driver.getJdbcType() == DriverMetadata.Type.SQL)
				{
					try(Statement statement = driver.getConnection().createStatement())
					{
						statement.executeQuery(driver.patchSQL("SELECT 1"));
					}
				}
			}

			/*--------------------------------------------------------------------------------------------------------*/

			flag = 0;

			for(AbstractDriver driver: transaction.values())
			{
				try { driver.rollbackAndRelease(); } catch(Exception e2) { flag = 2; }
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}
		catch(Exception e1)
		{
			/*--------------------------------------------------------------------------------------------------------*/

			flag = 1;

			for(AbstractDriver driver: transaction.values())
			{
				try { driver.rollbackAndRelease(); } catch(Exception e2) {/* IGNORE */}
			}

			/*--------------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

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

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	public static void clear()
	{
		/*------------------------------------------------------------------------------------------------------------*/

		int cnt = 0;

		synchronized(TransactionPoolSingleton.class)
		{
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/		/* ROLLBACK AND RELEASE CONNECTIONS                                                               */
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/
		/**/		for(Map<String, AbstractDriver> transaction: s_pools.values())
		/**/		{
		/**/			for(AbstractDriver connection: transaction.values())
		/**/			{
		/**/				try
		/**/				{
		/**/					connection.rollbackAndRelease();
		/**/				}
		/**/				catch(Exception e)
		/**/				{
		/**/					cnt++;
		/**/				}
		/**/			}
		/**/		}
		/**/
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/		/* CLEAR TRANSACTION POOL                                                                         */
		/**/		/*------------------------------------------------------------------------------------------------*/
		/**/
		/**/		s_pools.clear();
		/**/
		/**/		/*------------------------------------------------------------------------------------------------*/
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(cnt > 0)
		{
			LogSingleton.root.error(LogSingleton.FATAL, "{} broken transaction(s) without inconsistency", cnt);
		}

		/*------------------------------------------------------------------------------------------------------------*/
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
