package factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A factory for creating Thread objects.
 * 
 * @author Paul Mai
 */
public class ThreadFactory {
	
	/** The Constant instance. */
	private static final ThreadFactory instance = new ThreadFactory();
	
	/** The thread pool. */
	private static ExecutorService pool;
	
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
		pool = Executors.newFixedThreadPool(maxConnection, new MyThreadFactory());
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
			t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
		
	}
}
