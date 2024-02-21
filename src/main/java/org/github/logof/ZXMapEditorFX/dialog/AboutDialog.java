package org.github.logof.ZXMapEditorFX.dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.github.logof.ZXMapEditorFX.Main;
import java.io.IOException;

public class AboutDialog extends AnchorPane {
	private static AboutDialog wiAlertDialog;
	private static Stage newAlertDialog;

	private AboutDialog() {
		FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("AboutDialog.fxml"));
		fXMLLoader.setRoot(AboutDialog.this);
		fXMLLoader.setController(AboutDialog.this);
		try {
			fXMLLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void showAlertDialog() {
		if (newAlertDialog == null) {
			newAlertDialog = new Stage(StageStyle.DECORATED);
			newAlertDialog.setResizable(false);
			wiAlertDialog = new AboutDialog();
			newAlertDialog.setTitle("关于");
			newAlertDialog.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
			newAlertDialog.setScene(new Scene(wiAlertDialog, 600, 400));
			newAlertDialog.show();
		} else {
			newAlertDialog.show();
		}
	}

	public static void showAboutDialog() {
		showAlertDialog();
	}
}
