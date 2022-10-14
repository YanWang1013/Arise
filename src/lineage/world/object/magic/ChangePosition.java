package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectLock;
import lineage.util.Util;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;

public class ChangePosition {

	static public void init(Character cha, Skill skill, int object_id){
		// 타겟 찾기
		object o = cha.findInsideList( object_id );
		if(o!=null && SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)){
			// 투망상태 해제
			Detection.onBuff(cha);
			// 처리
			if((o instanceof PcInstance || o instanceof MonsterInstance) && Util.isAreaAttack(cha, o)){
				int temp_x = cha.getX();
				int temp_y = cha.getY();
				cha.toTeleport(o.getX(), o.getY(), o.getMap(), true);
				o.toTeleport(temp_x, temp_y, cha.getMap(), true);
				return;
			}
			// \f1마법이 실패했습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 280));
			cha.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
		}
	}

}
