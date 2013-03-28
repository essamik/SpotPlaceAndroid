package ch.heig.comem.spotplace.servicesRest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import ch.heig.comem.spotplace.ObserverREST;
import ch.heig.comem.spotplace.utilities.ServerUrl;

public class RequestRest extends AsyncTask <Void,  Void, String> { //param, barre de loading, Résultat du doInBackground
	private String serverUrl= new ServerUrl().serverUrl;
	private String serverPort= new ServerUrl().serverPort;
	private String path = "http://"+this.serverUrl+":"+this.serverPort+"/SpotPlaceApplication/webresources/";
	private ObserverREST callBack;
	private HttpMode modeRequest;
	private JSONObject objectJSON;

	public RequestRest(ObserverREST rest, String requete, HttpMode mode)
	{
		this.callBack = rest;
		Log.d("Longueur requête : ", ""+requete.length());
		if(requete.length() < 20) {
			this.path = path + requete;
		} else {
			this.path = requete;
		}

		this.modeRequest = mode;
	}

	public RequestRest(ObserverREST rest, String requete, HttpMode mode, JSONObject objJSON) {
		this(rest, requete, mode);
		this.objectJSON = objJSON;
	}

	@Override
	protected String doInBackground(Void... params) {
		String result = null;
		HttpClient httpclient = new DefaultHttpClient(); 
		if(modeRequest == HttpMode.GET)
			result = this.executeGet(httpclient);
		else if(modeRequest == HttpMode.POST) 
			result = this.executePost(httpclient);
		else if(modeRequest == HttpMode.PUT) 
			result = this.executePut(httpclient);

		return result;
	}	


	private String executePut(HttpClient httpclient) {
		String result = null;
		try {
			HttpPut httpPutRequest = new HttpPut(this.path);
			StringEntity se;
			se = new StringEntity(this.objectJSON.toString());
			httpPutRequest.setEntity(se);
			httpPutRequest.setHeader("Content-type", "application/json");

			HttpResponse response = (HttpResponse) httpclient.execute(httpPutRequest);
			result = response.toString();


		}	catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	private String executePost(HttpClient httpclient) {
		try {

			HttpPost httpPostRequest = new HttpPost(this.path);
			StringEntity se;
			se = new StringEntity(this.objectJSON.toString());
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Content-type", "application/json");

			httpclient.execute(httpPostRequest);

		}	catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private String executeGet(HttpClient httpclient) {
		String result = null;
		HttpGet request = new HttpGet(path);  
		request.setHeader("Accept", "application/json");
		ResponseHandler<String> handler = new BasicResponseHandler();  
		try {  
			result = httpclient.execute(request, handler);  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		httpclient.getConnectionManager().shutdown();  
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		if(modeRequest == HttpMode.GET) {

			Log.d("Contenu  : ", result);
			JSONObject jsonObjectResult = null;
			try {
				jsonObjectResult = new JSONObject(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			this.callBack.notifyCallBack(jsonObjectResult);
		} else {
			this.callBack.notifyEndOfExecution(null);
		}
	}
}
