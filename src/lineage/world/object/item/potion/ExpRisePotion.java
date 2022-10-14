package lineage.world.object.item.potion;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BuffController;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.sp.ExpPotion;
import lineage.share.Lineage;

public class ExpRisePotion extends ItemInstance {
	public static synchronized ItemInstance clone(ItemInstance paramItemInstance) {
		if (paramItemInstance == null)
			paramItemInstance = new ExpRisePotion();
		return paramItemInstance;
	}

	public void toClick(Character paramCharacter, ClientBasePacket paramClientBasePacket) {
		if ((paramCharacter instanceof PcInstance)) {
			PcInstance localPcInstance = (PcInstance) paramCharacter;
			Skill localSkill = null;
			if (this.item.getName().indexOf("20%") > -1)
				localSkill = SkillDatabase.find(505);
			else if (this.item.getName().indexOf("30%") > -1)
				localSkill = SkillDatabase.find(506);
			else if (this.item.getName().indexOf("40%") > -1)
				localSkill = SkillDatabase.find(507);
			else if (this.item.getName().indexOf("50%") > -1)
				localSkill = SkillDatabase.find(508);
			else if (this.item.getName().indexOf("100%") > -1)
				localSkill = SkillDatabase.find(509);
			else if (this.item.getName().indexOf("200%") > -1)
				localSkill = SkillDatabase.find(510);
			else if (this.item.getName().indexOf("300%") > -1) {
				if (paramCharacter.getLevel() > 49) {
					localSkill = SkillDatabase.find(511);
				} else {
					ChattingController.toChatting(paramCharacter, "드래곤의 최고급 다이아몬드 300%는 50레벨부터 사용 가능합니다.", 20);
					return;
				}
			}

			if (localSkill != null) {
				BuffController.remove(paramCharacter, ExpPotion.class);
				BuffController.append(paramCharacter,
						ExpPotion.clone(BuffController.getPool(ExpPotion.class), localSkill, 1800));
			}
			localPcInstance.getInventory().count(this, getCount() - 1L, true);
		}
	}
}
