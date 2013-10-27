package jartoe.common;

public final class Equals {
	public static boolean eq(Object o1, Object o2) {
		return eq(o1, o2, null);
	}

	public static <Type> boolean eq(Type o1, Type o2, Condition<Type> empty) {
		Condition<Type> _empty = empty;
		if (_empty == null) {
			_empty = new Condition<Type>() {
				public boolean test(Type o) {
					return false;
				}
			};
		}
		if (o1 == null || _empty.test(o1))
			return o2 == null || _empty.test(o2);
		if (o2 == null || _empty.test(o2))
			return false;
		return o1.equals(o2);
	}

	// no instantiation
	private Equals() {}
}
