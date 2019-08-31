package com.elevenpaths.googleindexretriever.exceptions;

import com.elevenpaths.googleindexretriever.App;

// TODO: Auto-generated Javadoc
/**
 * The Class EmptyQueryException.
 */
public class EmptyQueryException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5915254022559767738L;

	/**
	 * Instantiates a new empty query exception.
	 */
	public EmptyQueryException() {
		super(App.bundle.getString("emptyQueryException"));
	}

	/**
	 * Instantiates a new empty query exception.
	 *
	 * @param message the message
	 */
	public EmptyQueryException(final String message) {
		super(message);
	}

}