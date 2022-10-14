package lineage.world.object.item;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemBundle;
import lineage.database.ItemBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Bundle extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new Bundle();
		return item;
	}
	
	private List<ItemBundle> list;
	
	public Bundle(){
		list = new ArrayList<ItemBundle>();
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		// 아이템 지급.
		ItemBundleDatabase.find(list, getItem().getName());
		for(ItemBundle ib : list){
			// 확률 체크
			if(Util.random(0, Lineage.item_bundle_chance) <= Util.random(0, ib.getItemChance())){
				// 메모리 생성 및 초기화.
				ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(ib.getItem()));
				ii.setCount( Util.random(ib.getItemCountMin(), ib.getItemCountMax()) );
				ii.setBress(ib.getItemBless());
				ii.setEnLevel(ib.getItemEnchant());
				cha.getInventory().append(ii, ii.getCount());
				// \f1%0%s 당신에게 %1%o 주었습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, getName(), ii.toString()));
				// 메모리 재사용.
				ItemDatabase.setPool(ii);
			}
		}
		// 수량 하향.
		cha.getInventory().count(this, getCount()-1, true);
	}

}
