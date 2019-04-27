package com.github.immueggpain.simplelog;

import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleLog {

	private static ZoneId zoneId = ZoneId.systemDefault();
	private static DateTimeFormatter dtfmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'~'HH:mm:ss.SSSxxxxx'['VV']'");
	private static PrintWriter printer = new PrintWriter(System.out, true);

	public static void println(int level, String line) {
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
		printer.println(lvl + " " + datetime + " " + line);
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

}
