package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_TradeAddItem;
import lineage.network.packet.server.S_TradeStatus;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class Trade {
	private Character pc;
	private Character use;
	private List<ItemInstance> pc_list;
	private List<ItemInstance> use_list;
	private boolean pc_ok;
	private boolean use_ok;
	
	public Trade(){
		pc_list = new ArrayList<ItemInstance>();
		use_list = new ArrayList<ItemInstance>();
	}
	
	public void close(){
		pc = use = null;
		pc_ok = use_ok = false;
		pc_list.clear();
		use_list.clear();
	}
	
	/**
	 * 거래중인 물품에서 아데나 확인하는 함수.
	 *  : BuffRobotInstance.toTimer(long time) 에서 사용중.
	 * @param pc
	 * @param count
	 * @return
	 */
	public boolean isAden(boolean pc, long count){
		ItemInstance aden = null;
		if(pc){
			for(ItemInstance ii : pc_list){
				if(ii.getItem().getNameIdNumber()==4)
					aden = ii;
			}
		}else{
			for(ItemInstance ii : use_list){
				if(ii.getItem().getNameIdNumber()==4)
					aden = ii;
			}
		}
		
		return aden!=null && aden.getCount()==count;
	}

	public Character getPc() {
		return pc;
	}

	public void setPc(Character pc) {
		this.pc = pc;
	}

	public Character getUse() {
		return use;
	}

	public void setUse(Character use) {
		this.use = use;
	}

	/**
	 * 사용자가 거래중 ok를 누르면 호출 되는 메서드.
	 * 거래중인 사용자들이 전부 ok를 누른 상태인지 확인함.
	 */
	public boolean isTrade(){
		return pc_ok && use_ok;
	}

	public void setOk(Character pc){
		if(pc.getObjectId()==this.pc.getObjectId())
			pc_ok = true;
		else
			use_ok = true;
	}

	/**
	 * 거래에 정보를 담는 bean객체에 등록된 승인자 및 요청자의 객체 정보와
	 * 매개변수 객체정보가 일치하는지 확인하는 메서드.
	 */
	public boolean isTrade(Character pc){
		return pc.getObjectId()==this.pc.getObjectId() || pc.getObjectId()==use.getObjectId();
	}

	public void append(Character pc, ItemInstance item){
		if(pc.getObjectId()==this.pc.getObjectId()){
			pc_list.add(item);
			this.pc.toSender(S_TradeAddItem.clone(BasePacketPooling.getPool(S_TradeAddItem.class), item, true));
			use.toSender(S_TradeAddItem.clone(BasePacketPooling.getPool(S_TradeAddItem.class), item, false));
		}else{
			use_list.add(item);
			this.pc.toSender(S_TradeAddItem.clone(BasePacketPooling.getPool(S_TradeAddItem.class), item, false));
			use.toSender(S_TradeAddItem.clone(BasePacketPooling.getPool(S_TradeAddItem.class), item, true));
		}
	}

	/**
	 * 거래가 취소될때 호출해서 사용하는 메서드.
	 * 사용자가 거래중 월드를 나갈때도 호출됨.
	 * 처리는 똑같으니..
	 */
	public void toCancel(){
		// 아이템 다시 인벤으로 옴기기.
		for( ItemInstance item : pc_list ){
			if(item.getItem()==null)
				continue;
			
			// 추가가능한지 확인함.
			if(pc.getInventory().isAppend(item, item.getCount(), false)){
				ItemInstance temp = pc.getInventory().find(item);
				if(temp!=null){
					pc.getInventory().count(temp, temp.getCount()+item.getCount(), true);
					ItemDatabase.setPool(item);
				}else{
					pc.getInventory().append( item, true );
				}
			}else{
				// 등록이 불가능할때 땅에 드랍하기.
				item.toTeleport(pc.getX(), pc.getY(), pc.getMap(), false);
			}
		}
		for( ItemInstance item : use_list ){
			if(item.getItem()==null)
				continue;
			
			if(use.getInventory().isAppend(item, item.getCount(), false)){
				ItemInstance temp = use.getInventory().find(item);
				if(temp!=null){
					use.getInventory().count(temp, temp.getCount()+item.getCount(), true);
					ItemDatabase.setPool(item);
				}else{
					use.getInventory().append( item, true );
				}
			}else{
				item.toTeleport(use.getX(), use.getY(), use.getMap(), false);
			}
		}
		// 교환창 닫기.
		pc.toSender(S_TradeStatus.clone(BasePacketPooling.getPool(S_TradeStatus.class), false));
		use.toSender(S_TradeStatus.clone(BasePacketPooling.getPool(S_TradeStatus.class), false));
		// 거래 취소된거 알리기.
		pc.toTradeCancel(use);
		use.toTradeCancel(pc);
	}

	/**
	 * 요청자와 승인자가 모두 승인을 햇을경우 호출되서
	 * 아이템을 서로 교환하도록 처리하는 메서드.
	 */
	public void toOk(){
		// 아이템 이동
		for( ItemInstance item : pc_list ){
			if(item.getItem()==null)
				continue;
			
			if(use.getInventory().isAppend(item, item.getCount(), false)){
				ItemInstance temp = use.getInventory().find(item);
				if(temp!=null){
					use.getInventory().count(temp, temp.getCount()+item.getCount(), true);
					ItemDatabase.setPool(item);
				}else{
					use.getInventory().append( item, true );
				}
			}else{
				item.toTeleport(use.getX(), use.getY(), use.getMap(), false);
			}
		}
		for( ItemInstance item : use_list ){
			if(item.getItem()==null)
				continue;
			
			if(pc.getInventory().isAppend(item, item.getCount(), false)){
				ItemInstance temp = pc.getInventory().find(item);
				if(temp!=null){
					pc.getInventory().count(temp, temp.getCount()+item.getCount(), true);
					ItemDatabase.setPool(item);
				}else{
					pc.getInventory().append( item, true );
				}
			}else{
				item.toTeleport(pc.getX(), pc.getY(), pc.getMap(), false);
			}
		}
		// 거래완료
		pc.toSender(S_TradeStatus.clone(BasePacketPooling.getPool(S_TradeStatus.class), true));
		use.toSender(S_TradeStatus.clone(BasePacketPooling.getPool(S_TradeStatus.class), true));
		// 거래 완료된거 알리기.
		pc.toTradeOk(use);
		use.toTradeOk(pc);
	}
	
}
