package lineage.network.packet;

import java.util.ArrayList;
import java.util.List;

import lineage.share.TimeLine;

public final class BasePacketPooling {

	// 동적 생성된 패킷처리용 객체 담을 용도.
	static private List<BasePacket> pool;
	
	static public void init(){
		TimeLine.start("BasePacketPooling..");
		
		pool = new ArrayList<BasePacket>();
		
		TimeLine.end();
	}
	
	/**
	 * 풀목록에서 찾아서 리턴.
	 * @param c
	 * @return
	 */
	static public BasePacket getPool(Class<?> c){
		synchronized (pool) {
//			lineage.share.System.printf("pool : %d\r\n", pool.size());
			
			BasePacket bp = find(c);
			// 꺼낸 객체 목록에서 제거.
			if(bp!=null)
				pool.remove(bp);
			
//			lineage.share.System.printf("pool : %d\r\n", pool.size());
			return bp;
		}
	}
	
	static public void setPool(BasePacket bp){
		synchronized (pool) {
			// 최대값 설정. 너무 많이차면 서버가 견디질 못해서 ;;(java.lang.OutOfMemoryError: Java heap space 에러때문에.)
			if(isAppend(bp.getClass()))
				pool.add(bp);
			else
				bp = null;
			
//			lineage.share.System.printf("pool : %d\r\n", pool.size());
		}
	}
	
	/**
	 * 해당하는 객체 찾아서 리턴.
	 * @param c
	 * @return
	 */
	static private BasePacket find(Class<?> c){
		for(BasePacket b : pool){
			if(b.getClass().equals(c))
				return b;
		}
		return null;
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
	
	/**
	 * 풀링에 추가해도되는지 확인해주는 함수.
	 *  : 너무 많이 등록되면 문제가 되기대문에 적정선으로 카바..
	 * @param c
	 * @return
	 */
	static private boolean isAppend(Class<?> c){
		int count = 0;
		for(BasePacket b : pool){
			if(b.getClass().equals(c))
				count += 1;
		}
		return count < 50;
	}
}
