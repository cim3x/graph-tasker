package com.firkinofbrain.graphtask.database;

public class ItemTable {
	public static final String TABLE_NAME = "items";
	
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String DESC = "description";
	public static final String DEADLINE = "deadline";
	public static final String PROGNOTE = "progress_note";
	public static final String PROGRESS = "progress";
	public static final String LEVEL = "level";
	public static final String POSITIONX = "positionx";
	public static final String POSITIONY = "positiony";
	public static final String DONE = "done";
	public static final String STYLE = "styleid";
	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TITLE + " TEXT, "
			+ DESC + " TEXT, "
			+ DEADLINE + " INTEGER(13), "
			+ PROGNOTE + " TEXT, "
			+ PROGRESS + " INTEGER, "
			+ LEVEL + " INTEGER, "
			+ POSITIONX + " REAL,"
			+ POSITIONY + " REAL,"
			+ DONE + " INTEGER, "
			+ STYLE + " INTEGER)";
}	
