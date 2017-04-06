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
	
	public void gameSurrender(){
		turnInfo.setTimerOn(false);
		if (turnInfo.activePlayer.equals(gameInfo.playerRedMove.player)){
			gameInfo.winner=gameInfo.playerGreenMove.player;
			int points=0;
			for (ColPiece x:gameBoard.getBoardState()){
				if (x.field==FType.GREEN){
					if (x.piece.type==PType.PAWN)
						points+=1;
					else
						points+=2;
				}
			}
			gameInfo.playerGreenMove.player.getPlayerInfo().minorPoints+=points;
			gameInfo.playerGreenMove.player.getPlayerInfo().mainPoints+=3;
		}
		else {
			gameInfo.winner=gameInfo.playerRedMove.player;
			int points=0;
			for (ColPiece x:gameBoard.getBoardState()){
				if (x.field==FType.RED){
					if (x.piece.type==PType.PAWN)
						points+=1;
					else
						points+=2;
				}
			}
			gameInfo.playerRedMove.player.getPlayerInfo().minorPoints+=points;
			gameInfo.playerRedMove.player.getPlayerInfo().mainPoints+=3;
		}
	}
	
	private void gameWin(Player winner){
		//TODO: winning the game
		
	}
	public void gameDraw(){
		turnInfo.setTimerOn(false);
		gameInfo.winner=null;
		int greenPoints=0;
		int redPoints=0;
		for (ColPiece x:gameBoard.getBoardState()){
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
		gameInfo.playerRedMove.player.getPlayerInfo().minorPoints+=redPoints;
		gameInfo.playerGreenMove.player.getPlayerInfo().minorPoints+=greenPoints;
		gameInfo.playerRedMove.player.getPlayerInfo().mainPoints+=1;
		gameInfo.playerGreenMove.player.getPlayerInfo().mainPoints+=1;
	}
	public void gamePause(){
		turnInfo.setTimerOn(false);
		gameInfo.setBoardState(gameBoard.getBoardState());
	}
	void nextPlayer(){
		if (gameInfo.playerGreenMove.equals(turnInfo.activePlayer))
			turnInfo.activePlayer=gameInfo.playerRedMove.player;
		else
			turnInfo.activePlayer=gameInfo.playerGreenMove.player;
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
				gameSurrender();
			}
			else{
				Move move;
				if (turnInfo.activePlayer.equals(gameInfo.playerRedMove.player))
					move=gameInfo.playerRedMove.getMove();
				else
					move=gameInfo.playerGreenMove.getMove();
				
				if (move.moveFrom.field==FType.GREEN && turnInfo.activePlayer.equals(gameInfo.playerRedMove.player))
					break;
				if (move.moveFrom.field==FType.RED && turnInfo.activePlayer.equals(gameInfo.playerGreenMove.player))
					break;
				switch(gameBoard.movePiece(move.moveFrom.piece, move.moveTo.piece.row, move.moveTo.piece.column)){
				case MOVE:
					nextPlayer();
					break;
				case KILL:
					if (move.moveFrom.piece.type==PType.PAWN && move.moveTo.piece.row==gameBoard.getRowStop() && turnInfo.activePlayer.equals(gameInfo.playerRedMove.player) && move.moveTo.piece.type==PType.QUEEN){
						nextPlayer();
						break;
					}
					else if (move.moveFrom.piece.type==PType.PAWN && move.moveTo.piece.row==gameBoard.getRowStart() && turnInfo.activePlayer.equals(gameInfo.playerGreenMove.player) && move.moveTo.piece.type==PType.QUEEN){
						nextPlayer();
						break;
					}
					timer.nextTurn();
					break;
				case BAD:
					break;
				}
				switch (gameBoard.checkWinner()){
				case GREEN:
					
				}
			}
		}
	}
}
