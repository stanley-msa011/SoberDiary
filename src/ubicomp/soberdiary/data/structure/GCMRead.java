package ubicomp.soberdiary.data.structure;

public class GCMRead {
	public TimeValue tv;
	public TimeValue readTv;
	public String message;
	public boolean read;
	
	public GCMRead(long ts, long readTs,String message, boolean read){
		this.tv = TimeValue.generate(ts);
		this.readTv = TimeValue.generate(readTs);
		this.message = message==null?"":message;
		this.read = read;
	}
}
