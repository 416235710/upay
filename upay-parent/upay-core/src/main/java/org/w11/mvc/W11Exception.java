package org.w11.mvc;


/**
 * 异常
 */
public class W11Exception extends Exception {
	
	public W11Exception(String s) {
		super(s);
	}

	public W11Exception(Throwable e) {
		super(e);
	}

	public W11Exception(String s, Throwable e) {
		super(s, e);
	}
}