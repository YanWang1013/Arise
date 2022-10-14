package lineage.world.object.item.scroll;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.Teleport;

public class SpellScrollTeleport extends ItemInstance {
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SpellScrollTeleport();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		// 처리
		if(Teleport.onBuff(cha, cbp, getBress(), true, true))
			cha.toTeleport(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap(), true);
		// 수량 하향
		cha.getInventory().count(this, getCount()-1, true);
	}

}
