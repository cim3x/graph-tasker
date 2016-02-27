package com.firkinofbrain.graphtask.database;

public class Style {
	private long id;
	private String name;
	private String textColor;
	private String bgColor;
	private String borderColor;
	private double borderSize;
	private double fontSize;
	private float size;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double[] getTextColor() {
		String[] sColor = textColor.split(",");
		double[] color = {
			Double.parseDouble(sColor[0]),
			Double.parseDouble(sColor[1]), 
			Double.parseDouble(sColor[2]),
			Double.parseDouble(sColor[3])
		};
		return color;
	}
	public String getTextColorString(){
		return textColor;
	}
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}
	public double[] getBgColor() {
		String[] sColor = bgColor.split(",");
		double[] color = {
			Double.parseDouble(sColor[0]),
			Double.parseDouble(sColor[1]), 
			Double.parseDouble(sColor[2]),
			Double.parseDouble(sColor[3])
		};
		return color;
	}
	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}
	public String getBgColorString(){
		return bgColor;
	}
	public double[] getBorderColor() {
		String[] sColor = borderColor.split(",");
		double[] color = {
			Double.parseDouble(sColor[0]),
			Double.parseDouble(sColor[1]), 
			Double.parseDouble(sColor[2]),
			Double.parseDouble(sColor[3])
		};
		return color;
	}
	public String getBorderColorString(){
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}
	public double getBorderSize() {
		return borderSize;
	}
	public void setBorderSize(double borderSize) {
		this.borderSize = borderSize;
	}
	public double getFontSize() {
		return fontSize;
	}
	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}
	public float getSize() {
		return size;
	}
	public void setSize(float size) {
		this.size = size;
	}
}
