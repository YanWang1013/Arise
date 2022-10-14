package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class C_BoardClick extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_BoardClick(data, length);
		else
			((C_BoardClick)bp).clone(data, length);
		return bp;
	}
	
	public C_BoardClick(byte[] data, int length){
		clone(data, length);
	}

	@Override
	public BasePacket init(PcInstance pc){
		// 버그방지
		if(!isRead(4) || pc==null || pc.isWorldDelete())
			return this;
		
		object o = pc.findInsideList(readD());
		if(o!=null && (pc.getGm()>=Lineage.GMCODE || !pc.isTransparent()))
			o.toClick(pc, this);
		
		return this;
	}

}
