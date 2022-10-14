package lineage.world.object.npc.shop;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ShopBuy;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.ShopInstance;

public class OrcSanta extends ShopInstance {
	
	public OrcSanta(Npc npc){
		super(npc);
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orcsanta"));
	}

	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("request xmas card")){
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
		}else{
			super.toTalk(pc, action, type, cbp);
		}
	}
}
