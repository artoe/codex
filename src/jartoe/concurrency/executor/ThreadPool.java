package jartoe.concurrency.executor;

import jartoe.common.Strings;
import jartoe.concurrency.Threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public final class ThreadPool implements ExtendedExecutor {
	public static final int THREAD_PRIORITY = Thread.NORM_PRIORITY;

	private static final ExtendedExecutor INSTANCE = new ThreadPool();

	public static ExtendedExecutor getInstance() {
		return INSTANCE;
	}

	private final Safe safe = new Safe();

	private ThreadPool() {}

	public void execute(List<Runnable> commands) {
		Deque<Runnable> grouped = new LinkedList<>();
		List<Runnable> ungrouped = new ArrayList<>(commands.size());
		for (Runnable command : commands) {
			if (command instanceof GroupOperation)
				grouped.add(command);
			else
				ungrouped.add(command);
		}
		List<Runnable> list = new ArrayList<>(grouped.size() + 1);
		list.addAll(grouped);
		if (ungrouped.size() <= 1)
			list.addAll(ungrouped);
		else
			list.add(new GroupOperation(ungrouped));
		executeNoGrouping(list);
	}

	public void execute(Runnable command) {
		execute(Arrays.asList(command));
	}

	public void execute(Runnable... commands) {
		execute(Arrays.asList(commands));
	}

	public int getCoreCount() {
		return safe.getCoreCount();
	}

	void executeNoGrouping(List<Runnable> commands) {
		safe.add(commands);
	}

	private final class PoolThread extends Thread {
		private Runnable op;
		private boolean die;

		PoolThread(int id, Runnable op) {
			super(Strings.concat("PoolThread-", id));
			this.op = op;
			setDaemon(true);
			setPriority(THREAD_PRIORITY);
			start();
		}

		public synchronized void die() {
			die = true;
			notify();
		}

		public synchronized void wakeUp(Operation op) {
			notify();
			this.op = op;
		}

		@Override
		public void run() {
			for (;;) {
				Runnable op;
				synchronized (this) {
					op = this.op;
					this.op = null;
				}
				if (op != null) {
					try {
						op.run();
					} catch (Throwable ex) {
						System.err.println(Strings.concat("Operation ", op.getClass().getSimpleName(), " failed on ",
								ex));
					}
				}
				if (die)
					break;
				synchronized (this) {
					safe.idle(this);
				}
			}
		}
	}

	private final class Safe implements Runnable {
		private int _availableProcessors;
		private long _availableProcessorsCheck;
		private final List<Runnable> queue = new ArrayList<>();
		private int threadId;
		private final List<PoolThread> threads = new ArrayList<>(8);
		private final List<PoolThread> idles = new ArrayList<>(8);
		private final Thread handler;

		Safe() {
			updateCoreCount(System.nanoTime());
			handler = new Thread(this, "ThreadPool-handler");
			handler.setDaemon(true);
			handler.setPriority(THREAD_PRIORITY + 1);
			handler.start();
		}

		public void idle(PoolThread poolThread) {
			synchronized (this) {
				idles.add(poolThread);
			}
			Threads.wait(poolThread);
		}

		public void add(List<Runnable> commands) {
			synchronized (this) {
				int size = queue.size();
				for (Runnable command : commands)
					if (command != null)
						queue.add(command);
				if (size != queue.size())
					notify();
			}
		}

		public synchronized int getCoreCount() {
			long nanos = System.nanoTime();
			if (nanos - _availableProcessorsCheck >= 10000000000L)
				updateCoreCount(nanos);
			return Math.max(2, _availableProcessors);
		}

		public void run() {
			for (;;) {
				synchronized (this) {
					if (!queue.isEmpty()) {
						if (threads.isEmpty())
							newThread();
						while (threads.size() < getCoreCount() && queue.size() - threads.size() >= 2)
							newThread();
					}
				}
				//TODO
			}
		}

		private void newThread() {
			threads.add(new PoolThread(++threadId, queue.remove(0)));
		}

		private void updateCoreCount(long nanos) {
			_availableProcessors = Runtime.getRuntime().availableProcessors();
			_availableProcessorsCheck = nanos;
		}
	}
}
