package com.firkinofbrain.graphtask.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.firkinofbrain.graphtask.Node;

public class DatabaseManager {

	public static final String DRIVER = "org.sqlite.JDBC";
	public static final String DB_URL = "jdbc:sqlite:repository.db";

	private Connection connection;
	private Statement statement;

	public DatabaseManager() {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			connection = DriverManager.getConnection(DB_URL);
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		createTables();
	}

	private void createTables() {
		try {
			statement.execute(ItemTable.CREATE_TABLE);
			statement.execute(BindTable.CREATE_TABLE);

			boolean EXIST = false;
			DatabaseMetaData meta = connection.getMetaData();
			ResultSet res = meta.getTables(null, null, null,
					new String[] { "TABLE" });
			while (res.next()) {
				if (res.getString("TABLE_NAME").toString().equals(StyleTable.TABLE_NAME))
					EXIST = true;
			}
			
			if (!EXIST) {
				statement.execute(StyleTable.CREATE_TABLE);

				statement.executeUpdate(StyleTable.INSERT_ADD_STYLE);
				statement.executeUpdate(StyleTable.INSERT_PROJECT_STYLE);
				statement.executeUpdate(StyleTable.INSERT_SUBPROJECT_STYLE);
				statement.executeUpdate(StyleTable.INSERT_TASK_STYLE);
				statement.executeUpdate(StyleTable.INSERT_COWORKER_STYLE);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		try {
			statement.execute("DROP TABLE IF EXISTS " + ItemTable.TABLE_NAME);
			statement.execute("DROP TABLE IF EXISTS " + BindTable.TABLE_NAME);
			
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ITEM
	 */

	public long insertItem(Item item) {

		long id = 0L;

		try {
			PreparedStatement ps = connection
					.prepareStatement("INSERT INTO " + ItemTable.TABLE_NAME
							+ " VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, item.getTitle());
			ps.setString(2, item.getDescription());
			ps.setLong(3, item.getDeadline());
			ps.setString(4, item.getProgress());
			ps.setInt(5, item.getiProgress());
			ps.setInt(6, item.getLevel());
			ps.setFloat(7, item.getStartX());
			ps.setFloat(8, item.getStartY());
			ps.setInt(9, item.getDone());
			ps.setInt(10, item.getStyle());

			ps.execute();
			id = ps.getGeneratedKeys().getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
	}

	public boolean updateItem(Item item) {

		try {
			PreparedStatement ps = connection.prepareStatement("UPDATE "
					+ ItemTable.TABLE_NAME + " SET "
					+ ItemTable.TITLE + " = ?, "
					+ ItemTable.DESC + " = ?, "
					+ ItemTable.DEADLINE + " = ?, "
					+ ItemTable.PROGNOTE + " = ?, "
					+ ItemTable.PROGRESS + " = ?, "
					+ ItemTable.LEVEL + " = ?, "
					+ ItemTable.POSITIONX + " = ?, "
					+ ItemTable.POSITIONY + " = ?, "
					+ ItemTable.DONE + " = ?, "
					+ ItemTable.STYLE + " = ? "
					+ "WHERE " + StyleTable.ID + " = ?");
			ps.setString(1, item.getTitle());
			ps.setString(2, item.getDescription());
			ps.setLong(3, item.getDeadline());
			ps.setString(4, item.getProgress());
			ps.setInt(5, item.getiProgress());
			ps.setInt(6, item.getLevel());
			ps.setFloat(7, item.getX());
			ps.setFloat(8, item.getY());
			ps.setInt(9, item.getDone());
			ps.setInt(10, item.getStyle());

			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteItem(long id) {
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM "
					+ ItemTable.TABLE_NAME + " WHERE " + ItemTable.ID + " = ?");
			ps.setLong(1, id);

			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Map<Long, Item> selectAllItems() {
		Map<Long, Item> items = new HashMap<Long, Item>();

		try {
			ResultSet result = statement.executeQuery("SELECT * FROM "
					+ ItemTable.TABLE_NAME);
			while (result.next()) {
				Item item = new Item();
				item.setId(result.getLong(1));
				item.setTitle(result.getString(2));
				item.setDescription(result.getString(3));
				item.setProgress(result.getString(4));
				item.setLevel(result.getInt(5));
				item.setX(result.getFloat(6));
				item.setY(result.getFloat(7));
				item.setDone(result.getInt(8));
				item.setStyle(result.getInt(9));

				items.put(result.getLong(1), item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return items;
	}
	
	/*
	 * BIND
	 */

	public boolean insertBind(Bind bind) {

		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO "
					+ BindTable.TABLE_NAME + " VALUES (NULL, ?, ?)");
			ps.setLong(1, bind.getHighItemId());
			ps.setLong(2, bind.getLowItemId());

			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean deleteBind(long parent, long child) {
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM "
					+ BindTable.TABLE_NAME + " WHERE " + BindTable.HIGH_ITEM + " = ? AND " + BindTable.LOW_ITEM + " = ?");
			ps.setLong(1, parent);
			ps.setLong(2, child);

			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public boolean deleteAllBinds(long parent){
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM "
					+ BindTable.TABLE_NAME + " WHERE " + BindTable.HIGH_ITEM + " = ?");
			ps.setLong(1, parent);

			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Map<Long, ArrayList<Long>> selectAllBinds() {
		Map<Long, ArrayList<Long>> binds = new HashMap<Long, ArrayList<Long>>();
		
		try {
			ResultSet result = statement.executeQuery("SELECT * FROM "
					+ BindTable.TABLE_NAME);
			while (result.next()) {
				/*
				 * ArrayList Bind bind = new Bind();
				 * bind.setId(result.getLong(1));
				 * bind.setHighItemId(result.getLong(2));
				 * bind.setLowItemId(result.getLong(3));
				 * 
				 * binds.add(bind);
				 */

				/* HASHMAP */
				Bind.addBind(binds, result.getLong(2), result.getLong(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return binds;
	}
	
	/*
	 * Convert to node list
	 */
	
	public List<Node> getAllNodes(){
		
		List<Node> nodes = new ArrayList<Node>();
		
		Map<Long, Item> items = selectAllItems();
		Map<Long, ArrayList<Long>> binds = selectAllBinds();
		
		Map<Long, Node> nodesMap = new HashMap<Long, Node>();
		for(Entry<Long, Item> entry: items.entrySet()){
			nodesMap.put(entry.getKey(), new Node(entry.getValue()));
		}
		
		for(Entry<Long, Node> entry: nodesMap.entrySet()){
			
			ArrayList<Long> childrenIds = binds.get(entry.getKey());
			Node node = entry.getValue();
			List<Node> children = new ArrayList<Node>();
			for(int i=0;i<childrenIds.size();i++){
				Node child = nodesMap.get(childrenIds.get(i));
				children.add(child);
				child.setParent(node);
			}
			node.setChildren(children);
			nodes.add(node);
		}
		
		return nodes;
	}
	

	/*
	 * STYLE
	 */

	public boolean insertStyle(Style style) {

		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO "
					+ StyleTable.TABLE_NAME
					+ " VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, style.getName());
			ps.setString(2, style.getTextColorString());
			ps.setString(3, style.getBgColorString());
			ps.setString(4, style.getBorderColorString());
			ps.setDouble(5, style.getBorderSize());
			ps.setDouble(6, style.getFontSize());
			ps.setFloat(7, style.getSize());

			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean updateStyle(Style style) {

		try {
			PreparedStatement ps = connection.prepareStatement("UPDATE "
					+ StyleTable.TABLE_NAME + " SET " + StyleTable.NAME
					+ " = ?, " + StyleTable.TEXTCOLOR + " = ?, "
					+ StyleTable.BGCOLOR + " = ?, " + StyleTable.BORDERCOLOR
					+ " = ?, " + StyleTable.BORDERSIZE + " = ?, "
					+ StyleTable.FONTSIZE + " = ?, " + StyleTable.SIZE
					+ " = ? " + "WHERE " + StyleTable.ID + " = ?");
			ps.setString(0, style.getName());
			ps.setString(1, style.getTextColorString());
			ps.setString(2, style.getBgColorString());
			ps.setString(3, style.getBorderColorString());
			ps.setDouble(4, style.getBorderSize());
			ps.setDouble(5, style.getFontSize());
			ps.setFloat(6, style.getSize());
			ps.setLong(7, style.getId());

			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public List<Style> selectAllStyles() {
		List<Style> styles = new ArrayList<Style>();
		try {
			ResultSet result = statement.executeQuery("SELECT * FROM "
					+ StyleTable.TABLE_NAME);
			while (result.next()) {
				Style style = new Style();
				style.setId(result.getLong(1));
				style.setName(result.getString(2));
				style.setTextColor(result.getString(3));
				style.setBgColor(result.getString(4));
				style.setBorderColor(result.getString(5));
				style.setBorderSize(result.getDouble(6));
				style.setFontSize(result.getDouble(7));
				style.setSize(result.getFloat(8));
				
				styles.add(style);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return styles;
	}

	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
