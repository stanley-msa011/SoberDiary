package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import java.util.ArrayList;

import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.QuestionnaireContent;
import android.view.View.OnClickListener;

public abstract class QuestionnaireOnClickListener implements OnClickListener {

	protected QuestionnaireBox msgBox;
	protected ArrayList<Integer>seq;
	protected ArrayList <QuestionnaireContent> contentSeq;
	
	public QuestionnaireOnClickListener (QuestionnaireBox msgBox){
		this.msgBox = msgBox;
		this.seq = msgBox.getClickSequence();
		this.contentSeq = msgBox.getQuestionSequence();
	}
	
}
