package ch.heig.comem.spotplace;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import ch.heig.comem.spotplace.servicesRest.HttpMode;
import ch.heig.comem.spotplace.servicesRest.RequestRest;

public class SpotDetail extends Activity implements ObserverREST {
	private int idSpot;
	public static final String ID_SPOT = "ch.heig.comem.spotplace.ID_SPOT";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spot_detail);
		Intent intent = getIntent();
		this.idSpot = intent.getIntExtra(SpotList.ID, 0);
		String requete = "spots/" + idSpot;
		Log.d("Requête : ", requete);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 

		new RequestRest(SpotDetail.this, requete, HttpMode.GET).execute();
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


	@Override
	public void notifyCallBack(JSONObject objectSpot) {
		ArrayList<HashMap<String, String>> listComments = new ArrayList<HashMap<String, String>>();
		try {

			this.idSpot = objectSpot.getInt("id"); //id in attribut
			//TITRE
			TextView labelTitleSpot = (TextView) findViewById(R.id.titleSpot);
			labelTitleSpot.setText(objectSpot.getString("title"));
			//DESCRIPTION
			TextView labelDescriptionSpot = (TextView) findViewById(R.id.descriptionSpot);
			labelDescriptionSpot.setText(objectSpot.getString("description"));
			//RATING
			TextView labelRatingSpot = (TextView) findViewById(R.id.ratingSpot);
			labelRatingSpot.setText(objectSpot.getString("rating"));
			//By USER
			TextView labelByUser = (TextView) findViewById(R.id.byUser);
			labelByUser.setText("Submitted by " + objectSpot.getJSONObject("user").getString("userName"));
			//IMG
			ImageView pictureSpot = (ImageView) findViewById(R.id.pictureSpot);
			//A partir d'ici on retransforme la String en image
			byte[] imageBytes=Base64.decode(objectSpot.getString("urlPhoto"), 1);

			//             byte[] imageBytes = (byte[]) objectSpot.get("tableau");
			Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

			pictureSpot.setImageBitmap(bm);

			Object elementComment = objectSpot.get("comments");
			if (elementComment instanceof JSONObject) {
				////IF OBJECT//////////////////////////////////////////////////
				JSONObject objectComment = objectSpot.getJSONObject("comments");
				//On déclare la HashMap qui contiendra les informations pour un item
				HashMap<String, String> comment = new HashMap<String, String>();
				//List comment -> comment -> content
				comment.put("content", objectComment.getString("content"));
				//List comment -> comment -> user -> userName
				comment.put("userName", objectComment.getJSONObject("user").getString("userName"));
				listComments.add(comment);
			}
			else if(elementComment instanceof JSONArray)  {
				////////IF ARRAY//////////////////////////////////////////////////////
				JSONArray arrayComment = objectSpot.getJSONArray("comments");
				for(int i = arrayComment.length()-1; i >= 0 ; i--) {
					JSONObject myComment = arrayComment.getJSONObject(i);
					//On déclare la HashMap qui contiendra les informations pour un item
					HashMap<String, String> commentMap = new HashMap<String, String>();
					//List comment -> comment -> content
					commentMap.put("content", myComment.getString("content"));
					//List comment -> comment -> user -> userName
					commentMap.put("userName", myComment.getJSONObject("user").getString("userName"));
					listComments.add(commentMap);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
		SimpleAdapter adapterComments = new SimpleAdapter (this, listComments, R.layout.comment_item,
				new String[] {"content", "userName"}, new int[] {R.id.contentComment, R.id.userName});		 


		ListView listView = (ListView) findViewById(R.id.listComments);
		listView.setAdapter(adapterComments);

		//Action for submit comment
		Button submitComment = (Button) findViewById(R.id.buttonSubmitComment);
		submitComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText inPutComment = (EditText) findViewById(R.id.inputComment);
				String content = inPutComment.getText().toString();

				if(!content.isEmpty()) {
					SharedPreferences preferences = getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
					int idUser = preferences.getInt(LoginPage.ID_USER, 0);
					int idSpot = SpotDetail.this.idSpot;

					//Create new JSON comment
					JSONObject comment = new JSONObject();
					try {
						comment.put("content", content);
						JSONObject user = new JSONObject();
						user.put("id", idUser);
						comment.put("user", user);

						JSONObject spot = new JSONObject();
						spot.put("id", idSpot);
						comment.put("spot", spot);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.d("Mon objet JSON comment : ", comment.toString());

					new RequestRest(SpotDetail.this,"comments", HttpMode.POST, comment).execute();

				}
			}
		});

		//Action for a like
		ImageView likePost = (ImageView) findViewById(R.id.iconLike);
		likePost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Create new JSON event
				JSONObject spotUpdate = new JSONObject();
				try {
					spotUpdate.put("id", SpotDetail.this.idSpot);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				new RequestRest(SpotDetail.this,"spots/",HttpMode.PUT, spotUpdate).execute();

			}
		});

	}

	@Override
	public void notifyEndOfExecution(String result) {

		EditText inputComment = (EditText) findViewById(R.id.inputComment);
		inputComment.setText(null);

		String requete = "spots/" + idSpot;
		new RequestRest(SpotDetail.this, requete, HttpMode.GET).execute();

	}

}
