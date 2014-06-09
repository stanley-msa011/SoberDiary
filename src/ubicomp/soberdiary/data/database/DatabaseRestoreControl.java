package ubicomp.soberdiary.data.database
;

import ubicomp.soberdiary.data.structure.AdditionalQuestionnaire;
import ubicomp.soberdiary.data.structure.Detection;
import ubicomp.soberdiary.data.structure.EmotionDIY;
import ubicomp.soberdiary.data.structure.EmotionManagement;
import ubicomp.soberdiary.data.structure.FacebookInfo;
import ubicomp.soberdiary.data.structure.Questionnaire;
import ubicomp.soberdiary.data.structure.StorytellingRead;
import ubicomp.soberdiary.data.structure.StorytellingTest;
import ubicomp.soberdiary.data.structure.UserVoiceRecord;
import ubicomp.soberdiary.main.App;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**Handling Database restore functions*/
public class DatabaseRestoreControl {
	
    private SQLiteOpenHelper dbHelper = null;
    private SQLiteDatabase db = null;
    
    public DatabaseRestoreControl(){
    	dbHelper = new DBHelper(App.getContext());
    }
    
    public void restoreDetection(Detection data){
    	db = dbHelper.getWritableDatabase();
    	if (data.isPrime()){
    		String sql = "UPDATE Detection SET isPrime = 0" +
    				" WHERE year ="+data.getTv().getYear()+
    				" AND month="+data.getTv().getMonth()+
    				" AND day ="+data.getTv().getDay()+
    				" AND timeslot="+data.getTv().getTimeslot();
    		db.execSQL(sql);
    	}
    	ContentValues content = new ContentValues();
    	content.put("brac", data.getBrac());
    	content.put("year",data.getTv().getYear());
    	content.put("month",data.getTv().getMonth());
    	content.put("day", data.getTv().getDay());
    	content.put("ts", data.getTv().getTimestamp());
    	content.put("week", data.getTv().getWeek());
    	content.put("timeslot", data.getTv().getTimeslot());
    	content.put("emotion", data.getEmotion());
    	content.put("craving", data.getCraving());
    	content.put("isPrime", data.isPrime()?1:0);
    	content.put("weeklyScore", data.getWeeklyScore());
    	content.put("score", data.getScore());
    	content.put("upload", 1);
    	db.insert("Detection", null, content);
    	db.close();
    }
    
    public void restoreEmotionDIY(EmotionDIY data){
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year",data.getTv().getYear());
		content.put("month",data.getTv().getMonth());
		content.put("day", data.getTv().getDay());
		content.put("ts", data.getTv().getTimestamp());
		content.put("week", data.getTv().getWeek());
		content.put("timeslot", data.getTv().getTimeslot());
		content.put("selection", data.getSelection());
		content.put("recreation", data.getRecreation());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("EmotionDIY", null, content);
		db.close();
    }
    
    public void restoreQuestionnaire(Questionnaire data){
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year",data.getTv().getYear());
		content.put("month",data.getTv().getMonth());
		content.put("day", data.getTv().getDay());
		content.put("ts", data.getTv().getTimestamp());
		content.put("week", data.getTv().getWeek());
		content.put("timeslot", data.getTv().getTimeslot());
		content.put("type", data.getType());
		content.put("sequence", data.getSeq());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("Questionnaire", null, content);
		db.close();
    }
    
    public void restoreEmotionManagement(EmotionManagement data){
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year",data.getTv().getYear());
		content.put("month",data.getTv().getMonth());
		content.put("day", data.getTv().getDay());
		content.put("ts", data.getTv().getTimestamp());
		content.put("week", data.getTv().getWeek());
		content.put("timeslot", data.getTv().getTimeslot());
		content.put("recordYear", data.getRecordTv().getYear());
		content.put("recordMonth", data.getRecordTv().getMonth());
		content.put("recordDay", data.getRecordTv().getDay());
		content.put("emotion", data.getEmotion());
		content.put("type", data.getType());
		content.put("reason", data.getReason());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("EmotionManagement", null, content);
		db.close();
    }
    
    public void restoreUserVoiceRecord(UserVoiceRecord data){
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year",data.getTv().getYear());
		content.put("month",data.getTv().getMonth());
		content.put("day", data.getTv().getDay());
		content.put("ts", data.getTv().getTimestamp());
		content.put("week", data.getTv().getWeek());
		content.put("timeSlot", data.getTv().getTimeslot());
		content.put("recordYear",data.getRecordTv().getYear());
		content.put("recordMonth",data.getRecordTv().getMonth());
		content.put("recordDay", data.getRecordTv().getDay());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("UserVoiceRecord", null, content);
		db.close();
    }
    
    public void restoreAdditionalQuestionnaire(AdditionalQuestionnaire data){
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year",data.getTv().getYear());
		content.put("month",data.getTv().getMonth());
		content.put("day", data.getTv().getDay());
		content.put("ts", data.getTv().getTimestamp());
		content.put("week", data.getTv().getWeek());
		content.put("timeSlot", data.getTv().getTimeslot());
		content.put("addedScore",1);
		content.put("emotion",data.getEmotion());
		content.put("craving", data.getCraving());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("AdditionalQuestionnaire", null, content);
		db.close();
    }
    
    public void restoreStorytellingRead(StorytellingRead data){
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year",data.getTv().getYear());
		content.put("month",data.getTv().getMonth());
		content.put("day", data.getTv().getDay());
		content.put("ts", data.getTv().getTimestamp());
		content.put("week", data.getTv().getWeek());
		content.put("timeSlot", data.getTv().getTimeslot());
		content.put("addedScore",1);
		content.put("page",data.getPage());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("StorytellingRead", null, content);
		db.close();
    }
    
    public void restoreStorytellingTest(StorytellingTest data){
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year",data.getTv().getYear());
		content.put("month",data.getTv().getMonth());
		content.put("day", data.getTv().getDay());
		content.put("ts", data.getTv().getTimestamp());
		content.put("week", data.getTv().getWeek());
		content.put("timeslot", data.getTv().getTimeslot());
		content.put("questionPage", data.getQuestionPage());
		content.put("isCorrect", data.isCorrect()?1:0);
		content.put("selection", data.getSelection());
		content.put("agreement", data.getAgreement());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("StorytellingTest", null, content);
		db.close();
    }
    
    public void restoreFacebookInfo(FacebookInfo data){
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year",data.getTv().getYear());
		content.put("month",data.getTv().getMonth());
		content.put("day", data.getTv().getDay());
		content.put("ts", data.getTv().getTimestamp());
		content.put("week", data.getTv().getWeek());
		content.put("timeslot", data.getTv().getTimeslot());
		content.put("pageWeek", data.getPageWeek());
		content.put("pageLevel", data.getPageLevel());
		content.put("text", data.getText());
		content.put("addedScore", 1);
		content.put("uploadSuccess", 0);
		content.put("privacy", data.getPrivacy());
		content.put("score", data.getScore());
		content.put("upload", 1);
		db.insert("FacebookInfo", null, content);
		db.close();
    }
    
    //Clean All
    public void deleteAll(){
    	db = dbHelper.getWritableDatabase();
    	String sql=null;
    	sql = "DELETE FROM Detection";
    	db.execSQL(sql);
    	sql = "DELETE FROM Ranking";
    	db.execSQL(sql);
    	sql = "DELETE FROM RankingShort";
    	db.execSQL(sql);
    	sql = "DELETE FROM UserVoiceRecord";
    	db.execSQL(sql);
    	sql = "DELETE FROM EmotionDIY";
    	db.execSQL(sql);
    	sql = "DELETE FROM EmotionManagement";
    	db.execSQL(sql);
    	sql = "DELETE FROM Questionnaire";
    	db.execSQL(sql);
    	sql = "DELETE FROM StorytellingTest";
    	db.execSQL(sql);
    	sql = "DELETE FROM StorytellingRead";
    	db.execSQL(sql);
    	sql = "DELETE FROM GCMRead";
    	db.execSQL(sql);
    	sql = "DELETE FROM FacebookInfo";
    	db.execSQL(sql);
    	sql = "DELETE FROM AdditionalQuestionnaire";
    	db.execSQL(sql);
    	sql = "DELETE FROM UserVoiceFeedback";
    	db.execSQL(sql);
    	sql = "DELETE FROM ExchangeHistory";
    	db.execSQL(sql);
    	db.close();
    }
}
