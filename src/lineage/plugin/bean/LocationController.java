package lineage.plugin.bean;

import lineage.plugin.Plugin;
import lineage.world.object.object;

public class LocationController implements Plugin {

	/**
	 * 리스존 확인하여 좌표 변경하는 함수.
	 *  : home 값을 변경함.
	 * @param o
	 */
	public boolean toResetZone(object o){
		return false;
	}

	@Override
	public Object init(Class<?> c, Object... opt) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
