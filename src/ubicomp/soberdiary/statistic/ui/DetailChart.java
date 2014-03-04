package ubicomp.soberdiary.statistic.ui;

import ubicomp.soberdiary.data.structure.Rank;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.Typefaces;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DetailChart {

	
	private FrameLayout layout;
	private TextView title;
	private RelativeLayout[] items = new RelativeLayout[3];
	private TextView[] subtitles = new TextView[3];
	private TextView[] comments = new TextView[3];
	private ImageView[] bars = new ImageView[3];
	private ImageView[] bar_starts = new ImageView[3];
	private ImageView[] bar_ends = new ImageView[3];
	private ImageView[] bar_progress = new ImageView[3];
	
	private String[] detectionComment = App.context.getResources().getStringArray(R.array.radar_0);
	private String[] adviceQuestionComment = App.context.getResources().getStringArray(R.array.radar_1_0);
	private String[] adviceEmotionDIYComment = App.context.getResources().getStringArray(R.array.radar_1_1);
	private String[] manageVoiceComment = App.context.getResources().getStringArray(R.array.radar_2_0);
	private String[] manageEmotionComment = App.context.getResources().getStringArray(R.array.radar_2_1);
	private String[] manageAdditionalComment = App.context.getResources().getStringArray(R.array.radar_2_2);
	private String[] storyReadComment = App.context.getResources().getStringArray(R.array.radar_3_0);
	private String[] storyTestComment = App.context.getResources().getStringArray(R.array.radar_3_1);
	private String[] storyFbComment = App.context.getResources().getStringArray(R.array.radar_3_2);
	
	public static final int TYPE_DETECTION = 0;
	public static final int TYPE_ADVICE = 1;
	public static final int TYPE_MANAGE = 2;
	public static final int TYPE_STORY = 3;
	
	private int type = TYPE_DETECTION;
	private Rank rank = new Rank("",0);
	
	private DrawHandler handler = new DrawHandler();
	
	public DetailChart(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (FrameLayout) inflater.inflate(R.layout.detail_chart, null);
		
		Typeface wordTypeface = Typefaces.getWordTypeface();
		Typeface wordTypefaceBold = Typefaces.getWordTypefaceBold();
		
		
		title = (TextView) layout.findViewById(R.id.detail_title);
		title.setTypeface(wordTypefaceBold);
		
		items[0] = (RelativeLayout) layout.findViewById(R.id.detail_item_0);
		items[1] = (RelativeLayout) layout.findViewById(R.id.detail_item_1);
		items[2] = (RelativeLayout) layout.findViewById(R.id.detail_item_2);
		
		subtitles[0] = (TextView) layout.findViewById(R.id.detail_item_text_0);
		subtitles[1] = (TextView) layout.findViewById(R.id.detail_item_text_1);
		subtitles[2] = (TextView) layout.findViewById(R.id.detail_item_text_2);
		
		comments[0] = (TextView) layout.findViewById(R.id.detail_comment_0);
		comments[1] = (TextView) layout.findViewById(R.id.detail_comment_1);
		comments[2] = (TextView) layout.findViewById(R.id.detail_comment_2);
		
		for (int i=0;i<3;++i){
			subtitles[i].setTypeface(wordTypefaceBold);
			comments[i].setTypeface(wordTypeface);
		}
		
		bars[0] = (ImageView) layout.findViewById(R.id.detail_progress_bg_0);
		bars[1] = (ImageView) layout.findViewById(R.id.detail_progress_bg_1);
		bars[2] = (ImageView) layout.findViewById(R.id.detail_progress_bg_2);
		
		bar_starts[0] = (ImageView) layout.findViewById(R.id.detail_progress_start_0);
		bar_starts[1] = (ImageView) layout.findViewById(R.id.detail_progress_start_1);
		bar_starts[2] = (ImageView) layout.findViewById(R.id.detail_progress_start_2);
		
		bar_ends[0] = (ImageView) layout.findViewById(R.id.detail_progress_end_0);
		bar_ends[1] = (ImageView) layout.findViewById(R.id.detail_progress_end_1);
		bar_ends[2] = (ImageView) layout.findViewById(R.id.detail_progress_end_2);
		
		bar_progress[0] = (ImageView) layout.findViewById(R.id.detail_progress_inner_0);
		bar_progress[1] = (ImageView) layout.findViewById(R.id.detail_progress_inner_1);
		bar_progress[2] = (ImageView) layout.findViewById(R.id.detail_progress_inner_2);
		
	}
	
	public void setting(int type, Rank rank){
		this.type = type;
		this.rank = rank;
		handler.sendEmptyMessage(0);
	}
	
	public void hide(){
		items[0].setVisibility(View.INVISIBLE);
		items[1].setVisibility(View.INVISIBLE);
		items[2].setVisibility(View.INVISIBLE);
	}
	
	public View getView(){
		return layout;
	}
	
	private class DrawHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg){
			
			int total_len = bars[0].getWidth() - bar_starts[0].getWidth() - bar_ends[0].getWidth();
			RelativeLayout.LayoutParams[] params = new RelativeLayout.LayoutParams[3];
			for (int i=0;i<3;++i)
				params[i] = (LayoutParams) bar_progress[i].getLayoutParams();
			
			int len =0;
			int idx = 0;
			
			switch(type){
			case TYPE_DETECTION:
				title.setText(R.string.radar_label0_full);
				
				len = Math.min(rank.test * total_len/600,total_len);
				idx = Math.min(rank.test*3/600,detectionComment.length-1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label0_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(detectionComment[idx]);
				
				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.INVISIBLE);
				items[2].setVisibility(View.INVISIBLE);
				break;
				
			case TYPE_ADVICE:
				title.setText(R.string.radar_label1_full);
				
				len = Math.min(rank.advice_questionnaire*total_len/300, total_len);
				idx = Math.min(rank.advice_questionnaire*3/300, adviceQuestionComment.length-1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label1_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(adviceQuestionComment[idx]);
				
				len = Math.min(rank.advice_emotion_diy*total_len/300, total_len);
				idx = Math.min(rank.advice_emotion_diy*3/300, adviceEmotionDIYComment.length-1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label1_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(adviceEmotionDIYComment[idx]);
				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.VISIBLE);
				items[2].setVisibility(View.INVISIBLE);
				break;
				
			case TYPE_MANAGE:
				title.setText(R.string.radar_label2_full);
				
				len = Math.min(rank.manage_voice*total_len/300, total_len);
				idx = Math.min(rank.manage_voice*3/300, manageVoiceComment.length-1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label2_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(manageVoiceComment[idx]);
				
				len = Math.min(rank.manage_emotion*total_len/300, total_len);
				idx = Math.min(rank.manage_emotion*3/300, manageEmotionComment.length-1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label2_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(manageEmotionComment[idx]);
				
				len = Math.min(rank.manage_additional*total_len/100, total_len);
				idx = Math.min(rank.manage_emotion*2/100, manageAdditionalComment.length-1);
				params[2].width = len;
				subtitles[2].setText(R.string.radar_label2_2);
				items[2].updateViewLayout(bar_progress[2], params[2]);
				comments[2].setText(manageAdditionalComment[idx]);
				
				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.VISIBLE);
				items[2].setVisibility(View.VISIBLE);
				break;
				
			case TYPE_STORY:
				title.setText(R.string.radar_label3_full);
				
				len = Math.min(rank.story_read*total_len/300, total_len);
				idx = Math.min(rank.story_read*3/300, storyReadComment.length-1);
				params[0].width = len;
				subtitles[0].setText(R.string.radar_label3_0);
				items[0].updateViewLayout(bar_progress[0], params[0]);
				comments[0].setText(storyReadComment[idx]);
				
				len = Math.min(rank.story_test*total_len/300, total_len);
				idx = Math.min(rank.story_test*3/300, storyTestComment.length-1);
				params[1].width = len;
				subtitles[1].setText(R.string.radar_label3_1);
				items[1].updateViewLayout(bar_progress[1], params[1]);
				comments[1].setText(storyTestComment[idx]);
				
				len = Math.min(rank.story_fb*total_len/100*7, total_len);
				idx = Math.min(rank.story_fb*2/100*7, storyFbComment.length-1);
				params[2].width = len;
				subtitles[2].setText(R.string.radar_label3_2);
				items[2].updateViewLayout(bar_progress[2], params[2]);
				comments[2].setText(storyFbComment[idx]);
				
				items[0].setVisibility(View.VISIBLE);
				items[1].setVisibility(View.VISIBLE);
				items[2].setVisibility(View.VISIBLE);
				break;
			default:
				items[0].setVisibility(View.INVISIBLE);
				items[1].setVisibility(View.INVISIBLE);
				items[2].setVisibility(View.INVISIBLE);
			}
		}
		
	}
	
}
