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

	private static String SERVER_URL_USER="";
	private static String SERVER_URL_DETECTION="";
	private static String SERVER_URL_EMOTION_DIY="";
	private static String SERVER_URL_EMOTION_MANAGEMENT="";
	private static String SERVER_URL_QUESTIONNAIRE="";
	private static String SERVER_URL_STORYTELLING_TEST="";
	private static String SERVER_URL_STORYTELLING_READ="";
	private static String SERVER_URL_GCM_READ="";
	private static String SERVER_URL_FACEBOOK_INFO="";
	private static String SERVER_URL_USER_VOICE_RECORD="";
	private static String SERVER_URL_ADDITIONAL_QUESTIONNAIRE="";
	private static String SERVER_URL_CLICKLOG="";
	private static String SERVER_URL_USER_VOICE_FEEDBACK="";
	private static String SERVER_URL_EXCHANGE_HISTORY="";
	
	public static HttpPost genPost(){
		SERVER_URL_USER = ServerUrl.SERVER_URL_USER();
		HttpPost httpPost = new HttpPost(SERVER_URL_USER);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		nvps.add(new BasicNameValuePair("uid", uid));
		
		Calendar c = PreferenceControl.getStartDate();
		String joinDate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
		
		nvps.add(new BasicNameValuePair("userData[]",joinDate));
		nvps.add(new BasicNameValuePair("userData[]",PreferenceControl.getSensorID()));
		nvps.add(new BasicNameValuePair("userData[]",String.valueOf(PreferenceControl.getUsedCounter())));
		PackageInfo pinfo;
		try {
			pinfo = App.context.getPackageManager().getPackageInfo(App.context.getPackageName(), 0);
			String versionName = pinfo.versionName;
			nvps.add(new BasicNameValuePair("userData[]",versionName));
		} catch (NameNotFoundException e) {}
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		
		return httpPost;
	}

	public static HttpPost genPost(Detection detection){
		SERVER_URL_DETECTION = ServerUrl.SERVER_URL_DETECTION();
		File mainStorageDir = MainStorage.getMainStorageDirectory();
		String uid = PreferenceControl.getUID();
		HttpPost httpPost = new HttpPost(SERVER_URL_DETECTION);
		MultipartEntity mpEntity = new MultipartEntity();
		
		try {
			mpEntity.addPart("uid", new StringBody(uid));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.tv.timestamp)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.tv.week)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.emotion)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.craving)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.isPrime ? 1 : 0)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.weeklyScore)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(detection.score)));
		} catch (UnsupportedEncodingException e) {}
		
		String _ts = String.valueOf(detection.tv.timestamp);
		File[] imageFiles;
		File textFile, geoFile, detectionFile,geoAccuracyFile;
		imageFiles = new File[3];

		textFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + _ts + ".txt");
		geoFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "geo.txt");
		detectionFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator
				+ "detection_detail.txt");
		geoAccuracyFile = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "geoAccuracy.txt");

		for (int i = 0; i < imageFiles.length; ++i)
			imageFiles[i] = new File(mainStorageDir.getPath() + File.separator + _ts + File.separator + "IMG_"
					+ _ts + "_" + (i + 1) + ".sob");

		ContentBody cbFile = new FileBody(textFile, "application/octet-stream");
		mpEntity.addPart("file[]", cbFile);
		if (geoFile.exists()) {
			ContentBody cbGeoFile = new FileBody(geoFile, "application/octet-stream");
			mpEntity.addPart("file[]", cbGeoFile);
		}
		if (geoAccuracyFile.exists()){
			ContentBody cbGeoAccuracyFile = new FileBody(geoAccuracyFile, "application/octet-stream");
			mpEntity.addPart("file[]",cbGeoAccuracyFile);
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

	public static HttpPost genPost(EmotionDIY data){
		SERVER_URL_EMOTION_DIY = ServerUrl.SERVER_URL_EMOTION_DIY();
		HttpPost httpPost = new HttpPost(SERVER_URL_EMOTION_DIY);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.week)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.selection)));
		nvps.add(new BasicNameValuePair("data[]", data.recreation));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.score)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}

	public static HttpPost genPost(EmotionManagement data){
		SERVER_URL_EMOTION_MANAGEMENT = ServerUrl.SERVER_URL_EMOTION_MANAGEMENT();
		HttpPost httpPost = new HttpPost(SERVER_URL_EMOTION_MANAGEMENT);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.week)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.recordTv.year)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.recordTv.month+1)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.recordTv.day)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.emotion)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.type)));
		nvps.add(new BasicNameValuePair("data[]", data.reason));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.score)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}

	public static HttpPost genPost(Questionnaire data){
		SERVER_URL_QUESTIONNAIRE = ServerUrl.SERVER_URL_QUESTIONNAIRE();
		HttpPost httpPost = new HttpPost(SERVER_URL_QUESTIONNAIRE);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.week)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.type)));
		nvps.add(new BasicNameValuePair("data[]", data.seq));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.score)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}

	public static HttpPost genPost(StorytellingTest data){
		SERVER_URL_STORYTELLING_TEST = ServerUrl.SERVER_URL_STORYTELLING_TEST();
		HttpPost httpPost = new HttpPost(SERVER_URL_STORYTELLING_TEST);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.week)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.questionPage)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.isCorrect?1:0)));
		nvps.add(new BasicNameValuePair("data[]", data.selection));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.agreement)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.score)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}

	public static HttpPost genPost(StorytellingRead data){
		SERVER_URL_STORYTELLING_READ = ServerUrl.SERVER_URL_STORYTELLING_READ();
		HttpPost httpPost = new HttpPost(SERVER_URL_STORYTELLING_READ);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.week)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.addedScore?1:0)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.page)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.score)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}

	public static HttpPost genPost(GCMRead data){
		SERVER_URL_GCM_READ = ServerUrl.SERVER_URL_GCM_READ();
		HttpPost httpPost = new HttpPost(SERVER_URL_GCM_READ);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.readTv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", data.message));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}

	public static HttpPost genPost(FacebookInfo data){
		SERVER_URL_FACEBOOK_INFO = ServerUrl.SERVER_URL_FACEBOOK();
		HttpPost httpPost = new HttpPost(SERVER_URL_FACEBOOK_INFO);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.week)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.pageWeek)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.pageLevel)));
		nvps.add(new BasicNameValuePair("data[]", data.text));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.addedScore?1:0)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.uploadSuccess?1:0)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.privacy)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.score)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}

	public static HttpPost genPost(UserVoiceRecord data){
		SERVER_URL_USER_VOICE_RECORD = ServerUrl.SERVER_URL_USER_VOICE();
		HttpPost httpPost = new HttpPost(SERVER_URL_USER_VOICE_RECORD);
		String uid = PreferenceControl.getUID();
		File mainStorageDir = new File(MainStorage.getMainStorageDirectory(),"audio_records");
		if (!mainStorageDir.exists())
			mainStorageDir.mkdirs();
		boolean uploadFile = PreferenceControl.uploadVoiceRecord();
		
		MultipartEntity mpEntity = new MultipartEntity();
		
		try {
			mpEntity.addPart("uid", new StringBody(uid));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.tv.timestamp)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.tv.week)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.recordTv.year)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.recordTv.month+1)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.recordTv.day)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.score)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(uploadFile?1:0)));
		} catch (UnsupportedEncodingException e) {}
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
	
	public static HttpPost genPost(AdditionalQuestionnaire data){
		SERVER_URL_ADDITIONAL_QUESTIONNAIRE = ServerUrl.SERVER_URL_ADDITIONAL_QUESTIONNAIRE();
		HttpPost httpPost = new HttpPost(SERVER_URL_ADDITIONAL_QUESTIONNAIRE);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.week)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.emotion)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.craving)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.addedScore?1:0)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.score)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}

	public static HttpPost genPost(File logFile){//Clicklog
		SERVER_URL_CLICKLOG = ServerUrl.SERVER_URL_CLICKLOG();
		HttpPost httpPost = new HttpPost(SERVER_URL_CLICKLOG);
		String uid = PreferenceControl.getUID();
		MultipartEntity mpEntity = new MultipartEntity();
		try {
			mpEntity.addPart("uid", new StringBody(uid));
		} catch (UnsupportedEncodingException e) {}			
		
		if (logFile.exists()){
			ContentBody cbLogFile = new FileBody(logFile, "application/octet-stream");
			mpEntity.addPart("file[]", cbLogFile);
		}
		
		httpPost.setEntity(mpEntity);
		return httpPost;
	}
	
	public static HttpPost genPost(UserVoiceFeedback data){
		SERVER_URL_USER_VOICE_FEEDBACK = ServerUrl.SERVER_URL_USER_FEEDBACK();
		HttpPost httpPost = new HttpPost(SERVER_URL_USER_VOICE_FEEDBACK);
		String uid = PreferenceControl.getUID();
		File mainStorageDir = new File(MainStorage.getMainStorageDirectory(),"feedbacks");
		if (!mainStorageDir.exists())
			mainStorageDir.mkdirs();
		
		MultipartEntity mpEntity = new MultipartEntity();
		
		try {
			mpEntity.addPart("uid", new StringBody(uid));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.tv.timestamp)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.detectionTv.timestamp)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.testSuccess?1:0)));
			mpEntity.addPart("data[]", new StringBody(String.valueOf(data.hasData?1:0)));
		} catch (UnsupportedEncodingException e) {}
		if (mainStorageDir != null) {
			File audio = new File(mainStorageDir, data.detectionTv.timestamp + ".3gp");
			if (audio.exists() && data.hasData) {
				ContentBody aFile = new FileBody(audio, "application/octet-stream");
				mpEntity.addPart("file[]", aFile);
			}
		}
		httpPost.setEntity(mpEntity);
		return httpPost;
	}
	
	public static HttpPost genPost(ExchangeHistory data){
		SERVER_URL_EXCHANGE_HISTORY = ServerUrl.SERVER_URL_EXCHANGE_HISTORY();
		HttpPost httpPost = new HttpPost(SERVER_URL_EXCHANGE_HISTORY);
		String uid = PreferenceControl.getUID();
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("uid", uid));
		
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.tv.timestamp)));
		nvps.add(new BasicNameValuePair("data[]", String.valueOf(data.exchangeNum)));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {}
		return httpPost;
	}

}
