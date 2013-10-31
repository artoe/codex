package jartoe.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class BytesTest {
	@Test
	public void testReadSignedByte() {
		Assert.assertEquals(-120, Bytes.signedByte(new byte[] { 0, 0, -120 }, 2));
	}

	@Test
	public void testReadSignedInt() {
		Assert.assertEquals(-2000000000, Bytes.signedInt(new byte[] { 0, -120, -54, 108, 0 }, 1));
	}

	@Test
	public void testReadSignedLong() {
		Assert.assertEquals(-9000000000000000000L,
				Bytes.signedLong(new byte[] { -125, 25, -109, -81, 29, 124, 0, 0 }, 0));
	}

	@Test
	public void testReadSignedShort() {
		Assert.assertEquals(-30000, Bytes.signedShort(new byte[] { -118, -48, 0, 0 }, 0));
	}

	@Test
	public void testReadUnsignedByte() {
		Assert.assertEquals(256 - 55, Bytes.unsignedByte(new byte[] { -55 }, 0));
	}

	@Test
	public void testReadUnsignedInt() {
		Assert.assertEquals(1L + 2L * Integer.MAX_VALUE, Bytes.unsignedInt(new byte[] { -1, -1, -1, -1 }, 0));
	}

	@Test
	public void testReadUnsignedShort() {
		Assert.assertEquals(60000, Bytes.unsignedShort(new byte[] { -22, 96 }, 0));
	}

	@Test
	public void testWriteByte() {
		byte[] data = new byte[10];
		Assert.assertEquals(1, Bytes.writeByte(data, 5, 77));
		Assert.assertEquals(77, Bytes.signedByte(data, 5));
	}

	@Test
	public void testWriteInt() {
		byte[] data = new byte[10];
		Assert.assertEquals(4, Bytes.writeInt(data, 4, -2000000000));
		Assert.assertEquals(-2000000000, Bytes.signedInt(data, 4));
	}

	@Test
	public void testWriteLong() {
		byte[] data = new byte[20];
		Assert.assertEquals(8, Bytes.writeLong(data, 7, -9000000000000000000L));
		Assert.assertEquals(-9000000000000000000L, Bytes.signedLong(data, 7));

	}

	@Test
	public void testWriteShort() {
		byte[] data = new byte[10];
		Assert.assertEquals(2, Bytes.writeShort(data, 4, -30000));
		Assert.assertEquals(-30000, Bytes.signedShort(data, 4));
	}
}
