import java.nio.channels.SocketChannel;

public class PlayerService {
	public SocketChannel channel;
	public PlayerMove playerMove;
	
	public PlayerService(SocketChannel channel, PlayerMove playerMove){
		this.channel=channel;
		this.playerMove=playerMove;
	}
}
