package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.CharacterController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Ruba extends QuestInstance {
	
	public Ruba(Npc n) {
		super(n);
		// 관리목록에 등록. toTimer가 호출되도록 하기 위해.
		CharacterController.toWorldJoin(this);
		// 20초 단위로 멘트 표현.
		ment_show_sec = 20;
		// 예언서에 나타난 그분은 언제쯤 오시려나...
		list_ment.add("$1735");
		// 만일 지하 던전의 봉인이 풀리기라도 한다면...
		list_ment.add("$1736");
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_KNIGHT:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ruba4"));
				break;
			case Lineage.LINEAGE_CLASS_ROYAL:
			case Lineage.LINEAGE_CLASS_WIZARD:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ruba"));
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ruba5"));
				break;
			default:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ruba6"));
				break;
		}
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("request amulet of island")){
			// 검은열쇠
			// 계곡의 증표
		}
	}

}
