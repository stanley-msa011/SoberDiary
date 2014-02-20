package ubicomp.soberdiary.data.structure;

public class Rank {

	public String uid;
	public int score = 0;
	public int test = 0,advice = 0 ,manage= 0,story = 0;
	
	public int advice_questionnaire=0, advice_emotion_diy=0;
	public int manage_voice=0, manage_emotion=0, manage_additional=0;
	public int story_read=0,story_test=0,story_fb=0;
	
	public Rank(String uid,int score){
		this.uid = uid;
		this.score = score;
		
	}
	public Rank (String uid,int score, int test, int advice, int manage, int story, int[] additionals){
		this.uid = uid;
		this.score = score;
		this.test = test;
		this.advice = advice;
		this.manage = manage;
		this.story = story;
		this.advice_questionnaire = additionals[0];
		this.advice_emotion_diy = additionals[1];
		this.manage_voice = additionals[2];
		this.manage_emotion = additionals[3];
		this.manage_additional = additionals[4];
		this.story_read = additionals[5];
		this.story_test = additionals[6];
		this.story_fb = additionals[7];
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(uid+" "+score+"\n");
		sb.append("test<"+test+">\n");
		sb.append("advice<"+advice+","+advice_questionnaire+","+advice_emotion_diy+">\n");
		sb.append("manage<"+manage+","+manage_voice+','+manage_emotion+","+manage_additional+">\n");
		sb.append("story<"+story+","+story_read+","+story_test+","+story_fb+">\n");
		return sb.toString();
	}
	
}
