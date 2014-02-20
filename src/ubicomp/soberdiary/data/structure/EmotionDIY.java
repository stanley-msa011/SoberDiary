package ubicomp.soberdiary.data.structure;

public class EmotionDIY {

	public TimeValue tv;
	public int selection;
	public String recreation;
	public int score;
	
	public EmotionDIY(long ts,int selection,String recreation,int score){
		this.tv = TimeValue.generate(ts);
		this.selection = selection;
		this.recreation = recreation==null?"":recreation;
		this.score = score;
	}
}
