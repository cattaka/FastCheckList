package net.cattaka.android.fastchecklist.exception;

public class DbException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DbException() {
		super();
	}

	public DbException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public DbException(String detailMessage) {
		super(detailMessage);
	}

	public DbException(Throwable throwable) {
		super(throwable);
	}
}
