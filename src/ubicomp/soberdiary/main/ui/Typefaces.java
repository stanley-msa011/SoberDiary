package ubicomp.soberdiary.main.ui;

import ubicomp.soberdiary.main.App;
import android.graphics.Typeface;

public class Typefaces {
	private static Typeface wordTypeface,digitTypeface,wordTypefaceBold,digitTypefaceBold;
	
	static public Typeface getDigitTypeface(){
		if (digitTypeface == null)
			digitTypeface =Typeface.createFromAsset(App.context.getAssets(), "fonts/dinproregular.ttf");
		return digitTypeface;
	}
	
	static public Typeface getDigitTypefaceBold(){
		if (digitTypefaceBold == null)
			digitTypefaceBold =Typeface.createFromAsset(App.context.getAssets(),  "fonts/dinpromedium.ttf");
		return digitTypefaceBold;
	}
	
	static public Typeface getWordTypeface(){
		if (wordTypeface == null)
			wordTypeface =Typeface.createFromAsset(App.context.getAssets(), "fonts/DFLiHeiStd-W3.otf");
		return wordTypeface;
	}
	
	static public Typeface getWordTypefaceBold(){
		if (wordTypefaceBold == null)
			wordTypefaceBold =Typeface.createFromAsset(App.context.getAssets(), "fonts/DFLiHeiStd-W5.otf");
		return wordTypefaceBold;
	}
	
	static public void initAll(){
		getDigitTypeface();
		getDigitTypefaceBold();
		getWordTypeface();
		getWordTypefaceBold();
	}
}
