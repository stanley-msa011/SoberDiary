package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import ubicomp.soberdiary.statistic.ui.QuestionnaireDialog;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.BreathContent;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import android.view.View;

public class BreathOnClickListener extends QuestionnaireOnClickListener {

	public BreathOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View arg0) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_BREATH);
		seq.add(0);
		contentSeq.add(new BreathContent(msgBox));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
