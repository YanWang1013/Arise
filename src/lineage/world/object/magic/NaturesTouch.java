package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.npc.kingdom.KingdomDoor;

public class NaturesTouch {
	
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
			if(SkillController.isMagic(cha, skill, true) && SkillController.isFigure(cha, o, skill, false, false)){
				if(o instanceof KingdomDoor){
					return;
				}

				// 만피 채우는거라는데..
				if(o instanceof Character){
					Character c = (Character)o;
					c.setNowHp( c.getTotalHp() );
					c.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), c, skill.getCastGfx()), true);
				}
			}
		}
	}
	
}
