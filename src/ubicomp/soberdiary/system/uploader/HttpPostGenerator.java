package ubicomp.soberdiary.system.uploader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import ubicomp.soberdiary.data.file.MainStorage;
import ubicomp.soberdiary.data.structure.AdditionalQuestionnaire;
import ubicomp.soberdiary.data.structure.Detection;
import ubicomp.soberdiary.data.structure.EmotionDIY;
import ubicomp.soberdiary.data.structure.EmotionManagement;
import ubicomp.soberdiary.data.structure.ExchangeHistory;
import ubicomp.soberdiary.data.structure.FacebookInfo;
import ubicomp.soberdiary.data.structure.GCMRead;
import ubicomp.soberdiary.data.structure.Questionnaire;
import ubicomp.soberdiary.data.structure.StorytellingRead;
import ubicomp.soberdiary.data.structure.StorytellingTest;
import ubicomp.soberdiary.data.structure.UserVoiceFeedback;
import ubicomp.soberdiary.data.structure.UserVoiceRecord;
import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class HttpPostGenerator {

	private static String SERVER_URL_USER = "";
	private static String SERVER_URL_DETECTION = "";
	private static String SERVER_URL_EMOTION_DIY = "";
	private static String SERVER_URL_EMOTION_MANAGEMENT = "";
	private static String SERVER_URL_QUESTIONNAIRE = "";
	private static String SERVER_URL_STORYTELLING_TEST = "";
	private static String SERVER_URL_STORYTELLING_READ = "";
	private static String SERVER_URL_GCM_READ = "";
	private static String SERVER_URL_FACEBOOK_INFO = "";
	private static String SERVER_URL_USER_VOICE_RECORD = "";
	private static String SERVER_URL_ADDITIONAL_QUESTIONNAIRE = "";
	private static String SERVER_URL_CLICKLOG = "";
	private static String SERVER_URL_USER_VOICE_FEEDBACK = "";
	private static String SERVER_URL_EXCHANGE_HISTORY = "";

	public static HttpPost genPost() {
		SERVER_URL_USER = ServerUrl.SERVER_URL_USER();
		HttpPost httpPost = new HttpPost(SERVER_URL_USER);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		nvps.add(new BasicNameValuePair("uid", uid));

		Calendar c = PreferenceControl.getStartDate();
		String joinDate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);

		nvps.add(new BasicNameValuePair("userData[]", joinDate));
		nvps.add(new BasicNameValuePair("userData[]", PreferenceControl.getSensorID()));
		nvps.add(new BasicNameValuePair("userData[]", String.valueOf(PreferenceControl.getUsedCounter())));
		PackageInfo pinfo;
		try {
			pinfo = App.getContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0);
			String versionName = pinfo.versionName;
			nvps.add(new BasicNameValuePair("userData[]", versionName));
		} catch (NameNotFoundException e) {
		}

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}

		return httpPost;
	}

	public static HttpPost genPost(Detection detection) {
		SERVER_URL_DETECTION = ServerUrl.SERVER_URL_DETECTION();
		File mainStorageDir = MainStorage.getMainStorageDirectory();
		String uid = PreferenceControl.getUID();
		HttpPost httpPost = new HttpPost(SERVER_URL_DETECTION);
		MultipartEntity mpEntity = new MultipartEntity();

		try {
			mpEntity.addPart("uid", new StringBody(uid));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.getTv().getTimestamp())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.getTv().getWeek())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.getEmotion())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.getCraving())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.isPrime() ? 1 : 0)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.getWeeklyScore())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.getScore())));
		} catch (UnsupportedEncodingException e) {
		}

		String _ts = String.valueOf(detection.getTv().getTimestamp());
		File[] imageFiles;
		File textFile, geoFile, detectionFile, geoAccuracyFile;
		imageFiles = new File[3];

		textFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + _ts + ".txt");
		geoFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "geo.txt");
		detectionFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator
				+ "detection_detail.txt");
		geoAccuracyFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "geoAccuracy.txt");

		for (int i = 0; i < imageFiles.length; ++i)
			imageFiles[i] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_" + _ts
					+ "_" + (i + 1) + ".sob");

		ContentBody cbFile = new FileBody(textFile, "application/octet-stream");
		mpEntity.addPart("file[]", cbFile);
		if (geoFile.exists()) {
			ContentBody cbGeoFile = new FileBody(geoFile, "application/octet-stream");
			mpEntity.addPart("file[]", cbGeoFile);
		}
		if (geoAccuracyFile.exists()) {
			ContentBody cbGeoAccuracyFile = new FileBody(geoAccuracyFile, "application/octet-stream");
			mpEntity.addPart("file[]", cbGeoAccuracyFile);
		}

		if (detectionFile.exists()) {
			ContentBody cbDetectionFile = new FileBody(detectionFile, "application/octet-stream");
			mpEntity.addPart("file[]", cbDetectionFile);
		}

		for (int i = 0; i < imageFiles.length; ++i) {
			if (imageFiles[i].exists())
				mpEntity.addPart("file[]", new FileBody(imageFiles[i], "image/jpeg"));
		}

		httpPost.setEntity(mpEntity);
		return httpPost;
	}

	public static HttpPost genPost(EmotionDIY data) {
		SERVER_URL_EMOTION_DIY = ServerUrl.SERVER_URL_EMOTION_DIY();
		HttpPost httpPost = new HttpPost(SERVER_URL_EMOTION_DIY);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getSelection())));
		nvps.add(new BasicNameValuePair("data[]", data.getRecreation()));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}

	public static HttpPost genPost(EmotionManagement data) {
		SERVER_URL_EMOTION_MANAGEMENT = ServerUrl.SERVER_URL_EMOTION_MANAGEMENT();
		HttpPost httpPost = new HttpPost(SERVER_URL_EMOTION_MANAGEMENT);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getRecordTv().getYear())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getRecordTv().getMonth() + 1)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getRecordTv().getDay())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getEmotion())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getType())));
		nvps.add(new BasicNameValuePair("data[]", data.getReason()));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}

	public static HttpPost genPost(Questionnaire data) {
		SERVER_URL_QUESTIONNAIRE = ServerUrl.SERVER_URL_QUESTIONNAIRE();
		HttpPost httpPost = new HttpPost(SERVER_URL_QUESTIONNAIRE);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getType())));
		nvps.add(new BasicNameValuePair("data[]", data.getSeq()));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}

	public static HttpPost genPost(StorytellingTest data) {
		SERVER_URL_STORYTELLING_TEST = ServerUrl.SERVER_URL_STORYTELLING_TEST();
		HttpPost httpPost = new HttpPost(SERVER_URL_STORYTELLING_TEST);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getQuestionPage())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.isCorrect() ? 1 : 0)));
		nvps.add(new BasicNameValuePair("data[]", data.getSelection()));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getAgreement())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}

	public static HttpPost genPost(StorytellingRead data) {
		SERVER_URL_STORYTELLING_READ = ServerUrl.SERVER_URL_STORYTELLING_READ();
		HttpPost httpPost = new HttpPost(SERVER_URL_STORYTELLING_READ);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.isAddedScore() ? 1 : 0)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getPage())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}

	public static HttpPost genPost(GCMRead data) {
		SERVER_URL_GCM_READ = ServerUrl.SERVER_URL_GCM_READ();
		HttpPost httpPost = new HttpPost(SERVER_URL_GCM_READ);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getReadTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", data.getMessage()));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}

	public static HttpPost genPost(FacebookInfo data) {
		SERVER_URL_FACEBOOK_INFO = ServerUrl.SERVER_URL_FACEBOOK();
		HttpPost httpPost = new HttpPost(SERVER_URL_FACEBOOK_INFO);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getPageWeek())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getPageLevel())));
		nvps.add(new BasicNameValuePair("data[]", data.getText()));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.isAddedScore() ? 1 : 0)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.isUploadSuccess() ? 1 : 0)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getPrivacy())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}

	public static HttpPost genPost(UserVoiceRecord data) {
		SERVER_URL_USER_VOICE_RECORD = ServerUrl.SERVER_URL_USER_VOICE();
		HttpPost httpPost = new HttpPost(SERVER_URL_USER_VOICE_RECORD);
		String uid = PreferenceControl.getUID();
		File mainStorageDir = new File(MainStorage.getMainStorageDirectory(), "audio_records");
		if (!mainStorageDir.exists())
			mainStorageDir.mkdirs();
		boolean uploadFile = PreferenceControl.uploadVoiceRecord();

		MultipartEntity mpEntity = new MultipartEntity();

		try {
			mpEntity.addPart("uid", new StringBody(uid));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.getTv().getTimestamp())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.getTv().getWeek())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.getRecordTv().getYear())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.getRecordTv().getMonth() + 1)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.getRecordTv().getDay())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.getScore())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(uploadFile ? 1 : 0)));
		} catch (UnsupportedEncodingException e) {
		}
		if (mainStorageDir != null) {
			File audio = new File(mainStorageDir, data.toFileString() + ".3gp");
			if (uploadFile && audio.exists()) {
				ContentBody aFile = new FileBody(audio, "application/octet-stream");
				mpEntity.addPart("file[]", aFile);
			}
		}
		httpPost.setEntity(mpEntity);
		return httpPost;
	}

	public static HttpPost genPost(AdditionalQuestionnaire data) {
		SERVER_URL_ADDITIONAL_QUESTIONNAIRE = ServerUrl.SERVER_URL_ADDITIONAL_QUESTIONNAIRE();
		HttpPost httpPost = new HttpPost(SERVER_URL_ADDITIONAL_QUESTIONNAIRE);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getWeek())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getEmotion())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getCraving())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.isAddedScore() ? 1 : 0)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getScore())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}

	public static HttpPost genPost(File logFile) {// Clicklog
		SERVER_URL_CLICKLOG = ServerUrl.SERVER_URL_CLICKLOG();
		HttpPost httpPost = new HttpPost(SERVER_URL_CLICKLOG);
		String uid = PreferenceControl.getUID();
		MultipartEntity mpEntity = new MultipartEntity();
		try {
			mpEntity.addPart("uid", new StringBody(uid));
		} catch (UnsupportedEncodingException e) {
		}

		if (logFile.exists()) {
			ContentBody cbLogFile = new FileBody(logFile, "application/octet-stream");
			mpEntity.addPart("file[]", cbLogFile);
		}

		httpPost.setEntity(mpEntity);
		return httpPost;
	}

	public static HttpPost genPost(UserVoiceFeedback data) {
		SERVER_URL_USER_VOICE_FEEDBACK = ServerUrl.SERVER_URL_USER_FEEDBACK();
		HttpPost httpPost = new HttpPost(SERVER_URL_USER_VOICE_FEEDBACK);
		String uid = PreferenceControl.getUID();
		File mainStorageDir = new File(MainStorage.getMainStorageDirectory(), "feedbacks");
		if (!mainStorageDir.exists())
			mainStorageDir.mkdirs();

		MultipartEntity mpEntity = new MultipartEntity();

		try {
			mpEntity.addPart("uid", new StringBody(uid));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.getTv().getTimestamp())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.getDetectionTv().getTimestamp())));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.isTestSuccess() ? 1 : 0)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.hasData() ? 1 : 0)));
		} catch (UnsupportedEncodingException e) {
		}
		if (mainStorageDir != null) {
			File audio = new File(mainStorageDir, data.getDetectionTv().getTimestamp() + ".3gp");
			if (audio.exists() && data.hasData()) {
				ContentBody aFile = new FileBody(audio, "application/octet-stream");
				mpEntity.addPart("file[]", aFile);
			}
		}
		httpPost.setEntity(mpEntity);
		return httpPost;
	}

	public static HttpPost genPost(ExchangeHistory data) {
		SERVER_URL_EXCHANGE_HISTORY = ServerUrl.SERVER_URL_EXCHANGE_HISTORY();
		HttpPost httpPost = new HttpPost(SERVER_URL_EXCHANGE_HISTORY);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));

		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getTv().getTimestamp())));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.getExchangeNum())));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
		}
		return httpPost;
	}

}
