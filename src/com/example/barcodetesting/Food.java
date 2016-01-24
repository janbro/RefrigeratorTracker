package com.example.barcodetesting;

import java.util.Calendar;

public class Food {

	private String UPC;
	private String name;
	private Calendar expiryDate;
	private String imagePath;
	private boolean inStock;
	
	public Food(){
		inStock = true;
	}
	
	public Food(String upc, String name, Calendar expiryDate, String imagePath){
		this.UPC = upc;
		this.name = name;
		this.expiryDate = expiryDate;
		this.imagePath = imagePath;
	}
	
	public Food(String upc, String name, Calendar expiryDate){
		this.UPC = upc;
		this.name = name;
		this.expiryDate = expiryDate;
		this.imagePath = null;
	}
	
	public Food(String upc, String name){
		this.UPC = upc;
		this.name = name;
		this.expiryDate = null;
		this.imagePath = null;
	}
	
	public Food(String name, Calendar expiryDate){
		this.UPC = null;
		this.name = name;
		this.expiryDate = expiryDate;
		this.imagePath = null;
	}
	
	public String getUPC(){
		return UPC;
	}
	
	public String getName(){
		return name;
	}
	
	public Calendar getExpiryDate(){
		return expiryDate;
	}
	
	public String getImage(){
		return imagePath;
	}
	
	public void setUPC(String upc){
		this.UPC = upc;
	}
	
	public void setName(String foodName){
		this.name = foodName;
	}
	
	public void setExpiryDate(Calendar expiryDate){
		this.expiryDate = expiryDate;
	}
	
	public void setImagePath(String imagePath){
		this.imagePath = imagePath;
	}
	
	public int compareTo(Calendar other){
		if(this.expiryDate.before(other))
			return -1;
		else if(this.expiryDate.after(other))
			return 1;
		return 0;
	}
	
	public void setInStock(){
		inStock = true;
	}
	
	public void setOutOfStock(){
		inStock = false;
	}

	public boolean getStockState() {
		// TODO Auto-generated method stub
		return inStock;
	}
	
}
