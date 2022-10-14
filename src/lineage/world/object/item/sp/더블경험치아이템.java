package lineage.world.object.item.sp;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.sp.DubleExpPotion;
public class 더블경험치아이템 extends ItemInstance {

	static synchronized public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new 더블경험치아이템();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			Skill s = SkillDatabase.find(405);
			if(s != null)
				// 스킬 적용.
				BuffController.append(cha, DubleExpPotion.clone(BuffController.getPool(DubleExpPotion.class), s, 30*60));
			// 아이템 수량 갱신
			pc.getInventory().count(this, getCount()-1, true);
		}
	}

}
