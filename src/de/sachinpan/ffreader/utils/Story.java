package de.sachinpan.ffreader.utils;

public class Story {
	private String title,author,link,description,numberOfChapters;
	
	public Story(String title, String author, String description, String numberOfChapters, String link){
		this.title = title;
		this.author = author;
		this.link = link;
		this.description = description;
		this.numberOfChapters = numberOfChapters;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getNumberOfChapters(){
		return numberOfChapters;
	}
	
	public String getLink(){
		return link;
	}

	public String getAuthor() {
		return author;
	}

	public String getDescription() {
		return description;
	}

}
