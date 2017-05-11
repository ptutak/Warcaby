package general;
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
public class Move implements java.io.Serializable {
	private static final long serialVersionUID = -8092964267528656373L;
	public ColPiece moveFrom;
	public ColPiece moveTo;
	Move(){
		moveFrom=null;
		moveTo=null;
	}
	Move(ColPiece moveFrom, ColPiece moveTo){
		this.moveFrom=moveFrom;
		this.moveTo=moveTo;
	}
	@Override
	public String toString(){
		String ret=new String();
		ret+=moveFrom.field.toString()+" r"+moveFrom.piece.row+" c"+moveFrom.piece.column+" ";
		ret+=moveTo.field.toString()+" r"+moveTo.piece.row+" c"+moveTo.piece.column;
		return ret;
	}
}
