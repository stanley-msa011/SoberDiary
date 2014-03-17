package ubicomp.soberdiary.data.file;

import java.io.File;

import ubicomp.soberdiary.main.App;
import android.os.Environment;

public class MainStorage {

	private static File mainStorage = null;
	
	public static final File getMainStorageDirectory(){
		if (mainStorage == null){
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				mainStorage = new File(Environment.getExternalStorageDirectory(), "soberDiary");
			else
				mainStorage = new File(App.getContext().getFilesDir(),"soberDiary");
		}
		return mainStorage;
	}
}
