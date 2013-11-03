package jartoe.concurrency.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author Artoe
 */
public final class GroupOperation extends Operation {
	private static final int DEFAULT_PER_GROUP = 8;

	private final List<Runnable> commands;
	private final Runnable followUp = new Runnable() {
		public void run() {
			handleFollowUp();
		}
	};
	private int running;

	public GroupOperation(Executor executor, List<? extends Runnable> commands) {
		super(executor);
		this.commands = new ArrayList<>(commands.size());
		for (Runnable command : commands) {
			if (command != null)
				this.commands.add(command);
		}
	}

	public GroupOperation(Executor executor, Runnable... commands) {
		this(executor, Arrays.asList(commands));
	}

	public GroupOperation(List<? extends Runnable> commands) {
		this(null, commands);
	}

	public GroupOperation(Runnable... commands) {
		this(null, commands);
	}

	@Override
	protected void doOperation() {
		synchronized (this) {
			if (commands.isEmpty())
				return;
		}
		suspend();
		executeNextBatch();
	}

	void handleFollowUp() {
		boolean done, more = false;
		synchronized (this) {
			done = --running <= 0;
			if (done)
				more = !commands.isEmpty();

		}
		if (done) {
			if (more)
				executeNextBatch();
			else
				resume(null);
		}
	}

	private void executeNextBatch() {
		ThreadPool pool = null;
		int groupSize = DEFAULT_PER_GROUP;
		if (executor instanceof ThreadPool) {
			pool = (ThreadPool) executor;
			groupSize = pool.getCoreCount();
		}
		List<Runnable> list = prepareNextBatch(groupSize);
		List<Runnable> ops = new ArrayList<>(list.size());
		for (Runnable op : list)
			ops.add(follow(op));
		if (pool != null) {
			pool.executeNoGrouping(ops);
		} else if (executor instanceof ExtendedExecutor) {
			((ExtendedExecutor) executor).execute(ops);
		} else {
			for (Runnable op : ops)
				executor.execute(op);
		}
	}

	private Runnable follow(Runnable op) {
		Runnable wrap = op;
		if (op instanceof Operation) {
			((Operation) op).addFollowUp(followUp);
		} else
			wrap = new OperationWrapper(op);
		return wrap;
	}

	private synchronized List<Runnable> prepareNextBatch(int groupSize) {
		List<Runnable> ops = commands.size() > groupSize ? commands.subList(0, groupSize) : commands;
		List<Runnable> tmp = new ArrayList<>(ops);
		ops.clear();
		running = tmp.size();
		return tmp;
	}

	private final class OperationWrapper implements Runnable {
		private Runnable op;

		OperationWrapper(Runnable op) {
			this.op = op;
		}

		public void run() {
			try {
				op.run();
			} finally {
				handleFollowUp();
			}
		}
	}
}
