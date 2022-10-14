package lineage.world.object.item.potion;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class HealingPotion extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new HealingPotion();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if( !isClick(cha) )
			return;
		
		// 패킷처리
		cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, getItem().getEffect()), true);
		// \f1기분이 좋아졌습니다.
		cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 77));
		// 체력 상승
		cha.setNowHp(cha.getNowHp()+Util.random(getItem().getDmgMin(), getItem().getDmgMax()));
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}
	
}
