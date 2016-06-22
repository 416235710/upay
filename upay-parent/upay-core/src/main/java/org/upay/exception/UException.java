package org.upay.exception;

import org.upay.util.StringUtil;

public class UException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public UException(String message) {
		super(message);
	}
	
	public UException(String message, Object...params) {
		super(StringUtil.format(message, params));
	}
	
	public UException(Throwable e){
		super(e);
	}

	public UException(String message, Throwable e) {
		super(message, e);
	}
	
	public UException(String message, Throwable e, Object...params) {
		super(StringUtil.format(message, params), e);
	}
}