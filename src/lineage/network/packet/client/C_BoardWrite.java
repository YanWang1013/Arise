package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BoardController;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;

public class C_BoardWrite extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BoardWrite(data, length);
		else
			((C_BoardWrite)bp).clone(data, length);
		return bp;
	}
	
	public C_BoardWrite(byte[] data, int length){
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(!isRead(4) || pc==null || pc.isWorldDelete())
			return this;
		
		object o = pc.findInsideList(readD());
		if(o!=null && o instanceof BoardInstance)
			BoardController.toWrite(pc, (BoardInstance)o, readS(), readS());
		
		return this;
	}
	
}
