package jartoe.concurrency.executor;

import java.util.List;
import java.util.concurrent.Executor;

public interface ExtendedExecutor extends Executor {
	void execute(Runnable... commands);

	void execute(List<? extends Runnable> commands);

	void poller(Runnable poller);
}
