package jartoe.concurrency;

/**
 * @author Artoe
 */
public final class MutableBoolean {
	private boolean value;

	public MutableBoolean() {}

	public MutableBoolean(boolean initialValue) {
		this.value = initialValue;
	}

	public boolean is() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}
}
