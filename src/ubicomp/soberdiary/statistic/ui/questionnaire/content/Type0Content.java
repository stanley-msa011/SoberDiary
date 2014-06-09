package ubicomp.soberdiary.statistic.ui.questionnaire.content;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.statistic.ui.QuestionnaireDialog;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.BreathOnClickListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.EmotionDIYOnClickListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.InspireOnClickListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.SelectedListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.TryAgainDoneOnClickListener;
import ubicomp.soberdiary.system.config.PreferenceControl;

public class Type0Content extends QuestionnaireContent {

	public Type0Content(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		msgBox.showDialog();
		setHelp(R.string.question_type0_help);
		setSelectItem(R.string.breath_help, new SelectedListener(msgBox, new BreathOnClickListener(msgBox),
				R.string.next));
		setSelectItem(R.string.inspire_help, new SelectedListener(msgBox, new InspireOnClickListener(msgBox),
				R.string.next));
		setSelectItem(R.string.start_emotion_diy_help, new SelectedListener(msgBox, new EmotionDIYOnClickListener(
				msgBox), R.string.next));
		if (PreferenceControl.isDeveloper())
			setSelectItem(R.string.try_again, new SelectedListener(msgBox, new TryAgainDoneOnClickListener(msgBox),
					R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

}
