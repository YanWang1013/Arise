package lineage.world.object.npc;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.object.instance.PcInstance;

public class MotherOfTheForestAndElves extends TalkMovingNpc {

	public MotherOfTheForestAndElves(Npc npc){
		super(npc, null);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		super.toTalk(pc, cbp);
		
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_ELF:
				if(pc.getLawful()<Lineage.NEUTRAL)
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherE1"));
				else
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherEV4"));
				break;
		}
	}
}

