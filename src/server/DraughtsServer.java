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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
import enums.GameStatusType;
import enums.PackageLimiterType;


public class DraughtsServer extends Thread {

	private String host;
	private int port;
	
	final int BUFFSIZE=4096;
	private ByteBuffer readBuffer=ByteBuffer.allocate(BUFFSIZE);
	long responseTime=1500;
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connection established");
	}

	public void run(){
		serverState=true;
		serviceConnections();
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
					                                 
					if (key.isAcceptable()) { 
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


	private void serviceRequest(SocketChannel serviceChannel) {
		if (!serviceChannel.isOpen())
			return;
		ByteBuffer safeBuffer=ByteBuffer.allocate(BUFFSIZE);
		safeBuffer.clear();
		readBuffer.clear();
		UserCommandPackage command=null;
		long startTime=System.currentTimeMillis();
		while (System.currentTimeMillis()-startTime<responseTime) {
			try {
				long n = serviceChannel.read(readBuffer);
				safeBuffer.put(readBuffer);
				if (n > 0) {
					System.out.println(System.currentTimeMillis());
					ByteBuffer tmpBuffer= ByteBuffer.wrap(safeBuffer.array());
					tmpBuffer.flip();
					ByteArrayInputStream bis=new ByteArrayInputStream(tmpBuffer.array());
					ObjectInputStream ois=new ObjectInputStream(bis);
					PackageLimiterType begin=(PackageLimiterType)ois.readObject();
					if(begin==PackageLimiterType.PACKAGE_BEGIN){
						command=(UserCommandPackage)ois.readObject();
						PackageLimiterType end=(PackageLimiterType)ois.readObject();
						if (end==PackageLimiterType.PACKAGE_END)
							break;
					}
				}
			} catch (ClassNotFoundException e) {
				System.out.println("CNF");
			} catch (EOFException e){
				System.out.println("EOF");
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		if (command==null){
			System.out.println("ResponseTimeout");
			readBuffer.clear();
		}
		else
		switch(command.commandType){
		case REGISTER_NEW_USER:
			if (playerLoginSet.contains(command.player.getLogin()))
				writeResponse(serviceChannel,ResponseType.USER_EXISTS,null);
			else{
				playerMap.put(command.player,new PlayerService(serviceChannel, new PlayerMove(command.player,null)));
				writeResponse(serviceChannel,ResponseType.USER_REGISTERED,null);
			}
			break;
		case NEW_GAME:
			if (playerMap.containsKey(command.player)){
				if (!gameMap.containsKey(command.gameName)){
					GameInfo newGame=new GameInfo(command.gameName);
					newGame.setBoardBounds(command.boardBounds);
					newGame.setRowNumber(command.rowNumber);
					newGame.playerRedMove=playerMap.get(command.player).playerMove;
					gameMap.put(command.gameName, newGame);
					writeResponse(serviceChannel,ResponseType.GAME_CREATED,newGame.getID());
				}
				else
					writeResponse(serviceChannel,ResponseType.GAME_EXISTS,null);
			}
			break;
		case AVAILABLE_GAMES:
			if (serviceChannel.isOpen()){
				String[] gameList=(String[]) (gameMap.keySet()).toArray();
				writeResponse(serviceChannel,ResponseType.GAME_LIST,gameList);			
			}
			break;
		case JOIN_GAME:
			if (playerMap.containsKey(command.player)){
				if (gameMap.containsKey(command.gameName) && gameMap.get(command.gameName).getID().equals(command.gameID)){
					if (gameMap.get(command.gameName).getGameStatus().equals(GameStatusType.GAME_WAITING)){
						gameMap.get(command.gameName).playerGreenMove=playerMap.get(command.player).playerMove;
						gameMap.get(command.gameName).setGameStatus(GameStatusType.GAME_READY);
						Player playerRed=gameMap.get(command.gameName).playerRedMove.player;
						writeResponse(playerMap.get(playerRed).channel,ResponseType.GAME_READY,command.player.getLogin());
						writeResponse(serviceChannel,ResponseType.GAME_READY,playerRed.getLogin());
					}
				}
			}
			else {
				writeResponse(serviceChannel,ResponseType.USER_NOT_REGISTERED,null);
			}
			break;
		case GAME_MOVE:
			if (playerMap.containsKey(command.player) && gameMap.get(command.gameName).getID().equals(command.gameID)){
				
			}
			break;
		case END_CONNECTION:
			break;
		}
	}
	
	private void writeResponse(SocketChannel socketChannel,ResponseType response, Object object){
		ServerResponsePackage responsePackage=new ServerResponsePackage();
		responsePackage.response=response;
		responsePackage.object=object;
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos=new ObjectOutputStream(bos);
			oos.writeObject(PackageLimiterType.PACKAGE_BEGIN);
			oos.writeObject(responsePackage);
			oos.writeObject(PackageLimiterType.PACKAGE_END);
			oos.flush();
			socketChannel.write(ByteBuffer.wrap(bos.toByteArray()));
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	synchronized void addToDatabase (GameInfo gameInfo, TurnInfo turnInfo){

	}

	public static void main(String[] args) {
		DraughtsServer server=new DraughtsServer("127.0.0.1",50000);
		server.establishConnection();
		server.start();
	}
}
