package ubicomp.soberdiary.main;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.BarButtonGenerator;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.main.ui.spinnergroup.MultiRadioGroup;
import ubicomp.soberdiary.main.ui.spinnergroup.SingleRadioGroup;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.ConnectSocialInfo;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Activity for normal user setting
 * 
 * @author Stanley Wang
 */
public class SettingActivity extends Activity {

	private LayoutInflater inflater;

	private Typeface wordTypeface;
	private Typeface wordTypefaceBold;

	private LinearLayout titleLayout;
	private LinearLayout mainLayout;

	private Activity activity;

	private View fbView;
	private View uvView;
	private RelativeLayout[] recreationViews;
	private RelativeLayout[] contactViews;
	private MultiRadioGroup socialGroup;
	private View socialGroupView;
	private SingleRadioGroup notificationGroup;
	private View notificationGroupView;
	private View bluetoothView;

	private static final int PRIVACY = 0, RECREATION = 100, CONTACT = 200, SOCIAL = 300, ALARM = 400, SYSTEM = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		this.activity = this;
		titleLayout = (LinearLayout) this.findViewById(R.id.setting_title_layout);
		mainLayout = (LinearLayout) this.findViewById(R.id.setting_main_layout);
		inflater = LayoutInflater.from(activity);
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		mainLayout.removeAllViews();

		View title = BarButtonGenerator.createTitleView(R.string.setting_title);
		titleLayout.addView(title);

		setting();

	}

	private void setting() {

		RelativeLayout privacyView = createListView(R.string.setting_privacy, new OnClickListener() {
			private boolean visible = false;

			@Override
			public void onClick(View v) {
				ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + PRIVACY);
				ImageView list = (ImageView) v.findViewById(R.id.question_list);
				if (visible) {
					fbView.setVisibility(View.GONE);
					uvView.setVisibility(View.GONE);
					list.setVisibility(View.INVISIBLE);
				} else {
					fbView.setVisibility(View.VISIBLE);
					uvView.setVisibility(View.VISIBLE);
					list.setVisibility(View.VISIBLE);
				}
				visible = !visible;
			}
		});
		mainLayout.addView(privacyView);

		fbView = createCheckBoxView(R.string.setting_facebook, new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				ClickLog.Log(ClickLogId.SETTING_CHECK + PRIVACY + 0);
				boolean isChecked = (checkedId == R.id.question_check_yes);
				PreferenceControl.setUploadFacebookInfo(isChecked);
			}
		}, PreferenceControl.uploadFacebookInfo());
		fbView.setVisibility(View.GONE);
		mainLayout.addView(fbView);

		uvView = createCheckBoxView(R.string.setting_user_voice, new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				ClickLog.Log(ClickLogId.SETTING_CHECK + PRIVACY + 1);
				boolean isChecked = (checkedId == R.id.question_check_yes);
				PreferenceControl.setUploadVoiceRecord(isChecked);
			}
		}, PreferenceControl.uploadVoiceRecord());
		uvView.setVisibility(View.GONE);
		mainLayout.addView(uvView);

		RelativeLayout recreationView = createListView(R.string.setting_recreation, new OnClickListener() {

			private boolean visible = false;

			@Override
			public void onClick(View v) {
				ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + RECREATION);
				ImageView list = (ImageView) v.findViewById(R.id.question_list);
				if (visible) {
					for (int i = 0; i < recreationViews.length; ++i)
						recreationViews[i].setVisibility(View.GONE);
					list.setVisibility(View.INVISIBLE);
				} else {
					for (int i = 0; i < recreationViews.length; ++i)
						recreationViews[i].setVisibility(View.VISIBLE);
					list.setVisibility(View.VISIBLE);
				}
				visible = !visible;
			}
		});
		mainLayout.addView(recreationView);

		String[] recreations = PreferenceControl.getRecreations();
		recreationViews = new RelativeLayout[recreations.length];
		for (int i = 0; i < recreations.length; ++i) {
			recreationViews[i] = createEditRecreationView(recreations[i], i);
			recreationViews[i].setVisibility(View.GONE);
			mainLayout.addView(recreationViews[i]);
		}

		RelativeLayout contactView = createListView(R.string.setting_contact, new OnClickListener() {
			private boolean visible = false;

			@Override
			public void onClick(View v) {
				ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + CONTACT);
				ImageView list = (ImageView) v.findViewById(R.id.question_list);
				if (visible) {
					for (int i = 0; i < contactViews.length; ++i)
						contactViews[i].setVisibility(View.GONE);
					list.setVisibility(View.INVISIBLE);
				} else {
					for (int i = 0; i < contactViews.length; ++i)
						contactViews[i].setVisibility(View.VISIBLE);
					list.setVisibility(View.VISIBLE);
				}
				visible = !visible;
			}

		});
		mainLayout.addView(contactView);

		String[] names = PreferenceControl.getConnectFamilyName();
		String[] phones = PreferenceControl.getConnectFamilyPhone();
		int contactLen = names.length;
		contactViews = new RelativeLayout[contactLen];
		for (int i = 0; i < contactLen; ++i) {
			contactViews[i] = createEditPhoneView(names[i], phones[i], i);
			contactViews[i].setVisibility(View.GONE);
			mainLayout.addView(contactViews[i]);
		}

		int[] socialSelections = PreferenceControl.getConnectSocialHelpIdx();
		boolean[] socialSelected = new boolean[ConnectSocialInfo.NAME.length];
		for (int i = 0; i < socialSelected.length; ++i) {
			socialSelected[i] = false;
			for (int j = 0; j < socialSelections.length; ++j)
				if (i == socialSelections[j])
					socialSelected[i] = true;
		}

		socialGroup = new MultiRadioGroup(activity, ConnectSocialInfo.NAME, socialSelected, 3, R.string.setting_limit,
				ClickLogId.SETTING_SELECT + SOCIAL);
		socialGroupView = socialGroup.getView();

		View socialView = createListView(R.string.setting_social, new OnClickListener() {
			private boolean visible = false;

			@Override
			public void onClick(View v) {
				ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + SOCIAL);
				ImageView list = (ImageView) v.findViewById(R.id.question_list);
				if (visible) {
					socialGroupView.setVisibility(View.GONE);
					list.setVisibility(View.INVISIBLE);
				} else {
					socialGroupView.setVisibility(View.VISIBLE);
					list.setVisibility(View.VISIBLE);
				}
				visible = !visible;
			}
		});
		mainLayout.addView(socialView);

		mainLayout.addView(socialGroupView);
		socialGroupView.setVisibility(View.GONE);

		String[] strs = App.getContext().getResources().getStringArray(R.array.setting_time_gap);
		notificationGroup = new SingleRadioGroup(activity, strs, PreferenceControl.getNotificationTimeIdx(),
				ClickLogId.SETTING_SELECT + ALARM);
		notificationGroupView = notificationGroup.getView();
		notificationGroupView.setVisibility(View.GONE);

		RelativeLayout alarmView = createListView(R.string.setting_alarm, new OnClickListener() {
			private boolean visible = false;

			@Override
			public void onClick(View v) {
				ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + ALARM);
				ImageView list = (ImageView) v.findViewById(R.id.question_list);
				if (visible) {
					notificationGroupView.setVisibility(View.GONE);
					list.setVisibility(View.INVISIBLE);
				} else {
					notificationGroupView.setVisibility(View.VISIBLE);
					list.setVisibility(View.VISIBLE);
				}
				visible = !visible;
			}

		});
		mainLayout.addView(alarmView);
		mainLayout.addView(notificationGroupView);

		bluetoothView = BarButtonGenerator.createSettingButtonView(R.string.setting_bluetooth, new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClickLog.Log(ClickLogId.SETTING_CHECK + SYSTEM);
				Intent intentBluetooth = new Intent();
				intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
				startActivity(intentBluetooth);
			}

		});
		bluetoothView.setVisibility(View.GONE);

		RelativeLayout systemView = createListView(R.string.setting_system, new OnClickListener() {
			private boolean visible = false;

			@Override
			public void onClick(View v) {
				ClickLog.Log(ClickLogId.SETTING_TITLE_LIST + SYSTEM);
				ImageView list = (ImageView) v.findViewById(R.id.question_list);
				if (visible) {
					bluetoothView.setVisibility(View.GONE);
					list.setVisibility(View.INVISIBLE);
				} else {
					bluetoothView.setVisibility(View.VISIBLE);
					list.setVisibility(View.VISIBLE);
				}
				visible = !visible;
			}

		});
		mainLayout.addView(systemView);
		mainLayout.addView(bluetoothView);

	}

	@Override
	protected void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.SETTING_ENTER);
	}

	@Override
	protected void onPause() {

		int[] socialSelections = new int[3];
		int social_idx = 0;
		boolean[] socialSelected = socialGroup.getResult();
		for (int i = 0; i < socialSelected.length; ++i) {
			if (socialSelected[i])
				socialSelections[social_idx++] = i;
		}
		PreferenceControl.setConnectSocialHelpIdx(socialSelections);

		PreferenceControl.setNotificationTimeIdx(notificationGroup.getResult());

		ClickLog.Log(ClickLogId.SETTING_LEAVE);

		BootBoardcastReceiver.setRegularNotification(getBaseContext(), getIntent());

		super.onPause();
	}

	private RelativeLayout createListView(int titleStr, OnClickListener listener) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.bar_list_item, null);
		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setTypeface(wordTypefaceBold);
		text.setText(titleStr);
		layout.setOnClickListener(listener);
		return layout;
	}

	private RelativeLayout createEditRecreationView(String defaultText, int id) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.bar_edit_recreation_item, null);

		TextView text = (TextView) layout.findViewById(R.id.question_text);
		text.setTypeface(wordTypeface);
		text.setText(defaultText);

		EditText edit = (EditText) layout.findViewById(R.id.question_edit);
		edit.setTypeface(wordTypefaceBold);
		edit.setText(defaultText);
		edit.setVisibility(View.INVISIBLE);

		TextView button = (TextView) layout.findViewById(R.id.question_button);
		button.setTypeface(wordTypefaceBold);
		button.setOnClickListener(new RecreationOnClickListener(text, edit, button, id));

		return layout;
	}

	private class RecreationOnClickListener implements View.OnClickListener {
		private boolean editable = false;

		private TextView text;
		private EditText editText;
		private TextView button;
		private int id;

		private String ok = App.getContext().getString(R.string.ok);
		private String edit = App.getContext().getString(R.string.edit);

		private int ok_color = App.getContext().getResources().getColor(R.color.lite_orange);
		private int edit_color = App.getContext().getResources().getColor(R.color.text_gray);

		public RecreationOnClickListener(TextView text, EditText editText, TextView button, int id) {
			this.text = text;
			this.editText = editText;
			this.button = button;
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.SETTING_EDIT + RECREATION);
			if (editable) {
				String recreation = editText.getText().toString();
				text.setText(recreation);
				text.setVisibility(View.VISIBLE);
				editText.setVisibility(View.INVISIBLE);
				button.setText(edit);
				button.setTextColor(edit_color);
				PreferenceControl.setRecreation(recreation, id);
			} else {
				text.setVisibility(View.INVISIBLE);
				editText.setText(text.getText());
				editText.setVisibility(View.VISIBLE);
				button.setText(ok);
				button.setTextColor(ok_color);
			}
			editable = !editable;
		}

	}

	private RelativeLayout createEditPhoneView(String defaultName, String defaultPhone, int id) {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.bar_edit_contact_item, null);

		TextView nt = (TextView) layout.findViewById(R.id.question_name_text);
		nt.setTypeface(wordTypeface);
		nt.setText(defaultName);
		TextView pt = (TextView) layout.findViewById(R.id.question_phone_text);
		pt.setTypeface(wordTypeface);
		pt.setText(defaultPhone);

		EditText name = (EditText) layout.findViewById(R.id.question_name);
		name.setTypeface(wordTypefaceBold);
		name.setVisibility(View.INVISIBLE);
		EditText phone = (EditText) layout.findViewById(R.id.question_phone);
		phone.setTypeface(wordTypefaceBold);
		phone.setVisibility(View.INVISIBLE);

		TextView button = (TextView) layout.findViewById(R.id.question_button);
		button.setTypeface(wordTypefaceBold);
		button.setOnClickListener(new PhoneOnClickListener(nt, pt, name, phone, button, id));

		return layout;
	}

	private class PhoneOnClickListener implements View.OnClickListener {
		private boolean editable = false;

		private TextView nt, pt;
		private EditText name, phone;
		private TextView button;
		private int id;

		private String ok = App.getContext().getString(R.string.ok);
		private String edit = App.getContext().getString(R.string.edit);

		private int ok_color = App.getContext().getResources().getColor(R.color.lite_orange);
		private int edit_color = App.getContext().getResources().getColor(R.color.text_gray);

		public PhoneOnClickListener(TextView nt, TextView pt, EditText name, EditText phone, TextView button, int id) {
			this.nt = nt;
			this.pt = pt;
			this.name = name;
			this.phone = phone;
			this.button = button;
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.SETTING_EDIT + CONTACT);
			if (editable) {
				String name_text = name.getText().toString();
				nt.setText(name_text);
				nt.setVisibility(View.VISIBLE);
				name.setVisibility(View.INVISIBLE);
				String phone_text = phone.getText().toString();
				pt.setText(phone_text);
				pt.setVisibility(View.VISIBLE);
				phone.setVisibility(View.INVISIBLE);
				button.setText(edit);
				button.setTextColor(edit_color);
				PreferenceControl.setFamilyCallData(name_text, phone_text, id);
			} else {
				nt.setVisibility(View.INVISIBLE);
				pt.setVisibility(View.INVISIBLE);
				name.setText(nt.getText());
				name.setVisibility(View.VISIBLE);
				phone.setText(pt.getText());
				phone.setVisibility(View.VISIBLE);
				button.setText(ok);
				button.setTextColor(ok_color);
			}
			editable = !editable;
		}

	}

	private View createCheckBoxView(int str_id, OnCheckedChangeListener listener, boolean defaultCheck) {
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.bar_checkbox_item, null);

		TextView text = (TextView) layout.findViewById(R.id.question_description);
		text.setText(str_id);
		text.setTypeface(wordTypeface);

		RadioGroup radio = (RadioGroup) layout.findViewById(R.id.question_check);
		RadioButton yes = (RadioButton) layout.findViewById(R.id.question_check_yes);
		RadioButton no = (RadioButton) layout.findViewById(R.id.question_check_no);
		yes.setTypeface(wordTypefaceBold);
		no.setTypeface(wordTypefaceBold);

		if (defaultCheck)
			radio.check(R.id.question_check_yes);
		else
			radio.check(R.id.question_check_no);

		radio.setOnCheckedChangeListener(listener);

		return layout;
	}

}
