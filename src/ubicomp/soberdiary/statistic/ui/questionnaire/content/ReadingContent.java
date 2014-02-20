package ubicomp.soberdiary.statistic.ui.questionnaire.content;

import java.util.Random;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.EndOnClickListener;

public class ReadingContent extends QuestionnaireContent {

	private static String[] help;
	
	public ReadingContent(QuestionnaireBox msgBox) {
		super(msgBox);
		if (help==null)
			help = msgBox.getContext().getResources().getStringArray(R.array.question_reading_question);
	}

	@Override
	protected void setContent() {
		Random rand = new Random();
		int idx = rand.nextInt(help.length);
		
		setHelp(help[idx]);
		msgBox.showCloseButton(false);
		msgBox.showQuestionnaireLayout(false);
		msgBox.setNextButton(R.string.done,new EndOnClickListener(msgBox));
	}

}
