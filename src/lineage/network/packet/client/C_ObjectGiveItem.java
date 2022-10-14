package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class C_ObjectGiveItem extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ObjectGiveItem(data, length);
		else
			((C_ObjectGiveItem)bp).clone(data, length);
		return bp;
	}
	
	public C_ObjectGiveItem(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.isDead() || !isRead(16) || pc.isWorldDelete())
			return this;
		
		int target_objid = readD();
		int x = readH();
		int y = readH();
		int item_objid = readD();
		long item_count = readD();
		object o = pc.findInsideList(target_objid);
		if (o instanceof PcInstance) {
			return this;
		}
		ItemInstance item = pc.getInventory().value(item_objid);
		
		if(o!=null && item!=null && Util.isDistance(pc, o, 2) && pc.getInventory().isRemove(item, item_count, true, false) && (pc.getGm()>=Lineage.GMCODE || !pc.isTransparent()))
			o.toGiveItem(pc, item, item_count);
		
		return this;
	}
	
}
