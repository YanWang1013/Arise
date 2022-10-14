package lineage.bean.event;

import lineage.network.Client;
import lineage.network.Server;

public class ClientConnect implements Event {

	private Client c;
	private long key;
	
	public ClientConnect(Client c, long key){
		clone(c, key);
	}
	
	/**
	 * 변수 복사를 위해.
	 * @param c
	 */
	public void clone(Client c, long key){
		this.c = c;
		this.key = key;
	}
	
	/**
	 * 풀링에서 객체를 꺼냈는데 해당 객체가 null일수 있음. 그래서 이곳에서 동적으로 생성.
	 * @param e
	 * @param c
	 * @return
	 */
	static public Event clone(Event e, Client c, long key){
		if(e == null)
			e = new ClientConnect(c, key);
		else
			((ClientConnect)e).clone(c, key);
		return e;
	}
	
	@Override
	public void init() {
		// 서버관리 목록에 등록 및 후 처리.
		Server.connect(c, key);
	}

	@Override
	public void close() {
		// 할거 없음..
	}

}
