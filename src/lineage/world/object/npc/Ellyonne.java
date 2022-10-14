package lineage.world.object.npc;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public class Ellyonne extends object {

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		if(pc.getClassType() == Lineage.LINEAGE_CLASS_ELF)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ellyonne"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "ellyonne2"));
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(pc.getLevel()<30 || pc.getClassType()!=Lineage.LINEAGE_CLASS_ELF){
			// 정령이 튕겨졌습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 451));
			return;
		}
		
		if(action.equalsIgnoreCase("fire")){
			if(pc.getAttribute() == 0){
				pc.setAttribute(Lineage.ELEMENT_FIRE);
				// 몸 구석구석으로 %s의 기운이 스며들어옵니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 679, "$1059"));
			}else{
				// 정령이 튕겨버렸습니다. 이미 다른 정령 속성이 부여되어 있습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 449));
			}
		}else if(action.equalsIgnoreCase("water")){
			if(pc.getAttribute() == 0){
				pc.setAttribute(Lineage.ELEMENT_WATER);
				// 몸 구석구석으로 %s의 기운이 스며들어옵니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 679, "$1060"));
			}else{
				// 정령이 튕겨버렸습니다. 이미 다른 정령 속성이 부여되어 있습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 449));
			}
		}else if(action.equalsIgnoreCase("air")){
			if(pc.getAttribute() == 0){
				pc.setAttribute(Lineage.ELEMENT_WIND);
				// 몸 구석구석으로 %s의 기운이 스며들어옵니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 679, "$1061"));
			}else{
				// 정령이 튕겨버렸습니다. 이미 다른 정령 속성이 부여되어 있습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 449));
			}
		}else if(action.equalsIgnoreCase("earth")){
			if(pc.getAttribute() == 0){
				pc.setAttribute(Lineage.ELEMENT_EARTH);
				// 몸 구석구석으로 %s의 기운이 스며들어옵니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 679, "$1062"));
			}else{
				// 정령이 튕겨버렸습니다. 이미 다른 정령 속성이 부여되어 있습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 449));
			}
		}else{
			// 모든 정령마법 제거.
			// 19 : 3~7
			// 20 : 2~7
			// 21 : 2~7
			for(int s=19 ; s<22 ; ++s){
				for(int n=2 ; n<8 ; ++n){
					if(s==19 && n==2)
						continue;
					SkillController.remove(pc, s, n);
				}
			}
//			for(int i=93 ; i<=112 ; ++i)
//				SkillController.remove(pc, i, false);
			pc.setAttribute(Lineage.ELEMENT_NONE);
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 678));
		}
	}
}
