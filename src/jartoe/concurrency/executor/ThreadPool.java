package jartoe.concurrency.executor;

import jartoe.common.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public final class ThreadPool implements ExtendedExecutor {
	private static final ExtendedExecutor INSTANCE = new ThreadPool();

	public static ExtendedExecutor getInstance() {
		return INSTANCE;
	}

	private final Safe safe = new Safe();

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

	void executeNoGrouping(List<Runnable> commands) {
		safe.add(commands);
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
			synchronized (this) {
				int count = queue.size();
				for (Runnable command : commands)
					if (command != null)
						queue.add(command);
				count = queue.size() - count;
				if (count > 0) {
					// TODO
				}
			}
		}

		public synchronized int getCoreCount() {
			long nanos = System.nanoTime();
			if (nanos - _availableProcessorsCheck >= 10000000000L)
				updateCoreCount(nanos);
			return Math.max(2, _availableProcessors);
		}

		private void updateCoreCount(long nanos) {
			_availableProcessors = Runtime.getRuntime().availableProcessors();
			_availableProcessorsCheck = nanos;
		}
	}
}
