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

public class SimpleLog {

	private static ZoneId zoneId = ZoneId.systemDefault();
	private static DateTimeFormatter dtfmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'~'HH:mm:ss.SSSxxxxx'['VV']'");
	private static PrintWriter printer;
	private static DateTimeFormatter segfmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static String fileName;

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (printer != null)
				printer.close();
		}, "SimpleLog-shutdown"));
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

			String segName = LocalDateTime.now(zoneId).format(segfmt);
			if (!segName.equals(fileName)) {
				if (printer != null)
					printer.close();
				printer = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(segName + ".log", true), StandardCharsets.UTF_8)));
				fileName = segName;
			}

			printer.println(lvl + " " + datetime + " " + line);
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

	public static DateTimeFormatter getSegfmt() {
		return segfmt;
	}

	public static void setSegfmt(DateTimeFormatter segfmt) {
		SimpleLog.segfmt = segfmt;
	}

}
