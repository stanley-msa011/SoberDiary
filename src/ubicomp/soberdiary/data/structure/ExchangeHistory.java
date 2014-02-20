package ubicomp.soberdiary.data.structure;

public class ExchangeHistory {

	public TimeValue tv;
	public int exchangeNum;
	
	public ExchangeHistory(long ts,int exchangeNum){
		this.tv = TimeValue.generate(ts);
		this.exchangeNum = exchangeNum;
	}
}
