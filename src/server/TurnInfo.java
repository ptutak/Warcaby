package server;

import general.Player;

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
	private long turnLimitTime;
	private long gameLimitTime;
	private long turnStartTime;
	private long gameStartTime;
	private Player activePlayer;
	
	private boolean timerOn;
	
	public TurnInfo(){
		timerOn=false;
		gameLimitTime=0;
		turnLimitTime=0;
		gameTime=0;
		remainTurnTime=0;
		activePlayer=null;
	}
	public synchronized long getTurnStartTime() {
		return turnStartTime;
	}
	public synchronized long getGameStartTime() {
		return gameStartTime;
	}
	public synchronized void setTurnStartTime(long turnStartTime) {
		this.turnStartTime = turnStartTime;
	}
	public synchronized void setGameStartTime(long gameStartTime) {
		this.gameStartTime = gameStartTime;
	}
	public synchronized Player getActivePlayer() {
		return activePlayer;
	}
	public synchronized void setActivePlayer(Player activePlayer) {
		this.activePlayer = activePlayer;
	}
	public synchronized long getTurnLimitTime() {
		return turnLimitTime;
	}
	public synchronized long getGameLimitTime() {
		return gameLimitTime;
	}
	public synchronized void setTurnLimitTime(long turnLimitTime) {
		this.turnLimitTime = turnLimitTime;
	}
	public synchronized void setGameLimitTime(long gameLimitTime) {
		this.gameLimitTime = gameLimitTime;
	}
	public synchronized long getGameTime() {
		return gameTime;
	}
	public synchronized long getRemainTurnTime() {
		return remainTurnTime;
	}
	public synchronized void setGameTime(long gameTime) {
		this.gameTime = gameTime;
	}
	public synchronized void setRemainTurnTime(long remainTurnTime) {
		this.remainTurnTime = remainTurnTime;
	}
	public synchronized boolean isTimerOn() {
		return timerOn;
	}
	public synchronized void setTimerOn(boolean timerOn) {
		this.timerOn = timerOn;
	}
}