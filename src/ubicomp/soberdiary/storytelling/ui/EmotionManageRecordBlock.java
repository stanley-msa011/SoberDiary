package ubicomp.soberdiary.storytelling.ui;

import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.EmotionManagement;
import ubicomp.soberdiary.data.structure.TimeValue;
import ubicomp.soberdiary.main.EmotionManageActivity;
import ubicomp.soberdiary.main.EmotionManageHistoryActivity;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EmotionManageRecordBlock implements RecorderCallee {
	private Context context;
	private LayoutInflater inflater;
	private DatabaseControl db;

	private RelativeLayout contentLayout;

	private TextView help;

	private TimeValue curTV;
	private ImageView bottomIcon;
	private RelativeLayout topButton, bottomButton;
	private final AddOnClickListener addOnClickListener = new AddOnClickListener();
	private final HistoryOnClickListener historyOnClickListener = new HistoryOnClickListener();
	private LinearLayout listLayout;
	
	private static final int[] emotionBgs={
		R.drawable.emotion_type_0,
		R.drawable.emotion_type_1,
		R.drawable.emotion_type_2,
		R.drawable.emotion_type_3,
		R.drawable.emotion_type_4,
		R.drawable.emotion_type_5,
		R.drawable.emotion_type_6,
		R.drawable.emotion_type_7,
		R.drawable.emotion_type_8,
		R.drawable.emotion_type_9,
	};
	
	private static final int[] emotionVer1Bgs={
		R.drawable.emotion_ver1_0,
		R.drawable.emotion_ver1_1,
		R.drawable.emotion_ver1_2,
		R.drawable.emotion_ver1_3,
		R.drawable.emotion_ver1_4,
		R.drawable.emotion_ver1_5,
		R.drawable.emotion_ver1_6,
	};
	
	private Drawable historyDrawable,historyOffDrawable;
	
	public EmotionManageRecordBlock(RecordBlockCaller recordCaller,Context context) {
		this.context = context;
		db = new DatabaseControl();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentLayout = (RelativeLayout) inflater.inflate(R.layout.emotion_manage_record_block, null);
		listLayout = (LinearLayout) contentLayout.findViewById(R.id.em_record_list_layout);
		help = (TextView) contentLayout.findViewById(R.id.em_record_help);
		help.setTypeface(Typefaces.getWordTypefaceBold());

		topButton = (RelativeLayout) contentLayout.findViewById(R.id.em_record_top_button);
		bottomButton = (RelativeLayout) contentLayout.findViewById(R.id.em_record_bottom_button);

		historyDrawable = context.getResources().getDrawable(R.drawable.emotion_manage_history);
		historyOffDrawable = null;
		
		bottomIcon = (ImageView) contentLayout.findViewById(R.id.em_record_bottom_icon);
	}
	@Override
	public View getRecordBox(TimeValue tv, int idx) {
		curTV = tv;
		topButton.setOnClickListener(addOnClickListener);
		
		listLayout.removeAllViews();
		EmotionManagement[] ems = db.getDayEmotionManagement(curTV.year,curTV.month,curTV.day);
		
		if (ems != null){
			int len = Math.min(ems.length,5);
			for (int i=0;i<len;++i){
				ImageView im = new ImageView(context);
				if (ems[i].emotion < 100)
					im.setImageResource(emotionBgs[ems[i].emotion]);
				else
					im.setImageResource(emotionVer1Bgs[ems[i].emotion-100]);
				listLayout.addView(im);
			}
			bottomIcon.setImageDrawable(historyDrawable);
			bottomButton.setBackgroundResource(R.drawable.record_box_bottom_button);
			bottomButton.setOnClickListener(historyOnClickListener);
		}else{
			bottomIcon.setImageDrawable(historyOffDrawable);
			bottomButton.setBackgroundResource(R.drawable.record_button_bottom);
			bottomButton.setOnClickListener(null);
		}
		enableRecordBox(true);
		return contentLayout;
	}

	private class AddOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.STORYTELLING_RECORD_ADD_EM);
			Intent intent = new Intent(context,EmotionManageActivity.class);
			intent.putExtra("timeInMillis", curTV.timestamp);
			context.startActivity(intent);
		}
	}
	
	private class HistoryOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.STORYTELLING_RECORD_EM_HISTORY);
			Intent intent = new Intent(context,EmotionManageHistoryActivity.class);
			intent.putExtra("timeInMillis", curTV.timestamp);
			context.startActivity(intent);
		}
	}
	
	@Override
	public void cleanRecordBox() {
		enableRecordBox(false);
	}

	@Override
	public void enableRecordBox(boolean enable) {
		topButton.setEnabled(enable);
		bottomButton.setEnabled(enable);
	}

}
