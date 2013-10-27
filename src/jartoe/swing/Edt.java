package jartoe.swing;

import jartoe.concurrency.Mutable;
import jartoe.concurrency.MutableBoolean;
import jartoe.concurrency.Threads;

import java.awt.EventQueue;

public final class Edt {
	public void runOnEdt(Runnable r) {
		if (EventQueue.isDispatchThread())
			r.run();
		else
			EventQueue.invokeLater(r);
	}

	public void runOnEdtAndWait(final Runnable r) {
		if (EventQueue.isDispatchThread())
			r.run();
		else {
			final MutableBoolean lock = new MutableBoolean();
			final Mutable<Throwable> error = new Mutable<>();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						r.run();
					} catch (RuntimeException | Error ex) {
						error.set(ex);
					} finally {
						synchronized (lock) {
							lock.set(true);
							lock.notify();
						}
					}
				}
			});
			Threads.waitUntil(lock, true);
			if (error.get() instanceof RuntimeException)
				throw (RuntimeException) error.get();
			if (error.get() instanceof Error)
				throw (Error) error.get();
		}
	}
}
