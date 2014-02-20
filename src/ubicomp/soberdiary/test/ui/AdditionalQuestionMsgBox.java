package ubicomp.soberdiary.test.ui;

import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.AdditionalQuestionnaire;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.CustomToast;
import ubicomp.soberdiary.main.ui.CustomToastSmall;
import ubicomp.soberdiary.main.ui.EnablePage;
import ubicomp.soberdiary.main.ui.ScreenSize;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AdditionalQuestionMsgBox{

	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private TextView emotionText,cravingText;
	private SeekBar emotionSeekBar,cravingSeekBar;
	
	private RelativeLayout mainLayout;
	
	private ImageView emotionShow;
	private ImageView cravingShow;
	private TextView emotionShowText;
	private TextView cravingShowText;
	
	private TextView[] eNum,cNum;
	
	private static String[] emotionStr ;
	private static String[] cravingStr ;
	
	private LinearLayout questionLayout;
	
	private Resources r;
	private Point screen;
	
	private EndOnClickListener endListener;
	private CancelOnClickListener cancelListener;
	
	private Typeface digitTypeface;
	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;
	
	private Drawable[] emotionDrawables;
	private Drawable[] cravingDrawables;
	
	private TextView title,help;
	private TextView send,notSend;
	
	private EndOnTouchListener endOnTouchListener;
	
	private boolean done,doneByDoubleClick;
	
	private EnablePage enablePage;
	
	public AdditionalQuestionMsgBox(RelativeLayout mainLayout,EnablePage enablePage){
		this.context = App.context;
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		emotionStr = context.getResources().getStringArray(R.array.emotion_state);
		cravingStr = context.getResources().getStringArray(R.array.craving_state);
		screen = ScreenSize.getScreenSize();
		digitTypeface = Typefaces.getDigitTypeface();
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		this.enablePage = enablePage;
		setting();
		mainLayout.addView(boxLayout);
		
	}
	
	private void setting(){
		
		endListener = new EndOnClickListener();
		cancelListener = new CancelOnClickListener();
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.additional_question_box_layout,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.msg_question_layout);
		
		title = (TextView)boxLayout.findViewById(R.id.msg_title);
		title.setTypeface(wordTypefaceBold);
		
		emotionText = (TextView) boxLayout.findViewById(R.id.msg_emotion_text);
		emotionText.setTypeface(wordTypefaceBold);
		
		cravingText = (TextView) boxLayout.findViewById(R.id.msg_craving_text);
		cravingText.setTypeface(wordTypefaceBold);
		
		
		emotionSeekBar = (SeekBar) boxLayout.findViewById(R.id.msg_emotion_seek_bar);
		cravingSeekBar = (SeekBar) boxLayout.findViewById(R.id.msg_craving_seek_bar);
		
		emotionShow = (ImageView) boxLayout.findViewById(R.id.msg_emotion_show);
		cravingShow = (ImageView) boxLayout.findViewById(R.id.msg_craving_show);
		
		emotionSeekBar.setOnSeekBarChangeListener(new EmotionListener());
		cravingSeekBar.setOnSeekBarChangeListener(new DesireListener());
		
		emotionShowText = (TextView) boxLayout.findViewById(R.id.msg_emotion_show_text);
		emotionShowText.setTypeface(wordTypeface);
		
		cravingShowText = (TextView) boxLayout.findViewById(R.id.msg_craving_show_text);
		cravingShowText.setTypeface(wordTypeface);
		
		help = (TextView) boxLayout.findViewById(R.id.msg_help);
		help.setTypeface(wordTypefaceBold);
		
		eNum = new TextView[5];
		eNum[0] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num0);
		eNum[1] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num1);
		eNum[2] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num2);
		eNum[3] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num3);
		eNum[4] = (TextView) boxLayout.findViewById(R.id.msg_emotion_num4);

		for (int i=0;i<5;++i){
			eNum[i].setTypeface(digitTypeface);
		}
		
		
		cNum = new TextView[10];
		cNum[0] = (TextView) boxLayout.findViewById(R.id.msg_craving_num0);
		cNum[1] = (TextView) boxLayout.findViewById(R.id.msg_craving_num1);
		cNum[2] = (TextView) boxLayout.findViewById(R.id.msg_craving_num2);
		cNum[3] = (TextView) boxLayout.findViewById(R.id.msg_craving_num3);
		cNum[4] = (TextView) boxLayout.findViewById(R.id.msg_craving_num4);
		cNum[5] = (TextView) boxLayout.findViewById(R.id.msg_craving_num5);
		cNum[6] = (TextView) boxLayout.findViewById(R.id.msg_craving_num6);
		cNum[7] = (TextView) boxLayout.findViewById(R.id.msg_craving_num7);
		cNum[8] = (TextView) boxLayout.findViewById(R.id.msg_craving_num8);
		cNum[9] = (TextView) boxLayout.findViewById(R.id.msg_craving_num9);
		for (int i=0;i<10;++i){
			cNum[i].setTypeface(digitTypeface);
		}
		
		endOnTouchListener = new EndOnTouchListener();
		send = (TextView) boxLayout.findViewById(R.id.msg_send);
		send.setOnTouchListener(endOnTouchListener);
		send.setTypeface(wordTypefaceBold);
		notSend = (TextView) boxLayout.findViewById(R.id.msg_not_send);
		notSend.setOnTouchListener(endOnTouchListener);
		notSend.setTypeface(wordTypefaceBold);
	}
	
	public void gen(){
		
		RelativeLayout.LayoutParams boxParam = (LayoutParams) boxLayout.getLayoutParams();
		boxParam.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		boxParam.topMargin = screen.x * 80/480;
		boxParam.width = screen.x * 435/480;
		boxParam.height = screen.y * 637/762;
		
		RelativeLayout.LayoutParams qParam = (RelativeLayout.LayoutParams) questionLayout.getLayoutParams();
		qParam.leftMargin = qParam.rightMargin = screen.x * 44/480;
		qParam.width = screen.x * 400/480;
		
		emotionDrawables = new Drawable[5];
		emotionDrawables[0]  = r.getDrawable(R.drawable.msg_emotion_0);
		emotionDrawables[1]  = r.getDrawable(R.drawable.msg_emotion_1);
		emotionDrawables[2]  = r.getDrawable(R.drawable.msg_emotion_2);
		emotionDrawables[3]  = r.getDrawable(R.drawable.msg_emotion_3);
		emotionDrawables[4]  = r.getDrawable(R.drawable.msg_emotion_4);
		
		cravingDrawables = new Drawable[10];
		cravingDrawables[0]  = r.getDrawable(R.drawable.msg_craving_0);
		cravingDrawables[1]  = r.getDrawable(R.drawable.msg_craving_1);
		cravingDrawables[2]  = r.getDrawable(R.drawable.msg_craving_2);
		cravingDrawables[3]  = r.getDrawable(R.drawable.msg_craving_3);
		cravingDrawables[4]  = r.getDrawable(R.drawable.msg_craving_4);
		cravingDrawables[5]  = r.getDrawable(R.drawable.msg_craving_5);
		cravingDrawables[6]  = r.getDrawable(R.drawable.msg_craving_6);
		cravingDrawables[7]  = r.getDrawable(R.drawable.msg_craving_7);
		cravingDrawables[8]  = r.getDrawable(R.drawable.msg_craving_8);
		cravingDrawables[9]  = r.getDrawable(R.drawable.msg_craving_9);
		
	}
	
	public void clear(){
		if (mainLayout!=null && boxLayout!=null && boxLayout.getParent()!=null && boxLayout.getParent().equals(mainLayout))
			mainLayout.removeView(boxLayout);
		enablePage.enablePage(true);
	}
	
	private void enableSend(boolean enable){
		if (enable){
			send.setTextColor(0xFFf39800);
			notSend.setTextColor(0xFFf39800);
		}
		else{
			send.setTextColor(0xFF898989);
			notSend.setTextColor(0xFF898989);
		}
		done = enable;
		doneByDoubleClick = false;
	}
	
	private void enableSend(boolean enable,boolean click){
		if (enable){
			send.setTextColor(0xFFf39800);
			notSend.setTextColor(0xFFf39800);
		}
		else{
			send.setTextColor(0xFF898989);
			notSend.setTextColor(0xFF898989);
		}
		done = enable;
		doneByDoubleClick = click;
	}
	
	public void generateAdditionalBox(){
		emotionSeekBar.setProgress(1);
		emotionSeekBar.setProgress(0);
		cravingSeekBar.setProgress(1);
		cravingSeekBar.setProgress(0);
		questionLayout.setVisibility(View.VISIBLE);
		boxLayout.setVisibility(View.VISIBLE);
		send.setOnClickListener(endListener);
		notSend.setOnClickListener(cancelListener);
		enablePage.enablePage(false);
		enableSend(false);
	}
	
	private class EndOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			if (!done){
				CustomToastSmall.generateToast(R.string.msg_box_toast_send);
				enableSend(true,true);
				return;
			}
			
			if (doneByDoubleClick)
				ClickLog.Log(ClickLogId.TEST_ADDITIONAL_QUESTION_SEND_EMPTY	);
			else
				ClickLog.Log(ClickLogId.TEST_ADDITIONAL_QUESTION_SEND);
			
			boxLayout.setVisibility(View.INVISIBLE);
			int craving = cravingSeekBar.getProgress();
			int emotion =  emotionSeekBar.getProgress();
			DatabaseControl db = new DatabaseControl();
			int addScore = db.insertAdditionalQuestionnaire(new AdditionalQuestionnaire(System.currentTimeMillis(),true,emotion,craving,0));
			if (PreferenceControl.checkCouponChange())
				PreferenceControl.setCouponChange(true);
			CustomToast.generateToast(R.string.additional_questionnaire_toast, addScore);
			clear();
		}
	}
	
	private class CancelOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			
			if (!done){
				CustomToastSmall.generateToast(R.string.msg_box_toast_cancel);
				enableSend(true);
				return;
			}
			ClickLog.Log(ClickLogId.TEST_ADDITIONAL_QUESTION_CANCEL);
			boxLayout.setVisibility(View.INVISIBLE);
			int craving = -1;
			int emotion =  -1;
			DatabaseControl db = new DatabaseControl();
			db.insertAdditionalQuestionnaire(new AdditionalQuestionnaire(System.currentTimeMillis(),false,emotion,craving,0));
			clear();
		}
	}
	
	
	private class EmotionListener implements SeekBar.OnSeekBarChangeListener{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, 	boolean fromUser) {
			emotionShow.setImageDrawable(emotionDrawables[progress]);
			emotionShowText.setText(emotionStr[progress]);
			for (int i=0;i<eNum.length;++i)
				eNum[i].setVisibility(View.INVISIBLE);
			eNum[progress].setVisibility(View.VISIBLE);
			enableSend(true);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
	}
	
	private class DesireListener implements SeekBar.OnSeekBarChangeListener{
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, 	boolean fromUser) {
			cravingShow.setImageDrawable(cravingDrawables[progress]);
			cravingShowText.setText(cravingStr[progress]);
			for (int i=0;i<cNum.length;++i)
				cNum[i].setVisibility(View.INVISIBLE);
			cNum[progress].setVisibility(View.VISIBLE);
			enableSend(true);
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
	}
	
	
	private class EndOnTouchListener implements View.OnTouchListener{

		private Rect rect;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			TextView tv =(TextView)v;
			switch(e){
				case MotionEvent.ACTION_MOVE:
					if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()))
						tv.setTextSize(21.3f);
					break;
				case MotionEvent.ACTION_UP:
					tv.setTextSize(21.3f);
					break;
				case MotionEvent.ACTION_DOWN:
					tv.setTextSize(32f);
					rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
					break;
			}
			return false;
		}
	}
}
