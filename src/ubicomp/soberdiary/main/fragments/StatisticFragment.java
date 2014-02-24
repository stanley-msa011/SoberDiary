package ubicomp.soberdiary.main.fragments;

import java.util.ArrayList;

import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.MainActivity;
import ubicomp.soberdiary.main.ui.LoadingDialogControl;
import ubicomp.soberdiary.main.ui.ScaleOnTouchListener;
import ubicomp.soberdiary.main.ui.ScreenSize;
import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.statistic.ui.QuestionnaireBoxUpdater;
import ubicomp.soberdiary.statistic.ui.RadarChart4;
import ubicomp.soberdiary.statistic.ui.RankDetailChart;
import ubicomp.soberdiary.statistic.ui.ShowRadarChart;
import ubicomp.soberdiary.statistic.ui.block.AnalysisCounterView;
import ubicomp.soberdiary.statistic.ui.block.AnalysisRankView;
import ubicomp.soberdiary.statistic.ui.block.AnalysisSavingView;
import ubicomp.soberdiary.statistic.ui.block.StatisticPageView;
import ubicomp.soberdiary.statistic.ui.block.StatisticPagerAdapter;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class StatisticFragment extends Fragment implements ShowRadarChart, QuestionnaireBoxUpdater {

	private View view;
	private Activity activity;
	private ViewPager statisticView;
	private StatisticPagerAdapter statisticViewAdapter;
	private RelativeLayout allLayout;
	private ImageView[] dots;
	private Drawable dot_on, dot_off;
	private LinearLayout analysisLayout;
	private StatisticPageView[] analysisViews;
	private ScrollView analysisView;
	private LoadingHandler loadHandler;
	private StatisticFragment statisticFragment;

	private ImageView questionButton;

	private AlphaAnimation questionAnimation;

	private QuestionnaireBox msgBox;
	private View shadowView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this.getActivity();
		dot_on = getResources().getDrawable(R.drawable.statistic_dot_on);
		dot_off = getResources().getDrawable(R.drawable.statistic_dot_off);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.statistic_fragment, container, false);

		allLayout = (RelativeLayout) view.findViewById(R.id.statistic_fragment_layout);
		analysisLayout = (LinearLayout) view.findViewById(R.id.brac_analysis_layout);
		analysisView = (ScrollView) view.findViewById(R.id.brac_analysis);
		statisticView = (ViewPager) view.findViewById(R.id.brac_statistics);
		questionButton = (ImageView) view.findViewById(R.id.question_button);
		dots = new ImageView[3];
		dots[0] = (ImageView) view.findViewById(R.id.brac_statistic_dot0);
		dots[1] = (ImageView) view.findViewById(R.id.brac_statistic_dot1);
		dots[2] = (ImageView) view.findViewById(R.id.brac_statistic_dot2);
		shadowView = new View(activity);
		shadowView.setBackgroundColor(0x99000000);
		allLayout.addView(shadowView);
		shadowView.setVisibility(View.GONE);

		questionButton.setOnTouchListener(new ScaleOnTouchListener());
		loadHandler = new LoadingHandler();

		analysisLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent motion) {
				if (motion.getAction() == MotionEvent.ACTION_DOWN)
					ClickLog.Log(ClickLogId.STATISTIC_ANALYSIS);
				return false;
			}
		});
		return view;
	}

	public void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.STATISTIC_ENTER);
		enablePage(true);
		statisticFragment = this;
		analysisViews = new StatisticPageView[3];
		analysisViews[0] = new AnalysisCounterView();
		analysisViews[1] = new AnalysisSavingView();
		analysisViews[2] = new AnalysisRankView(statisticFragment);
		statisticViewAdapter = new StatisticPagerAdapter();
		msgBox = new QuestionnaireBox(this, (RelativeLayout) view);

		loadHandler.sendEmptyMessage(0);
	}

	public void onPause() {
		if (loadHandler != null)
			loadHandler.removeMessages(0);
		clear();
		ClickLog.Log(ClickLogId.STATISTIC_LEAVE);
		super.onPause();
	}

	private void clear() {
		removeRadarChart();
		statisticViewAdapter.clear();
		for (int i = 0; i < analysisViews.length; ++i) {
			if (analysisViews[i] != null)
				analysisViews[i].clear();
		}
		if (analysisLayout != null)
			analysisLayout.removeAllViews();
		if (msgBox != null)
			msgBox.clear();
	}

	private class StatisticOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int idx) {

			switch(idx){
			case 0:
				ClickLog.Log(ClickLogId.STATISTIC_TODAY);
				break;
			case 1:
				ClickLog.Log(ClickLogId.STATISTIC_WEEK);
				break;
			case 2:
				ClickLog.Log(ClickLogId.STATISTIC_MONTH);
				break;
			}
			for (int i = 0; i < 3; ++i)
				dots[i].setImageDrawable(dot_off);
			dots[idx].setImageDrawable(dot_on);
		}

	}

	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler {
		public void handleMessage(Message msg) {
			MainActivity.enableTabAndClick(false);
			statisticView.setAdapter(statisticViewAdapter);
			statisticView.setOnPageChangeListener(new StatisticOnPageChangeListener());
			statisticView.setSelected(true);
			analysisLayout.removeAllViews();

			questionButton.setOnClickListener(new QuestionOnClickListener());
			for (int i = 0; i < analysisViews.length; ++i)
				if (analysisViews[i] != null)
					analysisLayout.addView(analysisViews[i].getView());

			statisticViewAdapter.load();
			for (int i = 0; i < analysisViews.length; ++i)
				if (analysisViews[i] != null)
					analysisViews[i].load();

			statisticView.setCurrentItem(0);

			for (int i = 0; i < 3; ++i)
				dots[i].setImageDrawable(dot_off);
			dots[0].setImageDrawable(dot_on);

			if (msgBox != null)
				msgBox.load();

			questionAnimation = new AlphaAnimation(1.0F, 0.0F);
			questionAnimation.setDuration(200);
			questionAnimation.setRepeatCount(Animation.INFINITE);
			questionAnimation.setRepeatMode(Animation.REVERSE);

			setQuestionAnimation();

			MainActivity.enableTabAndClick(true);
			LoadingDialogControl.dismiss();
		}
	}

	private class QuestionOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int result = PreferenceControl.getTestResult();
			ClickLog.Log(ClickLogId.STATISTIC_QUESTION_BUTTON);
			if (msgBox == null)
				return;
			if (result == 0)
				msgBox.generateType0Box();
			else if (result == 1)
				msgBox.generateType1Box();
			else if (result == 2)
				msgBox.generateType2Box();
			else if (result == 3)
				msgBox.generateType3Box();
			else
				msgBox.generateNormalBox();
		}
	}

	public void setQuestionAnimation() {
		questionButton.setVisibility(View.VISIBLE);
		int result = PreferenceControl.getTestResult();
		if (result == -1) {
			questionAnimation.cancel();
			questionButton.setAnimation(null);
			if (Build.VERSION.SDK_INT >= 11)
				questionButton.setAlpha(1.0F);
		} else {
			questionButton.setAnimation(questionAnimation);
			questionAnimation.start();
		}
	}

	public void enablePage(boolean enable) {
		statisticView.setEnabled(enable);
		analysisView.setEnabled(enable);
		questionButton.setEnabled(enable);
		MainActivity.enableTabAndClick(enable);
	}

	@Override
	public void updateSelfHelpCounter() {
		try {
			AnalysisCounterView acv = (AnalysisCounterView) analysisViews[0];
			acv.updateCounter();
		} catch (Exception e) {
		}
	}

	private RadarChart4 rv;
	private RankDetailChart dv;

	public void showRadarChart(ArrayList<Double> scoreList) {
		ArrayList<String> labelList = new ArrayList<String>();
		labelList.add(getString(R.string.radar_label0));
		labelList.add(getString(R.string.radar_label1));
		labelList.add(getString(R.string.radar_label2));
		labelList.add(getString(R.string.radar_label3));

		removeRadarChart();
		removeDetailChart();

		rv = new RadarChart4(activity, scoreList, labelList, getString(R.string.radar_title));
		shadowView.setVisibility(View.VISIBLE);
		allLayout.addView(rv);
		Point screen = ScreenSize.getScreenSize();
		RelativeLayout.LayoutParams rvParam = (RelativeLayout.LayoutParams) rv.getLayoutParams();
		rvParam.height = rvParam.width = screen.x * 9 / 10;
		rvParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		allLayout.invalidate();
		rv.invalidate();
		rv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClickLog.Log(ClickLogId.STATISTIC_RADAR_CHART_CLOSE);
				removeRadarChart();
				int type = rv.getType();
				if (type >= 0) {
					ClickLog.Log(ClickLogId.STATISTIC_DETAIL_CHART_OPEN + type);
					addDetailChart(rv.getType());
				}
			}
		});
		ClickLog.Log(ClickLogId.STATISTIC_RADAR_CHART_OPEN);
		enablePage(false);
	}

	public void removeRadarChart() {
		if (rv != null && rv.getParent() != null && rv.getParent().equals(allLayout))
			allLayout.removeView(rv);
		shadowView.setVisibility(View.GONE);
		enablePage(true);
	}

	public void addDetailChart(int type) {
		removeRadarChart();
		removeDetailChart();
		dv = new RankDetailChart(activity);
		shadowView.setVisibility(View.VISIBLE);
		allLayout.addView(dv);
		Point screen = ScreenSize.getScreenSize();
		RelativeLayout.LayoutParams dvParam = (RelativeLayout.LayoutParams) dv.getLayoutParams();
		dvParam.width = screen.x * 9 / 10;
		dvParam.addRule(RelativeLayout.CENTER_IN_PARENT);
		dv.setRank(new DatabaseControl().getMyRank(), type);
		dv.invalidate();
		dv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClickLog.Log(ClickLogId.STATISTIC_DETAIL_CHART_CLOSE);
				removeDetailChart();
			}
		});
		dv.invalidate();
		enablePage(false);
	}

	public void removeDetailChart() {
		if (dv != null && dv.getParent() != null && dv.getParent().equals(allLayout))
			allLayout.removeView(dv);
		shadowView.setVisibility(View.GONE);
		enablePage(true);
	}

	@Override
	public Context getContext() {
		return this.getActivity();
	}
}
