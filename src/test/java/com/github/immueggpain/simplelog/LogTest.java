package com.github.immueggpain.simplelog;

import static com.github.immueggpain.simplelog.SimpleLog.println;
import com.github.immueggpain.simplelog.SimpleLog;

import org.junit.jupiter.api.Test;

public class LogTest {

	@Test
	public void testLog() {
		SimpleLog.getPrinter();
		println(0, "a simple line");
		println(0, "fmt line %d %s", 233, "ready");
	}

}
