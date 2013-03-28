package ch.heig.comem.spotplace;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ch.heig.comem.spotplace.servicesRest.HttpMode;
import ch.heig.comem.spotplace.servicesRest.RequestRest;
import ch.heig.comem.spotplace.utilities.ExtendedSimpleAdapter;

public class SpotList extends Activity implements ObserverREST {
	public static final String ID = "ch.heig.comem.spotplace.ID";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spot_list);

		Intent intent = getIntent();
		SharedPreferences preferences = getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
		int idUser = preferences.getInt(LoginPage.ID_USER, 0);

		new RequestRest(SpotList.this, "spots", HttpMode.GET).execute();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			  Intent intent = new Intent(this, SpotList.class);
			  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			  startActivity(intent);
			  break; 
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
	public void notifyCallBack(JSONObject objectJSON) {
		//Création de la ArrayList qui nous permettra de remplire la listView
		ArrayList<HashMap<String, Object>> listSpotItem = new ArrayList<HashMap<String, Object>>();
		try {
			for(int i = 0; i< objectJSON.getJSONArray("spotDto").length(); i++) {

				JSONObject mySPot = objectJSON.getJSONArray("spotDto").getJSONObject(i);
				//On déclare la HashMap qui contiendra les informations pour un item
				HashMap<String, Object> mapSpot = new HashMap<String, Object>();
				mapSpot.put("id", mySPot.getString("id"));
				mapSpot.put("title", mySPot.getString("title"));

				byte[] imageBytes=Base64.decode(mySPot.getString("urlPhoto"), 1);
				Bitmap imgSpot = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

				mapSpot.put("urlPhoto", imgSpot);

				mapSpot.put("rating", mySPot.getString("rating"));
				listSpotItem.add(mapSpot);


			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
		SimpleAdapter adapterSpot = new ExtendedSimpleAdapter(this, listSpotItem, R.layout.spot_item,
				new String[] {"id",  "title", "rating", "urlPhoto"}, new int[] {R.id.id_spot, R.id.titleSpot, R.id.rankingSpot, R.id.img_spot});



		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listSpotTitle);
		ListView listView = (ListView) findViewById(R.id.listSpotLayout);
		listView.setAdapter(adapterSpot);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				//on récupère la HashMap contenant les infos de notre item (titre, description, img)
				@SuppressWarnings("unchecked")
				HashMap<String, String> mypSpot = (HashMap<String, String>) arg0.getItemAtPosition(position);
				int idSpot  =Integer.parseInt(mypSpot.get("id"));
				Intent intent = new Intent(SpotList.this, SpotDetail.class);
				intent.putExtra(ID, idSpot);
				startActivity(intent);
			}

		});

	}


	@Override
	public void notifyEndOfExecution(String result) {
		// TODO Auto-generated method stub

	}

}
