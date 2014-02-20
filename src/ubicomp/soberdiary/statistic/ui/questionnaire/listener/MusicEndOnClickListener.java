package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import android.util.Log;
import android.view.View;
import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;

public class MusicEndOnClickListener extends QuestionnaireOnClickListener {

	
	public MusicEndOnClickListener(QuestionnaireBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_END);
		Log.d("CONTENT","MUSIC END");
		msgBox.closeBox();
	}

}
