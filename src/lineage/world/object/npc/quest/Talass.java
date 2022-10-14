package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class Talass extends QuestInstance {
	
	public Talass(Npc npc){
		super(npc);
		
		// hyper text 패킷 구성에 해당 구간을 npc 이름으로 사용함.
		temp_request_list.add( npc.getNameId() );
		
		// 제작 처리 초기화.
		Item i = ItemDatabase.find("사이하의 활");
		if(i != null){
			craft_list.put("request bow of sayha", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("장궁"), 1) );
			l.add( new Craft(ItemDatabase.find("풍룡 비늘"), 15) );
			l.add( new Craft(ItemDatabase.find("그리폰의 깃털"), 30) );
			l.add( new Craft(ItemDatabase.find("바람의 눈물"), 50) );
			list.put(i, l);
		}
		
		i = ItemDatabase.find("수정 지팡이");
		if(i != null){
			craft_list.put("request crystal staff", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("신비한 지팡이"), 1) );
			l.add( new Craft(ItemDatabase.find("언데드의 뼈"), 1) );
			list.put(i, l);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_WIZARD:
				Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
				if(q == null){
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talass"));
				}else{
					switch(q.getQuestStep()){
						case 4:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talassE1"));
							break;
						case 5:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talassE2"));
							break;
						default:
							pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talass"));
							break;
					}
				}
				break;
			default:
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "talass"));
				break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(!Lineage.CHARACTER_QUEST) {
			ChattingController.toChatting(pc, "서버에서 지원하지 않고 있습니다. 운영자에게 문의하세요.", Lineage.CHATTING_MODE_MESSAGE);
			return;
		}
		if(action.equalsIgnoreCase("quest 16 talassE2")){
			// 언데드의 뼈
			Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
			if(q==null || q.getQuestStep()!=4){
				toTalk(pc, null);
			}else{
				q.setQuestStep(5);
				toTalk(pc, null);
			}
		}else if(action.equalsIgnoreCase("request crystal staff")){
			// 언데드의 뼈조각을 건네 준다.
			Quest q = QuestController.find(pc, Lineage.QUEST_WIZARD_LV30);
			Item craft = craft_list.get(action);
			if(craft!=null && q!=null && q.getQuestStep()==5){
				List<Craft> l = list.get(craft);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					CraftController.toCraft(this, pc, craft, 1, true);
					// 퀘스트 스탭 변경.
					q.setQuestStep( 6 );
					// 안내창 띄우기.
					toTalk(pc, null);
				}
			}
		}else{
			super.toTalk(pc, action, type, cbp);
		}
	}
	
}
