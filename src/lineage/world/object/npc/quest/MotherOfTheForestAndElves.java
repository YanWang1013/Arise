package lineage.world.object.npc.quest;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.lineage.Craft;
import lineage.bean.lineage.Quest;
import lineage.database.ItemDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.controller.CraftController;
import lineage.world.controller.QuestController;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class MotherOfTheForestAndElves extends QuestInstance {
	
	public MotherOfTheForestAndElves(){
		super(null);
		
		/**
		 * Refer. Source code
		 * lineage.world.object.npc.quest.Gerard.java
		 * */
		
		// reward item select list 1
		Item i = ItemDatabase.find("오크족 검");
		
		// request elven short sword of constitution
		// request elven bow of dexterity
		// request elven short sword of magic power
		
		if(i != null){
			craft_list.put("request elven short sword of constitution", i);
			
			List<Craft> l = new ArrayList<Craft>();
			l.add( new Craft(ItemDatabase.find("듀펠게넌의 목걸이"), 1) );
			list.put(i, l);
		}
		
		// reward item select list 2
		i = ItemDatabase.find("민첩도 향상 요정족 활");
		if (i != null) {
			craft_list.put("request elven bow of dexterity", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("듀펠게넌의 목걸이"), 1));
			list.put(i, l);
		}
		
		// reward item select list 3
		i = ItemDatabase.find("난쟁이족 검");
		if (i != null) {
			craft_list.put("request elven short sword of magic power", i);

			List<Craft> l = new ArrayList<Craft>();
			l.add(new Craft(ItemDatabase.find("듀펠게넌의 목걸이"), 1));
			list.put(i, l);
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp) {
		switch (pc.getClassType()) {
		case Lineage.LINEAGE_CLASS_ELF:
			if (pc.getLevel() < 15) {
				// You are not ready for initiation
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherEV3"));
				ChattingController.toChatting(pc, "Not yet 15 level", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV15_DUPELGENON);
				if (q == null)
					q = QuestController.newQuest(pc, this, Lineage.QUEST_ELF_LV15_DUPELGENON);
				// Felix : Check if has necklace
				ItemInstance temp = pc.getInventory().find("듀펠게넌의 목걸이", false);
				if (temp != null && q.getQuestStep() == 2) {
					q.setQuestStep(3);
				}
				switch (q.getQuestStep()) {
				case 0:
					// Start the initiation
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherE1"));
					ChattingController.toChatting(pc, "motherE1: Start the initiation", Lineage.CHATTING_MODE_MESSAGE);
					break;
				case 1:
					// The way to solve the problem
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherEV1"));
					ChattingController.toChatting(pc, "motherEV1: The way to solve the problem", Lineage.CHATTING_MODE_MESSAGE);
					break;
				case 2:
					// fail
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherEV5"));
					ChattingController.toChatting(pc, "Try again.", Lineage.CHATTING_MODE_MESSAGE);
					break;
				case 3:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherEV6"));
					ChattingController.toChatting(pc, "Congraturation.", Lineage.CHATTING_MODE_MESSAGE);
					break;
				case 4:
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherEV7"));
					ChattingController.toChatting(pc, "End.", Lineage.CHATTING_MODE_MESSAGE);
					break;
				}
			}
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherM2"));
			ChattingController.toChatting(pc, "You're Darkelf", Lineage.CHATTING_MODE_MESSAGE);
			break;
		default:
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "motherM1"));
			ChattingController.toChatting(pc, "You're Human", Lineage.CHATTING_MODE_MESSAGE);
			break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){

		if(!Lineage.CHARACTER_QUEST) {
			return;
		}
		if(action.equalsIgnoreCase("start_event")){
			// 성인식을 시작한다.
			Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV15_DUPELGENON);
			if(q!=null && q.getQuestStep()==0){
				// 퀘스트 스탭 변경.
				q.setQuestStep(1);
				// 안내창 띄우기.
				toTalk(pc, null);
				
				// Felix: Create Dupelgenon Instance				
				MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.findNameid("$756"));
				if (mi != null) {
					int d_x = 0;
					int d_y = 0;
					int dice = Util.random(1, 3);
					if (dice == 1) {
						d_x = 33201;
						d_y = 32317;
					} else if (dice == 2) {
						d_x = 32978;
						d_y = 32442;
					} else {
						d_x = 33010;
						d_y = 32253;
					}
					// d_x = 33057;
					// d_y = 32338;
					// System.out.print("Dupelgenon Position : " + d_x + " , " + d_y + " \n");
					mi.setHomeX(d_x);
					mi.setHomeY(d_y);
					mi.setHomeMap(pc.getMap());
					mi.setHeading(Util.random(0, 7));
					mi.setQuestMonster(true);
					mi.toTeleport(d_x, d_y, pc.getMap(), false);
					mi.readDrop();

					AiThread.append(mi);
					q.setQuestStep(2);
				} else {
					ChattingController.toChatting(pc, "monster is not found", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
		}else if(action.equalsIgnoreCase("request elven short sword of constitution") 
				|| action.equalsIgnoreCase("request elven bow of dexterity") 
				|| action.equalsIgnoreCase("request elven short sword of magic power")){
			// Dupelgenon necklace give to mother tree
			Item craft = craft_list.get(action);
			Quest q = QuestController.find(pc, Lineage.QUEST_ELF_LV15_DUPELGENON);
			if(craft!=null && q!=null && q.getQuestStep()==3){
				List<Craft> l = list.get(craft);
				// material check
				if(CraftController.isCraft(pc, l, true)){
					// material remove
					CraftController.toCraft(pc, l);
					// give to reward item
					CraftController.toCraft(this, pc, craft, 1, true);
					// quest step next
					q.setQuestStep(4);
					// message pop up
					toTalk(pc, null);
				}
			}
		}
	}

}