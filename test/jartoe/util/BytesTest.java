package jartoe.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class BytesTest {
	@Test
	public void testSignedInt() {
		Assert.assertEquals(-2000000000, Bytes.signedInt(new byte[] { -120, -54, 108, 0 }, 0));
	}

	@Test
	public void testSignedLong() {
		Assert.assertEquals(-9000000000000000000L,
				Bytes.signedLong(new byte[] { -125, 25, -109, -81, 29, 124, 0, 0 }, 0));
	}

	@Test
	public void testSignedShort() {
		Assert.assertEquals(-30000, Bytes.signedShort(new byte[] { -118, -48 }, 0));
	}

	@Test
	public void testUnsignedByte() {
		Assert.assertEquals(256 - 55, Bytes.unsignedByte(new byte[] { -55 }, 0));
	}

	@Test
	public void testUnsignedInt() {
		Assert.assertEquals(1L + 2L * Integer.MAX_VALUE, Bytes.unsignedInt(new byte[] { -1, -1, -1, -1 }, 0));
	}

	@Test
	public void testUnsignedShort() {
		Assert.assertEquals(60000, Bytes.unsignedShort(new byte[] { -22, 96 }, 0));
	}
}
