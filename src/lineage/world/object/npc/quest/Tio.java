package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.CharacterController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Tio extends QuestInstance {
	
	public Tio(Npc n){
		super(n);
		// 관리목록에 등록. toTimer가 호출되도록 하기 위해.
		CharacterController.toWorldJoin(this);
		// 20초 단위로 멘트 표현.
		ment_show_sec = 20;
		// 지하 던전에는 무엇이 있는 걸까?
		list_ment.add("$1733");
		// 그 곳을 이렇게 계속 방치하다가는 무언가 큰일이 벌어질 거 같아...
		list_ment.add("$1734");
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_KNIGHT:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tio"));
				break;
			case Lineage.LINEAGE_CLASS_ROYAL:
			case Lineage.LINEAGE_CLASS_WIZARD:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tio4"));
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tio5"));
				break;
			default:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tio6"));
				break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("request amulet of valley")){
			// 붉은열쇠를 건네준다.
			// 계곡의 증표 지급.
		}
	}
	
}
