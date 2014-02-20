package ubicomp.soberdiary.data.structure;

public class Detection{

	public float brac;
	public TimeValue tv;
	public int emotion;
	public int craving;
	public boolean isPrime;
	public int weeklyScore,score;
	public static final float BRAC_THRESHOLD = 0.06f;
	public static final float BRAC_THRESHOLD_HIGH = 0.25f;
	private final static int MAX_WEEKLY_SCORE = 42;
	
	public Detection(float brac, long timestamp, int emotion, int craving, boolean isPrime,int weeklyScore, int score) {
		this.brac = brac;
		this.tv = TimeValue.generate(timestamp);
		this.emotion = emotion;
		this.craving = craving;
		this.isPrime = isPrime;
		this.weeklyScore = weeklyScore;
		this.score = score;
	}
	
	public boolean isSameTimeBlock(Detection d){
		return d!=null && tv!=null && tv.isSameTimeBlock(d.tv);
	}
	
	public boolean isPass(){
		return brac<BRAC_THRESHOLD;
	}
	
	public static float weeklyScoreToProgress(int score){
		float progress =  (float)score*100F/MAX_WEEKLY_SCORE;
		if (progress > 100.f)
			return 100.f;
		return progress;
	}
	
}
