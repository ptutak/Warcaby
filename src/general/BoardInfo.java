package general;

public class BoardInfo {
	public BoardBounds boardBounds;
	public int rowNumber;
	public int turnTimeLimit;
	public int gameTimeLimit;
	
	public BoardInfo(){
		boardBounds=new BoardBounds();
		rowNumber=3;
		turnTimeLimit=0;
		gameTimeLimit=0;
	}
}
