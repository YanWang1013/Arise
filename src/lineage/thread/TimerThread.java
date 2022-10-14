package lineage.thread;

import lineage.bean.event.Timer;
import lineage.share.Common;
import lineage.share.TimeLine;

public final class TimerThread implements Runnable {

	static private TimerThread thread;
	// 쓰레드동작 여부
	static private boolean running;
	// 타이머 각 프레임별 타임라인값.
	static private long timeline;

	/**
	 * 초기화 함수.
	 */
	static public void init(){
		TimeLine.start("TimerThread..");
		
		thread = new TimerThread();
		
		TimeLine.end();
	}
	
	/**
	 * 쓰레드 활성화 함수.
	 */
	static public void start(){
		running = true;
		Thread t = new Thread(thread);
		t.setName("TimerThread");
		t.start();
	}
	
	/**
	 * 종료 함수
	 */
	static public void close(){
		running = false;
		thread = null;
	}
	
	@Override
	public void run(){
		try {
			/*for(long s=System.currentTimeMillis(),e=s+1000 ; running ; s+=Common.THREAD_SLEEP){
				Thread.sleep(Common.THREAD_SLEEP);
				
				// 매초 마다 처리
				if(s>=e){
					s = System.currentTimeMillis();
					
					EventThread.append( Timer.clone(EventThread.getPool(Timer.class), s) );
					
					timeline = System.currentTimeMillis()-s;
					e = (s+1000)-timeline;
				}
			}*/
			
			for(;running;){
				Thread.sleep(Common.TIMER_SLEEP);
				
				EventThread.append( Timer.clone(EventThread.getPool(Timer.class), System.currentTimeMillis()) );
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : run()\r\n", TimerThread.class.toString());
			lineage.share.System.println(e);
		}
	}
	
	static public long getTimeLine(){
		return timeline;
	}
}
