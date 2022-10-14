package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;

public class ScrollLabeledDaneFools extends Enchant {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollLabeledDaneFools();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			ItemInstance weapon = cha.getInventory().value(cbp.readD());
			if(weapon!=null && weapon.getItem().isEnchant() && weapon instanceof ItemWeaponInstance){
				if (weapon.getEnLevel() >= Lineage.item_enchant_weapon_max) {
					ChattingController.toChatting(cha, "무기는 "+Lineage.item_enchant_weapon_max+"이상 인챈트 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				if(cha instanceof PcInstance)
					weapon.toEnchant((PcInstance)cha, toEnchant(cha, weapon));
				cha.getInventory().count(this, getCount()-1, true);
			}
		}
	}

}
