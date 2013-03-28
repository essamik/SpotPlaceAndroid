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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import ch.heig.comem.spotplace.servicesRest.HttpMode;
import ch.heig.comem.spotplace.servicesRest.RequestRest;
import ch.heig.comem.spotplace.utilities.ServerUrl;

public class UserProfil extends Activity implements ObserverREST{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profil);

		SharedPreferences preferences = getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
		int idUser = preferences.getInt(LoginPage.ID_USER, 0);

		String requete = "users/" + idUser;
		Log.d("Requête : ", requete);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 

		new RequestRest(UserProfil.this, requete, HttpMode.GET).execute();
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
	public void notifyCallBack(JSONObject objectUser) {
		try {

			//IF IT'S A USER 
			if(objectUser.has("playerURL")) {
				//TITRE
				TextView labelUserName = (TextView) findViewById(R.id.userProfile_username_affichage);
				labelUserName.setText(objectUser.getString("userName"));

				//EMAIL
				TextView labelEmail = (TextView) findViewById(R.id.userProfile_email_affichage);
				labelEmail.setText(objectUser.getString("email"));

				//CANTON
				TextView labelCanton = (TextView) findViewById(R.id.userProfile_canton_affichage);
				labelCanton.setText(objectUser.getString("canton"));


				//Get elements from the engine
				String urlPlayer = objectUser.getString("playerURL");
				int indexID = urlPlayer.lastIndexOf("/")+1;
				String idPlayer = "";
				
				while(indexID < urlPlayer.length()) {
					idPlayer+=urlPlayer.charAt(indexID);
					indexID++;
				}
				ServerUrl url = new ServerUrl();
				new RequestRest(UserProfil.this, "http://"+ url.serverUrl+":"+url.serverPort+"/SpotPlaceMoteur/webresources/players/" + idPlayer, HttpMode.GET).execute();

			} else {
				//IF IT'S A PLAYER
				TextView labelReputation = (TextView) findViewById(R.id.userProfile_reputation_affichage);
				labelReputation.setText(objectUser.getString("score"));

				//BADGES
				if(objectUser.has("badges")) {

					List<HashMap<String, Object>> listBadges = new ArrayList<HashMap<String, Object>>();

					Object elementBadge = objectUser.get("badges");
					if (elementBadge instanceof JSONObject) {
						////IF OBJECT//////////////////////////////////////////////////
						JSONObject objectBadge = objectUser.getJSONObject("badges");
						HashMap<String, Object> badge = new HashMap<String, Object>();
						badge.put("name", objectBadge.getString("name"));
						badge.put("description", objectBadge.getString("description"));

						String iconBadge = objectBadge.getString("icon");
						badge.put("icon", this.getBadgeByIcon(iconBadge));
						listBadges.add(badge);
					}
					else if(elementBadge instanceof JSONArray)  {
						////////IF ARRAY//////////////////////////////////////////////////////
						JSONArray arrayBadges = objectUser.getJSONArray("badges");

						for(int i = 0; i < arrayBadges.length(); i++) {
							JSONObject myBadge = arrayBadges.getJSONObject(i);
							//On déclare la HashMap qui contiendra les informations pour un item
							HashMap<String, Object> badgeMap = new HashMap<String, Object>();
							badgeMap.put("name", myBadge.getString("name"));
							badgeMap.put("description", myBadge.getString("description"));
							String iconBadge = myBadge.getString("icon");
							badgeMap.put("icon", this.getBadgeByIcon(iconBadge));
							listBadges.add(badgeMap);
						}
					}	

					//Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
					SimpleAdapter adapterBadges = new SimpleAdapter(this, listBadges, R.layout.badge_item,
							new String[] {"name", "description", "icon"},
							new int[] {R.id.titleBadge, R.id.descriptionBadge, R.id.img_badge});		 


					ListView listView = (ListView) findViewById(R.id.listBadgesUser);
					listView.setAdapter(adapterBadges);
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


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

	@Override
	public void notifyEndOfExecution(String result) {
		// TODO Auto-generated method stub

	}

}
