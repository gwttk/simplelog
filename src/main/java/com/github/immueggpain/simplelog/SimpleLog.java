package com.github.immueggpain.simplelog;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class SimpleLog {
	public static final int STDOUT = 1;
	public static final int STDERR = 2;

	/** Caption for labeling causative exception stack traces */
	private static final String CAUSE_CAPTION = "Caused by: ";

	/** Caption for labeling suppressed exception stack traces */
	private static final String SUPPRESSED_CAPTION = "Suppressed: ";

	/** TAB char '\t' converted to spaces */
	private static final String TAB = "    ";

	private static final Object mutex = new Object();

	private static ZoneId zoneId = ZoneId.systemDefault();
	private static DateTimeFormatter dtfmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'~'HH:mm:ss.SSSxxxxx'['VV']'");
	private static PrintWriter printer;
	private static DateTimeFormatter segfmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static String fileName;
	private static boolean outputToFile = true;
	private static int out;
	private static PrintWriter outWriter;

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			synchronized (mutex) {
				if (printer != null)
					printer.close();
			}
		}, "SimpleLog-shutdown"));

		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
				}
				synchronized (mutex) {
					if (printer != null)
						printer.flush();
				}
			}
		}, "SimpleLog-flush").start();
	}

	public static void println(int level, String line) {
		try {
			String lvl;
			switch (level) {
			case 0:
				lvl = "I";
				break;
			case 1:
				lvl = "W";
				break;
			case 2:
				lvl = "E";
				break;
			default:
				lvl = "" + level;
				break;
			}
			String datetime = ZonedDateTime.now(zoneId).format(dtfmt);
			String finalLine = lvl + " " + datetime + " " + line;

			if (outputToFile) {
				String segName = LocalDateTime.now(zoneId).format(segfmt);
				synchronized (mutex) {
					if (!segName.equals(fileName)) {
						if (printer != null)
							printer.close();
						printer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
								new FileOutputStream(segName + ".log", true), StandardCharsets.UTF_8)));
						fileName = segName;
					}

					printer.println(finalLine);
				}
			} else {
				if (out == STDOUT) {
					System.out.println(finalLine);
				} else if (out == STDERR) {
					System.err.println(finalLine);
				} else {

				}
			}

			if (outWriter != null) {
				outWriter.println(finalLine);
			}
		} catch (Throwable e) {
			// cause this is log, we can't do anything but print console
			e.printStackTrace();
		}
	}

	public static void println(int level, String line, Object... args) {
		println(level, String.format(line, args));
	}

	public static void println(String line, Object... args) {
		println(0, line, args);
	}

	public static void printex(Throwable e) {
		// Guard against malicious overrides of Throwable.equals by
		// using a Set with identity equality semantics.
		Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
		dejaVu.add(e);

		synchronized (mutex) {
			// Print our stack trace
			println(String.valueOf(e));
			StackTraceElement[] trace = e.getStackTrace();
			for (StackTraceElement traceElement : trace)
				println(TAB + "at " + traceElement);

			// Print suppressed exceptions, if any
			for (Throwable se : e.getSuppressed())
				printEnclosedStackTrace(se, trace, SUPPRESSED_CAPTION, TAB, dejaVu);

			// Print cause, if any
			Throwable ourCause = e.getCause();
			if (ourCause != null)
				printEnclosedStackTrace(ourCause, trace, CAUSE_CAPTION, "", dejaVu);
		}
	}

	private static void printEnclosedStackTrace(Throwable e, StackTraceElement[] enclosingTrace, String caption,
			String prefix, Set<Throwable> dejaVu) {
		assert Thread.holdsLock(mutex);
		if (dejaVu.contains(e)) {
			println(TAB + "[CIRCULAR REFERENCE:" + e + "]");
		} else {
			dejaVu.add(e);
			// Compute number of frames in common between this and enclosing trace
			StackTraceElement[] trace = e.getStackTrace();
			int m = trace.length - 1;
			int n = enclosingTrace.length - 1;
			while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
				m--;
				n--;
			}
			int framesInCommon = trace.length - 1 - m;

			// Print our stack trace
			println(prefix + caption + e);
			for (int i = 0; i <= m; i++)
				println(prefix + TAB + "at " + trace[i]);
			if (framesInCommon != 0)
				println(prefix + TAB + "... " + framesInCommon + " more");

			// Print suppressed exceptions, if any
			for (Throwable se : e.getSuppressed())
				printEnclosedStackTrace(se, trace, SUPPRESSED_CAPTION, prefix + TAB, dejaVu);

			// Print cause, if any
			Throwable ourCause = e.getCause();
			if (ourCause != null)
				printEnclosedStackTrace(ourCause, trace, CAUSE_CAPTION, prefix, dejaVu);
		}
	}

	public static ZoneId getZoneId() {
		return zoneId;
	}

	public static void setZoneId(ZoneId zoneId) {
		SimpleLog.zoneId = zoneId;
	}

	public static DateTimeFormatter getDtfmt() {
		return dtfmt;
	}

	public static void setDtfmt(DateTimeFormatter dtfmt) {
		SimpleLog.dtfmt = dtfmt;
	}

	public static PrintWriter getPrinter() {
		return printer;
	}

	public static void setPrinter(PrintWriter printer) {
		SimpleLog.printer = printer;
	}

	public static void setOutputFilePattern(String pattern) {
		SimpleLog.segfmt = DateTimeFormatter.ofPattern(pattern);
		outputToFile = true;
	}

	public static void setOutputFile(String filename) {
		SimpleLog.segfmt = DateTimeFormatter.ofPattern("'" + filename + "'");
		outputToFile = true;
	}

	public static void setOutputStd(int out) {
		SimpleLog.out = out;
		outputToFile = false;
	}

	public static void setOutputWriter(PrintWriter out) {
		SimpleLog.outWriter = out;
	}

}
