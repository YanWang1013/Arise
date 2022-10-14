package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Enchant;

public class ScrollLabeledZelgoMer extends Enchant {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollLabeledZelgoMer();
		return item;
	}

	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(cha.getInventory() != null){
			ItemInstance armor = cha.getInventory().value(cbp.readD());
			if(armor!=null && isEnchant(armor) && armor instanceof ItemArmorInstance){
				if (armor.getEnLevel() >= Lineage.item_enchant_armor_max) {
					ChattingController.toChatting(cha, "방어구는 "+Lineage.item_enchant_armor_max+"이상 인챈트 할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				if(cha instanceof PcInstance)
					armor.toEnchant((PcInstance)cha, toEnchant(cha, armor));
				cha.getInventory().count(this, getCount()-1, true);
			}
		}
	}

	/**
	 * 인첸트가 가능한지 확인해주는 함수.
	 * @param armor
	 * @return
	 */
	public boolean isEnchant(ItemInstance armor){
		if(armor == null)
			return false;
		if(!(armor instanceof ItemArmorInstance))
			return false;
		if(!armor.getItem().isEnchant()){
			if(Lineage.item_accessory_bless_enchant && armor.getBress()==0){
				return	armor.getItem().getType2().equalsIgnoreCase("necklace") || 
						armor.getItem().getType2().equalsIgnoreCase("ring") || 
						armor.getItem().getType2().equalsIgnoreCase("belt") || 
						armor.getItem().getType2().equalsIgnoreCase("earring");
			}else{
				return false;
			}
		}
		return true;
	}
}
