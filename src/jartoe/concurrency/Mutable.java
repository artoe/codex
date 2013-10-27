package jartoe.concurrency;

/**
 * @author Artoe
 */
public final class Mutable<Type> {
	private Type value;

	public Mutable() {}

	public Mutable(Type initialValue) {
		this.value = initialValue;
	}

	public Type get() {
		return value;
	}

	public void set(Type value) {
		this.value = value;
	}
}
