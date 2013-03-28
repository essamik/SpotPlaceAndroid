package ch.heig.comem.spotplace.servicesRest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import ch.heig.comem.spotplace.ObserverREST;
import ch.heig.comem.spotplace.utilities.ServerUrl;

public class CheckLogin extends AsyncTask <Void,  Void, String> { //param, barre de loading, Résultat du doInBackground
	private String serverUrl = new ServerUrl().serverUrl;
	private String serverPort= new ServerUrl().serverPort;
	private String path = "http://"+this.serverUrl+":"+this.serverPort+"/SpotPlaceApplication-master/webresources/";

	private JSONObject objectJSON;
	private ObserverREST callBack;

	public CheckLogin(ObserverREST rest, String requete, JSONObject objJSON)
	{
		
		this.path = path + requete;
		this.objectJSON = objJSON;
		this.callBack = rest;
	}

	@Override
	protected String doInBackground(Void... params) {
		String result = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();

			HttpPost httpPostRequest = new HttpPost(this.path);
			StringEntity se;
			se = new StringEntity(this.objectJSON.toString());
			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Content-type", "application/json");

			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
			
			
			result = EntityUtils.toString(response.getEntity());
			
			Log.d("RESULT DO IN BACKGROUND : " , result);

		}	catch (Exception e)
		{
			e.printStackTrace();
		}


	return result;
}	

@Override
protected void onPostExecute(String result) {
	Log.d("RESULT ON POST EXECUTE : " , result);
	this.callBack.notifyEndOfExecution(result);


}



}
