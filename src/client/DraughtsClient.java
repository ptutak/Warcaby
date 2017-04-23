package client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import enums.CommandType;
import enums.PackageLimiterType;
import enums.PlayerMoveType;
import enums.ResponseType;
import general.BoardBounds;
import general.Move;
import general.Player;
import general.ServerResponsePackage;
import general.UserCommandPackage;

public class DraughtsClient {
	
	private String serverIp=null;
	private int serverPort=0;
	private SocketChannel socketChannel=null;
	private final long responseTime=1500;
	
	private final int BUFFSIZE=4096;
	private ByteBuffer readBuffer=ByteBuffer.allocate(BUFFSIZE);
	
	private Player player=null;
	private String gameName=null;
	private UUID gameID=null;
	private BoardBounds boardBounds=null;
	private int rowNumber=0;
	private PlayerMoveType playerMoveType=null;
	private Move move=null;
	
	
	public boolean establishConnection(String ip, int port){
		int serverMultiTime=5;
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			boolean connected=socketChannel.connect(new InetSocketAddress(ip, port));
			int i=0;
			while (!connected){
				TimeUnit.MILLISECONDS.sleep(responseTime);
				connected=socketChannel.finishConnect();
				if (i>serverMultiTime)
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
	
	public boolean registerUser(String login){
		if (socketChannel==null)
			return false;
		player=new Player(login);
		writeCommand(socketChannel,CommandType.REGISTER_NEW_USER);
		ServerResponsePackage response=checkResponse(socketChannel);
		if (response!=null)
			if (response.response==ResponseType.USER_REGISTERED){
				player=(Player)response.object;
				return true;
			}
		return false;
	}
	
	private ServerResponsePackage checkResponse(SocketChannel socketChannel){
		if (!socketChannel.isOpen())
			return null;
		ByteBuffer safeBuffer=ByteBuffer.allocate(BUFFSIZE);
		ServerResponsePackage response;
		long startClock=System.currentTimeMillis();
		while (System.currentTimeMillis()-startClock<responseTime) {
			try {
				long n = socketChannel.read(readBuffer);
				safeBuffer.put(readBuffer);
				if (n >= 0) {
					ByteBuffer copyBuffer=safeBuffer.duplicate();
					copyBuffer.flip();
					byte[] tmpBuffer=new byte[copyBuffer.remaining()];
					copyBuffer.get(tmpBuffer,copyBuffer.arrayOffset(),copyBuffer.remaining());
					ByteArrayInputStream bis=new ByteArrayInputStream(tmpBuffer);
					ObjectInputStream ois=new ObjectInputStream(bis);
					PackageLimiterType begin=(PackageLimiterType)ois.readObject();
					if(begin.equals(PackageLimiterType.PACKAGE_BEGIN)){
						response=(ServerResponsePackage)ois.readObject();
						PackageLimiterType end=(PackageLimiterType)ois.readObject();
						if (end.equals(PackageLimiterType.PACKAGE_END))
							return response;
					}
				}
				else if (n==-1){
					socketChannel.close();
					return null;
				}
			} catch (ClassNotFoundException e) {
				System.out.println("CNF");
			} catch (EOFException e){
				System.out.println("EOF");
			} catch (IOException e) {
				System.out.println("IOException");
				e.printStackTrace();
				break;
			}	
		}
		return null;
	}
	
	public void closeConnection(){
		try {
			socketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeCommand(SocketChannel socketChannel,CommandType command){
		UserCommandPackage commandPackage=new UserCommandPackage();
		commandPackage.commandType=command;
		commandPackage.player=player;
		commandPackage.boardBounds=boardBounds;
		commandPackage.rowNumber=rowNumber;
		commandPackage.gameName=gameName;
		commandPackage.gameID=gameID;
		commandPackage.move=move;
		commandPackage.playerMoveType=playerMoveType;
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos=new ObjectOutputStream(bos);
			oos.writeObject(PackageLimiterType.PACKAGE_BEGIN);
			oos.writeObject(commandPackage);
			oos.writeObject(PackageLimiterType.PACKAGE_END);
			oos.flush();
			System.out.println(bos.toByteArray().length);

			socketChannel.write(ByteBuffer.wrap(bos.toByteArray()));
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		DraughtsClient client=new DraughtsClient();
		boolean response=client.establishConnection("127.0.0.1", 50000);
		System.out.println(response);
		response=client.registerUser("piotr");
		System.out.println(response);
		client.closeConnection();
	}
}
