package ch.heig.comem.spotplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ch.heig.comem.spotplace.servicesRest.HttpMode;
import ch.heig.comem.spotplace.servicesRest.RequestRest;
import ch.heig.comem.spotplace.utilities.ExtendedSimpleAdapter;
import ch.heig.comem.spotplace.utilities.ServerUrl;

public class UserList extends Activity implements ObserverREST{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list);
		ServerUrl url = new ServerUrl();

		new RequestRest(UserList.this, "http://"+ url.serverUrl+":"+url.serverPort+"/SpotPlaceMoteur/webresources/players/", HttpMode.GET).execute();		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
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
	public void notifyCallBack(JSONObject playersJSON) {
		//Création de la ArrayList qui nous permettra de remplire la listView
				ArrayList<HashMap<String, Object>> listPlayersItem = new ArrayList<HashMap<String, Object>>();
				try {
					for(int i = 0; i< playersJSON.getJSONArray("playerDto").length(); i++) {

						JSONObject myPlayer = playersJSON.getJSONArray("playerDto").getJSONObject(i);
						//On déclare la HashMap qui contiendra les informations pour un item
						HashMap<String, Object> mapPlayer = new HashMap<String, Object>();
						mapPlayer.put("userName", myPlayer.getString("userName"));
						mapPlayer.put("score", myPlayer.getString("score"));

						listPlayersItem.add(mapPlayer);


					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
				SimpleAdapter adapterPlayer = new ExtendedSimpleAdapter(this, listPlayersItem, R.layout.user_item,
						new String[] {"userName", "score"}, new int[] {R.id.userList_userName, R.id.userList_reputation});


				ListView listView = (ListView) findViewById(R.id.listUsersLayout);
				listView.setAdapter(adapterPlayer);		
	}

	@Override
	public void notifyEndOfExecution(String result) {
		// TODO Auto-generated method stub
		
	}
	
	private int getBadgeByIcon(String iconBadge) {
		int drawableBadge = 0;
		if(iconBadge.equals("badge_comment.png")) 
			drawableBadge = Integer.valueOf(R.drawable.badge_comment);
		else if(iconBadge.equals("badge_like.png"))
			drawableBadge =Integer.valueOf(R.drawable.badge_like);
		else if(iconBadge.equals("badge_spot.png"))
			drawableBadge =Integer.valueOf(R.drawable.badge_spot);
		else if(iconBadge.equals("badge_argent.png"))
			drawableBadge =Integer.valueOf(R.drawable.badge_argent);
		else if(iconBadge.equals("badge_bronze.png"))
			drawableBadge =Integer.valueOf(R.drawable.badge_bronze);
		else if(iconBadge.equals("badge_choc.png"))
			drawableBadge=Integer.valueOf(R.drawable.badge_choc);
		else if(iconBadge.equals("badge_or.png"))
			drawableBadge =Integer.valueOf(R.drawable.badge_or);
		else 
			drawableBadge = Integer.valueOf(R.drawable.badge_default);

		return drawableBadge;
	}

}
