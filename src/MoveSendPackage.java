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
public class MoveSendPackage implements java.io.Serializable {
	private static final long serialVersionUID = 5596422394990642535L;
	public UUID ID;
	public String login;
	public Move move;
	public MoveSendPackage(UUID iD, String login, Move move) {
		ID = iD;
		this.login = login;
		this.move = move;
	}
	
}
