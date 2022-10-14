package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;

public class C_BoardPaging extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BoardPaging(data, length);
		else
			((C_BoardPaging)bp).clone(data, length);
		return bp;
	}
	
	public C_BoardPaging(byte[] data, int length){
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(!isRead(8) || pc==null || pc.isWorldDelete())
			return this;
		
		object o = pc.findInsideList(readD());
		if(o!=null && o instanceof BoardInstance)
			((BoardInstance)o).toPage(pc, readD());
		
		return this;
	}

}
