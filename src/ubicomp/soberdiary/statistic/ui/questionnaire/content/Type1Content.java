package ubicomp.soberdiary.statistic.ui.questionnaire.content;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.EmotionDIYOnClickListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.HotLineOnClickListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.FamilyCallOnClickListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.ReadingOnClickListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.SelectedListener;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.SocialCallOnClickListener;

public class Type1Content extends QuestionnaireContent {

	public Type1Content(QuestionnaireBox msgBox) {
		super(msgBox);
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		seq.clear();
		msgBox.openBox();
		setHelp(R.string.question_type1_help);
		setSelectItem(R.string.read_sentence, new SelectedListener(msgBox,new ReadingOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.connect_to_family, new SelectedListener (msgBox,new FamilyCallOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.connect_to_emotion_hot_line, new SelectedListener(msgBox,new HotLineOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.connect_for_social_help, new SelectedListener(msgBox,new SocialCallOnClickListener(msgBox),R.string.next));
		setSelectItem(R.string.start_emotion_diy_help,new SelectedListener(msgBox,new EmotionDIYOnClickListener(msgBox),R.string.next));
		msgBox.showQuestionnaireLayout(true);
	}

}
