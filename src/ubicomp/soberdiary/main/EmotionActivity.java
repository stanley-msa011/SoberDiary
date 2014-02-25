package ubicomp.soberdiary.main;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.EmotionDIY;
import ubicomp.soberdiary.data.structure.Questionnaire;
import ubicomp.soberdiary.main.ui.BarGen;
import ubicomp.soberdiary.main.ui.CustomToast;
import ubicomp.soberdiary.main.ui.CustomToastSmall;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.ConnectSocialInfo;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class EmotionActivity extends Activity {

	private LayoutInflater inflater;

	private Typeface wordTypefaceBold;

	private RelativeLayout bgLayout;
	private LinearLayout mainTop;
	private LinearLayout mainLayout, titleLayout;
	private RelativeLayout callLayout;
	private RelativeLayout animEndLayout;
	private TextView callOK, callCancel, callHelp;
	private TextView animOK, animCancel, animHelp;
	private ImageView animLeft, animCenter, animationImg;
	private ImageView barBg, bar, barStart, barEnd;
	private RelativeLayout barLayout;
	
	private TextView endButton;
	
	private Activity activity;

	private MediaPlayer mediaPlayer;

	private AnimationDrawable animation;

	private static final int[] DRAWABLE_ID = { R.drawable.questionnaire_item_sol_0,
			R.drawable.questionnaire_item_sol_1, R.drawable.questionnaire_item_sol_2,
			R.drawable.questionnaire_item_sol_3, R.drawable.questionnaire_item_sol_4,
			R.drawable.questionnaire_item_sol_5 };

	private static String[] texts;

	private OnClickListener[] ClickListeners = { new AnimationSelectionOnClickListener(0),
			new AnimationSelectionOnClickListener(1), new AnimationSelectionOnClickListener(2),
			new RecreationOnClickListener(), new HelpOnClickListener(4), new HelpOnClickListener(5) };

	private static final int TYPE_SOCIAL = 4, TYPE_FAMILY = 5;

	private DatabaseControl db;

	private int state = 0;

	private int intent_type = -1;
	private int[] intent_seq = null;

	private final AnimationPlayClickListener animationPlayClickListener = new AnimationPlayClickListener();
	private final AnimationStopClickListener animationStopClickListener = new AnimationStopClickListener();
	private final MediaOnCompletionListener mediaOnCompletionListener = new MediaOnCompletionListener();
	
	private CountDownTimer musicTimer;
	
	private int anim_id, media_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emotion);

		Intent fromIntent = this.getIntent();
		this.intent_type = fromIntent.getIntExtra("type", -2);
		this.intent_seq = fromIntent.getIntArrayExtra("seq");

		texts = getResources().getStringArray(R.array.emotionDIY_solution);

		this.activity = this;
		bgLayout = (RelativeLayout) this.findViewById(R.id.emotion_all_layout);
		titleLayout = (LinearLayout) this.findViewById(R.id.emotion_title_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.emotion_main_layout);
		mainTop = (LinearLayout) this.findViewById(R.id.emotion_main_top);
		inflater = LayoutInflater.from(this);
		callLayout = (RelativeLayout) inflater.inflate(R.layout.call_check_layout, null);
		animEndLayout = (RelativeLayout) inflater.inflate(R.layout.check_end_layout, null);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		setCallCheckBox();
		setAnimEndBox();
		db = new DatabaseControl();
		
		View title = BarGen.createTitleView(R.string.emotionDIY_title);
		titleLayout.addView(title);

	}

	@Override
	protected void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.EMOTION_DIY_ENTER);
		enableBack = true;
		setQuestionStart();
	}

	@Override
	protected void onPause() {
		if (callLayout != null)
			bgLayout.removeView(callLayout);
		if (animEndLayout != null)
			bgLayout.removeView(animEndLayout);
		if (musicTimer !=null){
			musicTimer.cancel();
			musicTimer = null;
		}
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		int item_count = mainLayout.getChildCount();
		for (int i = 0; i < item_count; ++i)
			mainLayout.getChildAt(i).setEnabled(true);
		ClickLog.Log(ClickLogId.EMOTION_DIY_LEAVE);
		super.onPause();
	}

	private RelativeLayout.LayoutParams boxParam;

	private void setCallCheckBox() {

		callOK = (TextView) callLayout.findViewById(R.id.call_ok_button);
		callCancel = (TextView) callLayout.findViewById(R.id.call_cancel_button);
		callHelp = (TextView) callLayout.findViewById(R.id.call_help);

		callHelp.setTypeface(wordTypefaceBold);
		callOK.setTypeface(wordTypefaceBold);
		callCancel.setTypeface(wordTypefaceBold);

	}

	private void setAnimEndBox() {

		animOK = (TextView) animEndLayout.findViewById(R.id.anim_ok_button);
		animCancel = (TextView) animEndLayout.findViewById(R.id.anim_cancel_button);
		animHelp = (TextView) animEndLayout.findViewById(R.id.anim_help);

		animHelp.setTypeface(wordTypefaceBold);
		animOK.setTypeface(wordTypefaceBold);
		animCancel.setTypeface(wordTypefaceBold);
	}

	private void setQuestionStart() {
		state = 0;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		View tv = BarGen.createTextView(R.string.emotionDIY_help);
		mainLayout.addView(tv);

		for (int i = 0; i < texts.length; ++i) {
			View v = BarGen.createIconView(texts[i], DRAWABLE_ID[i], ClickListeners[i]);
			mainLayout.addView(v);
		}

	}

	private void setQuestionCall(int type) {
		state = 1;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		View tv = BarGen.createTextView(R.string.call_to);
		mainLayout.addView(tv);

		String[] names = new String[3];
		String[] calls = new String[3];

		if (type == TYPE_FAMILY) {
			names = PreferenceControl.getConnectFamilyName();
			calls = PreferenceControl.getConnectFamilyPhone();
		} else if (type == TYPE_SOCIAL) {
			int[] idxs = PreferenceControl.getConnectSocialHelpIdx();
			names[0] = ConnectSocialInfo.NAME[idxs[0]];
			names[1] = ConnectSocialInfo.NAME[idxs[1]];
			names[2] = ConnectSocialInfo.NAME[idxs[2]];
			calls[0] = ConnectSocialInfo.PHONE[idxs[0]];
			calls[1] = ConnectSocialInfo.PHONE[idxs[1]];
			calls[2] = ConnectSocialInfo.PHONE[idxs[2]];
		}

		int counter = 0;
		for (int i = 0; i < 3; ++i) {
			OnClickListener listener = new CallCheckOnClickListener(type, names[i], calls[i]);
			String text = names[i];
			if (names[i].length() > 0) {
				View vv = BarGen.createIconView(text, R.drawable.questionnaire_item_call, listener);
				mainLayout.addView(vv);
				++counter;
			}
		}
		if (counter == 0) {
			mainLayout.removeAllViews();

			View tv2 = BarGen.createTextView(R.string.emotion_connect_null);
			mainLayout.addView(tv2);
		}

	}

	private void setAnimationEnd(int selection) {
		state = 1;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();
		
		View tv;
		switch (selection) {
		case 0:
			tv = BarGen.createTextView(R.string.emotionDIY_help_case0);
			anim_id = R.anim.music_animation;
			media_id = R.raw.music;
			break;
		case 1:
			tv = BarGen.createTextView(R.string.emotionDIY_help_case1);
			anim_id = R.anim.breath_animation;
			media_id = R.raw.emotion1;
			break;
		case 2:
			tv = BarGen.createTextView(R.string.emotionDIY_help_case2);
			anim_id = R.anim.walk_animation;
			media_id = R.raw.emotion2;
			break;
		default:
			tv = BarGen.createTextView(R.string.emotionDIY_help_case1);
			anim_id = R.anim.breath_animation;
			media_id = R.raw.emotion1;
			break;
		}
		mainTop.addView(tv);

		if (animation != null) {
			animation.stop();
			animation = null;
		}
		if (musicTimer !=null){
			musicTimer.cancel();
			musicTimer = null;
		}
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}

		RelativeLayout av =null;
		av= (RelativeLayout) BarGen.createAnimationView(anim_id);
		
		barLayout = (RelativeLayout) av.findViewById(R.id.question_progress_layout);
		barBg = (ImageView) av.findViewById(R.id.question_progress_bar_bg);
		bar = (ImageView) av.findViewById(R.id.question_progress_bar);
		barStart = (ImageView) av.findViewById(R.id.question_progress_bar_start);
		barEnd = (ImageView) av.findViewById(R.id.question_progress_bar_end);
		
		animationImg = (ImageView) av.findViewById(R.id.question_animation);
		animation = (AnimationDrawable) animationImg.getDrawable();
		animation.start();
		
		endButton = (TextView) av.findViewById(R.id.question_animation_right_button);
		endButton.setOnClickListener(new AnimCheckOnClickListener(selection));
		
		mediaPlayer = MediaPlayer.create(getApplicationContext(), media_id);
		mediaPlayer.setOnCompletionListener(mediaOnCompletionListener);

		animLeft = (ImageView) av.findViewById(R.id.question_animation_left_button);
		animCenter = (ImageView) av.findViewById(R.id.question_animation_center_button);

		animCenter.setImageResource(R.drawable.stop);
		animLeft.setImageResource(R.drawable.pause);
		animCenter.setOnClickListener(animationStopClickListener);
		animLeft.setOnClickListener(animationPlayClickListener);

		mainTop.addView(av);

		int total_time = mediaPlayer.getDuration();
		musicTimer = new MusicTimer(total_time);
		mediaPlayer.start();
	}

	private class AnimationPlayClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			if (musicTimer !=null){
				musicTimer.cancel();
				musicTimer = null;
			}
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				animLeft.setImageResource(R.drawable.play);
				animLeft.setOnClickListener(animationPlayClickListener);
				animCenter.setImageResource(0);
				animCenter.setOnClickListener(null);
				animation.stop();
				ClickLog.Log(ClickLogId.EMOTION_DIY_PAUSE);
			} else {
				musicTimer = new MusicTimer(mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition());
				mediaPlayer.start();
				musicTimer.start();
				animLeft.setImageResource(R.drawable.pause);
				animLeft.setOnClickListener(animationPlayClickListener);
				animCenter.setImageResource(R.drawable.stop);
				animCenter.setOnClickListener(animationStopClickListener);
				animation.start();
				ClickLog.Log(ClickLogId.EMOTION_DIY_PLAY);
			}
		}
	}

	private class AnimationStopClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			if (musicTimer !=null){
				musicTimer.cancel();
				musicTimer = null;
			}
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				mediaPlayer.seekTo(0);
				animLeft.setImageResource(R.drawable.play);
				animLeft.setOnClickListener(animationPlayClickListener);
				animCenter.setImageResource(0);
				animCenter.setOnClickListener(null);
				animation.stop();
				ClickLog.Log(ClickLogId.EMOTION_DIY_STOP);
			}
		}
	}

	private class MediaOnCompletionListener implements MediaPlayer.OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			if (musicTimer!=null){
				musicTimer.cancel();
				musicTimer = null;
			}
			mp.seekTo(0);
			animLeft.setImageResource(R.drawable.play);
			animLeft.setOnClickListener(animationPlayClickListener);
			animCenter.setImageResource(0);
			animCenter.setOnClickListener(null);
			animation.stop();
		}
	}

	private void setRecreationEnd(String selected) {
		state = 2;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		String text = getString(R.string.emotionDIY_help_case4) + selected;
		View tv;
		tv = BarGen.createTextView(text);
		mainLayout.addView(tv);
		View vv = BarGen.createIconView(R.string.try_to_do, R.drawable.questionnaire_item_ok, new EndOnClickListener(3,
				selected));
		mainLayout.addView(vv);

	}

	private void setQuestionRecreation() {
		state = 1;

		mainLayout.removeAllViews();
		mainTop.removeAllViews();

		String[] recreation = PreferenceControl.getRecreations();
		if (recreation[0].length() == 0)
			recreation[0] = getString(R.string.default_recreation_1);

		if (recreation[1].length() == 0)
			recreation[1] = getString(R.string.default_recreation_2);

		if (recreation[2].length() == 0)
			recreation[2] = getString(R.string.default_recreation_3);

		boolean[] has_value = { recreation[0].length() > 0, recreation[1].length() > 0, recreation[2].length() > 0,
				recreation[3].length() > 0, recreation[4].length() > 0 };

		boolean exist = false;

		for (int i = 0; i < has_value.length; ++i)
			exist |= has_value[i];

		View tv;
		if (exist)
			tv = BarGen.createTextView(R.string.emotionDIY_help_case3);
		else
			tv = BarGen.createTextView(R.string.emotionDIY_help_case3_2);
		mainLayout.addView(tv);

		for (int i = 0; i < has_value.length; ++i) {
			if (has_value[i]) {
				View v = BarGen.createIconView(recreation[i], 0, new RecreationSelectionOnClickListener(recreation[i]));
				mainLayout.addView(v);
			}
		}

	}

	private class EndOnClickListener implements View.OnClickListener {
		int selection;
		String recreation = null;

		EndOnClickListener(int selection) {
			this.selection = selection;
		}

		EndOnClickListener(int selection, String recreation) {
			this.selection = selection;
			this.recreation = recreation;
		}

		@Override
		public void onClick(View v) {

			long ts = System.currentTimeMillis();
			int addScore = db.insertEmotionDIY(new EmotionDIY(ts, selection, recreation, 0));
			int addScore2 = 0;
			if (intent_type > -2) {
				addScore2 = db.insertQuestionnaire(new Questionnaire(ts, intent_type, seq_toString(), 0));
				PreferenceControl.setTestResult(-1);
			}
			CustomToast.generateToast(R.string.emotionDIY_end_toast, addScore + addScore2);
			ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
			activity.finish();
		}
	}

	private class AnimCheckOnClickListener implements View.OnClickListener {
		private int selection;

		AnimCheckOnClickListener(int selection) {
			this.selection = selection;
		}

		@Override
		public void onClick(View v) {

			if (mediaPlayer!= null && mediaPlayer.isPlaying()) {
				if (musicTimer !=null){
					musicTimer.cancel();
					musicTimer = null;
				}
				mediaPlayer.pause();
				animLeft.setImageResource(R.drawable.play);
				animLeft.setOnClickListener(animationPlayClickListener);
				animCenter.setImageResource(0);
				animCenter.setOnClickListener(null);
				animation.stop();
			}

			animLeft.setEnabled(false);
			animCenter.setEnabled(false);
			animationImg.setEnabled(false);
			bgLayout.addView(animEndLayout);

			boxParam = (LayoutParams) animEndLayout.getLayoutParams();
			boxParam.width = LayoutParams.MATCH_PARENT;
			boxParam.height = LayoutParams.MATCH_PARENT;
			boxParam.addRule(RelativeLayout.CENTER_IN_PARENT);

			animOK.setOnClickListener(new EndOnClickListener(selection));
			animCancel.setOnClickListener(new AnimCancelOnClickListener());
			ClickLog.Log(ClickLogId.EMOTION_DIY_END_PLAY);
		}
	}

	private class AnimCancelOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			animLeft.setEnabled(true);
			animCenter.setEnabled(true);
			animationImg.setEnabled(true);
			bgLayout.removeView(animEndLayout);
			ClickLog.Log(ClickLogId.EMOTION_DIY_END_PLAY_CANCEL);
		}
	}

	private class CallCheckOnClickListener implements View.OnClickListener {

		private int selection;
		private String name;
		private String call;

		CallCheckOnClickListener(int selection, String name, String call) {
			this.selection = selection;
			this.name = name;
			this.call = call;
		}

		@SuppressLint("InlinedApi")
		@Override
		public void onClick(View v) {
			int item_count = mainLayout.getChildCount();
			for (int i = 0; i < item_count; ++i)
				mainLayout.getChildAt(i).setEnabled(false);
			enableBack = false;

			bgLayout.addView(callLayout);

			boxParam = (LayoutParams) callLayout.getLayoutParams();
			boxParam.width = LayoutParams.MATCH_PARENT;
			boxParam.height = LayoutParams.MATCH_PARENT;
			boxParam.addRule(RelativeLayout.CENTER_IN_PARENT);

			String call_check = getResources().getString(R.string.call_check_help);
			String question_sign = getResources().getString(R.string.question_sign);
			callHelp.setText(call_check + " " + name + " " + question_sign);
			callOK.setOnClickListener(new CallOnClickListener(selection, name, call));
			callCancel.setOnClickListener(new CallCancelOnClickListener());
			ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}

	}

	private class RecreationSelectionOnClickListener implements View.OnClickListener {

		private String recreation;

		public RecreationSelectionOnClickListener(String recreation) {
			this.recreation = recreation;
		}

		@Override
		public void onClick(View v) {
			setRecreationEnd(recreation);
			ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}

	}

	private class CallCancelOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			bgLayout.removeView(callLayout);
			int item_count = mainLayout.getChildCount();
			for (int i = 0; i < item_count; ++i)
				mainLayout.getChildAt(i).setEnabled(true);
			enableBack = true;
			ClickLog.Log(ClickLogId.EMOTION_DIY_CALL_CANCEL);
		}

	}

	private class CallOnClickListener implements View.OnClickListener {
		private int selection;
		private String call;

		// private String name;

		CallOnClickListener(int selection, String name, String call) {
			this.selection = selection;
			// this.name = name;
			this.call = call;
		}

		@Override
		public void onClick(View v) {
			long ts = System.currentTimeMillis();
			db.insertEmotionDIY(new EmotionDIY(ts, selection, "", 0));
			if (intent_type > -2) {
				db.insertQuestionnaire(new Questionnaire(ts, intent_type, seq_toString(), 0));
				PreferenceControl.setTestResult(-1);
			}
			ClickLog.Log(ClickLogId.EMOTION_DIY_CALL_OK);
			Intent intentDial = new Intent("android.intent.action.CALL", Uri.parse("tel:" + call));
			activity.startActivity(intentDial);
			activity.finish();
		}
	}

	private String seq_toString() {
		int size = intent_seq.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; ++i) {
			sb.append(intent_seq[i]);
			if (i < size - 1)
				sb.append(",");
		}
		return sb.toString();
	}

	private class AnimationSelectionOnClickListener implements View.OnClickListener {
		int selection;

		AnimationSelectionOnClickListener(int selection) {
			this.selection = selection;
		}

		@Override
		public void onClick(View v) {
			setAnimationEnd(selection);
			ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}
	}

	private class RecreationOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			setQuestionRecreation();
			ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}
	}

	private class HelpOnClickListener implements View.OnClickListener {
		int type;

		HelpOnClickListener(int type) {
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			setQuestionCall(type);
			ClickLog.Log(ClickLogId.EMOTION_DIY_SELECTION);
		}
	}

	private class MusicTimer extends CountDownTimer {

		public MusicTimer(long total_millis) {
			super(total_millis, 50);
		}

		@Override
		public void onFinish() {
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (bar!=null){
				RelativeLayout.LayoutParams barParam = (LayoutParams) bar.getLayoutParams();
				int total_len = barBg.getWidth() - barStart.getWidth() - barEnd.getWidth();
				barParam.width = total_len*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();
				barLayout.updateViewLayout(bar, barParam);
				if (animation == null){
				}
			}
		}
	}
	
	
	private boolean enableBack = true;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ClickLog.Log(ClickLogId.EMOTION_DIY_RETURN);
			if (!enableBack)
				return false;
			if (animation != null) {
				animation.stop();
				animation = null;
			}
			if (musicTimer != null){
				musicTimer.cancel();
				musicTimer = null;
			}
			if (mediaPlayer != null) {
				mediaPlayer.release();
				mediaPlayer = null;
			}
			if (animEndLayout!= null && animEndLayout.getParent()!=null && animEndLayout.getParent().equals(bgLayout)){
				bgLayout.removeView(animEndLayout);
				return false;
			}
				
			if (state == 0) {
				CustomToastSmall.generateToast(R.string.emotionDIY_toast);
				--state;
			} else if (state == -1)
				return super.onKeyDown(keyCode, event);
			else {
				--state;
				if (state == 0)
					setQuestionStart();
				else if (state == 1)
					setQuestionRecreation();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
