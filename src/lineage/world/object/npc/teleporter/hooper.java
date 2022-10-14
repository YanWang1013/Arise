package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class hooper extends TeleportInstance {
	
	public hooper(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telefire1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp) {
		if (pc.getLawful() < Lineage.CHAOTIC) { //카오틱이면 멘트 출력
			if (action.equalsIgnoreCase("teleportURL")) {
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telefire2", null, list));
			} else {
				super.toTalk(pc, action, type, cbp);
			}
		}

	}
}
