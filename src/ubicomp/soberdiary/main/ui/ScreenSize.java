package ubicomp.soberdiary.main.ui;

import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class ScreenSize {

	private static Point screen = null;
	
	private static int title_height=(int) App.context.getResources().getDimension(R.dimen.title_bar_height);
	private static int drawable_height = (int) App.context.getResources().getDimension(R.dimen.bar_height);
	
	public static Point getScreenSize(){
		if (screen == null)
			getSize();
		return screen;
	}
	
	public static int getMinBars(){
		if (screen ==null)
			getSize();
		return (screen.y - title_height)/drawable_height;
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
	}
}
