package ubicomp.soberdiary.test.bluetooth;

import ubicomp.soberdiary.test.Tester;

public interface BluetoothCaller extends Tester {
	public void stopDueToInit();

	public void failBT();

	public void setPairMessage();
}
