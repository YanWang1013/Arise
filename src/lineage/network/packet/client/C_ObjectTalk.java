package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class C_ObjectTalk extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectTalk(data, length);
		else
			((C_ObjectTalk)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectTalk(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isWorldDelete() || !isRead(4))
			return this;
		
		int objId = readD();
		object o = pc.findInsideList(objId);
		if(o!=null && (pc.getGm()>=Lineage.GMCODE || !pc.isTransparent()))
			o.toTalk(pc, this);
			
		return this;
	}
}
