package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import ubicomp.soberdiary.statistic.ui.QuestionnaireDialog;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import android.view.View;

public class GoHomeOnClickListener extends QuestionnaireOnClickListener {

	public GoHomeOnClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View arg0) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_HOME);
		seq.add(7);
		msgBox.closeDialog();
	}

}
