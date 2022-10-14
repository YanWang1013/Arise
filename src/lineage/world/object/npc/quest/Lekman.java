package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.PcInstance;

public class Lekman extends FirstQuest {

	public Lekman(Npc npc){
		super(npc);
		
		List<Craft> l = new ArrayList<Craft>();
		l.add( new Craft(ItemDatabase.find("말하는 두루마리"), 1) );
		list.put(null, l);
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
		if(q == null)
			q = QuestController.newQuest(pc, this, Lineage.QUEST_TALKINGSCROLL);
		
		if(q.getQuestStep() == 0)
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena1"));
		else
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena4"));
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("0")){
			Quest q = QuestController.find(pc, Lineage.QUEST_TALKINGSCROLL);
			if(q!=null && q.getQuestStep()==0){
				List<Craft> l = list.get(null);
				// 재료 확인.
				if(CraftController.isCraft(pc, l, true)){
					// 재료 제거
					CraftController.toCraft(pc, l);
					// 아이템 지급.
					toStep1(pc);
					// 퀘스트 스탭 변경.
					q.setQuestStep( 1 );
					// 창 띄우기.
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "orena3"));
				}
			}
		}
	}
}
