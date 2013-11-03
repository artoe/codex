package jartoe.concurrency.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Artoe
 */
public final class GroupOperation extends Operation {
	private static final int MAX_PER_GROUP = 8;

	private final List<Runnable> operations;

	public GroupOperation(List<Runnable> commands) {
		this.operations = new ArrayList<>(commands.size());
		for (Runnable operation : commands) {
			if (operation != null)
				this.operations.add(operation);
		}
	}

	public GroupOperation(Runnable... commands) {
		this(Arrays.asList(commands));
	}

	@Override
	protected List<? extends Runnable> fork() {
		synchronized (this) {
			List<Runnable> ops = operations.size() > GroupOperation.MAX_PER_GROUP ? operations.subList(0,
					GroupOperation.MAX_PER_GROUP) : operations;
			List<Runnable> tmp = new ArrayList<>(ops.size());
			tmp.addAll(ops);
			ops.clear();
			return tmp;
		}
	}

	@Override
	protected boolean join(List<Runnable> operations) {
		return !this.operations.isEmpty();
	}
}
