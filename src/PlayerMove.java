import enums.PlayerMoveType;

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
public class PlayerMove {
	public Player player;
	private PlayerMoveType playerMoveType;
	private Move move;
	private boolean moveDone;
	
	public PlayerMove(Player player, Move move) {
		this.player = player;
		this.move = move;
	}

	public synchronized PlayerMoveType getPlayerMoveType() {
		return playerMoveType;
	}

	public synchronized void setPlayerMoveType(PlayerMoveType playerMoveType) {
		this.playerMoveType = playerMoveType;
	}

	public synchronized Move getMove() {
		while(!moveDone)
			try {
				wait();
			} catch (InterruptedException e){}
		moveDone=false;
		notifyAll();
		return move;
	}

	public synchronized void setMove(Move move) {
		while(moveDone)
			try{
				wait();
			} catch (InterruptedException e){}
		moveDone=true;
		this.move = move;
		notifyAll();
	}
	
}
