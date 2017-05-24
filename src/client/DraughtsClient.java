package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import enums.CommandType;
import enums.FieldType;
import enums.PackageLimiterType;
import enums.GameDecisionType;
import enums.ResponseType;
import general.Board;
import general.BoardInfo;
import general.GameInfo;
import general.Move;
import general.Player;
import general.ServerResponsePackage;
import general.UserCommandPackage;

public class DraughtsClient {

	private String serverIp=null;
	private int serverPort=0;
	private SocketChannel socketChannel=null;
	private final long RESPONSE_TIME=250;
	private final int SERVER_RESPONSE_MULTI_TIME=3;

	private final int BUFF_SIZE=4096;
	private ByteBuffer readBuffer=ByteBuffer.allocate(BUFF_SIZE);
	private ByteBuffer safeBuffer=ByteBuffer.allocate(16*BUFF_SIZE);
	private ByteBuffer writeBuffer=ByteBuffer.allocate(BUFF_SIZE);

	private Player player=null;


	private FieldType playerCol=null;
	private String gameName=null;
	private UUID gameID=null;
	private BoardInfo boardInfo=null;
	private GameDecisionType gameDecision=null;
	private Move gameMove=null;

	private Board board=null;
	
	private String oppositePlayer=null;

	private Thread waitingThread=null;
	private boolean gameReady=false;
	private boolean gameRunning=false;
	
	private ResponseType serverResponse=null;
	private Move responseMove=null;
	private GameInfo endedGame=null;

	public synchronized GameInfo getEndedGame() {
		return endedGame;
	}
	
	public synchronized Player getPlayer() {
		return player;
	}

	public synchronized void setEndedGame(GameInfo endedGame) {
		this.endedGame = endedGame;
	}

	private Runnable waitForGameReady=new Runnable(){
		@Override
		public void run(){
			while (socketChannel!=null && !gameReady){
				ServerResponsePackage response=checkResponse(socketChannel);
				if (response!=null){
					if (response.response==ResponseType.GAME_READY){
						setOppositePlayer((String)response.attachment);
						waitingThread=null;
						gameReady=true;
						waitForGameMove();
						break;
					} else if(response.response==ResponseType.GAME_ABORT){
						waitingThread=null;
						resetGame();
						break;
					}
				}
				try {
					Thread.sleep(RESPONSE_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	private void waitForGameReady(){
		if (waitingThread==null){
			waitingThread=new Thread(waitForGameReady);
			waitingThread.start();
		}
	}
	
	private void resetGame(){
		gameRunning=false;
		gameReady=false;
		responseMove=null;
		oppositePlayer=null;
		playerCol=null;
		gameName=null;
		gameID=null;
		boardInfo=null;
		gameDecision=null;
		gameMove=null;
	}
	
	private Runnable waitForGameMove=new Runnable(){
		@Override
		public void run(){
			while (socketChannel!=null && waitingThread!=null){
				ServerResponsePackage response=checkResponse(socketChannel);
				if (response!=null){
					switch(response.response){
					case GAME_STARTED:
						gameRunning=true;
					case GAME_OPPOSITE_USER_READY:
						break;
					case GAME_MOVE_FINAL:
					case GAME_MOVE_CONTINUE:
						board.makeMove(gameMove);
//						System.out.println(responseMoveType);
						break;
					case GAME_MOVE_PROMOTE:
						board.makeMove(gameMove);
						board.promotePiece(gameMove.moveTo.piece);
						break;
					case GAME_OPPOSITE_MOVE_FINAL:
					case GAME_OPPOSITE_MOVE_CONTINUE:
						responseMove=(Move)response.attachment;
						board.makeMove(responseMove);
//						System.out.println(responseMove);
						break;
					case GAME_OPPOSITE_MOVE_PROMOTE:
						responseMove=(Move)response.attachment;
						board.makeMove(responseMove);
						board.promotePiece(responseMove.moveTo.piece);
						break;
					case GAME_END:
						setEndedGame((GameInfo)response.attachment);
					case GAME_ABORT:
						waitingThread=null;
						resetGame();
						break;
					default:
						break;
					}
					setServerResponse(response.response);
					System.out.println(response.response);
				}
				try {
					Thread.sleep(RESPONSE_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	private void waitForGameMove(){
		if (waitingThread==null){
			waitingThread=new Thread(waitForGameMove);
			waitingThread.start();
		}
	}

	public Board getBoard() {
		return board;
	}

	public FieldType getPlayerCol() {
		return playerCol;
	}

	public BoardInfo getBoardInfo() {
		return boardInfo;
	}

	public synchronized String getOppositePlayer() {
		return oppositePlayer;
	}
	
	public synchronized ResponseType getServerResponse() {
		ResponseType resp=serverResponse;
		serverResponse=null;
		return resp;
	}

	public synchronized void setServerResponse(ResponseType serverResponse) {
		this.serverResponse = serverResponse;
	}

	public synchronized void setOppositePlayer(String oppositePlayer){
		this.oppositePlayer=oppositePlayer;
	}

	public String getPlayerName(){
		return player.getLogin();
	}

	public boolean establishConnection(String ip, int port){
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			boolean connected=socketChannel.connect(new InetSocketAddress(ip, port));
			int i=0;
			while (!connected){
				TimeUnit.MILLISECONDS.sleep(RESPONSE_TIME);
				connected=socketChannel.finishConnect();
				if (i>SERVER_RESPONSE_MULTI_TIME)
					break;
				i++;
			}
			if (connected){
				serverIp=ip;
				serverPort=port;
				return true;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		socketChannel=null;
		return false;
	}

	public ResponseType registerUser(String login){
		if (socketChannel==null)
			return null;
		player=new Player(login);
		writeCommand(socketChannel,CommandType.REGISTER_NEW_USER,null);
		ServerResponsePackage response=checkResponse(socketChannel);
		if (response!=null)
			if (response.response==ResponseType.USER_REGISTERED){
				//				player=(Player)response.object;
				return ResponseType.USER_REGISTERED;
			} else if(response.response==ResponseType.USER_EXISTS)
				return ResponseType.USER_EXISTS;
		return null;
	}
	
	public void startGame(){
		if (socketChannel==null)
			return;
		writeCommand(socketChannel,CommandType.START_GAME,null);
//		waitForGameMove();
	}

	public ResponseType joinGame(String gameName){
		if (socketChannel!=null){
			this.gameName=gameName;
			writeCommand(socketChannel,CommandType.JOIN_GAME,null);
			ServerResponsePackage response=checkResponse(socketChannel);
			if (response!=null){
				if (response.response==ResponseType.GAME_JOINED){
					GameInfo info=(GameInfo)response.attachment;
					oppositePlayer=info.getPlayerRed();
					boardInfo=new BoardInfo();
					boardInfo.boardBounds=info.getBoardBounds();
					boardInfo.rowNumber=info.getRowNumber();
					boardInfo.turnTimeLimit=info.getTurnTimeLimit();
					boardInfo.gameTimeLimit=info.getGameTimeLimit();
					gameID=info.getID();
					playerCol=FieldType.GREEN;
					initBoard();
					waitForGameMove();
				}
				return response.response;
			}
		}
		return null;
	}

	public ResponseType newGame(String gameName, int rowNumber, int rowColumns){
		if (socketChannel!=null){
			this.gameName=gameName;
			BoardInfo info=new BoardInfo();
			writeCommand(socketChannel, CommandType.NEW_GAME, info);
			ServerResponsePackage response=checkResponse(socketChannel);
			if (response!=null){
				if (response.response==ResponseType.GAME_CREATED){
					this.gameID=(UUID)response.attachment;
					boardInfo=info;
					playerCol=FieldType.RED;
					initBoard();
					waitForGameReady();
				} else if (response.response==ResponseType.GAME_EXISTS){
					this.gameName=null;
				}
				return response.response;
			}
		}
		return null;
	}
	
	public void gameSurrender(){
		if (socketChannel!=null){
			gameDecision=GameDecisionType.SURRENDER;
			gameMove=null;
			if (gameRunning)
				writeCommand(socketChannel,CommandType.GAME_MOVE,null);
			else
				writeCommand(socketChannel,CommandType.EXIT_GAME,null);
		}
	}

	private void initBoard(){
		if (boardInfo!=null){
			board=new Board(boardInfo.boardBounds);
			board.setNRowGame(boardInfo.rowNumber);
		}
	}

	public GameInfo[] getGameList(){
		if (socketChannel!=null){
			writeCommand(socketChannel,CommandType.AVAILABLE_GAMES,null);
			ServerResponsePackage response=checkResponse(socketChannel);
			if (response!=null){
				if (response.response==ResponseType.GAME_LIST){
					return (GameInfo[]) response.attachment;
				}
			}
			return null;
		}
		return null;
	}

	public ResponseType endConnection(){
		if (socketChannel!=null){
			if (waitingThread!=null)
				waitingThread=null;
			try {
				Thread.sleep(2*RESPONSE_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			writeCommand(socketChannel,CommandType.END_CONNECTION,null);
			ServerResponsePackage response=checkResponse(socketChannel);
			closeConnection();
			if (response!=null)
				return response.response;
		}
		return null;
	}

	public void move(int rowFrom, int colFrom, int rowTo, int colTo){
		if (socketChannel!=null){
			gameMove=board.getMove(rowFrom, colFrom, rowTo, colTo);
			gameDecision=GameDecisionType.MOVE;
			writeCommand(socketChannel,CommandType.GAME_MOVE,null);
		}
	}

	private void closeConnection(){
		if (socketChannel!=null)
			try {
				socketChannel.close();
				socketChannel=null;
				System.out.println("Socket Channel closed");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void reconnect(){
		closeConnection();
		socketChannel=null;
		establishConnection(serverIp,serverPort);
		registerUser(player.getLogin());
	}
	private synchronized ServerResponsePackage checkResponse(SocketChannel socketChannel){
//		System.out.println("check func");
		if (socketChannel==null)
			return null;
		if (!socketChannel.isOpen())
			return null;
		//		ByteBuffer safeBuffer=ByteBuffer.allocate(BUFFSIZE);
		ServerResponsePackage response=null;
		long startTime=System.currentTimeMillis();
		while (System.currentTimeMillis()-startTime<RESPONSE_TIME) {
			try {
				long n = socketChannel.read(readBuffer);
				//				System.out.println(n);
				if (n > 0) {
					readBuffer.flip();
					byte[] tmpBuffer=new byte[readBuffer.remaining()];
					readBuffer.get(tmpBuffer);
					safeBuffer.put(tmpBuffer);
					readBuffer.clear();
					ByteBuffer copyBuffer=safeBuffer.duplicate();
					copyBuffer.flip();
					int packageSize=0;
					if (copyBuffer.remaining()>=4)
						packageSize=copyBuffer.getInt();
					else
						continue;
					if (copyBuffer.remaining()<packageSize)
						continue;

					tmpBuffer=new byte[copyBuffer.remaining()];
					copyBuffer.get(tmpBuffer);

					ByteArrayInputStream bis=new ByteArrayInputStream(tmpBuffer);
					ObjectInputStream ois=new ObjectInputStream(bis);
					PackageLimiterType begin=(PackageLimiterType)ois.readObject();

					if(begin==PackageLimiterType.PACKAGE_BEGIN){
						//						System.out.println("package begin");
						response=(ServerResponsePackage)ois.readObject();
						//						System.out.println(response);
						PackageLimiterType end=(PackageLimiterType)ois.readObject();
						if (end==PackageLimiterType.PACKAGE_END){
							System.out.println("package end");
							break;
						}	
					}
				}
				else if (n==-1){
					socketChannel.close();
					System.out.println("Service Channel Closed");
					response=null;
					break;
				}
			} catch (ClassNotFoundException e) {
				System.out.println("CNF");
			} catch (EOFException e){
				System.out.println("EOF");
			} catch (ClosedChannelException e){
				System.out.println("CCE");
				try {
					socketChannel.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			} catch (IOException e) {
				System.out.println("IOE");
				//				e.printStackTrace();
				try {
					socketChannel.close();
					System.out.println("Service Channel Closed");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}	
		}
		readBuffer.clear();
		safeBuffer.clear();
		return response;
	}



	private synchronized void writeCommand(SocketChannel socketChannel,CommandType command,Object object){
		UserCommandPackage commandPackage=new UserCommandPackage();
		commandPackage.command=command;
		commandPackage.player=player;
		commandPackage.attachment=object;
		commandPackage.gameName=gameName;
		commandPackage.gameID=gameID;
		commandPackage.move=gameMove;
		commandPackage.gameDecision=gameDecision;
		try {
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(bos);
			oos.writeObject(PackageLimiterType.PACKAGE_BEGIN);
			oos.writeObject(commandPackage);
			oos.writeObject(PackageLimiterType.PACKAGE_END);
			oos.flush();
			System.out.println(bos.toByteArray().length);
			writeBuffer.putInt(bos.toByteArray().length);
			writeBuffer.put(bos.toByteArray());
			writeBuffer.flip();
			socketChannel.write(writeBuffer);

		} catch (ClosedChannelException e){
			reconnect();
			
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			writeBuffer.clear();
		}
		
	}
}
