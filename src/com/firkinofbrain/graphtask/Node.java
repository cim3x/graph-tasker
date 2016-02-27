package com.firkinofbrain.graphtask;

import java.util.Date;
import java.util.List;

import com.firkinofbrain.graphtask.database.Item;
import com.firkinofbrain.graphtask.database.Style;

public class Node {
	
	private long id; //unique id
	private double sx, sy;
	private double x, y; //coordinates
	private double vx, vy;
	private Style style;
	
	private Date deadline;
	private boolean done;
	private boolean mark;
	private boolean hidden;
	
	private String title;
	private String description;
	private String sProgress;
	private int iProgress;
	
	private Node parent;
	private List<Node> children;
	
	public Node(){
		
	}
	
	public Node(Item i){
		
		this.setId(i.getId());
		this.setSx(i.getStartX());
		this.setSy(i.getStartY());
		this.setX(i.getX());
		this.setY(i.getY());
		this.setVx(i.getVelocityX());
		this.setVy(i.getVelocityY());
		this.setDeadline(new Date(i.getDeadline()));
		this.setDone(i.getDone() == 1 ? true : false);
		this.setMark(i.isMark());
		this.setHidden(i.isHidden());
		this.setTitle(i.getTitle());
		this.setDescription(i.getDescription());
		this.setsProgress(i.getProgress());
		this.setiProgress(i.getiProgress());
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getSx() {
		return sx;
	}
	public void setSx(double sx) {
		this.sx = sx;
	}
	public double getSy() {
		return sy;
	}
	public void setSy(double sy) {
		this.sy = sy;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getVx() {
		return vx;
	}
	public void setVx(double vx) {
		this.vx = vx;
	}
	public double getVy() {
		return vy;
	}
	public void setVy(double vy) {
		this.vy = vy;
	}
	public Style getStyle() {
		return style;
	}
	public void setStyle(Style style) {
		this.style = style;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public boolean isMark() {
		return mark;
	}
	public void setMark(boolean mark) {
		this.mark = mark;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
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
	public String getsProgress() {
		return sProgress;
	}
	public void setsProgress(String sProgress) {
		this.sProgress = sProgress;
	}
	public int getiProgress() {
		return iProgress;
	}
	public void setiProgress(int iProgress) {
		this.iProgress = iProgress;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}
}
