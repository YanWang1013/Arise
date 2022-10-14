package lineage.world.object.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.bean.lineage.Kingdom;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_ShopBuy;
import lineage.network.packet.server.S_ShopSell;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.object;

public class ShopInstance extends object {

	protected Npc npc;
	private List<String> list_html;
	// 상점 처리할때 사용되는 전역 변수.
	private List<ItemInstance> search_list;
	private List<ItemInstance> sell_list;
	// 성 정보
	protected Kingdom kingdom;

	public ShopInstance(Npc npc){
		this.npc = npc;
		list_html = new ArrayList<String>();
		search_list = new ArrayList<ItemInstance>();
		sell_list = new ArrayList<ItemInstance>();
		kingdom = null;
	}

	public Npc getNpc() {
		return npc;
	}

	public void setNpc(Npc npc) {
		this.npc = npc;
	}
	
	/**
	 * 현재 물가 추출.
	 * @return
	 */
	public int getTax(){
		return kingdom==null ? 0 : kingdom.getTaxRate();
	}

	/**
	 * 세금으로인한 차액을 공금에 추가하기.
	 * @param price
	 */
	public void addTax(int price){
		if(kingdom != null)
			kingdom.toTax(price, true, "shop");
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		if(action.equalsIgnoreCase("buy")){
			pc.toSender(S_ShopBuy.clone(BasePacketPooling.getPool(S_ShopBuy.class), this));
		}else if(action.equalsIgnoreCase("sell")){
			sell_list.clear();
			
			if(this instanceof SlimeraceInstance){
				// 디비 로그에 기록된 정보를 토대로 작성하기.
				Connection con = null;
				PreparedStatement st = null;
				ResultSet rs = null;
				try {
					con = DatabaseConnection.getLineage();
					// 로그 참고로 목록 만들기.
					st = con.prepareStatement("SELECT * FROM slimerace");
					rs = st.executeQuery();
					while(rs.next()){
						search_list.clear();
						pc.getInventory().findSlimeRaceTicket(rs.getInt("uid"), rs.getInt("race_idx"), search_list);
						// 가격 맞추기.
						for( ItemInstance item : search_list ){
							item.TempPrice = rs.getInt("price");
							sell_list.add(item);
						}
					}
				} catch (Exception e) {
					lineage.share.System.println(ShopInstance.class+" : toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp)");
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st, rs);
				}
				
			}else{
				for( Shop s : npc.getShop_list() ){
					// 판매할 수 있도록 설정된 목록만 처리.
					if(s.isItemSell()){
						search_list.clear();
						pc.getInventory().findDbName(s.getItemName(), search_list);
						for( ItemInstance item : search_list ){
							if(!item.isEquipped() && item.getItem().isSell() && (s.getItemEnLevel()==0 || s.getItemEnLevel()==item.getEnLevel())){
								if(item.getItem().getNameIdNumber()==61 || item.getItem().getNameIdNumber()==93 || item.getItem().getNameIdNumber()==773){
									// 화살 및 은화살은 가격책정하면 안됨.
									item.TempPrice = 0;
								}else{
									if(s.getPrice() != 0)
										item.TempPrice = getTaxPrice(s.getPrice(), true);
									else
										item.TempPrice = getTaxPrice(item.getItem().getShopPrice(), true);
								}
								sell_list.add(item);
							}
						}
					}
				}
			}
			if(sell_list.size() > 0)
				pc.toSender(S_ShopSell.clone(BasePacketPooling.getPool(S_ShopSell.class), this, sell_list));
			else
				pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "nosell"));
		}else if(action.indexOf("3")>0 || action.indexOf("6")>0 || action.indexOf("7")>0){
			list_html.clear();
			list_html.add(String.valueOf(getTax()));
			pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, action, null, list_html));
		}
	}

	@Override
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp){
		switch(cbp.readC()){
			case 0:	// 상점 구입
				toBuy(pc, cbp);
				break;
			case 1:	// 상점 판매
				toSell(pc, cbp);
				break;
		}
	}

	/**
	 * 상점 구매
	 */
	protected void toBuy(PcInstance pc, ClientBasePacket cbp){
		int count = cbp.readH();
		if(count>0 && count<=100){
			for(int j=0 ; j<count ; ++j){
				int item_idx = cbp.readD();
				long item_count = cbp.readD();
				if(item_count>0 && item_count<=1000){
					Shop s = npc.findShop(item_idx);
					if(s != null){
						Item i = ItemDatabase.find(s.getItemName());
						int shop_price = s.getPrice()!=0 ? getTaxPrice(s.getPrice(), false) : getTaxPrice(i.getShopPrice(), false);
						if(pc.getInventory().isAppend(i, item_count)){

							if(pc.getInventory().isAden(s.getAdenType(), shop_price*item_count, true)){
								// 공금 추가하기.
								// 아이템 갯수에 맞게 갯수 재 설정.
								long new_item_count = item_count * s.getItemCount();

								ItemInstance temp = pc.getInventory().find(s.getItemName(), s.getItemBress(), true);
								if(temp == null){
									// 겹칠수 있는 아이템이 존재하지 않을경우.

									if(i.isPiles()){
										temp = ItemDatabase.newInstance(i);
										temp.setObjectId(ServerDatabase.nextItemObjId());
										temp.setCount(new_item_count);
										temp.setEnLevel(s.getItemEnLevel());
										temp.setDefinite(true);
										pc.getInventory().append(temp, true);
									}else{
										for(int k=0 ; k<new_item_count ; ++k){
											temp = ItemDatabase.newInstance(i);
											temp.setObjectId(ServerDatabase.nextItemObjId());
											// 겜블 아이템은 겹칠일이 없어서 여기다가 넣음.
											if(s.isGamble()){
												temp.setEnLevel( getGambleEnLevel() );
											}else{
												temp.setEnLevel(s.getItemEnLevel());
												temp.setDefinite(true);
											}
											pc.getInventory().append(temp, true);
										}
									}

								}else{
									// 겹치는 아이템이 존재할 경우.
									pc.getInventory().count(temp, temp.getCount()+new_item_count, true);

								}
								// 아데나일때만 처리.
								if(s.getAdenType().equalsIgnoreCase("아데나")){
									// 세금으로인한 차액을 공금에 추가.
									if(s.getPrice() != 0)
										addTax( (int)((shop_price-s.getPrice())*item_count) );
									else
										addTax( (int)((shop_price-i.getShopPrice())*item_count) );
								}
								
							}else{
								// \f1아데나가 충분치 않습니다.
//								pc.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189) );
								ChattingController.toChatting(pc, String.format("%s가 충분치 않습니다.", s.getAdenType()), Lineage.CHATTING_MODE_MESSAGE);
								break;
							}

						}
					}
				}
			}
		}
	}

	/**
	 * 상점 판매
	 */
	protected void toSell(PcInstance pc, ClientBasePacket cbp){
		int count = cbp.readH();
		if(count>0 && count<=Lineage.inventory_max){
			for(int i=0 ; i<count ; ++i){
				int inv_id = cbp.readD();
				long item_count = cbp.readD();
				ItemInstance temp = pc.getInventory().value(inv_id);
				if(temp!=null && !temp.isEquipped() && item_count>0 && temp.getCount()>=item_count){
					Shop s = npc.findShopItemId(temp.getItem().getName());
					// 판매될수 있는 아이템만 처리.
					if(s!=null && s.isItemSell()){
						// 아덴 지급
						if(temp.TempPrice>0){
							ItemInstance aden = pc.getInventory().find(s.getAdenType(), true);
							if(aden == null){
								aden = ItemDatabase.newInstance(ItemDatabase.find(s.getAdenType()));
								aden.setObjectId(ServerDatabase.nextItemObjId());
								aden.setCount(0);
								pc.getInventory().append(aden, true);
							}
							pc.getInventory().count(aden, aden.getCount()+(temp.TempPrice*item_count), true);
							// 세금계산은 아데나일때만 처리.
							if(s.getAdenType().equalsIgnoreCase("아데나")){
								// 세금으로인한 차액을 공금에 추가.
								if(s.getPrice() != 0)
									addTax((int)((s.getPrice()*0.5)-temp.TempPrice));
								else
									addTax((int)((temp.getItem().getShopPrice()*0.5)-temp.TempPrice));
							}
						}
						// 판매되는 아이템 제거.
						pc.getInventory().count(temp, temp.getCount()-item_count, true);
					}
				}
			}
		}
	}

	/**
	 * 설정된 세율에 따라 가격을 연산하여 리턴함.
	 * @param price
	 * @return
	 */
	public int getTaxPrice(double price, boolean sell){
		// sell 일경우에만 기본가격에 반값
		double a = sell ? price*0.5 : price;
		// 세율값 +@ 또는 -@ [원가에 지정된 세율만큼만]
		if(sell)
			a -= a*(getTax()*0.01);
		else
			a += a*(getTax()*0.01);
		// 반올림 처리.
		return (int)Math.round(a);
	}
	
	/**
	 * 겜블 상점에서 아이템 구매시 인첸트 값 추출해주는 함수.
	 * @return
	 */
	private int getGambleEnLevel(){
		int a = Util.random(0, 100);
		int b = Util.random(0, 100);
		int c = 0;
		if((a<=80)&&(b<=80)){
			c = Util.random(3, 6);
		}else if((a<=95)&&(b<=95)){
			c = Util.random(0, 2);
		}else{
			c = 7;
		}
		switch(c){
			case 0:
				switch(Util.random(0, 2)){
					case 0:
						return Util.random(6, 7);
					case 1:
						return Util.random(4, 7);
					case 2:
						return Util.random(2, 7);
				}
				break;
			case 1:
				switch(Util.random(0, 2)){
					case 0:
						return Util.random(5, 6);
					case 1:
						return Util.random(3, 6);
					case 2:
						return Util.random(1, 6);
				}
				break;
			case 2:
				switch(Util.random(0, 2)){
					case 0:
						return Util.random(4, 5);
					case 1:
						return Util.random(2, 5);
					case 2:
						return Util.random(0, 5);
				}
				break;
			case 3:
				switch(Util.random(0, 2)){
					case 0:
						return Util.random(3, 4);
					case 1:
						return Util.random(1, 4);
					case 2:
						return Util.random(0, 4);
				}
				break;
			case 4:
				switch(Util.random(0, 2)){
					case 0:
						return Util.random(2, 3);
					case 1:
						return Util.random(1, 2);
					case 2:
						return Util.random(0, 3);
				}
				break;
			case 5:
				switch(Util.random(0, 1)){
					case 0:
						return Util.random(1, 2);
					case 1:
						return Util.random(0, 2);
				}
				break;
			case 6:
				return 0;
			default:
				switch(Util.random(0, 2)){
					case 0:
						return Util.random(6, 7);
					case 1:
						return Util.random(3, 7);
					case 2:
						return Util.random(0, 7);
				}
				break;
		}
		return 0;
	}
}
