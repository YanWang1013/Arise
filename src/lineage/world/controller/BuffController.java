package lineage.world.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.lineage.Buff;
import lineage.bean.lineage.BuffInterface;
import lineage.network.Server;
import lineage.share.TimeLine;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public final class BuffController {

	static private List<Buff> pool_buff;
	static private List<BuffInterface> pool;
	static private Map<object, Buff> list;
	static private List<Buff> list_temp;
	
	/**
	 * 초기화 함수.
	 */
	static public void init(){
		TimeLine.start("BuffController..");
		
		pool_buff = new ArrayList<Buff>();
		pool = new ArrayList<BuffInterface>();
		list = new HashMap<object, Buff>();
		list_temp = new ArrayList<Buff>();
		
		TimeLine.end();
	}
	
	/**
	 * 월드 접속시 호출됨.
	 * @param o
	 */
	static public void toWorldJoin(object o){
		Buff b = find(o);
		if(b == null){
			b = getPool(o);
			synchronized (list) {
				list.put(o, b);
			}
		}
	}
	
	/**
	 * 월드에서 나갈때 호출됨.
	 * @param o
	 */
	static public void toWorldOut(object o){
		Buff b = find(o);
		if(b != null){
			setPool(b);
			synchronized (list) {
				list.remove(o);
			}
		}
	}
	
	/**
	 * 버프처리할 목록에 등록요청 처리하는 함수
	 * @param o		: 대상자
	 * @param bi	: 버프 객체
	 */
	static public void append(object o, BuffInterface bi){
		Buff b = find(o);
		if(b == null){
			b = getPool(o);
			synchronized (list) {
				list.put(o, b);
			}
		}
		b.append(bi);
	}
	
	/**
	 * 버프 강제 제거 요청 처리 함수.
	 * @param o
	 * @param c
	 */
	static public void remove(object o, Class<?> c){
		Buff b = find(o);
		if(b != null)
			b.remove(c);
	}
	
	/**
	 * 버프 강제로 제거 요청 처리 함수.
	 *  : 적용된 버프 전체 해당됨.
	 *  : Character.toReset 에서 사용중.
	 * @param o
	 * @param revival
	 */
	static public void removeAll(object o){
		try {
			Buff b = find(o);
			if(b != null)
				b.removeAll();
		} catch (Exception e) { }
	}
	
	/**
	 * 타이머에서 주기적으로 호출함.
	 */
	static public void toTimer(long time){
		synchronized (list) {
			list_temp.clear();
			list_temp.addAll(list.values());
		}
		// 등록된 목록 순회.
		for(Buff b : list_temp){
			try {
				b.toTimer(time);
			} catch (Exception e) {
				lineage.share.System.printf("%s : toTimer(long time)\r\n", BuffController.class.toString());
				lineage.share.System.println(e);

				// 에러발생한 버프는 제거처리.
				if(b.getObject() instanceof PcInstance){
					PcInstance pc = (PcInstance)b.getObject();
					try {
						pc.toWorldOut();
						Server.close(pc.getClient());
					} catch (Exception e2) { }
				}
				b.close();
			}
		}
	}
	
	/**
	 * 버프객체 재사용 관리 함수
	 * @param c
	 * @return
	 */
	static public BuffInterface getPool(Class<?> c){
		BuffInterface bi = null;
		
		synchronized (pool) {
			for(BuffInterface b : pool){
				if(b.getClass().equals(c)){
					bi = b;
					break;
				}
			}
			
			if(bi != null)
				pool.remove(bi);
		}
		
//		lineage.share.System.println("remove : "+pool.size());
		return bi;
	}
	
	/**
	 * 버프객체 재사용 관리 함수
	 * @param bi
	 */
	static public void setPool(BuffInterface bi){
		try {
			bi.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : setPool(BuffInterface bi)\r\n", BuffController.class.toString());
			lineage.share.System.println(e);
		}
		synchronized (pool) {
			pool.add(bi);
		}
		
//		lineage.share.System.println("append : "+pool.size());
	}
	
	/**
	 * 버프 관리자 재사용 관리 함수
	 * @return
	 */
	static public Buff getPool(object o){
		Buff b = null;
		synchronized (pool_buff) {
			if(pool_buff.size()>0){
				b = pool_buff.get(0);
				pool_buff.remove(0);
				b.setObject(o);
			}else{
				b = new Buff(o);
			}
		}
		
//		lineage.share.System.println("remove : "+pool_buff.size());
		return b;
	}
	
	/**
	 * 버프 관리자 재사용 관리 함수
	 * @param b
	 */
	static public void setPool(Buff b){
		b.close();
		synchronized (pool_buff) {
			pool_buff.add(b);
		}
		
//		lineage.share.System.println("append : "+pool_buff.size());
	}
	
	/**
	 * 버프 찾기
	 */
	static public Buff find(object o){
		synchronized (list) {
			return list.get(o);
		}
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
	
	static public int getPoolBuffSize(){
		return pool_buff.size();
	}
}
