package ubicomp.soberdiary.statistic.ui.block;

import ubicomp.soberdiary.main.App;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class StatisticPageView {
	protected Context context;
	protected View view;
	private LayoutInflater inflater;
	
	public StatisticPageView(int layout_id){
		this.context = App.context;
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(layout_id,null);
	}
	
	public View getView(){
		return view;
	}
	
	abstract public void load();
	
	abstract public void onCancel();
	
	abstract public void clear();
	
}
