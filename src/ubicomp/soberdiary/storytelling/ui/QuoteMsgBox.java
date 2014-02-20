package ubicomp.soberdiary.storytelling.ui;

import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.StorytellingRead;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.CustomToast;
import ubicomp.soberdiary.main.ui.EnablePage;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.config.PreferenceControl;
import ubicomp.soberdiary.system.uploader.DataUploader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("InlinedApi")
public class QuoteMsgBox {

	
	private EnablePage enablePage;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private RelativeLayout mainLayout;
	private TextView help,end;
	private Typeface wordTypefaceBold;
	private int page;
	private DatabaseControl db;
	private String[] learningArray;
	
	public QuoteMsgBox(EnablePage enablePage,RelativeLayout mainLayout){
		this.enablePage = enablePage;
		this.inflater = (LayoutInflater) App.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		db=new DatabaseControl ();
		learningArray = App.context.getResources().getStringArray(R.array.quote_learning);
		setting();
	}
	
	private void setting(){
		
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.quote_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		help = (TextView) boxLayout.findViewById(R.id.quote_text);
		end = (TextView) boxLayout.findViewById(R.id.quote_enter);
		
		mainLayout.addView(boxLayout);
		
		RelativeLayout.LayoutParams boxParam = (RelativeLayout.LayoutParams) boxLayout.getLayoutParams();
		boxParam.width = boxParam.height = LayoutParams.MATCH_PARENT;
		
		
		help.setTypeface(wordTypefaceBold);
		
		end.setTypeface(wordTypefaceBold);
		
		end.setOnClickListener(new EndListener());
	}
	
	public void clear(){
		if (boxLayout !=null)
			mainLayout.removeView(boxLayout);
	}
	
	
	public void openBox(int page){
		enablePage.enablePage(false);
		boxLayout.setVisibility(View.VISIBLE);
		this.page =page;
		help.setText(learningArray[page]);
		return;
}
	
	private class EndListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			closeBox();
			ClickLog.Log(ClickLogId.STORYTELLING_READ_OK);
			int addScore = db.insertStorytellingRead(
					new StorytellingRead(System.currentTimeMillis(),false,page,0));
			Log.d("STORYTELLING_READ","addScore "+addScore);
			if (PreferenceControl.checkCouponChange())
				PreferenceControl.setCouponChange(true);
			CustomToast.generateToast(R.string.bonus, addScore);
			DataUploader.upload();
		}
		
	}
	
	public void closeBox(){
		enablePage.enablePage(true);
		boxLayout.setVisibility(View.INVISIBLE);
	}
}
