package implement.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A factory for creating Thread objects.
 * 
 * @author Paul Mai
 */
public class ThreadFactory {
	
	/** The Constant instance. */
	private static final ThreadFactory instance = new ThreadFactory();
	
	/** The thread pool. */
	private static ThreadPoolExecutor pool;
	/**
	 * Gets the single instance of ThreadFactory.
	 * 
	 * @return single instance of ThreadFactory
	 */
	public final static ThreadFactory getInstance() {
		return instance;
	}
	
	/**
	 * Inits the pool.
	 * 
	 * @param poolSize the pool size
	 */
	public void connect(int maxConnection) {
		pool = (ThreadPoolExecutor) Executors.newCachedThreadPool(new MyThreadFactory());
		pool.setMaximumPoolSize(maxConnection);
	}
	
	/**
	 * Release pool.
	 */
	public void release() {
		pool.shutdown();
		int count = 0;
		while (!pool.isTerminated()) {
			count++;
			if (count == 30) {
				// Force shutdown after 30 seconds
				pool.shutdownNow();
			}
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				
			}
		}
		pool = null;
	}
	
	/**
	 * Get and the thread get from thread pool.
	 * 
	 * @param runnable the runnable
	 */
	public void start(Runnable runnable) {
		pool.execute(runnable);
	}
	
	/**
	 * A factory for creating MyThread objects.
	 */
	private class MyThreadFactory implements java.util.concurrent.ThreadFactory {
		
		/* (non-Javadoc)
		 * 
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable) */
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setPriority(Thread.MAX_PRIORITY);
			return t;
		}
		
	}
	
	/**
	 * Gets the pool.
	 *
	 * @return the pool
	 */
	public ThreadPoolExecutor getPool() {
		return pool;
	}
}
