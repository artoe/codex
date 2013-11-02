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

	public void appendByte(int b) {
		byte[] data = new byte[1];
		Bytes.writeByte(data, 0, b);
		append(data);
	}

	public void appendInt(int i) {
		byte[] data = new byte[4];
		Bytes.writeInt(data, 0, i);
		append(data);
	}

	public void appendLong(long l) {
		byte[] data = new byte[8];
		Bytes.writeLong(data, 0, l);
		append(data);
	}

	public void appendShort(int s) {
		byte[] data = new byte[2];
		Bytes.writeShort(data, 0, s);
		append(data);
	}

	public void appendString(String str) {
		// encoding?
		append(str.getBytes());
	}

	public int available() {
		return firstLength + second.length - offset;
	}

	public int indexOf(byte b) {
		for (int i = 0; i < available(); i++)
			if (b == peek(i))
				return i;
		return -1;
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

	public int peek(int offset) {
		if (this.offset + offset < firstLength) {
			if (first instanceof ByteArray)
				return peek(this.offset + offset);
			return Bytes.unsignedByte(((byte[]) first)[this.offset + offset]);
		}
		if (this.offset + offset < second.length)
			return Bytes.unsignedByte(second[this.offset + offset]);
		return -1;
	}

	public byte[] peekAll() {
		byte[] all = new byte[available()];
		peek(all);
		return all;
	}

	public long peekSignedLong() {
		byte[] data = new byte[8];
		peek(data);
		return Bytes.signedLong(data, 0);
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

	public long readSignedLong() {
		return Bytes.signedLong(read(), read(), read(), read(), read(), read(), read(), read());
	}

	public int readUnsignedByte() {
		return Bytes.unsignedByte(read());
	}

	public long readUnsignedInt() {
		return Bytes.unsignedInt(read(), read(), read(), read());
	}

	public int readUnsignedShort() {
		return Bytes.unsignedShort(read(), read());
	}

	public void skip(int bytes) {
		if (bytes > 0)
			inc(Math.min(bytes, available()));
	}

	@Override
	public String toString() {
		return new String(peekAll());
	}

	public String toString(int offset) {
		return toString(offset, available() - offset);
	}

	public String toString(int offset, int length) {
		if (length <= 0)
			return "";
		byte[] data = new byte[offset + length];
		peek(data, 0, offset + length);
		return new String(data, offset, length);
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
}
