package cz.silesnet.service;

public class SignedEmailGenerateException extends RuntimeException {

	private static final long serialVersionUID = 5500038266531890445L;

	public SignedEmailGenerateException() {
		super();
	}

	public SignedEmailGenerateException(String message) {
		super(message);
	}

	public SignedEmailGenerateException(String message, Throwable cause) {
		super(message, cause);
	}

	public SignedEmailGenerateException(Throwable cause) {
		super(cause);
	}
}
