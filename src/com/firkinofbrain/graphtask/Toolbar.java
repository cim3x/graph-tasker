package com.firkinofbrain.graphtask;

import com.firkinofbrain.graphtask.Main.Flag;
import com.firkinofbrain.graphtask.database.Item.ItemLevel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Toolbar extends VBox{
	
	private Node item;
	
	ImageView zoomin = new ImageView(new Image(getClass().getResourceAsStream(
			"../../../drawable/zoomin.png")));
	ImageView move = new ImageView(new Image(getClass().getResourceAsStream(
			"../../../drawable/move.png")));
	ImageView deleteItem = new ImageView(new Image(getClass().getResourceAsStream(
			"../../../drawable/delete.png")));
	ImageViewtick = new ImageView(new Image(getClass().getResourceAsStream(
			"../../../drawable/tick.png")));
	
	public Toolbar(Node clicked, Node movable, Flag action){
		
	}
	
	HBox buttons = new HBox();

	Button bMove = new Button();
	bMove.setGraphic(move);
	bMove.setPrefWidth(this.getPrefWidth());
	bMove.setOnMouseClicked(new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (item.getLevel() != ItemLevel.PROJECT.getValue()) {
				movable = item;
				action = Flag.MOVING_ITEM;
			}
		}
	});
	buttons.getChildren().add(bMove);

	Button bZoom = new Button();
	bZoom.setGraphic(zoomin);
	bZoom.setPrefWidth(hbToolbar.getPrefWidth());
	bZoom.setOnMouseClicked(new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			zoomItem(item);
		}
	});
	buttons.getChildren().add(bZoom);

	Button bDelete = new Button();
	bDelete.setGraphic(deleteItem);
	bDelete.setPrefWidth(hbToolbar.getPrefWidth());
	bDelete.setOnMouseClicked(new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			deleteItem(item);
			resetAction();
		}
	});
	buttons.getChildren().add(bDelete);

	Button bTick = new Button();
	bTick.setGraphic(tick);
	bTick.setPrefWidth(hbToolbar.getPrefWidth());
	bTick.setOnMouseClicked(new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			completeTask(item);
		}
	});
	buttons.getChildren().add(bTick);

	hbToolbar.getChildren().add(buttons);

	TextField tfName = new TextField();
	tfName.setPromptText(item.getTitle());
	tfName.textProperty().addListener(new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> observable,
				String oldValue, String newValue) {
			item.setTitle(newValue);
		}

	});
	hbToolbar.getChildren().add(tfName);

	TextArea taDesc = new TextArea();
	taDesc.setPrefRowCount(5);
	taDesc.setPrefColumnCount(25);
	taDesc.setWrapText(true);
	taDesc.setText("Extra information");
	taDesc.textProperty().addListener(new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> observable,
				String oldValue, String newValue) {
			item.setDescription(newValue);
		}

	});
	this.getChildren().add(taDesc);
}
