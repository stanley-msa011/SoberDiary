package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import ubicomp.soberdiary.statistic.ui.QuestionnaireDialog;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.CallCheckContent;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import android.view.View;

public class CallCheckOnClickListener extends QuestionnaireOnClickListener {

	private String name,phone;
	public CallCheckOnClickListener(QuestionnaireDialog msgBox,String name,String phone) {
		super(msgBox);
		this.name = name;
		this.phone = phone;
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_CALL_CHECK);
		contentSeq.add(new CallCheckContent(msgBox,name,phone));
		contentSeq.get(contentSeq.size()-1).onPush();
	}

}
