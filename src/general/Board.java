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
import java.io.Console;
import java.util.*;

import enums.FieldType;
import enums.MoveType;
import enums.PieceType;

public class Board{

	private LinkedList<Piece> red=new LinkedList<Piece>();
	private LinkedList<Piece> green=new LinkedList<Piece>();

	private int colStart;
	private int colStop;
	private int rowStart;
	private int rowStop;

	public Board(BoardBounds boardBounds){
		this.rowStart=boardBounds.rowStart;
		this.rowStop=boardBounds.rowStop;
		this.colStop=boardBounds.colStop;
		this.colStart=boardBounds.colStart;
		if (this.colStart<0)
			this.colStart=0;
		if (this.rowStart<0)
			this.rowStart=0;
	}

	public int getColStart() {
		return colStart;
	}
	public int getColStop() {
		return colStop;
	}	
	public int getRowStart() {
		return rowStart;
	}
	public int getRowStop() {
		return rowStop;
	}

	public BoardBounds getBoardBounds(){
		return new BoardBounds(rowStart,rowStop,colStart,colStop);
	}

	public void setBoardBounds(int rowStart,int rowStop,int colStart,int colStop){
		this.rowStart=rowStart;
		if (this.rowStart<0)
			this.rowStart=0;
		this.rowStop=rowStop;
		this.colStart=colStart;
		if (this.colStart<0)
			this.colStart=0;
		this.colStop=colStop;
	}

	private ColPiece checkMove(ArrayList<ColPiece> list,int row, int column){
		for (ColPiece x:list){
			if (x.piece.row==row && x.piece.column==column && x.field==FieldType.FREE){
					System.out.println(x);
					return x;
			}
		}
		return null;
	}

	public ColPiece fieldState(int row, int column){
		for (Piece x:red){
			if (x.row==row && x.column==column)
				return new ColPiece(x,FieldType.RED);
		}
		for (Piece x:green){
			if (x.row==row && x.column==column)
				return new ColPiece(x,FieldType.GREEN);
		}
		return new ColPiece(new Piece(PieceType.BLANK,row,column),FieldType.FREE);
	}


	private ArrayList<ColPiece> validMove(ColPiece movedField){
		Piece x=movedField.piece;
		ArrayList<ColPiece> ret=new ArrayList<ColPiece>();

		if (x.type==PieceType.PAWN){
			if (movedField.field==FieldType.RED){
				if(x.row+1<=rowStop && x.column-1>=colStart && fieldState(x.row+1,x.column-1).field==FieldType.FREE)
					ret.add(new ColPiece(new Piece(PieceType.BLANK ,x.row+1,x.column-1),FieldType.FREE));
				if(x.row+1<=rowStop && x.column+1<=colStop && fieldState(x.row+1,x.column+1).field==FieldType.FREE)
					ret.add(new ColPiece(new Piece(PieceType.BLANK,x.row+1,x.column+1),FieldType.FREE));
			} else if (movedField.field==FieldType.GREEN){
				if(x.row-1>=rowStart && x.column-1>=colStart && fieldState(x.row-1,x.column-1).field==FieldType.FREE)
					ret.add(new ColPiece(new Piece(PieceType.BLANK ,x.row-1,x.column-1),FieldType.FREE));
				if(x.row-1>=rowStart && x.column+1<=colStop && fieldState(x.row-1,x.column+1).field==FieldType.FREE)
					ret.add(new ColPiece(new Piece(PieceType.BLANK,x.row-1,x.column+1),FieldType.FREE));
			}
			
			if(x.row+2<=rowStop && x.column-2>=colStart && fieldState(x.row+2,x.column-2).field==FieldType.FREE) {
				ColPiece toSmash=fieldState(x.row+1,x.column-1);
				if (toSmash.field!=FieldType.FREE)
				if (toSmash.field!=movedField.field){
					ret.add(toSmash);
					ret.add(new ColPiece(new Piece(x.type,x.row+2,x.column-2),FieldType.FREE));
				}
			}					
			if(x.row+2<=rowStop && x.column+2<=colStop && fieldState(x.row+2,(x.column+2)).field==FieldType.FREE){
				ColPiece toSmash=fieldState(x.row+1,(x.column+1));
				if (toSmash.field!=FieldType.FREE)
				if (toSmash.field!=movedField.field){
					ret.add(toSmash);
					ret.add(new ColPiece(new Piece(x.type,x.row+2,(x.column+2)),FieldType.FREE));
				}
			}

			if(x.row-2>=rowStart && x.column-2>=colStart && fieldState(x.row-2,(x.column-2)).field==FieldType.FREE){
				ColPiece toSmash=fieldState(x.row-1,(x.column-1));
				if (toSmash.field!=FieldType.FREE)
				if (toSmash.field!=movedField.field){
					ret.add(toSmash);
					ret.add(new ColPiece(new Piece(x.type,x.row-2,(x.column-2)),FieldType.FREE));
				}
			}

			if(x.row-2>=rowStart && x.column+2<=colStop && fieldState(x.row-2,(x.column+2)).field==FieldType.FREE){
				ColPiece toSmash=fieldState(x.row-1,(x.column+1));
				if (toSmash.field!=FieldType.FREE)
				if (toSmash.field!=movedField.field){
					ret.add(toSmash);
					ret.add(new ColPiece(new Piece(x.type,x.row-2,(x.column+2)),FieldType.FREE));
				}
			}
		}
		else if (x.type==PieceType.QUEEN){
			int maxRowCol=Math.max(colStop-colStart, rowStop-rowStart)+1;
			boolean valid=true;
			for(int dist=1;dist<maxRowCol;++dist){
				if (x.row+dist<=rowStop && x.column-dist>=colStart && fieldState(x.row+dist,(x.column-dist)).field==FieldType.FREE)
					if (valid)
						ret.add(new ColPiece(new Piece(PieceType.BLANK,x.row+dist,(x.column-dist)),FieldType.FREE));
					else
						ret.add(new ColPiece(new Piece(x.type,x.row+dist,(x.column-dist)),FieldType.FREE));
				else {
					ColPiece toSmash=fieldState(x.row+dist,(x.column-dist));
					if (toSmash.field==movedField.field)
						break;
					if (!valid)
						break;
					valid=false;
					if (x.row+dist+1<=rowStop && x.column-dist-1>=colStart && fieldState(x.row+dist+1,(x.column-dist-1)).field==FieldType.FREE)
						ret.add(toSmash);
					else
						break;
				}
			}
			ret.add(new ColPiece(new Piece(PieceType.BLANK,-1,-1),FieldType.FREE));
			valid=true;
			for(int dist=1;dist<maxRowCol;++dist){
				if (x.row+dist<=rowStop && x.column+dist<=colStop && fieldState(x.row+dist,(x.column+dist)).field==FieldType.FREE)
					if (valid)
						ret.add(new ColPiece(new Piece(PieceType.BLANK,x.row+dist,(x.column+dist)),FieldType.FREE));
					else
						ret.add(new ColPiece(new Piece(x.type,x.row+dist,(x.column+dist)),FieldType.FREE));

				else{
					ColPiece toSmash=fieldState(x.row+dist,(x.column+dist));
					if (toSmash.field==movedField.field)
						break;
					if (!valid)
						break;
					valid=false;
					if (x.row+dist+1<=rowStop && x.column+dist+1<=colStop && fieldState(x.row+dist+1,(x.column+dist+1)).field==FieldType.FREE)
						ret.add(toSmash);
					else
						break;
				}
			}
			ret.add(new ColPiece(new Piece(PieceType.BLANK,-1,-1),FieldType.FREE));
			valid=true;
			for(int dist=1;dist<maxRowCol;++dist){
				if (x.row-dist>=rowStart && x.column+dist<=colStop && fieldState(x.row-dist,(x.column+dist)).field==FieldType.FREE)
					if (valid)
						ret.add(new ColPiece(new Piece(PieceType.BLANK,x.row-dist,(x.column+dist)),FieldType.FREE));
					else
						ret.add(new ColPiece(new Piece(x.type,x.row-dist,(x.column+dist)),FieldType.FREE));
				else{
					ColPiece toSmash=fieldState(x.row-dist,(x.column+dist));
					if (toSmash.field==movedField.field)
						break;
					if (!valid)
						break;
					valid=false;
					if (x.row-dist-1>=rowStart && x.column+dist+1<=colStop && fieldState(x.row-dist-1,(x.column+dist+1)).field==FieldType.FREE)
						ret.add(toSmash);
					else
						break;
				}
			}
			ret.add(new ColPiece(new Piece(PieceType.BLANK,-1,-1),FieldType.FREE));
			valid=true;
			for(int dist=1;dist<maxRowCol;++dist){
				if (x.row-dist>=rowStart && x.column-dist>=colStart && fieldState(x.row-dist,(x.column-dist)).field==FieldType.FREE)
					if (valid)
						ret.add(new ColPiece(new Piece(PieceType.BLANK,x.row-dist,(x.column-dist)),FieldType.FREE));
					else
						ret.add(new ColPiece(new Piece(x.type,x.row-dist,(x.column-dist)),FieldType.FREE));
				else{
					ColPiece toSmash=fieldState(x.row-dist,(x.column-dist));
					if (toSmash.field==movedField.field)
						break;
					if (!valid)
						break;
					valid=false;
					if (x.row-dist-1>=rowStart && x.column-dist-1>=colStart && fieldState(x.row-dist-1,(x.column-dist-1)).field==FieldType.FREE)
						ret.add(toSmash);
					else
						break;
				}
			}	
		}
		return ret;
	}

	public boolean setNRowGame(int n){
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
					red.add(new Piece(PieceType.PAWN,i,j));
			else
				for(;j<=colStop;j+=2)
					green.add(new Piece(PieceType.PAWN,i,j));
		}
		return true;
	}

	public Move getMove(int rowFrom, int colFrom, int rowTo, int colTo){
		ColPiece moveFrom=fieldState(rowFrom,colFrom);
		ColPiece moveTo=fieldState(rowTo,colTo);
		return new Move(moveFrom,moveTo);
	}

	public MoveType makeMove(Move move){
		ColPiece colPiece=fieldState(move.moveFrom.piece.row,move.moveFrom.piece.column);
		boolean obligatoryKill=false;
		if (colPiece.field==FieldType.GREEN){
			for (Piece x : green){
				if (checkKill(x))
					obligatoryKill=true;
			}
		} else {
			for (Piece x : red){
				if (checkKill(x))
					obligatoryKill=true;
			}
		}
		return movePiece(colPiece.piece,move.moveTo.piece.row,move.moveTo.piece.column,obligatoryKill);
	}
	
	public void promotePiece(Piece piece){
		Piece boardPiece=fieldState(piece.row,piece.column).piece;
		if (boardPiece.type==PieceType.PAWN)
			boardPiece.type=PieceType.QUEEN;
	}
	
	public boolean checkKill(Piece piece){
		ArrayList<ColPiece> vMove=validMove(fieldState(piece.row,piece.column));
		for (ColPiece x : vMove){
			if (x.field==FieldType.GREEN || x.field==FieldType.RED)
				return true;
		}
		return false;
	}
	public boolean checkKill(ColPiece piece){
		ArrayList<ColPiece> vMove=validMove(piece);
		for (ColPiece x : vMove){
			if (x.field==FieldType.GREEN || x.field==FieldType.RED)
				return true;
		}
		return false;
	}
	
	private ArrayList<ColPiece> checkKillQueen(ColPiece piece,ArrayList<ColPiece> validMove){
		ArrayList<ColPiece> ret=new ArrayList<ColPiece>();
		boolean killFound=false;
		for (ColPiece x:validMove){
			if (x.piece.row==-1){
				killFound=false;
				continue;
			}
			if (killFound){
				if (checkKill(new ColPiece(new Piece(PieceType.QUEEN,x.piece.row,x.piece.column),piece.field))){
					ret.add(x);
				}
			}
			if (x.field!=FieldType.FREE){
				killFound=true;
			}

		}
		return ret;
	}

	private MoveType movePiece(Piece piece,int row, int column,boolean obligatoryKill){
		if (piece==null){
			System.out.println("piece null");
			return MoveType.BAD;
		}
		if (piece.type==PieceType.BLANK){
			System.out.println("piece blank");
			return MoveType.BAD;
		}
		if (fieldState(row,column).field!=FieldType.FREE){
			System.out.println("field != Free");
			return MoveType.BAD;
		}
		ColPiece moveFrom=fieldState(piece.row,piece.column);
		ArrayList<ColPiece> vMove=validMove(moveFrom);
		ColPiece moveTo=checkMove(vMove,row,column);
		if (moveTo!=null){
			if (moveTo.piece.type!=PieceType.BLANK){
				if (piece.type==PieceType.QUEEN){
					ArrayList<ColPiece> queenKillMoveList=checkKillQueen(moveFrom,vMove);
					if (!queenKillMoveList.isEmpty() && !queenKillMoveList.contains(moveTo))
						return MoveType.BAD;
				}
				ColPiece toRemove=null;
				for (ColPiece x:vMove){
					if (x.piece.equals(piece))
						break;
					if (x.field!=FieldType.FREE){
						toRemove=x;
					}
				}
				piece.row=row;
				piece.column=column;
				if (toRemove.field==FieldType.GREEN)
					green.remove(toRemove.piece);
				else
					red.remove(toRemove.piece);
				return MoveType.KILL;
			}else if (!obligatoryKill) {
				piece.row=row;
				piece.column=column;
				return MoveType.MOVE;					
			}
		}
		return MoveType.BAD;
	}

	public ArrayList<ColPiece> getBoardState(){
		ArrayList<ColPiece> ret=new ArrayList<ColPiece>();
		for (Piece x:red)
			ret.add(new ColPiece(x,FieldType.RED));
		for (Piece x:green)
			ret.add(new ColPiece(x,FieldType.GREEN));
		return ret;
	}

	public FieldType checkWinner(){
		if (red.isEmpty())
			return FieldType.GREEN;
		else if (green.isEmpty())
			return FieldType.RED;
		return FieldType.FREE;
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
		for(int i=rowStart;i<=rowStop;++i){
			System.out.print(i);
			if (i<10)
				System.out.print(" |");
			else
				System.out.print("|");
			for(int j=colStart;j<=colStop;++j){
				if(red.contains(new Piece(PieceType.PAWN,i,j)))
					System.out.print(" r ");
				else if (red.contains(new Piece(PieceType.QUEEN,i,j)))
					System.out.print(" R ");
				else if (green.contains(new Piece(PieceType.PAWN,i,j)))
					System.out.print(" g ");
				else if (green.contains(new Piece(PieceType.QUEEN,i,j)))
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
		Board x=new Board(new BoardBounds(0,7,0,7));
		x.setNRowGame(3);
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
				MoveType done=x.makeMove(new Move(toMove,x.fieldState(bRow, bColumn)));
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