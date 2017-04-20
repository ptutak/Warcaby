package client;

import java.util.LinkedList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUI extends Application {
	
	private static MainWindowController mainWindowController;
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loaderMain=new FXMLLoader(this.getClass().getResource("mainWindow.fxml"));
		Parent mainNode=loaderMain.load();
		mainWindowController=loaderMain.getController();
		Scene mainScene=new Scene(mainNode);
		
		stage.setScene(mainScene);
		stage.show();
	}

}
