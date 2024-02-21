package org.github.logof.ZXMapEditorFX.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
import org.github.logof.ZXMapEditorFX.Constans;
import org.github.logof.ZXMapEditorFX.property.TiledMap;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewMapDialog extends AnchorPane implements Initializable {
	@FXML
	private TextField tileWidthTf;
	@FXML
	private TextField tileHeightTf;
	@FXML
	private TextField mapWidthTf;
	@FXML
	private TextField mapHeightTf;

	private Stage newAlertDialog;
	@Setter
	@Getter
	private OnNewMapDialogActionListener onNewMapDialogActionListener;

	public NewMapDialog() {
		FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("NewMapDialog.fxml"));
		fXMLLoader.setRoot(NewMapDialog.this);
		fXMLLoader.setController(NewMapDialog.this);
		try {
			fXMLLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/*
		 * tileWidthTf.lengthProperty().addListener(new
		 * ChangeListener<Number>(){
		 * 
		 * @Override public void changed(ObservableValue<? extends Number>
		 * observable, Number oldValue, Number newValue) {
		 * if(newValue.intValue() > oldValue.intValue()){ char ch =
		 * tileWidthTf.getText().charAt(oldValue.intValue()); //Check if the new
		 * character is the number or other's if(!(ch >= '0' && ch <= '9' )){
		 * tileWidthTf
		 * .setText(tileWidthTf.getText().substring(0,tileWidthTf.getText
		 * ().length()-1)); } } } });
		 */
	}

	public void showAlertDialog() {
		if (newAlertDialog == null) {
			newAlertDialog = new Stage(StageStyle.TRANSPARENT);
			newAlertDialog.setResizable(false);
			newAlertDialog.setScene(new Scene(this));
			newAlertDialog.show();
		} else {
			newAlertDialog.show();
		}
	}

	public void hideAlertDialog() {
		if (newAlertDialog != null) {
			newAlertDialog.hide();
		}
	}

	@FXML
	private void onNewMapAction(ActionEvent event) {
		String mapWidthStr = mapWidthTf.getText();
		String mapHeightStr = mapHeightTf.getText();
		if (!mapWidthStr.isBlank() && !mapHeightStr.isBlank()) {
			try {
				int mapWidth = Integer.parseInt(mapWidthStr) * Constans.SCREEN_TILES_ON_WIDTH;
				int mapHeight = Integer.parseInt(mapHeightStr) * Constans.SCREEN_TILES_ON_HEIGHT;
				TiledMap.getInstance().setMapProperty(mapWidth, mapHeight);
				if (onNewMapDialogActionListener != null) {
					onNewMapDialogActionListener.onNewMapOkAction();
				}
				hideAlertDialog();
			} catch (NumberFormatException e) {
                e.printStackTrace();
			}
		}
	}

	@FXML
	private void onNewMapCancelAction(ActionEvent event) {
		if (onNewMapDialogActionListener != null) {
			onNewMapDialogActionListener.onNewMapCancelAction();
		}
		hideAlertDialog();
	}

	public interface OnNewMapDialogActionListener {
		public void onNewMapOkAction();

		public void onNewMapCancelAction();
	}
}
