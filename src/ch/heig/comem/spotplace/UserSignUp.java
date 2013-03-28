package ch.heig.comem.spotplace;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import ch.heig.comem.spotplace.servicesRest.HttpMode;
import ch.heig.comem.spotplace.servicesRest.RequestRest;

public class UserSignUp extends Activity implements ObserverREST {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_sign_up);
		
		Spinner spinner = (Spinner) findViewById(R.id.signUp_canton_Input);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.canton_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        
		Button buttonSignUp = (Button) findViewById(R.id.signUp_button);
		buttonSignUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONObject user = new JSONObject();
				
				EditText inputUserName = (EditText) findViewById(R.id.signUp_username_Input);
				EditText inputPassword = (EditText) findViewById(R.id.signUp_password_Input);
				EditText inputEmail = (EditText) findViewById(R.id.signUp_email_Input);
				Spinner inputCanton = (Spinner) findViewById(R.id.signUp_canton_Input);
				
				String userName = inputUserName.getText().toString();			
				String password = inputPassword.getText().toString();				
				String email = inputEmail.getText().toString();				
				String canton = inputCanton.getSelectedItem().toString();
				try {
					user.put("userName", userName);
					user.put("password", password);
					user.put("email", email);
					user.put("canton", canton);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("Mon objet JSON comment : ", user.toString());
				
				new RequestRest(UserSignUp.this, "users", HttpMode.POST, user).execute();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void notifyCallBack(JSONObject objectJSON) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyEndOfExecution(String result) {
		Intent intent = new Intent(UserSignUp.this, LoginPage.class);
		
		startActivity(intent);
		Toast toast = Toast.makeText(getApplicationContext(), "Account created",  Toast.LENGTH_SHORT);
		toast.show();
		
	}

 
        

}
