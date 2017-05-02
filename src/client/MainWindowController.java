package client;

import general.Board;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MainWindowController {
	
	Board board;
	Image greenPaw=new Image("File./img/green-paw.png");
	Image redPaw=new Image("File./img/red-paw.png");

	@FXML GridPane boardGrid;
	
	public synchronized Board getBoard() {
		return board;
	}

	public synchronized void setBoard(Board board) {
		this.board = board;
	}

	@FXML public void initialize(){

		boardGrid.add(pawnImageView, 0, 0);
		GridPane.setHalignment(pawnImageView,HPos.CENTER);
	}

}
