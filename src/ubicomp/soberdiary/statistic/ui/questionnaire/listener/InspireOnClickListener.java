package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import ubicomp.soberdiary.statistic.ui.QuestionnaireDialog;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.InspireContent;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import android.view.View;

public class InspireOnClickListener extends QuestionnaireOnClickListener {

	public InspireOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View arg0) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_INSPIRE);
		seq.add(1);
		contentSeq.add(new InspireContent(msgBox));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
