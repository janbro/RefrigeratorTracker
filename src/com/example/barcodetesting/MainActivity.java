package com.example.barcodetesting;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.core4j.Enumerable;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OQueryRequest;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	/**
	 * this Activity
	 */
	final Activity thisActivity = this;
	
	/**
	 * context
	 */
	final Context context = this;
	
	/**
	 * API INFO
	 */
	private final static String apiKEY = "2869168b8e9d7cd1f5a2bb2d0f776216";
	
	/**
	 * URLs
	 */

	/**
	 * Item List View
	 */
	ListView lv;
	
	/**
	 * Item adapter
	 */
	FoodItemAdapter adapter;
	
	/**
	 * listview arrrays
	 */
	static ArrayList<Food> foodItems = new ArrayList<Food>();
	static ArrayList<Food> shoppingList = new ArrayList<Food>();
	static ArrayList<Food> recipes = new ArrayList<Food>();
	
	//Will be stored in txt file later
	private String[] fruit = {"apple","apricot","banana","blackberry","blueberry","cherry","cranberry","currant","fig","grape","grapefruit","grapes","kiwi","kumquat","lemon","lime","melon","nectarine","orange","peach","pear","persimmon","pineapple","plum","pomegranate","prune","raspberry","strawberry","tangerine","watermelon"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

		//Testing purposes
//		foodItems.add("banana");
//		foodItems.add("Milk");
		//shoppingList.add(new Food("butter", new GregorianCalendar()));
		
		//Set 
		adapter = new FoodItemAdapter(this,foodItems);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
	}

	@Override 
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		Log.d("SWITCHVIEW", ""+number);
		Log.d("ADDRESSSWITCH", ""+MainActivity.shoppingList.size());
		switch (number) {
		case 1:
			mTitle = getString(R.string.refrigerator);
			adapter = new FoodItemAdapter(this,foodItems);
			break;
		case 2:
			mTitle = getString(R.string.shopping_list);
			adapter = new FoodItemAdapter(this,shoppingList);
			break;
		case 3:
			mTitle = getString(R.string.recipes);
			adapter = new FoodItemAdapter(this,recipes);
			break;
		}
        lv = (ListView) findViewById(R.id.listView);
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 
	 * @author Alejandro
	 * Gets food information from outpan (food name, seller, etc)
	 */
	class RequestTask extends AsyncTask<String, String, Food>{

	    protected Food doInBackground(String... upc){
    		Food foodItem = new Food();
    		foodItem.setUPC(upc[0]);
	    	try{
    			//Find the food information
	    		String requestURL = "https://api.outpan.com/v2/products/" + upc[0] + "?apikey=" + apiKEY;
	    		String response = getJSON(requestURL);
	    		Log.d("RESPONSE", response);
	    		JSONObject jsonObject = null;
	            try{
	            	jsonObject = new JSONObject(response);
	            	Log.i(MainActivity.class.getName(), jsonObject.getString("name"));
	            } catch(Exception e){e.printStackTrace();}
	            if(jsonObject.getString("name")=="null"){
	            	return foodItem;
	            }
	    		//Log.d("BARCODE", jsonObject.toString());
	    		foodItem.setName(jsonObject.getString("name"));
    		} catch (JSONException e) {
    			Log.e("ERROR", "JSONException");
    			return null;
    		}
	    	ODataConsumer c = ODataConsumers
			    .newBuilder("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/")
			    .setClientBehaviors(OClientBehaviors.basicAuth("accountKey", "yR+8Nc0P8cY5Mo7VcE/B8Ss0H+Xzx987VTGKynfEZWw"))
			    .build();

			OQueryRequest<OEntity> oRequest = c.getEntities("Web")
			    .custom("Query", "'"+foodItem.getName()+" expiration date \"eat by date\"'");

			Enumerable<OEntity> entities = oRequest.execute();
			String eatByDateURL="";
			if(entities!=null&&entities.count()>0){
				 eatByDateURL = entities.first().getProperty("Url").getValue().toString();
			}

	    	if(!eatByDateURL.isEmpty()&&eatByDateURL.contains("eatbydate.com")){
			    Document doc = null;
				try {
					doc = Jsoup.connect(eatByDateURL).get();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Element elem = doc.getElementById("unopened");
			    Pattern pattern = Pattern.compile(">.*(Hour|Hours|Week|Weeks|Month|Months|Day|Days|Year|Years)<");
			    Matcher matcher = pattern.matcher(elem.toString());
			    String expiryDate = "";
			    if (matcher.find())
			    {
			        expiryDate = matcher.group(0).replaceAll("(>|<)", "");
			    }
			    //Parse eat by date expiration date
			    foodItem.setExpiryDate(parseExpiryDate(expiryDate));
	    	}
		    
		    return foodItem;
    	}
	    
	    /**
		 * Runs after food name is retrieved
		 * @param result
		 */
		 @Override
		 protected void onPostExecute(Food result) {
			 super.onPostExecute(result);
		     //Do anything with response..
//			 ListView myListView = (ListView) rootView.findViewById(R.id.myListView);
//			 ArrayList<String> myStringArray1 = new ArrayList<String>();
//			 myStringArray1.add("something");
//			 adapter = new CustomAdapter(getActivity(), R.layout.row, myStringArray1);
//			 myListView.setAdapter(adapter);
			 if(result.getName()!=null){
				 foodItems.add(result);
				 adapter.add(foodItems);
				 //adapter.add(result);
				 
				 Log.d("DEBUG", result.getUPC() + " : " + result.getName());
				 Toast toast = Toast.makeText(context, result.getUPC() + " : " + result.getName(), Toast.LENGTH_LONG);
				 toast.show();
			 }
			 else{
				 Toast toast = Toast.makeText(context, "Food Item not Found!", Toast.LENGTH_LONG);
				 toast.show();
			 }
		 }
	}
	
	/**
	 * Run after the barcode is scanned
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		switch(requestCode){
		case IntentIntegrator.REQUEST_CODE:
			if(resultCode==Activity.RESULT_OK){
				IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
				String isbn=result.getContents();
				new RequestTask().execute(isbn);
			}
		case 0:
		    if (resultCode == RESULT_OK) {
		    	if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
		    		//Send image to Clarifai
//		    		File path = new File(getFilesDir(), "tmp/");
//	                if (!path.exists()) path.mkdirs();
//	                File imageFile = new File(path, "image.jpg");
		    		Bitmap bmp = (Bitmap) intent.getExtras().get("data");
		    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		    		byte[] byteArray = stream.toByteArray();
		            if(byteArray!=null){
		            	new RetrieveFeedTask().execute(byteArray);
		            }
		        }
		    }
		}
	}
	
	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
		// Add the buttons
		builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	   System.exit(0);
		           }
		       });
		builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		           }
		       });
		// Set other dialog properties
		builder.setMessage("Are you sure you want to exit?");

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
	
	public void initiateScan(){
		Toast toast = Toast.makeText(context, "Scan clicked!", Toast.LENGTH_SHORT);
		toast.show();
		new IntentIntegrator(this).initiateScan();
	}

	/**
	 * Initiate camera picture to upload to Clarifai
	 */
	public void initiateCameraScan() {
		// TODO Auto-generated method stub
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//		 File path = new File(this.getFilesDir(), "tmp/");
//		    if (!path.exists()) path.mkdirs();
//		    File image = new File(path, "image.jpg");
//		    Uri imageUri = FileProvider.getUriForFile(this, "com.example.barcodetesting.fileprovider", image);
//
//			List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//			for (ResolveInfo resolveInfo : resInfoList) {
//			    String packageName = resolveInfo.activityInfo.packageName;
//			    context.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//			}
//		intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
		startActivityForResult(intent, 0);
	}
	
	/**
	 * Gets url json response
	 * @param address
	 * @return
	 */
	public String getJSON(String address){
    	StringBuilder builder = new StringBuilder();
    	HttpClient client = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(address);
    	try{
    		HttpResponse response = client.execute(httpGet);
    		StatusLine statusLine = response.getStatusLine();
    		int statusCode = statusLine.getStatusCode();
    		if(statusCode == 200){
    			HttpEntity entity = response.getEntity();
    			InputStream content = entity.getContent();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    			String line;
    			while((line = reader.readLine()) != null){
    				builder.append(line);
    			}
    		} else {
    			Log.e(MainActivity.class.toString(),"Failedet JSON object");
    		}
    	}catch(ClientProtocolException e){
    		e.printStackTrace();
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    	return builder.toString();
    }
	

	
	static Calendar parseExpiryDate(String expiryDate){
		if(expiryDate==null||expiryDate.length()==0)
			return null;
		String[] timeAndUnit = expiryDate.split(" ");
		if(timeAndUnit[0].contains("-")){
			String[] toFrom = timeAndUnit[0].split("-");
			timeAndUnit[0] = ""+(Integer.parseInt(toFrom[0])+Integer.parseInt(toFrom[1]))/2;
		}
		Calendar date = new GregorianCalendar();
		System.out.println(date.getTime());
		Log.d("TIME",timeAndUnit[0]+" : "+timeAndUnit[1]);
		date.add(dateLookUp(timeAndUnit[1]), Integer.parseInt(timeAndUnit[0]));
		return date;
	}
	
	static int dateLookUp(String raw){
		HashMap <String,Integer>hs = new HashMap<String,Integer>();
		hs.put("Day", Calendar.DAY_OF_YEAR);
		hs.put("Days", Calendar.DAY_OF_YEAR);
		hs.put("Month", Calendar.MONTH);
		hs.put("Months", Calendar.MONTH);
		hs.put("Year", Calendar.YEAR);
		hs.put("Years", Calendar.YEAR);
		hs.put("Week", Calendar.WEEK_OF_YEAR);
		hs.put("Weeks", Calendar.WEEK_OF_YEAR);
		hs.put("Hour", Calendar.HOUR_OF_DAY);
		hs.put("Hours", Calendar.HOUR_OF_DAY);
		
		if(hs.containsKey(raw)){
			return hs.get(raw);
		}
		return Calendar.DAY_OF_YEAR;
	}

	public static void addItemToShopping(Food food) {
		// TODO Auto-generated method stub
		Log.d("BEFORE", ""+shoppingList.size());
		shoppingList.add(food);
		Log.d("ADDRESS", ""+MainActivity.shoppingList.size());
		
	}
	
	public static void removeItemFromShopping(Food food) {
		// TODO Auto-generated method stub
		for(int i = 0; i<shoppingList.size();i++){
			if(food.getUPC()!=null){
				if(food.getUPC().equals(shoppingList.get(i).getUPC())){
					shoppingList.remove(i);		
				}
			}
			else{
				if(food.getName().equals(shoppingList.get(i).getName())){
					shoppingList.remove(i);		
				}
			}
		}
	}
	
	class RetrieveFeedTask extends AsyncTask<byte[], Void, Food > {

	    private Exception exception;

	    protected Food doInBackground(byte[]... urls) {
	    	ClarifaiClient clarifai = new ClarifaiClient("IxJsLTICX4Zca5_gjwTZcJ5ST0tvZyO8sQ5H8TGx", "06MDBL42eViqAMWIgcbk-v8rNR1H6qapZfVn52TZ");
    		List<RecognitionResult> results = clarifai.recognize(new RecognitionRequest(urls));
    		List<String> fruits = Arrays.asList(fruit);
    		Food foodItem = new Food();
  		  	Log.d("TAG",""+results.get(0).getStatusMessage());
    		for (Tag tag : results.get(0).getTags()) {
    		  Log.d("TAG",tag.getName() + ": " + tag.getProbability());
    		  if(fruits.contains(tag.getName())){
    			  foodItem.setName(tag.getName());
    			  break;
    		  }
    		}//Log.d("NAME",foodItem.getName());
    		if(foodItem.getName()==null||foodItem.getName()==""){
    			return null;
    		}
    		ODataConsumer c = ODataConsumers
    			    .newBuilder("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/")
    			    .setClientBehaviors(OClientBehaviors.basicAuth("accountKey", "yR+8Nc0P8cY5Mo7VcE/B8Ss0H+Xzx987VTGKynfEZWw"))
    			    .build();

			OQueryRequest<OEntity> oRequest = c.getEntities("Web")
			    .custom("Query", "'"+foodItem.getName()+" expiration date \"eat by date\"'");

			Enumerable<OEntity> entities = oRequest.execute();
			String eatByDateURL="";
			if(entities!=null&&entities.count()>0){
				 eatByDateURL = entities.first().getProperty("Url").getValue().toString();
			}

	    	if(!eatByDateURL.isEmpty()&&eatByDateURL.contains("eatbydate.com")){
			    Document doc = null;
				try {
					doc = Jsoup.connect(eatByDateURL).get();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Element elem = doc.getElementById("unopened");
			    Log.d("DEBUG", doc.toString());
			    Pattern pattern = Pattern.compile(">.*(Hour|Hours|Week|Weeks|Month|Months|Day|Days|Year|Years)<");
			    if(elem==null)
			    	return null;
			    Matcher matcher = pattern.matcher(elem.toString());
			    String expiryDate = "";
			    if (matcher.find())
			    {
			        expiryDate = matcher.group(0).replaceAll("(>|<)", "");
			    }
			    //Parse eat by date expiration date
			    foodItem.setExpiryDate(parseExpiryDate(expiryDate));
	    	}
			return foodItem;
	    }

	    protected void onPostExecute(Food foodItem) {
	        // TODO: check this.exception 
	        // TODO: do something with the feed
	    	if(foodItem!=null&&(foodItem.getName()!=null||foodItem.getName()!="")){
				 foodItems.add(foodItem);
				 adapter.add(foodItems);
				 //adapter.add(result);
				 
				 Log.d("DEBUG", foodItem.getUPC() + " : " + foodItem.getName());
				 Toast toast = Toast.makeText(context, foodItem.getUPC() + " : " + foodItem.getName(), Toast.LENGTH_LONG);
				 toast.show();
			 }
			 else{
				 Toast toast = Toast.makeText(context, "Food Item not Found!", Toast.LENGTH_LONG);
				 toast.show();
			 }
	    }
	}
	
}
