package lineage.plugin.bean;

import lineage.plugin.Plugin;

public class World implements Plugin {

	/**
	 * 타이머에서 주기적으로 호출됨.
	 * @param time
	 */
	public void toTimer(long time){}

	@Override
	public Object init(Class<?> c, Object... opt) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
