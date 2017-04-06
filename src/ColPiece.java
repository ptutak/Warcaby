import enums.FType;

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
class ColPiece implements java.io.Serializable{

	private static final long serialVersionUID = 5914182075578536563L;
	public Piece piece;
	public FType field;
	ColPiece(Piece piece,FType field){
		this.piece=piece;
		this.field=field;
	}
}