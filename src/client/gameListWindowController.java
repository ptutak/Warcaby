package client;

import enums.ResponseType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import server.GameInfo;

public class GameListWindowController {
	
	private DraughtsClient client;
	private Stage stage;
	private Scene mainScene;
	
	@FXML private Label infoLabel;
	
	private ObservableList<GameInfo> gameList=FXCollections.observableArrayList();
	@FXML private TableColumn<GameInfo, String> gameNameCol;
	@FXML private TableColumn<GameInfo, String> rowNumberCol;
	@FXML private TableColumn<GameInfo, String> playerRedCol;
	@FXML private TableColumn<GameInfo, String> turnTimeLimitCol;
	@FXML private TableColumn<GameInfo, String> gameTimeLimitCol;
	@FXML private TableView<GameInfo> gameTableView;
	
	@FXML private Button newGameButton;
	@FXML private Button joinGameButton;
	@FXML private Button refreshButton;
	@FXML private Button quitButton;
	
	@FXML private TextField gameNameTextField;
	
	@FXML private AnchorPane gameManager;
	

	public synchronized void setStage(Stage stage) {
		this.stage = stage;
	}
	public synchronized void setMainScene(Scene mainScene) {
		this.mainScene = mainScene;
	}
	public synchronized void setClient(DraughtsClient client) {
		this.client = client;
	}
	public void gameAdd(GameInfo gameInfo){
		gameList.add(gameInfo);
	}
	public void gameRemove(GameInfo gameInfo){
		gameList.remove(gameInfo);
	}
	private void updateList(){
		GameInfo[] gameList=client.getGameList();
		if (gameList!=null)
			this.gameList.setAll(gameList);
	}
	

	@FXML
	private void newGameButtonClick(){
		String gameName=gameNameTextField.getText();
		if (!gameName.equals("")){
			ResponseType response=client.newGame(gameName, 3, 8);
			if (response==ResponseType.GAME_CREATED){
				stage.setScene(mainScene);
			} else if (response==ResponseType.GAME_EXISTS){
				infoLabel.setText("Game with this name already exists");
			} else {
				infoLabel.setText("Connection error");
			}
		}
	}
	@FXML
	private void joinGameButtonClick(){
		String gameName=gameNameTextField.getText();
		if (!gameName.equals("")){
			ResponseType response=client.joinGame(gameName);
			if (response==ResponseType.GAME_JOINED){
				stage.setScene(mainScene);
			} else if(response==ResponseType.WRONG_GAME_NAME){
				infoLabel.setText("Wrong game name");
			} else {
				infoLabel.setText("Connection error");
			}
		}
	}
	@FXML
	private void refreshButtonClick(){
		updateList();
	}
	@FXML
	private void quitButtonClick(){
		client.endConnection();
		System.exit(0);
	}
	@FXML public void initialize(){
		gameTableView.setItems(gameList);
		gameNameCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("gameName"));
		rowNumberCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("rowNumber"));
		playerRedCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("playerRed"));
		turnTimeLimitCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("turnTimeLimit"));
		gameTimeLimitCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("gameTimeLimit"));
		gameTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null) {
		        gameNameTextField.setText(newSelection.getGameName());
		    }
		});
	}

}
