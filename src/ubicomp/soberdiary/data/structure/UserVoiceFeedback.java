package ubicomp.soberdiary.data.structure;

public class UserVoiceFeedback {
	public TimeValue tv;
	public TimeValue detectionTv;
	public boolean testSuccess = false;
	public boolean hasData = false;
	
	public UserVoiceFeedback(long ts, long detectionTs,boolean testSuccess, boolean hasData){
		tv = TimeValue.generate(ts);
		detectionTv = TimeValue.generate(detectionTs);
		this.testSuccess = testSuccess;
		this.hasData = hasData;
	}

}
