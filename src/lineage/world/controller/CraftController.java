package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.lineage.Craft;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.share.TimeLine;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;

public final class CraftController {

	static private List<ItemInstance> temp_list;			// 제작 작업에 잠시 이용되는 변수.
	
	static public void init(){
		TimeLine.start("CraftController..");

		temp_list = new ArrayList<ItemInstance>();

		TimeLine.end();
	}
	
	/**
	 * 제작에 필요한 재료들이 현재 인벤토리에 있는지 확인해주는 함수.
	 * @param o
	 * @param list
	 * @param packet
	 * @return
	 */
	static public boolean isCraft(object o, List<Craft> list, boolean packet){
		if(o==null || o.getInventory()==null || list==null || list.size()==0)
			return false;

		boolean isCraft = true;
		int have_count = 0;
		for(Craft c : list){
			synchronized (temp_list) {
				// 초기화
				have_count = 0;
				temp_list.clear();
				// 검색
				o.getInventory().findDbName(c.getItem().getName(), temp_list);
				for(ItemInstance ii : temp_list){
					if(!ii.isEquipped())
						have_count += ii.getCount();
				}
				// 갯수 확인
				if(c.getCount()>have_count){
					// 에러 표현.
					if(packet){
						StringBuffer sb = new StringBuffer();
						sb.append(c.getItem().getName());
						if(c.getCount()-have_count > 1){
							sb.append(" (");
							sb.append( c.getCount()-have_count );
							sb.append(")");
						}
						// \f1%0%s 부족합니다.
						o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 337, sb.toString()));
					}
					isCraft = false;
				}
			}
		}
		return isCraft;
	}
	
	/**
	 * 현재 인벤에 재료들 제거 처리하는 함수.
	 * @param o
	 * @param list
	 * @return
	 */
	static public boolean toCraft(object o, List<Craft> list){
		if(o==null || o.getInventory()==null || list==null || list.size()==0)
			return false;
		
		long count = 0;
		long temp_count = 0;
		for(Craft c : list){
			synchronized (temp_list) {
				// 초기화
				count = c.getCount();
				temp_list.clear();
				// 검색 및 제거.
				o.getInventory().findDbName(c.getItem().getName(), temp_list);
				for(ItemInstance ii : temp_list){
					// 모두다 제거됐다면 해당 루프문 종료. 다음 재료 체크.
					if(count<=0)
						break;
					
					if(!ii.isEquipped()){
						// 체크하려는 수량 확인. 수량이 이상일 경우 0 으로 설정.
						temp_count = count;
						count = count>ii.getCount() ? count-ii.getCount() : 0;
						// 재료 제거.
						o.getInventory().count(ii, ii.getCount()-temp_count, true);
					}
				}
				// 필요한 재료갯수가 모두 제거되지 않앗을경우 실패.
				if(count > 0)
					return false;
			}
		}
		return true;
	}
	
	/**
	 * 사용자 인벤토리에 해당하는 아이템을 찾아서 그아이템에 갯수만큼 가격을 +@ 한후 최종값을 리턴함.
	 *  : 라이라 계약후 정산할때 사용중.
	 * @param pc		: 확인할 객체
	 * @param item_name	: 아이템 이름
	 * @param price		: 해당아이템에 1개당 가격
	 * @param del		: 인벤토리에서 제거할지 여부.
	 * @return
	 */
	static public int toCraft(PcInstance pc, String item_name, int price, boolean del){
		synchronized (temp_list) {
			int aden = 0;
			temp_list.clear();
			// 토템 검색
			pc.getInventory().findDbName(item_name, temp_list);
			// 아이템 제거.
			for(ItemInstance ii : temp_list){
				aden += ii.getCount() * price;
				if(del)
					pc.getInventory().count(ii, 0, true);
			}
			return aden;
		}
	}
	
	/**
	 * 제작요청된 아이템 지급처리 함수.
	 * @param o
	 * @param target
	 * @param item
	 * @param count
	 */
	static public void toCraft(object o, object target, Item item, long count, boolean packet){
		if(o==null || target==null || target.getInventory()==null || item==null || count<=0)
			return;
		
		// 객체 생성
		ItemInstance temp = ItemDatabase.newInstance(item);
		// 등록처리.
		target.getInventory().append(temp, count);
		// 메모리 재사용.
		ItemDatabase.setPool(temp);
		
		if(packet)
			// \f1%0%s 당신에게 %1%o 주었습니다.
			target.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 143, o.getName(), String.format("%s (%d)", item.getName(), count)));
	}
	
	/**
	 * 제작 가능한 최대값 추출.
	 * @param o
	 * @param list
	 * @return
	 */
	static public int getMax(object o, List<Craft> list){
		if(o==null || o.getInventory()==null || list==null || list.size()==0)
			return 0;
		
		int have_count = 0;
		int max = 0;
		// 재료별 소지하고있는 전체 갯수 추출 후 재료별 제작가능한 갯수 추출.
		for(Craft c : list){
			synchronized (temp_list) {
				// 초기화
				have_count = 0;
				temp_list.clear();
				// 검색
				o.getInventory().findDbNameId(c.getItem().getNameIdNumber(), temp_list);
				for(ItemInstance ii : temp_list){
					if(!ii.isEquipped())
						have_count += ii.getCount();
				}
				// 기록
				if(have_count>=c.getCount())
					c.setTempCraftMax( have_count/c.getCount() );
				else
					return 0;
			}
		}
		// 제작 가능한 최대값 추출.
		for(Craft c : list){
			if(max==0 || max>c.getTempCraftMax())
				max = c.getTempCraftMax();
		}
		return max;
	}
}
