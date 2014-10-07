package de.sachinpan.ffreader.utils;

public class Item {
	private String title, link, strNum;
	private double numberOfStories;

	public Item(String title, String numberOfStories, String link) {
		this.title = title;
		this.strNum = numberOfStories;
		String num = numberOfStories.replace(",", "");
		this.numberOfStories = (num.charAt(num.length() - 1) == 'K') ? Double
				.parseDouble(num.substring(0, num.length() - 1)) * 1000
				: Double.parseDouble(num);
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public double getNumberOfStories() {
		return numberOfStories;
	}

	public String getNumberOfStoriesStr() {
		return strNum;
	}

	public String getLink() {
		return link;
	}

	public String toString() {
		return title;
	}
}
