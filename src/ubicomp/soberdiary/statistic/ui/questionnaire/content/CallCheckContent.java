package ubicomp.soberdiary.statistic.ui.questionnaire.content;

import android.content.Context;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.statistic.ui.questionnaire.listener.CallDialOnClickListener;

public class CallCheckContent extends QuestionnaireContent {

	private String name,phone;
	private boolean isEmotion = false;
	
	public CallCheckContent(QuestionnaireBox msgBox,String name, String phone) {
		super(msgBox);
		this.name = name;
		this.phone = phone;
	}
	
	public CallCheckContent(QuestionnaireBox msgBox,String name, String phone,boolean isEmotion) {
		super(msgBox);
		this.name = name;
		this.phone = phone;
		this.isEmotion = isEmotion;
	}

	@Override
	protected void setContent() {
		msgBox.setNextButton("", null);
		if (isEmotion){
			setHelp(R.string.call_check_help_emotion_hot_line);
		}
		else{
			Context context = msgBox.getContext();
			String call_check = context.getString(R.string.call_check_help);
			String question_sign = context.getString(R.string.question_sign);
			setHelp(call_check+" "+name +" "+question_sign);
		}
		msgBox.showQuestionnaireLayout(false);
		msgBox.setNextButton(R.string.ok,new CallDialOnClickListener(msgBox,phone));
	}

}
