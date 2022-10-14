package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BoardController;
import lineage.world.controller.RankController;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.RankBoardInstance;

public class C_BoardView extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BoardView(data, length);
		else
			((C_BoardView)bp).clone(data, length);
		return bp;
	}
	
	public C_BoardView(byte[] data, int length){
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(!isRead(8) || pc==null || pc.isWorldDelete())
			return this;
		
		object o = pc.findInsideList(readD());
		if(o!=null && o instanceof BoardInstance){
			if(o instanceof RankBoardInstance)
				RankController.toView(pc, readD());
			else
				BoardController.toView(pc, (BoardInstance)o, readD());
		}
		
		return this;
	}

}
