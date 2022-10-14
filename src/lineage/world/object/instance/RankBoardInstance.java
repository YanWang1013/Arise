package lineage.world.object.instance;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_BoardList;
import lineage.world.controller.RankController;
import lineage.world.object.Character;

public class RankBoardInstance extends BoardInstance {
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 패킷 처리
		cha.toSender(S_BoardList.clone(BasePacketPooling.getPool(S_BoardList.class), this, RankController.getList()));
	}
	
}
