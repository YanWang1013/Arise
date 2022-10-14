package lineage.thread;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.event.Event;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;

public final class EventThread implements Runnable {

	static private EventThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	// 처리할 이벤트 목록
	static private List<Event> list;
	// 실제 처리되는 이벤트 목록
	static private List<Event> run;
	// 메모리 재사용을 위해
	static private List<Event> pool;
	
	/**
	 * 초기화 처리 함수.
	 */
	static public void init(){
		TimeLine.start("EventThread..");
		
		pool = new ArrayList<Event>();
		run = new ArrayList<Event>();
		list = new ArrayList<Event>();
		thread = new EventThread();
		
		TimeLine.end();
	}
	
	/**
	 * 쓰레드 활성화 함수.
	 */
	static public void start(){
		running = true;
		Thread t = new Thread(thread);
		t.setName("EventThread");
		t.start();
	}
	
	/**
	 * 쓰레드 종료처리 함수.
	 */
	static public void close(){
		running = false;
		thread = null;
	}
	
	/**
	 * 등록처리 함수.
	 * @param e
	 */
	static public void append(Event e){
		synchronized (list) {
			list.add(e);
		}
	}
	
	/**
	 * 풀에 저장된거 재사용을위해 사용.
	 * 있으면 리턴.
	 * @param c
	 * @return
	 */
	static public Event getPool(Class<?> c){
		synchronized (pool) {
//			lineage.share.System.println("pool1 : "+pool.size());
			
			Event e = findPool(c);
			if(e!=null)
				pool.remove(e);
			
//			lineage.share.System.println("pool2 : "+pool.size());
			return e;
		}
	}
	
	/**
	 * 찾는 객체 꺼내기.
	 *  : 반드시 이것으루 호출하는 코드내에서는 synchronized (pool) 정의되어 있어야 안전하다.
	 * @param c
	 * @return
	 */
	static private Event findPool(Class<?> c){
		for(Event e : pool){
			if(e.getClass().equals(c))
				return e;
		}
		return null;
	}
	
	@Override
	public void run(){
		try {
			for(;running;){
				Thread.sleep(Common.THREAD_SLEEP);
				// 이벤트 처리요청된거 옴기기
				synchronized (list) {
					if(list.size()>0){
						run.addAll(list);
						list.clear();
					}
				}
				// 실제 이벤트 처리 구간.
				if(run.size()>0){
					for(Event e : run){
						try {
							e.init();
						} catch (Exception e2) {
							lineage.share.System.printf("%s : %s\r\n", e.toString(), e2.toString());
						}
						e.close();
						// 재사용을위해 풀에 다시 넣기.
						synchronized (pool) {
//							lineage.share.System.println(e.toString()+" "+e.hashCode());
							// 갯수 확인.
							int count = 0;
							if(Lineage.pool_max != 0){
								for(Event ee : pool){
									if(ee.getClass().equals(e.getClass()))
										count += 1;
								}
							}
							if(Lineage.pool_max==0 || count<Lineage.pool_max)
								pool.add(e);
						}
					}
					run.clear();
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : run()\r\n", EventThread.class.toString());
			lineage.share.System.println(e.toString());
		}
	}
	
	static public int  getListSize(){
		return list.size();
	}
	
	static public int getRunSize(){
		return run.size();
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
	
}
