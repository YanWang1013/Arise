package lineage.bean.event;

import lineage.network.Client;
import lineage.network.Server;

public class ClientClose implements Event {

	private Client c;
	
	public ClientClose(Client c){
		clone(c);
	}
	
	/**
	 * 풀링에서 객체를 꺼냈는데 해당 객체가 null일수 있음. 그래서 이곳에서 동적으로 생성.
	 * @param e
	 * @param c
	 * @return
	 */
	static public Event clone(Event e, Client c){
		if(e == null)
			e = new ClientClose(c);
		else
			((ClientClose)e).clone(c);
		return e;
	}
	
	/**
	 * 변수 복사를 위해.
	 * @param c
	 */
	public void clone(Client c){
		this.c = c;
	}
	
	@Override
	public void init() {
		// 서버관리목록에서 제거 및 후 처리.
		Server.close(c);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	
}
