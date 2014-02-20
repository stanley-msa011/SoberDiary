package ubicomp.soberdiary.data.structure;

public class FacebookInfo {

	public TimeValue tv;
	public int pageWeek;
	public int pageLevel;
	public String text;
	public boolean addedScore;
	public boolean uploadSuccess;
	public int privacy;
	public int score;
	
	public static final int FRIEND = 0;
	public static final int SELF = 1;
	
	public FacebookInfo(long ts,int pageWeek,int pageLevel,String text,boolean addedScore, boolean uploadSuccess,int privacy,int score){
		this.tv = TimeValue.generate(ts);
		this.pageWeek = pageWeek;
		this.pageLevel = pageLevel;
		this.text = text==null?"":text;
		this.addedScore = addedScore;
		this.uploadSuccess = uploadSuccess;
		this.privacy = privacy;
		this.score = score;
		
	}
}
