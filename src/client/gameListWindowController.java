package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import server.GameInfo;

public class gameListWindowController {
	
	private DraughtsClient client;
	
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
	

	public synchronized void setClient(DraughtsClient client) {
		this.client = client;
	}
	public void gameAdd(GameInfo gameInfo){
		gameList.add(gameInfo);
	}
	public void gameRemove(GameInfo gameInfo){
		gameList.remove(gameInfo);
	}
	public void updateList(GameInfo[] gameList){
		this.gameList.setAll(gameList);
	}
	
	@FXML
	private void newGameButtonClick(){
		String gameName=gameNameTextField.getText();
		if (!gameName.equals("")){
			
		}
	}
	@FXML
	private void joinGameButtonClick(){
		
	}
	@FXML
	private void refreshButtonClick(){
		updateList(client.getGameList());
	}
	@FXML
	private void quitButtonClick(){
		
	}
	@FXML public void initialize(){
		gameTableView.setItems(gameList);
		gameNameCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("gameName"));
		rowNumberCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("rowNumber"));
		playerRedCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("playerRed"));
		turnTimeLimitCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("turnTimeLimit"));
		gameTimeLimitCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("gameTimeLimit"));
	}

}
