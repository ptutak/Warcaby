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
public class TurnInfo {
	private long gameTime;
	private long remainTurnTime;
	private Player activePlayer;
	private boolean timerOn;
	
	public TurnInfo(){
		timerOn=false;
		activePlayer=null;
	}
	public synchronized long getGameTime() {
		return gameTime;
	}
	public synchronized long getRemainTurnTime() {
		return remainTurnTime;
	}
	public synchronized Player getActivePlayer() {
		return activePlayer;
	}
	public synchronized void setGameTime(long gameTime) {
		this.gameTime = gameTime;
	}
	public synchronized void setRemainTurnTime(long remainTurnTime) {
		this.remainTurnTime = remainTurnTime;
	}
	public synchronized void setActivePlayer(Player activePlayer) {
		this.activePlayer = activePlayer;
	}
	public synchronized boolean isTimerOn() {
		return timerOn;
	}
	public synchronized void setTimerOn(boolean timerOn) {
		this.timerOn = timerOn;
	}
}