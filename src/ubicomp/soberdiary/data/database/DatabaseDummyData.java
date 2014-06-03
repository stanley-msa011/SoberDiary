package ubicomp.soberdiary.data.database;

import ubicomp.soberdiary.main.PreSettingActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class DatabaseDummyData extends AsyncTask<Void, Void, Void> {

	private Context context;

	public DatabaseDummyData(Context context) {
		this.context = context;
	}

	private ProgressDialog dialog = null;

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage("Please Wait...");
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		DummyData.insertDummyData();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (dialog != null)
			dialog.dismiss();
		Intent intent = new Intent(context, PreSettingActivity.class);
		context.startActivity(intent);
	}

}
