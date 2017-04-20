package general;
import java.io.Serializable;

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
public class PlayerInfo implements Serializable {

	private static final long serialVersionUID = 8316210781814688250L;
	public String firstName;
	public String lastName;
	public String nickName;
	public long minorPoints;
	public long mainPoints;
	
	public PlayerInfo(){
		firstName=null;
		lastName=null;
		nickName=null;
		minorPoints=0;
		mainPoints=0;
	}
	
	public PlayerInfo(String firstName, String lastName, String nickName, long minorPoints, long mainPoints) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.nickName = nickName;
		this.minorPoints = minorPoints;
		this.mainPoints = mainPoints;
	}
	

}
