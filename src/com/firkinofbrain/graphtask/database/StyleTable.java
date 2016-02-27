package com.firkinofbrain.graphtask.database;

public class StyleTable {
	public static final String TABLE_NAME = "styles";
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String TEXTCOLOR = "textcolor";
	public static final String BGCOLOR = "backgroundcolor";
	public static final String BORDERCOLOR = "bordercolor";
	public static final String BORDERSIZE = "bordersize";
	public static final String FONTSIZE = "fontsize";
	public static final String SIZE = "size";
	
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" 
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ NAME + " TEXT, "
			+ TEXTCOLOR + " TEXT, "
			+ BGCOLOR + " TEXT, "
			+ BORDERCOLOR + " TEXT, "
			+ BORDERSIZE + " REAL, "
			+ FONTSIZE + " REAL, "
			+ SIZE + " REAL)";
	
	//TODO insert new styles
	
	public static final String INSERT_ADD_STYLE = "INSERT INTO " + TABLE_NAME + " VALUES ("
			+ "1, 'Adder', '255, 255, 255, 1', '0,93,97, 0.5', '0,83,160,1', 10, 20, 100)";
	
	public static final String INSERT_PROJECT_STYLE = "INSERT INTO " + TABLE_NAME + " VALUES ("
			+ "2, 'Project', '133,137,118,1', '192,235,20,0.5', '63,78,0,1', 6, 18, 75)";
	
	public static final String INSERT_SUBPROJECT_STYLE = "INSERT INTO " + TABLE_NAME + " VALUES ("
			+ "3, 'Subproject', '255,51,0,1', '224,216,202,0.5', '137,27,0,1',5, 10, 55)";
	
	public static final String INSERT_TASK_STYLE = "INSERT INTO " + TABLE_NAME + " VALUES ("
			+ "4, 'Task', '0,12,39,1', '157,187,255,0.5', '0,13,255,1',4, 9, 45)";
	
	public static final String INSERT_COWORKER_STYLE = "INSERT INTO " + TABLE_NAME + " VALUES ("
			+ "5, 'Coworker', '255,255,255,1', '153,81,246,0.5', '6,1,13,1',6, 11, 60)";
 }
