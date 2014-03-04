package ubicomp.soberdiary.data.database;

import java.util.Calendar;

import ubicomp.soberdiary.data.structure.AdditionalQuestionnaire;
import ubicomp.soberdiary.data.structure.Detection;
import ubicomp.soberdiary.data.structure.EmotionDIY;
import ubicomp.soberdiary.data.structure.EmotionManagement;
import ubicomp.soberdiary.data.structure.ExchangeHistory;
import ubicomp.soberdiary.data.structure.FacebookInfo;
import ubicomp.soberdiary.data.structure.GCMRead;
import ubicomp.soberdiary.data.structure.Questionnaire;
import ubicomp.soberdiary.data.structure.Rank;
import ubicomp.soberdiary.data.structure.StorytellingRead;
import ubicomp.soberdiary.data.structure.StorytellingTest;
import ubicomp.soberdiary.data.structure.TimeValue;
import ubicomp.soberdiary.data.structure.UserVoiceFeedback;
import ubicomp.soberdiary.data.structure.UserVoiceRecord;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.main.GPSService;
import ubicomp.soberdiary.system.check.StartDateCheck;
import ubicomp.soberdiary.system.check.WeekNumCheck;
import ubicomp.soberdiary.system.config.PreferenceControl;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseControl {

	private SQLiteOpenHelper dbHelper = null;
	private SQLiteDatabase db = null;

	public DatabaseControl() {
		dbHelper = new DBHelper(App.context);
	}

	static final Object sqlLock = new Object();

	// Detection

	public Detection[] getAllPrimeDetection() {
		db = dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM DETECTION WHERE isPrime = 1 ORDER BY ts ASC";
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		Detection[] detections = new Detection[count];
		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			float brac = cursor.getFloat(1);
			long ts = cursor.getLong(5);
			int emotion = cursor.getInt(8);
			int craving = cursor.getInt(9);
			boolean isPrime = cursor.getInt(10) == 1;
			int weeklyScore = cursor.getInt(11);
			int score = cursor.getInt(12);
			detections[i] = new Detection(brac, ts, emotion, craving, isPrime, weeklyScore, score);
		}

		cursor.close();
		db.close();
		return detections;
	}

	public Detection getLatestDetection() {
		db = dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM DETECTION ORDER BY ts DESC LIMIT 1";
		Cursor cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return new Detection(0f, 0, -1, -1, false, 0, 0);
		}

		float brac = cursor.getFloat(1);
		long ts = cursor.getLong(5);
		int emotion = cursor.getInt(8);
		int craving = cursor.getInt(9);
		boolean isPrime = cursor.getInt(10) == 1;
		int weeklyScore = cursor.getInt(11);
		int score = cursor.getInt(12);
		Detection detection = new Detection(brac, ts, emotion, craving, isPrime, weeklyScore, score);

		cursor.close();
		db.close();
		return detection;
	}

	public int insertDetection(Detection data, boolean update, Context context) {
		synchronized (sqlLock) {

			Detection prev_data = getLatestDetection();
			int weeklyScore = prev_data.weeklyScore;
			if (prev_data.tv.week < data.tv.week)
				weeklyScore = 0;
			int score = prev_data.score;
			db = dbHelper.getWritableDatabase();
			if (!update) {
				boolean isPrime = !(data.isSameTimeBlock(prev_data));
				int isPrimeValue = isPrime ? 1 : 0;
				int addScore = 0;
				addScore += isPrimeValue;
				addScore += isPrime && data.isPass() ? 1 : 0;
				if (!StartDateCheck.afterStartDate())
					addScore = 0;
				
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
				content.put("isPrime", isPrimeValue);
				content.put("weeklyScore", weeklyScore + addScore);
				content.put("score", score + addScore);
				db.insert("Detection", null, content);
				db.close();
				return addScore;
			} else {
				int addScore = data.isPass() ? 1 : 0;
				if (!StartDateCheck.afterStartDate())
					addScore = 0;
				String sql = "UPDATE Detection SET isPrime = 0 WHERE ts =" + prev_data.tv.timestamp;
				db.execSQL(sql);
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
				content.put("isPrime", 1);
				content.put("weeklyScore", weeklyScore + addScore);
				content.put("score", score + addScore);
				db.insert("Detection", null, content);
				db.close();
				return addScore;
			}
		}
	}

	public Float[] getTodayPrimeBrac() {
		Float[] brac = new Float[3];
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);

		db = dbHelper.getReadableDatabase();

		String sql = "SELECT brac,timeSlot FROM Detection WHERE year = " + year + " AND month = " + month
				+ " AND day = " + day + " AND isPrime = 1" + " ORDER BY timeSlot ASC";
		Cursor cursor = db.rawQuery(sql, null);

		int count = cursor.getCount();

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			float _brac = cursor.getFloat(0);
			int _timeSlot = cursor.getInt(1);
			brac[_timeSlot] = _brac;
		}
		cursor.close();
		db.close();
		return brac;
	}

	public Float[] getMultiDaysPrimeBrac(int n_days) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		final long DAY = AlarmManager.INTERVAL_DAY;
		long ts_days = (long) (n_days - 1) * DAY;
		long start_ts = cal.getTimeInMillis() - ts_days;

		String sql = "SELECT brac,ts,timeSlot FROM Detection WHERE ts >=" + start_ts + " AND isPrime = 1"
				+ " ORDER BY ts ASC";
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);

		Float[] brac = new Float[3 * n_days];
		long ts_from = start_ts;
		long ts_to = start_ts + DAY;
		int pointer = 0;
		int count = cursor.getCount();

		for (int i = 0; i < brac.length; ++i) {
			int timeSlot = i % 3;

			float _brac;
			long _ts;
			int _timeSlot;
			while (pointer < count) {
				cursor.moveToPosition(pointer);
				_brac = cursor.getFloat(0);
				_ts = cursor.getLong(1);
				_timeSlot = cursor.getInt(2);
				if (_ts < ts_from) {
					++pointer;
					continue;
				} else if (_ts >= ts_to) {
					break;
				}
				if (_timeSlot > timeSlot)
					break;
				else if (_timeSlot < timeSlot) {
					++pointer;
					continue;
				}
				brac[i] = _brac;
				break;
			}

			if (timeSlot == 2) {
				ts_from += DAY;
				ts_to += DAY;
			}
		}
		cursor.close();
		db.close();
		return brac;
	}

	public void setDetectionUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE Detection SET upload = 1 WHERE ts=" + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	public Integer[] getDetectionScoreByWeek() {
		db = dbHelper.getReadableDatabase();
		int curWeek = WeekNumCheck.getWeek(Calendar.getInstance().getTimeInMillis());
		Integer[] scores = new Integer[curWeek + 1];

		String sql = "SELECT weeklyScore, week FROM Detection WHERE week<=" + curWeek + " GROUP BY week";

		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		int pointer = 0;
		int week = 0;
		for (int i = 0; i < scores.length; ++i) {
			while (pointer < count) {
				cursor.moveToPosition(pointer);
				week = cursor.getInt(1);
				if (week < i) {
					++pointer;
					continue;
				} else if (week > i)
					break;
				int weeklyScore = cursor.getInt(0);
				scores[i] = weeklyScore;
				break;
			}
		}
		for (int i = 0; i < scores.length; ++i)
			if (scores[i] == null)
				scores[i] = 0;

		cursor.close();
		db.close();
		return scores;
	}

	public Detection[] getAllNotUploadedDetection() {
		db = dbHelper.getReadableDatabase();
		long cur_ts = System.currentTimeMillis();
		long gps_ts = PreferenceControl.getGPSStartTime() + GPSService.GPS_TOTAL_TIME;
		String sql;
		if (cur_ts <= gps_ts) {
			long gps_detection_ts = PreferenceControl.getDetectionTimestamp();
			sql = "SELECT * FROM Detection WHERE upload = 0 AND ts <> " + gps_detection_ts + " ORDER BY ts ASC";
		} else {
			sql = "SELECT * FROM Detection WHERE upload = 0  ORDER BY ts ASC";
		}
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		Detection[] detections = new Detection[count];
		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			float brac = cursor.getFloat(1);
			long ts = cursor.getLong(5);
			int emotion = cursor.getInt(8);
			int craving = cursor.getInt(9);
			boolean isPrime = cursor.getInt(10) == 1;
			int weeklyScore = cursor.getInt(11);
			int score = cursor.getInt(12);
			detections[i] = new Detection(brac, ts, emotion, craving, isPrime, weeklyScore, score);
		}
		cursor.close();
		db.close();
		return detections;
	}

	public boolean detectionIsDone() {
		db = dbHelper.getReadableDatabase();
		long ts = System.currentTimeMillis();
		TimeValue tv = TimeValue.generate(ts);
		String sql = "SELECT id FROM Detection WHERE" + " year =" + tv.year + " AND month = " + tv.month + " AND day= "
				+ tv.day + " AND timeSlot= " + tv.timeslot;
		Cursor cursor = db.rawQuery(sql, null);
		boolean result = cursor.getCount() > 0;
		cursor.close();
		db.close();
		return result;
	}

	public int getPrimeDetectionPassTimes() {
		db = dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM Detection WHERE isPrime = 1 AND brac < " + Detection.BRAC_THRESHOLD;
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}

	public boolean canTryAgain() {
		TimeValue curTV = TimeValue.generate(System.currentTimeMillis());
		int year = curTV.year;
		int month = curTV.month;
		int day = curTV.day;
		int timeslot = curTV.timeslot;
		db = dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM DETECTION WHERE year=" + year + " AND month=" + month + " AND day=" + day
				+ " AND timeSlot=" + timeslot;
		Cursor cursor = db.rawQuery(sql, null);
		return (cursor.getCount() == 1);
	}

	// Ranking
	public Rank getMyRank() {
		db = dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM Ranking WHERE user_id='" + PreferenceControl.getUID() + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return new Rank("", 0);
		}
		String uid = cursor.getString(0);
		int score = cursor.getInt(1);
		int test = cursor.getInt(2);
		int advice = cursor.getInt(3);
		int manage = cursor.getInt(4);
		int story = cursor.getInt(5);
		int[] additionals = new int[8];
		for (int j = 0; j < additionals.length; ++j)
			additionals[j] = cursor.getInt(6 + j);
		Rank rank = new Rank(uid, score, test, advice, manage, story, additionals);
		cursor.close();
		db.close();
		return rank;
	}

	public Rank[] getAllRanks() {
		Rank[] ranks = null;
		db = dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM Ranking ORDER BY total_score DESC, user_id ASC";
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}
		ranks = new Rank[count];
		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			String uid = cursor.getString(0);
			int score = cursor.getInt(1);
			int test = cursor.getInt(2);
			int advice = cursor.getInt(3);
			int manage = cursor.getInt(4);
			int story = cursor.getInt(5);
			int[] additionals = new int[8];
			for (int j = 0; j < additionals.length; ++j)
				additionals[j] = cursor.getInt(6 + j);
			ranks[i] = new Rank(uid, score, test, advice, manage, story, additionals);
		}
		cursor.close();
		db.close();
		return ranks;
	}

	public Rank[] getAllRankShort() {
		Rank[] ranks = null;
		db = dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM RankingShort ORDER BY total_score DESC, user_id ASC";
		Cursor cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}
		ranks = new Rank[count];
		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			String uid = cursor.getString(0);
			int score = cursor.getInt(1);
			ranks[i] = new Rank(uid, score);
		}
		cursor.close();
		db.close();
		return ranks;
	}

	public void clearRank() {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "DELETE  FROM Ranking";
			db.execSQL(sql);
			db.close();
		}
	}

	public void updateRank(Rank data) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "SELECT * FROM Ranking WHERE user_id = '" + data.uid + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.getCount() == 0) {
				ContentValues content = new ContentValues();
				content.put("user_id", data.uid);
				content.put("total_score", data.score);
				content.put("test_score", data.test);
				content.put("advice_score", data.advice);
				content.put("manage_score", data.manage);
				content.put("story_score", data.story);
				content.put("advice_questionnaire", data.advice_questionnaire);
				content.put("advice_emotion_diy", data.advice_emotion_diy);
				content.put("manage_voice", data.manage_voice);
				content.put("manage_emotion", data.manage_emotion);
				content.put("manage_additional", data.manage_additional);
				content.put("story_read", data.story_read);
				content.put("story_test", data.story_test);
				content.put("story_fb", data.story_fb);
				db.insert("Ranking", null, content);
			} else {
				sql = "UPDATE Ranking SET" + " total_score = " + data.score + "," + " test_score = " + data.test + ","
						+ " advice_score = " + data.advice + "," + " manage_score=" + data.manage + ","
						+ " story_score = " + data.story + "," + " advice_questionnaire=" + data.advice_questionnaire
						+ "," + " advice_emotion_diy=" + data.advice_emotion_diy + "," + " manage_voice="
						+ data.manage_voice + "," + " manage_emotion=" + data.manage_emotion + ","
						+ " manage_additional=" + data.manage_additional + "," + " story_read=" + data.story_read + ","
						+ " story_test=" + data.story_test + "," + " story_fb=" + data.story_fb + " WHERE user_id = "
						+ "'" + data.uid + "'";
				db.execSQL(sql);
			}
			cursor.close();
			db.close();
		}
	}

	public void clearRankShort() {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "DELETE  FROM RankingShort";
			db.execSQL(sql);
			db.close();
		}
	}

	public void updateRankShort(Rank data) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "SELECT * FROM RankingShort WHERE user_id = '" + data.uid + "'";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.getCount() == 0) {
				ContentValues content = new ContentValues();
				content.put("user_id", data.uid);
				content.put("total_score", data.score);
				db.insert("RankingShort", null, content);
			} else {
				sql = "UPDATE RankingShort SET" + " total_score = " + data.score + " WHERE user_id = " + "'" + data.uid
						+ "'";
				db.execSQL(sql);
			}
			cursor.close();
			db.close();
		}
	}

	// Questionnaire

	public Questionnaire getLatestQuestionnaire() {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM Questionnaire WHERE type >= 0 ORDER BY ts DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return new Questionnaire(0, 0, null, 0);
		}
		long ts = cursor.getLong(4);
		int type = cursor.getInt(7);
		String seq = cursor.getString(8);
		int score = cursor.getInt(9);
		return new Questionnaire(ts, type, seq, score);
	}

	public int insertQuestionnaire(Questionnaire data) {
		synchronized (sqlLock) {
			Questionnaire prev_data = getLatestQuestionnaire();
			int addScore = 0;
			if (!prev_data.tv.isSameTimeBlock(data.tv) && data.type >= 0)
				addScore = 1;
			if (!StartDateCheck.afterStartDate())
				addScore = 0;
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
			content.put("score", prev_data.score + addScore);
			db.insert("Questionnaire", null, content);
			db.close();
			return addScore;
		}
	}

	public Questionnaire[] getNotUploadedQuestionnaire() {
		Questionnaire[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM Questionnaire WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new Questionnaire[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(4);
			int type = cursor.getInt(7);
			String seq = cursor.getString(8);
			int score = cursor.getInt(9);
			data[i] = new Questionnaire(ts, type, seq, score);
		}

		cursor.close();
		db.close();

		return data;

	}

	public void setQuestionnaireUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE Questionnaire SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	// EmotionDIY

	public EmotionDIY getLatestEmotionDIY() {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM EmotionDIY ORDER BY ts DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return new EmotionDIY(0, 0, null, 0);
		}
		long ts = cursor.getLong(4);
		int selection = cursor.getInt(7);
		String recreation = cursor.getString(8);
		int score = cursor.getInt(9);
		return new EmotionDIY(ts, selection, recreation, score);
	}

	public int insertEmotionDIY(EmotionDIY data) {
		synchronized (sqlLock) {
			EmotionDIY prev_data = getLatestEmotionDIY();
			int addScore = 0;
			if (!prev_data.tv.isSameTimeBlock(data.tv))
				addScore = 1;
			if (!StartDateCheck.afterStartDate())
				addScore = 0;
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
			content.put("score", prev_data.score + addScore);
			db.insert("EmotionDIY", null, content);
			db.close();
			return addScore;
		}
	}

	public EmotionDIY[] getNotUploadedEmotionDIY() {
		EmotionDIY[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM EmotionDIY WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new EmotionDIY[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(4);
			int selection = cursor.getInt(7);
			String recreation = cursor.getString(8);
			int score = cursor.getInt(9);
			data[i] = new EmotionDIY(ts, selection, recreation, score);
		}

		cursor.close();
		db.close();

		return data;

	}

	public void setEmotionDIYUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE EmotionDIY SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	// EmotionManagement
	public EmotionManagement getLatestEmotionManagement() {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM EmotionManagement ORDER BY ts DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(0);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			return new EmotionManagement(0, year, month, day, 0, 0, null, 0);
		}
		long ts = cursor.getLong(4);
		int year = cursor.getInt(7);
		int month = cursor.getInt(8);
		int day = cursor.getInt(9);
		int emotion = cursor.getInt(10);
		int type = cursor.getInt(11);
		String reason = cursor.getString(12);
		int score = cursor.getInt(13);
		return new EmotionManagement(ts, year, month, day, emotion, type, reason, score);
	}

	public int insertEmotionManagement(EmotionManagement data) {
		synchronized (sqlLock) {
			EmotionManagement prev_data = getLatestEmotionManagement();
			int addScore = 0;
			if (!prev_data.tv.isSameTimeBlock(data.tv))
				addScore = 1;
			if (!StartDateCheck.afterStartDate())
				addScore = 0;
			
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
			content.put("score", prev_data.score + addScore);
			db.insert("EmotionManagement", null, content);
			db.close();
			return addScore;
		}
	}

	public EmotionManagement[] getNotUploadedEmotionManagement() {
		EmotionManagement[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM EmotionManagement WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new EmotionManagement[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(4);
			int year = cursor.getInt(7);
			int month = cursor.getInt(8);
			int day = cursor.getInt(9);
			int emotion = cursor.getInt(10);
			int type = cursor.getInt(11);
			String reason = cursor.getString(12);
			int score = cursor.getInt(13);
			data[i] = new EmotionManagement(ts, year, month, day, emotion, type, reason, score);
		}

		cursor.close();
		db.close();

		return data;

	}

	public EmotionManagement[] getDayEmotionManagement(int rYear, int rMonth, int rDay) {
		EmotionManagement[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM EmotionManagement WHERE recordYear = " + rYear + " AND recordMonth = " + rMonth
				+ " AND recordDay = " + rDay + " ORDER BY id DESC";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new EmotionManagement[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(4);
			int year = cursor.getInt(7);
			int month = cursor.getInt(8);
			int day = cursor.getInt(9);
			int emotion = cursor.getInt(10);
			int type = cursor.getInt(11);
			String reason = cursor.getString(12);
			int score = cursor.getInt(13);
			data[i] = new EmotionManagement(ts, year, month, day, emotion, type, reason, score);
		}

		cursor.close();
		db.close();

		return data;

	}

	public void setEmotionManagementUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE EmotionManagement SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	public String[] getEmotionManagementString(int type) {
		db = dbHelper.getReadableDatabase();
		String sql = "SELECT DISTINCT reason FROM EmotionManagement WHERE type = " + type + " ORDER BY ts DESC LIMIT 5";
		String[] out = null;

		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		out = new String[cursor.getCount()];

		for (int i = 0; i < out.length; ++i)
			if (cursor.moveToPosition(i))
				out[i] = cursor.getString(0);

		cursor.close();
		db.close();
		return out;
	}

	public boolean hasEmotionManagement(TimeValue tv) {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM EmotionManagement WHERE" + " recordYear =" + tv.year + " AND recordMonth=" + tv.month
				+ " AND recordDay =" + tv.day;
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count > 0;
	}

	// StorytellingTest
	public StorytellingTest getLatestStorytellingTest() {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM StorytellingTest WHERE isCorrect = 1 ORDER BY ts DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return new StorytellingTest(0, 0, false, "", 0, 0);
		}
		long ts = cursor.getLong(4);
		int page = cursor.getInt(7);
		boolean isCorrect = (cursor.getInt(8) == 1) ? true : false;
		String selection = cursor.getString(9);
		int agreement = cursor.getInt(10);
		int score = cursor.getInt(11);
		return new StorytellingTest(ts, page, isCorrect, selection, agreement, score);
	}

	public int insertStorytellingTest(StorytellingTest data) {
		synchronized (sqlLock) {
			StorytellingTest prev_data = getLatestStorytellingTest();
			int addScore = 0;
			if (!prev_data.tv.isSameTimeBlock(data.tv) && data.isCorrect)
				addScore = 1;
			if (!StartDateCheck.afterStartDate())
				addScore = 0;
			
			db = dbHelper.getWritableDatabase();
			ContentValues content = new ContentValues();
			content.put("year", data.tv.year);
			content.put("month", data.tv.month);
			content.put("day", data.tv.day);
			content.put("ts", data.tv.timestamp);
			content.put("week", data.tv.week);
			content.put("timeslot", data.tv.timeslot);
			content.put("questionPage", data.questionPage);
			content.put("isCorrect", data.isCorrect ? 1 : 0);
			content.put("selection", data.selection);
			content.put("agreement", data.agreement);
			content.put("score", prev_data.score + addScore);
			db.insert("StorytellingTest", null, content);
			db.close();
			return addScore;
		}
	}

	public StorytellingTest[] getNotUploadedStorytellingTest() {
		StorytellingTest[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM StorytellingTest  WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new StorytellingTest[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(4);
			int page = cursor.getInt(7);
			boolean isCorrect = (cursor.getInt(8) == 1) ? true : false;
			String selection = cursor.getString(9);
			int agreement = cursor.getInt(10);
			int score = cursor.getInt(11);
			data[i] = new StorytellingTest(ts, page, isCorrect, selection, agreement, score);
		}

		cursor.close();
		db.close();

		return data;

	}

	public void setStorytellingTestUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE StorytellingTest SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	// UserVoiceRecord
	public UserVoiceRecord getLatestUserVoiceRecord() {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM UserVoiceRecord ORDER BY ts DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(0);
			return new UserVoiceRecord(0, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH), 0);
		}
		long ts = cursor.getLong(4);
		int rYear = cursor.getInt(7);
		int rMonth = cursor.getInt(8);
		int rDay = cursor.getInt(9);
		int score = cursor.getInt(10);
		return new UserVoiceRecord(ts, rYear, rMonth, rDay, score);
	}

	public int insertUserVoiceRecord(UserVoiceRecord data) {
		synchronized (sqlLock) {
			UserVoiceRecord prev_data = getLatestUserVoiceRecord();
			int addScore = 0;
			if (!prev_data.tv.isSameTimeBlock(data.tv))
				addScore = 1;
			if (!StartDateCheck.afterStartDate())
				addScore = 0;
			
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
			content.put("score", prev_data.score + addScore);
			db.insert("UserVoiceRecord", null, content);
			db.close();
			return addScore;
		}
	}

	public UserVoiceRecord[] getNotUploadedUserVoiceRecord() {
		UserVoiceRecord[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM UserVoiceRecord WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new UserVoiceRecord[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(4);
			int rYear = cursor.getInt(7);
			int rMonth = cursor.getInt(8);
			int rDay = cursor.getInt(9);
			int score = cursor.getInt(10);
			data[i] = new UserVoiceRecord(ts, rYear, rMonth, rDay, score);
		}

		cursor.close();
		db.close();

		return data;

	}

	public void setUserVoiceRecordUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE UserVoiceRecord SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	public boolean hasAudio(TimeValue tv) {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM UserVoiceRecord WHERE" + " recordYear =" + tv.year + " AND recordMonth=" + tv.month
				+ " AND recordDay =" + tv.day;
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count > 0;
	}

	// StorytellingRead
	public StorytellingRead getLatestStorytellingRead() {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM  StorytellingRead WHERE addedScore = 1 ORDER BY ts DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return new StorytellingRead(0, false, 0, 0);
		}
		long ts = cursor.getLong(4);
		boolean addedScore = (cursor.getInt(7) == 1);
		int page = cursor.getInt(8);
		int score = cursor.getInt(9);
		return new StorytellingRead(ts, addedScore, page, score);
	}

	public int insertStorytellingRead(StorytellingRead data) {
		synchronized (sqlLock) {
			StorytellingRead prev_data = getLatestStorytellingRead();
			int addScore = 0;
			int addedScore = 0;
			if (data.tv.afterADay(prev_data.tv)) {
				addScore = 3;
				addedScore = 1;
			}
			if (!StartDateCheck.afterStartDate()) {
				addScore = 0;
				addedScore = 0;
			}
			
			db = dbHelper.getWritableDatabase();
			ContentValues content = new ContentValues();
			content.put("year", data.tv.year);
			content.put("month", data.tv.month);
			content.put("day", data.tv.day);
			content.put("ts", data.tv.timestamp);
			content.put("week", data.tv.week);
			content.put("timeSlot", data.tv.timeslot);
			content.put("addedScore", addedScore);
			content.put("page", data.page);
			content.put("score", prev_data.score + addScore);
			db.insert("StorytellingRead", null, content);
			db.close();
			return addScore;
		}
	}

	public StorytellingRead[] getNotUploadedStorytellingRead() {
		StorytellingRead[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM StorytellingRead WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new StorytellingRead[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(4);
			boolean addedScore = (cursor.getInt(7) == 1);
			int page = cursor.getInt(8);
			int score = cursor.getInt(9);
			data[i] = new StorytellingRead(ts, addedScore, page, score);
		}

		cursor.close();
		db.close();

		return data;

	}

	public void setStorytellingReadUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE StorytellingRead SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	// GCMRead
	public GCMRead getGCMRead(long timestamp) {
		db = dbHelper.getReadableDatabase();
		String sql = "SELECT * FROM GCMRead WHERE ts=" + timestamp + " LIMIT 1";
		Cursor cursor = db.rawQuery(sql, null);
		GCMRead data = null;
		if (cursor.moveToFirst()) {
			long ts = cursor.getLong(1);
			long readTs = cursor.getLong(2);
			String message = cursor.getString(3);
			data = new GCMRead(ts, readTs, message, false);
		}
		cursor.close();
		db.close();
		return data;
	}

	public void insertGCMRead(GCMRead data) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();

			ContentValues content = new ContentValues();
			content.put("ts", data.tv.timestamp);
			content.put("message", data.message);
			content.put("read", data.read ? 1 : 0);
			db.insert("GCMRead", null, content);
			db.close();
		}
	}

	public void readGCMRead(GCMRead data) {
		synchronized (sqlLock) {
			String sql;
			db = dbHelper.getWritableDatabase();
			sql = "UPDATE GCMRead SET  read = 1, readTs =" + data.readTv.timestamp + " WHERE ts=" + data.tv.timestamp;
			db.execSQL(sql);
			db.close();
		}
	}

	public GCMRead[] getNotReadGCMRead() {
		GCMRead[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		long ts_lower_bound = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000;
		sql = "SELECT * FROM GCMRead WHERE read = 0 AND ts>" + ts_lower_bound;
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new GCMRead[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(1);
			long readTs = cursor.getLong(2);
			String message = cursor.getString(3);
			data[i] = new GCMRead(ts, readTs, message, false);
		}

		cursor.close();
		db.close();

		return data;
	}

	public GCMRead[] getNotUploadedGCMRead() {
		GCMRead[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM GCMRead WHERE read = 1 AND upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new GCMRead[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(1);
			long readTs = cursor.getLong(2);
			String message = cursor.getString(3);
			data[i] = new GCMRead(ts, readTs, message, false);
		}

		cursor.close();
		db.close();

		return data;
	}

	public void setGCMReadUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE GCMRead SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	// FacebookInfo
	public FacebookInfo getLatestFacebookInfo() {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM  FacebookInfo WHERE addedScore = 1 ORDER BY ts DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return new FacebookInfo(0, 0, 0, "", false, false, 0, 0);
		}
		long ts = cursor.getLong(4);
		int pageWeek = cursor.getInt(7);
		int pageLevel = cursor.getInt(8);
		String text = cursor.getString(9);
		boolean addedScore = (cursor.getInt(10) == 1);
		boolean uploadSuccess = (cursor.getInt(11) == 1);
		int privacy = cursor.getInt(12);
		int score = cursor.getInt(13);
		return new FacebookInfo(ts, pageWeek, pageLevel, text, addedScore, uploadSuccess, privacy, score);
	}

	public int insertFacebookInfo(FacebookInfo data) {
		synchronized (sqlLock) {
			FacebookInfo prev_data = getLatestFacebookInfo();
			int addScore = 0;
			if (data.tv.afterAWeek(prev_data.tv) && data.uploadSuccess)
				addScore = 1;
			if (!StartDateCheck.afterStartDate())
				addScore = 0;
			
			db = dbHelper.getWritableDatabase();
			ContentValues content = new ContentValues();
			content.put("year", data.tv.year);
			content.put("month", data.tv.month);
			content.put("day", data.tv.day);
			content.put("ts", data.tv.timestamp);
			content.put("week", data.tv.week);
			content.put("timeslot", data.tv.timeslot);
			content.put("pageWeek", data.pageWeek);
			content.put("pageLevel", data.pageLevel);
			content.put("text", data.text);
			content.put("addedScore", addScore);
			content.put("uploadSuccess", data.uploadSuccess ? 1 : 0);
			content.put("privacy", data.privacy);
			content.put("score", prev_data.score + addScore);
			db.insert("FacebookInfo", null, content);
			db.close();
			return addScore;
		}
	}

	public FacebookInfo[] getNotUploadedFacebookInfo() {
		FacebookInfo[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM FacebookInfo WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new FacebookInfo[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(4);
			int pageWeek = cursor.getInt(7);
			int pageLevel = cursor.getInt(8);
			String text = cursor.getString(9);
			boolean addedScore = (cursor.getInt(10) == 1);
			boolean uploadSuccess = (cursor.getInt(11) == 1);
			int privacy = cursor.getInt(12);
			int score = cursor.getInt(13);
			data[i] = new FacebookInfo(ts, pageWeek, pageLevel, text, addedScore, uploadSuccess, privacy, score);
		}

		cursor.close();
		db.close();

		return data;

	}

	public void setFacebookInfoUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE FacebookInfo SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	// Additional Questionnaire
	public AdditionalQuestionnaire getLatestAdditionalQuestionnaire() {
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM  AdditionalQuestionnaire ORDER BY ts DESC LIMIT 1";
		cursor = db.rawQuery(sql, null);
		if (!cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return new AdditionalQuestionnaire(0, false, 0, 0, 0);
		}
		long ts = cursor.getLong(4);
		boolean addedScore = (cursor.getInt(7) == 1);
		int emotion = cursor.getInt(8);
		int craving = cursor.getInt(9);
		int score = cursor.getInt(10);
		return new AdditionalQuestionnaire(ts, addedScore, emotion, craving, score);
	}

	public int insertAdditionalQuestionnaire(AdditionalQuestionnaire data) {
		synchronized (sqlLock) {
			AdditionalQuestionnaire prev_data = getLatestAdditionalQuestionnaire();
			int addScore = data.addedScore ? 1 : 0;
			if (!StartDateCheck.afterStartDate())
				addScore = 0;
			if (prev_data.tv.isSameDay(data.tv))
				addScore = 0;
			
			db = dbHelper.getWritableDatabase();
			ContentValues content = new ContentValues();
			content.put("year", data.tv.year);
			content.put("month", data.tv.month);
			content.put("day", data.tv.day);
			content.put("ts", data.tv.timestamp);
			content.put("week", data.tv.week);
			content.put("timeSlot", data.tv.timeslot);
			content.put("addedScore", addScore);
			content.put("emotion", data.emotion);
			content.put("craving", data.craving);
			content.put("score", prev_data.score + addScore);
			db.insert("AdditionalQuestionnaire", null, content);
			db.close();
			return addScore;
		}
	}

	public AdditionalQuestionnaire[] getNotUploadedAdditionalQuestionnaire() {
		AdditionalQuestionnaire[] data = null;

		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;

		sql = "SELECT * FROM AdditionalQuestionnaire WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new AdditionalQuestionnaire[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(4);
			boolean addedScore = (cursor.getInt(7) == 1);
			int emotion = cursor.getInt(8);
			int craving = cursor.getInt(9);
			int score = cursor.getInt(10);
			data[i] = new AdditionalQuestionnaire(ts, addedScore, emotion, craving, score);
		}

		cursor.close();
		db.close();

		return data;

	}

	public void setAdditionalQuestionnaireUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE AdditionalQuestionnaire SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	// UserVoiceFeedback
	public void insertUserVoiceFeedback(UserVoiceFeedback data) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			ContentValues content = new ContentValues();
			content.put("ts", data.tv.timestamp);
			content.put("detectionTs", data.detectionTv.timestamp);
			content.put("testSuccess", data.testSuccess ? 1 : 0);
			content.put("hasData", data.hasData ? 1 : 0);
			db.insert("UserVoiceFeedback", null, content);
			db.close();
		}
	}

	public UserVoiceFeedback[] getNotUploadedUserVoiceFeedback() {
		UserVoiceFeedback[] data = null;
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		long cur_ts = System.currentTimeMillis();
		long gps_ts = PreferenceControl.getGPSStartTime() + GPSService.GPS_TOTAL_TIME;

		if (cur_ts <= gps_ts) {
			long gps_detection_ts = PreferenceControl.getDetectionTimestamp();
			sql = "SELECT * FROM UserVoiceFeedback WHERE upload = 0 AND detectionTs <> " + gps_detection_ts
					+ " ORDER BY ts ASC";
		} else {
			sql = "SELECT * FROM UserVoiceFeedback WHERE upload = 0";
		}
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new UserVoiceFeedback[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(1);
			long detectionTs = cursor.getLong(2);
			boolean testSuccess = cursor.getInt(cursor.getColumnIndex("testSuccess")) == 1;
			boolean hasData = cursor.getInt(cursor.getColumnIndex("hasData")) == 1;
			data[i] = new UserVoiceFeedback(ts, detectionTs, testSuccess, hasData);
		}

		cursor.close();
		db.close();

		return data;
	}

	public void setUserVoiceFeedbackUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE UserVoiceFeedback SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}

	// ExchangeHistory
	public void insertExchangeHistory(ExchangeHistory data) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			ContentValues content = new ContentValues();
			content.put("ts", data.tv.timestamp);
			content.put("exchangeCounter", data.exchangeNum);
			db.insert("ExchangeHistory", null, content);
			db.close();
		}
	}

	public ExchangeHistory[] getNotUploadedExchangeHistory() {
		ExchangeHistory[] data = null;
		db = dbHelper.getReadableDatabase();
		String sql;
		Cursor cursor;
		sql = "SELECT * FROM ExchangeHistory WHERE upload = 0";
		cursor = db.rawQuery(sql, null);
		int count = cursor.getCount();
		if (count == 0) {
			cursor.close();
			db.close();
			return null;
		}

		data = new ExchangeHistory[count];

		for (int i = 0; i < count; ++i) {
			cursor.moveToPosition(i);
			long ts = cursor.getLong(1);
			int exchangeCounter = cursor.getInt(2);
			data[i] = new ExchangeHistory(ts, exchangeCounter);
		}
		cursor.close();
		db.close();
		return data;
	}

	public void setExchangeHistoryUploaded(long ts) {
		synchronized (sqlLock) {
			db = dbHelper.getWritableDatabase();
			String sql = "UPDATE ExchangeHistory SET upload = 1 WHERE ts = " + ts;
			db.execSQL(sql);
			db.close();
		}
	}
}
