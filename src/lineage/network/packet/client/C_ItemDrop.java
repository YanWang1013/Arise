package lineage.network.packet.client;

import lineage.bean.lineage.Inventory;
import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class C_ItemDrop extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ItemDrop(data, length);
		else
			((C_ItemDrop)bp).clone(data, length);
		return bp;
	}
	
	public C_ItemDrop(byte[] data, int length){
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
		
		Inventory inv = pc.getInventory();
		ItemInstance item = inv.value( object_id );
		if(item!=null && (pc.getGm()>=Lineage.GMCODE || !pc.isTransparent())){
			if(Util.isDistance(pc.getX(), pc.getY(), pc.getMap(), x, y, pc.getMap(), 2))
				inv.toDrop(item, count, x, y, true);
		}
		
		return this;
	}
}
