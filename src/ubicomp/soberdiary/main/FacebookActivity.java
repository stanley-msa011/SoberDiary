package ubicomp.soberdiary.main;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.FacebookInfo;
import ubicomp.soberdiary.main.ui.BarGen;
import ubicomp.soberdiary.main.ui.CustomToast;
import ubicomp.soberdiary.main.ui.CustomToastSmall;
import ubicomp.soberdiary.main.ui.LoadingDialogControl;
import ubicomp.soberdiary.main.ui.ScreenSize;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.storytelling.facebook.BitmapGenerator;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.config.PreferenceControl;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout.LayoutParams;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;

public class FacebookActivity extends Activity {

	private static final String TAG = "FACEBOOK";

	private RelativeLayout loginLayout, callLayout;
	private RelativeLayout bgLayout;
	private ScrollView inputScrollview;
	private LinearLayout inputLayout;
	private LoginButton authButton;

	private LayoutInflater inflater;

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

	private TextView titleText, loginText;
	private ImageView image;
	private Typeface wordTypeface, wordTypefaceBold;

	private int image_week, image_score;

	private EditText texts;
	private int sendGroup = 0;

	private View shareButton, inputMessage, guideMessage, privacySelection;

	private Bitmap state_bmp;

	private UiLifecycleHelper uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook);

		Bundle data = this.getIntent().getExtras();
		image_week = data.getInt("image_week", 0);
		image_score = data.getInt("image_score", 0);

		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		bgLayout = (RelativeLayout) this.findViewById(R.id.fb_main_layout);
		titleText = (TextView) this.findViewById(R.id.fb_title);
		image = (ImageView) this.findViewById(R.id.fb_input_image);
		inputLayout = (LinearLayout) this.findViewById(R.id.fb_input_layout);
		loginLayout = (RelativeLayout) this.findViewById(R.id.fb_login_layout);
		inputScrollview = (ScrollView) this.findViewById(R.id.facebook_scrollview);
		loginText = (TextView) this.findViewById(R.id.fb_login_message);

		fb_array = getResources().getStringArray(R.array.fb_selection);

		titleText.setTypeface(wordTypefaceBold);
		loginText.setTypeface(wordTypeface);

		authButton = (LoginButton) this.findViewById(R.id.authButton);
		authButton.setReadPermissions(Arrays.asList("basic_info", "read_friendlists"));
		authButton.setTypeface(wordTypefaceBold);

		state_bmp = BitmapGenerator.generateBitmap(image_week, image_score);

		Point screen = ScreenSize.getScreenSize();
		LinearLayout.LayoutParams imageParam = (android.widget.LinearLayout.LayoutParams) image.getLayoutParams();
		imageParam.width = screen.x;
		imageParam.height = screen.x * 597 / 567;

		image.setImageBitmap(state_bmp);

		guideMessage = BarGen.createTextView(R.string.fb_message);
		inputLayout.addView(guideMessage);

		inputMessage = createEditView();
		inputLayout.addView(inputMessage);

		privacySelection = createSendGroupView();
		inputLayout.addView(privacySelection);

		shareButton = BarGen.createIconView(R.string.fb_share, R.drawable.ok,
				new SendOnClickListener());

		inputLayout.addView(shareButton);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		Session.openActiveSession(this, true, callback);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.FACEBOOK_ENTER);
		enablePage(true);
		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		ClickLog.Log(ClickLogId.FACEBOOK_LEAVE);
		if (callLayout != null && callLayout.getParent() != null)
			bgLayout.removeView(callLayout);
		enablePage(true);
		uiHelper.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (state_bmp != null && !state_bmp.isRecycled()) {
			state_bmp.recycle();
		}
		uiHelper.onDestroy();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {
		super.onStop();
		uiHelper.onStop();
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.d(TAG, state.name() + " " + state.toString());
		if (state.isOpened()) {
			inputScrollview.setVisibility(View.VISIBLE);
			loginLayout.setVisibility(View.INVISIBLE);
		} else if (state.isClosed()) {
			inputScrollview.setVisibility(View.INVISIBLE);
			loginLayout.setVisibility(View.VISIBLE);
		} else {
			inputScrollview.setVisibility(View.INVISIBLE);
			loginLayout.setVisibility(View.VISIBLE);
		}
	}

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	private void publishStory() {
		Session session = Session.getActiveSession();

		if (session != null) {
			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this,
						PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			LoadingDialogControl.show(this, 1);

			Request.Callback callback = new Request.Callback() {
				@Override
				public void onCompleted(Response response) {
					boolean result = false;
					if (response != null) {
						GraphObject gobj = response.getGraphObject();
						if (gobj == null) {
							Log.d(TAG, "upload failed");
							CustomToastSmall.generateToast(R.string.fb_fail_toast);
						} else {
							JSONObject graphResponse = gobj.getInnerJSONObject();
							try {
								graphResponse.getString("id");
							} catch (Exception e) {
								Log.i(TAG, "upload exception" + e.getMessage());
							}
							FacebookRequestError error = response.getError();
							if (error == null) {
								result = true;
							}
						}
					}

					String text_msg = null;
					if (PreferenceControl.uploadFacebookInfo()) {
						if (texts != null && texts.getText() != null)
							text_msg = texts.getText().toString();
						else
							text_msg = "";
					}
					FacebookInfo info;

					DatabaseControl db = new DatabaseControl();

					if (result) {
						Log.d(TAG, "upload success");
						info = new FacebookInfo(System.currentTimeMillis(), image_week, image_score, text_msg, false,
								true, sendGroup, 0);
						int addScore = db.insertFacebookInfo(info);
						if (PreferenceControl.checkCouponChange())
							PreferenceControl.setCouponChange(true);
						CustomToast.generateToast(R.string.fb_success_toast, addScore);

					} else {
						Log.d(TAG, "upload failed");
						info = new FacebookInfo(System.currentTimeMillis(), image_week, image_score, text_msg, false,
								false, sendGroup, 0);
						db.insertFacebookInfo(info);
						CustomToastSmall.generateToast(R.string.fb_fail_toast);
					}
					LoadingDialogControl.dismiss();
					if (result)
						finish();
				}
			};

			Request request = Request.newUploadPhotoRequest(session, state_bmp, callback);
			Bundle params = request.getParameters();
			if (texts != null && texts.getText().length() > 0)
				params.putString("name", texts.getText() + "\n" + getString(R.string.app_name) + " "
						+ getString(R.string.homepage));
			else
				params.putString("name", getString(R.string.app_name) + " " + getString(R.string.homepage));

			JSONObject privacy = new JSONObject();
			try {
				switch (sendGroup) {
				case 0:
					privacy.put("value", "ALL_FRIENDS");
					break;
				case 1:
					privacy.put("value", "SELF");
					break;
				}
			} catch (JSONException e1) {
			}
			params.putString("privacy", privacy.toString());

			request.setParameters(params);
			request.executeAsync();

		}

	}

	private View createEditView() {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.question_edit_item, null);
		texts = (EditText) layout.findViewById(R.id.question_edit);
		texts.setTypeface(wordTypefaceBold);
		return layout;
	}

	private Spinner privacySpinner;

	private String[] fb_array;

	private View createSendGroupView() {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.facebook_send_group, null);

		TextView text = (TextView) layout.findViewById(R.id.fb_privacy_text);
		text.setTypeface(wordTypefaceBold);

		privacySpinner = (Spinner) layout.findViewById(R.id.fb_privacy_spinner);

		ArrayAdapter<CharSequence> timeAdapter = new ArrayAdapter<CharSequence>(getBaseContext(),
				R.layout.time_spinner, R.id.custom_spinner_text, fb_array);

		privacySpinner.setAdapter(timeAdapter);
		sendGroup = FacebookInfo.FRIEND;
		privacySpinner.setSelection(0);

		privacySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (sendGroup != position) {
					sendGroup = position;
					ClickLog.Log(ClickLogId.FACEBOOK_PRIVACY);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		return layout;
	}

	@SuppressLint("InlinedApi")
	private class SendOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			enablePage(false);

			ClickLog.Log(ClickLogId.FACEBOOK_SUBMIT);
			if (callLayout == null) {
				callLayout = (RelativeLayout) inflater.inflate(R.layout.call_check_layout, null);
				TextView callOK = (TextView) callLayout.findViewById(R.id.call_ok_button);
				TextView callCancel = (TextView) callLayout.findViewById(R.id.call_cancel_button);
				TextView callHelp = (TextView) callLayout.findViewById(R.id.call_help);
				callHelp.setTypeface(wordTypefaceBold);
				callOK.setTypeface(wordTypefaceBold);
				callCancel.setTypeface(wordTypefaceBold);

				callHelp.setText(R.string.fb_check);
				callOK.setOnClickListener(new CallOnClickListener());
				callCancel.setOnClickListener(new CallCancelOnClickListener());
			}

			bgLayout.addView(callLayout);
			RelativeLayout.LayoutParams boxParam = (RelativeLayout.LayoutParams) callLayout.getLayoutParams();
			boxParam.width = LayoutParams.MATCH_PARENT;
			boxParam.height = LayoutParams.MATCH_PARENT;
			boxParam.addRule(RelativeLayout.CENTER_IN_PARENT);

		}
	}

	private class CallOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.FACEBOOK_SUBMIT_OK);
			publishStory();
			bgLayout.removeView(callLayout);
			enablePage(true);
		}
	}

	private class CallCancelOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.FACEBOOK_SUBMIT_CANCEL);
			bgLayout.removeView(callLayout);
			enablePage(true);
		}
	}

	private void enablePage(boolean enable) {
		authButton.setEnabled(enable);
		shareButton.setEnabled(enable);
		texts.setEnabled(enable);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ClickLog.Log(ClickLogId.FACEBOOK_RETURN);
			if (callLayout != null && callLayout.getParent() != null){
				bgLayout.removeView(callLayout);
				enablePage(true);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
