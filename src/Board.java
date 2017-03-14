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
import java.io.Console;
import java.util.*;

public class Board{
	
	private LinkedList<Piece> Red=new LinkedList<Piece>();
	private LinkedList<Piece> Green=new LinkedList<Piece>();

	private int colStart;
	private int colStop;
	private int rowStart;
	private int rowStop;
	
	Board(BoardBounds boardBounds){
		this.rowStart=boardBounds.rowStart;
		this.rowStop=boardBounds.rowStop;
		this.colStop=boardBounds.colStop;
		this.colStart=boardBounds.colStart;
	}
	
	int getColStart() {
		return colStart;
	}
	int getColStop() {
		return colStop;
	}	
	int getRowStart() {
		return rowStart;
	}
	int getRowStop() {
		return rowStop;
	}
	
	public BoardBounds getBoardBounds(){
		return new BoardBounds(rowStart,rowStop,colStart,colStop);
	}
	
	void setGameBounds(int rowStart,int rowStop,int colStart,int colStop){
		this.rowStart=rowStart;
		this.rowStop=rowStop;
		this.colStart=colStart;
		this.colStop=colStop;
	}
	
	private ColPiece checkMove(ArrayList<ColPiece> list,int row, int column){
		for (ColPiece x:list){
			if(x.piece!=null)
				if (x.piece.row==row && x.piece.column==column && x.field==FType.FREE)
					return x;
		}
		return null;
	}
	
	private ColPiece fieldState(int row, int column){
		for (Piece x:Red){
			if (x.row==row && x.column==column)
				return new ColPiece(x,FType.RED);
		}
		for (Piece x:Green){
			if (x.row==row && x.column==column)
				return new ColPiece(x,FType.GREEN);
		}
		return new ColPiece(null,FType.FREE);
	}
	
	private ArrayList<ColPiece> validMove(Piece x){
		ColPiece toMove=fieldState(x.row,x.column);
		ArrayList<ColPiece> ret=new ArrayList<ColPiece>();
		int maxRowCol=Math.max(colStop-colStart, rowStop-rowStart);
		if (x.type==PType.PAWN){
			if(x.row+1<=rowStop && x.column-1>=colStart && fieldState(x.row+1,x.column-1).field==FType.FREE)
				ret.add(new ColPiece(new Piece(PType.BLANK ,x.row+1,x.column-1),FType.FREE));
			if(x.row+1<=rowStop && x.column+1<=colStop && fieldState(x.row+1,x.column+1).field==FType.FREE)
				ret.add(new ColPiece(new Piece(PType.BLANK,x.row+1,x.column+1),FType.FREE));
			
			if(x.row+2<=rowStop && x.column-2>=colStart && fieldState(x.row+2,x.column-2).field==FType.FREE) {
				ColPiece toSmash=fieldState(x.row+1,x.column-1);
				if (toSmash.field!=toMove.field){
					ret.add(toSmash);
					ret.add(new ColPiece(new Piece(x.type,x.row+2,x.column-2),FType.FREE));
				}
			}					
			if(x.row+2<=rowStop && x.column+2<=colStop && fieldState(x.row+2,(x.column+2)).field==FType.FREE){
				ColPiece toSmash=fieldState(x.row+1,(x.column+1));
				if (toSmash.field!=toMove.field){
					ret.add(toSmash);
					ret.add(new ColPiece(new Piece(x.type,x.row+2,(x.column+2)),FType.FREE));
				}
			}
					
			if(x.row-2>=rowStart && x.column-2>=colStart && fieldState(x.row-2,(x.column-2)).field==FType.FREE){
				ColPiece toSmash=fieldState(x.row-1,(x.column-1));
				if (toSmash.field!=toMove.field){
					ret.add(toSmash);
					ret.add(new ColPiece(new Piece(x.type,x.row-2,(x.column-2)),FType.FREE));
				}
			}
					
			if(x.row-2>=rowStart && x.column+2<=colStop && fieldState(x.row-2,(x.column+2)).field==FType.FREE){
				ColPiece toSmash=fieldState(x.row-1,(x.column+1));
				if (toSmash.field!=toMove.field){
					ret.add(toSmash);
					ret.add(new ColPiece(new Piece(x.type,x.row-2,(x.column+2)),FType.FREE));
				}
			}
		}
		else {
			boolean valid=true;
			for(int dist=1;dist<maxRowCol;++dist){
				if (x.row+dist<=rowStop && x.column-dist>=colStart && fieldState(x.row+dist,(x.column-dist)).field==FType.FREE)
					if (valid)
						ret.add(new ColPiece(new Piece(PType.BLANK,x.row+dist,(x.column-dist)),FType.FREE));
					else
						ret.add(new ColPiece(new Piece(x.type,x.row+dist,(x.column-dist)),FType.FREE));
				else{
					ColPiece toSmash=fieldState(x.row+dist,(x.column-dist));
					if (toSmash.field==toMove.field)
						break;
					if (!valid)
						break;
					valid=false;
					if (x.row+dist+1<=rowStop && x.column-dist-1>=colStart && fieldState(x.row+dist+1,(x.column-dist-1)).field==FType.FREE)
						ret.add(toSmash);
					else
						break;
				}
			}
			valid=true;
			for(int dist=1;dist<maxRowCol;++dist){
				if (x.row+dist<=rowStop && x.column+dist<=colStop && fieldState(x.row+dist,(x.column+dist)).field==FType.FREE)
					if (valid)
						ret.add(new ColPiece(new Piece(PType.BLANK,x.row+dist,(x.column+dist)),FType.FREE));
					else
						ret.add(new ColPiece(new Piece(x.type,x.row+dist,(x.column+dist)),FType.FREE));
					
				else{
					ColPiece toSmash=fieldState(x.row+dist,(x.column+dist));
					if (toSmash.field==toMove.field)
						break;
					if (!valid)
						break;
					valid=false;
					if (x.row+dist+1<=rowStop && x.column+dist+1<=colStop && fieldState(x.row+dist+1,(x.column+dist+1)).field==FType.FREE)
						ret.add(toSmash);
					else
						break;
				}
			}
			valid=true;
			for(int dist=1;dist<maxRowCol;++dist){
				if (x.row-dist>=rowStart && x.column+dist<=colStop && fieldState(x.row-dist,(x.column+dist)).field==FType.FREE)
					if (valid)
						ret.add(new ColPiece(new Piece(PType.BLANK,x.row-dist,(x.column+dist)),FType.FREE));
					else
						ret.add(new ColPiece(new Piece(x.type,x.row-dist,(x.column+dist)),FType.FREE));
				else{
					ColPiece toSmash=fieldState(x.row-dist,(x.column+dist));
					if (toSmash.field==toMove.field)
						break;
					if (!valid)
						break;
					valid=false;
					if (x.row-dist-1>=rowStart && x.column+dist+1<=colStop && fieldState(x.row-dist-1,(x.column+dist+1)).field==FType.FREE)
						ret.add(toSmash);
					else
						break;
				}
			}
			valid=true;
			for(int dist=1;dist<maxRowCol;++dist){
				if (x.row-dist>=rowStart && x.column-dist>=colStart && fieldState(x.row-dist,(x.column-dist)).field==FType.FREE)
					if (valid)
						ret.add(new ColPiece(new Piece(PType.BLANK,x.row-dist,(x.column-dist)),FType.FREE));
					else
						ret.add(new ColPiece(new Piece(x.type,x.row-dist,(x.column-dist)),FType.FREE));
				else{
					ColPiece toSmash=fieldState(x.row-dist,(x.column-dist));
					if (toSmash.field==toMove.field)
						break;
					if (!valid)
						break;
					valid=false;
					if (x.row-dist-1>=rowStart && x.column-dist-1>=colStart && fieldState(x.row-dist-1,(x.column-dist-1)).field==FType.FREE)
						ret.add(toSmash);
					else
						break;
				}
			}	
		}
		return ret;
	}
	
	boolean setNRowGame(int n){
		if ((rowStop-rowStart+1)/2<=n)
			return false;
		for (int i=rowStart;i<=rowStop;++i){
			if (i>=rowStart+n && i<=rowStop-n)
				continue;
			int j;
			if (i%2==1)
				j=colStart;
			else
				j=colStart+1;
			if (i<(rowStart+rowStop)/2)
				for(;j<=colStop;j+=2)
					Red.add(new Piece(PType.PAWN,i,j));
			else
				for(;j<=colStop;j+=2)
					Green.add(new Piece(PType.PAWN,i,j));
		}
		return true;
	}
		
	
	MType movePiece(Piece piece,int row, int column){
		if (fieldState(row,column).field!=FType.FREE)
			return MType.BAD;
		ArrayList<ColPiece> vMove=validMove(piece);
		ColPiece move=checkMove(vMove,row,column);
		if (move!=null){
			if (move.piece.type!=PType.BLANK){
				ColPiece toRemove=null;
				for (ColPiece x:vMove){
					if (x==move)
						break;
					if (x.field!=FType.FREE)
						toRemove=x;
				}
				piece.row=row;
				piece.column=column;
				if (toRemove.field==FType.GREEN)
					Green.remove(toRemove.piece);
				else
					Red.remove(toRemove.piece);
				return MType.KILL;
			}
			else{
				piece.row=row;
				piece.column=column;
				return MType.MOVE;					
			}
		}
		return MType.BAD;
	}
	
	ArrayList<ColPiece> boardState(){
		ArrayList<ColPiece> ret=new ArrayList<ColPiece>();
		for (Piece x:Red)
			ret.add(new ColPiece(x,FType.RED));
		for (Piece x:Green)
			ret.add(new ColPiece(x,FType.GREEN));
		return ret;
	}

	
	
	
	
	
	
	
	void print(){
		System.out.print("   ");
		for (int i=colStart;i<=colStop;++i)
			System.out.print(" "+i+" ");
		System.out.println("");
		System.out.print("  -");
		for(int i=colStart;i<=colStop;++i)
			System.out.print("---");
		System.out.println("-");
		for(int i=rowStop;i>=rowStart;--i){
			System.out.print(i);
			if (i<10)
				System.out.print(" |");
			else
				System.out.print("|");
			for(int j=colStart;j<=colStop;++j){
				if(Red.contains(new Piece(PType.PAWN,i,j)))
					System.out.print(" r ");
				else if (Red.contains(new Piece(PType.QUEEN,i,j)))
					System.out.print(" R ");
				else if (Green.contains(new Piece(PType.PAWN,i,j)))
					System.out.print(" g ");
				else if (Green.contains(new Piece(PType.QUEEN,i,j)))
					System.out.print(" G ");
				else
					System.out.print(" _ ");
			}
			System.out.println("|");
		}
		System.out.print("  -");
		for(int i=colStart;i<=colStop;++i)
			System.out.print("---");
		System.out.println("-");
	}

	public static void main(String[] args){
		Board x=new Board(new BoardBounds(1,10,1,8));
		x.setNRowGame(4);
		x.print();
		while(true){
			Console cons=System.console();
			String tmp=cons.readLine().trim();
			if (tmp.equals(new String("koniec")))
				break;
			String[] coord=tmp.split(" ");
			if (coord.length<4){
				System.out.println("Z³e koordynaty");
				continue;				
			}
			int aRow=Integer.parseInt(coord[0]);
			int aColumn=Integer.parseInt(coord[1]);
			int bRow=Integer.parseInt(coord[2]);
			int bColumn=Integer.parseInt(coord[3]);
			
			ColPiece toMove=x.fieldState(aRow,aColumn);
			if (toMove.piece!=null){
				MType done=x.movePiece(toMove.piece, bRow, bColumn);
				switch(done){
				case BAD:
					System.out.println("Zly ruch");
					break;
				case MOVE:
					System.out.println("Ruch z poz.: "+aRow+aColumn+"\nNa poz.: "+bRow+bColumn);
					break;
				case KILL:
					System.out.println("Ruch z poz.: "+aRow+aColumn+"\nNa poz.: "+bRow+bColumn+"\nZbicie!");
				}
			}
			x.print();
		}
	}
	
	
	
	
}