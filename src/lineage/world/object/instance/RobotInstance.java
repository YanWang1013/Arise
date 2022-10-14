package lineage.world.object.instance;

import lineage.bean.lineage.Inventory;
import lineage.world.World;
import lineage.world.controller.InventoryController;
import lineage.world.object.Character;

public class RobotInstance extends Character {

	// 인벤토리
	private Inventory inv;

	public void toWorldJoin(){
		InventoryController.toWorldJoin(this);
		
		toTeleport(getX(), getY(), getMap(), false);
	}
	
	public void toWorldOut(){
		InventoryController.toWorldOut(this);

		clearList(true);
		World.remove(this);
	}

	@Override
	public void setInventory(Inventory inv){
		this.inv = inv;
	}
	
	@Override
	public Inventory getInventory(){
		return inv;
	}
	
}
