package lineage.world.object.item.scroll;

import lineage.database.PolyDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.ShapeChange;

public class ScrollPolymorph extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollPolymorph();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		String name = cbp.readS();
		// 시전 처리.
		ShapeChange.init(cha, cha, PolyDatabase.getName(name), 1800, bress);
		
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}

}
