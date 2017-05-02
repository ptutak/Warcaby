package server;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import enums.ResponseType;
import general.Player;
import general.PlayerMove;
import general.ServerResponsePackage;
import general.UserCommandPackage;
import general.BoardInfo;
import enums.GameStatusType;
import enums.PackageLimiterType;


public class DraughtsServer extends Thread {

	private String host;
	private int port;

	final int BUFFSIZE=4096;
	ByteBuffer readBuffer=ByteBuffer.allocate(BUFFSIZE);
	ByteBuffer safeBuffer=ByteBuffer.allocate(BUFFSIZE);

	long responseTime=500;

	private ServerSocketChannel serverSocketChannel = null;
	private Selector selector = null;

	private boolean serverState;

	private HashSet<String> playerLoginSet=new HashSet<String>();
	private HashMap<Player,PlayerService> playerMap=new HashMap<Player,PlayerService>();
	private HashMap<String,GameInfo> gameMap=new HashMap<String,GameInfo>();


	public synchronized boolean getServerState() {
		return serverState;
	}

	public synchronized void setServerState(boolean serverState) {
		this.serverState = serverState;
	}

	public DraughtsServer(String host, int port ) {
		this.host=host;
		this.port=port;
	}

	public void establishConnection(){
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(host, port));
			selector = Selector.open();
			serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Connection established");
	}

	public void run(){
		serverState=true;
		serviceConnections();
	}
	
	private void gameEnd(GameInfo game){
		
		
	}
	
	private boolean removePlayer(SocketChannel channel){
		for (PlayerService x:playerMap.values()){
			if (x.channel.equals(channel)){
				String[] ks = new String[gameMap.size()];
				gameMap.keySet().toArray(ks);
				for (String game:ks){
					if (gameMap.containsKey(game))
						if ((gameMap.get(game).playerRedMove.player.equals(x.playerMove.player)) || (gameMap.get(game).playerGreenMove!=null && gameMap.get(game).playerGreenMove.player.equals(x.playerMove.player))) {
							gameEnd(gameMap.get(game));
							gameMap.remove(game);
						} 
				}
				if (playerLoginSet.remove(x.playerMove.player.getLogin()))
					return playerMap.remove(x.playerMove.player, x);
			}
		}
		return false;
	}

	private void serviceConnections() {
		System.out.println("ServiceConnections");
		while(getServerState()) {
			try {
				selector.select();

				Set<SelectionKey> selectorKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = selectorKeys.iterator();

				while(iter.hasNext()) {  
					SelectionKey key = (SelectionKey) iter.next();
					if (!key.isValid()){
						key.cancel();
						System.out.println("Key canceled");
					} else if (key.isAcceptable()) { 
						SocketChannel newChannel = serverSocketChannel.accept();
						newChannel.configureBlocking(false);
						newChannel.register(selector, SelectionKey.OP_READ);
					} else if (key.isReadable()) {  
						SocketChannel serviceChannel = (SocketChannel) key.channel();
						serviceRequest(serviceChannel);
					}

					iter.remove();
				}
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	private UserCommandPackage checkCommand(SocketChannel socketChannel){

				System.out.println("Check func begin");
		if (!socketChannel.isOpen())
			return null;
		UserCommandPackage command=null;
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
						command=(UserCommandPackage)ois.readObject();
						//						System.out.println(command);
						PackageLimiterType end=(PackageLimiterType)ois.readObject();
						if (end==PackageLimiterType.PACKAGE_END){
														System.out.println("package end");
							break;
						}	
					}
				}
				else if (n==-1){
					removePlayer(socketChannel);
					socketChannel.close();
					System.out.println("Service Channel Closed");
					command=null;
					break;
				}
			} catch (ClassNotFoundException e) {
				System.out.println("CNF");
			} catch (EOFException e){
				System.out.println("EOF");
			} catch (ClosedChannelException e){
				System.out.println("CCE");
				try {
					removePlayer(socketChannel);
					socketChannel.close();
					System.out.println("Service Channel Closed");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				command=null;
				break;
			} catch (IOException e) {
				System.out.println("IOE");
				//				e.printStackTrace();
				try {
					removePlayer(socketChannel);
					socketChannel.close();
					System.out.println("Service Channel Closed");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				command=null;
				break;
			}	
		}
		safeBuffer.clear();
		readBuffer.clear();
		return command;
	}


	private void serviceRequest(SocketChannel serviceChannel) {
		UserCommandPackage command=checkCommand(serviceChannel);
		if (command==null){
		}
		else{
			switch(command.commandType){
			case REGISTER_NEW_USER:
				if (playerLoginSet.contains(command.player.getLogin())){
					writeResponse(serviceChannel,ResponseType.USER_EXISTS,null);
					System.out.println(ResponseType.USER_EXISTS);
				} else{
					playerMap.put(command.player,new PlayerService(serviceChannel, new PlayerMove(command.player,null)));
					playerLoginSet.add(command.player.getLogin());
					writeResponse(serviceChannel,ResponseType.USER_REGISTERED,null);
					System.out.println(ResponseType.USER_REGISTERED);
				}
				break;
			case USER_RECONNECT:
				if (playerLoginSet.contains(command.player.getLogin())){
					if (playerMap.containsKey(command.player)){
						playerMap.get(command.player).channel=serviceChannel;
						writeResponse(serviceChannel,ResponseType.USER_RECONNECTED,null);
						System.out.println(ResponseType.USER_RECONNECTED);
					} else{
						writeResponse(serviceChannel,ResponseType.USER_NOT_REGISTERED,null);
						System.out.println(ResponseType.USER_NOT_REGISTERED);
					}
				} else{
					writeResponse(serviceChannel,ResponseType.USER_NOT_REGISTERED,null);
					System.out.println(ResponseType.USER_NOT_REGISTERED);
				}
				break;
			case NEW_GAME:
				if (playerMap.containsKey(command.player)){
					if (!gameMap.containsKey(command.gameName)){
						GameInfo newGame=new GameInfo(command.gameName);
						BoardInfo boardInfo=(BoardInfo)command.attachment;
						newGame.setBoardBounds(boardInfo.boardBounds);
						newGame.setRowNumber(boardInfo.rowNumber);
						newGame.playerRedMove=playerMap.get(command.player).playerMove;
						gameMap.put(command.gameName, newGame);
						writeResponse(serviceChannel,ResponseType.GAME_CREATED,newGame.getID());
						System.out.println(ResponseType.GAME_CREATED);
					}
					else{
						writeResponse(serviceChannel,ResponseType.GAME_EXISTS,null);
						System.out.println(ResponseType.GAME_EXISTS);
					}
				}
				break;
			case AVAILABLE_GAMES:
				if (serviceChannel.isOpen()){
					GameInfo[] gameList=gameMap.values().toArray(new GameInfo[0]);
					writeResponse(serviceChannel,ResponseType.GAME_LIST,gameList);
					System.out.println(ResponseType.GAME_LIST);
				}
				break;
			case JOIN_GAME:
				if (playerMap.containsKey(command.player)){
					if (gameMap.containsKey(command.gameName) && gameMap.get(command.gameName).getID().equals(command.gameID)){
						if (gameMap.get(command.gameName).getGameStatus().equals(GameStatusType.GAME_WAITING)){
							gameMap.get(command.gameName).playerGreenMove=playerMap.get(command.player).playerMove;
							
							Player playerRed=gameMap.get(command.gameName).playerRedMove.player;
							writeResponse(serviceChannel,ResponseType.GAME_JOINED,playerRed.getLogin());
							writeResponse(playerMap.get(playerRed).channel,ResponseType.GAME_READY,command.player.getLogin());
							
							gameMap.get(command.gameName).setGameStatus(GameStatusType.GAME_READY);
							System.out.println(ResponseType.GAME_JOINED);
							System.out.println(ResponseType.GAME_READY);
						}
					}
					else {
						writeResponse(serviceChannel,ResponseType.WRONG_GAME_NAME,null);
						System.out.println(ResponseType.WRONG_GAME_NAME);
					}
				} 
				else {
					writeResponse(serviceChannel,ResponseType.USER_NOT_REGISTERED,null);
					System.out.println(ResponseType.USER_NOT_REGISTERED);
				}
				break;
			case GAME_MOVE:
				if (playerMap.containsKey(command.player) && gameMap.get(command.gameName).getID().equals(command.gameID)){

				}
				break;
			case END_CONNECTION:
				if (playerMap.containsKey(command.player)){
					writeResponse(serviceChannel,ResponseType.CONNECTION_ENDED,null);
					String[] ks = new String[gameMap.size()];
					gameMap.keySet().toArray(ks);
					for (String game:ks){
						if (gameMap.containsKey(game))
							if ((gameMap.get(game).playerRedMove.player.equals(command.player)) || (gameMap.get(game).playerGreenMove!=null && gameMap.get(game).playerGreenMove.player.equals(command.player))) {
								gameEnd(gameMap.get(game));
								gameMap.remove(game);
							} 
					}
					playerLoginSet.remove(command.player.getLogin());
					try {
						playerMap.get(command.player).channel.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					playerMap.remove(command.player);
					System.out.println(ResponseType.CONNECTION_ENDED);
				}
				break;
			}
		}
	}


	private void writeResponse(SocketChannel socketChannel,ResponseType response, Object object){

		ByteBuffer writeBuffer=ByteBuffer.allocate(BUFFSIZE);

		ServerResponsePackage responsePackage=new ServerResponsePackage();
		responsePackage.response=response;
		responsePackage.object=object;
		try {
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(bos);

			oos.writeObject(PackageLimiterType.PACKAGE_BEGIN);
			oos.writeObject(responsePackage);
			oos.writeObject(PackageLimiterType.PACKAGE_END);
			oos.flush();

			writeBuffer.putInt(bos.toByteArray().length);
			writeBuffer.put(bos.toByteArray());
			writeBuffer.flip();
			socketChannel.write(writeBuffer);
			writeBuffer.clear();
		} catch (IOException e){
			e.printStackTrace();
		} 
	}
	synchronized void addToDatabase (GameInfo gameInfo, TurnInfo turnInfo){

	}

	public static void main(String[] args) {
		System.out.println("Podaj adres ip i port");

		String ipS=new String("127.0.0.1");
		int port=50000;
		
/*		ipS=System.console().readLine();
		port=Integer.parseInt(System.console().readLine());
	
		System.out.println(ipS);
		System.out.println(Integer.toString(port));
		new InetSocketAddress(ipS, port);
*/
		DraughtsServer server=new DraughtsServer(ipS,port);

		server.establishConnection();
		server.start();
	}
}
