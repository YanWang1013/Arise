package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class C_ItemPickup extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ItemPickup(data, length);
		else
			((C_ItemPickup)bp).clone(data, length);
		return bp;
	}
	
	public C_ItemPickup(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.getInventory()==null || !isRead(12) || pc.isDead() || pc.isWorldDelete())
			return this;
		
		int x = readH();
		int y = readH();
		int object_id = readD();
		long count = readD();
		
		object o = pc.findInsideList(object_id);
		if(o!=null && (pc.getGm()>=Lineage.GMCODE || !pc.isTransparent())){
			if(o.getX()==x && o.getY()==y && Util.isDistance(pc, o, 2))
				pc.getInventory().toPickup(o, count);
		}
		
		return this;
	}
}
