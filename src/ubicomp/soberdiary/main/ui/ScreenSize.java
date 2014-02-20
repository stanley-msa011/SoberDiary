package ubicomp.soberdiary.main.ui;

import ubicomp.soberdiary.main.App;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class ScreenSize {

	private static Point screen = null;

	public static int getScreenX(){
		if (screen != null)
			return screen.x;
		getSize();
		return screen.x;
	}
	
	public static Point getScreenSize(){
		if (screen != null)
			return screen;
		getSize();
		return screen;
	}
	
	@SuppressWarnings("deprecation")
	private static void getSize(){
		WindowManager wm = (WindowManager) App.context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		screen = new Point();
		
		if (Build.VERSION.SDK_INT < 13){
			screen.x = display.getWidth();
			screen.y = display.getHeight();
		}else{
			display.getSize(screen);
		}
		
		if (screen.x > screen.y){
			int tmp = screen.x;
			screen.x = screen.y;
			screen.y = tmp;
		}
		//screen.y = screen.y - screen.x * 209/1080;
	}
}
