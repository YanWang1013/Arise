package lineage.world.object.item.scroll;

import lineage.bean.database.ItemTeleport;
import lineage.database.ItemTeleportDatabase;
import lineage.network.packet.ClientBasePacket;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class ScrollTeleport extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new ScrollTeleport();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		try {
			// 초기화
			int uid = Integer.valueOf( getItem().getType2().substring( getItem().getType2().indexOf("_")+1 ) );
			ItemTeleport it = ItemTeleportDatabase.find(uid);
			// 델레포트
			ItemTeleportDatabase.toTeleport(it, cha, true);
		} catch (Exception e) {
			lineage.share.System.printf("%s : toClick(Character cha, ClientBasePacket cbp)\r\n", ScrollTeleport.class.toString());
			lineage.share.System.println(e);
		}
		// 제거
		cha.getInventory().count(this, getCount()-1, true);
	}
}
