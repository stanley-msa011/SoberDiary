package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import android.view.View;
import ubicomp.soberdiary.main.MainActivity;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.config.PreferenceControl;

public class TryAgainDoneOnClickListener extends QuestionnaireOnClickListener {

	public TryAgainDoneOnClickListener(QuestionnaireBox msgBox) {
		super(msgBox);
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_TRYAGAIN);
		seq.add(8);
		msgBox.closeBox(R.string.try_again_toast);
		PreferenceControl.setUpdateDetection(true);
		MainActivity.getMainActivity().changeTab(0);
	}

}
