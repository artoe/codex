package jartoe.util.timer;

import jartoe.concurrency.Threads;
import jartoe.concurrency.executor.ThreadPool;

import org.junit.Test;

/**
 * @author Artoe
 */
public final class TimerTest {
	@Test
	public void test001Timer() {
		Timer t = new Timer(ThreadPool.getInstance(), 5, 1000L) {
			public void run() {
				System.out.println(Thread.currentThread().getName() + ": timer sprung");
			}
		};
		t.start();
		Threads.sleep(4000L);
		t.restart();
		Threads.sleep(4000L);
		t.stop();
		Threads.sleep(4000L);
	}
}
