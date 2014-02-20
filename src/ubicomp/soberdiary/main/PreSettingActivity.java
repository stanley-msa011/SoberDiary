package ubicomp.soberdiary.main;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.data.database.DatabaseRestore;
import ubicomp.soberdiary.system.config.PreferenceControl;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class PreSettingActivity extends Activity {

	private EditText uid, target_good, target, drink;

	private Button ok_button, clean_button, restoreButton, debugButton;
	boolean debug;
	private Activity activity;
	private static final int MIN_NAME_LENGTH = 3;

	private int mYear, mMonth, mDay;
	private int lYear, lMonth, lDay;

	private TextView mDateDisplay;
	private Button mPickDate;

	private CheckBox lDateCheckBox;
	private TextView lDateDisplay;
	private Button lPickDate;

	private TextView versionText;

	private String target_g;
	private int target_t, drink_t;

	private CheckBox developer_switch;

	private static final int DATE_DIALOG_ID = 0;
	private static final int LOCK_DIALOG_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pre_setting);
		activity = this;

		uid = (EditText) this.findViewById(R.id.uid_edit);
		uid.setText(PreferenceControl.getUID());

		developer_switch = (CheckBox) this.findViewById(R.id.developer_switch);
		developer_switch.setChecked(PreferenceControl.isDeveloper());

		target_good = (EditText) this.findViewById(R.id.target_good_edit);
		target_good.setText(PreferenceControl.getSavingGoal());

		target = (EditText) this.findViewById(R.id.target_money_edit);
		target.setText(String.valueOf(PreferenceControl.getSavingGoalMoney()));

		drink = (EditText) this.findViewById(R.id.target_drink_edit);
		drink.setText(String.valueOf(PreferenceControl.getSavingDrinkCost()));


		mDateDisplay = (TextView) findViewById(R.id.date);
		mPickDate = (Button) findViewById(R.id.date_button);

		int[] startDateData = PreferenceControl.getStartDateData();
		mYear = startDateData[0];
		mMonth = startDateData[1];
		mDay = startDateData[2];

		int[] lockDateData = PreferenceControl.getLockDateData();
		lYear = lockDateData[0];
		lMonth = lockDateData[1];
		lDay = lockDateData[2];

		ok_button = (Button) this.findViewById(R.id.uid_OK);
		ok_button.setOnClickListener(new OKOnclickListener());

		versionText = (TextView) this.findViewById(R.id.version);

		lDateCheckBox = (CheckBox) findViewById(R.id.system_lock);
		lDateDisplay = (TextView) findViewById(R.id.lock_date);
		lPickDate = (Button) findViewById(R.id.lock_button);

		lDateCheckBox.setChecked(PreferenceControl.isLocked());

		PackageInfo pinfo;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = pinfo.versionName;
			versionText.setText("Verstion: " + versionName);
		} catch (NameNotFoundException e) {
		}

		mPickDate.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		lPickDate.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				showDialog(LOCK_DIALOG_ID);
			}
		});

		updateDisplay();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("確定清除點數?");
		builder.setMessage("確定?");
		builder.setPositiveButton("確定", new CleanListener());
		builder.setNegativeButton("取消", null);
		AlertDialog cleanAlertDialog = builder.create();
		clean_button = (Button) this.findViewById(R.id.clean_OK);
		clean_button.setOnClickListener(new AlertOnClickListener(cleanAlertDialog));

		builder = new AlertDialog.Builder(this);
		builder.setTitle("Restore?");
		builder.setMessage("確定?");
		builder.setPositiveButton("確定", new RestoreOnClickListener());
		builder.setNegativeButton("取消", null);
		AlertDialog resotreAlertDialog = builder.create();
		restoreButton = (Button) this.findViewById(R.id.restore);
		restoreButton.setOnClickListener(new AlertOnClickListener(resotreAlertDialog));

		debug = PreferenceControl.isDebugMode();
		debugButton = (Button) this.findViewById(R.id.debug_normal_switch);

		if (debug) {
			debugButton.setText("Switch to normal mode");
		} else {
			debugButton.setText("Switch to debug mode");
		}
		debugButton.setOnClickListener(new DebugOnClickListener());

	}

	private class RestoreOnClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			DatabaseRestore rd = new DatabaseRestore(uid.getText().toString(), activity);
			rd.execute();
		}
	}

	private class DebugOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			debug = !debug;
			PreferenceControl.setDebugMode(debug);
			if (debug) {
				debugButton.setText("Switch to normal mode");
			} else {
				debugButton.setText("Switch to debug mode");
			}
		}
	}

	private class OKOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String text = uid.getText().toString();
			boolean check = true;
			if (text.length() < MIN_NAME_LENGTH)
				check = false;
			if (!text.startsWith("sober"))
				check = false;

			target_g = target_good.getText().toString();
			if (target_g.length() == 0)
				check = false;

			if (target.getText().toString().length() == 0)
				check = false;
			else {
				target_t = Integer.valueOf(target.getText().toString());
				if (target_t <= 0)
					check = false;
			}

			if (drink.getText().toString().length() == 0)
				check = false;
			else {
				drink_t = Integer.valueOf(drink.getText().toString());
				if (drink_t == 0)
					check = false;
			}

			if (check) {
				PreferenceControl.setUID(text);
				PreferenceControl.setIsDeveloper(developer_switch.isChecked());
				PreferenceControl.setGoal(target_g, target_t, drink_t);
				PreferenceControl.setStartDate(mYear, mMonth, mDay);

				PreferenceControl.setLocked(lDateCheckBox.isChecked());
				if (lDateCheckBox.isChecked()) {
					PreferenceControl.setLockDate(lYear, lMonth, lDay);
				}

				activity.finish();
			}

		}

	}

	private class AlertOnClickListener implements View.OnClickListener {

		private AlertDialog alertDialog;

		public AlertOnClickListener(AlertDialog ad) {
			this.alertDialog = ad;
		}

		@Override
		public void onClick(View v) {
			alertDialog.show();
		}
	}

	private class CleanListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			PreferenceControl.exchangeCoupon();
		}
	}

	private void updateDisplay() {
		this.mDateDisplay.setText(new StringBuilder().append(mMonth + 1).append("-").append(mDay).append("-")
				.append(mYear).append(" "));
		this.lDateDisplay.setText(new StringBuilder().append(lMonth + 1).append("-").append(lDay).append("-")
				.append(lYear).append(" "));
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	private DatePickerDialog.OnDateSetListener lDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			lYear = year;
			lMonth = monthOfYear;
			lDay = dayOfMonth;
			updateDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		case LOCK_DIALOG_ID:
			return new DatePickerDialog(this, lDateSetListener, lYear, lMonth, lDay);
		}
		return null;
	}
}
