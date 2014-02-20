package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class CallDialOnClickListener extends QuestionnaireOnClickListener {

	private String phone;
	public CallDialOnClickListener(QuestionnaireBox msgBox,String phone) {
		super(msgBox);
		this.phone = phone;
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_CALL_OK);
		msgBox.closeBoxCall();
		Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+phone));
		msgBox.getContext().startActivity(intentDial);
	}

}
