package jartoe.concurrency.executor;

import jartoe.common.Strings;
import jartoe.concurrency.Nanos;
import jartoe.concurrency.Threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Artoe
 */
public final class ThreadPool implements ExtendedExecutor {
	public static final int THREAD_PRIORITY = Thread.NORM_PRIORITY;

	private static final ExtendedExecutor INSTANCE = new ThreadPool();

	public static ExtendedExecutor getInstance() {
		return INSTANCE;
	}

	private final Safe safe = new Safe();

	private ThreadPool() {}

	void _overrideCoreCount(int coreCount) {
		safe.setOverrideCoreCount(coreCount);
	}

	public void execute(List<? extends Runnable> commands) {
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

	public void poller(Runnable poller) {
		execute(new Poller(poller));
	}

	public int getCoreCount() {
		return safe.getCoreCount();
	}

	void executeNoGrouping(List<Runnable> commands) {
		safe.add(commands);
	}

	private final class PoolThread extends Thread {
		private boolean die;
		private Runnable op;
		private Safe safe;

		PoolThread(Safe safe, String name, Runnable op) {
			super(name);
			this.safe = safe;
			this.op = op;
			setDaemon(true);
			setPriority(THREAD_PRIORITY);
			start();
		}

		public synchronized void die() {
			die = true;
			notify();
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
				safe.idle(this);
			}
		}

		public synchronized void wakeUp(Runnable op) {
			assignOp(op);
			notify();
		}

		public void assignOp(Runnable op) {
			this.op = op;
		}
	}

	private final class Safe implements Runnable {
		private int _availableProcessors;
		private long _availableProcessorsCheck;
		private int _override_coreCount;
		private boolean added;
		private final Thread handler;
		private final Deque<PoolThread> idles = new LinkedList<>();
		private final Deque<Runnable> queue = new LinkedList<>();
		private final Deque<Runnable> pollers = new LinkedList<>();
		private long tenSecondsInNanos = Nanos.secondsNs(10);
		private int threadId;
		private final List<PoolThread> threads = new ArrayList<>(8);

		Safe() {
			updateCoreCount(System.nanoTime());
			handler = new Thread(this, "ThreadPool-handler");
			handler.setDaemon(true);
			handler.setPriority(THREAD_PRIORITY + 1);
			handler.start();
		}

		public synchronized void setOverrideCoreCount(int coreCount) {
			_override_coreCount = coreCount;
		}

		public synchronized void add(List<Runnable> commands) {
			boolean added = false;
			for (Runnable command : commands) {
				if (command != null) {
					added = true;
					if (command instanceof Poller)
						pollers.add(command);
					else
						queue.add(command);
				}
			}
			if (added) {
				this.added = true;
				notify();
			}
		}

		public synchronized int getCoreCount() {
			int cores;
			if (_override_coreCount > 0) {
				cores = _override_coreCount;
			} else {
				long nanos = System.nanoTime();
				if (nanos - _availableProcessorsCheck >= tenSecondsInNanos)
					updateCoreCount(nanos);
				cores = _availableProcessors;
			}
			return Math.max(2, cores);
		}

		public void idle(PoolThread poolThread) {
			synchronized (poolThread) {
				synchronized (this) {
					if (!queue.isEmpty()) {
						poolThread.assignOp(queue.pollFirst());
						return;
					}
					idles.add(poolThread);
				}
				Threads.wait(poolThread);
			}
		}

		public void run() {
			Nanos n = new Nanos();
			for (;;) {
				synchronized (this) {
					if (added) {
						n.reset();
						added = false;
						while (!pollers.isEmpty())
							newThread(pollers.pollFirst());
					}
					if (!queue.isEmpty()) {
						if (threads.isEmpty())
							newThread();
						while (!queue.isEmpty() && !idles.isEmpty())
							idles.pollFirst().wakeUp(queue.pollFirst());
						while (threads.size() < getCoreCount() && queue.size() - threads.size() >= 2)
							newThread();
					}
					n.checkpoint();
					if (n.asNanos() > tenSecondsInNanos) {
						n.reset();
						if (!idles.isEmpty())
							idles.pollFirst().die();
					}
					Threads.wait(this, threads.isEmpty() ? 0 : tenSecondsInNanos);
				}
			}
		}

		private void newThread(Runnable command) {
			String name;
			if (command instanceof Poller)
				name = "PoolThread-Poller-";
			else
				name = "PoolThread-";
			threads.add(new PoolThread(this, Strings.concat(name, ++threadId), command));
		}

		private void newThread() {
			newThread(queue.pollFirst());
		}

		private void updateCoreCount(long nanos) {
			_availableProcessors = Runtime.getRuntime().availableProcessors();
			_availableProcessorsCheck = nanos;
		}
	}

	private final class Poller implements Runnable {
		private final Runnable poller;

		Poller(Runnable poller) {
			this.poller = poller;
		}

		public void run() {
			poller.run();
		}
	}
}
