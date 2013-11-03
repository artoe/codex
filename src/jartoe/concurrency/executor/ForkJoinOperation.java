package jartoe.concurrency.executor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Artoe
 */
public abstract class ForkJoinOperation extends Operation {
	private final List<Runnable> operations = new ArrayList<>(0);
	private int operationCount;

	public ForkJoinOperation() {}

	@Override
	protected final void doOperation() {
		init();
		final List<Runnable> forks = new ArrayList<>(fork());
		suspend();
		execute(forks);
	}

	protected abstract List<? extends Runnable> fork();

	protected void init() {}

	/**
	 * Joining the results of the forked operations.
	 * 
	 * @return <code>true</code> for re-forking
	 */
	protected abstract boolean join(List<Runnable> operations);

	void handleJoining() {
		List<Runnable> forks;
		synchronized (this) {
			forks = operations;
		}
		Throwable error = null;
		boolean refork;
		try {
			refork = join(forks);
		} catch (Throwable ex) {
			error = ex;
			refork = false;
		}
		if (refork) {
			forks = new ArrayList<>(fork());
			execute(forks);
		} else {
			resume(error);
		}
	}

	private void execute(List<Runnable> forks) {
		int count = forks.size();
		synchronized (this) {
			operations.clear();
			operations.addAll(forks);
			operationCount = count;
		}
		if (count == 0)
			handleJoining();

		GroupOperation o = new GroupOperation(forks);
		o.addFollowUp(new Operation() {
			@Override
			protected void doOperation() {
				handleJoining();
			}
		});
	}
}
