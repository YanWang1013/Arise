package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.magic.monster.CurseGhoul;

public class RemoveCurse {

	static public void init(Character cha, Skill skill, int object_id){
		// 초기화
		object o = null;
		// 타겟 찾기
		if(object_id == cha.getObjectId())
			o = cha;
		else
			o = cha.findInsideList( object_id );
		// 처리
		if(o != null){
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
			if(SkillController.isMagic(cha, skill, true))
				onBuff(o, skill);
		}
	}
	
	static public void onBuff(object o, Skill skill){
		o.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), o, skill.getCastGfx()), true);

		// 커스: 포이즌
		BuffController.remove(o, CursePoison.class);
		// 커스: 블라인드
		BuffController.remove(o, CurseBlind.class);
		// 커스: 패럴라이즈
		BuffController.remove(o, CurseParalyze.class);
		// 구울 독
		BuffController.remove(o, CurseGhoul.class);
	}
}
