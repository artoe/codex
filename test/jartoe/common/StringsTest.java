package jartoe.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class StringsTest {
	@Test
	public void testConcat() {
		Assert.assertEquals("123abcnull", Strings.concat(123, "abc", null));
	}

	@Test
	public void testConcatBuilder() {
		assertEquals("concat: Abcnull123", Strings.concat(new StringBuilder("concat: "), "Abc", null, 123));
	}

	@Test
	public void testEnsureMinimumDigits() {
		Assert.assertEquals("22", Strings.ensureMinimumDigits(22, 0));
		Assert.assertEquals("0000000999", Strings.ensureMinimumDigits(999, 10));
		Assert.assertEquals("-000000022", Strings.ensureMinimumDigits(-22, 10));
		Assert.assertEquals("-000009223372036854775808", Strings.ensureMinimumDigits(Long.MIN_VALUE, 25));
	}

	@Test
	public void testEnsureMinimumDigitsBuilder() {
		assertEquals("the number is 22", Strings.ensureMinimumDigits(new StringBuilder("the number is "), 22, 0));
		assertEquals("padded with zeroes: 0000000999",
				Strings.ensureMinimumDigits(new StringBuilder("padded with zeroes: "), 999, 10));
		assertEquals("it's negative: -000000022",
				Strings.ensureMinimumDigits(new StringBuilder("it's negative: "), -22, 10));
	}

	private void assertEquals(String expected, StringBuilder actual) {
		Assert.assertEquals(expected, actual.toString());
	}
}
