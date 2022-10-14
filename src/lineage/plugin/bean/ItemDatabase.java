package lineage.plugin.bean;

import lineage.bean.database.Item;
import lineage.plugin.Plugin;
import lineage.world.object.instance.ItemInstance;

public class ItemDatabase implements Plugin {

	public ItemInstance newInstance(Item item){
		return null;
	}

	@Override
	public Object init(Class<?> c, Object... opt) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
