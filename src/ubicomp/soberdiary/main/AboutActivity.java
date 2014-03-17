package ubicomp.soberdiary.main;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.CustomTypefaceSpan;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.main.ui.toast.CustomToastSmall;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class AboutActivity extends Activity {

	private TextView titleText,aboutText, copyrightText, about, phone, phone_number, email;
	
	private ImageView logo,logo0,logo1,logo2;
	private Typeface wordTypeface,wordTypefaceBold,digitTypeface,digitTypefaceBold;
	
	private static final String EMAIL = "ubicomplab.ntu@gmail.com";
	private static final String COPYRIGHT  = "\u00a9 2014 National Taiwan University,Intel-NTU Connected Context Computing Center, and Taipei City Hospital";
	
	private int hidden_state;
	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		activity = this;
		
		
		wordTypeface = Typefaces.getWordTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		digitTypeface = Typefaces.getDigitTypeface();
		digitTypefaceBold = Typefaces.getDigitTypefaceBold();
		bgLayout = (RelativeLayout) this.findViewById(R.id.about_background_layout);
		titleText = (TextView) this.findViewById(R.id.about_title);
		phone = (TextView)this.findViewById(R.id.about_phone);
		phone_number = (TextView)this.findViewById(R.id.about_phone_number);
		email = (TextView)this.findViewById(R.id.about_email);
		about = (TextView) this.findViewById(R.id.about_about);
		aboutText = (TextView) this.findViewById(R.id.about_content);
		logo = (ImageView) this.findViewById(R.id.about_logo);
		logo0 = (ImageView) this.findViewById(R.id.about_logo0);
		logo1 = (ImageView) this.findViewById(R.id.about_logo1);
		logo2 = (ImageView) this.findViewById(R.id.about_logo2);
		copyrightText = (TextView) this.findViewById(R.id.about_copyright);
		
		titleText.setTypeface(wordTypefaceBold);
		about.setTypeface(wordTypefaceBold);
		aboutText.setTypeface(wordTypefaceBold);
		phone.setTypeface(wordTypeface);
		phone_number.setTypeface(digitTypefaceBold);
		email.setTypeface(digitTypefaceBold);
		copyrightText.setTypeface(digitTypeface);
		copyrightText.setText(COPYRIGHT);
		
		
		logo.setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (hidden_state == 0)
							++hidden_state;
						else if (hidden_state ==4){
							Intent newIntent = new Intent(activity, DeveloperActivity.class);
							activity.startActivity(newIntent);
						}
						else 
							hidden_state = 0;
						return false;
					}
				}
				);
		logo0.setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN){
							if (hidden_state == 1)
								++hidden_state;
							else 
								hidden_state = 0;
						}
						return false;
					}
				}
				);
		logo1.setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN){
							if (hidden_state == 2)
								++hidden_state;
							else 
								hidden_state = 0;
						}
						return false;
					}
				}
				);
		logo2.setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN){
							if (hidden_state == 3)
								++hidden_state;
							else 
								hidden_state = 0;
						}
						return false;
					}
				}
				);
		
		String[] message = getResources().getStringArray(R.array.about_message);
		String ntu = getString(R.string.ntu);
		String dot = getString(R.string.dot);
		String intel_ntu = getString(R.string.intel_ntu);
		String taipei_city_hospital = getString(R.string.taipei_city_hospital);
		String happ_design = getString(R.string.happ_design);
		
		String curVersion = getString(R.string.current_version);
		String rickie_wu = getString(R.string.rickie_wu);
		String yuga_huang = getString(R.string.yuda_huang);
		String versionName =" unknown";
		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {}
		
    	Spannable helpSpannable = new SpannableString(
    			message[0]+"\n"+
    			ntu+dot+intel_ntu+dot+taipei_city_hospital+
    			message[1]+"\n\n"+message[2]+"\n"+message[3]+"\n"+curVersion+
    			versionName+"\n\n"+
    			message[4]+
    			happ_design+"\n"+
    			message[5]+rickie_wu+"\n"+
    			message[6]+yuga_huang+"\n"
    			);
		int start= 0;
		int end =message[0].length()+1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start=end;
		end = start+ntu.length()+dot.length()+intel_ntu.length()+dot.length()+taipei_city_hospital.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom2",wordTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start+message[1].length()+2+message[2].length()+1+message[3].length()+1+curVersion.length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + versionName.length()+2;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + message[4].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + happ_design.length()+1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + message[5].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + rickie_wu.length()+1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + message[6].length();
		helpSpannable.setSpan(new CustomTypefaceSpan("custom1",wordTypeface,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		start = end;
		end = start + yuga_huang.length()+1;
		helpSpannable.setSpan(new CustomTypefaceSpan("custom3",digitTypefaceBold,0xFF727171), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		
		aboutText.setText(helpSpannable);
		
		inflater = LayoutInflater.from(this);
		callLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_callout_check, null);
		setCallCheckBox();
		
		phone_number.setOnClickListener(new CallCheckOnClickListener());
		email.setOnClickListener(new EmailOnClickListener());
	}

	private LayoutInflater inflater;
	private RelativeLayout callLayout;
	private RelativeLayout bgLayout;
	private TextView callOK,callCancel,callHelp;
	
	private void setCallCheckBox(){
		
		callOK = (TextView) callLayout.findViewById(R.id.call_ok_button);
		callCancel = (TextView) callLayout.findViewById(R.id.call_cancel_button);
		callHelp = (TextView) callLayout.findViewById(R.id.call_help);
		
		callHelp.setTypeface(wordTypefaceBold);
		callOK.setTypeface(wordTypefaceBold);
		callCancel.setTypeface(wordTypefaceBold);
	}
	
	private class CallCheckOnClickListener  implements View.OnClickListener{
		
		@Override
		public void onClick(View v) {
			bgLayout.addView(callLayout);
			
			RelativeLayout.LayoutParams boxParam = (RelativeLayout.LayoutParams) callLayout.getLayoutParams();
			boxParam.width = LayoutParams.MATCH_PARENT;
			boxParam.height = LayoutParams.MATCH_PARENT;
			
			callHelp.setText(R.string.phone_check);
			callOK.setOnClickListener(new CallOnClickListener());
			callCancel.setOnClickListener(new CallCancelOnClickListener());
			phone_number.setOnClickListener(null);
			email.setOnClickListener(null);
			ClickLog.Log(ClickLogId.ABOUT_CALL);
		}
	}
	
	private class CallCancelOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			bgLayout.removeView(callLayout);
			phone_number.setOnClickListener(new CallCheckOnClickListener());
			email.setOnClickListener(new EmailOnClickListener());
			ClickLog.Log(ClickLogId.ABOUT_CALL_CANCEL);
		}
		
	}
	
	private class CallOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.ABOUT_CALL_OK);
			Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:0233664926"));
			activity.startActivity(intentDial);
			activity.finish();
		}
	}
	
	
private class EmailOnClickListener  implements View.OnClickListener{
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{EMAIL});
			
			String uid = PreferenceControl.getUID();
			if (uid.equals( "sober_default_test")){
				CustomToastSmall.generateToast(R.string.email_reject);
				return;
			}
			i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject)+" "+uid);
			try {
			    startActivity(Intent.createChooser(i, getString(R.string.email_message)));
			} catch (android.content.ActivityNotFoundException ex) {
				CustomToastSmall.generateToast(R.string.email_fail);
			}
			ClickLog.Log(ClickLogId.ABOUT_EMAIL);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK ){
			if (callLayout.getParent()!=null && callLayout.getParent().equals(bgLayout)){
				bgLayout.removeView(callLayout);
				phone_number.setOnClickListener(new CallCheckOnClickListener());
				email.setOnClickListener(new EmailOnClickListener());
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		ClickLog.Log(ClickLogId.ABOUT_ENTER);
	}
	@Override
	public void onPause(){
		ClickLog.Log(ClickLogId.ABOUT_LEAVE);
		super.onPause();
	}
	
}