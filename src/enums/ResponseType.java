package enums;
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
public enum ResponseType {
	GAME_LIST,
	GAME_CREATED,
	GAME_JOINED,
	GAME_EXISTS,
	GAME_READY,
	GAME_NOT_RUNNING,
	GAME_NOT_READY,
	GAME_END,
	
	GAME_OPPOSITE_USER,
	
	GAME_MOVE_FINAL,
	GAME_MOVE_CONTINUE,
	GAME_OPPOSITE_MOVE_FINAL,
	GAME_OPPOSITE_MOVE_CONTINUE,
	
	USER_REGISTERED,
	USER_EXISTS,
	USER_NOT_REGISTERED,
	USER_RECONNECTED,
	
	WRONG_GAME_NAME,
	USER_NOT_IN_GAME,
	CONNECTION_ENDED
}
