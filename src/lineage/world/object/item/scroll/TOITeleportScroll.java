package lineage.world.object.item.scroll;

import lineage.network.packet.ClientBasePacket;
import lineage.world.controller.LocationController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;

public class TOITeleportScroll extends ItemInstance {

	private int f;
	
	static public ItemInstance clone(ItemInstance item, int f){
		if(item == null)
			item = new TOITeleportScroll();
		((TOITeleportScroll)item).setF(f);
		return item;
	}
	
	public void setF(int f){
		this.f = f;
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.getInventory().count(this, getCount()-1, true);

		if(LocationController.isTeleportZone(cha, true, true)){
			switch(f){
				case 11:
					cha.setHomeX(32631);
					cha.setHomeY(32935);
					cha.setHomeMap(111);
					break;
				case 21:
					cha.setHomeX(32631);
					cha.setHomeY(32935);
					cha.setHomeMap(121);
					break;
				case 31:
					cha.setHomeX(32631);
					cha.setHomeY(32935);
					cha.setHomeMap(131);
					break;
				case 41:
					cha.setHomeX(32631);
					cha.setHomeY(32935);
					cha.setHomeMap(141);
					break;
				case 51:
					cha.setHomeX(32668);
					cha.setHomeY(32815);
					cha.setHomeMap(151);
					break;
				case 61:
					cha.setHomeX(32668);
					cha.setHomeY(32815);
					cha.setHomeMap(161);
					break;
				case 71:
					cha.setHomeX(32668);
					cha.setHomeY(32815);
					cha.setHomeMap(171);
					break;
				case 81:
					cha.setHomeX(32668);
					cha.setHomeY(32815);
					cha.setHomeMap(181);
					break;
				case 91:
					cha.setHomeX(32668);
					cha.setHomeY(32815);
					cha.setHomeMap(191);
					break;
				default:
					cha.setHomeX(32693);
					cha.setHomeY(32902);
					cha.setHomeMap(200);
					break;
			}
			cha.toTeleport(cha.getHomeX(), cha.getHomeY(), cha.getHomeMap(), true);
		}
	}

}
