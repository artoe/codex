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

	@Test
	public void test016PeekUnsignedNumbers() {
		ByteArray a = new ByteArray(new byte[] { -1, 0x02, -1, 0x04 });
		Assert.assertEquals(0xff, a.peekUnsignedByte());
		Assert.assertEquals(0xff02, a.peekUnsignedShort());
		Assert.assertEquals("ff02ff04", Long.toHexString(a.peekUnsignedInt()));
		Assert.assertEquals(4, a.available());
	}

	@Test
	public void test017ReadUnsignedNumbers() {
		ByteArray a = new ByteArray(new byte[] { -1, -1, 124, -1, -1, 0, 66 });
		Assert.assertEquals(255, a.readUnsignedByte());
		Assert.assertEquals(256 * 255 + 124, a.readUnsignedShort());
		Assert.assertEquals(256L * (256L * (256L * 255L + 255L) + 0L) + 66L, a.readUnsignedInt());
		Assert.assertEquals(0, a.available());
	}

	@Test
	public void test018ReadSignedLong() {
		ByteArray a = new ByteArray();
		a.append(new byte[] { -1, -1 });
		a.append(new byte[] { 0x33, 0x44 });
		a.append(new byte[] { -128, 0x55 });
		a.append(new byte[] { 0x66, 0x77 });
		Assert.assertEquals("ffff334480556677", Long.toHexString(a.readSignedLong()));
		Assert.assertEquals(0, a.available());
	}

	@Test
	public void test019PeekSignedLong() {
		ByteArray a = new ByteArray();
		a.append(new byte[] { -1, -128 });
		a.append(new byte[] { -1, 0x00 });
		a.append(new byte[] { -128, 0x00 });
		a.append(new byte[] { 0x66, -1 });
		Assert.assertEquals("ff80ff00800066ff", Long.toHexString(a.peekSignedLong()));
		Assert.assertEquals(8, a.available());
	}

	@Test
	public void test020AppendByte() {
		ByteArray a = new ByteArray();
		a.appendByte(0xf1);
		Assert.assertEquals(1, a.available());
		Assert.assertEquals(0xf1, a.peekUnsignedByte());
	}

	@Test
	public void test021AppendShort() {
		ByteArray a = new ByteArray();
		a.appendShort(0xfafb);
		Assert.assertEquals(2, a.available());
		Assert.assertEquals(0xfafb, a.peekUnsignedShort());
	}

	@Test
	public void test022AppendInt() {
		ByteArray a = new ByteArray();
		a.appendInt(0xfafbfcfd);
		Assert.assertEquals(4, a.available());
		Assert.assertEquals(0xfafbfcfdL, a.peekUnsignedInt());
	}

	@Test
	public void test023AppendLong() {
		ByteArray a = new ByteArray();
		a.appendLong(0x0102030405060708L);
		Assert.assertEquals(8, a.available());
		Assert.assertEquals(0x0102030405060708L, a.peekSignedLong());
	}
}
