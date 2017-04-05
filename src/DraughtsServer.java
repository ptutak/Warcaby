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
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


public class DraughtsServer extends Thread {

	private String host;
	private int port;
	private ServerSocketChannel serverSocketChannel = null;
	private Selector selector = null;
	private boolean serverState;


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
	private ByteBuffer buffer=ByteBuffer.allocate(BUFFSIZE);

	private void serviceRequest(SocketChannel serviceChannel) throws EOFException {
		if (!serviceChannel.isOpen())
			return;
		ByteBuffer oldBuffer=ByteBuffer.allocate(BUFFSIZE);
		while (true) {
			try {
				long n = serviceChannel.read(buffer);
				oldBuffer.put(buffer);
				if (n > 0) {
					oldBuffer.flip();
					ByteArrayInputStream bis=new ByteArrayInputStream(oldBuffer.array());
					ObjectInputStream ois=new ObjectInputStream(bis);
					SType begin=(SType)ois.readObject();
					if(begin==SType.PACKAGE_BEGIN){
						MoveSendPackage newMove=(MoveSendPackage)ois.readObject();
						SType end=(SType)ois.readObject();
						if (end==SType.PACKAGE_END)
							break;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (EOFException e){
				oldBuffer=buffer.duplicate();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	synchronized void addToDatabase (GameInfo gameInfo, TurnInfo turnInfo){

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MoveSendPackage move=new MoveSendPackage(UUID.randomUUID(),"PiotrTutak,ABCS",new Move(PMType.MOVE,new ColPiece(new Piece(PType.QUEEN,1,1),FType.GREEN),new ColPiece(new Piece(PType.PAWN,1,1),FType.GREEN)));
		System.out.println(move);
		ByteArrayOutputStream byteStream=new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectStream=new ObjectOutputStream(byteStream);
			objectStream.writeObject(move);
			objectStream.flush();
			byte[] byteObject=byteStream.toByteArray();
			ByteArrayInputStream byteStream2=new ByteArrayInputStream(byteObject);
			ObjectInputStream objectStream2=new ObjectInputStream(byteStream2);
			try {
				MoveSendPackage x=(MoveSendPackage) objectStream2.readObject();
				System.out.println(x);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("nie ma klasy");
			} catch (ClassCastException e){
				e.printStackTrace();
				System.out.println("ut");
			}
			try {
				MoveSendPackage x2=(MoveSendPackage) objectStream2.readObject();
				System.out.println(x2);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("nie ma tej kklasy znowu :(");
			} catch (EOFException e){
				e.printStackTrace();
				System.out.println("tutu");
			}
			
			
			System.out.println(byteObject.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				byteStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
