package ubicomp.soberdiary.system.config;

import java.util.Calendar;

import android.content.SharedPreferences;
import ubicomp.soberdiary.data.database.DatabaseControl;
import ubicomp.soberdiary.data.structure.ExchangeHistory;
import ubicomp.soberdiary.data.structure.TimeValue;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.R;

public class PreferenceControl {

	private static final SharedPreferences sp = App.sp;
	
	public static void defaultSetting(){
		setUID("sober_default_test");
		setIsDeveloper(false);
		Calendar cal = Calendar.getInstance();
		setStartDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
	}
	
	
	public static String getUID(){
		return sp.getString("uid", "sober_default_test");
	}
	public static void setUID(String uid){
		SharedPreferences.Editor edit= sp.edit();
		edit.putString("uid", uid);
		edit.commit();
	}
	public static boolean defaultCheck(){
		return getUID().equals("sober_default_test");
	}
	public static boolean checkFirstUID(){
		return sp.getString("uid", "").equals("");
	}
	
	
	public static String getSensorID(){
		return sp.getString("sensor_id", "unknown");
	}
	public static void setSensorID(String sensorID){
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("sensor_id", sensorID);
		edit.commit();
	}
	
	
	public static String[] getConnectFamilyName(){
		String[] names = new String[3];
		names[0] = sp.getString("family_name0", "");
		names[1] = sp.getString("family_name1", "");
		names[2] = sp.getString("family_name2", "");
		return  names;
	}
	public static String[] getConnectFamilyPhone(){
		String[] calls = new String[3];
		calls[0] = sp.getString("family_phone0", "");
		calls[1] = sp.getString("family_phone1", "");
		calls[2] = sp.getString("family_phone2", "");
		return  calls;
	}
	public static void setFamilyCallData(String[] name, String[] phone){
		SharedPreferences.Editor edit= sp.edit();
		edit.putString("family_name0", name[0]);
		edit.putString("family_name1", name[1]);
		edit.putString("family_name2", name[2]);
		edit.putString("family_phone0", phone[0]);
		edit.putString("family_phone1", phone[1]);
		edit.putString("family_phone2", phone[2]);
		edit.commit();
	}
	
	
	public static int[] getConnectSocialHelpIdx(){
		int[] calls = new int[3];
		calls[0] = sp.getInt("social_help0", 0);
		calls[1] = sp.getInt("social_help1", 1);
		calls[2] = sp.getInt("social_help2", 2);
		return  calls;
	}
	public static void setConnectSocialHelpIdx(int[] idx){
		SharedPreferences.Editor edit= sp.edit();
		edit.putInt("social_help0", idx[0]);
		edit.putInt("social_help1", idx[1]);
		edit.putInt("social_help2", idx[2]);
		edit.commit();
	}
	
	public static String[] getRecreations(){
		String[] recreation = new String[5];
		recreation[0] = sp.getString("recreation0", App.context.getString(R.string.default_recreation_1));
		recreation[1] = sp.getString("recreation1", App.context.getString(R.string.default_recreation_2));
		recreation[2] = sp.getString("recreation2", App.context.getString(R.string.default_recreation_3));
		recreation[3] = sp.getString("recreation3", "");
		recreation[4] = sp.getString("recreation4", "");
		return recreation;
	} 
	public static void setRecreations(String[] recreations){
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("recreation0", recreations[0]);
		edit.putString("recreation1", recreations[1]);
		edit.putString("recreation2", recreations[2]);
		edit.putString("recreation3", recreations[3]);
		edit.putString("recreation4", recreations[4]);
		edit.commit();
	}
	
	public static void setTestResult(int result){
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("testResult", result);
		edit.commit();
	}
	
	public static int getTestResult(){
		return sp.getInt("testResult", -1);
	}
	
	public static boolean uploadFacebookInfo(){
		return sp.getBoolean("uploadFacebookInfo", false);
	}
	public static void setUploadFacebookInfo(boolean upload){
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("uploadFacebookInfo",upload);
		edit.commit();
	}
	
	public static boolean uploadVoiceRecord(){
		return sp.getBoolean("uploadUserVoiceRecord", false);
	}
	
	public static void setUploadVoiceRecord(boolean upload){
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("uploadUserVoiceRecord",upload);
		edit.commit();
	}
	
	public static long getGPSStartTime(){
		return sp.getLong("latestStartGPSTimestamp", 0);
	}
	
	public static long getDetectionTimestamp(){
		return sp.getLong("latestDetectionTimestamp", 0);
	}
	
	public static void setGPSTime(long timestamp,Long testTimestamp){
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("latestStartGPSTimestamp", timestamp);
		edit.putLong("latestDetectionTimestamp", testTimestamp);
		edit.commit();
	}
	
	public static void resetGPSTime(){
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("latestStartGPSTimestamp", 0);
		edit.putLong("latestDetectionTimestamp", 0);
		edit.commit();
	}
	
	public static void setTestFail(){
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("latestDetectionDoneTimestamp", System.currentTimeMillis());
		edit.putBoolean("latestTestFail", true);
		edit.commit();
	}
	public static void setTestSuccess(){
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("latestDetectionDoneTimestamp", System.currentTimeMillis());
		edit.putBoolean("latestTestFail", false);
		edit.commit();
	}
	
	public static long getLastTestTime(){
		return sp.getLong("latestDetectionDoneTimestamp", 0);
	}
	
	public static boolean isTestFail(){
		return sp.getBoolean("latestTestFail", false);
	}
	
	public static void timeReset(){
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("latestDetectionDoneTimestamp", 0L);
		edit.putLong("latestStartGPSTimestamp", 0);
		edit.putLong("latestDetectionTimestamp", 0);
		edit.commit();
	}
	
	public static boolean isDebugMode(){
		return sp.getBoolean("debug", false);
	}
	public static void setDebugMode(boolean debug){
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("debug", debug);
		edit.commit();
	}
	
	public static boolean debugType(){
		return sp.getBoolean("debugType", false);
	}
	
	public static void setDebugType(boolean type){
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("debugType", type);
		edit.commit();
	}
	public static boolean isFirstTime(){
		return sp.getBoolean("firstTime", true);
	}
	
	public static void setAfterFirstTime(){
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("firstTime", false);
		editor.commit();
	}
	
	public static boolean isDeveloper(){
		return sp.getBoolean("developer", false);
	}
	public static void setIsDeveloper(boolean developer){
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("developer", developer);
		edit.commit();
	}
	
	public static int lastShowedCoupon(){
		return sp.getInt("showedCoupon", 0);
	}
	
	public static void setShowedCoupon(int num){
		SharedPreferences.Editor edit = sp.edit();
    	edit.putInt("showedCoupon", num);
    	edit.commit();
	}
	
	
	public static void setGoal(String goal,int money, int drink_cost){
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("targetGood", goal);
		edit.putInt("targetMoney", money);
		edit.putInt("perDrinkCost", drink_cost);
		edit.commit();
	}
	
	public static String getSavingGoal(){
		return sp.getString("targetGood", App.context.getString(R.string.default_goal_good));
	}
	public static int getSavingGoalMoney(){
		return sp.getInt("targetMoney", 50000);
	}
	
	public static int getSavingDrinkCost(){
		return sp.getInt("perDrinkCost", 200);
	}
	public static Calendar getStartDate(){
		int[] data = new int[3];
		Calendar cal = Calendar.getInstance();
		data[0]= sp.getInt("sYear", cal.get(Calendar.YEAR));
	    data[1] = sp.getInt("sMonth", cal.get(Calendar.MONTH));
	    data[2] = sp.getInt("sDay", cal.get(Calendar.DATE));
	    
	    cal.set(data[0], data[1], data[2], 0, 0, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal;
	}
	
	public static int[] getStartDateData(){
		int[] data = new int[3];
		Calendar cal = Calendar.getInstance();
		data[0]= sp.getInt("sYear", cal.get(Calendar.YEAR));
	    data[1] = sp.getInt("sMonth", cal.get(Calendar.MONTH));
	    data[2] = sp.getInt("sDay", cal.get(Calendar.DATE));
	    return data;
	}
	public static void setStartDate(int year, int month, int day){
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("sYear", year);
		edit.putInt("sMonth", month);
		edit.putInt("sDay", day);
		edit.commit();
	}
	
	
	public static boolean isLocked(){
		return sp.getBoolean("systemLock", false);
	}
	public static void setLocked(boolean lock){
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("systemLock", lock);
		edit.commit();
	}
	
	public static Calendar getLockDate(){
		int[] data = new int[3];
		Calendar cal = Calendar.getInstance();
		data[0] = sp.getInt("lockYear", cal.get(Calendar.YEAR));
	    data[1] = sp.getInt("lockMonth", cal.get(Calendar.MONTH));
	    data[2] = sp.getInt("lockDay", cal.get(Calendar.DATE));
	    
	    cal.set(data[0], data[1], data[2], 0, 0, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal;
	}
	
	public static int[] getLockDateData(){
		int[] data = new int[3];
		Calendar cal = Calendar.getInstance();
		data[0] = sp.getInt("lockYear", cal.get(Calendar.YEAR));
	    data[1] = sp.getInt("lockMonth", cal.get(Calendar.MONTH));
	    data[2] = sp.getInt("lockDay", cal.get(Calendar.DATE));
	    return data;
	}
	
	public static void setLockDate(int year, int month, int day){
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("lockYear", year);
		edit.putInt("lockMonth", month);
		edit.putInt("lockDay", day);
		edit.commit();
	}
	
	public static void setShowAdditonalQuestionnaire(){
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("additionalQuestionTime", System.currentTimeMillis());
		edit.commit();
	}
	
	public static boolean showAdditionalQuestionnaire(){
		long prevTime = sp.getLong("additionalQuestionTime", 0);
		long curTime = System.currentTimeMillis();
		TimeValue prevTV = TimeValue.generate(prevTime);
		TimeValue curTV = TimeValue.generate(curTime);
		if (curTV.hour < 20)
			return false;
		return (!prevTV.isSameDay(curTV));
	}
	
	public static int getStorytellingReadTimes(){
		return sp.getInt("readTimes", 0);
	}
	
	public static void addStorytellingReadTimes(){
		int time = sp.getInt("readTimes", 0);
		if (time < Config.STORYTELLING_READ_LIMIT) {
			SharedPreferences.Editor edit = sp.edit();
			edit.putInt("readTimes", (time + 1));
			edit.commit();
		}
	}
	
	public static void resetStorytellingReadTimes(){
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("readTimes", 0);
		edit.commit();
	}
	
	public static int getTotalCounter(){
		DatabaseControl db = new DatabaseControl();
		int total_counter = 
    			db.getLatestDetection().score
    			+db.getLatestEmotionDIY().score
    			+db.getLatestEmotionManagement().score
    			+db.getLatestQuestionnaire().score+
    			+db.getLatestStorytellingTest().score+
    			+db.getLatestUserVoiceRecord().score+
    			+db.getLatestStorytellingRead().score+
    			+db.getLatestFacebookInfo().score+
				+db.getLatestAdditionalQuestionnaire().score;
		int usedCounter = sp.getInt("usedCounter", 0);
		return total_counter - usedCounter;
	}
	
	public static void exchangeCoupon(){
		int currentCounter = getTotalCounter();
		int usedCounter = sp.getInt("usedCounter", 0);
		int coupon = currentCounter/Config.COUPON_COUNTER;
		int exchangeCounter = coupon * Config.COUPON_COUNTER;
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("usedCounter", usedCounter+exchangeCounter);
		edit.commit();
		DatabaseControl db = new DatabaseControl();
		db.insertExchangeHistory(new ExchangeHistory(System.currentTimeMillis(),exchangeCounter));
	}
	
	public static int getUsedCounter(){
		return sp.getInt("usedCounter", 0);
	}
	
	public static void setUsedCounter(int counter){
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("usedCounter", counter);
		edit.commit();
	}
	
	public static void setLatestTestCompleteTime(long timestamp){
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("testCompleteTime", timestamp);
		edit.commit();
	}
	
	public static long getLatestTestCompleteTime(){
		return sp.getLong("testCompleteTime", 0);
	}
	
	public static boolean questionnaireShowUpdateDetection(){
		long curTs = System.currentTimeMillis();
		long prevTs = sp.getLong("testCompleteTime", 0);
		if (curTs - prevTs < 3*60*1000){
			long addTs = prevTs + 5*60*1000;
			TimeValue prevTv = TimeValue.generate(prevTs);
			TimeValue addTv = TimeValue.generate(addTs);
			return prevTv.timeslot == addTv.timeslot;
		}
		return false;
	}
	
	public static void setUpdateDetection(boolean update){
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("updateDetection", update);
		edit.commit();
	}
	
	public static boolean getUpdateDetection(){
		return sp.getBoolean("updateDetection", false);
	}
	
	public static void setUpdateDetectionTimestamp(long timestamp){
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("updateDetectionTimestamp", timestamp);
		edit.commit();
	}
	
	public static long getUpdateDetectionTimestamp(){
		return sp.getLong("updateDetectionTimestamp", 0);
	}
	
	public static int getNotificationTimeIdx(){
		return sp.getInt("notificationTimeGap", 2);
	}
	
	public static int getNotificationTime(){
		int[] time={30,60,120};
		int idx = sp.getInt("notificationTimeGap", 2);
		return time[idx];
	}
	
	public static void setNotificationTimeIdx(int idx){
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("notificationTimeGap", idx);
		edit.commit();
	}
	
	public static void setDebugDetectionTimestamp(long timestamp){
		SharedPreferences.Editor edit = sp.edit();
		edit.putLong("debugDetection", timestamp);
		edit.commit();
	}
	public static long getDebugDetectionTimestamp(){
		return sp.getLong("debugDetection", 0);
	}
	
	public static void setPrevShowWeekState(int week, int state){
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("prevShowWeek", week);
		edit.putInt("prevShowWeekState", state);
		edit.commit();
	}
	
	public static int getPrevShowWeek(){
		return sp.getInt("prevShowWeek", 0);
	}
	
	public static int getPrevShowWeekState(){
		return sp.getInt("prevShowWeekState", 0);
	}
	
	public static void setPageChange(boolean change){
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("pageChange", change);
		edit.commit();
	}
	
	public static boolean getPageChange(){
		return sp.getBoolean("pageChange", false);
	}
	
}
