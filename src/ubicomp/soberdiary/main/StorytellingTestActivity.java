package ubicomp.soberdiary.main;

import java.util.Random;

import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.StorytellingTest;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.BarGen;
import ubicomp.soberdiary.main.ui.CustomToast;
import ubicomp.soberdiary.main.ui.CustomToastSmall;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.config.PreferenceControl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;

public class StorytellingTestActivity extends Activity {

	private LinearLayout inputLayout;

	private LayoutInflater inflater;

	private TextView titleText;
	private Typeface wordTypefaceBold;

	private int image_week;

	private String question = "";
	private String answer = "";
	private String selectedAnswer = "";

	private TextView[] selections = new TextView[3];

	private SeekBar agreementSeekbar;
	private TextView agreementText;
	private String[] agreementLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storytelling_test);

		Bundle data = this.getIntent().getExtras();
		image_week = data.getInt("image_week", 0);

		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();

		titleText = (TextView) this.findViewById(R.id.st_title);
		inputLayout = (LinearLayout) this.findViewById(R.id.st_input_layout);
		titleText.setTypeface(wordTypefaceBold);

		View messageView = BarGen.createTextView(R.string.storytelling_test_question);
		inputLayout.addView(messageView);
		String[] selections = settingQuestion();

		View questionView = BarGen.createQuoteQuestionView(question);
		inputLayout.addView(questionView);

		View selectionView = createSelectionView(selections);
		inputLayout.addView(selectionView);

		View agreeTextView = BarGen.createTextView(R.string.storytelling_test_agreement);
		inputLayout.addView(agreeTextView);

		View agreementView = createSeekBarView();
		inputLayout.addView(agreementView);

		View submitView = BarGen.createIconView(R.string.done, R.drawable.questionnaire_item_ok,
				new SubmitOnClickListener());
		inputLayout.addView(submitView);
	}

	private String[] settingQuestion() {
		String[] questions = null;
		String[] answers = null;
		Resources r = App.context.getResources();
		switch (image_week) {
		case 0:
			questions = r.getStringArray(R.array.quote_question_0);
			answers = r.getStringArray(R.array.quote_answer_0);
			break;
		case 1:
			questions = r.getStringArray(R.array.quote_question_1);
			answers = r.getStringArray(R.array.quote_answer_1);
			break;
		case 2:
			questions = r.getStringArray(R.array.quote_question_2);
			answers = r.getStringArray(R.array.quote_answer_2);
			break;
		case 3:
			questions = r.getStringArray(R.array.quote_question_3);
			answers = r.getStringArray(R.array.quote_answer_3);
			break;
		case 4:
			questions = r.getStringArray(R.array.quote_question_4);
			answers = r.getStringArray(R.array.quote_answer_4);
			break;
		case 5:
			questions = r.getStringArray(R.array.quote_question_5);
			answers = r.getStringArray(R.array.quote_answer_5);
			break;
		case 6:
			questions = r.getStringArray(R.array.quote_question_6);
			answers = r.getStringArray(R.array.quote_answer_6);
			break;
		case 7:
			questions = r.getStringArray(R.array.quote_question_7);
			answers = r.getStringArray(R.array.quote_answer_7);
			break;
		case 8:
			questions = r.getStringArray(R.array.quote_question_8);
			answers = r.getStringArray(R.array.quote_answer_8);
			break;
		case 9:
			questions = r.getStringArray(R.array.quote_question_9);
			answers = r.getStringArray(R.array.quote_answer_9);
			break;
		case 10:
			questions = r.getStringArray(R.array.quote_question_10);
			answers = r.getStringArray(R.array.quote_answer_10);
			break;
		case 11:
			questions = r.getStringArray(R.array.quote_question_11);
			answers = r.getStringArray(R.array.quote_answer_11);
			break;
		default:
			questions = r.getStringArray(R.array.quote_question_0);
			answers = r.getStringArray(R.array.quote_answer_0);
			break;
		}

		Random rand = new Random();
		int qid = rand.nextInt(3);
		question = questions[qid];
		answer = new String(answers[qid * 5]);

		String[] tempSelection = new String[4];
		for (int i = 0; i < tempSelection.length; ++i)
			tempSelection[i] = answers[qid * 5 + i + 1];
		shuffleArray(tempSelection);
		String[] selectAns = new String[3];
		for (int i = 0; i < selectAns.length; ++i)
			selectAns[i] = tempSelection[i];

		int ans_id = rand.nextInt(selectAns.length);
		selectAns[ans_id] = answer;

		return selectAns;
	}

	private static void shuffleArray(String[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; --i) {
			int index = rnd.nextInt(i + 1);
			String a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	private View createSelectionView(String[] selectionStrs) {
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_multi_select_item, null);
		selections[0] = (TextView) layout.findViewById(R.id.question_select0);
		selections[1] = (TextView) layout.findViewById(R.id.question_select1);
		selections[2] = (TextView) layout.findViewById(R.id.question_select2);

		for (int i = 0; i < selectionStrs.length; ++i) {
			selections[i].setText(selectionStrs[i]);
			selections[i].setOnClickListener(new SelectionOnClickListener());
			selections[i].setTypeface(wordTypefaceBold);
		}

		return layout;
	}

	private boolean agreementChange = false;

	private View createSeekBarView() {
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.question_seekbar_item, null);
		agreementSeekbar = (SeekBar) layout.findViewById(R.id.question_seek_bar);
		agreementText = (TextView) layout.findViewById(R.id.question_seekbar_message);
		agreementLevel = App.context.getResources().getStringArray(R.array.agreement);
		agreementText.setText(agreementLevel[2]);
		agreementText.setTypeface(wordTypefaceBold);
		agreementSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				agreementText.setText(agreementLevel[arg1]);
				agreementText.invalidate();
				agreementChange = true;
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}
		});
		return layout;
	}

	private class SelectionOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			selectedAnswer = ((TextView) v).getText().toString();
			for (int i = 0; i < selections.length; ++i)
				selections[i].setBackgroundResource(0);
			v.setBackgroundColor(0x55DDCCAA);
			ClickLog.Log(ClickLogId.STORYTELLING_TEST_SELECT);
		}
	}

	private class SubmitOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (selectedAnswer == null || selectedAnswer.length() == 0)
				CustomToastSmall.generateToast(R.string.storytelling_test_toast);
			else {
				boolean isCorrect = false;
				int agreement = agreementSeekbar.getProgress();
				if (selectedAnswer.equals(answer))
					isCorrect = true;
				DatabaseControl db = new DatabaseControl();
				int addScore = db.insertStorytellingTest(new StorytellingTest(System.currentTimeMillis(), image_week,
						isCorrect, selectedAnswer, agreement, 0));
				if (!isCorrect)
					CustomToast.generateToast(R.string.storytelling_test_incorrect, -1);
				else{
					if (PreferenceControl.checkCouponChange())
						PreferenceControl.setCouponChange(true);
					CustomToast.generateToast(R.string.storytelling_test_correct, addScore);
				}
				if (agreementChange)
					ClickLog.Log(ClickLogId.STORYTELLING_TEST_SUBMIT);
				else
					ClickLog.Log(ClickLogId.STORYTELLING_TEST_SUBMIT_EMPTY);
				finish();
			}

		}
	}

	@Override
	protected void onResume() {
		agreementChange = false;
		super.onResume();
		ClickLog.Log(ClickLogId.STORYTELLING_TEST_ENTER);
	}

	@Override
	protected void onPause() {
		ClickLog.Log(ClickLogId.STORYTELLING_TEST_LEAVE);
		super.onPause();
	}

}
