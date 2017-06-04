package sia.knights;

import java.io.PrintStream;

public class Minstrel {

	private PrintStream printStream;

	public Minstrel(PrintStream printStream) {
		this.printStream = printStream;
	}

	public void singBeforeQuest() {// 探险前调用
		printStream.println("探险前..............");
	}

	public void singAfterQuest() {// 探险后调用
		printStream.println("探险后..............");
	}
}
