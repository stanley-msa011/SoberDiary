package ubicomp.soberdiary.test.ui;

import java.util.Random;

import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.TimeValue;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.EmotionActivity;
import ubicomp.soberdiary.main.MainActivity;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.EnablePage;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.system.check.StartDateCheck;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotificationDialog {

	private FrameLayout layout;
	
	private TextView title,subtitle,comment,cancelText,gotoText;
	private ImageView image;
	
	private String[] subtitles = App.context.getResources().getStringArray(R.array.notification_subtitles);
	private String[] comments = App.context.getResources().getStringArray(R.array.notification_comments);
	private int[] IMAGE_ID = {R.drawable.notification_brac_test, 
			R.drawable.notification_additional_questionnaire, 
			R.drawable.notification_emotion_diy,
			R.drawable.notification_voice_record,
			R.drawable.notification_emotion_management,
			R.drawable.notification_storytelling};
	
	
	private DatabaseControl db = new DatabaseControl();
	
	private RelativeLayout main_layout;
	private EnablePage enablePage;
	
	private int showType = -1;
	
	private NotificationInterface notificationInterface;
	
	private Context context;
	
	public NotificationDialog(Context context, RelativeLayout main_layout, EnablePage enablePage, NotificationInterface notificationInterface){
		
		this.context = context;
		this.main_layout = main_layout;
		this.enablePage = enablePage;
		this.notificationInterface = notificationInterface;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (FrameLayout) inflater.inflate(R.layout.notification_dialog, null);
		
		Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();
		Typeface wordTypeface = Typefaces.getWordTypeface();
		
		title = (TextView) layout.findViewById(R.id.notification_title);
		title.setTypeface(wordTypefaceBold);
		
		subtitle = (TextView) layout.findViewById(R.id.notification_subtitle);
		subtitle.setTypeface(wordTypefaceBold);
		
		comment = (TextView) layout.findViewById(R.id.notification_comment);
		comment.setTypeface(wordTypeface);
		
		cancelText = (TextView) layout.findViewById(R.id.notification_cancel);
		cancelText.setTypeface(wordTypefaceBold);
		cancelText.setOnClickListener(new CancelOnClickListener());
		cancelText.setOnTouchListener(new EndOnTouchListener());
		
		gotoText = (TextView) layout.findViewById(R.id.notification_ok);
		gotoText.setTypeface(wordTypefaceBold);
		gotoText.setOnClickListener(new GotoOnClickListener());
		gotoText.setOnTouchListener(new EndOnTouchListener());
		
		image = (ImageView) layout.findViewById(R.id.notification_image);
	}
	
	public boolean setting(){
		
		showType = -1;
		
		if (!StartDateCheck.afterStartDate())
			return false;
		
		boolean check = PreferenceControl.showNotificationDialog();
		
		if (!check)
			return false;
		
		long curTime = System.currentTimeMillis();
		TimeValue curTv = TimeValue.generate(curTime);
		
		TimeValue[] tvs = new TimeValue[subtitles.length];
		tvs[0] = db.getLatestDetection().tv;
		tvs[1] = db.getLatestAdditionalQuestionnaire().tv;
		tvs[2] = db.getLatestEmotionDIY().tv;
		tvs[3] = db.getLatestUserVoiceRecord().tv;
		tvs[4] = db.getLatestEmotionManagement().tv;
		tvs[5] = db.getLatestStorytellingRead().tv;
		
		if (curTv.week <= 0)//StorytellingReading
			tvs[5] = curTv;

		boolean[] show_dialog = new boolean[subtitles.length];
		
		int mod = 0;
		for (int i=0;i<tvs.length;++i){
			show_dialog[i] = tvs[i].showNotificationDialog(curTime);
			if (show_dialog[i] && i!=5)
				++mod;
		}
		
		if (mod == 0)
			return false;
		
		PreferenceControl.setShowedNotificationDialog();
		
		Random rand = new Random();
		int randNum = rand.nextInt(mod);
		int type = -1;
		for (int i=0;i<show_dialog.length;++i){
			if (randNum == 0){
				type = i;
				break;
			}
			else if (show_dialog[i])
				--randNum;
		}
		
		settingType(type);
		
		removeView();
		addView();
		
		return true;
	}
	
	private void settingType(int type){
		if (type < 0)
			return;
		
		showType = type;
		
		subtitle.setText(subtitles[type]);
		comment.setText(comments[type]);
		image.setImageResource(IMAGE_ID[type]);
	}
	
	private void addView(){
		main_layout.addView(layout);
		LayoutParams param = layout.getLayoutParams();
		param.width = param.height = LayoutParams.MATCH_PARENT;
		enablePage.enablePage(false);
	}
	
	public void removeView(){
		if (layout!=null && layout.getParent() !=null){
			ViewGroup vg = (ViewGroup)layout.getParent();
			vg.removeView(layout);
			enablePage.enablePage(true);
		}
	}
	
	private class CancelOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.TEST_NOTIFICATION_CANCEL);
			removeView();
		}
	}
	
	private class GotoOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.TEST_NOTIFICATION_GOTO);
			removeView();
			switch (showType){
			case 0:
				notificationInterface.notifyStartButton();
				break;
			case 1:
				notificationInterface.notifyAdditionalQuestionnaire();
				break;
			case 2:
				Intent intent = new Intent(context, EmotionActivity.class);
				context.startActivity(intent);
				break;
			case 3:
				MainActivity.changeTab(2, MainActivity.ACTION_RECORD);
				break;
			case 4:
				MainActivity.changeTab(2, MainActivity.ACTION_RECORD);
				break;
			case 5:
				MainActivity.changeTab(2);
				break;
			}
			
		}
	}
	
	private class EndOnTouchListener implements View.OnTouchListener{

		private Rect rect;
		private final int normalSize = App.context.getResources().getDimensionPixelSize(R.dimen.normal_title_size);
		private int largeSize = App.context.getResources().getDimensionPixelSize(R.dimen.large_title_size);
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int e = event.getAction();
			TextView tv =(TextView)v;
			switch(e){
				case MotionEvent.ACTION_MOVE:
					if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()))
						tv.setTextSize(normalSize);
					break;
				case MotionEvent.ACTION_UP:
					tv.setTextSize(normalSize);
					break;
				case MotionEvent.ACTION_DOWN:
					tv.setTextSize(largeSize);
					rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
					break;
			}
			return false;
		}
	}
	
}
