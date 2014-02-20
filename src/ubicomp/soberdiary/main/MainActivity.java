package ubicomp.soberdiary.main;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.fragments.StorytellingFragment;
import ubicomp.soberdiary.main.fragments.StatisticFragment;
import ubicomp.soberdiary.main.fragments.TestFragment;
import ubicomp.soberdiary.main.ui.CustomMenu;
import ubicomp.soberdiary.main.ui.CustomTab;
import ubicomp.soberdiary.main.ui.CustomToast;
import ubicomp.soberdiary.main.ui.LoadingDialogControl;
import ubicomp.soberdiary.main.ui.ScreenSize;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.system.check.LockCheck;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.config.Config;
import ubicomp.soberdiary.system.config.PreferenceControl;
import ubicomp.soberdiary.system.gcm.GCMNotificationGen;
import ubicomp.soberdiary.system.gcm.GCMUtilities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	public static MainActivity mainActivity = null;
	
	static private TabHost tabHost;

	static private TabSpec[] tabs;
	static private CustomTab[] customTabs;

	private static final String[] tabName = { "Test", "Statistic", "Storytelling" };
	private static final int[] iconId = { R.drawable.tabs_test_selector, R.drawable.tabs_statistic_selector,
			R.drawable.tabs_history_selector };
	private static final int[] iconOnId = { R.drawable.tab_test_selected, R.drawable.tab_statistic_selected,
			R.drawable.tab_storytelling_selected };

	static private Fragment[] fragments;
	private android.support.v4.app.FragmentTransaction ft;
	private android.support.v4.app.FragmentManager fm;
	TabChangeListener tabChangeListener;

	private LoadingPageHandler loadingPageHandler;

	private Thread t;

	private CustomMenu menu;

	private static RelativeLayout count_down_layout;
	private static TextView count_down_text;

	//private static final String TAG = "MAIN_ACTIVITY";

	private static boolean canUpdate;
	private static CountDownTimer updateTestTimer = null;

	private static final long TEST_GAP_DURATION_LONG = Config.TEST_GAP_DURATION_LONG;
	private static final long TEST_GAP_DURATION_SHORT = Config.TEST_GAP_DURATION_SHORT;
	private static CountDownTimer sensorCountDownTimer = null;
	private static boolean isRecovery = false;

	private static SoundPool soundpool;
	private static int timer_sound_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mainActivity = this;
		
		setContentView(R.layout.activity_main);

		if (PreferenceControl.checkFirstUID())
			PreferenceControl.defaultSetting();

		Typefaces.initAll();
		CustomToast.settingSoundPool();

		loading_page = (ImageView) findViewById(R.id.loading_page);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		if (tabs == null)
			tabs = new TabSpec[3];
		if (customTabs == null)
			customTabs = new CustomTab[3];

		for (int i = 0; i < 3; ++i) {
			customTabs[i] = new CustomTab(iconId[i], iconOnId[i]);
			tabs[i] = tabHost.newTabSpec(tabName[i]).setIndicator(customTabs[i].getTab());
			tabs[i].setContent(new DummyTabFactory(this));
			tabHost.addTab(tabs[i]);
		}
		fm = getSupportFragmentManager();
		fragments = new Fragment[3];
		tabHost.setOnTabChangedListener(new TabChangeListener());

		setDefaultTab();

		loadingPageHandler = new LoadingPageHandler();
		t = new Thread(new TimerRunnable());
		t.start();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;

		TabWidget tabWidget = tabHost.getTabWidget();

		int count = tabWidget.getChildCount();
		for (int i = 0; i < count; ++i)
			tabWidget.getChildTabViewAt(i).setMinimumWidth(screenWidth / count);

		GCMUtilities.onCreate(getApplicationContext());

		enableTabAndClick(false);

		if (soundpool == null) {
			soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
			timer_sound_id = soundpool.load(getApplicationContext(), R.raw.end_count_down, 0);
		}

		count_down_layout = (RelativeLayout) findViewById(R.id.main_count_down_layout);
		count_down_text = (TextView) findViewById(R.id.main_count_down_text);
		count_down_text.setTypeface(Typefaces.getDigitTypefaceBold());
		count_down_layout.setOnTouchListener(new OnTouchListener() {

			private int width = 0, height = 0;

			public boolean onTouch(View v, MotionEvent event) {
				RelativeLayout.LayoutParams param = (LayoutParams) v.getLayoutParams();
				Point screen = ScreenSize.getScreenSize();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					width = width > 0 ? width : v.getWidth();
					height = height > 0 ? height : v.getHeight();
					param.width = width * 3 / 2;
					param.height = height * 3 / 2;
					count_down_text.setTextSize(21);
					v.setBackgroundResource(R.drawable.count_down_circle_pressed);
					v.setLayoutParams(param);
					v.invalidate();
					break;

				case MotionEvent.ACTION_MOVE:
					param.leftMargin = (int) event.getRawX() - width * 3 / 4;
					param.topMargin = (int) event.getRawY() - height * 3 / 4;
					param.leftMargin = Math.max(param.leftMargin, 0);
					param.topMargin = Math.max(param.topMargin, 0);
					param.leftMargin = Math.min(param.leftMargin, screen.x - width * 3 / 2);
					param.topMargin = Math.min(param.topMargin, screen.y - height * 3 / 2);
					v.setLayoutParams(param);
					v.invalidate();
					break;

				case MotionEvent.ACTION_UP:
					v.setBackgroundResource(R.drawable.count_down_circle);
					param.width = width;
					param.height = height;
					v.setLayoutParams(param);
					count_down_text.setTextSize(14);
					v.invalidate();
					break;
				}
				return true;
			}
		});
	}

	@Override
	protected void onStart() {
		UploadService.startUploadService(this);
		super.onStart();
	}

	protected void onResume() {
		super.onResume();
		if (LockCheck.check()) {
			Intent lock_intent = new Intent(this, LockedActivity.class);
			startActivity(lock_intent);
			finish();
			return;
		}
		setTimers();
		clickable = true;
	}

	@Override
	protected void onDestroy() {
		GCMUtilities.onDestroy(getApplicationContext());
		GCMNotificationGen.gen(App.context);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		closeOptionsMenu();
		closeTimers();
		super.onPause();
	}

	public void setTabState(String tabId) {
		for (int i = 0; i < 3; ++i) {
			if (tabId.equals(tabName[i]))
				customTabs[i].changeState(true);
			else
				customTabs[i].changeState(false);
		}
	}

	public static void changeTab(int pos) {
		TabWidget tabWidget = tabHost.getTabWidget();
		int count = tabWidget.getChildCount();
		if (pos >= 0 && pos < count) {
			tabHost.setCurrentTab(pos);
		}
	}

	public void setCouponChange(boolean change){
		customTabs[1].showHighlight(change);
	}
	
	private void setDefaultTab() {
		ft = fm.beginTransaction();
		fragments[0] = new TestFragment();
		fragments[1] = new StatisticFragment();
		fragments[2] = new StorytellingFragment();

		ft.add(android.R.id.tabcontent, fragments[0], tabName[0]);
		setTabState(tabName[0]);
		
		customTabs[1].showHighlight(PreferenceControl.getCouponChange());
		customTabs[2].showHighlight(PreferenceControl.getPageChange());
		
		ft.commit();
	}

	public class TabChangeListener implements TabHost.OnTabChangeListener {

		private String lastTabId;

		public TabChangeListener() {
			lastTabId = tabName[0];
		}

		@Override
		public void onTabChanged(String tabId) {

			if (lastTabId.equals(tabId))
				return;

			LoadingDialogControl.show(MainActivity.this);

			setTimers();
			if (tabId.equals(tabName[0])) {
				ClickLog.Log(ClickLogId.TAB_TEST);
				customTabs[1].showHighlight(PreferenceControl.getCouponChange());
				customTabs[2].showHighlight(PreferenceControl.getPageChange());
			} else if (tabId.equals(tabName[1])) {
				ClickLog.Log(ClickLogId.TAB_STATISTIC);
				customTabs[1].showHighlight(false);
				customTabs[2].showHighlight(PreferenceControl.getPageChange());
			} else if (tabId.equals(tabName[2])) {
				ClickLog.Log(ClickLogId.TAB_STORYTELLING);
				customTabs[1].showHighlight(PreferenceControl.getCouponChange());
				customTabs[2].showHighlight(false);
			}
			ft = fm.beginTransaction();

			for (int i = 0; i < fragments.length; ++i) {
				if (fragments[i] != null)
					ft.remove(fragments[i]);
			}
			for (int i = 0; i < tabName.length; ++i) {
				if (tabId.equals(tabName[i])) {
					ft.add(android.R.id.tabcontent, fragments[i], tabName[i]);
					break;
				}
			}
			lastTabId = tabId;
			setTabState(tabId);
			ft.commit();
		}

	}

	private static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context context;

		public DummyTabFactory(Context context) {
			this.context = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(context);
			return v;
		}
	}

	public static void enableTabAndClick(boolean enable) {
		enableTab(enable);
		setClickable(enable);
	}

	private static void enableTab(boolean enable) {
		if (tabHost == null || tabHost.getTabWidget() == null)
			return;

		int count = tabHost.getTabWidget().getChildCount();
		for (int i = 0; i < count; ++i) {
			tabHost.getTabWidget().getChildAt(i).setClickable(enable);
		}
	}

	private static void setClickable(boolean enable) {
		clickable = enable;
	}

	public static boolean getClickable() {
		return clickable;
	}

	private ImageView loading_page;

	private class TimerRunnable implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(3000);
				loadingPageHandler.sendEmptyMessage(0);
			} catch (InterruptedException e) {
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private class LoadingPageHandler extends Handler {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				loading_page.setVisibility(View.INVISIBLE);
				enableTabAndClick(true);
			} else
				loading_page.setVisibility(View.VISIBLE);
		}
	}

	private static boolean clickable = false;

	private boolean doubleClickState = false;
	private long latestClickTime = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!clickable)
			return super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			long cur_time = System.currentTimeMillis();
			if ((cur_time - latestClickTime) < 600 && doubleClickState) {
				doubleClickState = false;
				openOptionsMenu();
				latestClickTime = 0;
				return false;
			} else if ((cur_time - latestClickTime) >= 600 || !doubleClickState) {
				doubleClickState = true;
				latestClickTime = cur_time;
				return false;
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void openOptionsMenu() {
		if (Build.VERSION.SDK_INT < 14) {
			super.openOptionsMenu();
			return;
		}
		if (menu == null)
			menu = new CustomMenu(this);
		if (!menu.isShowing() && clickable)
			menu.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void closeOptionsMenu() {
		if (Build.VERSION.SDK_INT < 14) {
			super.closeOptionsMenu();
			return;
		}
		if (menu != null && menu.isShowing())
			menu.dismiss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Intent newIntent;
		switch (id) {
		case R.id.menu_about:
			newIntent = new Intent(this, AboutActivity.class);
			startActivity(newIntent);
			return true;
		case R.id.menu_setting:
			newIntent = new Intent(this, SettingActivity.class);
			startActivity(newIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public int getTabHeight() {
		View v = findViewById(android.R.id.tabs);
		return v.getBottom() - v.getTop();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (Build.VERSION.SDK_INT <14){
				return super.onKeyUp(keyCode, event);
			}
			else{
				if (menu != null && menu.isShowing())
					closeOptionsMenu();
				else
					openOptionsMenu();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (menu != null && menu.isShowing()) {
				closeOptionsMenu();
				return true;
			} else {
				if (clickable) {
					return super.onKeyUp(keyCode, event);
				} else
					return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	private static void setSensorCountDownTimer() {
		long lastTime = PreferenceControl.getLastTestTime();
		long curTime = System.currentTimeMillis();
		boolean debug = PreferenceControl.isDebugMode();
		boolean testFail = PreferenceControl.isTestFail();
		long TEST_GAP_DURATION = testFail ? TEST_GAP_DURATION_SHORT : TEST_GAP_DURATION_LONG;
		long time = curTime - lastTime;
		isRecovery = false;
		if (time <= TEST_GAP_DURATION) {
			closeSensorCountDownTimer();
			if (debug)
				sensorCountDownTimer = new SensorCountDownTimer(0);
			else{
				isRecovery = true;
				sensorCountDownTimer = new SensorCountDownTimer(TEST_GAP_DURATION - time);
			}
			sensorCountDownTimer.start();
		}
	}

	private static void closeSensorCountDownTimer() {
		if (sensorCountDownTimer != null) {
			sensorCountDownTimer.cancel();
			sensorCountDownTimer = null;
			if (tabHost.getCurrentTab() == 0) {
				((TestFragment) fragments[0]).enableStartButton(true);
			}
		}
	}

	private static class SensorCountDownTimer extends CountDownTimer {

		public SensorCountDownTimer(long millisInFuture) {
			super(millisInFuture, 100);
		}

		@Override
		public void onFinish() {
			soundpool.play(timer_sound_id, 1f, 1f, 0, 0, 1f);
			isRecovery = false;
			count_down_layout.setVisibility(View.GONE);
			if (tabHost.getCurrentTab() == 0) {
				((TestFragment) fragments[0]).setState(TestFragment.STATE_INIT);
				((TestFragment) fragments[0]).enableStartButton(true);
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long time = millisUntilFinished / 1000L;
			isRecovery = true;
			count_down_text.setText(String.valueOf(time));
			count_down_layout.setVisibility(View.VISIBLE);
			if (tabHost.getCurrentTab() == 0) {
				if (canUpdate) {
					((TestFragment) fragments[0]).setGuideMessage(R.string.test_guide_recovery_update_top,
							R.string.test_guide_recovery_update_bottom);
				} else {
					((TestFragment) fragments[0]).setGuideMessage(R.string.test_guide_recovery_top,
							R.string.test_guide_recovery_bottom);
				}
				((TestFragment) fragments[0]).setStartButtonText(R.string.recoverying);
				((TestFragment) fragments[0]).enableStartButton(false);
			}

		}
	}

	private static class UpdateTestTimer extends CountDownTimer {

		private static final long FIVE_MINUTES = 5 * 60 * 1000;

		public UpdateTestTimer() {
			super((PreferenceControl.getLatestTestCompleteTime() + FIVE_MINUTES - System.currentTimeMillis()), 100);
		}

		@Override
		public void onFinish() {
			canUpdate = false;
			count_down_layout.setVisibility(View.GONE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			int time = (int) millisUntilFinished / 1000;
			if (!isRecovery) {
				count_down_text.setText(String.valueOf(time));
				if (tabHost.getCurrentTab() == 0) {
					((TestFragment) fragments[0]).setGuideMessage(R.string.test_guide_update_top,
							R.string.test_guide_update_bottom);
				}
			}
			count_down_layout.setVisibility(View.VISIBLE);
		}
	}

	private static void closeUpdateTestTimer() {
		if (updateTestTimer != null) {
			updateTestTimer.cancel();
			updateTestTimer = null;
		}
	}

	private static void setUpdateTestTimer() {
		canUpdate = false;
		canUpdate = PreferenceControl.getUpdateDetection();
		if (canUpdate) {
			closeUpdateTestTimer();
			updateTestTimer = new UpdateTestTimer();
			updateTestTimer.start();
		}
	}

	public static void setTimers() {
		closeTimers();
		setSensorCountDownTimer();
		setUpdateTestTimer();
	}

	public static void closeTimers() {
		count_down_layout.setVisibility(View.GONE);
		closeUpdateTestTimer();
		closeSensorCountDownTimer();
	}

	public static boolean canUpdate() {
		return canUpdate;
	}

}