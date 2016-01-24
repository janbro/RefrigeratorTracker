package com.example.barcodetesting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

public class FoodItemAdapter extends BaseAdapter implements ListAdapter { 
	private ArrayList<Food> foods = new ArrayList<Food>();
	private Context context; 
	
	public FoodItemAdapter( Context context, ArrayList<Food> foods) { 
		this.foods = foods;
	    this.context = context;
		notifyDataSetChanged();
	} 
	
	public void add(ArrayList<Food> foods) { 
		this.foods = foods;
		notifyDataSetChanged();
	} 
	
	public void add(Food food) {
		// TODO Auto-generated method stub
		foods.add(food);
		notifyDataSetChanged();
	}
	
	public void add(String foodName, Calendar expiryDate){
		foods.add(new Food(foodName,expiryDate));
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() { 
	    return foods.size(); 
	} 
	
	@Override
	public Object getItem(int pos) { 
	    return foods.get(pos); 
	} 
	
	@Override
	public long getItemId(int pos) { 
	    return 0;
	    //just return 0 if your list items do not have an Id variable.
	} 
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	    View view = convertView;
	    if (view == null) {
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	        view = inflater.inflate(R.layout.food_bar, null);
	    }
	    float[] g = {0,0.0471f,1.0000f};
    	view.setBackgroundColor(Color.TRANSPARENT);
    	
	    if(foods.get(position).getExpiryDate()!=null){
		    Calendar nearDate = (Calendar) foods.get(position).getExpiryDate().clone();
		    nearDate.roll(Calendar.DAY_OF_YEAR, 4);
		    
		    if(foods.get(position).getExpiryDate().before(new GregorianCalendar())){
		    	view.setBackgroundColor(Color.RED);
		    }
		    else if(nearDate.after(foods.get(position).getExpiryDate())){
		    	view.setBackgroundColor(Color.YELLOW);
		    }else{
	        	view.setBackgroundColor(Color.TRANSPARENT);
		    }
	    }
	
	    //Handle TextView and display string from your list
	    TextView listItemText = (TextView)view.findViewById(R.id.food_description); 
	    String foodDescription = foods.get(position).getName();
	    if(foods.get(position).getExpiryDate()!=null){
	    	foodDescription+="\n"+foods.get(position).getExpiryDate().getTime();
	    }
	    listItemText.setText(foodDescription); 
	
	    //Handle buttons and add onClickListeners
	    Switch foodInStockSwitch = (Switch)view.findViewById(R.id.in_stock_switch);
	    
	    foodInStockSwitch.setChecked(foods.get(position).getStockState());
	    
	    ImageButton deleteButton = (ImageButton)view.findViewById(R.id.imageButton1);
	    
	    deleteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				// Add the buttons
				builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               // User clicked OK button
				        	   remove(position);
				           }
				       });
				builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               // User cancelled the dialog
				           }
				       });
				// Set other dialog properties
				builder.setMessage("Delete item?");

				// Create the AlertDialog
				AlertDialog dialog = builder.create();
				dialog.show();
			}
	    	
	    });
	    
	    foodInStockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	            // do something, the isChecked will be
	            // true if the switch is in the On position
	        	if(isChecked){
	        		//food is in stock
	        		foods.get(position).setInStock();
	        		MainActivity.removeItemFromShopping(foods.get(position));
	        		notifyDataSetChanged();
	        	}
	        	else{
	        		//food is out of stock, turn bar grey and move to bottom
	        		Log.d("ADD FOOD", foods.get(position).getName());
	        		foods.get(position).setOutOfStock();
	        		MainActivity.addItemToShopping(foods.get(position));
	        		notifyDataSetChanged();
	        	}
	        }
	    });
	
	    return view; 
	}
	
	public void remove(int position){
		MainActivity.removeItemFromShopping(foods.get(position));
		foods.remove(position);
		notifyDataSetChanged();
	}

}