package lineage.world.object.item.wand;

import lineage.bean.database.Item;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ShapeChange;

public class MapleWand extends ItemInstance {

	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new MapleWand();
		return item;
	}

	@Override
	public ItemInstance clone(Item item){
		quantity = Util.random(5, 15);
		
		return super.clone(item);
	}
	
	@Override
	public void toClick(Character cha, ClientBasePacket cbp){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_WAND), true);

		if(quantity<=0){
			// \f1아무일도 일어나지 않았습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 79));
			return;
		}
		
		setQuantity(quantity-1);
		cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), this));

		int objid = cbp.readD();
		object o = null;
		if(objid==cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList(objid);
		
		// 사용자일경우 세이프존이 아닐때만 처리되게 하기.
		if(objid!=cha.getObjectId() && o instanceof PcInstance && World.isSafetyZone(o.getX(), o.getY(), o.getMap()))
			return;
		
		ShapeChange.onBuff(cha, o, null, 7200, true, true);
	}

}
