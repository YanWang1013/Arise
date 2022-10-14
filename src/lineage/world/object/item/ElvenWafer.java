package lineage.world.object.item;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.magic.Wafer;

public class ElvenWafer extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ElvenWafer();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if( !isClick(cha) )
			return;
		
		// 요정만 처리
		if(cha.getClassType() == Lineage.LINEAGE_CLASS_ELF){
			// 이팩트 표현
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, getItem().getEffect()), true);
			// 버프 처리
			Wafer.init(cha, 300);
		}else{
			// 아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
		}
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}
}
