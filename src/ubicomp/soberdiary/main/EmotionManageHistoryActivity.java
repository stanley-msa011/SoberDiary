package ubicomp.soberdiary.main;

import java.util.ArrayList;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.EmotionManagement;
import ubicomp.soberdiary.data.structure.TimeValue;
import ubicomp.soberdiary.main.ui.BarGen;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class EmotionManageHistoryActivity extends Activity {

	private ArrayList<EmotionManagement> list;
	
	private LinearLayout titleLayout;
	private LinearLayout main;

	private static final int[] EMOTION_DRAWABLE_ID = { 
			R.drawable.questionnaire_item_e0,R.drawable.questionnaire_item_e1,
			R.drawable.questionnaire_item_e2, R.drawable.questionnaire_item_e3,
			R.drawable.questionnaire_item_e4, R.drawable.questionnaire_item_e5,
			R.drawable.questionnaire_item_e6, R.drawable.questionnaire_item_e7,
			R.drawable.questionnaire_item_e8, R.drawable.questionnaire_item_e9,
	};

	private DatabaseControl db;

	private long timeInMillis;
	private TimeValue curTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emotion_manage_history);

		timeInMillis = this.getIntent().getLongExtra("timeInMillis", System.currentTimeMillis());
		curTV = TimeValue.generate(timeInMillis);
		
		db = new DatabaseControl();
		titleLayout = (LinearLayout) this.findViewById(R.id.emotion_manage_title_layout);
		main = (LinearLayout) this.findViewById(R.id.emotion_manage_main_layout);
		
		View title = BarGen.createTitleView(getString(R.string.emotion_manage_history_title)+"　"+curTV.toSimpleDateString());
		titleLayout.addView(title);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.EMOTION_MANAGE_HISTORY_ENTER);
		setQuestionEmotion();
	}
	
	@Override
	protected void onPause(){
		ClickLog.Log(ClickLogId.EMOTION_MANAGE_HISTORY_LEAVE);
		super.onPause();
	}

	private void setQuestionEmotion() {
		main.removeAllViewsInLayout();

		EmotionManagement[] ems = db.getDayEmotionManagement(curTV.year,curTV.month,curTV.day); 
		
		if (titleLayout.getChildCount()>1)
			titleLayout.removeViewAt(1);
		View tv = BarGen.createTextView(R.string.emotion_manage_history_help);
		titleLayout.addView(tv);

		list = new ArrayList<EmotionManagement>();
		
		if (ems==null)
			return;
		for (int i=0;i<ems.length;++i){
			list.add(ems[i]);
			main.addView(createItem(ems[i],i));
		}
	}
	
	
	private View createItem(EmotionManagement em,int idx){
		return BarGen.createIconViewInverse(em.reason, EMOTION_DRAWABLE_ID[em.emotion], new CustomOnItemSelectListener(idx));
	}
	
	private View selectItem(EmotionManagement em){
		return BarGen.createTextAreaViewInverse(em.reason, EMOTION_DRAWABLE_ID[em.emotion]);
	}

	private int prevPosition = -1;
	
	private class CustomOnItemSelectListener implements OnClickListener{

		private int position;
		
		public CustomOnItemSelectListener(int pos){
			this.position = pos;
		}
		@Override
		public void onClick(View v) {
			if (prevPosition == position)
				return;
			ClickLog.Log(ClickLogId.EMOTION_MANAGE_HISTORY_SELECT);
			if (prevPosition > -1){
				main.removeViewAt(prevPosition);
				main.addView(createItem(list.get(prevPosition),prevPosition), prevPosition);
			}
			main.removeView(v);
			main.addView(selectItem(list.get(position)), position);
			prevPosition = position;
		}
	}
}
