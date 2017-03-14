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
		
	void setTurnLimitTime(long turnLimitTime) {
		if (turnLimitTime<0)
			this.turnLimitTime=0;
		else
			this.turnLimitTime = turnLimitTime;
	}

	Timer(TurnInfo turnInfo){
		this.turnInfo=turnInfo;
	}
	
	Timer(long turnLimitTime){
		if (turnLimitTime<0)
			this.turnLimitTime=0;
		else
			this.turnLimitTime=turnLimitTime;
	}
	
	void nextTurn(){
		turnStartTime=System.currentTimeMillis();
	}
	
	void gameStart(){
		gameStartTime=System.currentTimeMillis();
		turnStartTime=gameStartTime;
	}
	
	@Override
	public void run(){
		Player prevPlayer=turnInfo.getActivePlayer();
		while (!turnInfo.isTimerOn()) {
			try {
				sleep(10);	
				}  catch (InterruptedException e){	}
			}
		gameStart();
		while (turnInfo.isTimerOn()){
			Player nextPlayer=turnInfo.getActivePlayer();
			if (prevPlayer!=nextPlayer){
				prevPlayer=nextPlayer;
				nextTurn();
			}
			turnInfo.setGameTime(System.currentTimeMillis()-gameStartTime);
			if (turnLimitTime>0)
				turnInfo.setRemainTurnTime(turnLimitTime-System.currentTimeMillis()+turnStartTime);
			else
				turnInfo.setRemainTurnTime(1);
		}
	}
}
