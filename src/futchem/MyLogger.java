package futchem;

import java.lang.System.Logger;
import java.util.ResourceBundle;


public class MyLogger implements Logger {
	@Override
	public void log(Level level, String msg) {
		System.out.println("[" + level + "] " + msg);
	}

	@Override
	public String getName() { return "CustomLogger"; }

	@Override
	public boolean isLoggable(Level level) {
		if (level == Level.ERROR || level == Level.WARNING) {
			return true;
		}
		return false;
	}

	@Override
	public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
		if (thrown != null) {
			System.out.println("[" + level + "] " + msg + " - Exception: " + thrown.getMessage());
		}
		else {
			System.out.println("[" + level + "] " + msg);
		}

	}

	@Override
	public void log(Level level, ResourceBundle bundle, String format, Object... params) {
		String msg = String.format(format, params);
		System.out.println("[" + level + "] " + msg);

	}
}
