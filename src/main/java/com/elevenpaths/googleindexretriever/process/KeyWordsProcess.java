package com.elevenpaths.googleindexretriever.process;

import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.elevenpaths.googleindexretriever.Control;
import com.elevenpaths.googleindexretriever.GoogleSearch;
import com.elevenpaths.googleindexretriever.exceptions.EmptyQueryException;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class KeyWordsProcess.
 */
@SuppressWarnings("restriction")
public class KeyWordsProcess extends Observer implements Runnable {

	/** The thread name. */
	public static String THREAD_NAME = "keywords";

	/** The thread. */
	private Thread thread;

	/** The control. */
	private final Control control;

	/** The words found queue. */
	private final ArrayDeque<String> wordsFoundQueue = new ArrayDeque<String>();

	/** The words processed. */
	private final Set<String> wordsProcessed = new HashSet<String>();

	/** The start time. */
	private final long startTime;

	/** The query. */
	private final String query;

	/** The gs. */
	private final GoogleSearch gs;

	/** The word queue. */
	private String wordQueue = "";

	/** The first query. */
	private boolean firstQuery = true;

	/** The that. */
	private final KeyWordsProcess that;

	/** The message queue. */
	private final BlockingQueue<String> messageQueue;

	/**
	 * Instantiates a new key words process.
	 *
	 * @param control the control
	 * @param gs the gs
	 * @param query the query
	 */
	public KeyWordsProcess(final Control control, final GoogleSearch gs, final String query) {
		this.control = control;
		this.gs = gs;
		this.query = query;
		startTime = System.nanoTime();

		that = this;

		messageQueue = new ArrayBlockingQueue<String>(1);

		final LongProperty lastUpdate = new SimpleLongProperty();

		final long minUpdateInterval = 0; // nanoseconds. Set to higher number to slow output.

		final AnimationTimer timer = new AnimationTimer() {

			@Override
			public void handle(final long now) {
				if (now - lastUpdate.get() > minUpdateInterval) {
					final String message = messageQueue.poll();
					if (message != null) {

						try {
							gs.setQuery(message, that);
						} catch (final UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							control.stop();
							e.printStackTrace();

						} catch (final EmptyQueryException e) {
							control.stop();
							e.printStackTrace();

						}
					}
					lastUpdate.set(now);
				}
			}

		};

		timer.start();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {

			// First
			do {

				semaphore = false;

				control.setPathProgressBar(query);

				final String message = query;
				messageQueue.put(message);

				while (semaphore == false) {
					Thread.sleep(1000);
				}

				// Thread.sleep(5000);

				// Successful request
				wordsProcessed.add(wordQueue);
				final String time = control
						.calcHMS((int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

				// add time to window
				control.setElepased(time);

				// add new result if it is new
				control.addList(time, wordQueue, result);

				if (result == null) {
					result = "";
				}

				result = result.replaceAll("\\.\\.\\.|\\.|\\:|\\,", " ");

				final String[] parts = result.split(" ");

				for (final String a : parts) {

					if (!wordsProcessed.contains(a) && !wordsFoundQueue.contains(a) && !a.isEmpty()) {
						wordsFoundQueue.addFirst(a);
					}
				}
				firstQuery = false;// First query successful

			} while (firstQuery);

			do {
				try {
					if (wordsFoundQueue.size() > 0) {
						semaphore = false;

						wordQueue = wordsFoundQueue.pop();

						control.setPathProgressBar(query + " \"" + wordQueue + "\"");

						// gs.setQuery(query + " \"" + wordQueue + "\"", that);

						final String message = query + " \"" + wordQueue + "\"";
						messageQueue.put(message);

						while (semaphore == false) {
							Thread.sleep(1000);
						}

						// Thread.sleep(5000);

						// Successful request
						wordsProcessed.add(wordQueue);
						final String time = control.calcHMS(
								(int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

						// add time to window
						control.setElepased(time);

						if (result == null) {
							result = "";
						}

						// add new result if it is new
						control.addList(time, wordQueue, result);

						result = result.replaceAll("\\.\\.\\.|\\.|\\:|\\,", " ");

						final String[] parts = result.split(" ");

						for (final String a : parts) {

							if (!wordsProcessed.contains(a) && !wordsFoundQueue.contains(a) && !a.isEmpty()) {
								wordsFoundQueue.addFirst(a);
							}
						}
					}
				} catch (final InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} while (!stop && !wordsFoundQueue.isEmpty());

			// Starts KeyWords
			if (gs.isUseKeywords()) {
				wordsFoundQueue.addAll(gs.getKeywords());

				do {
					try {

						semaphore = false;
						wordQueue = wordsFoundQueue.pop();

						control.setPathProgressBar(query + " \"" + wordQueue + "\"");

						final String message = query + " \"" + wordQueue + "\"";
						messageQueue.put(message);

						// result = gs.getResults();
						while (semaphore == false) {
							Thread.sleep(1000);
						}

						// Successful request
						wordsProcessed.add(wordQueue);
						final String time = control.calcHMS(
								(int) TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

						// add time to window
						control.setElepased(time);

						// add new result if it is new
						control.addList(time, wordQueue, result);

						if (result == null) {
							result = "";
						}

						result = result.replaceAll("\\.\\.\\.|\\.|\\:|\\,", " ");

						final String[] parts = result.split(" ");

						for (final String a : parts) {

							if (!wordsProcessed.contains(a) && !wordsFoundQueue.contains(a) && !a.isEmpty()) {
								wordsFoundQueue.addFirst(a);
							}
						}

					} catch (final InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} while (!stop && !wordsFoundQueue.isEmpty());

			}

			control.stop();
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Sets the result query.
	 *
	 * @param result the new result query
	 */
	public void setResultQuery(final String result) {
		this.result = result;
	}

	/**
	 * Start.
	 */
	public void start() {
		// System.out.println("Starting " + threadName );
		if (thread == null) {
			thread = new Thread(this, THREAD_NAME);
			thread.start();
		}
	}

	/**
	 * Gets the thread.
	 *
	 * @return the thread
	 */
	public Thread getThread() {
		return thread;
	}

}