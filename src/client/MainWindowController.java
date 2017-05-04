package client;

import java.util.ArrayList;

import enums.FieldType;
import enums.PieceType;
import general.ColPiece;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MainWindowController {

	private DraughtsClient client=null;
	private Image greenPawn=new Image("File:./img/green-pawn.png");
	private Image redPawn=new Image("File:./img/red-pawn.png");
	private Image greenQueen=new Image("File:./img/green-queen.png");
	private Image redQueen=new Image("File:./img/red-queen.png");
//	private Image blank=new Image("File:./img/blank.png");

	@FXML private Button startGameButton;
	@FXML private GridPane boardGrid;

	@FXML private void startGameButtonClick(){
		refreshBoard();
	}
	
	public void setClient(DraughtsClient client) {
		this.client = client;
	}

	public void refreshBoard(){
		if (client!=null)
			if (client.getBoard()!=null){
//				System.out.println("jest");
				boardGrid.getChildren().clear();
				ArrayList<ColPiece> boardState=client.getBoard().getBoardState();
				for (ColPiece piece:boardState){
					ImageView newPiece;
					if (piece.field==FieldType.RED){
						if (piece.piece.type==PieceType.PAWN)
							newPiece=new ImageView(redPawn);
						else
							newPiece=new ImageView(redQueen);
//						System.out.println("red");
					} else {
						if (piece.piece.type==PieceType.PAWN)
							newPiece=new ImageView(greenPawn);
						else
							newPiece=new ImageView(greenQueen);
//						System.out.println("green");
					}
					boardGrid.add(newPiece, piece.piece.column, piece.piece.row);
					GridPane.setHalignment(newPiece, HPos.CENTER);
				}
				/*
			for (int i=0; i<board.getRowStop()-board.getRowStart()+1;i++){
				for (int j=0;j<board.getColStop()-board.getColStart()+1;j++){


				}
			}
				 */
			}
	}

	@FXML public void initialize(){
		//		board=new Board(new BoardBounds());
	}

}
