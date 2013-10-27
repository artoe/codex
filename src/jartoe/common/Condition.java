package jartoe.common;

/**
 * @author Artoe
 */
public interface Condition<Type> {
	boolean test(Type o);
}
