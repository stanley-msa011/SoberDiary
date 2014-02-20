package ubicomp.soberdiary.main.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.BarInfo;
import ubicomp.soberdiary.data.structure.Detection;
import ubicomp.soberdiary.data.structure.TimeValue;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.FacebookActivity;
import ubicomp.soberdiary.main.MainActivity;
import ubicomp.soberdiary.main.StorytellingTestActivity;
import ubicomp.soberdiary.main.ui.CustomToastSmall;
import ubicomp.soberdiary.main.ui.CustomTypefaceSpan;
import ubicomp.soberdiary.main.ui.EnablePage;
import ubicomp.soberdiary.main.ui.LoadingDialogControl;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.storytelling.ui.ChartCaller;
import ubicomp.soberdiary.storytelling.ui.ChartLabelView;
import ubicomp.soberdiary.storytelling.ui.ChartTitleView;
import ubicomp.soberdiary.storytelling.ui.ChartView;
import ubicomp.soberdiary.storytelling.ui.ChartYAxisView;
import ubicomp.soberdiary.storytelling.ui.RecordBox;
import ubicomp.soberdiary.storytelling.ui.StorytellingGraphics;
import ubicomp.soberdiary.storytelling.ui.PageAnimationCaller;
import ubicomp.soberdiary.storytelling.ui.PageAnimationTaskVertical;
import ubicomp.soberdiary.storytelling.ui.PageAnimationTaskVerticalFling;
import ubicomp.soberdiary.storytelling.ui.PageWidgetVertical;
import ubicomp.soberdiary.storytelling.ui.QuoteMsgBox;
import ubicomp.soberdiary.storytelling.ui.RecorderCaller;
import ubicomp.soberdiary.system.check.NetworkCheck;
import ubicomp.soberdiary.system.check.WeekNumCheck;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.config.Config;
import ubicomp.soberdiary.system.config.PreferenceControl;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class StorytellingFragment extends Fragment implements EnablePage, PageAnimationCaller, RecorderCaller, ChartCaller {

	private View view;

	private RelativeLayout topLayout;
	private RelativeLayout pageLayout;
	private RelativeLayout chartAreaLayout;
	private PageWidgetVertical pageWidget;
	private ImageView prevPageImage;
	private PageAnimationTaskVertical pageAnimationTask;
	private PageAnimationTaskVerticalFling pageAnimationTask2;
	private HorizontalScrollView scrollView;
	private FrameLayout frame;
	private ChartView chartView;
	private ChartTitleView chartTitle;
	private ChartYAxisView chartYAxis;
	private ChartLabelView chartLabel;
	private ScrollView quoteScrollView;
	private RelativeLayout quoteHiddenLayout;
	private TextView quoteHiddenText;
	private TextView quoteText;
	private RelativeLayout stageLayout;
	private TextView stageMessageText, stageMessage, stageRateText;
	
	private DatabaseControl db = new DatabaseControl();
	
	private ArrayList<BarInfo> bars = new ArrayList<BarInfo>();
	private ArrayList<Boolean> hasAudio = new ArrayList<Boolean>();

	private int NUM_OF_BARS;
	private int page_week;

	private GestureListener gListener = new GestureListener();
	private GestureDetector gDetector = new GestureDetector(App.context, gListener);
	private PageTouchListener gtListener = new PageTouchListener();

	private boolean isAnimation = false;

	private int page_width, page_height;

	private Bitmap cur_bg_bmp, next_bg_bmp;

	private PointF from, to;
	private StorytellingFragment storytellingFragment;

	private LoadingHandler loadHandler = new LoadingHandler();

	private Typeface wordTypefaceBold,  digitTypefaceBold;

	private DecimalFormat format;

	private Calendar from_cal;
	private Calendar to_cal;

	private static final int MAX_PAGE_WEEK = 11;
	private int max_week;
	private Integer[] page_states;
	private static String[] QUOTE_STR = App.context.getResources().getStringArray(R.array.quote_message);
	private String doneStr = App.context.getResources().getString(R.string.done);

	private ScrollToHandler scrollToHandler = new ScrollToHandler();
	private QuoteScrollHandler quoteScrollHandler = new QuoteScrollHandler();
	private ScrollHandler scrollHandler = new ScrollHandler();
	private Thread infiniteThread;
	
	private ImageView storytellingButton, fbButton;
	private StorytellingTestOnClickListener storytellingOnClickListener  = new StorytellingTestOnClickListener();
	private FacebookOnClickListener facebookOnClickListener = new FacebookOnClickListener();
	private final RecordBoxOnKeyListener recordBoxOnKeyListener = new RecordBoxOnKeyListener();
	private QuoteScrollListener quoteScrollListener = new QuoteScrollListener();
	private AlphaAnimation animation = new AlphaAnimation(1.f, 0.f);
	
	private static final long READING_PAGE_TIME = Config.READING_PAGE_TIME;

	private RecordBox recordBox;
	private QuoteMsgBox quoteMsgBox;

	//private static final String TAG = "STORYTELLING";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		storytellingFragment = this;
		wordTypefaceBold = Typefaces.getDigitTypefaceBold();
		digitTypefaceBold = Typefaces.getWordTypefaceBold();

		format = new DecimalFormat();
		format.setMaximumIntegerDigits(3);
		format.setMinimumIntegerDigits(1);
		format.setMinimumFractionDigits(0);
		format.setMaximumFractionDigits(0);

		animation.setDuration(2500);
		animation.setFillAfter(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.storytelling_fragment, container, false);

		topLayout = (RelativeLayout) view.findViewById(R.id.history_top_layout);
		pageLayout = (RelativeLayout) view.findViewById(R.id.history_book_layout);
		scrollView = (HorizontalScrollView) view.findViewById(R.id.history_scroll_view);
		chartAreaLayout = (RelativeLayout) view.findViewById(R.id.history_chart_area_layout);
		quoteHiddenLayout = (RelativeLayout) view.findViewById(R.id.history_quote_hidden_layout);
		quoteHiddenText = (TextView) view.findViewById(R.id.history_quote_hidden_text);
		quoteScrollView = (ScrollView) view.findViewById(R.id.history_quote_scroll_view);
		fbButton = (ImageView) view.findViewById(R.id.history_fb_button);
		stageRateText = (TextView) view.findViewById(R.id.history_stage_rate);
		quoteText = (TextView) view.findViewById(R.id.history_quote);
		stageLayout = (RelativeLayout) view.findViewById(R.id.history_stage_message_layout);
		stageMessage = (TextView) view.findViewById(R.id.history_stage);
		stageMessageText = (TextView) view.findViewById(R.id.history_stage_message);
		storytellingButton = (ImageView) view.findViewById(R.id.history_storytelling_button);
		prevPageImage = (ImageView) view.findViewById(R.id.history_prev_image);
		frame = (FrameLayout) view.findViewById(R.id.history_frame_layout);
		chartView = (ChartView) view.findViewById(R.id.chartView);
		chartYAxis = (ChartYAxisView) view.findViewById(R.id.chartYAxisView);
		chartTitle = (ChartTitleView) view.findViewById(R.id.chartTitleView);
		chartLabel = (ChartLabelView) view.findViewById(R.id.chartLabelView);
		
		stageMessage.setTypeface(wordTypefaceBold);
		stageMessageText.setTypeface(digitTypefaceBold);
		quoteText.setTypeface(wordTypefaceBold);
		quoteHiddenText.setTypeface(wordTypefaceBold);
		
		storytellingButton.setOnClickListener(storytellingOnClickListener);
		fbButton.setOnClickListener(facebookOnClickListener);
		quoteScrollView.setOnTouchListener(quoteScrollListener);
		scrollView.setSmoothScrollingEnabled(true);
		
		recordBox = new RecordBox(storytellingFragment, getActivity());
		quoteMsgBox = new QuoteMsgBox(storytellingFragment, (RelativeLayout) view);
		
		loadHandler.sendEmptyMessage(0);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		ClickLog.Log(ClickLogId.STORYTELLING_ENTER);
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		settingBars();
		checkHasRecorder();
		reopenRecordBox();
	}

	@Override
	public void onPause() {
		ClickLog.Log(ClickLogId.STORYTELLING_LEAVE);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		clear();
		super.onDestroy();
	}

	private void clear() {
		closeRecorder();
		if (recordBox != null) {
			recordBox.cleanRecordBox();
			recordBox = null;
		}
		
		bars.clear();
		hasAudio.clear();

		if (scrollToHandler != null)
			scrollToHandler.removeMessages(0);
		if (quoteScrollHandler != null)
			quoteScrollHandler.removeMessages(0);
		if (infiniteThread != null && !infiniteThread.isInterrupted()) {
			infiniteThread.interrupt();
			try {
				infiniteThread.join();
			} catch (InterruptedException e) {
			} finally {
				infiniteThread = null;
			}
		}
		if (scrollHandler != null) {
			scrollHandler.removeMessages(0);
			scrollHandler.removeMessages(1);
		}
		if (quoteMsgBox != null)
			quoteMsgBox.closeBox();
		
		quoteScrollView.scrollTo(0, 0);
		pageLayout.removeView(pageWidget);

		if (pageWidget != null)
			pageWidget.setBitmaps(null, null);

		if (loadHandler != null)
			loadHandler.removeMessages(0);

		if (pageAnimationTask != null && !pageAnimationTask.isCancelled())
			pageAnimationTask.cancel(true);
		if (pageAnimationTask2 != null && !pageAnimationTask2.isCancelled())
			pageAnimationTask2.cancel(true);
		
		if (cur_bg_bmp != null && !cur_bg_bmp.isRecycled())
			cur_bg_bmp.recycle();
		if (next_bg_bmp != null && !next_bg_bmp.isRecycled())
			next_bg_bmp.recycle();
		if (pageWidget != null) {
			pageWidget.destroyDrawingCache();
			pageWidget.clear();
			pageWidget = null;
		}
	}

	private void setStorytellingTexts() {

		Integer score = page_states[page_week];
		float progress = Detection.weeklyScoreToProgress(score.intValue());
		String stageText = String.valueOf(page_week + 1);
		stageMessageText.setText(stageText);
		String progress_str = format.format(progress) + "%\n";
		Spannable p_str = new SpannableString(progress_str + doneStr);
		p_str.setSpan(new CustomTypefaceSpan("c1", digitTypefaceBold, 0xFFE79100), 0, progress_str.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		p_str.setSpan(new CustomTypefaceSpan("c2", wordTypefaceBold, 0xFF717071), progress_str.length(),
				progress_str.length() + doneStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		stageRateText.setText(p_str);
		quoteText.setText(QUOTE_STR[page_week]);
	}

	private void endAnimation() {
		setStorytellingTexts();
		setStageVisible(true);
		MainActivity.enableTabAndClick(true);
		isAnimation = false;
		chartView.invalidate();
	}

	private void showAlphaAnimation() {
		if (PreferenceControl.getPageChange()) {
			int curState = StorytellingGraphics.getPageIdx(page_states[page_week], page_week);
			int prevStateId = StorytellingGraphics.getPageByIdx(curState - 1, page_week);
			prevPageImage.setImageResource(prevStateId);
			prevPageImage.setAnimation(animation);
			animation.start();
			PreferenceControl.setPrevShowWeekState(page_week, curState);
			PreferenceControl.setPageChange(false);
		}
	}

	@Override
	public void endOnViewCreateAnimation() {
		endAnimation();
		showAlphaAnimation();
	}

	@Override
	public void endFlingAnimation() {
		endAnimation();
		pageWidget.setOnTouchListener(gtListener);
		quoteScrollListener.setEnable(true);
		if (infiniteThread != null && !infiniteThread.isInterrupted()) {
			infiniteThread.interrupt();
			try {
				infiniteThread.join();
			} catch (InterruptedException e) {
			} finally {
				infiniteThread = null;
			}
		}
		if (scrollHandler != null) {
			scrollHandler.removeMessages(0);
			scrollHandler.removeMessages(1);
		}
		quoteScrollView.scrollTo(0, 0);

	}

	@Override
	public void resetPage(int change) {
		if (cur_bg_bmp != null && !cur_bg_bmp.isRecycled()) {
			cur_bg_bmp.recycle();
			cur_bg_bmp = null;
		}
		if (next_bg_bmp != null && !next_bg_bmp.isRecycled()) {
			next_bg_bmp.recycle();
			next_bg_bmp = null;
		}
		if (change > 0) {
			++page_week;
			if (page_week > max_week)
				page_week = max_week;
		} else if (change < 0) {
			--page_week;
			if (page_week < 0)
				page_week = 0;
		}
		Integer score = page_states[page_week];
		Bitmap tmp = BitmapFactory.decodeResource(App.context.getResources(),
				StorytellingGraphics.getPage(score, page_week));
		cur_bg_bmp = Bitmap.createScaledBitmap(tmp, page_width, page_height, true);
		tmp.recycle();
		pageWidget.setBitmaps(cur_bg_bmp, next_bg_bmp);
		
		int scroll_value = chartView.getScrollValue(page_week);
		if (scroll_value < 0)
			scroll_value = 0;
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putInt("pos", scroll_value);
		msg.setData(data);
		msg.what = 0;
		scrollToHandler.sendMessage(msg);
	}

	@SuppressLint("HandlerLeak")
	private class ScrollToHandler extends Handler {
		public void handleMessage(Message msg) {
			chartView.setPageWeek(page_week);
			int pos = msg.getData().getInt("pos", 0);
			scrollView.scrollTo(pos, 0);
			chartView.setTouchToday();
		}
	}

	@SuppressLint("HandlerLeak")
	private class LoadingHandler extends Handler {

		public void handleMessage(Message msg) {

			page_width = topLayout.getWidth();
			page_height = topLayout.getHeight();
			
			from = new PointF(page_width, page_height);
			to = new PointF(page_width / 2, -page_height*4/3);

			if (pageWidget!=null){
				if (pageWidget.getParent()!=null && pageWidget.getParent().equals(pageLayout))
					pageLayout.removeView(pageWidget);
				pageWidget.clear();
				pageWidget = null;
			}
			
			pageWidget = new PageWidgetVertical(getActivity());
			pageLayout.addView(pageWidget, 0);
			
			pageWidget.setting(page_width, page_height);
			
			LayoutParams prevParam = (LayoutParams) prevPageImage.getLayoutParams();
			prevParam.width = page_width;
			prevParam.height = page_height;
			
			LayoutParams param = (LayoutParams) pageWidget.getLayoutParams();
			param.width = page_width;
			param.height = page_height;
			
			pageWidget.invalidate();

			chartView.setting(bars, page_week, hasAudio, scrollView, storytellingFragment);
			int chartWidth = chartView.getChartWidth();
			if (chartWidth < page_width)
				chartWidth = page_width;
			ViewGroup.LayoutParams chartParam = chartView.getLayoutParams();
			chartParam.width = chartWidth;
			chartParam.height = App.context.getResources().getDimensionPixelSize(R.dimen.chart_height);
			frame.updateViewLayout(chartView, chartParam);
			chartTitle.setting(storytellingFragment);
			setChartType(chart_type);
			
			pageWidget.setOnTouchListener(gtListener);
			storytellingButton.setVisibility(View.VISIBLE);
			storytellingButton.bringToFront();
			fbButton.setVisibility(View.VISIBLE);
			fbButton.bringToFront();

			isAnimation = false;

			page_states = db.getDetectionScoreByWeek();
			page_week = page_states.length - 1;
			if (page_week > MAX_PAGE_WEEK)
				page_week = MAX_PAGE_WEEK;
			max_week = page_week;

			endAnimation();

			if (page_week == 0) {
				resetPage(0);
				pageWidget.invalidate();
				showAlphaAnimation();
			} else {
				startAnim();
			}
			LoadingDialogControl.dismiss();

		}
	}

	private void startAnim() {

		isAnimation = true;
		MainActivity.enableTabAndClick(false);
		int[] aBgs = StorytellingGraphics.getAnimationBgs(page_states);
		int startIdx = page_week - 1;
		setStageVisible(false);
		pageAnimationTask = new PageAnimationTaskVertical(pageWidget, from, to, aBgs, storytellingFragment, startIdx);
		if (Build.VERSION.SDK_INT >= 11)
			pageAnimationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
		else
			pageAnimationTask.execute();
	}

	public void setStageVisible(boolean visible) {
		if (visible) {
			stageLayout.setVisibility(View.VISIBLE);
			storytellingButton.setVisibility(View.VISIBLE);
			stageRateText.setVisibility(View.VISIBLE);
			quoteScrollView.setVisibility(View.VISIBLE);
			fbButton.setVisibility(View.VISIBLE);
		} else {
			stageLayout.setVisibility(View.INVISIBLE);
			storytellingButton.setVisibility(View.INVISIBLE);
			stageRateText.setVisibility(View.INVISIBLE);
			quoteScrollView.setVisibility(View.INVISIBLE);
			fbButton.setVisibility(View.INVISIBLE);
		}
	}

	private class PageTouchListener implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gDetector.onTouchEvent(event);
		}
	}

	private void quoteScroll(int next_page) {
		hideSpecialQuote();
		Message msg = new Message();
		msg.what = 0;
		Bundle data = new Bundle();
		data.putInt("time", next_page);
		msg.setData(data);
		quoteScrollHandler.sendMessageDelayed(msg, READING_PAGE_TIME);
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e1) {
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			final int FLING_THRESHOLD = 5;
			if (Math.abs(velocityX) > 1.2 * Math.abs(velocityY))
				return true;
			if (isAnimation)
				return true;

			float y1 = e1.getY();
			float y2 = e2.getY();
			if (y1 - y2 > FLING_THRESHOLD) {// UP
				int[] aBgs = StorytellingGraphics.getAnimationBgs(page_states);
				int pageIdx = page_week;
				if (pageIdx == max_week) {
					isAnimation = false;
					pageWidget.setOnTouchListener(gtListener);
					MainActivity.enableTabAndClick(true);
					return true;
				} else {
					isAnimation = true;
					pageWidget.setOnTouchListener(null);
					MainActivity.enableTabAndClick(false);
				}
				quoteScroll(page_week + 1);
				setStageVisible(false);
				ClickLog.Log(ClickLogId.STORYTELLING_PAGE_UP);
				pageAnimationTask2 = new PageAnimationTaskVerticalFling(pageWidget, from, to, aBgs,
						storytellingFragment, pageIdx, 1);
				if (Build.VERSION.SDK_INT >= 11)
					pageAnimationTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
				else
					pageAnimationTask2.execute();
			} else if (y2 - y1 > FLING_THRESHOLD) {// DOWN
				int[] aBgs = StorytellingGraphics.getAnimationBgs(page_states);
				int pageIdx = page_week;
				if (pageIdx == 0) {
					isAnimation = false;
					pageWidget.setOnTouchListener(gtListener);
					MainActivity.enableTabAndClick(true);
					return true;
				} else {
					isAnimation = true;
					pageWidget.setOnTouchListener(null);
					MainActivity.enableTabAndClick(false);
				}
				quoteScroll(page_week - 1);
				setStageVisible(false);
				ClickLog.Log(ClickLogId.STORYTELLING_PAGE_DOWN);
				pageAnimationTask2 = new PageAnimationTaskVerticalFling(pageWidget, from, to, aBgs,
						storytellingFragment, pageIdx, 0);
				if (Build.VERSION.SDK_INT >= 11)
					pageAnimationTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
				else
					pageAnimationTask2.execute();
			}
			return true;
		}
	}

	private static final long DAY_MILLIS = AlarmManager.INTERVAL_DAY;

	public void settingBars() {

		from_cal = PreferenceControl.getStartDate();
		to_cal = Calendar.getInstance();
		if (from_cal.before(to_cal)) {
			long millis = to_cal.getTimeInMillis() - from_cal.getTimeInMillis();
			NUM_OF_BARS = (int) (millis / AlarmManager.INTERVAL_DAY) + 1;
		} else
			NUM_OF_BARS = 0;

		Detection[] detections = db.getAllPrimeDetection();
		bars.clear();

		if (NUM_OF_BARS == 0)
			return;

		long from_t = from_cal.getTimeInMillis();
		Calendar ccal = Calendar.getInstance();
		ccal.setTimeInMillis(from_cal.getTimeInMillis());

		for (int i = 0; i < NUM_OF_BARS; ++i) {

			int count = 0;
			int q_count = 0;
			float e_sum = 0;
			float c_sum = 0;
			float b_sum = 0;
			boolean drink = false;

			float emotion, craving, brac;

			int pos = 0;

			int bar_week = -1;
			
			if (detections != null){
				for (int j = pos; j < detections.length; ++j) {
					Detection detection = detections[j];
					if (detection.tv.timestamp >= from_t && detection.tv.timestamp < from_t + DAY_MILLIS) {
						e_sum += (detection.emotion + 1 > 0) ? detection.emotion + 1 : 0;
						c_sum += (detection.craving + 1 > 0) ? detection.craving + 1 : 0;
						if (detection.emotion >= 0)
							++q_count;
						if (!(detection.brac < Detection.BRAC_THRESHOLD))
							drink = true;
						bar_week = detection.tv.week;
						b_sum += detection.brac;
						++count;
					} else if (detection.tv.timestamp >= from_t + DAY_MILLIS) {
						pos = j;
						break;
					}
				}
			}
			
			boolean hasData = true;
			if (count == 0) {
				hasData = false;
				brac = 0F;
				bar_week = WeekNumCheck.getWeek(from_t);
			} else {
				brac = b_sum / count;
			}
			if (q_count == 0)
				emotion = craving = 0F;
			else {
				emotion = e_sum / q_count;
				craving = c_sum / q_count;
			}

			TimeValue tv = TimeValue.generate(ccal.getTimeInMillis());

			BarInfo barInfo = new BarInfo(emotion, craving, brac, bar_week, hasData, tv, drink);

			bars.add(barInfo);

			from_t += DAY_MILLIS;
			ccal.add(Calendar.DATE, 1);
		}
	}

	private int chart_type = 0;

	@Override
	public void setChartType(int type){
		chart_type = type;
		switch(chart_type){
		case 0:
			chartAreaLayout.setBackgroundResource(R.drawable.chart_bg_0);
			 break;
		case 1:
			chartAreaLayout.setBackgroundResource(R.drawable.chart_bg_1);
			break;
		case 2:
			chartAreaLayout.setBackgroundResource(R.drawable.chart_bg_2);
			break;
		case 3:
			chartAreaLayout.setBackgroundResource(R.drawable.chart_bg_3);
			break;
		}
		chartYAxis.setChartType(chart_type);
		chartYAxis.invalidate();
		chartView.setChartType(chart_type);
		chartView.invalidate();
		chartLabel.setChartType(chart_type);
		chartLabel.invalidate();
		chartTitle.setChartType(chart_type);
		chartTitle.invalidate();
	}
	
	private void checkHasRecorder() {
		hasAudio.clear();
		for (int i = 0; i < bars.size(); ++i) {
			if (db.hasAudio(bars.get(i).tv) || db.hasEmotionManagement(bars.get(i).tv))
				hasAudio.add(true);
			else
				hasAudio.add(false);
		}
		if (chartView != null)
			chartView.invalidate();
	}

	@Override
	public void updateHasRecorder(int idx) {
		if (idx >= 0 && idx < bars.size())
			hasAudio.set(idx, db.hasAudio(bars.get(idx).tv) || db.hasEmotionManagement(bars.get(idx).tv));
		chartView.invalidate();
	}

	private class StorytellingTestOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.STORYTELLING_TEST);
			Intent intent = new Intent(getActivity(), StorytellingTestActivity.class);
			intent.putExtra("image_week", page_week);
			intent.putExtra("image_score", page_states[page_week]);
			getActivity().startActivity(intent);
		}
	}

	private class QuoteScrollListener implements View.OnTouchListener {
		private boolean enable = true;

		public void setEnable(final boolean enable) {
			this.enable = enable;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (enable)
				return gDetector.onTouchEvent(event);
			else
				return true;
		}
	}

	@SuppressLint("HandlerLeak")
	private class QuoteScrollHandler extends Handler {
		public void handleMessage(Message msg) {
			int time = msg.getData().getInt("time");
			if (time == page_week) {
				PreferenceControl.addStorytellingReadTimes();
				int limit = Config.STORYTELLING_READ_LIMIT;
				if (db.getLatestStorytellingRead().tv.timestamp == 0)
					limit /= 2;
				int cur_time = PreferenceControl.getStorytellingReadTimes();
				if (cur_time >= limit) {
					infiniteThread = new InfiniteScroll();
					infiniteThread.start();
					PreferenceControl.resetStorytellingReadTimes();
					View.OnClickListener listener = new QuoteOnClickListener(page_week);
					quoteHiddenLayout.setOnClickListener(listener);
				}
			}
		}
	}

	private class InfiniteScroll extends Thread {
		@Override
		public void run() {
			while (true) {
				scrollHandler.removeMessages(0);
				scrollHandler.removeMessages(1);
				scrollHandler.sendEmptyMessage(1);
				if (isInterrupted())
					break;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					break;
				}
				if (isInterrupted())
					break;
				scrollHandler.removeMessages(0);
				scrollHandler.removeMessages(1);
				scrollHandler.sendEmptyMessage(0);
				if (isInterrupted())
					break;
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					break;
				}
				if (isInterrupted())
					break;
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private class ScrollHandler extends Handler {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {// up
				quoteScrollView.smoothScrollTo(0, 0);
				quoteScrollListener.setEnable(true);
			} else if (msg.what == 1) {// down
				quoteScrollView.smoothScrollTo(0, quoteScrollView.getBottom());
				quoteScrollListener.setEnable(false);
			}
		}
	}

	private class QuoteOnClickListener implements View.OnClickListener {

		private int page;

		public QuoteOnClickListener(int page) {
			this.page = page;
		}

		@Override
		public void onClick(View v) {
			ClickLog.Log(ClickLogId.STORYTELLING_READ);
			hideSpecialQuote();
			quoteMsgBox.openBox(page);
		}

	}

	private void hideSpecialQuote() {
		quoteScrollHandler.removeMessages(0);
		if (infiniteThread != null && !infiniteThread.isInterrupted()) {
			infiniteThread.interrupt();
			try {
				infiniteThread.join();
			} catch (InterruptedException e) {
			} finally {
				infiniteThread = null;
			}
		}
		if (scrollHandler != null) {
			scrollHandler.removeMessages(0);
			scrollHandler.removeMessages(1);
		}
		quoteScrollView.scrollTo(0, 0);
		quoteHiddenLayout.setOnClickListener(null);
	}

	private View recordBoxView;
	private TimeValue temp_tv = null;
	private int temp_selected_button = -1;

	@Override
	public void openRecordBox(TimeValue tv, int selected_button) {

		this.temp_tv = tv;
		this.temp_selected_button = selected_button;
		recordBoxView = recordBox.getRecordBox(tv, selected_button);
		closeRecorder();
		RelativeLayout main = (RelativeLayout) view;
		main.addView(recordBoxView);
		RelativeLayout.LayoutParams param = (LayoutParams) recordBoxView.getLayoutParams();
		param.width = page_width;
		param.height = page_height;
		partEnablePage(false);
		if (getView() != null)
			getView().setOnKeyListener(recordBoxOnKeyListener);
	}

	@Override
	public void closeRecorder() {
		if (recordBoxView != null) {
			if (recordBoxView.getParent() != null) {
				ViewGroup parent = (ViewGroup) recordBoxView.getParent();
				parent.removeView(recordBoxView);
			}
		}
		enablePage(true);
		if (getView() != null)
			getView().setOnKeyListener(null);
	}

	private void reopenRecordBox() {
		if (recordBoxView == null || recordBoxView.getParent() == null)
			return;
		if (temp_tv == null || temp_selected_button == -1)
			return;
		openRecordBox(temp_tv, temp_selected_button);
	}

	public void partEnablePage(boolean enable) {
		if (pageWidget != null)
			pageWidget.setEnabled(enable);
		if (scrollView != null)
			scrollView.setEnabled(enable);
		if (chartView != null)
			chartView.setEnabled(true);
		if (chartTitle != null)
			chartTitle.setTouchable(true);
		MainActivity.enableTabAndClick(true);
		storytellingButton.setEnabled(enable);
		fbButton.setEnabled(enable);
	}

	@Override
	public void enablePage(boolean enable) {
		if (pageWidget != null)
			pageWidget.setEnabled(enable);
		if (scrollView != null)
			scrollView.setEnabled(enable);
		if (chartView != null)
			chartView.setEnabled(enable);
		if (chartTitle != null)
			chartTitle.setTouchable(enable);
		MainActivity.enableTabAndClick(enable);
		storytellingButton.setEnabled(enable);
		fbButton.setEnabled(enable);
	}

	private class RecordBoxOnKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				closeRecorder();
				ClickLog.Log(ClickLogId.STORYTELLING_RECORD_BACK);
				getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				return true;
			}
			return false;
		}
	}
	
	private class FacebookOnClickListener implements View.OnClickListener{
			@Override
			public void onClick(View arg0) {
				if (!NetworkCheck.networkCheck()) {
					CustomToastSmall.generateToast(R.string.facebook_no_network);
					return;
				}
				ClickLog.Log(ClickLogId.STORYTELLING_FACEBOOK);
				Intent intent = new Intent(getActivity(), FacebookActivity.class);
				intent.putExtra("image_week", page_week);
				intent.putExtra("image_score", page_states[page_week]);
				getActivity().startActivity(intent);
			}
	}

	@Override
	public int getPageWidth() {
		return page_width;
	}
	
	@Override
	public int getPageHeight() {
		return page_height;
	}
	
	@Override
	public void invalidatePage() {
		if (pageWidget!=null){
			if (from != null)
				pageWidget.setTouchPosition(from);
			pageWidget.invalidate();
		}
	}
	
}
