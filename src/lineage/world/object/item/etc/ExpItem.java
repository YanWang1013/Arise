package lineage.world.object.item.etc;

import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.sp.ExpPotion;
//import lineage.world.object.magic.ExpPotion;

public class ExpItem extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ExpItem();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){

		Skill s = SkillDatabase.find(405);
		if(s != null)
			// 스킬 적용.
			BuffController.append(cha, ExpPotion.clone(BuffController.getPool(ExpPotion.class), s, 30*60));
		// 
		cha.setBuffExpPotion( (float)(item.getDmgMax()*0.01) );
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}

}
