package ubicomp.soberdiary.main.ui;

import android.graphics.Point;

public class TextSize {

	private static Point screen = null;
	
	private static int getScreenX(){
		if (screen == null)
			screen = ScreenSize.getScreenSize();
		return screen.x;
	}

	
	private static int normalTextSize = 0;
	static public int normalTextSize(){
		normalTextSize =  normalTextSize > 0 ?normalTextSize: getScreenX() * 21/480;
		return normalTextSize;
	}
	
	private static int smallTitleTextSize = 0;
	static public int smallTitleTextSize(){
		smallTitleTextSize = smallTitleTextSize > 0 ? smallTitleTextSize: getScreenX()* 24/480;
		return smallTitleTextSize;
	}
	
	private static int smallTextSize = 0;
	static public int smallTextSize(){
		smallTextSize =  smallTextSize > 0 ? smallTextSize:getScreenX() * 16/480;
		return smallTextSize;
	}
	
	private static int mTextSize = 0;
	static public int mTextSize(){
		mTextSize =  mTextSize > 0 ? mTextSize:getScreenX() * 18/480;
		return mTextSize;
	}
	
	private static int titleSize = 0;
	static public int titleSize(){
		titleSize = titleSize > 0 ? titleSize:getScreenX() * 27/480;
		return titleSize;
	}
	
	private static int largeTitleSize = 0;
	static public int largeTitleSize(){
		largeTitleSize = largeTitleSize > 0 ? largeTitleSize:getScreenX() * 32/480;
		return largeTitleSize;
	}
	
	private static int slargeTitleSize = 0;
	static public int slargeTitleSize(){
		slargeTitleSize = slargeTitleSize > 0 ? slargeTitleSize:getScreenX() * 48/480;
		return slargeTitleSize;
	}
	
	private static int mlargeTitleSize = 0;
	static public int mlargeTitleSize(){
		mlargeTitleSize = mlargeTitleSize > 0 ? mlargeTitleSize:getScreenX() * 58/480;
		return mlargeTitleSize;
	}
	
	private static int xlargeTextSize = 0;
	static public int xlargeTextSize(){
		xlargeTextSize = xlargeTextSize > 0 ? xlargeTextSize:getScreenX() * 64/480;
		return xlargeTextSize;
	}
	
	private static int xxlargeTextSize = 0;
	static public int xxlargeTextSize(){
		xxlargeTextSize = xxlargeTextSize > 0 ? xxlargeTextSize:getScreenX() * 75/480;
		return xxlargeTextSize;
	}
	
	private static int largeTextSize = 0;
	static public int largeTextSize(){
		largeTextSize = largeTextSize > 0 ? largeTextSize:getScreenX() * 42/480;
		return largeTextSize;
	}
	
}
