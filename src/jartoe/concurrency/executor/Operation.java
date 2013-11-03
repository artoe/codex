package jartoe.concurrency.executor;

import jartoe.concurrency.Threads;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

public abstract class Operation implements Runnable {
	private boolean done;
	private Throwable error;
	private final Set<Runnable> followUps = new HashSet<>();
	private boolean running;
	private boolean suspended;
	protected final Executor executor;

	protected Operation() {
		this(null);
	}

	protected Operation(Executor executor) {
		this.executor = executor == null ? ThreadPool.getInstance() : executor;
	}

	public final void addFollowUp(Runnable operation) {
		boolean executeNow;
		synchronized (this) {
			if (done) {
				executeNow = true;
			} else {
				executeNow = false;
				followUps.add(operation);
			}
		}
		if (executeNow)
			execute(operation);
	}

	public final void execute() {
		executor.execute(this);
	}

	public synchronized final Throwable getError() {
		return error;
	}

	public synchronized final boolean hasFailed() {
		return done && error != null;
	}

	public synchronized final boolean isDone() {
		return done;
	}

	public synchronized final boolean isRunning() {
		return running;
	}

	public synchronized final boolean isSuccessful() {
		return done && error == null;
	}

	public final void run() {
		Throwable error = null;
		if (startExecution()) {
			try {
				doOperation();
			} catch (Throwable ex) {
				error = ex;
			}
			endExecution(error);
		}
	}

	protected abstract void doOperation() throws Throwable;

	protected final void resume(Throwable error) {
		synchronized (this) {
			suspended = false;
		}
		endExecution(error);
	}

	protected synchronized final void suspend() {
		suspended = true;
	}

	private void endExecution(Throwable error) {
		Set<Runnable> followUps = null;
		synchronized (this) {
			if (!suspended && running) {
				followUps = new HashSet<>(this.followUps);
				this.done = true;
				this.error = trim(error);
				this.running = false;
				this.followUps.clear();
				notifyAll();
			}
		}
		// do this outside synchronization
		if (followUps != null) {
			for (Runnable followUp : followUps)
				execute(followUp);
		}
	}

	private void execute(Runnable followUp) {
		if (followUp instanceof Operation)
			((Operation) followUp).execute();
		else
			executor.execute(followUp);
	}

	public synchronized final void join() {
		while (!done)
			Threads.wait(this);
	}

	private synchronized boolean startExecution() {
		if (running || done)
			return false;
		running = true;
		return true;
	}

	private Throwable trim(Throwable ex) {
		Throwable trimmed = ex;
		while (trimmed instanceof InvocationTargetException && trimmed.getCause() != trimmed
				&& trimmed.getCause() != null)
			trimmed = trimmed.getCause();
		return trimmed;
	}
}
