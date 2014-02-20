package ubicomp.soberdiary.data.structure;

public class AdditionalQuestionnaire {

	public TimeValue tv;
	public boolean addedScore;
	public int emotion;
	public int craving;
	public int score;
	
	public AdditionalQuestionnaire(long timestamp, boolean addedScore,int emotion, int craving, int score){
		this.tv = TimeValue.generate(timestamp);
		this.addedScore = addedScore;
		this.emotion = emotion;
		this.craving = craving;
		this.score = score;
	}
}
