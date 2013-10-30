package jartoe.util;

import java.lang.ref.WeakReference;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class ByteArrayTest {
	@Test
	public void test001AvailableEmpty() {
		ByteArray a = new ByteArray();
		Assert.assertEquals(0, a.available());
	}

	@Test
	public void test002AvailableArray() {
		ByteArray a = new ByteArray(new byte[] { 1, 2, 3, 4 });
		Assert.assertEquals(4, a.available());
	}

	@Test
	public void test003Append() {
		ByteArray a = new ByteArray();
		a.append(new byte[] { 1, 2, 3 });
		Assert.assertEquals(3, a.available());
	}

	@Test
	public void test004AppendMultiple() {
		ByteArray a = new ByteArray(new byte[] { 1, 2, 3 });
		a.append(new byte[] { 4, 5 });
		a.append(new byte[] { 6, 7, 8 });
		Assert.assertEquals(8, a.available());
	}

	@Test
	public void test005ReadEmpty() {
		ByteArray a = new ByteArray();
		Assert.assertEquals(-1, a.read());
		Assert.assertEquals(0, a.available());
	}

	@Test
	public void test006ReadByte() {
		ByteArray a = new ByteArray(new byte[] { 1, 2, 3 });
		Assert.assertEquals(1, a.read());
		Assert.assertEquals(2, a.available());
		Assert.assertEquals(2, a.read());
		Assert.assertEquals(1, a.available());
		Assert.assertEquals(3, a.read());
		Assert.assertEquals(0, a.available());
		Assert.assertEquals(-1, a.read());
	}

	@Test
	public void test007ReadByteArray() {
		ByteArray a = new ByteArray(new byte[] { 1, 2 });
		a.append(new byte[] { 3, 4 });
		a.append(new byte[] { 5 });
		Assert.assertEquals(1, a.read());
		Assert.assertEquals(4, a.available());
		Assert.assertEquals(2, a.read());
		Assert.assertEquals(3, a.available());
		Assert.assertEquals(3, a.read());
		Assert.assertEquals(2, a.available());
		Assert.assertEquals(4, a.read());
		Assert.assertEquals(1, a.available());
		Assert.assertEquals(5, a.read());
		Assert.assertEquals(0, a.available());
	}

	@Test
	public void test008ReadMaxByte() {
		ByteArray a = new ByteArray(new byte[] { -1 });
		Assert.assertEquals(255, a.read());
	}

	@Test
	public void test009ReadingPastAnArrayDropsItsReference() {
		byte[] array = new byte[] { 1, 2, 3 };
		ByteArray a = new ByteArray(array);
		WeakReference<byte[]> ref1 = new WeakReference<>(array);
		array = new byte[] { 4 };
		a.append(array);
		WeakReference<byte[]> ref2 = new WeakReference<>(array);
		array = new byte[] { 5, 6 };
		a.append(array);
		WeakReference<byte[]> ref3 = new WeakReference<>(array);
		array = null;
		System.gc();
		Assert.assertNotNull(ref1.get());
		Assert.assertNotNull(ref2.get());
		Assert.assertNotNull(ref3.get());
		Assert.assertEquals(1, a.read());
		Assert.assertEquals(2, a.read());
		System.gc();
		Assert.assertNotNull(ref1.get());
		Assert.assertNotNull(ref2.get());
		Assert.assertNotNull(ref3.get());
		Assert.assertEquals(3, a.read());
		System.gc();
		Assert.assertNull(ref1.get());
		Assert.assertNotNull(ref2.get());
		Assert.assertNotNull(ref3.get());
		Assert.assertEquals(4, a.read());
		System.gc();
		Assert.assertNull(ref2.get());
		Assert.assertNotNull(ref3.get());
		Assert.assertEquals(5, a.read());
		Assert.assertEquals(6, a.read());
		System.gc();
		Assert.assertNull(ref3.get());
	}

	@Test
	public void test010PeekEmpty() {
		ByteArray a = new ByteArray();
		Assert.assertEquals(-1, a.peek());
	}

	@Test
	public void test011PeekByte() {
		ByteArray a = new ByteArray(new byte[] { 1, 2, 3 });
		Assert.assertEquals(1, a.peek());
		Assert.assertEquals(1, a.peek());
		Assert.assertEquals(1, a.read());
		Assert.assertEquals(2, a.read());
		Assert.assertEquals(3, a.peek());
	}

	@Test
	public void test012PeekByteArray() {
		ByteArray a = new ByteArray(new byte[] { 1 });
		a.append(new byte[] { 2, 3 });
		a.append(new byte[] { 4 });
		Assert.assertEquals(1, a.peek());
		Assert.assertEquals(1, a.peek());
		Assert.assertEquals(1, a.read());
		Assert.assertEquals(2, a.read());
		Assert.assertEquals(3, a.peek());
		Assert.assertEquals(3, a.read());
		Assert.assertEquals(4, a.peek());
	}

	@Test
	public void test013ReadAll() {
		ByteArray a = new ByteArray(new byte[] { 1 });
		a.append(new byte[] { 2, 3 });
		a.append(new byte[] { 4 });
		Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4 }, a.readAll());
		Assert.assertEquals(0, a.available());
		Assert.assertEquals(-1, a.read());
	}

	@Test
	public void test014PeekAll() {
		ByteArray a = new ByteArray(new byte[] { 1 });
		a.append(new byte[] { 2, 3 });
		a.append(new byte[] { 4 });
		Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4 }, a.peekAll());
		Assert.assertEquals(4, a.available());
		Assert.assertEquals(1, a.read());
	}

	@Test
	public void test015Skip() {
		ByteArray a = new ByteArray(new byte[] { 1, 2, 3 });
		a.append(new byte[] { 4, 5, 6 });
		a.append(new byte[] { 7, 8, 9 });
		Assert.assertEquals(9, a.available());
		a.skip(8);
		Assert.assertEquals(1, a.available());
		Assert.assertEquals(9, a.peek());
	}
}
