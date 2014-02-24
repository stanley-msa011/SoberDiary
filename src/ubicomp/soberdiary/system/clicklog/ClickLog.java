package ubicomp.soberdiary.system.clicklog;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import ubicomp.soberdiary.data.file.MainStorage;
import ubicomp.soberdiary.system.check.DefaultCheck;
import ubicomp.soberdiary.system.check.LockCheck;
import android.util.Log;

public class ClickLog {

	public static void Log(long id){
		if(DefaultCheck.check()||LockCheck.check())
			return;
		
		long message = id;
		long timestamp = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		String date = sdf.format(timestamp);
		
		File mainStorage = MainStorage.getMainStorageDirectory();
		File dir = new File(mainStorage, "sequence_log");
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		File logFile =  new File(dir, date + ".txt");
		DataOutputStream ds = null; 
		try {
			ds = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(logFile,logFile.exists())));
			ds.writeLong(timestamp);
			ds.writeLong(message);
			ds.flush();
		} catch (Exception e) {
			Log.d("CLICKLOG","WRITE FAIL");
		} finally{
			try {
				if (ds!=null)
					ds.close();
			} catch (Exception e) {}
		}
		/*Intent intent = new Intent(App.context,ClickLoggerService.class);
		intent.putExtra(ClickLogId.LOG_MSG_ID, id);
		App.context.startService(intent);
		*/
	}
	
}
