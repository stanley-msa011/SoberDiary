package ubicomp.soberdiary.statistic.ui.questionnaire.listener;

import ubicomp.soberdiary.statistic.ui.QuestionnaireBox;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.SolutionContent;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import android.view.View;

public class SituationOnClickListener extends QuestionnaireOnClickListener {

	private int aid;
	public SituationOnClickListener(QuestionnaireBox msgBox,int aid) {
		super(msgBox);
		this.aid = aid;
	}

	@Override
	public void onClick(View v) {
		ClickLog.Log(ClickLogId.STATISTIC_QUESTION_SITUATION);
		seq.add(aid);
		contentSeq.add(new SolutionContent(msgBox,aid));
		contentSeq.get(contentSeq.size()-1).onPush();

	}

}
