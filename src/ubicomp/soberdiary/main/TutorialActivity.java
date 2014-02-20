package ubicomp.soberdiary.main;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.ScaleOnTouchListener;
import ubicomp.soberdiary.main.ui.ScreenSize;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class TutorialActivity extends Activity {

	private ImageView replay, arrow;
	private ImageView tab;
	private Drawable[] arrowDrawables;

	private TextView step;
	private TextView help;
	private TextView notify;

	private LoadingHandler loadingHandler;
	private static Point screen;

	private RelativeLayout layout;
	private Typeface digitTypeface;
	private Typeface wordTypefaceBold;

	private static final String[] STEP_STR = { "1", "2", "3" };
	private static String[] HELP_STR;

	private AlphaAnimation animation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		screen = ScreenSize.getScreenSize();

		digitTypeface = Typefaces.getDigitTypeface();
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		replay = (ImageView) this.findViewById(R.id.tutorial_replay);
		replay.setOnTouchListener(new ScaleOnTouchListener());
		arrow = (ImageView) this.findViewById(R.id.tutorial_arrow);

		step = (TextView) this.findViewById(R.id.tutorial_step);
		step.setTypeface(digitTypeface);

		notify = (TextView) this.findViewById(R.id.tutorial_notify);
		notify.setTypeface(wordTypefaceBold);

		help = (TextView) this.findViewById(R.id.tutorial_help);
		help.setTypeface(wordTypefaceBold);
		RelativeLayout.LayoutParams hParam = (RelativeLayout.LayoutParams) help.getLayoutParams();
		hParam.topMargin = screen.y * 486 / 854;

		tab = (ImageView) this.findViewById(R.id.tutorial_tab);
		layout = (RelativeLayout) this.findViewById(R.id.tutorial_layout);

		HELP_STR = getResources().getStringArray(R.array.tutorial_step);

		loadingHandler = new LoadingHandler();
	}

	private ProgressDialog mDialog;

	protected void onStart() {
		mDialog = new ProgressDialog(this);
		mDialog.setMessage(this.getResources().getString(R.string.loading));
		mDialog.setCancelable(false);
		if (!mDialog.isShowing())
			mDialog.show();
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.TUTORIAL_ENTER);
		loadingHandler.sendEmptyMessage(0);
	}

	protected void onPause() {
		loadingHandler.removeMessages(0);
		if (animation != null) {
			if (Build.VERSION.SDK_INT >= 8)
				animation.cancel();
		}
		if (arrow != null) {
			arrow.setAnimation(null);
		}
		super.onPause();
		ClickLog.Log(ClickLogId.TUTORIAL_LEAVE);
	}

	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler {
		public void handleMessage(Message msg) {

			arrowDrawables = new Drawable[3];
			arrowDrawables[0] = getResources().getDrawable(R.drawable.tutorial_arrow1);
			arrowDrawables[1] = getResources().getDrawable(R.drawable.tutorial_arrow2);
			arrowDrawables[2] = getResources().getDrawable(R.drawable.tutorial_arrow3);

			animation = new AlphaAnimation(1.F, 0.F);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setRepeatMode(Animation.REVERSE);
			animation.setDuration(300);
			arrow.setAnimation(animation);

			settingState(0);
			if (mDialog != null && mDialog.isShowing())
				mDialog.dismiss();

		}
	}

	private void settingState(int state) {
		step.setText(STEP_STR[state]);
		help.setText(HELP_STR[state]);

		RelativeLayout.LayoutParams aParam = (LayoutParams) arrow.getLayoutParams();
		aParam.addRule(RelativeLayout.RIGHT_OF, 0);
		aParam.addRule(RelativeLayout.ABOVE, 0);
		aParam.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
		if (state == 0) {
			layout.setOnClickListener(new Listener(0));
			replay.setOnClickListener(null);
			replay.setVisibility(View.INVISIBLE);
			tab.setVisibility(View.INVISIBLE);
			arrow.setImageDrawable(arrowDrawables[0]);
			aParam.addRule(RelativeLayout.RIGHT_OF, help.getId());
			aParam.topMargin = screen.y * 550 / 854;

			aParam.leftMargin = screen.x * 10 / 480;
			animation.start();
		} else if (state == 1) {
			layout.setOnClickListener(new Listener(1));
			replay.setOnClickListener(null);
			replay.setVisibility(View.INVISIBLE);
			tab.setVisibility(View.VISIBLE);
			arrow.setImageDrawable(arrowDrawables[1]);
			aParam.addRule(RelativeLayout.ABOVE, tab.getId());
			aParam.topMargin = 0;
			aParam.leftMargin = screen.x * 40 / 480;
			animation.start();
		} else if (state == 2) {
			layout.setOnClickListener(new EndListener());
			replay.setOnClickListener(new Listener(-1));
			replay.setVisibility(View.VISIBLE);
			tab.setVisibility(View.INVISIBLE);
			arrow.setImageDrawable(arrowDrawables[2]);
			aParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			aParam.topMargin = screen.y * 377 / 854;

			aParam.leftMargin = screen.x * 170 / 480;
			animation.start();
		}
	}

	private class Listener implements View.OnClickListener {

		private int step;

		Listener(int step) {
			this.step = step;
		}

		@Override
		public void onClick(View v) {
			if (step == -1)
				ClickLog.Log(ClickLogId.TUTORIAL_REPLAY);
			else
				ClickLog.Log(ClickLogId.TUTORIAL_NEXT);
			settingState(step + 1);
		}

	}

	private class EndListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.TUTORIAL_NEXT);
			arrow.setAnimation(null);
			if (Build.VERSION.SDK_INT >= 8)
				animation.cancel();
			finish();
		}
	}

}
