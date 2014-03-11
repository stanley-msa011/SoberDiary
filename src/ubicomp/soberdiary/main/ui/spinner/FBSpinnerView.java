package ubicomp.soberdiary.main.ui.spinner;

import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.Typefaces;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FBSpinnerView implements SpinnerView{

	private View view;
	private int text_id,icon_id;
	
	public FBSpinnerView(int text_id, int icon_id){
		LayoutInflater inflater = (LayoutInflater) App.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.spinner_view, null);
		this.text_id = text_id;
		this.icon_id = icon_id;
		
		Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();
		TextView text = (TextView) view.findViewById(R.id.spinner_text);
		text.setTypeface(wordTypefaceBold);
		text.setText(text_id);
		ImageView icon = (ImageView) view.findViewById(R.id.spinner_icon);
		icon.setImageResource(icon_id);
	}
	
	@Override
	public View getView() {
		return view;
	}

	@Override
	public View getDropDownView() {
		return null;
	}

	@Override
	public int getImageId() {
		return icon_id;
	}

	@Override
	public int getTextId() {
		return text_id;
	}

}
