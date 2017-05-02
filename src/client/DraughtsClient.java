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
import enums.PackageLimiterType;
import enums.PlayerMoveType;
import enums.ResponseType;
import general.BoardInfo;
import general.Move;
import general.Player;
import general.ServerResponsePackage;
import general.UserCommandPackage;
import server.GameInfo;

public class DraughtsClient {

	private String serverIp=null;
	private int serverPort=0;
	private SocketChannel socketChannel=null;
	private final long responseTime=1500;
	private final int serverResponseMultiTime=3;

	private final int BUFFSIZE=4096;
	private ByteBuffer readBuffer=ByteBuffer.allocate(BUFFSIZE);
	private ByteBuffer safeBuffer=ByteBuffer.allocate(16*BUFFSIZE);
	private ByteBuffer writeBuffer=ByteBuffer.allocate(BUFFSIZE);

	private Player player=null;
	private String gameName=null;
	private UUID gameID=null;
	private PlayerMoveType playerMoveType=null;
	private Move move=null;

	private String oppositePlayer;

	public String getOppositePlayer() {
		return oppositePlayer;
	}

	public boolean establishConnection(String ip, int port){
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			boolean connected=socketChannel.connect(new InetSocketAddress(ip, port));
			int i=0;
			while (!connected){
				TimeUnit.MILLISECONDS.sleep(responseTime);
				connected=socketChannel.finishConnect();
				if (i>serverResponseMultiTime)
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
	
	public ResponseType joinGame(String gameName){
		if (socketChannel!=null){
			this.gameName=gameName;
			writeCommand(socketChannel,CommandType.JOIN_GAME,null);
			ServerResponsePackage response=checkResponse(socketChannel);
			if (response!=null){
				if (response.response==ResponseType.GAME_JOINED){
					oppositePlayer=(String)response.object;
					return ResponseType.GAME_JOINED;
				} else if (response.response==ResponseType.WRONG_GAME_NAME){
					return ResponseType.WRONG_GAME_NAME;
				}
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
					this.gameID=(UUID)response.object;
					return ResponseType.GAME_CREATED;
				} else if (response.response==ResponseType.GAME_EXISTS){
					this.gameName=null;
					return ResponseType.GAME_EXISTS;
				}
			}
		}
		return null;
	}
	
	public GameInfo[] getGameList(){
		if (socketChannel!=null){
			writeCommand(socketChannel,CommandType.AVAILABLE_GAMES,null);
			ServerResponsePackage response=checkResponse(socketChannel);
			if (response!=null){
				if (response.response==ResponseType.GAME_LIST){
					return (GameInfo[]) response.object;
				}
			}
			return null;
		}
		return null;
	}

	private ServerResponsePackage checkResponse(SocketChannel socketChannel){
		System.out.println("check func");
		if (!socketChannel.isOpen())
			return null;
		//		ByteBuffer safeBuffer=ByteBuffer.allocate(BUFFSIZE);
		ServerResponsePackage response=null;
		long startTime=System.currentTimeMillis();
		while (System.currentTimeMillis()-startTime<responseTime) {
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
	


	public void closeConnection(){
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
		try {
			socketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socketChannel=null;
		establishConnection(serverIp,serverPort);
		registerUser(player.getLogin());
	}

	private void writeCommand(SocketChannel socketChannel,CommandType command,Object object){
		UserCommandPackage commandPackage=new UserCommandPackage();
		commandPackage.commandType=command;
		commandPackage.player=player;
		commandPackage.attachment=object;
		commandPackage.gameName=gameName;
		commandPackage.gameID=gameID;
		commandPackage.move=move;
		commandPackage.playerMoveType=playerMoveType;
		try {
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(bos);
			oos.writeObject(PackageLimiterType.PACKAGE_BEGIN);
			oos.writeObject(commandPackage);
			oos.writeObject(PackageLimiterType.PACKAGE_END);
			oos.flush();
			writeBuffer.putInt(bos.toByteArray().length);
			writeBuffer.put(bos.toByteArray());
			writeBuffer.flip();
			socketChannel.write(writeBuffer);
			writeBuffer.clear();
		} catch (ClosedChannelException e){
			reconnect();
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		DraughtsClient client=new DraughtsClient();
		boolean response=client.establishConnection("127.0.0.1", 50000);
		System.out.println(response);
		ResponseType resp=client.registerUser("piotr");
		System.out.println(resp);
		//		client.closeConnection();
		while(true);
	}
}
