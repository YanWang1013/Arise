package lineage.world.object.npc.teleporter;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.TeleportInstance;

public class Matt extends TeleportInstance {
	
	public Matt(Npc npc){
		super(npc);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telesilver1"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("teleportURL")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telesilver2", null, list));
		} else if (action.equalsIgnoreCase("teleportURL2")) {
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "telesilver3", null, list));
		} else {
			super.toTalk(pc, action, type, cbp);
		}
	}

}
