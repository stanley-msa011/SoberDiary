package ubicomp.soberdiary.main;

import ubicomp.soberdiary.system.check.DefaultCheck;
import ubicomp.soberdiary.system.check.LockCheck;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "ALARM_RECEIVER";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(DefaultCheck.check() || LockCheck.check())
			return;
		
		if (intent.getAction()=="") return;
		if (intent.getAction().equals("Regular_notification")){
			Log.d(TAG,"Regular");
			Intent a_intent = new Intent(context,AlarmService.class);
			context.startService(a_intent);
		} else if (intent.getAction().equals("Regular_check")){
			Log.d(TAG,"Regular Check");
			Intent a_intent = new Intent(context,UploadService.class);
			context.startService(a_intent);
		}
	}

}
