package client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginWindowController {
	
	@FXML private TextField loginTextField;
	@FXML private TextField ipTextField;
	@FXML private TextField portTextField;
	@FXML private Button connectLoginButton;
	@FXML private Label infoLabel;
	
	@FXML private void connectLoginButtonClick(){
		String ip = ipTextField.getText();
		String port = portTextField.getText();
		String login=loginTextField.getText();
	}
	
	@FXML public void initialize(){
		
	}

}
