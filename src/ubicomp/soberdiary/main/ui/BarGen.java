package ubicomp.soberdiary.main.ui;

import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BarGen {
	private static final LayoutInflater inflater = (LayoutInflater) App.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	private static Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();
	private static Typeface wordTypeface = Typefaces.getWordTypeface();
	
	public static View createTextView(int textStr) {
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_text_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		return layout;
	}

	public static View createTextView(String textStr) {

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_text_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		return layout;
	}
	
	public static View createQuoteQuestionView(String textStr){
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_text_area_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_text);
		String quoteBlank = App.context.getString(R.string.quote_blank);
		int firstIdx = textStr.indexOf(quoteBlank);
		int lastIdx = textStr.lastIndexOf(quoteBlank);
		Spannable spannable=new SpannableString(textStr);
		spannable.setSpan(new CustomTypefaceSpan("c1",wordTypeface), 0, firstIdx, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new CustomTypefaceSpan("c2",wordTypefaceBold), firstIdx, lastIdx+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		spannable.setSpan(new CustomTypefaceSpan("c1",wordTypeface), lastIdx+1, textStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text.setText(spannable);
		return layout;
	}
	
	public static class CustomTypefaceSpan extends TypefaceSpan {

		private final Typeface type;
		
		public CustomTypefaceSpan(String family,Typeface type) {
			super(family);
			this.type = type;
		}
		
		@Override
		public void updateDrawState(TextPaint ds){
			ds.setTypeface(type);
		}
		
		@Override
		public void updateMeasureState(TextPaint paint){
			paint.setTypeface(type);
		}

	}
	
	
	public static View createIconView(String textStr, int DrawableId, OnClickListener listener) {

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.context.getResources().getDrawable(DrawableId));

		layout.setOnClickListener(listener);

		return layout;
	}

	public static View createIconView(int textStr, int DrawableId, OnClickListener listener) {

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.context.getResources().getDrawable(DrawableId));

		layout.setOnClickListener(listener);

		return layout;
	}

	public static View createIconViewInverse(String textStr, int DrawableId, OnClickListener listener) {

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item_inverse, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.context.getResources().getDrawable(DrawableId));

		layout.setOnClickListener(listener);

		return layout;
	}
	
	public static View createTextAreaViewInverse(String textStr, int DrawableId) {

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_text_item_inverse, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.context.getResources().getDrawable(DrawableId));
		return layout;
	}

	public static View createIconViewInverse(int textStr, int DrawableId, OnClickListener listener) {

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_select_item_inverse, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(textStr);

		ImageView icon = (ImageView) layout.findViewById(R.id.question_icon);
		if (DrawableId > 0)
			icon.setImageDrawable(App.context.getResources().getDrawable(DrawableId));

		layout.setOnClickListener(listener);

		return layout;
	}
	
	public static View createTitleView(int titleStr) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_titlebar, null);
		TextView text = (TextView) layout.findViewById(R.id.titlebar_text);
		text.setTypeface(wordTypefaceBold);
		text.setText(titleStr);

		return layout;
	}
	
	public static View createTitleView(String titleStr) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_titlebar, null);
		TextView text = (TextView) layout.findViewById(R.id.titlebar_text);
		text.setTypeface(wordTypefaceBold);
		text.setText(titleStr);

		return layout;
	}

	public static View createBlankView() {

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_blank_item, null);
		return layout;
	}

	
	public static View createAnimationView(int anim_id){
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_animation_item, null);
		ImageView img = (ImageView)layout.findViewById(R.id.question_animation);
		//img.setBackgroundResource(anim_id);
		img.setImageResource(anim_id);
		TextView text = (TextView)layout.findViewById(R.id.question_animation_right_button);
		text.setTypeface(wordTypefaceBold);
		return layout;
	}
	
	
}
