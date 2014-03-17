package ubicomp.soberdiary.statistic.ui;

import java.util.ArrayList;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.Questionnaire;
import ubicomp.soberdiary.main.ui.Typefaces;
import ubicomp.soberdiary.main.ui.toast.CustomToast;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.QuestionnaireContent;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.Type0Content;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.Type1Content;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.Type2Content;
import ubicomp.soberdiary.statistic.ui.questionnaire.content.Type3Content;
import ubicomp.soberdiary.system.clicklog.ClickLogId;
import ubicomp.soberdiary.system.clicklog.ClickLog;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class QuestionnaireBox {

	private ArrayList<Integer> clickSequence;
	private ArrayList<QuestionnaireContent> contentSequence;
	
	private QuestionnaireBoxUpdater quesBoxUpdater;
	private Context context;
	private LayoutInflater inflater;
	private RelativeLayout boxLayout = null;
	
	private RelativeLayout mainLayout;
	private LinearLayout questionLayout;
	private ImageView closeButton;
	private TextView help,next;
	private Drawable choiceDrawable, choiceSelectedDrawable;
	private Resources r;
	private DatabaseControl db;
	private Typeface wordTypefaceBold;
	private int type;
	
	private MediaPlayer mediaPlayer;
	
	private LinearLayout.LayoutParams questionParam;
	
	public QuestionnaireBox(QuestionnaireBoxUpdater quesBoxUpdater,RelativeLayout mainLayout){
		this.context = quesBoxUpdater.getContext();
		this.quesBoxUpdater = quesBoxUpdater;
		this.r = context.getResources();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mainLayout = mainLayout;
		db = new DatabaseControl();
		clickSequence = new ArrayList<Integer>();
		contentSequence = new ArrayList<QuestionnaireContent>();
		type = -1;
		
		setting();
	}
	
	private void setting(){
		
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		
		boxLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_statistic_questionnaire,null);
		boxLayout.setVisibility(View.INVISIBLE);
		
		questionLayout = (LinearLayout) boxLayout.findViewById(R.id.question_layout);
		questionParam = (LinearLayout.LayoutParams) questionLayout.getLayoutParams();
		
		help = (TextView) boxLayout.findViewById(R.id.question_text);
		next = (TextView) boxLayout.findViewById(R.id.question_next);
		
		closeButton = (ImageView) boxLayout.findViewById(R.id.question_exit);
	}
	
	@SuppressLint("InlinedApi")
	public void load(){
		
		mainLayout.addView(boxLayout);
		RelativeLayout.LayoutParams mainParam = (LayoutParams) boxLayout.getLayoutParams();
		mainParam.width = mainParam.height = LayoutParams.MATCH_PARENT;
		help.setTypeface(wordTypefaceBold);
		next.setTypeface(wordTypefaceBold);
		choiceDrawable = r.getDrawable(R.drawable.radio_button_normal);
		choiceSelectedDrawable = r.getDrawable(R.drawable.radio_button_checked);
		closeButton.setOnClickListener(new ExitListener());
	}
	
	public void clear(){
		closeMediaPlayer();
		if (boxLayout !=null)
			mainLayout.removeView(boxLayout);
	}
	
	public void generateType0Box(){
		type = 0;
		showCloseButton(true);
		setNextButton("",null);
		contentSequence.clear();
		contentSequence.add(new Type0Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType1Box(){
		type = 1;
		showCloseButton(true);
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type1Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType2Box(){
		type = 2;
		showCloseButton(true);
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type2Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateType3Box(){
		type = 3;
		showCloseButton(true);
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type3Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void generateNormalBox(){
		type = -1;
		showCloseButton(true);
		setNextButton("", null);
		contentSequence.clear();
		contentSequence.add(new Type0Content(this));
		contentSequence.get(contentSequence.size()-1).onPush();
	}
	
	public void openBox(){
		quesBoxUpdater.enablePage(false);
		boxLayout.setVisibility(View.VISIBLE);
		return;
}
	
	public void closeBox(){
		closeBox(R.string.after_questionnaire);
	}
	
	public void closeMediaPlayer(){
		if (mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	
	public MediaPlayer setMediaPlayer(int id){
		closeMediaPlayer();
		mediaPlayer =MediaPlayer.create(getContext(), id);
		return mediaPlayer;
	}
	
	public void closeBox(int toast_str_id){
		closeMediaPlayer();
		
		PreferenceControl.setTestResult(-1);
    	
    	int addScore = insertSeq();
    	CustomToast.generateToast(toast_str_id, addScore);
    	quesBoxUpdater.updateSelfHelpCounter();
    	
    	quesBoxUpdater.enablePage(true);
		boxLayout.setVisibility(View.INVISIBLE);
		quesBoxUpdater.setQuestionAnimation();
	}
	
	public void closeBoxCall(){
		
		closeMediaPlayer();
		PreferenceControl.setTestResult(-1);
    	
    	 insertSeq();
    	
    	quesBoxUpdater.enablePage(true);
		boxLayout.setVisibility(View.INVISIBLE);
		quesBoxUpdater.setQuestionAnimation();
		return;
	}
	
	public void closeBoxNull(){
		
		closeMediaPlayer();
		quesBoxUpdater.enablePage(true);
		boxLayout.setVisibility(View.INVISIBLE);
		return;
	}
	
	public Context getContext(){
		return context;
	}
	
	public Drawable getChoiceDrawable(){
		return choiceDrawable;
	}
	
	public Drawable getChoiceSelectedDrawable(){
		return choiceSelectedDrawable;
	}
	
	public LinearLayout getQuestionnaireLayout(){
		return questionLayout; 
	}
	
	public ArrayList<Integer> getClickSequence(){
		return clickSequence;
	}
	
	public ArrayList<QuestionnaireContent> getQuestionSequence(){
		return contentSequence;
	}
	
	private int insertSeq(){
		int addScore = db.insertQuestionnaire(new Questionnaire(System.currentTimeMillis(),type,seq_toString(),0));
		return addScore;
	}
	

	private String seq_toString(){
		int size = clickSequence.size();
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<size;++i){
			sb.append(clickSequence.get(i));
			if (i<size-1)
				sb.append(",");
		}
		return sb.toString();
	}
	
	public void setHelpMessage(String str){
		help.setText(str);
	}
	
	public void setHelpMessage(int str_id){
		help.setText(str_id);
	}
	
	public void setNextButton(String str, View.OnClickListener listener){
		next.setText(str);
		next.setOnClickListener(listener);
	}
	
	public void setNextButton(int str_id, View.OnClickListener listener){
		next.setText(str_id);
		next.setOnClickListener(listener);
	}
	
	public Typeface getTypeface(){
		return wordTypefaceBold;
	}
	
	public void cleanSelection(){
		int idx = contentSequence.size()-1;
		if (idx >=0)
			contentSequence.get(idx).cleanSelection();
	}
	
	public void showQuestionnaireLayout(boolean visible){
		if (visible)
			questionParam.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		else
			questionParam.height = 0;
	}
	
	
	private class ExitListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			clickSequence.clear();
			contentSequence.clear();
			quesBoxUpdater.enablePage(true);
			boxLayout.setVisibility(View.INVISIBLE);
			ClickLog.Log(ClickLogId.STATISTIC_QUESTION_EXIT);
		}
		
	}
	
	public int getType(){
		return type;
	}
	
	public void showCloseButton(boolean show){
		if (show)
			closeButton.setVisibility(View.VISIBLE);
		else
			closeButton.setVisibility(View.INVISIBLE);
		
	}
}
