package com.example.testproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	ArrayList<String> items = new ArrayList<String>();
	ArrayList<String> arrItemsSample = new ArrayList<String>();

	static InputStream is = null;

	private static String url = "https://api.github.com/gists/public";

	JSONArray people = null;
	private static final String USER = "user";
	static JSONObject jObj = null;
	static String json = "";
	private static final String USER_ID = "id";
	private static final String USER_NAME = "login";
	String id = "";
	String name = "";
	String val_compare = "";
	int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheListItemListener();
	}

	private void setTheListItemListener() {
		this.getListView().setOnItemLongClickListener(
				new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// Toast.makeText(MainActivity.this, "postion: " +
						// getListView().getSelectedItemPosition(),
						// Toast.LENGTH_SHORT).show();
						Object o = parent.getItemAtPosition(position);
						final String keyword = o.toString();
						Log.v("value ", "result is " + keyword);

						AlertDialog.Builder builder = new AlertDialog.Builder(
								MainActivity.this);
						builder.setCancelable(true);
						builder.setTitle("Delete");
						builder.setInverseBackgroundForced(true);
						builder.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();

										items.remove(keyword);
										setListAdapter(new ArrayAdapter<String>(
												getApplicationContext(),
												R.layout.simple_list_item,
												items));

									}
								});
						builder.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
						return false;
					}
				});

	}

	/*
	 * private void getAlertWindow() { AlertDialog.Builder builder = new
	 * AlertDialog.Builder(this); builder.setCancelable(true);
	 * builder.setTitle("Delete"); builder.setInverseBackgroundForced(true);
	 * builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * dialog.dismiss(); arrItemsSample.remove("Ashish"); setListAdapter(new
	 * ArrayAdapter<String>( getApplicationContext(), R.layout.simple_list_item,
	 * arrItemsSample));
	 * 
	 * } }); builder.setNegativeButton("No", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * dialog.dismiss(); } }); AlertDialog alert = builder.create();
	 * alert.show(); }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add:
			new MyTasks().execute();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class MyTasks extends AsyncTask<URL, Void, JSONObject> {
		ProgressDialog pd = new ProgressDialog(MainActivity.this);

		@Override
		protected JSONObject doInBackground(URL... urls) {

			// return loadJSON(url);
			try {
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				// HttpPost httpPost = new HttpPost(url);
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);

				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}

			// try parse the string to a JSON object

			try {

				JSONArray people = new JSONArray(json);
				// JSONArray people = new JSONArray(json);

				outerloop: for (int i = 0; i < people.length(); i++) {
					// System.out.println(courses.getJSONObject(i).toString());
					JSONObject p = people.getJSONObject(i);
					flag = 0;
					// Storing each json item in variable
					id = p.getString(USER_ID);
					// String name = p.getString(name);

					JSONObject login = p.getJSONObject(USER);
					name = login.getString(USER_NAME);
					// items.add("  ID: " + id + "\t\t\t\t    Name: " + name);

					val_compare = "  ID: " + id + "\t\t\t\t    Name: " + name;

					Log.d("Inside outerloop", "The items are " + items.size());

					if (items.size() > 0) {
						for (String new_id : items) {
							Log.d("Inside for each loop", "New_id: " + new_id);
							Log.d("Inside for each loop", "Value to compare: "
									+ val_compare);

							if ((items.contains(id))
									|| (val_compare.equals(new_id.toString()))) {
								flag = flag + 1;
								Log.d("Flag Value", "Flag Value: " + flag);

							}

						}

					} else {
						items.add("  ID: " + id + "\t\t\t\t    Name: " + name);
						Log.d("Initialize", "First time");

						break outerloop;
					}
					if (flag == 0) {
						items.add("  ID: " + id + "\t\t\t\t    Name: " + name);
						Log.d("Found", "Inserted an item");

						break outerloop;
					}
					/* Log.v("--", "People: \n" + "\n UPI: " + person_id); */
				}
				// jObj = new JSONObject(json);
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}

			// return JSON String
			return jObj;
		}

		protected void onPreExecute() {

			pd.setMessage("Loading Contacts");
			pd.show();
		}

		protected void onPostExecute(JSONObject json) {

			setListAdapter(new ArrayAdapter<String>(getApplicationContext(),
					R.layout.simple_list_item, items));
			pd.hide();
		}
	}
}
