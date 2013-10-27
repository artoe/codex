package jartoe.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class EqualsTest {
	@Test
	public void testEmptyCondition() {
		String obj = "";
		String nil = null;
		assertEquals(false, obj, nil, null);
		assertEquals(true, obj, nil, new Condition<String>() {
			public boolean test(String o) {
				return o.isEmpty();
			}
		});
	}

	@Test
	public void testEquals() {
		String obj = "a";
		String similar = "a";
		String diff = "b";
		String nil = null;
		assertEquals(true, obj, obj);
		assertEquals(true, obj, similar);
		assertEquals(false, obj, diff);
		assertEquals(false, obj, nil);
		assertEquals(true, nil, nil);
	}

	private void assertEquals(boolean expected, Object o1, Object o2) {
		Assert.assertEquals(expected, Equals.eq(o1, o2));
		Assert.assertEquals(expected, Equals.eq(o2, o1));
	}

	private <Type> void assertEquals(boolean expected, Type o1, Type o2, Condition<Type> empty) {
		Assert.assertEquals(expected, Equals.eq(o1, o2, empty));
		Assert.assertEquals(expected, Equals.eq(o2, o1, empty));
	}
}
