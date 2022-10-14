package lineage.network.packet.client;

import lineage.network.packet.BasePacket;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class C_ItemClick extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_ItemClick(data, length);
		else
			((C_ItemClick)bp).clone(data, length);
		return bp;
	}
	
	public C_ItemClick(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(PcInstance pc){
		// 버그 방지.
		if(pc==null || pc.getInventory()==null || !isRead(4) || pc.isDead() || pc.isWorldDelete())
			return this;
		
		ItemInstance item = pc.getInventory().value( readD() );
		try {
			if(item!=null && item.isClick(pc) && (pc.getGm()>=Lineage.GMCODE || !pc.isTransparent()))
				item.toClick(pc, this);
		} catch (Exception e) { }
		
		return this;
	}
}
