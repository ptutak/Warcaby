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
import java.util.UUID;

import enums.SPType;


public class DraughtsServer extends Thread {

	private String host;
	private int port;
	private ServerSocketChannel serverSocketChannel = null;
	private Selector selector = null;
	private boolean serverState;
	private HashMap<Player,PlayerMove> playerMap=new HashMap<Player,PlayerMove>();
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

	public void run(){
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
		System.out.println("Server started");
		serviceConnections();
	}
	private void serviceConnections() {
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

	final int BUFFSIZE=4096;
	private ByteBuffer readBuffer=ByteBuffer.allocate(BUFFSIZE);

	private void serviceRequest(SocketChannel serviceChannel) throws EOFException {
		if (!serviceChannel.isOpen())
			return;
		ByteBuffer safeBuffer=ByteBuffer.allocate(BUFFSIZE);
		CommandPackage command;
		while (true) {
			try {
				long n = serviceChannel.read(readBuffer);
				safeBuffer.put(readBuffer);
				if (n > 0) {
					ByteBuffer tmpBuffer=safeBuffer.duplicate();
					tmpBuffer.flip();
					ByteArrayInputStream bis=new ByteArrayInputStream(tmpBuffer.array());
					ObjectInputStream ois=new ObjectInputStream(bis);
					SPType begin=(SPType)ois.readObject();
					if(begin==SPType.PACKAGE_BEGIN){
						command=(CommandPackage)ois.readObject();
						SPType end=(SPType)ois.readObject();
						if (end==SPType.PACKAGE_END)
							break;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (EOFException e){
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		switch(command.commandType){
		case REGISTER_NEW_USER:
			playerMap.put(command.player, new PlayerMove(command.player,null));
			break;
		case NEW_GAME:
			if (playerMap.containsKey(command.player)){
				if (!gameMap.containsKey(command.gameName)){
					GameInfo newGame=new GameInfo(command.gameName);
					newGame.setBoardBounds(command.boardBounds);
					newGame.setRowNumber(command.rowNumber);
					newGame.playerRedMove.player=command.player;
					gameMap.put(command.gameName, newGame);
				}
					
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
	synchronized void addToDatabase (GameInfo gameInfo, TurnInfo turnInfo){

	}

	public static void main(String[] args) {

	}
}
