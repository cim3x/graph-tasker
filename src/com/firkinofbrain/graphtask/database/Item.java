package com.firkinofbrain.graphtask.database;

public class Item {
	private long id;
	private long parent;
	private String title;
	private String description;
	private long deadline;
	private String progress;
	private int iProgress;
	private int level;
	private int style;
	private int done;
	private boolean mark;
	private boolean hidden;
	private float x;
	private float y;
	private float zone;
	private float sx; //start x
	private float sy; //start y
	private float vx, vy; //velocity
	
	public Item(){
		this.setZone(50);
		this.mark = false;
	}
	
	public long getId() {
		return id;
	}
	public long getParent() {
		return parent;
	}

	public void setParent(long parent) {
		this.parent = parent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getDeadline(){
		return this.deadline;
	}

	public void setDeadline(long deadline){
		this.deadline = deadline;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	
	public float getStartX() {
		return sx;
	}
	public void setStartX(float sx) {
		this.x = this.sx = sx;
	}

	public float getStartY() {
		return sy;
	}
	public void setStartY(float sy) {
		this.y = this.sy = sy;
	}

	public float getVelocityX() {
		return vx;
	}
	public float getVelocityY(){
		return vy;
	}
	
	public void setVelocity(float vx, float vy) {
		this.vx = vx;
		this.vy = vy;
	}
	
	public void setVelocity(double vx, double vy) {
		this.vx = (float) vx;
		this.vy = (float) vy;
	}
	
	public boolean isDone(){
		return done == 1;
	}
	
	public int getDone() {
		return done;
	}
	
	public void setDone(boolean done){
		this.done = done ? 1 : 0;
	}

	public void setDone(int done) {
		this.done = done;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public boolean isMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}
	
	public boolean move(float delta){
		boolean tight = false;
		float nx = this.x + vx*delta;
		float ny = this.y + vy*delta;
		
		double d = Math.pow(nx - sx, 2) + Math.pow(ny - sy, 2);
		if(d < zone*zone){
			this.x = nx;
			this.y = ny;
		}else{
			zone -= 6;
			int dirX = vx < 0 ? 1 : -1;
			int dirY = vy < 0 ? 1 : -1;
			
			vx = (float) (dirX * Math.random()*5);
			vy = (float) (dirY * Math.random()*5);
		}
		
		if(zone < 5){
			tight = true;
			this.x = this.sx;
			this.y = this.sy;
		}
		
		return tight;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public float getZone() {
		return zone;
	}

	public void setZone(float zone) {
		this.zone = zone;
	}

	public int getiProgress() {
		return iProgress;
	}

	public void setiProgress(int iProgress) {
		this.iProgress = iProgress;
	}

	public enum ItemLevel{
		ADD(0), PROJECT(1), SUBPROJECT(2), TASK(3), COOWORKER(4);
		
		private final int value;
		private ItemLevel(int value){
			this.value = value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
}
