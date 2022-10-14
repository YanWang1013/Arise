package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;

public class ScrollResurrection extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollResurrection();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		object o = cha.findInsideList(cbp.readD());
		if(o != null)
			o.toRevival(cha);
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}
}
