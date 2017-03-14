/* 
  Copyright 2017 Piotr Tutak
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
class Play extends Thread {
	private GameInfo gameInfo;
	private TurnInfo turnInfo;
	private Board gameBoard;
	private Timer timer;
	
	Play(Board gameBoard, GameInfo gameInfo,Timer timer, TurnInfo turnInfo){
		this.gameBoard=gameBoard;
		this.timer=timer;
		this.turnInfo=turnInfo;
		this.gameInfo=gameInfo;
	}
	
	void gameWin(){
		turnInfo.setTimerOn(false);
		if (turnInfo.getActivePlayer()==gameInfo.getPlayerRed()){
			gameInfo.setWinner(gameInfo.getPlayerGreen());
			int points=0;
			for (ColPiece x:gameBoard.boardState()){
				if (x.field==FType.GREEN){
					if (x.piece.type==PType.PAWN)
						points+=1;
					else
						points+=2;
				}
			}
			gameInfo.getPlayerGreen().minorPoints+=points;
			gameInfo.getPlayerGreen().mainPoints+=3;
		}
		else {
			gameInfo.setWinner(gameInfo.getPlayerRed());
			int points=0;
			for (ColPiece x:gameBoard.boardState()){
				if (x.field==FType.RED){
					if (x.piece.type==PType.PAWN)
						points+=1;
					else
						points+=2;
				}
			}
			gameInfo.getPlayerRed().minorPoints+=points;
			gameInfo.getPlayerRed().mainPoints+=3;
		}
	}
	void gameDraw(){
		turnInfo.setTimerOn(false);
		gameInfo.setWinner(null);
		int greenPoints=0;
		int redPoints=0;
		for (ColPiece x:gameBoard.boardState()){
			if (x.field==FType.GREEN){
				if (x.piece.type==PType.PAWN)
					greenPoints+=1;
				else
					greenPoints+=2;
			} else {
				if (x.piece.type==PType.PAWN)
					redPoints+=1;
				else
					redPoints+=2;			}
		}
		gameInfo.getPlayerRed().minorPoints+=redPoints;
		gameInfo.getPlayerGreen().minorPoints+=greenPoints;
		gameInfo.getPlayerRed().mainPoints+=1;
		gameInfo.getPlayerGreen().mainPoints+=1;
	}
	void gamePause(){
		turnInfo.setTimerOn(false);
		gameInfo.setBoardState(gameBoard.boardState());
		
	}
	void nextPlayer(){
		if (gameInfo.getPlayerGreen()==turnInfo.getActivePlayer())
			turnInfo.setActivePlayer(gameInfo.getPlayerRed());
		else
			turnInfo.setActivePlayer(gameInfo.getPlayerGreen());
	}
	
	public void run(){
		while(!turnInfo.isTimerOn()){
			try{
				sleep(10);
			} catch (InterruptedException e){}
		}
		gameInfo.setGameState(GSType.GAME_RUNNING);
		while (gameInfo.getGameState()==GSType.GAME_RUNNING){
			if (turnInfo.getRemainTurnTime()<=0){
				gameInfo.setGameState(GSType.GAME_END);
				gameWin();
			}
			else{
				Move move=turnInfo.getActivePlayer().getMove();
				switch(move.playerMove){
				case SURRENDER:
					gameInfo.setGameState(GSType.GAME_END);
					gameWin();
					break;
				case PAUSE:
					gameInfo.setGameState(GSType.GAME_PAUSE);
					gamePause();
					break;
				case DRAW:
					gameInfo.setGameState(GSType.GAME_END);
					gameDraw();
					break;
				case MOVE:
					if (move.moveFrom.field==FType.GREEN && turnInfo.getActivePlayer()==gameInfo.getPlayerRed())
						break;
					if (move.moveFrom.field==FType.RED && turnInfo.getActivePlayer()==gameInfo.getPlayerGreen())
						break;
					switch(gameBoard.movePiece(move.moveFrom.piece, move.moveTo.piece.row, move.moveTo.piece.column)){
					case MOVE:
						nextPlayer();
						break;
					case KILL:
						if (move.moveFrom.piece.type==PType.PAWN && move.moveTo.piece.row==gameBoard.getRowStop() && turnInfo.getActivePlayer()==gameInfo.getPlayerRed() && move.moveTo.piece.type==PType.QUEEN){
							nextPlayer();
							break;
						}
						else if (move.moveFrom.piece.type==PType.PAWN && move.moveTo.piece.row==gameBoard.getRowStart() && turnInfo.getActivePlayer()==gameInfo.getPlayerGreen() && move.moveTo.piece.type==PType.QUEEN){
							nextPlayer();
							break;
						}
						timer.nextTurn();
						break;
					case BAD:
						break;
					}
				}
			}
		}
	}
}
