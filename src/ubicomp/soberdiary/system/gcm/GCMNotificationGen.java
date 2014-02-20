package ubicomp.soberdiary.system.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.GCMRead;
import ubicomp.soberdiary.main.GCMAlertActivity;
import ubicomp.soberdiary.main.R;


public class GCMNotificationGen {

	private static final int BASE_ID = 100;
	
	public static void gen(Context context){
		DatabaseControl db = new DatabaseControl(); 
		GCMRead[] gcms = db.getNotReadGCMRead();
		if (gcms == null)
			return;
		for (int i=0;i<gcms.length;++i){
			generateNotification(context,gcms[i],i+BASE_ID);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, GCMRead data,int id) {
		
		String title = context.getString(R.string.app_name)+context.getString(R.string.gcm_title);
		String msgText = data.message;
		
		Intent sIntent = new Intent(context,GCMAlertActivity.class);
		sIntent.putExtra("gcm_ts",data.tv.timestamp);
		
		PendingIntent pIntent = PendingIntent.getActivity(context, id,sIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		Notification notification;
		
		if (Build.VERSION.SDK_INT>=11){
		
			Notification.Builder notificationBuilder = new Notification.Builder(context);
		
			notificationBuilder.setContentTitle(title);
			notificationBuilder.setContentText(msgText);
			notificationBuilder.setSmallIcon(R.drawable.icon);
			notificationBuilder.setContentIntent(pIntent);

			if (Build.VERSION.SDK_INT <16)
				notification = notificationBuilder.getNotification();
			else
				notification = notificationBuilder.build();
		
			if (id == BASE_ID)
				notification.defaults = Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
			
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(id);
			notificationManager.notify(id,notification);
		}else{
			notification = new Notification();
			if (id == BASE_ID)
				notification.defaults = Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
			notification.setLatestEventInfo(context, title, msgText , pIntent);
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(id);
			notificationManager.notify(id,notification);
		}

	}
}
