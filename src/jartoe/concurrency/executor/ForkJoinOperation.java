package jartoe.concurrency.executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author Artoe
 */
public abstract class ForkJoinOperation extends Operation {
	private final List<Runnable> operations = new ArrayList<>(0);

	public ForkJoinOperation(Executor executor) {
		super(executor);
	}

	public ForkJoinOperation() {
		this(null);
	}

	@Override
	protected final void doOperation() {
		init();
		final List<Runnable> forks = new ArrayList<>(fork());
		if (!forks.isEmpty()) {
			suspend();
			execute(forks);
		}
	}

	protected abstract List<? extends Runnable> fork();

	protected void init() {}

	/**
	 * Joining the results of the forked operations.
	 */
	protected abstract void join(List<? extends Runnable> operations);

	void handleJoining() {
		List<? extends Runnable> forks;
		synchronized (this) {
			forks = new ArrayList<>(operations);
			operations.clear();
		}
		Throwable error = null;
		boolean refork = true;
		try {
			join(forks);
		} catch (Throwable ex) {
			error = ex;
			refork = false;
		}
		forks = Collections.emptyList();
		if (refork)
			forks = fork();
		if (forks.isEmpty()) {
			resume(error);
		} else {
			execute(forks);
		}
	}

	private void execute(List<? extends Runnable> forks) {
		synchronized (this) {
			operations.clear();
			operations.addAll(forks);
		}
		GroupOperation o = new GroupOperation(executor, forks);
		o.addFollowUp(new Operation() {
			@Override
			protected void doOperation() {
				handleJoining();
			}
		});
	}
}
