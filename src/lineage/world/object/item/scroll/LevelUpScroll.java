package lineage.world.object.item.scroll;

import lineage.database.ExpDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class LevelUpScroll extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new LevelUpScroll();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.getInventory().count(this, getCount()-1, true);
		
		cha.setExp( ExpDatabase.find(cha.getLevel()).getBonus() );
	}

}
