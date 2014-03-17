package ubicomp.soberdiary.main.ui;

import ubicomp.soberdiary.main.App;
import android.graphics.Typeface;

public class Typefaces {
	private static Typeface wordTypeface, digitTypeface, wordTypefaceBold, digitTypefaceBold;

	public static Typeface getDigitTypeface() {
		if (digitTypeface == null)
			digitTypeface = Typeface.createFromAsset(App.getContext().getAssets(), "fonts/dinproregular.ttf");
		return digitTypeface;
	}

	public static Typeface getDigitTypefaceBold() {
		if (digitTypefaceBold == null)
			digitTypefaceBold = Typeface.createFromAsset(App.getContext().getAssets(), "fonts/dinpromedium.ttf");
		return digitTypefaceBold;
	}

	public static Typeface getWordTypeface() {
		if (wordTypeface == null)
			wordTypeface = Typeface.createFromAsset(App.getContext().getAssets(), "fonts/DFLiHeiStd-W3.otf");
		return wordTypeface;
	}

	public static Typeface getWordTypefaceBold() {
		if (wordTypefaceBold == null)
			wordTypefaceBold = Typeface.createFromAsset(App.getContext().getAssets(), "fonts/DFLiHeiStd-W5.otf");
		return wordTypefaceBold;
	}

	public static void initAll() {
		getDigitTypeface();
		getDigitTypefaceBold();
		getWordTypeface();
		getWordTypefaceBold();
	}
}
