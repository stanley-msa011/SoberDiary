package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import android.view.View;
import ubicomp.soberdiary.statistic.ui.QuestionnaireDialog;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;

public class CloseClickListener extends QuestionnaireOnClickListener {

	public CloseClickListener(QuestionnaireDialog msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_CLOSE);
		msgBox.closeBoxNull();
	}

}
