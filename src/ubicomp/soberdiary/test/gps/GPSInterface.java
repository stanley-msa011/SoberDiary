package ubicomp.soberdiary.test.gps;

/** Interface defines GPS related functions */
public interface GPSInterface {
	/** Start to run GPS */
	public void runGPS();

	/** Call GPS Setting Activity in Android */
	public void callGPSActivity();

	/** Start GPS */
	public void startGPS(boolean enable);
}
