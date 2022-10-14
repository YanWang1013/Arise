package lineage.util;

import lineage.Main;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.world.World;

public final class Shutdown extends Thread {

	static private Shutdown _instance;
	static public Shutdown getInstance(){
		if(_instance==null)
			_instance = new Shutdown();
		return _instance;
	}
	
	// 셧다운 처리전 딜레이할 시간값. 분단위
	static public int shutdown_delay;
	
	@Override
	public void run() {
		try {
			while(shutdown_delay > 0){
				String msg = String.format("%d분 후 서버가 종료됩니다.", shutdown_delay);
				// 시간대기 : 매분마다 시간 표현도.
				lineage.share.System.println(msg);
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
				sleep(1000 * 60);
				shutdown_delay -= 1;
			}
			for(int i=10 ; i>0 ; --i){
				String msg = String.format("%d초 후 서버가 종료됩니다.", i);
				lineage.share.System.println(msg);
				World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), msg));
				sleep(1000);
			}
		} catch (Exception e) { }
		
		Main.close();
	}
	
}
