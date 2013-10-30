package jartoe.util;

/**
 * @author Artoe
 */
public final class ByteArray {
	private static final byte[] EMPTY = new byte[0];

	private Object first = EMPTY;
	private int firstLength;
	private int offset;
	private byte[] second = EMPTY;

	public ByteArray() {}

	public ByteArray(byte[] array) {
		if (array != null)
			second = array;
	}

	private ByteArray(Object first, byte[] second) {
		this.first = first;
		this.second = second;
		if (first instanceof ByteArray) {
			this.firstLength = ((ByteArray) first).available();
		} else {
			this.firstLength = ((byte[]) first).length;
		}
	}

	public void append(byte[] array) {
		if (firstLength == 0) {
			first = second;
			firstLength = second.length;
			second = EMPTY;
		}
		if (second.length > 0) {
			ByteArray merge = new ByteArray(first, second);
			first = merge;
			firstLength = merge.available();
			offset = 0;
		}
		second = array;
	}

	public int available() {
		return firstLength + second.length - offset;
	}

	public int peek() {
		return nextByte(false);
	}

	public byte[] peek(byte[] to) {
		return peek(to, 0, to.length);
	}

	public byte[] peek(byte[] to, int offset, int length) {
		return copy(to, offset, length, false);
	}

	public byte[] peekAll() {
		return peek(new byte[available()]);
	}

	public int read() {
		return nextByte(true);
	}

	public byte[] read(byte[] to) {
		return read(to, 0, to.length);
	}

	public byte[] read(byte[] to, int offset, int length) {
		return copy(to, offset, length, true);
	}

	public byte[] readAll() {
		byte[] all = new byte[available()];
		return read(all);
	}

	private byte[] copy(byte[] to, int offset, int length, boolean read) {
		int len = Math.min(length, available());
		int firstBytes = 0;
		if (len > 0 && firstLength > 0) {
			firstBytes = Math.min(len, firstLength);
			if (first instanceof ByteArray)
				((ByteArray) first).copy(to, offset, firstBytes, read);
			else
				System.arraycopy(first, this.offset, to, offset, firstBytes);
		}
		if (len - firstBytes > 0)
			System.arraycopy(second, firstLength > 0 ? 0 : this.offset, to, offset + firstBytes, len - firstBytes);
		if (read)
			inc(len);
		return to;
	}

	private int fromFirst(boolean read) {
		if (first instanceof ByteArray) {
			ByteArray array = (ByteArray) first;
			if (read)
				return array.read();
			return array.peek();
		}
		return toByte(((byte[]) first)[offset]);
	}

	private void inc() {
		inc(1);
	}

	private void inc(int by) {
		for (int i = by; --i >= 0;) {
			if (offset < firstLength) {
				if (++offset >= firstLength) {
					offset = 0;
					first = EMPTY;
					firstLength = 0;
				}
			} else if (offset < second.length) {
				if (++offset == second.length) {
					offset = 0;
					second = EMPTY;
				}
			} else
				break;
		}
	}

	private int nextByte(boolean read) {
		int val = -1;
		if (offset < firstLength) {
			val = fromFirst(read);
			if (read)
				inc();
		} else if (offset < second.length) {
			val = toByte(second[offset]);
			if (read)
				inc();
		}
		return val;
	}

	private int toByte(int b) {
		return 0xff & b;
	}
}
