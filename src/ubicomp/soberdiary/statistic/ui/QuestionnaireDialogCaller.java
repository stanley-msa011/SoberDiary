package ubicomp.soberdiary.statistic.ui;

import android.content.Context;
import ubicomp.soberdiary.main.ui.EnablePage;

public interface QuestionnaireDialogCaller extends EnablePage {
	public void setQuestionAnimation();
	public void updateSelfHelpCounter();
	public Context getContext();
}
