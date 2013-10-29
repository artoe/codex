package jartoe.util;

import java.lang.ref.WeakReference;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class ByteArrayTest {
	@Test
	public void testAppend() {
		ByteArray a = new ByteArray();
		Assert.assertEquals(0, a.available());
		a.append(new byte[] { 1, 2, 3 });
		Assert.assertEquals(3, a.available());
	}

	@Test
	public void testAppendNothing() {
		ByteArray a = new ByteArray();
		a.append(new byte[] { 1, 2, 3 });
		Assert.assertEquals(3, a.available());
		a.append(new byte[0]);
		Assert.assertEquals(3, a.available());
		a.append(null);
		Assert.assertEquals(3, a.available());
	}

	@Test
	public void testAppendUponAppend() {
		ByteArray a = new ByteArray();
		a.append(new byte[] { 1, 2, 3 });
		a.append(new byte[] { 4, 5 });
		Assert.assertEquals(5, a.available());
		a.append(new byte[] { 6, 7, 8, 9, 10 });
		Assert.assertEquals(10, a.available());
	}

	@Test
	public void testInit() {
		ByteArray a = new ByteArray();
		Assert.assertEquals(0, a.available());
	}

	@Test
	public void testInitWithByteArray() {
		ByteArray a = new ByteArray(new byte[] { 1, 2, 3, 4 });
		Assert.assertEquals(4, a.available());
	}

	@Test
	public void testReadEmpty() {
		ByteArray a = new ByteArray();
		Assert.assertEquals(-1, a.read());
	}

	@Test
	public void testReadingPastAnArrayDropsItsReference() {
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
	public void testReadWithAppendedByteArray() {
		ByteArray a = new ByteArray(new byte[] { 1, 2 });
		a.append(new byte[] { 3 });
		a.append(new byte[] { 4, 5 });
		Assert.assertEquals(1, a.read());
		Assert.assertEquals(2, a.read());
		Assert.assertEquals(3, a.read());
		Assert.assertEquals(4, a.read());
		Assert.assertEquals(5, a.read());
		Assert.assertEquals(-1, a.read());
	}

	@Test
	public void testReadWithAppendedData() {
		ByteArray a = new ByteArray(new byte[] { 1, 2 });
		a.append(new byte[] { 3 });
		Assert.assertEquals(1, a.read());
		Assert.assertEquals(2, a.read());
		Assert.assertEquals(3, a.read());
		Assert.assertEquals(-1, a.read());
	}

	@Test
	public void testReadWithData() {
		ByteArray a = new ByteArray(new byte[] { 1, 2, 3 });
		Assert.assertEquals(1, a.read());
		Assert.assertEquals(2, a.read());
		Assert.assertEquals(3, a.read());
		Assert.assertEquals(-1, a.read());
	}
}
