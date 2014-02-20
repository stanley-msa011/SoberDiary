package ubicomp.soberdiary.main;

import ubicomp.soberdiary.system.check.DefaultCheck;
import ubicomp.soberdiary.system.uploader.DataUploader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class UploadService extends Service {

	
	public static void startUploadService(Context context){
		Intent intent = new Intent(context,UploadService.class);
		context.startService(intent);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags,int startId){
		super.onStartCommand(intent, flags, startId);
		
		if(DefaultCheck.check())
			return Service.START_REDELIVER_INTENT;
		
		DataUploader.upload();
		
		return Service.START_REDELIVER_INTENT;
	}
	
	
}
