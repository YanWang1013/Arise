package lineage.world.object.item.scroll;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class TalkingScroll extends ItemInstance {
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new TalkingScroll();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "tscrolla"));
	}

}
