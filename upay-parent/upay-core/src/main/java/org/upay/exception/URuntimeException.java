package org.upay.exception;

import org.upay.util.StringUtil;

public class URuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public URuntimeException(Throwable e){
		super(e);
	}
	
	public URuntimeException(String message) {
		super(message);
	}
	
	public URuntimeException(String message, Object...params) {
		super(StringUtil.format(message, params));
	}
	
	public URuntimeException(String message, Throwable e) {
		super(message, e);
	}

	public URuntimeException(String message, Throwable e, Object...params) {
		super(StringUtil.format(message, params), e);
	}
}