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
public class BoardBounds implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1917974409260687929L;
	public int rowStart;
	public int rowStop;
	public int colStart;
	public int colStop;
	
	public BoardBounds(){
		rowStart=0;
		rowStop=7;
		colStart=0;
		colStop=7;
	}
	public BoardBounds(int rowStart, int rowStop, int colStart, int colStop){
		this.rowStart=rowStart;
		this.rowStop=rowStop;
		this.colStart=colStart;
		this.colStop=colStop;
	}
}
