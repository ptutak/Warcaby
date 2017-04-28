package server;
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
import java.util.ArrayList;
import java.util.UUID;

import enums.GameStatusType;
import general.BoardBounds;
import general.ColPiece;
import general.Player;
import general.PlayerMove;

public class GameInfo {
	private String gameName;
	private UUID ID;
	private GameStatusType gameStatus;
	private ArrayList<ColPiece> boardState;
	private BoardBounds boardBounds;
	private int rowNumber;
	private int turnTimeLimit;
	private int gameTimeLimit;

	public PlayerMove playerRedMove;
	public PlayerMove playerGreenMove;
	public Player winner;
	
	GameInfo(String gameName){
		this.gameName=gameName;
		this.ID=UUID.randomUUID();
		gameStatus=GameStatusType.GAME_WAITING;
		boardState=null;
		boardBounds=null;
		playerRedMove=null;
		playerGreenMove=null;
		winner=null;
		turnTimeLimit=0;
		gameTimeLimit=0;
	}
	
	public int getTurnTimeLimit() {
		return turnTimeLimit;
	}

	public int getGameTimeLimit() {
		return gameTimeLimit;
	}

	public void setTurnTimeLimit(int turnTimeLimit) {
		this.turnTimeLimit = turnTimeLimit;
	}

	public void setGameTimeLimit(int gameTimeLimit) {
		this.gameTimeLimit = gameTimeLimit;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getGameName() {
		return gameName;
	}

	public UUID getID() {
		return ID;
	}

	public synchronized GameStatusType getGameStatus() {
		return gameStatus;
	}
	
	public synchronized BoardBounds getBoardBounds() {
		return boardBounds;
	}
	
	public synchronized ArrayList<ColPiece> getBoardState() {
		return boardState;
	}
	
	public synchronized void setGameStatus(GameStatusType gameStatus) {
		this.gameStatus = gameStatus;
	}

	public synchronized void setBoardState(ArrayList<ColPiece> boardState) {
		this.boardState = boardState;
	}
	
	public synchronized void setBoardBounds(BoardBounds boardBounds) {
		this.boardBounds = boardBounds;
	}

	public String getPlayerRed(){
		return playerRedMove.player.getLogin();
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((gameName == null) ? 0 : gameName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameInfo other = (GameInfo) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (gameName == null) {
			if (other.gameName != null)
				return false;
		} else if (!gameName.equals(other.gameName))
			return false;
		return true;
	}
	
}
