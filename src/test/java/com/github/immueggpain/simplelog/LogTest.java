package com.github.immueggpain.simplelog;

import static com.github.immueggpain.simplelog.SimpleLog.*;

import org.junit.jupiter.api.Test;

public class LogTest {

	@Test
	public void testLog() {
		// level line
		println(0, "a simple line");
		// level fmt line
		println(0, "fmt line %d %s", 233, "ready");
		// fmt line
		println("fmt line %d %s", 244, "default");
		// print exception stack trace
		printex(new Throwable("excp msg", new Throwable("cause excp")));
		// print complex exception
		Throwable cause = new Throwable("cause");
		Throwable sup1 = new Throwable("sup1");
		Throwable sup2 = new Throwable("sup2");
		Throwable outter = new Throwable("outter");
		outter.initCause(cause);
		cause.addSuppressed(sup1);
		outter.addSuppressed(sup1);
		outter.addSuppressed(sup2);
		outter.addSuppressed(cause);
		printex(outter);
	}

}
