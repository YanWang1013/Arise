package lineage.world.object.item;

import lineage.database.ItemDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public class RedSock extends ItemInstance {

	static private int[][] list_nice = {
		// name_id, count, bress, quantity
		{237, 5, 1, 0},	// 빨간 물약
		{235, 5, 1, 0},	// 주홍 물약
		{238, 5, 1, 0},	// 맑은 물약
		{234, 4, 1, 0},	// 초록 물약
		{257, 3, 1, 0},	// 부활 주문서
		{28, 1, 1, 5},	// 소나무 막대
		{249, 1, 1, 0},	// 젤
		{249, 1, 0, 0},	// 축 젤
		{27, 1, 1, 5},	// 단풍 막대
		{244, 1, 1, 0},	// 데이
		{244, 1, 0, 0},	// 축 데이
	};
	static private int[][] list_bad = {
		// name_id, count, bress, quantity
		{237, 5, 1, 0},	// 빨간 물약
		{235, 5, 1, 0},	// 주홍 물약
		{238, 5, 1, 0},	// 맑은 물약
		{234, 4, 1, 0},	// 초록 물약
		{257, 3, 1, 0},	// 부활 주문서
		{971, 2, 1, 0},	// 변신주문서
		{971, 3, 1, 0},	// 변신주문서
		{971, 4, 1, 0},	// 변신주문서
		{232, 3, 1, 0},	// 파란물약
		{232, 4, 1, 0},	// 파란물약
		{232, 5, 1, 0},	// 파란물약
	};
	
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new RedSock();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		if(!(cha instanceof PcInstance))
			return;

		// 정보 초기화.
		PcInstance pc = (PcInstance)cha;
		int[][] list_item = pc.getPkCount()>0 ? list_bad : list_nice;
		int pos = -1;
		if(cha.getLawful() >= Lineage.NEUTRAL+32000){
			if(pc.getLevel()>40)
				pos = 10;
			else if(pc.getLevel()>=30)
				pos = 9;
			else
				pos = 8;
		}else if(cha.getLawful() >= Lineage.NEUTRAL+28000){
			if(pc.getLevel()>40)
				pos = 7;
			else if(pc.getLevel()>=30)
				pos = 6;
			else
				pos = 5;
		}else{
			if(cha.getLawful() >= Lineage.NEUTRAL+25000)
				pos = 4;
			else if(cha.getLawful() >= Lineage.NEUTRAL+18000)
				pos = 3;
			else if(cha.getLawful() >= Lineage.NEUTRAL+5000)
				pos = 2;
			else if(cha.getLawful() >= Lineage.NEUTRAL+500)
				pos = 1;
			else if(cha.getLawful() >= Lineage.NEUTRAL)
				pos = 0;
		}
		
		// 아이템 지급.
		if(pos >= 0){
			pos = Util.random(0, pos);
			ItemInstance ii = ItemDatabase.newInstance(ItemDatabase.find(list_item[pos][0]));
			if(ii != null){
				ii.setCount(list_item[pos][1]);
				ii.setBress(list_item[pos][2]);
				ii.setQuantity(list_item[pos][3]);
				cha.toGiveItem(this, ii, ii.getCount());
			}
		}
		
		// 아이템 수량 갱신
		cha.getInventory().count(this, getCount()-1, true);
	}

}
