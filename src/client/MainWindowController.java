package client;

import java.util.ArrayList;

import enums.FieldType;
import enums.PieceType;
import general.ColPiece;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	private Image greenPlayer=new Image("File:./img/green-player.png");
	private Image redPlayer=new Image("File:./img/red-player.png");

	@FXML private Button startGameButton;
	@FXML private GridPane boardGrid;
	@FXML private Label oppositePlayerNameLabel;
	@FXML private ImageView oppositePlayerImageView;

	@FXML private void startGameButtonClick(){
	}
	
	public void initImages(){
		System.out.println(client.getOppositePlayer());
		oppositePlayerNameLabel.setText(client.getOppositePlayer());
		if (client.getPlayerCol()==FieldType.GREEN)
			oppositePlayerImageView.setImage(redPlayer);
		else
			oppositePlayerImageView.setImage(greenPlayer);
		System.out.println(client.getPlayerCol());
	}
	
	public void setClient(DraughtsClient client) {
		this.client = client;
	}

	public void refreshBoard(){
		if (client!=null)
			if (client.getBoard()!=null){

				boardGrid.getChildren().clear();
				ArrayList<ColPiece> boardState=client.getBoard().getBoardState();
				
				for (ColPiece piece:boardState){
					ImageView newPiece;
					if (piece.field==FieldType.RED){
						if (piece.piece.type==PieceType.PAWN)
							newPiece=new ImageView(redPawn);
						else
							newPiece=new ImageView(redQueen);

					} else {
						if (piece.piece.type==PieceType.PAWN)
							newPiece=new ImageView(greenPawn);
						else
							newPiece=new ImageView(greenQueen);

					}
					if (client.getPlayerCol()==FieldType.GREEN)
						boardGrid.add(newPiece, piece.piece.column, piece.piece.row);
					else
						boardGrid.add(newPiece, client.getBoardInfo().boardBounds.colStop-piece.piece.column,client.getBoardInfo().boardBounds.rowStop-piece.piece.row );
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
