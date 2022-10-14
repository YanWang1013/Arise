package lineage.world.object.item.wand;

import lineage.bean.database.Item;
import lineage.network.packet.ClientBasePacket;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.object;

public class StormWalk extends ItemInstance {
	public static synchronized ItemInstance clone(ItemInstance paramItemInstance) {
		if (paramItemInstance == null)
			paramItemInstance = new StormWalk();
		return paramItemInstance;
	}

	public ItemInstance clone(Item paramItem) {
		return super.clone(paramItem);
	}

	public void toClick(Character paramCharacter, ClientBasePacket paramClientBasePacket) {
		int i = paramClientBasePacket.readD();
		int j = paramClientBasePacket.readH();
		int k = paramClientBasePacket.readH();
		paramCharacter.setHeading(Util.calcheading(paramCharacter, j, k));
		Object localObject = null;
		if (i == paramCharacter.getObjectId())
			localObject = paramCharacter;
		else
			localObject = paramCharacter.findInsideList(i);
		long l1 = System.currentTimeMillis() / 1000L;
		if (paramCharacter.getDelaytime() + 2L > l1) {
			long l2 = paramCharacter.getDelaytime() + 2L - l1;
			ChattingController.toChatting(paramCharacter, l2 + "초간 지연시간이 필요합니다.", 20);
			return;
		}
		if (localObject == null)
			paramCharacter.toTeleport(j, k, paramCharacter.getMap(), false);
		else
			paramCharacter.toTeleport(((object) localObject).getX(), ((object) localObject).getY(),
					((object) localObject).getMap(), false);
		paramCharacter.setDelaytime(l1);
	}
}
