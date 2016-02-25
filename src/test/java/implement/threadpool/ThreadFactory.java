package implement.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A factory for creating Thread objects.
 * 
 * @author Paul Mai
 */
public class ThreadFactory {

	/**
	 * A factory for creating MyThread objects.
	 */
	private class MyThreadFactory implements java.util.concurrent.ThreadFactory {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setPriority(Thread.MAX_PRIORITY);
			return t;
		}

	}

	/** The Constant instance. */
	private static final ThreadFactory instance = new ThreadFactory();

	/** The thread pool. */
	private static ThreadPoolExecutor  pool;

	/**
	 * Gets the single instance of ThreadFactory.
	 * 
	 * @return single instance of ThreadFactory
	 */
	public final static ThreadFactory getInstance() {
		return ThreadFactory.instance;
	}

	/**
	 * Inits the pool.
	 *
	 * @param maxConnection the max connection
	 */
	public void connect(int maxConnection) {
		ThreadFactory.pool = (ThreadPoolExecutor) Executors.newCachedThreadPool(new MyThreadFactory());
		ThreadFactory.pool.setMaximumPoolSize(maxConnection);
	}

	/**
	 * Gets the pool.
	 *
	 * @return the pool
	 */
	public ThreadPoolExecutor getPool() {
		return ThreadFactory.pool;
	}

	/**
	 * Release pool.
	 */
	public void release() {
		ThreadFactory.pool.shutdown();
		int count = 0;
		while (!ThreadFactory.pool.isTerminated()) {
			count++;
			if (count == 30) {
				// Force shutdown after 30 seconds
				ThreadFactory.pool.shutdownNow();
			}
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {

			}
		}
		ThreadFactory.pool = null;
	}

	/**
	 * Get and the thread get from thread pool.
	 * 
	 * @param runnable the runnable
	 */
	public void start(Runnable runnable) {
		ThreadFactory.pool.execute(runnable);
	}
}
