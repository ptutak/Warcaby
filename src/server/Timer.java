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
class Timer extends Thread {
	private long gameStartTime;
	private long turnStartTime;
	private long turnLimitTime;
	private TurnInfo turnInfo;
		
	public Timer(TurnInfo turnInfo){
		this.turnInfo=turnInfo;
		turnLimitTime=turnInfo.getTurnLimitTime();
	}
	
	public synchronized void nextTurn(){
		turnStartTime=System.currentTimeMillis();
	}
	
	private synchronized long getTurnStartTime(){
		return turnStartTime;
	}
	
	private synchronized void gameStart(){
		gameStartTime=System.currentTimeMillis();
		turnStartTime=gameStartTime;
	}
	
	@Override
	public void run(){
		while (!turnInfo.isTimerOn()) {
			try {
				sleep(10);	
				}  catch (InterruptedException e){e.printStackTrace();	}
		}
		gameStart();
		while (turnInfo.isTimerOn()){
			turnInfo.setGameTime(System.currentTimeMillis()-gameStartTime);
			if (turnLimitTime>0)
				turnInfo.setRemainTurnTime(turnLimitTime-System.currentTimeMillis()+getTurnStartTime());
			else
				turnInfo.setRemainTurnTime(1);
			try {
				sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
