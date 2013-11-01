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

	public int peek(byte[] to) {
		return peek(to, 0, to.length);
	}

	public int peek(byte[] to, int offset, int length) {
		return copy(to, offset, length, false);
	}

	public byte[] peekAll() {
		byte[] all = new byte[available()];
		peek(all);
		return all;
	}

	public int read() {
		return nextByte(true);
	}

	public int read(byte[] to) {
		return read(to, 0, to.length);
	}

	public int read(byte[] to, int offset, int length) {
		return copy(to, offset, length, true);
	}

	public byte[] readAll() {
		byte[] all = new byte[available()];
		read(all);
		return all;
	}

	public void skip(int bytes) {
		if (bytes > 0)
			inc(Math.min(bytes, available()));
	}

	private int copy(byte[] to, int offset, int length, boolean read) {
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
		return len;
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
				inc(1);
		} else if (offset < second.length) {
			val = toByte(second[offset]);
			if (read)
				inc(1);
		}
		return val;
	}

	private int toByte(int b) {
		return 0xff & b;
	}

	public int peekUnsignedByte() {
		return Bytes.unsignedByte(peek());
	}

	public long peekUnsignedInt() {
		byte[] data = new byte[4];
		peek(data);
		return Bytes.unsignedInt(data, 0);
	}

	public int peekUnsignedShort() {
		byte[] data = new byte[2];
		peek(data);
		return Bytes.unsignedShort(data, 0);
	}

	public int readUnsignedByte() {
		return Bytes.unsignedByte(read());
	}

	public int readUnsignedShort() {
		return Bytes.unsignedShort(read(), read());
	}

	public long readUnsignedInt() {
		return Bytes.unsignedInt(read(), read(), read(), read());
	}
}
