package ubicomp.soberdiary.test.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.BreathDetail;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.system.config.PreferenceControl;
import ubicomp.soberdiary.test.camera.CameraRunHandler;
import ubicomp.soberdiary.test.data.BracPressureHandler;
import ubicomp.soberdiary.test.data.BracValueFileHandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class Bluetooth {

	protected static final String TAG = "BT";

	protected BluetoothAdapter btAdapter;

	protected final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected final static String DEVICE_NAME_FORMAL_OLD = "sober123_";
	protected final static String DEVICE_NAME_FORMAL_NEW = "sober456_";
	protected BluetoothDevice sensor;
	protected BluetoothSocket socket;

	protected InputStream in;
	protected Context context;

	protected float pressureMin;
	protected float pressureCurrent;
	protected boolean isPeak = false;
	protected static float PRESSURE_DIFF_MIN_RANGE;
	protected static float PRESSURE_DIFF_MIN;
	protected final static float PRESSURE_DIFF_MIN_RANGE_OLD = 50f;
	protected final static float PRESSURE_DIFF_MIN_OLD = 950.f;
	protected final static float PRESSURE_DIFF_MIN_RANGE_NEW = 50f;
	protected final static float PRESSURE_DIFF_MIN_NEW = 80.f;
	protected final static float MAX_PRESSURE = Float.MAX_VALUE;
	protected final static long IMAGE_MILLIS_0 = 500;
	protected final static long IMAGE_MILLIS_1 = 2500;
	protected final static long IMAGE_MILLIS_2 = 4900;
	protected final static long MAX_DURATION_MILLIS = 5000;

	protected final static long MILLIS_1 = 500;
	protected final static long MILLIS_2 = 1650;
	protected final static long MILLIS_3 = 2800;
	protected final static long MILLIS_4 = 3850;
	protected final static long MILLIS_5 = 4980;

	protected final static long BIP_MILLIS = 332;

	protected final static long START_MILLIS = 2000;
	protected final static long MAX_TEST_TIME = 25000;

	protected long startTime;
	protected long endTime;
	protected long firstStartTime;
	protected long totalDuration = 0;
	protected long tempDuration = 0;

	protected boolean start = false;

	protected final static int READ_NULL = 0;
	protected final static int READ_ALCOHOL = 1;
	protected final static int READ_PRESSURE = 2;
	protected final static int READ_VOLTAGE = 3;
	protected final static int READ_SERIAL = 4;

	protected Object lock = new Object();
	protected BTUIHandler btUIHandler;

	protected int imageCount;

	protected CameraRunHandler cameraRunHandler;
	protected BracValueFileHandler bracFileHandler;

	protected float showValue = 0.f;

	protected long disconnectionMillis;
	protected static final int MAX_ZERO_DURATION = 7000;

	protected boolean startToRecord = false;

	private int blowStartTimes = 0;
	private int blowBreakTimes = 0;
	private BracPressureHandler bracPressureHandler = null;
	private ArrayList<Float> pressureList;
	private float tempPressure;
	private float pressureDiffMax;
	private int voltageInit = 9999;

	private static final float MAX_INITIAL_BRAC = 0.05f;

	private static SoundPool soundpool;
	private static int soundId, soundIdBlow;
	private long soundTime = 0;

	protected boolean btEnabledBeforeStart = true;
	protected BluetoothDebugger debugger;

	protected String EXCEPTION_NO_BATTERY = "NO BATTERY";
	protected String EXCEPTION_BLOW_TWICE = "BLOW TWICE";
	protected String EXCEPTION_ZERO_DURATION = "ZERO DURATION";
	protected String EXCEPTION_TIME_OUT = "TIME OUT";
	protected String EXCEPTION_PRESSURE_ERROR = "PRESSURE ERROR";
	protected String EXCEPTION_HIGH_INITIAL_CONDITION = "HIGH_INITIAL_CONDITION";

	protected static final long MIN_SLEEP_TIME = 40;
	protected static final long MAX_SLEEP_TIME = 640;
	protected long sleepTime = MIN_SLEEP_TIME;

	protected static final int READ_BUFFER_SIZE = 32;

	protected int updateCircleTimes = 0;

	private ArrayList<Integer> serialList = new ArrayList<Integer>();

	public Bluetooth(BluetoothDebugger debugger, BluetoothMessageUpdater updater, CameraRunHandler cameraRunHandler,
			BracValueFileHandler bracFileHandler, boolean recordPressure) {
		this.debugger = debugger;
		this.context = App.getContext();
		this.cameraRunHandler = cameraRunHandler;
		this.bracFileHandler = bracFileHandler;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null)
			Log.e(TAG, "THE DEVICE DOES NOT SUPPORT BLUETOOTH");
		pressureCurrent = 0.f;
		btUIHandler = new BTUIHandler(updater);
		start = false;
		if (recordPressure) {
			bracPressureHandler = new BracPressureHandler(bracFileHandler.getDirectory(),
					bracFileHandler.getTimestamp());
			pressureList = new ArrayList<Float>();
		}
		if (soundpool == null) {
			soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
			soundId = soundpool.load(context, R.raw.din_ding, 1);
			soundIdBlow = soundpool.load(context, R.raw.bo, 1);
		}
	}

	public void enableAdapter() {
		btEnabledBeforeStart = true;
		if (!btAdapter.isEnabled()) {
			btEnabledBeforeStart = false;
			btAdapter.enable();
			int state = btAdapter.getState();
			while (state != BluetoothAdapter.STATE_ON) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				state = btAdapter.getState();
			}
		}
	}

	public boolean pair() {
		sensor = null;
		Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
		Iterator<BluetoothDevice> iter = devices.iterator();
		while (iter.hasNext()) {
			BluetoothDevice device = iter.next();
			if (device.getName() != null) {
				if (device.getName().startsWith(DEVICE_NAME_FORMAL_OLD)
						|| device.getName().startsWith(DEVICE_NAME_FORMAL_NEW)) {
					sensor = device;
					PreferenceControl.setSensorID(device.getName());
					return true;
				}
			}
		}
		if (sensor == null) {
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			BroadcastReceiver receiver = new btReceiver();
			context.registerReceiver(receiver, filter);
			btAdapter.startDiscovery();
		}
		return false;
	}

	protected class btReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device == null)
					return;
				String name = device.getName();
				if (name == null)
					return;
				if (name.startsWith(DEVICE_NAME_FORMAL_OLD) || name.startsWith(DEVICE_NAME_FORMAL_NEW)) {
					if (btAdapter.isDiscovering())
						btAdapter.cancelDiscovery();
					sensor = device;
					connect();
					close();
				}
			}
		}
	}

	public boolean connect() {
		if (btAdapter != null && btAdapter.isDiscovering())
			btAdapter.cancelDiscovery();

		String sensorId = PreferenceControl.getSensorID();
		if (sensorId.startsWith(DEVICE_NAME_FORMAL_OLD)) {
			PRESSURE_DIFF_MIN_RANGE = PRESSURE_DIFF_MIN_RANGE_OLD;
			PRESSURE_DIFF_MIN = PRESSURE_DIFF_MIN_OLD;
		} else {
			PRESSURE_DIFF_MIN_RANGE = PRESSURE_DIFF_MIN_RANGE_NEW;
			PRESSURE_DIFF_MIN = PRESSURE_DIFF_MIN_NEW;
		}

		if (sensor == null) {
			close();
			return false;
		}
		try {
			if (Build.VERSION.SDK_INT < 11)
				socket = sensor.createRfcommSocketToServiceRecord(uuid);
			else
				socket = sensor.createRfcommSocketToServiceRecord(uuid);
			try {
				socket.close();
				if (Build.VERSION.SDK_INT < 11)
					socket = sensor.createRfcommSocketToServiceRecord(uuid);
				else
					socket = sensor.createRfcommSocketToServiceRecord(uuid);
			} catch (Exception e) {
				Log.d(TAG, "FAIL TO CLOSE BEFORE CONNECTION");
			}
			socket.connect();
		} catch (Exception e) {
			Log.e(TAG, "FAIL TO CONNECT TO THE SENSOR: " + e.toString());
			closeWithCamera();
			return false;
		}
		return true;
	}

	public void start() {
		start = true;
		soundpool.play(soundId, 1.f, 1.f, 0, 0, 1.f);
	}

	protected final static byte[] sendStartMessage = { 'y', 'y', 'y' };
	protected final static byte[] sendEndMessage = { 'z', 'z', 'z' };

	protected OutputStream out;

	protected boolean connected = false;

	public boolean sendStart() {
		try {
			int counter = 0;
			while (true) {
				debugger.showDebug("start_to_send 'y'");
				out = socket.getOutputStream();
				in = socket.getInputStream();
				for (int i = 0; i < 5; ++i)
					out.write(sendStartMessage);
				Thread t1 = new Thread(new SRunnable());
				Thread t2 = new Thread(new SRunnable2());
				t1.start();
				t2.start();

				try {
					t2.join();
					if (!connected) {
						debugger.showDebug("no ack");
						t1.join(1);
						++counter;
					} else {
						debugger.showDebug("ack");
						t1.join();
						break;
					}
					if (counter == 3)
						return false;
				} catch (InterruptedException e) {
				}
			}
			return true;
		} catch (IOException e) {
			Log.d(TAG, "SEND START FAIL " + e.toString());
			close();
			cameraRunHandler.sendEmptyMessage(1);
			return false;
		}
	}

	protected class SRunnable implements Runnable {
		@Override
		public void run() {
			try {
				in = socket.getInputStream();
				byte[] temp = new byte[256];
				// block for waiting for the response
				int bytes = in.read(temp);
				if (bytes > 0)
					connected = true;
			} catch (IOException e) {
			}
		}
	}

	protected class SRunnable2 implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public void sendEnd() {
		try {
			if (out == null)
				return;
			for (int i = 0; i < 5; ++i) {
				if (socket != null) {
					if (Build.VERSION.SDK_INT >= 14 && socket.isConnected())
						out.write(sendEndMessage);
					else
						out.write(sendEndMessage);
				}
			}
			return;
		} catch (Exception e) {
			Log.e(TAG, "SEND END FAIL " + e.toString());
			return;
		}
	}

	public void read() {

		boolean end = false;
		byte[] temp = new byte[READ_BUFFER_SIZE];
		int bytes = 0;
		String msg = "";
		isPeak = false;
		pressureMin = MAX_PRESSURE;
		pressureCurrent = 0;
		int read_type = READ_NULL;
		totalDuration = 0;
		tempDuration = 0;
		firstStartTime = -1;
		imageCount = 0;
		showValue = 0.f;
		startToRecord = false;
		blowStartTimes = 0;
		blowBreakTimes = 0;
		soundTime = 0;
		pressureDiffMax = 0;
		updateCircleTimes = 0;
		if (pressureList != null)
			pressureList.clear();
		if (serialList != null)
			serialList.clear();
		try {
			in = socket.getInputStream();
			if (in.available() > 0)
				bytes = in.read(temp);

			while (bytes >= 0) {
				long time = System.currentTimeMillis();
				long time_gap = time - firstStartTime;
				if (firstStartTime == -1)
					firstStartTime = time;
				else if (time_gap > MAX_TEST_TIME)
					throw new Exception(EXCEPTION_TIME_OUT);

				for (int i = 0; i < bytes; ++i) {
					if ((char) temp[i] == 'a') {
						end = sendMsgToApp(msg);
						msg = "a";
						read_type = READ_ALCOHOL;
					} else if ((char) temp[i] == 'm') {
						end = sendMsgToApp(msg);
						msg = "m";
						read_type = READ_PRESSURE;
					} else if ((char) temp[i] == 'v') {
						end = sendMsgToApp(msg);
						msg = "v";
						read_type = READ_VOLTAGE;
					} else if ((char) temp[i] == 'b') {
						throw new Exception(EXCEPTION_NO_BATTERY);
					} else if ((char) temp[i] == 'p') {
						throw new Exception(EXCEPTION_PRESSURE_ERROR);
					} else if ((char) temp[i] == 's') {
						end = sendMsgToApp(msg);
						msg = "s";
						read_type = READ_SERIAL;
					} else if (read_type != READ_NULL)
						msg += (char) temp[i];
				}
				if (end)
					break;
				if (in.available() > 0) {
					bytes = in.read(temp);
					sleepTime /= 2;
					sleepTime = Math.max(MIN_SLEEP_TIME, sleepTime);
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(sleepTime);
							} catch (InterruptedException e) {
							}
						}
					});
					t.start();
					t.join();
					Log.d(TAG, "READ_DATA=" + sleepTime);
				} else {
					bytes = 0;
					if (disconnectionMillis > MAX_ZERO_DURATION)
						throw new Exception(EXCEPTION_ZERO_DURATION);
					sleepTime *= 2;
					sleepTime = Math.min(MAX_SLEEP_TIME, sleepTime);

					int try_time = 0;
					while (in.available() <= 0 && try_time < sleepTime) {
						Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(5);
								} catch (InterruptedException e) {
								}
							}
						});
						t.start();
						t.join();
						try_time += 5;
					}
					disconnectionMillis += try_time;

					Log.d(TAG, "ZERO_DURATION=" + disconnectionMillis + " " + try_time + " " + sleepTime);
				}
			}
			close();
		} catch (Exception e) {
			close();
			handleException(e);
		}
	}

	protected void handleException(Exception e) {
		Log.e(TAG, "FAIL TO READ DATA FROM THE SENSOR: " + e.toString());
		if (e.getMessage() != null && e.getMessage().equals(EXCEPTION_TIME_OUT)) {
			cameraRunHandler.sendEmptyMessage(3);
		} else if (e.getMessage() != null && e.getMessage().equals(EXCEPTION_BLOW_TWICE)) {
			cameraRunHandler.sendEmptyMessage(4);
		} else if (e.getMessage() != null && e.getMessage().equals(EXCEPTION_ZERO_DURATION)) {
			cameraRunHandler.sendEmptyMessage(5);
		} else if (e.getMessage() != null && e.getMessage().equals(EXCEPTION_PRESSURE_ERROR)) {
			cameraRunHandler.sendEmptyMessage(6);
		} else if (e.getMessage() != null && e.getMessage().equals(EXCEPTION_HIGH_INITIAL_CONDITION)) {
			cameraRunHandler.sendEmptyMessage(7);
		} else
			cameraRunHandler.sendEmptyMessage(2);
	}

	protected boolean sendMsgToApp(String msg) throws Exception {
		synchronized (lock) {
			if (msg == "")
				;// Do nothing
			else if (msg.charAt(0) == 'a') {
				if (!start) {
					float alcohol = Float.valueOf(msg.substring(1));
					if (alcohol > MAX_INITIAL_BRAC) {
						throw new Exception(EXCEPTION_HIGH_INITIAL_CONDITION);
					}
				} else if (isPeak) {
					long timeStamp = System.currentTimeMillis() / 1000L;
					float alcohol = Float.valueOf(msg.substring(1));
					String output = timeStamp + "\t" + alcohol + "\n";
					if (startToRecord) {
						showValue = alcohol;
						pressureList.add(tempPressure);
						write_to_file(output);
					}
				}
			} else if (msg.charAt(0) == 'm') {

				pressureCurrent = Float.valueOf(msg.substring(1));

				tempPressure = pressureCurrent;

				long time = System.currentTimeMillis();
				if (!start && pressureCurrent < pressureMin) {
					pressureMin = pressureCurrent;
				}

				if (!start)
					return false;

				if (pressureDiffMax < (pressureCurrent - pressureMin))
					pressureDiffMax = (pressureCurrent - pressureMin);

				float diff_limit = PRESSURE_DIFF_MIN_RANGE * (5000.f - tempDuration) / 5000.f + PRESSURE_DIFF_MIN;
				if (pressureCurrent > pressureMin + diff_limit && !isPeak) {
					isPeak = true;
					startTime = time;
					++blowStartTimes;
					if (blowStartTimes == 1) {
						PreferenceControl.setTestFail();
						PreferenceControl.setUpdateDetection(false);
					}
					if (blowStartTimes > 2) {
						throw new Exception(EXCEPTION_BLOW_TWICE);
					}
					tempDuration = 0;
					Log.d(TAG, "Peak Start");
				} else if (pressureCurrent > pressureMin + diff_limit && isPeak) {
					endTime = time;
					totalDuration += (endTime - startTime);
					tempDuration += (endTime - startTime);
					startTime = endTime;

					if (totalDuration > MILLIS_5 && updateCircleTimes < 5) {
						showBrACCircle(5);
						updateCircleTimes = 5;
					} else if (totalDuration > MILLIS_4 && updateCircleTimes < 4) {
						showBrACCircle(4);
						updateCircleTimes = 4;
					} else if (totalDuration > MILLIS_3 && updateCircleTimes < 3) {
						showBrACCircle(3);
						updateCircleTimes = 3;
					} else if (totalDuration > MILLIS_2 && updateCircleTimes < 2) {
						showBrACCircle(2);
						updateCircleTimes = 2;
					} else if (totalDuration > MILLIS_1 && updateCircleTimes < 1) {
						showBrACCircle(1);
						updateCircleTimes = 1;
					}

					if (totalDuration > soundTime) {
						soundpool.play(soundIdBlow, 1.f, 1.f, 0, 0, 1.f);
						soundTime += 100;
					}

					if (totalDuration >= START_MILLIS)
						startToRecord = true;

					if (imageCount == 0 && totalDuration > IMAGE_MILLIS_0) {
						cameraRunHandler.sendEmptyMessage(0);
						++imageCount;
					} else if (imageCount == 1 && totalDuration > IMAGE_MILLIS_1) {
						cameraRunHandler.sendEmptyMessage(0);
						++imageCount;
					} else if (imageCount == 2 && totalDuration > IMAGE_MILLIS_2) {
						cameraRunHandler.sendEmptyMessage(0);
						++imageCount;
					} else if (imageCount == 3 && totalDuration > MAX_DURATION_MILLIS) {
						showBrACCircle(6);
						return true;
					}

				} else if (isPeak) {
					isPeak = false;
					startTime = endTime = 0;
					++blowBreakTimes;
					Log.d(TAG, "Peak End");
				}
			} else if (msg.charAt(0) == 'v') {
				if (!start) {
					voltageInit = Integer.valueOf(msg.substring(1, msg.length() - 1));
				}
			} else if (msg.charAt(0) == 's') {
				int serial = Integer.valueOf(msg.substring(1, msg.length() - 1));
				serialList.add(serial);
			}
		}
		return false;
	}

	public void close() {
		normalClose();

		try {
			if (bracPressureHandler != null && pressureList != null) {
				float pressureAverage = 0.0f;
				int count = pressureList.size();
				if (count > 0) {
					Iterator<Float> iter = pressureList.iterator();
					while (iter.hasNext())
						pressureAverage += iter.next();
					pressureAverage /= count;
				}

				Message msg = new Message();
				Bundle data = new Bundle();

				int serialDiffMax = 0;
				float serialDiffAverage = 0;
				if (serialList.size() > 0) {
					int prev_serial = serialList.get(0);
					for (int i = 1; i < serialList.size(); ++i) {
						int serial = serialList.get(i);
						int diff = serial - prev_serial;
						diff = diff >= 0 ? diff : diff + 10000;
						serialDiffMax = diff > serialDiffMax ? diff : serialDiffMax;
						serialDiffAverage += diff;
						prev_serial = serial;
					}
					serialDiffAverage /= (serialList.size() - 1);
				}

				long bdTs = Long.valueOf(bracFileHandler.getTimestamp());

				BreathDetail bd = new BreathDetail(bdTs, blowStartTimes, blowBreakTimes, pressureDiffMax, pressureMin,
						pressureAverage, voltageInit, disconnectionMillis, serialDiffMax, serialDiffAverage);
				DatabaseControl db = new DatabaseControl();
				db.insertBreathDetail(bd);

				data.putString("pressure", blowStartTimes + "\t" + blowBreakTimes + "\t" + pressureAverage + "\t"
						+ pressureMin + "\t" + voltageInit + "\t" + "(" + disconnectionMillis + ")" + "\t("
						+ serialDiffMax + "," + serialDiffAverage + ")");
				msg.setData(data);
				msg.what = 0;
				bracPressureHandler.sendMessage(msg);
			}
		} catch (Exception e) {
		}
		;
	}

	protected void normalClose() {
		Log.d(TAG, "NORMAL CLOSE");

		sendEnd();

		Log.d(TAG, "SEND END DONE");
		try {
			if (in != null)
				in.close();
		} catch (Exception e) {
			Log.e(TAG, "FAIL TO CLOSE THE SENSOR INPUTSTREAM");
		}
		try {
			if (out != null)
				out.close();
		} catch (Exception e) {
			Log.e(TAG, "FAIL TO CLOSE THE SENSOR OUTPUTSTREAM");
		}
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "FAIL TO CLOSE THE SENSOR");
		}
		if (bracFileHandler != null)
			bracFileHandler.close();

		try {
			if (!btEnabledBeforeStart) {
				btAdapter.disable();
			}
		} catch (Exception e) {
		}
		;
	}

	public void closeWithCamera() {
		close();
		cameraRunHandler.sendEmptyMessage(1);
	}

	protected void write_to_file(String str) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("ALCOHOL", str);
		msg.setData(data);
		bracFileHandler.sendMessage(msg);
	}

	protected void showBrACCircle(int time) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putInt("TIME", time);
		msg.setData(data);
		msg.what = 2;
		btUIHandler.sendMessage(msg);
	}
	
	protected void showBrACValue(float value){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putFloat("value", value);
		msg.setData(data);
		msg.what = 3;
		btUIHandler.sendMessage(msg);
	}

}
