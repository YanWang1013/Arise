package lineage.world.object.npc.guard;

import lineage.bean.database.Npc;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectHeading;
import lineage.share.Lineage;
import lineage.world.object.instance.GuardInstance;

public class SentryGuard extends GuardInstance {

	/**
	 * 보초병
	 * @param npc
	 */
	public SentryGuard(Npc npc){
		super(npc);
	}
	
	@Override
	protected void toAiWalk(long time){
		ai_time = SpriteFrameDatabase.find(gfx, gfxMode+Lineage.GFX_MODE_WALK);
		
		// 피커 찾기.
		toSearchPKer();
		
		// 스폰된 좌표와 다르다면 스폰된 좌표로 이동하도록 유도.
		if(x!=homeX || y!=homeY){
			toMoving(homeX, homeY, 0);
			return;
		}
		
		// 방향 맞추기.
		if(heading!=homeHeading){
			setHeading(homeHeading);
			toSender(S_ObjectHeading.clone(BasePacketPooling.getPool(S_ObjectHeading.class), this), false);
		}
	}
}
