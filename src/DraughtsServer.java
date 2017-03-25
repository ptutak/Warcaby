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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
		    boolean serverIsRunning = true;
		    while(serverIsRunning) {
		      try {
		        selector.select();

		        Set<SelectionKey> selectorKeys = selector.selectedKeys();

		        Iterator<SelectionKey> iter = selectorKeys.iterator();
		        while(iter.hasNext()) {  
		          SelectionKey key = (SelectionKey) iter.next(); 
		          iter.remove();                                 

		          if (key.isAcceptable()) { 
		            SocketChannel newChannel = serverSocketChannel.accept();
		            newChannel.configureBlocking(false);
		            newChannel.register(selector, SelectionKey.OP_READ);
		            continue;
		          }

		          if (key.isReadable()) {  
		            SocketChannel serviceChannel = (SocketChannel) key.channel();
		            serviceRequest(serviceChannel);
		            continue;
		          }
		        }
		      } catch (IOException e){
		    	  e.printStackTrace();
		      }
		    }
	}
	
	final static int BUFFSIZE=4096;
	private ByteBuffer buffer=ByteBuffer.allocate(BUFFSIZE);
	
	private void serviceRequest(SocketChannel socketChannel) {
		 if (!socketChannel.isOpen())
			 return;
		 buffer.clear();
		 while (true) {
	        int n;
			try {
				n = socketChannel.read(buffer);
				if (n > 0) {
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
