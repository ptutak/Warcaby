package client;

import java.util.regex.Pattern;

import enums.ResponseType;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginWindowController {
	@FXML private AnchorPane loginWindow;
	@FXML private TextField loginTextField;
	@FXML private TextField ipTextField;
	@FXML private TextField portTextField;
	@FXML private Button connectLoginButton;
	@FXML private Label infoLabel;
	
	
	private boolean connectionEstablished=false;
	
	private Stage stage;
	private DraughtsClient client;
	private Scene gameListScene;
	
	public synchronized void setGameListScene(Scene gameListScene) {
		this.gameListScene = gameListScene;
	}

	public synchronized void setStage(Stage stage) {
		this.stage = stage;
	}

	public synchronized void setClient(DraughtsClient client) {
		this.client = client;
	}

	private static final Pattern pattern= Pattern.compile(
	        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public static boolean validate(final String ip) {
	    return pattern.matcher(ip).matches();
	}
	
	@FXML private void connectLoginButtonClick(){
		if (!connectionEstablished){
		String ip = ipTextField.getText();
		if (!validate(ip)){
			infoLabel.setText("Wrong ip address");
			return;
		}
		int port=-1;
		try{
			port = Integer.parseInt(portTextField.getText());
		} catch (NumberFormatException e){
			infoLabel.setText("Wrong port");
			return;
		}
		if (client.establishConnection(ip, port))
			connectionEstablished=true;
		else{
			infoLabel.setText("Cannot establish connection");
			return;
		}
			
		} 
		String login=loginTextField.getText();
		if (connectionEstablished){
			ResponseType resp=client.registerUser(login);
			if (resp==ResponseType.USER_REGISTERED){
				stage.setScene(gameListScene);
			} else if (resp==ResponseType.USER_EXISTS){
				infoLabel.setText("User exists");
			} else{
				infoLabel.setText("Server not responding, or no connection to the server, Try again.");
			}
		}
	}
	
	@FXML public void initialize(){
		
	}

}
