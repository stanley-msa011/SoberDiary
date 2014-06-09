package ubicomp.soberdiary.data.file;

import java.io.File;

import ubicomp.soberdiary.main.App;
import android.os.Environment;

/**
 * This class is used for getting mainStorage path
 * 
 * @author Stanley Wang
 */
public class MainStorage {

	/**File to record main storage path*/
	private static File mainStorage = null;

	/**
	 * Get main storage directory path. If the path does not exist, make
	 * directory for it.
	 * 
	 * @return File directory path
	 */
	public static final File getMainStorageDirectory() {
		if (mainStorage == null) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				mainStorage = new File(Environment.getExternalStorageDirectory(), "soberDiary");
			else
				mainStorage = new File(App.getContext().getFilesDir(), "soberDiary");
		}
		if (!mainStorage.exists())
			mainStorage.mkdirs();

		return mainStorage;
	}
}
