package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Trade;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_TradeStart;
import lineage.share.TimeLine;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public final class TradeController {

	private static List<Trade> list;
	private static List<Trade> pool;
	
	static public void init(){
		TimeLine.start("TradeController..");
		
		list = new ArrayList<Trade>();
		pool = new ArrayList<Trade>();
		
		TimeLine.end();
	}
	
	static public void toWorldJoin(Character pc){
		// 할거 없음.
	}
	
	static public void toWorldOut(Character pc){
		try {
			toTradeCancel( find(pc) );
		} catch (Exception e) { }
	}
	
	/**
	 * 종료 처리 함수.
	 */
	static public void close(){
		synchronized (list) {
			for(Trade t : list){
				t.toCancel();
				setPool(t);
			}
			list.clear();
		}
		pool.clear();
	}
	
	/**
	 * 거래 요청 처리 함수.
	 * @param pc
	 * @param use
	 */
	static public void toTrade(Character pc, Character use){
		if(use!=null && !use.isDead()){
			Trade p_t = find(pc);
			Trade u_t = find(use);
			if(p_t!=null || u_t!=null){
				if(p_t != null)
					// \f1당신은 이미 다른 사람과 거래중입니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 258));
				if(u_t != null)
					// \f1그는 이미 다른 사람과 거래중입니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 259));
			}else{
				p_t = getPool();
				p_t.setPc(pc);
				p_t.setUse(use);
				use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 252, pc.getName()));
				
				list.add(p_t);
			}
		}
	}

	/**
	 * 거래 요청에 대한 응답처리 메서드.
	 */
	static public void toTrade(Character pc, boolean yes){
		Trade t = find(pc);
		if(t != null){
			if(yes){
				// %0와의 거래를 시작합니다...
				t.getPc().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 254, t.getUse().getName()));
				t.getUse().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 254, t.getPc().getName()));
				// 교환창 띄우기
				t.getPc().toSender(S_TradeStart.clone(BasePacketPooling.getPool(S_TradeStart.class), t.getUse()));
				t.getUse().toSender(S_TradeStart.clone(BasePacketPooling.getPool(S_TradeStart.class), t.getPc()));
			}else{
				// %0%d 당신과의 거래를 거절하였습니다.
				t.getPc().toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 253, t.getUse().getName()));
				toTradeCancel(t);
			}
		}
	}

	/**
	 * 사용자가 거래취소를 하거명 호출됨.
	 * 또 그의외에 제거가 필요할때 호출해서 사용.
	 */
	static public void toTradeCancel(Character pc){
		toTradeCancel( find(pc) );
	}

	/**
	 * 사용자가 거래 확인을 눌렀을때 호출됨.
	 * @param pc
	 */
	static public void toTradeOk(Character pc){
		Trade t = find(pc);
		if(t != null){
			t.setOk(pc);
			if(t.isTrade()){
				t.toOk();
				list.remove(t);
				setPool(t);
			}
		}
	}
	
	/**
	 * 거래중인 목록에 아이템 추가처리하는 함수.
	 * @param pc
	 * @param item
	 * @param count
	 */
	static public void toTradeAddItem(Character pc, ItemInstance item, long count){
		Trade t = find(pc);
		if(t!=null && item!=null){
			// 인벤에서 제거 가능한지 확인.
			if(pc.getInventory().isRemove(item, count, true, false)){
				if(item.getCount()-count<=0){
					// 모두 꺼내는거라면 인벤에서 제거
					pc.getInventory().remove(item, true);
					// 모두 넘기는 아이템에 한해서만 버프가 지정되잇는 것이므로 여기에만 넣음.
					item.toDrop(pc);
				}else{
					// 일부분만 꺼내는거라면 수량변경하고 객체 새로 생성.
					pc.getInventory().count(item, item.getCount()-count, true);
					item = ItemDatabase.newInstance(item);
					item.setCount(count);
				}
				// 교환창에 등록.
				t.append(pc, item);
			}
		}
	}

	/**
	 * 사용자가 거래취소를 하거나 월드를 나갈때 호출됨.
	 * 또 그의외에 제거가 필요할때 호출해서 사용.
	 */
	static private void toTradeCancel(Trade t){
		if(t != null){
			try {
				t.toCancel();
			} catch (Exception e) {
				lineage.share.System.println(TradeController.class+" : toTradeCancel(Trade t)");
				lineage.share.System.println(e);
			}
			list.remove(t);
			setPool(t);
		}
	}

	/**
	 * 매개변수의 pc가 현재 거래에 쓰이고있는 bean이 등록되어 있는지 확인하는 메서드.
	 *  : BuffRobotInstance.toTimer(long time) 에서 사용중.
	 */
	static public Trade find(Character pc){
		for( Trade t : list ){
			if(t.isTrade(pc))
				return t;
		}
		return null;
	}
	
	static private Trade getPool(){
		Trade t = null;
		if(pool.size()>0){
			t = pool.get(0);
			pool.remove(0);
		}else{
			t = new Trade();
		}
		return t;
	}
	
	static private void setPool(Trade t){
		if(t != null){
			t.close();
			pool.add(t);
		}
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
}
