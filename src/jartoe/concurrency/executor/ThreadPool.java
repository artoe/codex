package jartoe.concurrency.executor;

import jartoe.common.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public final class ThreadPool implements ExtendedExecutor {
	private static final ExtendedExecutor INSTANCE = new ThreadPool();

	private final Safe safe = new Safe();

	public void execute(List<Runnable> commands) {
		safe.add(commands);
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

	private final class PoolThread extends Thread {
		PoolThread(int id) {
			super(Strings.concat("PoolThread-", id));
			setDaemon(true);
			setPriority(NORM_PRIORITY);
			start();
		}

		@Override
		public void run() {
			// TODO
		}
	}

	private final class Safe {
		private int _availableProcessors;
		private long _availableProcessorsCheck;
		private final List<Runnable> queue = new ArrayList<>();

		Safe() {
			updateCoreCount(System.nanoTime());
		}

		public void add(List<Runnable> commands) {
			Deque<Runnable> grouped = new LinkedList<>();
			List<Runnable> ungrouped = new ArrayList<>(commands.size());
			for (Runnable command : commands) {
				if (command instanceof GroupOperation)
					grouped.add(command);
				else
					ungrouped.add(command);
			}
			synchronized (this) {
				int count = queue.size();
				queue.addAll(grouped);
				if (ungrouped.size() <= 1)
					queue.addAll(ungrouped);
				else
					queue.add(new GroupOperation(ungrouped));
				count = queue.size() - count;
				if (count > 0) {
					// TODO
				}
			}
		}

		private void updateCoreCount(long nanos) {
			_availableProcessors = Runtime.getRuntime().availableProcessors();
			_availableProcessorsCheck = nanos;
		}

		public synchronized int getCoreCount() {
			long nanos = System.nanoTime();
			if (nanos - _availableProcessorsCheck >= 10000000000L)
				updateCoreCount(nanos);
			return Math.max(2, _availableProcessors);
		}
	}

	public static ExtendedExecutor getInstance() {
		return INSTANCE;
	}
}
