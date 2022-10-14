package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Isvall extends ShopInstance {
	
	public Isvall(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getGm()>=Lineage.GMCODE){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "isvall4"));
		}else{
			if(pc.getLevel()<40){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "isvall2"));
			}else if(pc.getLevel()<45){
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "isvall3"));
			}else{
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "isvall4"));
			}
		}
	}
}
