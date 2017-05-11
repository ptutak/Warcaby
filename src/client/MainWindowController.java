package client;

import java.util.ArrayList;

import enums.FieldType;
import enums.PieceType;
import enums.ResponseType;
import general.ColPiece;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class MainWindowController {

	private DraughtsClient client=null;
	private Image greenPawn=new Image("File:./img/green-pawn.png");
	private Image redPawn=new Image("File:./img/red-pawn.png");
	private Image greenQueen=new Image("File:./img/green-queen.png");
	private Image redQueen=new Image("File:./img/red-queen.png");
	private Image blank=new Image("File:./img/blank.png");
	private Image greenPlayer=new Image("File:./img/green-player.png");
	private Image redPlayer=new Image("File:./img/red-player.png");

	private Integer rowFrom=null;
	private Integer colFrom=null;
	private Integer rowTo=null;
	private Integer colTo=null;

	@FXML private Button startGameButton;
	@FXML private GridPane boardGrid;
	@FXML private Label oppositePlayerNameLabel;
	@FXML private ImageView oppositePlayerImageView;
	@FXML private Label playerNameLabel;
	@FXML private ImageView playerImageView;
	boolean gameReady=false;
	boolean gameStarted=false;
	
	Runnable updateImages=new Runnable(){
		@Override
		public void run(){
			initImages();
		}
	};
	
	Runnable refreshBoard=new Runnable(){
		@Override
		public void run(){
			refreshBoard();
		}
	};
	
	public void waitForGameReady(){
		Runnable waitForGameReady=new Runnable(){
			@Override
			public void run(){
				while(true){
					if (client.getOppositePlayer()!=null){
						gameReady=true;
						Platform.runLater(updateImages);
						break;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(waitForGameReady).start();
	}
	
	private void waitForGameMove(){
		Runnable waitForGameMove=new Runnable(){
			@Override
			public void run(){
				boolean run=true;
				while(run){
					ResponseType response=client.getServerResponse();
					if (response!=null){
						switch(response){
						case GAME_STARTED:
							client.setServerResponse(null);
							break;
						case GAME_MOVE_FINAL:
						case GAME_MOVE_CONTINUE:
						case GAME_OPPOSITE_MOVE_FINAL:
						case GAME_OPPOSITE_MOVE_CONTINUE:
							client.setServerResponse(null);
							Platform.runLater(refreshBoard);
							break;
						case GAME_ABORT:
							client.setServerResponse(null);
							run=false;
							break;
						default:
							break;
						}
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(waitForGameMove).start();
	}

	@FXML private void startGameButtonClick(){
		if (client.getOppositePlayer()!=null && !gameStarted && gameReady){
			gameStarted=true;
			client.startGame();
			waitForGameMove();
		}
	}
	
	@FXML private void exitButtonClick(){
		ResponseType response=null;
		if (client!=null)
			response=client.endConnection();
		System.out.println(response);
		if (response==ResponseType.CONNECTION_ENDED)
			System.exit(0);
		else{
			System.out.println("ABORT");
			System.exit(-1);
		}
			
	}

	public void initImages(){
		if (client.getOppositePlayer()!=null){
			oppositePlayerNameLabel.setText(client.getOppositePlayer());
			if (client.getPlayerCol()==FieldType.GREEN)
				oppositePlayerImageView.setImage(redPlayer);
			else
				oppositePlayerImageView.setImage(greenPlayer);
		} else
			oppositePlayerNameLabel.setText("No player");
		if (client.getPlayerCol()==FieldType.GREEN)
			playerImageView.setImage(greenPlayer);
		else
			playerImageView.setImage(redPlayer);
		playerNameLabel.setText(client.getPlayerName());
	}
	

	public void setClient(DraughtsClient client) {
		this.client = client;
	}

	private void move(){
		if (gameStarted){
			if (client.getPlayerCol()==FieldType.GREEN)
				client.move(rowFrom, colFrom, rowTo, colTo);
			else
				client.move(client.getBoardInfo().boardBounds.rowStop-rowFrom, client.getBoardInfo().boardBounds.colStop-colFrom, client.getBoardInfo().boardBounds.rowStop-rowTo, client.getBoardInfo().boardBounds.colStop-colTo);
		}
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
					newPiece.addEventFilter(MouseEvent.MOUSE_PRESSED, (event)->{
						if (rowFrom==null){
							rowFrom=GridPane.getRowIndex(newPiece);
							colFrom=GridPane.getColumnIndex(newPiece);
	//						System.out.println("Source: "+rowFrom.toString()+" "+colFrom.toString());
						}
					});
					if (client.getPlayerCol()==FieldType.GREEN){
						boardGrid.add(newPiece, piece.piece.column, piece.piece.row);
					} else
						boardGrid.add(newPiece, client.getBoardInfo().boardBounds.colStop-piece.piece.column,client.getBoardInfo().boardBounds.rowStop-piece.piece.row );
					GridPane.setHalignment(newPiece, HPos.CENTER);
				}


				for (int i=0; i<boardGrid.getRowConstraints().size();i++){
					for (int j=0;j<boardGrid.getColumnConstraints().size();j++){
						boolean contains=false;

						for (Node node : boardGrid.getChildren()) {
							if (GridPane.getColumnIndex(node).intValue() == j && GridPane.getRowIndex(node).intValue() == i){
								contains=true;
								break;
							} 	
						}
						if (contains)
							continue;
						ImageView blankImg=new ImageView(blank);
						blankImg.setPickOnBounds(true);
						blankImg.addEventFilter(MouseEvent.MOUSE_PRESSED, (event)->{
							if (rowFrom!=null){
								rowTo=GridPane.getRowIndex(blankImg);
								colTo=GridPane.getColumnIndex(blankImg);
//								System.out.println("Destination:"+rowTo.toString()+" "+colTo.toString());
								move();
								rowFrom=null;
							}
						});
						boardGrid.add(blankImg, j, i);
						GridPane.setHalignment(blankImg, HPos.CENTER);
					}
				}
			}
	}

	@FXML public void initialize(){
		//		board=new Board(new BoardBounds());

	}

}
