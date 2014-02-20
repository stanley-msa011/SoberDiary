package ubicomp.soberdiary.main.ui;

import ubicomp.soberdiary.main.R;
import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialogControl {
	
	static ProgressDialog dialog = null;

	public static void show(Context context,int type){
		
		if (dialog == null){
			dialog = new ProgressDialog(context);
		}
		else if (dialog.getContext() == null ||  !dialog.getContext().equals(context)){
			try{
				dialog.cancel();
			}catch (Exception e){}
			dialog = new ProgressDialog(context);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
		dialog.setCancelable(false);
		if (!dialog.isShowing()){
			try{
				dialog.show();
				if (type == 0)
					dialog.setContentView(R.layout.loading_box_theme);
				else if (type == 1)
					dialog.setContentView(R.layout.upload_box_dialog);
			}catch(Exception e){}
		}
	}
	
	public static void dismiss(){
		if (dialog == null)
			return;
		dialog.cancel();
		if (dialog.isShowing())
			dialog.dismiss();
	}
	
	public static void show(Context context){
		show(context, 0);
	}
}
