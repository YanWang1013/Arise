package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.Teleport;

public class ScrollLabeledVenzarBorgavve extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollLabeledVenzarBorgavve();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 메모리제거구문인 delete를 시행하면 가지고있던 bress값이 변하므로 아래와같이 임시로 담아서 아래쪽에서 활용.
		int bress = this.bress;

		// 수량 갱신.
		cha.getInventory().count(this, getCount()-1, true);

		if(Teleport.onBuff(cha, cbp, bress, true, true))
			// 이동
			cha.toTeleport(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap(), true);
	}

}
