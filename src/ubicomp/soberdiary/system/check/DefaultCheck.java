package ubicomp.soberdiary.system.check;

import ubicomp.soberdiary.system.config.PreferenceControl;

public class DefaultCheck {

	public static boolean check() {
		return PreferenceControl.defaultCheck();
	}

}
