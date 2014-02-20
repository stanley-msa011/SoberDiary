package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import android.view.View;
import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;

public class EndOnClickListener extends QuestionnaireOnClickListener {

	public EndOnClickListener(QuestionnaireBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_END);
		msgBox.closeBox();
	}

}
