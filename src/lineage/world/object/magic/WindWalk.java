package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;

public class WindWalk {
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(SkillController.isMagic(cha, skill, true)){
			// 패킷 처리
			cha.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), cha, skill.getCastGfx()), true);
			// 처리.
			Wafer.init(cha, skill.getBuffDuration());
		}
	}
	
}
