package ubicomp.soberdiary.main;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import ubicomp.soberdiary.data.file.MainStorage;
import ubicomp.soberdiary.system.check.DefaultCheck;
import ubicomp.soberdiary.system.check.LockCheck;
import ubicomp.soberdiary.system.clicklog.ClickLogId;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class ClickLoggerService extends Service {

	private static final String TAG = "CLICKLOGGER_SERVICE";
	
	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		
		if(DefaultCheck.check()||LockCheck.check())
			return Service.START_REDELIVER_INTENT;
		
		long message = intent.getLongExtra(ClickLogId.LOG_MSG_ID, -1);
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
			Log.d(TAG,"WRITE FAIL");
		} finally{
			try {
				if (ds!=null)
					ds.close();
			} catch (Exception e) {}
		}
		stopSelf();
		return Service.START_REDELIVER_INTENT;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
