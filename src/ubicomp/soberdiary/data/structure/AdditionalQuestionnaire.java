package ubicomp.soberdiary.data.structure;

public class AdditionalQuestionnaire {

	private TimeValue tv;
	private boolean addedScore;
	private int emotion;
	private int craving;
	private int score;
	
	public AdditionalQuestionnaire(long timestamp, boolean addedScore,int emotion, int craving, int score){
		this.tv=TimeValue.generate(timestamp);
		this.addedScore = addedScore;
		this.emotion = emotion;
		this.craving = craving;
		this.score = score;
	}

	public TimeValue getTv() {
		return tv;
	}

	public boolean isAddedScore() {
		return addedScore;
	}

	public int getEmotion() {
		return emotion;
	}

	public int getCraving() {
		return craving;
	}

	public int getScore() {
		return score;
	}
}
