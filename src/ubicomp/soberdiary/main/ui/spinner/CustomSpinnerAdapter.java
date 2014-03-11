package ubicomp.soberdiary.main.ui.spinner;

import java.util.ArrayList;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.Typefaces;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter implements SpinnerAdapter {

	private ArrayList<SpinnerView> data;
	private LayoutInflater inflater;
	private Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();
	
	
	public CustomSpinnerAdapter(Context context, ArrayList<SpinnerView> data) {
		this.data = data;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
    public View getView(int position, View convertView, ViewGroup parent){
        return data.get(position).getView();
    }
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent){
		View dropDownView = inflater.inflate(R.layout.spinner_dropdown, null,false);
		TextView textDrop = (TextView) dropDownView.findViewById(R.id.spinner_text);
		textDrop.setTypeface(wordTypefaceBold);
		textDrop.setText(data.get(position).getTextId());
		ImageView iconDrop = (ImageView) dropDownView.findViewById(R.id.spinner_icon);
		iconDrop.setImageResource(data.get(position).getImageId());
		return dropDownView;
	}
	
	@Override
	public int getCount() {
		return data==null?0:data.size();
	}
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public int getItemViewType(int position) {
		return 0;
	}
	@Override
	public int getViewTypeCount() {
		return 1;
	}
	@Override
	public boolean hasStableIds() {
		return false;
	}
	@Override
	public boolean isEmpty() {
		return data == null || data.size()==0;
	}
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	}
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}
}
