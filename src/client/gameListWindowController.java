package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import server.GameInfo;

public class gameListWindowController {
	
	@FXML private Label infoLabel;
	
	private ObservableList<GameInfo> gameList=FXCollections.observableArrayList();
	@FXML private TableColumn<GameInfo, String> gameNameCol;
	@FXML private TableColumn<GameInfo, String> rowNumberCol;
	@FXML private TableColumn<GameInfo, String> playerRedCol;
	@FXML private TableColumn<GameInfo, String> turnTimeLimitCol;
	@FXML private TableColumn<GameInfo, String> gameTimeLimitCol;
	@FXML private ListView<GameInfo> gameTableView;
	
	@FXML private Button newGameButton;
	@FXML private Button joinGameButton;
	@FXML private Button refreshButton;
	@FXML private Button cancelButton;
	
	
	public void gameAdd(GameInfo gameInfo){
		gameList.add(gameInfo);
	}
	public void gameRemove(GameInfo gameInfo){
		gameList.remove(gameInfo);
	}
	
	@FXML public void initialize(){
		gameTableView.setItems(gameList);
		gameNameCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("gameName"));
		rowNumberCol.setCellValueFactory(new PropertyValueFactory<GameInfo,String>("rowNumber"));
	}

}
