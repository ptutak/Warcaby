package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DraughtsClientGui extends Application {
	
//	private static MainWindowController mainWindowController;
	private LoginWindowController loginWindowController;
	
	private static DraughtsClient client;
	
	public DraughtsClientGui(){
		if(client==null){
			client = new DraughtsClient();
		}
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loaderMain=new FXMLLoader(this.getClass().getResource("mainWindow.fxml"));
		Parent mainNode=loaderMain.load();
//		mainWindowController=loaderMain.getController();
		Scene mainScene=new Scene(mainNode);

		FXMLLoader loaderLogin=new FXMLLoader(this.getClass().getResource("mainWindow.fxml"));
		Parent loginNode=loaderLogin.load();
		loginWindowController=loaderLogin.getController();
		loginWindowController.setClient(client);
		loginWindowController.setMainScene(mainScene);
		loginWindowController.setStage(stage);
		Scene loginScene=new Scene(loginNode);
		
		
		
		stage.setScene(loginScene);

		stage.show();
	}

}
