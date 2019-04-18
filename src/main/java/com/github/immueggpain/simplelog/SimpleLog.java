package com.github.immueggpain.simplelog;

import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleLog {

	private static ZoneId zoneId = ZoneId.systemDefault();
	private static DateTimeFormatter dtfmt = DateTimeFormatter.ISO_ZONED_DATE_TIME;
	private static PrintWriter printer = new PrintWriter(System.out, true);

	public static void println(String line) {
		String datetime = ZonedDateTime.now(zoneId).format(dtfmt);
		printer.println(datetime + " " + line);
	}

	public static void println(String line, Object... args) {
		println(String.format(line, args));
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

}
