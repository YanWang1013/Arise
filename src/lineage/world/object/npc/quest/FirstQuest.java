package lineage.world.object.npc.quest;

import lineage.bean.database.Npc;
import lineage.database.ItemDatabase;
import lineage.share.Lineage;
import lineage.world.controller.CraftController;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.QuestInstance;

public class FirstQuest extends QuestInstance {

	public FirstQuest(Npc npc){
		super(npc);
	}
	
	/**
	 * 첫번째 아이템 지급 처리.
	 * @param pc
	 */
	protected void toStep1(PcInstance pc){
		// 공통 추가 부분.
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 체력 회복제"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("숨겨진 계곡 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("노래하는 섬 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("말하는 섬 마을 귀환 주문서"), 10, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 확인 주문서"), 22, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 갑옷"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 투구"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽 샌달"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 가죽장갑"), 1, true);
		CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 단검"), 1, true);
		
		// 클레스별 추가 부분.
		switch(pc.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 한손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 양손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 도끼"), 1, true);
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 한손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 양손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 도끼"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 석궁"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 1000, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 창"), 1, true);
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 1000, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 한손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 석궁"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 활"), 1, true);
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 지팡이"), 1, true);
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				CraftController.toCraft(this, pc, ItemDatabase.find("화살"), 1000, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 한손검"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 석궁"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 크로우"), 1, true);
				CraftController.toCraft(this, pc, ItemDatabase.find("상아탑의 이도류"), 1, true);
				break;
		}
	}
}
