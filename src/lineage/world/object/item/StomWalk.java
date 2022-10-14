package lineage.world.object.item;

import lineage.network.packet.ClientBasePacket;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class StomWalk extends ItemInstance {
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new StomWalk();
		return item;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		int obj_id = cbp.readD();
		int x = cbp.readH();
		int y = cbp.readH();
		
		// 방향 전환.
		cha.setHeading( Util.calcheading(cha, x, y) );
		cha.toTeleport(x, y, cha.getMap(), true); // 텔레포트
	
	}

}
