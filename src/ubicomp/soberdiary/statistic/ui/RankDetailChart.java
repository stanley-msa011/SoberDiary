package ubicomp.soberdiary.statistic.ui;

import ubicomp.soberdiary.data.structure.Rank;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.TextSize;
import ubicomp.soberdiary.main.ui.Typefaces;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.view.View;

public class RankDetailChart extends View {

	private Rank rank = new Rank("",0);
	
	private Paint chartLine, valueLine, valuePaint, titlePaint, labelPaint,commentPaint;
	
	private Typeface wordTypeface,wordTypefaceBold;
	private int titleSize,labelSize,textSize,barHeight;
	private String[] detectionComment = App.context.getResources().getStringArray(R.array.radar_0);
	private String[] adviceQuestionComment = App.context.getResources().getStringArray(R.array.radar_1_0);
	private String[] adviceEmotionDIYComment = App.context.getResources().getStringArray(R.array.radar_1_1);
	private String[] manageVoiceComment = App.context.getResources().getStringArray(R.array.radar_2_0);
	private String[] manageEmotionComment = App.context.getResources().getStringArray(R.array.radar_2_1);
	private String[] manageAdditionalComment = App.context.getResources().getStringArray(R.array.radar_2_2);
	private String[] storyReadComment = App.context.getResources().getStringArray(R.array.radar_3_0);
	private String[] storyTestComment = App.context.getResources().getStringArray(R.array.radar_3_1);
	private String[] storyFbComment = App.context.getResources().getStringArray(R.array.radar_3_2);
	
	
	private int drawHeight = 0;
	
	private int TYPE = TYPE_DETECTION;
	
	public RankDetailChart(Context context) {
		super(context);
		this.setBackgroundResource(R.drawable.toast_small);
		wordTypefaceBold = Typefaces.getWordTypefaceBold();
		wordTypeface = Typefaces.getWordTypeface();
		titleSize = TextSize.largeTitleSize();
		labelSize = TextSize.smallTitleTextSize();
		textSize = TextSize.normalTextSize();
		barHeight = labelSize/2;
		createPaints();
	}
	
	public static final int TYPE_DETECTION = 0;
	public static final int TYPE_ADVICE = 1;
	public static final int TYPE_MANAGE = 2;
	public static final int TYPE_STORY = 3;
	
	
	public void setRank(Rank rank, int type){
		this.rank = rank;
		switch (type){
		case TYPE_DETECTION:
			drawHeight = itemHeight1();
			break;
		case TYPE_ADVICE:
			drawHeight = itemHeight2();
			break;
		default:
			drawHeight = itemHeight3();
			break;
		}
		TYPE = type;
	}
	
	private void createPaints(){
		chartLine = new Paint();
		chartLine.setColor(0xFFababab);
		chartLine.setStyle(Style.STROKE);
		chartLine.setStrokeWidth(2);
		
		valueLine = new Paint();
		valueLine.setColor(0xFFF19700);
		valueLine.setStyle(Style.FILL_AND_STROKE);
		valueLine.setStrokeWidth(2);
		
		valuePaint = new Paint();
		valuePaint.setColor(0xFFF19700);
		valuePaint.setStyle(Style.FILL);
		valuePaint.setAlpha(100);
		
		titlePaint = new Paint();
		titlePaint.setColor(0xFF000000);
		titlePaint.setTextSize(titleSize);
		titlePaint.setTypeface(wordTypefaceBold);
		titlePaint.setTextAlign(Align.LEFT);
		
		labelPaint = new Paint();
		labelPaint.setColor(0xFF000000);
		labelPaint.setTextSize(labelSize);
		labelPaint.setTypeface(wordTypefaceBold);
		labelPaint.setTextAlign(Align.LEFT);
		
		commentPaint = new Paint();
		commentPaint.setColor(0xFF000000);
		commentPaint.setTextSize(textSize);
		commentPaint.setTypeface(wordTypeface);
		commentPaint.setTextAlign(Align.LEFT);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas){
		Rect area = new Rect(getLeft(),getTop(),getRight(),getBottom());
		int limit_x = area.width();
		int left_margin = limit_x/20; 
		int top_margin = limit_x/10;
		int block_width = limit_x/10;
		
		switch(TYPE){
		case TYPE_DETECTION:
			top_margin = drawDetection(canvas,left_margin,top_margin,block_width);
			break;
		case TYPE_ADVICE:
			top_margin = drawAdvice(canvas,left_margin,top_margin,block_width);
			break;
		case TYPE_MANAGE:
			top_margin = drawManage(canvas,left_margin,top_margin,block_width);
			break;
		case TYPE_STORY:
			top_margin = drawStory(canvas,left_margin,top_margin,block_width);
			break;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

	    int desiredWidth = 100;
	    int desiredHeight = drawHeight;

	    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    int width;
	    int height;

	    if (widthMode == MeasureSpec.EXACTLY) {
	        width = widthSize;
	    } else if (widthMode == MeasureSpec.AT_MOST) {
	        width = Math.min(desiredWidth, widthSize);
	    } else {
	        width = desiredWidth;
	    }

	    if (heightMode == MeasureSpec.EXACTLY) {
	        height = heightSize;
	    } else if (heightMode == MeasureSpec.AT_MOST) {
	        height = Math.min(desiredHeight, heightSize);
	    } else {
	        height = desiredHeight;
	    }

	    setMeasuredDimension(width, height);
	}
	private int drawDetection(Canvas canvas,int left_margin,int top_margin,int block_width){
		canvas.drawText(App.context.getString(R.string.radar_label0), left_margin, top_margin, labelPaint);
		top_margin += barHeight;
		int len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, chartLine);
		len = rank.test*len/600;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, valueLine);
		top_margin+=labelSize+barHeight;
		int idx = rank.test*3/600;
		idx = Math.min(idx,detectionComment.length-1);
		canvas.drawText(detectionComment[idx], left_margin, top_margin, commentPaint);
		top_margin+=labelSize+barHeight;
		return top_margin;
	}
	
	private int itemHeight1(){
		Rect area = new Rect(getLeft(),getTop(),getRight(),getBottom());
		int top = area.width()/10;
		return top+labelSize*5;
	}
	
	private int itemHeight2(){
		Rect area = new Rect(getLeft(),getTop(),getRight(),getBottom());
		int top = area.width()/10;
		return top+labelSize*17/2;
	}
	
	private int itemHeight3(){
		Rect area = new Rect(getLeft(),getTop(),getRight(),getBottom());
		int top = area.width()/10;
		return top+labelSize*12;
	}
	
	
	private int drawAdvice(Canvas canvas,int left_margin,int top_margin,int block_width){
		String advice = App.context.getString(R.string.radar_label1);
		canvas.drawText(advice+"-"+App.context.getString(R.string.radar_label1_0), left_margin, top_margin, labelPaint);
		top_margin += barHeight;
		int len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, chartLine);
		len = rank.advice_questionnaire*len/300;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, valueLine);
		top_margin+=labelSize+barHeight;
		int idx = rank.advice_questionnaire*3/300;
		idx = Math.min(idx,adviceQuestionComment.length-1);
		canvas.drawText(adviceQuestionComment[idx], left_margin, top_margin, commentPaint);
		top_margin+=labelSize+barHeight;
		
		canvas.drawText(advice+"-"+App.context.getString(R.string.radar_label1_1), left_margin, top_margin, labelPaint);
		top_margin += barHeight;
		len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, chartLine);
		len = rank.advice_emotion_diy*len/300;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, valueLine);
		top_margin+=labelSize+barHeight;
		idx = rank.advice_emotion_diy*3/300;
		idx = Math.min(idx,adviceEmotionDIYComment.length-1);
		canvas.drawText(adviceEmotionDIYComment[idx], left_margin, top_margin, commentPaint);
		top_margin+=labelSize+barHeight;
		return top_margin;
	}
	
	private int drawManage(Canvas canvas,int left_margin,int top_margin,int block_width){
		String manage = App.context.getString(R.string.radar_label2);
		canvas.drawText(manage+"-"+App.context.getString(R.string.radar_label2_0), left_margin, top_margin, labelPaint);
		top_margin += barHeight;
		int len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, chartLine);
		len = rank.manage_voice*len/300;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, valueLine);
		top_margin+=labelSize+barHeight;
		int idx = rank.manage_voice*3/300;
		idx = Math.min(idx,manageVoiceComment.length-1);
		canvas.drawText(manageVoiceComment[idx], left_margin, top_margin, commentPaint);
		top_margin+=labelSize+barHeight;
		
		canvas.drawText(manage+"-"+App.context.getString(R.string.radar_label2_1), left_margin, top_margin, labelPaint);
		top_margin += barHeight;
		len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, chartLine);
		len = rank.manage_emotion*len/300;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, valueLine);
		top_margin+=labelSize+barHeight;
		idx = rank.manage_emotion*3/300;
		idx = Math.min(idx,manageEmotionComment.length-1);
		canvas.drawText(manageEmotionComment[idx], left_margin, top_margin, commentPaint);
		top_margin+=labelSize+barHeight;
		
		canvas.drawText(manage+"-"+App.context.getString(R.string.radar_label2_2), left_margin, top_margin, labelPaint);
		top_margin += barHeight;
		len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, chartLine);
		len = rank.manage_additional*len/100;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, valueLine);
		top_margin+=labelSize+barHeight;
		idx = rank.manage_additional*2/100;
		idx = Math.min(idx,manageAdditionalComment.length-1);
		canvas.drawText(manageAdditionalComment[idx], left_margin, top_margin, commentPaint);
		top_margin+=labelSize+barHeight;
		return top_margin;
	}
	
	private int drawStory(Canvas canvas,int left_margin,int top_margin,int block_width){
		String story = App.context.getString(R.string.radar_label3);
		canvas.drawText(story+"-"+App.context.getString(R.string.radar_label3_0), left_margin, top_margin, labelPaint);
		top_margin += barHeight;
		int len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, chartLine);
		len = rank.story_read*len/300;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, valueLine);
		top_margin+=labelSize+barHeight;
		int idx = rank.story_read*3/300;
		idx = Math.min(idx,storyReadComment.length-1);
		canvas.drawText(storyReadComment[idx], left_margin, top_margin, commentPaint);
		top_margin+=labelSize+barHeight;
		
		canvas.drawText(story+"-"+App.context.getString(R.string.radar_label3_1), left_margin, top_margin, labelPaint);
		top_margin += barHeight;
		len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, chartLine);
		len = rank.story_test*len/300;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, valueLine);
		top_margin+=labelSize+barHeight;
		idx = rank.story_test*3/300;
		idx = Math.min(idx,storyTestComment.length-1);
		canvas.drawText(storyTestComment[idx], left_margin, top_margin, commentPaint);
		top_margin+=labelSize+barHeight;
		
		canvas.drawText(story+"-"+App.context.getString(R.string.radar_label3_2), left_margin, top_margin, labelPaint);
		top_margin += barHeight;
		len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, chartLine);
		len = rank.story_fb*len/100*7;
		if (len > 6*block_width)
			len = 6*block_width;
		canvas.drawRect(left_margin, top_margin, left_margin+len, top_margin+barHeight, valueLine);
		top_margin+=labelSize+barHeight;
		idx = rank.story_fb*2/100*7;
		idx = Math.min(idx,storyFbComment.length-1);
		canvas.drawText(storyFbComment[idx], left_margin, top_margin, commentPaint);
		top_margin+=labelSize+barHeight;
		return top_margin;
	}

}
