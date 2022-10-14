package lineage.world.object.item.helm;

import java.sql.Connection;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterSpMr;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.share.Lineage;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class HelmetMagicResistance extends ItemArmorInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new HelmetMagicResistance();
		return item;
	}
	
	@Override
	public void toEnchant(PcInstance pc, int en){
		super.toEnchant(pc, en);
		
		// 인첸을 성공했다면 마법망토는 mr값을 상승해야함.
		if(en!=0){
			int new_mr = getEnLevel();
			if(equipped){
				// 이전에 세팅값 빼기.
				pc.setDynamicMr(pc.getDynamicMr()-getDynamicMr());
				// 인첸에따른 새로운값 적용.
				pc.setDynamicMr(pc.getDynamicMr()+new_mr);
				pc.toSender(S_CharacterSpMr.clone(BasePacketPooling.getPool(S_CharacterSpMr.class), pc));
			}
			setDynamicMr(new_mr);
			if(Lineage.server_version <= 144){
				pc.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));
			}else{
				pc.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), this));
			}
		}
	}

	@Override
	public void toWorldJoin(Connection con, PcInstance pc){
		setDynamicMr(getEnLevel());
		if(Lineage.server_version >= 160)
			pc.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), this));
		super.toWorldJoin(con, pc);
	}

}
