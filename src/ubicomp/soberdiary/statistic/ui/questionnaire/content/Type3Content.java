package ubicomp.soberdiary.statistic.ui.questionnaire.content;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.statistic.ui.QuestionnaireDialog;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.EmotionDIYOnClickListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.GoHomeOnClickListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.SelectedListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.SelfOnClickListener;

public class Type3Content extends QuestionnaireContent {

	public Type3Content(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		msgBox.showDialog();
		setHelp(R.string.question_type3_help);
		setSelectItem(R.string.go_home, new SelectedListener(msgBox,new GoHomeOnClickListener(msgBox),R.string.ok));
		setSelectItem(R.string.self_help,new SelectedListener(msgBox,new SelfOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.start_emotion_diy_help,new SelectedListener(msgBox,new EmotionDIYOnClickListener(msgBox),R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

}
