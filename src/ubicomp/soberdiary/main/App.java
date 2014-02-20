package ubicomp.soberdiary.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application {
	public static Context context;
	public static SharedPreferences sp;
    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
