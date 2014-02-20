package ubicomp.soberdiary.main;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.BarGen;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.ConnectSocialInfo;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingActivity extends Activity {

	private LayoutInflater inflater;

	private Typeface wordTypefaceBold;

	private LinearLayout titleLayout;
	private LinearLayout mainLayout;

	private Activity activity;
	
	ArrayAdapter<String> socialAdapter;
	ArrayAdapter<Integer> timeAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);


		this.activity = this;
		titleLayout = (LinearLayout) this.findViewById(R.id.setting_title_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.setting_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		mainLayout.removeAllViews();

		View title = BarGen.createTitleView(R.string.setting_title);
		titleLayout.addView(title);
		
		setting();

	}

	private View fbView;
	private View uvView;
	private RelativeLayout[] recreationViews;
	private EditText[] recreationEdits;
	private RelativeLayout[] contactViews;
	private EditText[] contactNameEdits;
	private EditText[] contactPhoneEdits;
	private RelativeLayout[] socialViews;
	private Spinner[] socialSpinners;
	private RelativeLayout notificationView;
	private Spinner notificationSpinner;
	
	private static final int PRIVACY = 0, RECREATION = 100, CONTACT = 200, SOCIAL = 300, OTHERS = 400;
	
	private void setting(){
		
		View privacyView = BarGen.createTextView(R.string.setting_privacy);
		mainLayout.addView(privacyView);
		privacyView.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + PRIVACY );
						if (fbView.getVisibility()==View.VISIBLE)
							fbView.setVisibility(View.GONE);
						else
							fbView.setVisibility(View.VISIBLE);
						if (uvView.getVisibility()==View.VISIBLE)
							uvView.setVisibility(View.GONE);
						else
							uvView.setVisibility(View.VISIBLE);
					}
		});
		
		
		fbView = createCheckBoxView(R.string.setting_facebook,
				new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						ClickLog.Log(ClickLogId.SETTING_CHECK + PRIVACY  + 0);
						PreferenceControl.setUploadFacebookInfo(isChecked);
					}
		},PreferenceControl.uploadFacebookInfo());
		fbView.setVisibility(View.GONE);
		mainLayout.addView(fbView);
		
		uvView = createCheckBoxView(R.string.setting_user_voice,
				new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						ClickLog.Log(ClickLogId.SETTING_CHECK + PRIVACY  + 1);
						PreferenceControl.setUploadVoiceRecord(isChecked);
					}
		},PreferenceControl.uploadVoiceRecord());
		uvView.setVisibility(View.GONE);
		mainLayout.addView(uvView);
		
		View recreationView = BarGen.createTextView(R.string.setting_recreation);
		recreationView.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + RECREATION );
						
						for (int i=0;i<recreationViews.length;++i)
							if (recreationViews[i].getVisibility() == View.VISIBLE)
								recreationViews[i].setVisibility(View.GONE);
							else
								recreationViews[i].setVisibility(View.VISIBLE);
					}
		});
		mainLayout.addView(recreationView);
		
		String[] recreations = PreferenceControl.getRecreations();
		recreationEdits = new EditText[recreations.length];
		recreationViews = new RelativeLayout[recreations.length];
		for (int i=0;i<recreations.length;++i){
			recreationViews[i] = createEditRecreationView(recreations[i]);
			recreationEdits[i] = (EditText) recreationViews[i].findViewById(R.id.question_edit);
			recreationViews[i].setVisibility(View.GONE);
			mainLayout.addView(recreationViews[i]);
		}
		
		View contactView = BarGen.createTextView(R.string.setting_contact);
		contactView.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + CONTACT );
						
						for (int i=0;i<contactViews.length;++i)
							if (contactViews[i].getVisibility() == View.VISIBLE)
								contactViews[i].setVisibility(View.GONE);
							else
								contactViews[i].setVisibility(View.VISIBLE);
					}
		});
		mainLayout.addView(contactView);
		
		String[] names = PreferenceControl.getConnectFamilyName();
		String[] phones = PreferenceControl.getConnectFamilyPhone();
		int contactLen = names.length;
		contactViews = new RelativeLayout[contactLen];
		contactNameEdits = new EditText[contactLen];
		contactPhoneEdits = new EditText[contactLen];
		for (int i=0;i<contactLen;++i){
			contactViews[i] = createEditPhoneView(names[i],phones[i]);
			contactNameEdits[i] = (EditText) contactViews[i].findViewById(R.id.question_name);
			contactPhoneEdits[i] = (EditText) contactViews[i].findViewById(R.id.question_phone);
			contactViews[i].setVisibility(View.GONE);
			mainLayout.addView(contactViews[i]);
		}
		
		socialAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ConnectSocialInfo.NAME);
		socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		int[] socialSelections = PreferenceControl.getConnectSocialHelpIdx();
		int sLen = socialSelections.length;
		socialViews = new RelativeLayout[sLen];
		socialSpinners = new Spinner[sLen];
		View socialView = BarGen.createTextView(R.string.setting_social);
		socialView.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + SOCIAL );
						for (int i=0;i<socialViews.length;++i)
							if (socialViews[i].getVisibility() == View.VISIBLE)
								socialViews[i].setVisibility(View.GONE);
							else
								socialViews[i].setVisibility(View.VISIBLE);
					}
		});
		mainLayout.addView(socialView);
		
		for (int i=0;i<sLen;++i){
			socialViews[i] = createSpinnerView(socialSelections[i]);
			socialSpinners[i] = (Spinner) socialViews[i].findViewById(R.id.question_spinner);
			socialViews[i].setVisibility(View.GONE);
			mainLayout.addView(socialViews[i]);
		}
		
		View otherView = BarGen.createTextView(R.string.setting_others);
		mainLayout.addView(otherView);
		otherView.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v) {
						ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + OTHERS );
						
						if (notificationView.getVisibility()==View.VISIBLE)
							notificationView.setVisibility(View.GONE);
						else
							notificationView.setVisibility(View.VISIBLE);
					}
		});
		
		timeAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, new Integer[]{30,60,120});
		timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		notificationView = this.createTextSpinnerView(R.string.setting_notification_time, PreferenceControl.getNotificationTimeIdx());
		mainLayout.addView(notificationView);
		notificationView.setVisibility(View.GONE);
		notificationSpinner = (Spinner) notificationView.findViewById(R.id.question_spinner);
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.SETTING_ENTER);
	}

	@Override
	protected void onPause() {
		String[] recreation_strs = new String[recreationEdits.length];
		for (int i=0;i<recreation_strs.length;++i)
			recreation_strs[i] = recreationEdits[i].getText().toString();
		PreferenceControl.setRecreations(recreation_strs);
		
		String[] cName_strs = new String[contactNameEdits.length];
		String[] cPhone_strs = new String[contactPhoneEdits.length];
		for (int i=0;i<cName_strs.length;++i){
			cName_strs[i] = contactNameEdits[i].getText().toString();
			cPhone_strs[i] = contactPhoneEdits[i].getText().toString();
		}
		PreferenceControl.setFamilyCallData(cName_strs, cPhone_strs);
		
		int[] socialSelections = new int[socialSpinners.length];
		for (int i=0;i<socialSelections.length;++i)
			socialSelections[i] = socialSpinners[i].getSelectedItemPosition();
		PreferenceControl.setConnectSocialHelpIdx(socialSelections);
		
		int time_idx = notificationSpinner.getSelectedItemPosition();
		PreferenceControl.setNotificationTimeIdx(time_idx);
		
		ClickLog.Log(ClickLogId.SETTING_LEAVE);
		
		BootBoardcastReceiver.testNotificationSetting(getBaseContext(), getIntent());
		
		super.onPause();
	}
	
	

	private RelativeLayout createEditRecreationView(String defaultText) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_edit_recreation_item, null);

		EditText edit = (EditText) layout.findViewById(R.id.question_edit);
		edit.setTypeface(wordTypefaceBold);
		edit.setText(defaultText);
		edit.setOnKeyListener(
				new OnKeyListener(){
					@Override
					public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
						ClickLog.Log(ClickLogId.SETTING_EDIT+RECREATION);
						return false;
					}
				});
		
		return layout;
	}
	
	private RelativeLayout createEditPhoneView(String defaultName,String defaultPhone) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_edit_contact_item, null);

		TextView nt = (TextView) layout.findViewById(R.id.question_name_text);
		nt.setTypeface(wordTypefaceBold);
		TextView pt = (TextView) layout.findViewById(R.id.question_phone_text);
		pt.setTypeface(wordTypefaceBold);
		
		EditText name = (EditText) layout.findViewById(R.id.question_name);
		name.setTypeface(wordTypefaceBold);
		name.setText(defaultName);
		name.setOnKeyListener(
				new OnKeyListener(){
					@Override
					public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
						ClickLog.Log(ClickLogId.SETTING_EDIT+CONTACT + 0);
						return false;
					}
				});
		
		EditText edit = (EditText) layout.findViewById(R.id.question_phone);
		edit.setTypeface(wordTypefaceBold);
		edit.setText(defaultPhone);
		name.setOnKeyListener(
				new OnKeyListener(){
					@Override
					public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
						ClickLog.Log(ClickLogId.SETTING_EDIT+CONTACT + 1);
						return false;
					}
				});
		
		return layout;
	}
	
	private RelativeLayout createSpinnerView(int defaultSelection) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_spinner_item, null);

		Spinner spinner = (Spinner) layout.findViewById(R.id.question_spinner);
		spinner.setAdapter(socialAdapter);
		spinner.setSelection(defaultSelection);
		
		spinner.setOnItemSelectedListener(
				new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						ClickLog.Log(ClickLogId.SETTING_SPINNER+SOCIAL);
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {}
				});
		
		return layout;
	}
	
	private RelativeLayout createTextSpinnerView(int str_id,int defaultSelection) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_text_spinner_item, null);

		TextView text = (TextView)layout.findViewById(R.id.question_description);
		text.setText(str_id);
		text.setTypeface(wordTypefaceBold);
		
		Spinner spinner = (Spinner) layout.findViewById(R.id.question_spinner);
		spinner.setAdapter(timeAdapter);
		spinner.setSelection(defaultSelection);
		
		spinner.setOnItemSelectedListener(
				new OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						ClickLog.Log(ClickLogId.SETTING_SPINNER+OTHERS+0);
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {}
				});
		
		return layout;
	}

	private View createCheckBoxView(int str_id,OnCheckedChangeListener listener,boolean defaultCheck){
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_checkbox_item, null);

		TextView text = (TextView)layout.findViewById(R.id.question_description);
		text.setText(str_id);
		text.setTypeface(wordTypefaceBold);
		
		CheckBox check = (CheckBox) layout.findViewById(R.id.question_check);
		check.setOnCheckedChangeListener(listener);
		check.setChecked(defaultCheck);
		return layout;
	}

}
