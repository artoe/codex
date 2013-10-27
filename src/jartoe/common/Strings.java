package jartoe.common;

/**
 * @author Artoe
 */
public final class Strings {
	public static String concat(Object... objects) {
		return concat(new StringBuilder(50), objects).toString();
	}

	public static StringBuilder concat(StringBuilder builder, Object... objects) {
		for (Object object : objects)
			builder.append(object);
		return builder;
	}

	public static String ensureMinimumDigits(long number, int digits) {
		return ensureMinimumDigits(new StringBuilder(digits), number, digits).toString();
	}

	public static StringBuilder ensureMinimumDigits(StringBuilder b, long number, int digits) {
		String text = Long.toString(number);
		int length = b.length();
		if (text.startsWith("-")) {
			text = text.substring(1);
			b.append('-');
		}
		length += digits - text.length();
		while (b.length() < length)
			b.append('0');
		return b.append(text);
	}
}
