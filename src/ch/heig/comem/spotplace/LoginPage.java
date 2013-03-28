package ch.heig.comem.spotplace;

import org.json.JSONException;
import org.json.JSONObject;

import ch.heig.comem.spotplace.servicesRest.CheckLogin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPage extends Activity implements ObserverREST{
	public final static String ID_USER = "MyPrefsFile";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences preferences = getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);

		if(preferences.getInt(LoginPage.ID_USER, 0) != 0) {
			int idUser = preferences.getInt(LoginPage.ID_USER, 0);
			Intent intent = new Intent(LoginPage.this, SpotList.class);
		
			startActivity(intent);
		}

		Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				EditText inPutLogin = (EditText) findViewById(R.id.inputLogin);
				String userName = inPutLogin.getText().toString();
				EditText inputPassword = (EditText) findViewById(R.id.inputPassword);
				String password = inputPassword.getText().toString();

				JSONObject objectAuth = new JSONObject();

				try {
					objectAuth.put("userName", userName);
					objectAuth.put("password", password);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.d("My login : ", objectAuth.toString());
				new CheckLogin(LoginPage.this, "auth", objectAuth).execute();
			}
		});

		TextView labelSignUp = (TextView) findViewById(R.id.labelRegistration);
		labelSignUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intentInscription = new Intent(LoginPage.this, UserSignUp.class);
				startActivity(intentInscription);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}


	@Override
	public void notifyCallBack(JSONObject objectJSON) {

	}

	@Override
	public void notifyEndOfExecution(String idUser) {
		Log.d("My user id : ", idUser);
		if(!idUser.equals("0")) {
			Intent intent = new Intent(LoginPage.this, SpotList.class);

			SharedPreferences preferences = getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);

			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt(ID_USER, Integer.valueOf(idUser));
//			editor.putString(ID_USER, idUser);

			editor.commit();

			startActivity(intent);
			Toast toast = Toast.makeText(getApplicationContext(), "Connexion...",  Toast.LENGTH_SHORT);
			toast.show();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Wrong login !",  Toast.LENGTH_SHORT);
			toast.show();
		}			
	}	
}
