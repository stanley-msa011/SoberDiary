package ubicomp.soberdiary.system.clicklog;

import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.ClickLoggerService;
import android.content.Intent;

public class ClickLog {

	public static void Log(long id){
		Intent intent = new Intent(App.context,ClickLoggerService.class);
		intent.putExtra(ClickLogId.LOG_MSG_ID, id);
		App.context.startService(intent);
	}
	
}
