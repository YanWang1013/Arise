package lineage.bean.event;

import lineage.world.object.object;

public class Ai implements Event {
	
	private object o;
	private long time;
	
	/**
	 * 풀링에서 객체를 꺼냈는데 해당 객체가 null일수 있음. 그래서 이곳에서 동적으로 생성.
	 * @param e
	 * @param c
	 * @return
	 */
	static public Event clone(Event e, object o, long time){
		if(e == null)
			e = new Ai();
		((Ai)e).setObject(o);
		((Ai)e).setTime(time);
		return e;
	}

	@Override
	public void init() {
		try {
			o.toAi(time);
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", Ai.class.toString());
			lineage.share.System.println(e);
			
			// 에러낫을경우 해당 객체 제거처리.
			o.toAiThreadDelete();
		}
	}

	@Override
	public void close() {
		// 할거 없음..
	}

	public void setObject(object o) {
		this.o = o;
	}

	public void setTime(long time){
		this.time = time;
	}
}
