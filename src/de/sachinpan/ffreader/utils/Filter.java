package de.sachinpan.ffreader.utils;

public class Filter {
	private String text;
	private int value;
	
	public Filter(String value, String text){
		this.value = Integer.parseInt(value);
		this.text = text;
	}
	
	public int getValue(){
		return value;
	}
	
	public String getText(){
		return text;
	}
	
	public String toString(){
		return text;
	}

}
