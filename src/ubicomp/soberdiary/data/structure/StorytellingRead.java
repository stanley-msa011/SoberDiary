package ubicomp.soberdiary.data.structure;

public class StorytellingRead {

	public TimeValue tv;
	public boolean addedScore;
	public int page;
	public int score;
	
	public StorytellingRead(long ts, boolean addedScore, int page, int score){
		this.tv = TimeValue.generate(ts);
		this.addedScore = addedScore;
		this.page = page;
		this.score = score;
		
	}
}
