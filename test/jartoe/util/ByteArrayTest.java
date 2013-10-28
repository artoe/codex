package jartoe.util;

import org.junit.Assert;
import org.junit.Test;

public final class ByteArrayTest {
	@Test
	public void testInitEmptyArray() {
		ByteArray a = new ByteArray();
		Assert.assertEquals(0, a.available());
		Assert.assertArrayEquals(new byte[0], a.peekAll());
		Assert.assertEquals(-1, a.read());
	}

	@Test
	public void testInitWith2ByteArrays() {
		ByteArray a = new ByteArray(new byte[] { 1, 2, 3 }, new byte[] { 4, 5, 6 });
		Assert.assertEquals(6, a.available());
		Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6 }, a.peekAll());
	}

	@Test
	public void testNestingArrays() {
		ByteArray a = new ByteArray(new ByteArray(new ByteArray(new byte[] { 1 }, new byte[] { 2, 3 }), new byte[] { 4,
				5, 6, 7 }), new byte[] { 8, 9, 10, 11, 12, 13, 14, 15, 16 });
		Assert.assertEquals(16, a.available());
		Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }, a.peekAll());
	}
}
