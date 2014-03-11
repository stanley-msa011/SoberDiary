package ubicomp.soberdiary.data.database;

import ubicomp.soberdiary.data.structure.Detection;
import ubicomp.soberdiary.data.structure.EmotionDIY;
import ubicomp.soberdiary.data.structure.EmotionManagement;
import ubicomp.soberdiary.data.structure.Questionnaire;
import ubicomp.soberdiary.data.structure.StorytellingRead;
import ubicomp.soberdiary.data.structure.UserVoiceRecord;
import ubicomp.soberdiary.main.App;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseRestoreControlVer1 {

	private SQLiteOpenHelper dbHelper = null;
	private SQLiteDatabase db = null;
	
	public DatabaseRestoreControlVer1() {
		dbHelper = new DBHelper(App.context);
	}

	public void restoreDetection(Detection data) {
		db = dbHelper.getWritableDatabase();

		String sql = "SELECT * FROM Detection WHERE year = " + data.tv.year + " AND month=" + data.tv.month
				+ " AND day = " + data.tv.day + " AND timeslot=" + data.tv.timeslot;

		Cursor cursor = db.rawQuery(sql, null);
		boolean isPrime = ! cursor.moveToFirst();
		cursor.close();
		
		ContentValues content = new ContentValues();
		content.put("brac", data.brac);
		content.put("year", data.tv.year);
		content.put("month", data.tv.month);
		content.put("day", data.tv.day);
		content.put("ts", data.tv.timestamp);
		content.put("week", data.tv.week);
		content.put("timeslot", data.tv.timeslot);
		content.put("emotion", data.emotion);
		content.put("craving", data.craving);
		content.put("isPrime", isPrime ? 1 : 0);
		content.put("weeklyScore", data.weeklyScore);
		content.put("score", data.score);
		content.put("upload", 1);
		db.insert("Detection", null, content);
		db.close();
	}

	public void restoreEmotionDIY(EmotionDIY data) {
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year", data.tv.year);
		content.put("month", data.tv.month);
		content.put("day", data.tv.day);
		content.put("ts", data.tv.timestamp);
		content.put("week", data.tv.week);
		content.put("timeslot", data.tv.timeslot);
		content.put("selection", data.selection);
		content.put("recreation", data.recreation);
		content.put("score", data.score);
		content.put("upload", 1);
		db.insert("EmotionDIY", null, content);
		db.close();
	}

	public void restoreQuestionnaire(Questionnaire data) {
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year", data.tv.year);
		content.put("month", data.tv.month);
		content.put("day", data.tv.day);
		content.put("ts", data.tv.timestamp);
		content.put("week", data.tv.week);
		content.put("timeslot", data.tv.timeslot);
		content.put("type", data.type);
		content.put("sequence", data.seq);
		content.put("score", data.score);
		content.put("upload", 1);
		db.insert("Questionnaire", null, content);
		db.close();
	}

	public void restoreEmotionManagement(EmotionManagement data) {
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year", data.tv.year);
		content.put("month", data.tv.month);
		content.put("day", data.tv.day);
		content.put("ts", data.tv.timestamp);
		content.put("week", data.tv.week);
		content.put("timeslot", data.tv.timeslot);
		content.put("recordYear", data.recordTv.year);
		content.put("recordMonth", data.recordTv.month);
		content.put("recordDay", data.recordTv.day);
		content.put("emotion", data.emotion);
		content.put("type", data.type);
		content.put("reason", data.reason);
		content.put("score", data.score);
		content.put("upload", 1);
		db.insert("EmotionManagement", null, content);
		db.close();
	}

	public void restoreUserVoiceRecord(UserVoiceRecord data) {
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year", data.tv.year);
		content.put("month", data.tv.month);
		content.put("day", data.tv.day);
		content.put("ts", data.tv.timestamp);
		content.put("week", data.tv.week);
		content.put("timeSlot", data.tv.timeslot);
		content.put("recordYear", data.recordTv.year);
		content.put("recordMonth", data.recordTv.month);
		content.put("recordDay", data.recordTv.day);
		content.put("score", data.score);
		content.put("upload", 1);
		db.insert("UserVoiceRecord", null, content);
		db.close();
	}

	public void restoreStorytellingRead(StorytellingRead data) {
		db = dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues();
		content.put("year", data.tv.year);
		content.put("month", data.tv.month);
		content.put("day", data.tv.day);
		content.put("ts", data.tv.timestamp);
		content.put("week", data.tv.week);
		content.put("timeSlot", data.tv.timeslot);
		content.put("addedScore", 1);
		content.put("page", data.page);
		content.put("score", data.score);
		content.put("upload", 1);
		db.insert("StorytellingRead", null, content);
		db.close();
	}

	// Clean All
	public void deleteAll() {
		db = dbHelper.getWritableDatabase();
		String sql = null;
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
