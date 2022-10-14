package lineage.network.packet.server;

import lineage.bean.database.Item;
import lineage.bean.database.Shop;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.share.Lineage;
import lineage.world.controller.SlimeRaceController;
import lineage.world.object.instance.ShopInstance;

public class S_ShopBuy extends S_Inventory {

	static public BasePacket clone(BasePacket bp, ShopInstance shop){
		if(bp == null)
			bp = new S_ShopBuy(shop);
		else
			((S_ShopBuy)bp).toClone(shop);
		return bp;
	}
	
	public S_ShopBuy(ShopInstance shop){
		toClone(shop);
	}
	
	public void toClone(ShopInstance shop){
		clear();

		writeC(Opcodes.S_OPCODE_SHOPBUY);
		writeD(shop.getObjectId());
		
		// 일반상점 구성구간.
		toShop(shop);
	}
	
	private void toShop(ShopInstance shop){
		writeH(shop.getNpc().getBuySize());
		
		for( Shop s : shop.getNpc().getShop_list() ){
			if(s.isItemBuy()){
				Item i = ItemDatabase.find(s.getItemName());
				if(i != null){
					writeD(s.getUid());
					writeH(i.getInvGfx());
					if(s.getPrice() != 0)
						writeD( shop.getTaxPrice(s.getPrice(), false) );
					else
						writeD( shop.getTaxPrice(i.getShopPrice(), false) );

					if(i.getName().equalsIgnoreCase("슬라임 레이스표")){
						writeS(SlimeRaceController.SlimeRaceTicketName(s.getUid()));
					}else{
						if(s.getItemCount()>1) {
							writeS(String.format("%s (%d)", i.getName(), s.getItemCount()));
						}else{
							writeS(i.getName());
						}
					}
					
					if(Lineage.server_version>144){
						if(i.getType1().equalsIgnoreCase("armor")){
							toArmor(i, 0, s.getItemEnLevel(), (int)i.getWeight(), 0);
						}else if(i.getType1().equalsIgnoreCase("weapon")){
							toWeapon(i, 0, s.getItemEnLevel(), (int)i.getWeight());
						}else{
							toEtc(i, (int)i.getWeight());
						}
					}
				}
			}
		}
	}
}
