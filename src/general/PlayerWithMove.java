package general;
import java.io.Serializable;

import enums.GameDecisionType;

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
public class PlayerWithMove implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2382901570392446785L;
	public Player player;
	private GameDecisionType gameDecisionType;
	private Move move;
	private boolean moveDone;
	
	public PlayerWithMove(Player player, Move move) {
		this.player = player;
		this.move = move;
	}

	public GameDecisionType getGameDecisionType() {
		return gameDecisionType;
	}

	public void setGameDecisionType(GameDecisionType gameDecisionType) {
		this.gameDecisionType = gameDecisionType;
	}
	
	public void zeroMove(){
		moveDone=false;
		this.move=null;
	}

	public void setMove(Move move){
		this.move=move;
		moveDone=true;
	}

	public Move getMove() {
		while(!moveDone){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
		moveDone=false;
		return move;
	}

/*	public synchronized void setMove(Move move) {
		while(moveDone)
			try{
				wait();
			} catch (InterruptedException e){}
		moveDone=true;
		this.move = move;
		notifyAll();
	}
	*/
}
