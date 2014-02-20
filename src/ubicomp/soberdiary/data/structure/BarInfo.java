package ubicomp.soberdiary.data.structure;

public class BarInfo{
	public float emotion = 0.F,craving=0.F,brac=0.F;
	public int week;
	public boolean hasData;
	public boolean drink;
	public TimeValue tv;
	
	public BarInfo (float emotion, float craving, float brac, int week,boolean hasData,TimeValue tv, boolean drink){
		this.emotion = emotion>0.f?emotion:0;
		this.craving = craving >0.f?craving:0;
		this.brac = drink?brac:0;
		this.week = week;
		this.hasData = hasData;
		this.tv = tv;
		this.drink = drink;
	}
}