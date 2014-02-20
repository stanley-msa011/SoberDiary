package ubicomp.soberdiary.system.gcm;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import com.google.android.gcm.GCMRegistrar;


import ubicomp.soberdiary.system.config.PreferenceControl;
import ubicomp.soberdiary.system.uploader.HttpSecureClientGenerator;
import ubicomp.soberdiary.system.uploader.ServerUrl;
import android.content.Context;

public class GCMRegisterUtilities {

	public static boolean register(Context context,String regId){
		
        String serverUrl = ServerUrl.SERVER_URL_GCM_REGISTER();
        String uid = PreferenceControl.getUID();
        
		try {
			DefaultHttpClient httpClient = HttpSecureClientGenerator.getSecureHttpClient();
			HttpPost httpPost = new HttpPost(serverUrl);
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();
			mpEntity.addPart("uid", new StringBody(uid));
			mpEntity.addPart("regId", new StringBody(regId));
			httpPost.setEntity(mpEntity);
			boolean result =uploader(httpClient, httpPost);
			GCMRegistrar.setRegisteredOnServer(context, result);
			return result;
		} catch (Exception e) {
			GCMRegistrar.setRegisteredOnServer(context, false);
			return false;
		} 
	}
	
	private static boolean uploader(HttpClient httpClient, HttpPost httpPost){
		HttpResponse httpResponse;
		ResponseHandler <String> res=new BasicResponseHandler();  
		boolean  result = false;
		try {
			httpResponse = httpClient.execute(httpPost);
			result = (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK);
			if (result){
				String response = res.handleResponse(httpResponse).toString();
				result = response.contains("upload success");
			}
		} catch (Exception e) {
		} finally{
			if ( httpClient!=null){
				ClientConnectionManager ccm= httpClient.getConnectionManager();
					if (ccm!=null)
						ccm.shutdown();
				}
		}
		return result;
	}
}
