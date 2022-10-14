package lineage.network.packet.server;

import lineage.bean.lineage.Inventory;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public class S_InventoryList extends S_Inventory {

	static public BasePacket clone(BasePacket bp, Inventory inv){
		if(bp == null)
			bp = new S_InventoryList(inv);
		else
			((S_InventoryList)bp).clone(inv);
		return bp;
	}
	
	public S_InventoryList(Inventory inv){
		clone(inv);
	}
	
	public void clone(Inventory inv){
		clear();
		
		writeC(Opcodes.S_OPCODE_ITEMLIST);
		writeC(inv.getList().size());
		for(ItemInstance item : inv.getList()){
			writeD(item.getObjectId());
			if(item instanceof ItemWeaponInstance || item instanceof ItemArmorInstance){
				writeH(item.getItem().getEquippedSlot());
				writeH(item.getItem().getInvGfx());
				writeC(item.getBressPacket());
				writeD((int)item.getCount());
				writeC(item.isDefinite() ? 1 : 0);
				writeS(getName(item));
				if(item.isDefinite()){
					if(item instanceof ItemWeaponInstance){
						toWeapon(item);
					}else{
						toArmor(item);
					}
				}else{
					writeC(0x00);
				}
			}else{
				if(item.getBress()==0)
					writeC(item.getItem().getAction2());
				else
					writeC(item.getItem().getAction1());
				writeC(item.getQuantity());
				writeH(item.getItem().getInvGfx());
				writeC(item.getBressPacket());
				writeD((int)item.getCount());
				writeC(item.isDefinite() ? 1 : 0);
				writeS(getName(item));
				if(item.isDefinite())
					toEtc(item);
				else
					writeC(0x00);
			}
		}
	}
}
