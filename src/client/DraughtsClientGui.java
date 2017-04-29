package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DraughtsClientGui extends Application {
	
//	private static MainWindowController mainWindowController;
	private LoginWindowController loginWindowController;
	private GameListWindowController gameListWindowController;
	private static DraughtsClient client;
	
	public DraughtsClientGui(){
		client = new DraughtsClient();
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loaderMain=new FXMLLoader(this.getClass().getResource("mainWindow.fxml"));
		Parent mainNode=loaderMain.load();
//		mainWindowController=loaderMain.getController();
		Scene mainScene=new Scene(mainNode);

		FXMLLoader loaderLogin=new FXMLLoader(this.getClass().getResource("loginWindow.fxml"));
		Parent loginNode=loaderLogin.load();
		loginWindowController=loaderLogin.getController();
		loginWindowController.setClient(client);
		loginWindowController.setStage(stage);
		Scene loginScene=new Scene(loginNode);
		
		FXMLLoader loaderList=new FXMLLoader(this.getClass().getResource("gameListWindow.fxml"));
		Parent gameListNode=loaderList.load();
		gameListWindowController=loaderList.getController();
		gameListWindowController.setClient(client);
		gameListWindowController.setStage(stage);
		Scene gameListScene=new Scene(gameListNode);
		loginWindowController.setGameListScene(gameListScene);
			
		
		stage.setScene(loginScene);

		stage.show();
	}
	
	public static void main(String[] args){
		DraughtsClientGui client=new DraughtsClientGui();
		Application.launch(client.getClass());
	}

}
