package ch.heig.comem.spotplace;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import ch.heig.comem.spotplace.servicesRest.HttpMode;
import ch.heig.comem.spotplace.servicesRest.RequestRest;
import ch.heig.comem.spotplace.utilities.GPSTracker;
import ch.heig.comem.spotplace.utilities.ImageToString;

public class SpotCreate extends Activity implements ObserverREST {

	private String stringValueOfImg;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spot_create);
		
		
		GPSTracker gps = new GPSTracker(this);
			gps.getLatitude(); // returns latitude
			gps.getLongitude(); // returns longitude
			Log.d("TAG GPS", ""+gps.getLatitude());
			TextView inputLatitude = (TextView) findViewById(R.id.spotCreate_latitude_Input);
			inputLatitude.setText(""+gps.getLatitude());
			TextView inputLongitude = (TextView) findViewById(R.id.spotCreate_longitude_Input);
            inputLongitude.setText(""+gps.getLongitude());				
		
		ImageButton addImg = (ImageButton) findViewById(R.id.spotCreate_photo_button);
		addImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				  Intent intent = new Intent();
				  intent.setType("image/*");
				  intent.setAction(Intent.ACTION_GET_CONTENT);
				  startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
			}
		});
		
		Button buttonCreateSpot = (Button) findViewById(R.id.spotCreate_buttonCreate);
		buttonCreateSpot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JSONObject spot = new JSONObject();
				SharedPreferences preferences = getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
				int idUser = preferences.getInt(LoginPage.ID_USER, 0);
				

				EditText inputTitle = (EditText) findViewById(R.id.spotCreate_title_Input);
				EditText inputDescription = (EditText) findViewById(R.id.spotCreate_description_Input);
				TextView inputLatitude = (TextView) findViewById(R.id.spotCreate_latitude_Input);
				TextView inputLongitude = (TextView) findViewById(R.id.spotCreate_longitude_Input);
				EditText inputHashtag = (EditText) findViewById(R.id.spotCreate_hashtags_Input);
				//GET IMG
				
				String title = inputTitle.getText().toString();			
				String description = inputDescription.getText().toString();				
				String latitude = inputLatitude.getText().toString();				
				String longitude = inputLongitude.getText().toString();
				
				String urlPhoto = SpotCreate.this.stringValueOfImg;
				
				String hashtag = inputHashtag.getText().toString();
				
				try {
					spot.put("title", title);
					spot.put("description", description);
					spot.put("latitude", latitude);
					spot.put("longitude", longitude);
					spot.put("rating", 0);
					spot.put("urlPhoto", urlPhoto);
					
					JSONArray hashtags = new JSONArray();
					List<String> items = Arrays.asList(hashtag.split("\\s*,\\s*"));
					for(String myHashTag : items) {
						JSONObject hashTag = new JSONObject();
						hashTag.put("tag", myHashTag);
						hashtags.put(hashTag);
					}					

					spot.put("hashtags", hashtags);
					JSONObject user = new JSONObject();
					user.put("id", idUser);
					spot.put("user", user);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				new RequestRest(SpotCreate.this, "spots",HttpMode.POST, spot).execute();

			}
		});
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_addSpot:
			Intent intentAddSpot = new Intent(this, SpotCreate.class);
			startActivity(intentAddSpot);
			break;
		case R.id.menu_seeSpots:
			Intent intentSeeSpots = new Intent(this, SpotList.class);
			startActivity(intentSeeSpots);
			break;
		case R.id.menu_seeUsers:
			Intent intentUsers = new Intent(this, UserList.class);
			startActivity(intentUsers);
			break;
		case R.id.menu_profil:
			Intent intentProfil = new Intent(this, UserProfil.class);
			startActivity(intentProfil);
			break;
		case R.id.menu_logout:
			SharedPreferences preferences = getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear();
			editor.commit();
		
			Intent intentLogout = new Intent(this, LoginPage.class);
			startActivity(intentLogout);
			break;
		default:
			break;
		}


		return true;
	} 	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	public void onBtnClicked(View v)
    { 
        if(v.getId() == R.id.spotCreate_hashtag_helpButton)
        {
            Toast.makeText(this, "Il est possible d'insérer plusieurs Hastags, séparés par des virgules", Toast.LENGTH_LONG).show();
        }       
    }

	@Override
	public void notifyCallBack(JSONObject objectJSON) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyEndOfExecution(String result) {
		Intent intent = new Intent(SpotCreate.this, SpotList.class);
		
		startActivity(intent);
		Toast toast = Toast.makeText(getApplicationContext(), "Spot created",  Toast.LENGTH_SHORT);
		toast.show();		
	}
	
	// To handle when an image is selected from the browser
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	        if (requestCode == 1) {
	        // currImageURI is the global variable I’m using to hold the content:
	            Uri currImageURI = data.getData();
//	            System.out.println("Current image Path is ----->" + getRealPathFromURI(currImageURI));
//	            TextView tv_path = (TextView) findViewById(R.id.path);
//	            tv_path.setText(getRealPathFromURI(currImageURI));
	            ImageToString itb = new ImageToString();
	            try {
	               this.stringValueOfImg = itb.execute(getRealPathFromURI(currImageURI)).get();        
		            Log.d("IMG STRING : ", "" + this.stringValueOfImg.length());

	             //A partir d'ici on retransforme la String en image
//	               byte[] imageBytes=Base64.decode(s, 1);
//	               ImageView imgViewer = (ImageView) findViewById(R.id.imageView1);
//	               Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//	               DisplayMetrics dm = new DisplayMetrics();
//	               getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//	               imgViewer.setMinimumHeight(dm.heightPixels);
//	               imgViewer.setMinimumWidth(dm.widthPixels);
//	               imgViewer.setImageBitmap(bm);
	               
	               
	            } catch (InterruptedException e) {
	              e.printStackTrace();
	            } catch (ExecutionException e) {
	              e.printStackTrace();
	            }
	            
	        }
	    }
	}
	 
	
	public String getRealPathFromURI(Uri contentUri) {
	    String [] proj={MediaStore.Images.Media.DATA};
	    android.database.Cursor cursor = managedQuery( contentUri,
	    proj,     // Which columns to return
	    null,     // WHERE clause; which rows to return (all rows)
	    null,     // WHERE clause selection arguments (none)
	    null);     // Order-by clause (ascending by name)
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}	

}
