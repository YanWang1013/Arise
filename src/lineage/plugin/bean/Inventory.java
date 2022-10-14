package lineage.plugin.bean;

import lineage.plugin.Plugin;
import lineage.world.object.Character;

public class Inventory implements Plugin {

	/**
	 * 아이템을 들수있는 최대값을 추출.
	 */
	public double getMaxWeight(Character cha){
		return 0;
	}

	@Override
	public Object init(Class<?> c, Object... opt) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
