package com.firkinofbrain.graphtask.database;

public class BindTable {
	public static final String TABLE_NAME = "bounds";
	
	public static final String ID = "id";
	public static final String HIGH_ITEM = "high_item";
	public static final String LOW_ITEM = "low_item";
	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ HIGH_ITEM + " INTEGER, "
			+ LOW_ITEM + "INTEGER)";
}
