import java.util.UUID;

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

public class Player {
	private final UUID ID;
	private String login;
	
	private String firstName;
	private String lastName;
	
	private Move move;
	private boolean moveDone;
	
	private long mainPoints;
	private long minorPoints;
	
	Player(String login){
		ID=UUID.randomUUID();
		firstName=new String();
		lastName=new String();
		this.login=login;
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
	
	public synchronized UUID getID() {
		return ID;
	}

	public synchronized String getLogin() {
		return login;
	}

	public synchronized String getFirstName() {
		return firstName;
	}

	public synchronized String getLastName() {
		return lastName;
	}

	public synchronized long getMainPoints() {
		return mainPoints;
	}

	public synchronized long getMinorPoints() {
		return minorPoints;
	}

	public synchronized void setLogin(String login) {
		this.login = login;
	}

	public synchronized void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public synchronized void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public synchronized void setMainPoints(long mainPoints) {
		this.mainPoints = mainPoints;
	}

	public synchronized void setMinorPoints(long minorPoints) {
		this.minorPoints = minorPoints;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
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
		Player other = (Player) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}
}
