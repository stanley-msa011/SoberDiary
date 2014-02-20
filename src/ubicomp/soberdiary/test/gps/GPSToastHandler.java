package ubicomp.soberdiary.test.gps;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.CustomToastSmall;
import android.os.Handler;
import android.os.Message;

public class GPSToastHandler extends Handler {
	
	public void handleMessage(Message msg){
		CustomToastSmall.generateToast(R.string.open_gps);
	}
}
