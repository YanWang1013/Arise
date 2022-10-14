package lineage.world.object.instance;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.world.controller.SlimeRaceController;
import lineage.world.object.item.SlimeRaceTicket;

public class SlimeraceInstance extends ShopInstance {

	public SlimeraceInstance(Npc n){
		super(n);
		
		// 슬라임 레이스표 넣기.
		for(int i=0 ; i<5 ; ++i){
			n.getShop_list().add( new Shop("슬라임 레이스표", 1, 1) );
			n.getShop_list().get(i).setUid(i);
		}
	}

	@Override
	public void toTalk(PcInstance pc, ClientBasePacket cbp){
		// 오성주 일경우 goraEv1
		// gora1 이 운영안한다는 내용이라서 goraEv1로 대처함.
		switch(SlimeRaceController.getStatus()){
			case STOP:
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gora5") );
				break;
			case CLEAR:
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "goraEV1") );
				break;
			case READY:
			case PLAY:
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gora3") );
				break;
			default:
				pc.toSender( S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gora2") );
				break;
		}
	}
	
	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("status")){
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "gora4", null, SlimeRaceController.getSlimeStatus()));
		}else{
			super.toTalk(pc, action, type, cbp);
		}
	}
	
	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		super.toTeleport(x, y, map, effect);
		// 관리목록에 등록.
		SlimeRaceController.appendNpc(this);
	}

	@Override
	protected void toBuy(PcInstance pc, ClientBasePacket cbp){
		// 경기시작되기 9분 대기할때만 구매할 수 있도록 하기위한것.
		switch(SlimeRaceController.getStatus()){
			case CLEAR:
				break;
			default:
				return;
		}

		int count = cbp.readH();
		if(count>0 && count<=100){
			for(int j=0 ; j<count ; ++j){
				int item_idx = cbp.readD();
				long item_count = cbp.readD();
				if(item_count>0 && item_count<=1000){
					Shop s = npc.findShop(item_idx);
					if(s != null){
						Item i = ItemDatabase.find(s.getItemName());
						// 가격 추출.
						int shop_price = s.getPrice()!=0 ? getTaxPrice(s.getPrice(), false) : getTaxPrice(i.getShopPrice(), false);
						// 추가 가능한가?
						if(pc.getInventory().isAppend(i, item_count)){
							// 아데나 확인.
							if(pc.getInventory().isAden(shop_price*item_count, true)){
								// 겹치는 슬라임레이스표 잇는지 확인.
								SlimeRaceTicket ticket = pc.getInventory().findSlimeRaceTicket(s.getSlimeraceUid(), s.getUid());
								if(ticket == null){
									// 겹칠수 있는 아이템이 존재하지 않을경우.
									ticket = (SlimeRaceTicket)ItemDatabase.newInstance(i);
									ticket.setObjectId(ServerDatabase.nextItemObjId());
									ticket.setCount(item_count);
									ticket.setSlimeRaceTicket( SlimeRaceController.SlimeRaceTicketName(s.getUid()) );
									pc.getInventory().append(ticket, true);
								}else{
									// 겹치는 아이템이 존재할 경우.
									pc.getInventory().count(ticket, ticket.getCount()+item_count, true);
								}
								// 세금으로인한 차액을 공금에 추가.
								addTax(shop_price-Lineage.slime_race_price);
								// 슬라임 레이스표 카운팅.
								SlimeRaceController.setCountting( item_idx, (int)item_count );
								
							}else{
								// \f1아데나가 충분치 않습니다.
								pc.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189) );
								break;
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void toSell(PcInstance pc, ClientBasePacket cbp){
		// 경기가 진행중일때는 무시하기.
		switch(SlimeRaceController.getStatus()){
			case READY:
			case PLAY:
				return;
		}

		int count = cbp.readH();
		if(count>0 && count<=Lineage.inventory_max){
			for(int i=0 ; i<count ; ++i){
				int inv_id = cbp.readD();
				long item_count = cbp.readD();
				ItemInstance temp = pc.getInventory().value(inv_id);
				if(temp!=null && !temp.isEquipped() && item_count>0 && temp.getCount()>=item_count){
					// 아덴 지급
					if(temp.TempPrice>0){
						ItemInstance aden = pc.getInventory().findAden();
						if(aden == null){
							aden = ItemDatabase.newInstance(ItemDatabase.find("아데나"));
							aden.setObjectId(ServerDatabase.nextItemObjId());
							aden.setCount(0);
							pc.getInventory().append(aden, true);
						}
						pc.getInventory().count(aden, aden.getCount()+(temp.TempPrice*item_count), true);
					}
					// 판매되는 아이템 제거.
					pc.getInventory().count(temp, temp.getCount()-item_count, true);
				}
			}
		}
	}
}
