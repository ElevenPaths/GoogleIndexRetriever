package com.elevenpaths.googleindexretriever.exceptions;

import com.elevenpaths.googleindexretriever.App;

// TODO: Auto-generated Javadoc
/**
 * The Class ManyResultsException.
 */
public class ManyResultsException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6324159658956710638L;

	/**
	 * Instantiates a new many results exception.
	 */
	public ManyResultsException() {
		super(App.bundle.getString("manyResults"));
	}

	/**
	 * Instantiates a new many results exception.
	 *
	 * @param message the message
	 */
	public ManyResultsException(final String message) {
		super(message);
	}

}