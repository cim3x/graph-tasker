package com.firkinofbrain.graphtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.firkinofbrain.graphtask.database.Bind;
import com.firkinofbrain.graphtask.database.DatabaseManager;
import com.firkinofbrain.graphtask.database.Item;
import com.firkinofbrain.graphtask.database.Item.ItemLevel;
import com.firkinofbrain.graphtask.database.Point;
import com.firkinofbrain.graphtask.database.Style;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	private Scene scene;
	private Group root;
	private GraphicsContext gc;
	private double cW, cH;
	private double accHeight = 400;

	private ObservableMap<Long, Item> itemsMap;
	private Map<Long, ArrayList<Long>> bindsMap;

	private List<Style> styles;
	private List<Item> moved;
	private Item movable;
	private Item parent;
	private Point translate = new Point(0, 0);

	private long startFrame, endFrame; //lasting of one frame
	private long startHover; //mouse hover item
	private boolean HOVER = false, SHAKE = false;

	private DatabaseManager dbm;

	private VBox hbToolbar;
	private Label itemTitle, itemDesc;

	private ImageView zoomin, move, deleteItem, tick; //toolbar
	private ImageView tree1, tree2, tree3, tree4; // tree items
	private ImageView powerOff, toolbar, projectTree;
	private ImageView editStyle, viewCircle, viewTree, viewDeadline, viewCalendar, viewTodo, viewPrint;


	private ActionFlag action = ActionFlag.FREE;

	private ContextFlag context = ContextFlag.TREE_VIEW_PROJECT;

	@SuppressWarnings("deprecation")
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("GraphTask");
		root = new Group();
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		double width = screenBounds.getWidth();
		double height = screenBounds.getHeight();
		scene = new Scene(root, width, height, Color.WHITE);
		stage.setScene(scene);
		stage.setX(screenBounds.getMinX());
		stage.setY(screenBounds.getMinY());
		stage.setWidth(width);
		stage.setHeight(height);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>(){

			@Override
			public void handle(WindowEvent event) {
				for(Item item : itemsMap.values()){
					dbm.updateItem(item);
				}
			}
			
		});

		// ++++++++
		// DATABASE
		// ++++++++

		dbm = new DatabaseManager();
		dbm.clear(); // TODO delete later

		// +++++
		// ICONS
		// +++++
		
		tree1 = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/tree_1.png")));
		tree2 = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/tree_2.png")));
		tree3 = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/tree_3.png")));
		tree4 = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/tree_4.png")));

		powerOff = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/power.png")));
		toolbar = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/box.png")));
		projectTree = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/navigation.png")));

		editStyle = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/command.png")));
		viewCircle = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/target.png")));
		viewTree = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/optimi.png")));
		viewDeadline = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/deadline.png")));
		viewCalendar = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/calendar.png")));
		viewTodo = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/todo.png")));
		viewPrint = new ImageView(new Image(getClass().getResourceAsStream(
				"../../../drawable/layers.png")));

		// ++++++
		// CANVAS
		// ++++++

		double menuWidth = 200;
		double canvasWidth = width - menuWidth;
		cW = canvasWidth;
		cH = height;
		initializeLists();
		HBox wrap = new HBox();
		Canvas canvas = new Canvas(canvasWidth, height);
		canvas.widthProperty().bind(scene.widthProperty().subtract(menuWidth));
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent mouseEvent) {
						Point mouse = new Point((float) mouseEvent.getX()
								- translate.x, (float) mouseEvent.getY()
								- translate.y);
						resetAction();

						if (action == ActionFlag.FREE) {
							
							boolean ITEM_CLICK = false;
							Iterator<Item> iterator = itemsMap.values().iterator();
							while (iterator.hasNext()) {
								Item item = iterator.next();
								if (containsPoint(item, mouse)) {
									ITEM_CLICK = true;

									if (context == ContextFlag.TREE_VIEW_PROJECT) {
										if (mouseEvent.getButton() == MouseButton.PRIMARY) {
											if(item.getLevel() < 2)
												addNewItem(item, mouse);
											else
												zoomItem(item);
												
										} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
											openToolBar(item, mouse.x, mouse.y);
										}

									}else if(context == ContextFlag.TREE_VIEW_ZOOM){
										if (mouseEvent.getButton() == MouseButton.PRIMARY) {
											addNewItem(item, mouse);
										} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
											openToolBar(item, mouse.x, mouse.y);
										}
									}
								}
							}
							
							if(!ITEM_CLICK){
								if (mouseEvent.getButton() == MouseButton.PRIMARY) {
									//TODO add free item or smth
								} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
									zoomOut(parent);
								}
							}
							
						} else if (action == ActionFlag.MOVING_ITEM) {
							movable = null;
							action = ActionFlag.FREE;
						}

					}

				});
		canvas.addEventHandler(MouseEvent.MOUSE_MOVED,
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent mouseEvent) {
						Point mouse = new Point((float) mouseEvent.getX()
								- translate.x, (float) mouseEvent.getY()
								- translate.y);
						
						boolean ITEM_HOVER = false;
						for (Item item : itemsMap.values()) {
							if (containsPoint(item, mouse)) {
								overItem(item);
								if (item.getLevel() == ItemLevel.ADD.getValue()) {

								}
								
								ITEM_HOVER = true;
								highlightTreeChild(item);
								//XXX
								/*if(action == ActionFlag.FREE) //SHAKE neighbours
									if(!HOVER){
										startHover = System.currentTimeMillis();
										HOVER = true;
									}else{
										if(!SHAKE)
											if(System.currentTimeMillis() - startHover > 1000){
												shakeNeighbours(item);
												SHAKE = true;
											}
									}
								*/
							} else {
								escapeItem(item);
							}
						}
						
						if(!ITEM_HOVER){
							mouseReset();
						}

						if (movable != null) {
							movable.setStartX(mouse.x);
							movable.setStartY(mouse.y);
						}
					}

				});

		gc = canvas.getGraphicsContext2D();
		wrap.getChildren().add(canvas);

		startFrame = System.currentTimeMillis();
		final Duration duration = Duration.millis(1000 / 60);
		@SuppressWarnings("unchecked")
		final KeyFrame frame = new KeyFrame(duration, new EventHandler() {

			@Override
			public void handle(Event event) {
				endFrame = System.currentTimeMillis();
				float delta = (endFrame - startFrame);
				startFrame = System.currentTimeMillis();
				
				update(delta);

				paint();
				
			}

		});

		TimelineBuilder.create()
			.cycleCount(Animation.INDEFINITE)
			.keyFrames(frame)
			.build()
			.play();

		// ++++
		// MENU XXX
		// ++++

		//Accordion menu = new Accordion();
		VBox menu = new VBox();
		menu.setPrefSize(menuWidth, accHeight);

		// MENU PROJECT TREE
		
		final TitledPane menuProjectTree = new TitledPane();
		menuProjectTree.setText("ProjectTree");
		menuProjectTree.setPrefHeight(300);
		menuProjectTree.setMaxHeight(accHeight);

		itemsMap.addListener(new MapChangeListener<Long, Item>() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onChanged(Change change) {
				menuProjectTree.setContent(setTreeView());
			}

		});
		menuProjectTree.setContent(setTreeView());
		menuProjectTree.setExpanded(true);

		// MENU VIEW
		TitledPane menuView = new TitledPane();
		menuView.setText("View");
		menuView.setMaxHeight(accHeight);
		menuView.setPrefHeight(200);
		VBox menuViewContent = new VBox();

		Button treeView = new Button("Tree view");
		treeView.setGraphic(viewTree);
		treeView.setPrefWidth(menuWidth);
		treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				context = ContextFlag.TREE_VIEW_PROJECT;
			}

		});

		Button circleView = new Button("Circle view");
		circleView.setGraphic(viewCircle);
		circleView.setPrefWidth(menuWidth);
		circleView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				context = ContextFlag.CIRCLE_VIEW_PROJECT;
			}

		});

		menuViewContent.getChildren().addAll(treeView, circleView);
		menuView.setContent(menuViewContent);
		menuView.setExpanded(true);

		// Exit button
		TitledPane tpTools = new TitledPane();
		tpTools.setMaxHeight(accHeight);
		tpTools.setPrefHeight(100);
		tpTools.setText("Info");
		VBox vbTools = new VBox();
		itemTitle = new Label();
		itemDesc = new Label();
		vbTools.getChildren().addAll(itemTitle, itemDesc);
		
		tpTools.setContent(vbTools);
		tpTools.setExpanded(true);
		
		//menu.getPanes().addAll(menuProjectTree, menuView, tpTools);
		menu.getChildren().addAll(menuProjectTree, menuView, tpTools);

		wrap.getChildren().add(menu);

		// ++++++++
		// FINALLY
		// ++++++++

		root.getChildren().add(wrap);
		stage.show();
	}

	private void initializeLists() {
		
		//Background gradient colors
		stopColors = new ArrayList<Color>();
		stopColors.add(Color.web("#bfdb9b"));//#96dad1"));
		stopColors.add(Color.web("#aedd9e"));//#99d0dc"));
		stopColors.add(Color.web("#a1dfa4"));//#9cbede"));
		stopColors.add(Color.web("#a5e1ba"));//#9faddf"));
		stopColors.add(Color.web("#a8e3ce"));//#a7a2e1"));
		
		moved = new ArrayList<Item>();
		styles = dbm.selectAllStyles();
		bindsMap = dbm.selectAllBinds();
		itemsMap = FXCollections.observableMap(dbm.selectAllItems());
		if (itemsMap.size() == 0) { // first launch
			Item item = new Item();
			item.setTitle("GraphTask");
			item.setLevel(ItemLevel.ADD.getValue());
			item.setStartX((float) (cW / 2));
			item.setStartY((float) (cH / 2));
			long id = dbm.insertItem(item);
			item.setId(id);
			item.setParent(0);
			itemsMap.put(id, item);
		}
	
		Iterator<Item> it = itemsMap.values().iterator();
		parent = it.next();
	}

	protected void openProjectTree(Item item) {
		gc.save();
		translate.set((float) (cW / 2 - item.getStartX()),
				(float) (cH / 2 - item.getStartY()));
		context = ContextFlag.TREE_VIEW_PROJECT;
		action = ActionFlag.FREE;
		parent = item;
	}

	protected void openToolBar(Item item, float x, float y) {

		hbToolbar = new VBox();

		

		hbToolbar.setTranslateX(x + translate.x);
		hbToolbar.setTranslateY(y + translate.y);
		root.getChildren().add(hbToolbar);
	}
	
	protected void zoomItem(Item item){
		this.parent = item;
		float x = (float) (cW/2 - item.getStartX());
		float y = (float) (cH/2 - item.getStartY());
		this.translate.set(x, y);
		
		context = ContextFlag.TREE_VIEW_ZOOM;
	}
	
	protected void completeTask(Item item) {

		completeTaskDown(item);
		completeTaskUp(item);
	}

	private void completeTaskUp(Item item) {

		long parentId = item.getParent();
		Item parent = itemsMap.get(parentId);
		if (parentId != 0) {
			List<Long> siblings = bindsMap.get(parentId);
			boolean ALL_DONE = true;
			for (int i = 0; ALL_DONE && i < siblings.size(); i++) {
				Item sib = itemsMap.get(siblings.get(i));
				if (sib != null && !sib.isDone())
					ALL_DONE = false;
			}

			if (ALL_DONE) {
				updateItemDone(parent, true);
			} else {
				updateItemDone(parent, false);
			}

			completeTaskUp(parent);
		}
	}

	private void completeTaskDown(Item item) {

		updateItemDone(item, true);
		Long id = item.getId();
		if (bindsMap.containsKey(id)) {
			List<Long> children = bindsMap.get(id);
			for (int i = 0; i < children.size(); i++) {
				Item child = itemsMap.get(children.get(i));
				completeTaskDown(child);
			}
		}

	}

	protected void deleteItem(Item item) {

		Long id = item.getId();
		if (bindsMap.containsKey(id)) {
			List<Long> children = bindsMap.get(id);
			for (int i = 0; i < children.size(); i++) {
				Item child = itemsMap.get(children.get(i));
				deleteItem(child);
			}
			deleteBind(id);
		}
		deleteItem(id);
	}
	
	private void deleteBind(long id){
		bindsMap.remove(id);
		dbm.deleteAllBinds(id);
	}

	private void deleteItem(long id){
		itemsMap.remove(id);
		dbm.deleteItem(id);
	}
	
	private void updateItemDone(Item item, boolean done){
		item.setDone(done);
		dbm.updateItem(item);
	}
	
	protected void zoomOut(Item item) {
		if(item.getLevel() > ItemLevel.PROJECT.getValue()){
			parent = itemsMap.get(item.getParent());
			translate.set((float)(cW/2 - parent.getStartX()), (float) (cH/2 - parent.getStartY()));
		}else{
			context = ContextFlag.TREE_VIEW_PROJECT;
			parent = itemsMap.get(item.getParent());
			translate.set((float)(cW/2 - parent.getStartX()), (float) (cH/2 - parent.getStartY()));
		}
	}

	private float bgChange = 0;
	private void update(float delta) {
		
		// Background change
		if(bgChange > 60000){
			bgChange = 0;
			Collections.shuffle(stopColors);
		}
		bgChange += delta;
		
		//shake item and neighbours
		for(Item item : moved){
			
			if(item.move(delta)){
				moved.remove(item);
				item.setMark(true);
			}
			
		}
	}

	private void paint() {
		drawBackground();
		
		if (context == ContextFlag.TREE_VIEW_PROJECT || context == ContextFlag.TREE_VIEW_ZOOM) {

			deepSearchPaint(parent);

		} else if (context == ContextFlag.CIRCLE_VIEW_PROJECT) {

			deepSearchPaintArcs(parent, 0.0, 360.0, 50.0);
		}

	}
	
	private List<Color> stopColors;
	private void drawBackground(){
		gc.restore();
		
		Stop[] stops = new Stop[]{
			new Stop(0.0, stopColors.get(0)),
			new Stop(1.0, stopColors.get(1)),
		};
		LinearGradient lg = new LinearGradient(1,0,0,1,true,CycleMethod.REFLECT,stops);
		gc.setFill(lg);
		gc.fillRect(0, 0, cW, cH);
		
		gc.save();
		gc.translate(translate.x, translate.y);
		
	}

	private void deepSearchPaint(Item item) {
		if (item != null) {
			drawItem(item);
			
			if((context == ContextFlag.TREE_VIEW_PROJECT && item.getLevel() < 3) 
					|| (context == ContextFlag.TREE_VIEW_ZOOM)){
				Long key = item.getId();
				if (bindsMap.containsKey(key)) {
					ArrayList<Long> children = bindsMap.get(key);
	
					for (int i = 0; i < children.size(); i++) {
						Item child = itemsMap.get(children.get(i));
						if (child != null) {
							deepSearchPaint(child);
							drawLine(item, child);
						}
	
					}
				}
			}
		}
	}

	private void drawLine(Item parent, Item child) {

		Style style = styles.get(parent.getLevel());

		gc.setLineCap(StrokeLineCap.ROUND);
		double[] c = style.getBgColor();
		gc.setStroke(Color.rgb((int) c[0], (int) c[1], (int) c[2], c[3]));
		gc.setLineWidth(style.getBorderSize());

		gc.strokeLine(parent.getX(), parent.getY(),
				child.getX(), child.getY());

	}

	private void drawItem(Item item) {

		Style style = styles.get(item.getStyle());
		float l = style.getSize();
		if(item.isMark()) l *= 1.25;
		float r = l / 2;
		float cx = item.getX() - r; // circle x
		float cy = item.getY() - r; // circle y

		double[] c;

		if (!item.isDone())
			c = style.getBgColor();
		else
			c = style.getBorderColor();

		gc.setFill(Color.rgb((int) c[0], (int) c[1], (int) c[2], c[3]));
		gc.fillOval(cx, cy, l, l);

		c = style.getBorderColor();
		gc.setStroke(Color.rgb((int) c[0], (int) c[1], (int) c[2], c[3]));
		gc.setLineWidth(style.getBorderSize());
		gc.strokeOval(cx, cy, l, l);

		c = style.getTextColor();
		gc.setFill(Color.rgb((int) c[0], (int) c[1], (int) c[2], c[3]));
		gc.setFont(Font.font("TimesNewRoman", style.getFontSize()));
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		gc.fillText(item.getTitle(), item.getX(), item.getY());

	}

	// presettings
	// angle = 360; radius = 0
	private void deepSearchPaintArcs(Item item, double startAngle,
			double angle, double radius) {

		Style style = styles.get(item.getStyle()); // get style of item
		double r = radius + style.getSize(); // make new radius
		Long id = item.getId();
		if (bindsMap.containsKey(id)) {
			List<Long> children = bindsMap.get(id);
			double a = angle / children.size();

			for (int i = 0; i < children.size(); i++) {
				Item child = itemsMap.get(children.get(i));
				deepSearchPaintArcs(child, startAngle + a * i, a, r);
			}
		}

		drawArc(item, startAngle, angle, r);

	}

	// start angle, arc extent, radius
	private void drawArc(Item item, double sa, double a, double r) {

		Style style = styles.get(item.getStyle());
		float cx = (float) (cW - r) / 2;
		float cy = (float) (cH - r) / 2;

		gc.setFill(Color.WHITE);
		gc.fillArc(cx, cy, r, r, sa, a, ArcType.ROUND);
		
		double[] c;

		if (!item.isDone())
			c = style.getBgColor();
		else
			c = style.getBorderColor();

		gc.setFill(Color.rgb((int) c[0], (int) c[1], (int) c[2], c[3]));
		gc.fillArc(cx, cy, r, r, sa, a, ArcType.ROUND);

		c = style.getBorderColor();
		gc.setStroke(Color.rgb((int) c[0], (int) c[1], (int) c[2], c[3]));
		gc.setLineWidth(style.getBorderSize());
		gc.strokeArc(cx, cy, r, r, sa, a, ArcType.ROUND);
	}

	private void addNewItem(Item parent, Point start) {

		action = ActionFlag.MOVING_ITEM;

		int level = parent.getLevel() + 1;
		int styleId = level > 3 ? 3 : level;
		Style style = styles.get(styleId);

		double[] c = style.getBgColor();

		Item nItem = new Item();
		nItem.setTitle(style.getName());
		nItem.setLevel(level);
		nItem.setStyle(styleId);
		nItem.setDone(false);

		nItem.setStartX(start.x);
		nItem.setStartY(start.y);
		long id = dbm.insertItem(nItem);
		nItem.setId(id);
		nItem.setParent(parent.getId());

		// New item where mouse
		movable = nItem;

		addNewBind(parent, id);

		itemsMap.put(id, nItem); // it will notify listener - setTreeView will
									// invoke
		completeTaskUp(nItem); // check for updates in complete
	}

	private void addNewBind(Item parent, long id) {
		Bind bind = new Bind();
		bind.setHighItemId(parent.getId());
		bind.setLowItemId(id);
		if (dbm.insertBind(bind)) {
			Bind.addBind(bindsMap, parent.getId(), id);

		}
	}

	private ArrayList<Long> getNeighboursId(Item item) {

		ArrayList<Long> list = new ArrayList<Long>();
		list.addAll(bindsMap.get(item.getId()));
		long p2Id = itemsMap.get(item.getParent()).getParent();
		if(p2Id != 0)
			list.add(item.getParent());

		return list;
	}
	
	private void shakeNeighbours(Item item){
		ArrayList<Long> neighbours = getNeighboursId(item);
		
		for(Long id : neighbours){
			Item neigh = itemsMap.get(id);
			neigh.setZone(30);
			neigh.setVelocity(Math.random()*10-5,Math.random()*10-5);
			moved.add(neigh);
		}
	}

	private ArrayList<Long> getChildrenId(Item item) {

		ArrayList<Long> list = new ArrayList<Long>();

		Long key = item.getId();
		if (bindsMap.containsKey(key)) {
			ArrayList<Long> children = bindsMap.get(key);

			for (int i = 0; i < children.size(); i++) {
				Item child = itemsMap.get(children.get(i));
				if (child.getId() != item.getParent())
					list.addAll(getChildrenId(child)); // FIXME
			}
		}

		return list;
	}

	private TreeView<ItemCut> setTreeView() {

		Item root = null;

		if (itemsMap.size() > 0) {
			root = itemsMap.get(itemsMap.keySet().toArray()[0]);
		}

		TreeItem<ItemCut> rootItem = deepSearchTree(root);

		TreeView<ItemCut> tree = new TreeView<ItemCut>(rootItem);
		tree.setMaxHeight(accHeight);
		tree.setPrefHeight(accHeight);
		
		tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){

			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				
				for(Item i : itemsMap.values())	i.setMark(false);
				
				ItemCut cut = ((TreeItem<ItemCut>)newValue).getValue();
				Item item = itemsMap.get(cut.getId());
				item.setMark(true);
			}
			
		});
		
		return tree;
	}
	
	private class ItemCut{
		private String title;
		private long id;
		
		public ItemCut(String title, long id){
			this.title = title;
			this.id = id;
		}
		
		public String getTitle(){
			return title;
		}
		
		public long getId(){
			return id;
		}
		
		public String toString(){
			return getTitle();
		}
	}

	private TreeItem<ItemCut> deepSearchTree(Item item) {

		TreeItem<ItemCut> treeItem = new TreeItem<ItemCut>(new ItemCut(item.getTitle(), item.getId()));
		if(item.getLevel() == ItemLevel.ADD.getValue()){
			treeItem.setGraphic(cloneIcon(tree1));
		}else if(item.getLevel() == ItemLevel.PROJECT.getValue()){
			treeItem.setGraphic(cloneIcon(tree2));
		}else if(item.getLevel() == ItemLevel.SUBPROJECT.getValue()){
			treeItem.setGraphic(cloneIcon(tree3));
		}else if(item.getLevel() >= ItemLevel.TASK.getValue()){
			treeItem.setGraphic(cloneIcon(tree4));
		}
		
		Long key = item.getId();
		if (bindsMap.containsKey(key)) {
			ArrayList<Long> children = bindsMap.get(key);

			for (int i = 0; i < children.size(); i++) {
				Item child = itemsMap.get(children.get(i));
				if (child != null && child.getId() != item.getParent()
						&& child.getParent() == item.getId())
					treeItem.getChildren().add(deepSearchTree(child));
			}
		}
		
		treeItem.setExpanded(true);
		return treeItem;

	}


	protected void highlightTreeChild(Item item) {
		itemTitle.setText("Title: " + item.getTitle());
		String desc = item.getDescription();
		if(desc == null || desc.equals("null")) desc = "Right click to add informations";
		itemDesc.setText("Info: " + desc);
	}
	
	private boolean containsPoint(Item item, Point point) {
		boolean result = false;

		Style style = styles.get(item.getLevel());
		float size = style.getSize();
		if (Math.sqrt(Math.pow(item.getStartX() - point.x, 2)
				+ Math.pow(item.getStartY() - point.y, 2)) < size)
			result = true;

		return result;
	}

	private void resetAction() {
		root.getChildren().remove(hbToolbar);
	}

	private void overItem(Item item) {
		scene.setCursor(Cursor.HAND);
	}

	private void escapeItem(Item item){
		item.setMark(false);
	}
	
	private void mouseReset() {
		scene.setCursor(Cursor.DEFAULT);
		HOVER = false;
		SHAKE = false;
	}
	
	private ImageView cloneIcon(ImageView iv){
		ImageView clone = new ImageView(iv.getImage());
		
		return clone;
	}
}
