package lineage.plugin.bean;

import lineage.plugin.Plugin;

public class Main implements Plugin {
	
	/**
	 * 서버 시작됫을때 호출됨.
	 */
	public void toStart(){ }
	
	/**
	 * 서버 종료될때 호출됨.
	 */
	public void toEnd(){ }

	@Override
	public Object init(Class<?> c, Object... opt) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
