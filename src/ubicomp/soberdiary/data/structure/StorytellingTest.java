package ubicomp.soberdiary.data.structure;

public class StorytellingTest {

	public TimeValue tv;
	public int questionPage;
	public boolean isCorrect;
	public String selection;
	public int agreement;
	public int score;
	
	public StorytellingTest(long ts,int questionPage,boolean isCorrect,String selection,int agreement,int score){
		this.tv = TimeValue.generate(ts);
		this.questionPage = questionPage;
		this.isCorrect = isCorrect;
		this.selection = selection==null?"":selection;
		this.agreement = agreement;
		this.score = score;
	}
}
