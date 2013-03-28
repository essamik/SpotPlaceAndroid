package ch.heig.comem.spotplace;

import org.json.JSONObject;

public interface ObserverREST {
	
	public void notifyCallBack(JSONObject objectJSON);
	public void notifyEndOfExecution(String result);

}
