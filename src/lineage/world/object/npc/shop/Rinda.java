package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.KingdomController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class Rinda extends ShopInstance {
	
	public Rinda(Npc npc){
		super(npc);
		kingdom = KingdomController.find(Lineage.KINGDOM_ABYSS);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getLawful()<Lineage.NEUTRAL) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rinda2"));
		} else {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "rinda"));
		}
	}
	
}
