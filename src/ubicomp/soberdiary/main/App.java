package ubicomp.soberdiary.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application {
	private static Context context;
	private static SharedPreferences sp;
    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }
	public static Context getContext() {
		return context;
	}
	public static SharedPreferences getSp() {
		return sp;
	}
}
