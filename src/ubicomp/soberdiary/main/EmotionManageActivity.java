package ubicomp.soberdiary.main;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.EmotionManagement;
import ubicomp.soberdiary.data.structure.TimeValue;
import ubicomp.soberdiary.main.ui.BarGen;
import ubicomp.soberdiary.main.ui.CustomToast;
import ubicomp.soberdiary.main.ui.CustomToastSmall;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class EmotionManageActivity extends Activity {

	private LayoutInflater inflater;

	private Typeface wordTypefaceBold;

	private LinearLayout titleLayout;
	private LinearLayout mainLayout;

	private Activity activity;

	private int emotion, r_type;
	private String reason;
	private EditText r_texts;

	private static final int[] EMOTION_DRAWABLE_ID = { R.drawable.emotion_type_0,
			R.drawable.emotion_type_1, R.drawable.emotion_type_2, R.drawable.emotion_type_3,
			R.drawable.emotion_type_4, R.drawable.emotion_type_5, R.drawable.emotion_type_6,
			R.drawable.emotion_type_7, R.drawable.emotion_type_8, R.drawable.emotion_type_9, };

	private static String[] emotion_texts;

	private static final int[] RELATED_DRAWABLE_ID = { R.drawable.reason_type_0,
			R.drawable.reason_type_1, R.drawable.reason_type_2, R.drawable.reason_type_3 };

	private static String[] related_texts;

	private DatabaseControl db;
	private int state = 0;

	private ImageView updown;
	private Drawable upDrawable, downDrawable;

	private long timeInMillis;
	private TimeValue curTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emotion_manage);

		timeInMillis = this.getIntent().getLongExtra("timeInMillis", System.currentTimeMillis());
		curTV = TimeValue.generate(timeInMillis);

		emotion = r_type = -1;
		reason = "";
		r_texts = null;

		emotion_texts = getResources().getStringArray(R.array.emotion_manage_emotion);
		related_texts = getResources().getStringArray(R.array.emotion_manage_related);

		this.activity = this;
		db = new DatabaseControl();
		titleLayout = (LinearLayout) this.findViewById(R.id.emotion_manage_title_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.emotion_manage_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		mainLayout.removeAllViews();

		upDrawable = getResources().getDrawable(R.drawable.list_hide);
		downDrawable = getResources().getDrawable(R.drawable.list_show);

		View title = BarGen.createTitleView(getString(R.string.emotion_manage_title0) + curTV.toSimpleDateString()
				+ getString(R.string.emotion_manage_title1));
		titleLayout.addView(title);

	}

	@Override
	protected void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.EMOTION_MANAGE_ENTER);
		setQuestionEmotion();
	}

	@Override
	protected void onPause() {
		ClickLog.Log(ClickLogId.EMOTION_MANAGE_LEAVE);
		super.onPause();
	}

	private void setQuestionEmotion() {
		mainLayout.removeAllViews();

		if (titleLayout.getChildCount() > 1)
			titleLayout.removeViewAt(1);
		View tv = BarGen.createTextView(R.string.emotion_manage_help1);
		titleLayout.addView(tv);

		for (int i = 0; i < emotion_texts.length; ++i) {
			View v = BarGen.createIconViewInverse(emotion_texts[i], EMOTION_DRAWABLE_ID[i], new EmotionOnClickListener(
					i));
			mainLayout.addView(v);
		}

	}

	private void setQuestionType() {
		mainLayout.removeAllViews();

		if (titleLayout.getChildCount() > 1)
			titleLayout.removeViewAt(1);
		View tv = BarGen.createTextView(R.string.emotion_manage_help2);
		titleLayout.addView(tv);

		for (int i = 0; i < related_texts.length; ++i) {
			View vv = BarGen.createIconView(related_texts[i], RELATED_DRAWABLE_ID[i], new RelatedOnClickListener(i));
			mainLayout.addView(vv);
		}

	}

	private String[] select_item;

	private void setQuestionEdit() {
		mainLayout.removeAllViews();

		if (titleLayout.getChildCount() > 1)
			titleLayout.removeViewAt(1);

		select_item = db.getEmotionManagementString(r_type);

		String str = getResources().getString(R.string.emotion_manage_help3) + related_texts[r_type];

		View tv = BarGen.createTextView(str);
		mainLayout.addView(tv);

		View edv = createEditView(r_type);
		mainLayout.addView(edv);

		View ev = BarGen.createTextView(R.string.emotion_manage_help4);
		mainLayout.addView(ev);

		View vv = BarGen.createIconView(R.string.ok, R.drawable.ok, new EditedOnClickListener());
		mainLayout.addView(vv);

	}

	private void setQuestionEnd() {
		mainLayout.removeAllViews();
		if (r_texts != null)
			reason = r_texts.getText().toString();

		if (titleLayout.getChildCount() > 1)
			titleLayout.removeViewAt(1);
		View tv = BarGen.createTextView(R.string.emotion_end_message);
		titleLayout.addView(tv);

		View vv = BarGen.createIconView(R.string.done, R.drawable.ok, new EndOnClickListener());
		mainLayout.addView(vv);

	}

	private View createEditView(int type) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_edit_item, null);

		EditText edit = (EditText) layout.findViewById(R.id.question_edit);
		edit.setTypeface(wordTypefaceBold);

		updown = (ImageView) layout.findViewById(R.id.question_list);
		if (select_item != null) {
			updown.setVisibility(View.VISIBLE);
			isShow = false;
			layout.setOnClickListener(new SelectionOnClickListener());
		}
		r_texts = edit;
		return layout;
	}

	private class ChangeTextOnClickListener implements View.OnClickListener {

		private String str;

		public ChangeTextOnClickListener(String str) {
			this.str = str;
		}

		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.EMOTION_MANAGE_LIST_TEXT);
			r_texts.setText(str);
			isShow = false;
			if (select_item != null) {
				mainLayout.removeViews(3, select_item.length);
				updown.setImageDrawable(downDrawable);
			}
		}

	}

	private class EndOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.EMOTION_MANAGE_SELECTION);
			
			
			int addScore = db.insertEmotionManagement(new EmotionManagement(System.currentTimeMillis(), curTV.year,
					curTV.month, curTV.day, emotion, r_type, reason, 0));
			
			if (PreferenceControl.checkCouponChange())
				PreferenceControl.setCouponChange(true);
			
			CustomToast.generateToast(R.string.emotion_manage_end_toast, addScore);
			activity.finish();
		}
	}

	private class EmotionOnClickListener implements View.OnClickListener {

		int _emotion;

		EmotionOnClickListener(int _emotion) {
			this._emotion = _emotion;
		}

		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.EMOTION_MANAGE_SELECTION);
			state = 1;
			emotion = _emotion;
			setQuestionType();
		}
	}

	private class RelatedOnClickListener implements View.OnClickListener {

		private int type;

		public RelatedOnClickListener(int type) {
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.EMOTION_MANAGE_SELECTION);
			state = 2;
			r_type = type;
			setQuestionEdit();
		}
	}

	private class EditedOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (r_texts.getText().toString().length() == 0) {
				CustomToastSmall.generateToast(R.string.emotion_manage_check_toast);
				return;
			}
			ClickLog.Log(ClickLogId.EMOTION_MANAGE_SELECTION);
			state = 3;
			setQuestionEnd();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ClickLog.Log(ClickLogId.EMOTION_MANAGE_RETURN);
			if (state == 0) {
				CustomToastSmall.generateToast(R.string.emotion_manage_toast);
				--state;
			} else if (state == -1)
				return super.onKeyDown(keyCode, event);
			else {
				--state;
				if (state == 0)
					setQuestionEmotion();
				else if (state == 1)
					setQuestionType();
				else if (state == 2)
					setQuestionEdit();
				else if (state == 3)
					setQuestionEnd();
			}
		}

		return false;
	}

	private boolean isShow = false;

	private class SelectionOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.EMOTION_MANAGE_SELECT_TEXT);
			if (select_item == null)
				return;
			if (isShow) {
				mainLayout.removeViews(3, select_item.length);
				updown.setImageDrawable(downDrawable);
			} else {
				for (int i = 0; i < select_item.length; ++i) {
					View vv = BarGen.createIconView(select_item[i], 0, new ChangeTextOnClickListener(select_item[i]));
					mainLayout.addView(vv, 3 + i);
				}
				updown.setImageDrawable(upDrawable);
			}
			isShow = !isShow;

		}

	}
}
