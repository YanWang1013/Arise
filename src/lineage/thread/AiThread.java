package lineage.thread;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.event.Ai;
import lineage.bean.event.DeleteObject;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.object;

public final class AiThread implements Runnable {

	static private List<AiThread> threadList;
	// 쓰레드동작 여부
	static private boolean running;
	
	// 인공지능 처리 목록
	private List<object> list;
	private List<object> appendList;
	private List<object> removeList;

	public AiThread(){
		removeList = new ArrayList<object>();
		appendList = new ArrayList<object>();
		list = new ArrayList<object>();
	}
	
	/**
	 * 초기화 처리 함수.
	 */
	static public void init(){
		TimeLine.start("AiThread..");
		
		threadList = new ArrayList<AiThread>();
		for(int i=0 ; i<Lineage.thread_ai ; ++i)
			threadList.add( new AiThread() );
		
		TimeLine.end();
	}
	
	/**
	 * 쓰레드 활성화 함수.
	 */
	static public void start(){
		running = true;
		int i = 0;
		for(AiThread at : threadList){
			Thread t = new Thread(at);
			t.setName( String.format("%s : %d", AiThread.class.toString(), i++) );
			t.start();
		}
	}
	
	/**
	 * 종료 함수
	 */
	static public void close(){
		running = false;
		threadList.clear();
	}
	
	/**
	 * 인공지능 활성화할 객체 등록 함수.
	 * @param o
	 */
	static public void append(object o){
		// 작업량 확인.
		int min_cnt = 0;
		AiThread ai = null;
		// 작업량 적은쪽 찾기.
		for(AiThread a : threadList){
			int count = a.getSizeAi();
			if(ai==null || count<=min_cnt){
				min_cnt = count;
				ai = a;
			}
		}
		// 요청.
		if(ai == null)
			threadList.get(0).appendAi(o);
		else
			ai.appendAi(o);
	}

	public void appendAi(object o){
		synchronized (appendList) {
			appendList.add(o);
		}
	}
	
	@Override
	public void run(){
		try {
			for(long time=0 ; running ;){
				Thread.sleep(Common.THREAD_SLEEP);
				time = System.currentTimeMillis();
				
				// System.currentTimeMillis() 방식을 버리므로서 성능향상과, 몬스터가 일정주기마다 동작안하는 문제 확인겸 변경함.
//				if(time < 0)
//					time = 0;
//				else
//					time += Common.THREAD_SLEEP;
				
				// 등록요청 처리 구간
				synchronized (appendList) {
					if(appendList.size()>0){
						list.addAll(appendList);
						appendList.clear();
					}
				}
				
				// 인공지능 처리 구간
				for(object o : list){
					// 인공지능에서 제거해야할 경우.
					if(o.getAiStatus()<0)
						removeList.add(o);
					// 인공지능 활성화 시간이 되엇을 경우.
					else if(o.isAi(time))
						EventThread.append( Ai.clone(EventThread.getPool(Ai.class), o, time) );
				}
				
				// 인공지능 제거 처리 구간
				for(object o : removeList){
					list.remove(o);
					EventThread.append( DeleteObject.clone(EventThread.getPool(DeleteObject.class), o) );
				}
				removeList.clear();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : run()\r\n", AiThread.class.toString());
			lineage.share.System.println(e.toString());
		}
	}
	
	public int getSizeAi(){
		return list.size() + appendList.size();
	}
	
	static public int getSize(){
		int count = 0;
		for(AiThread a : threadList)
			count += a.getSizeAi();
		
		return count;
	}
}
