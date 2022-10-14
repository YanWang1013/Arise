package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class GoddessAgata extends object {

	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "restore"));
	}

	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("exp")){
			if(pc.getLostExp() > 0){
				if(pc.getInventory().isAden(pc.getLevel()*pc.getLevel()*100, true)){
					if(pc.getLevel() > 79)
						// 경험치99% 복구.
						pc.setExp(pc.getExp() + (pc.getLostExp()*0.99));
					else
						// 경험치50% 복구.
						pc.setExp(pc.getExp() + (pc.getLostExp()*0.50));
					pc.setLostExp(0);
					pc.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), pc));
				}else{
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
				}
			}
		}
	}
}
