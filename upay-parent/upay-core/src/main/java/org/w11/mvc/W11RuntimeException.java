package org.w11.mvc;

/**
 * 运行时异常
 */
public class W11RuntimeException extends RuntimeException {
	public W11RuntimeException(String s) {
		super(s);
	}

	public W11RuntimeException(Throwable e) {
		super(e);
	}

	public W11RuntimeException(String s, Throwable e) {
		super(s, e);
	}
}