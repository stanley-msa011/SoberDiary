package ubicomp.soberdiary.data.structure;

public class Questionnaire {

	public TimeValue tv;
	public int type;
	public String seq;
	public int score;
	
	public Questionnaire(long ts,int type,String seq,int score){
		this.tv = TimeValue.generate(ts);
		this.type = type;
		this.seq = seq;
		this.score = score;
	}
}
