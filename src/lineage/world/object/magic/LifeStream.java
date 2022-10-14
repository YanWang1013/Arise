package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.BuffController;
import lineage.world.controller.SkillController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;

public class LifeStream extends Magic {
	
	private BackgroundInstance lifestream;
	
	public LifeStream(Skill skill){
		super(null, skill);
		
		lifestream = new lineage.world.object.npc.background.LifeStream();
		lifestream.setGfx(2231);
		lifestream.setLight(6);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new LifeStream(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		lifestream.setObjectId(ServerDatabase.nextEtcObjId());
		lifestream.toTeleport(o.getX(), o.getY(), o.getMap(), false);
	}

	@Override
	public void toBuff(object o) {
		super.toBuff(o);
		// 2셀 내에 있는 객체 체력 상승 해주기
		for(object oo : lifestream.getInsideList()){
			if(!oo.isDead() && oo instanceof Character && Util.isDistance(lifestream, oo, 2)){
				long dx = oo.getX()-lifestream.getX();
				long dy = oo.getY()-lifestream.getY();
				int add_hp = 3 - ((int)Math.sqrt(dx*dx + dy*dy));
				if(add_hp>0)
					oo.setNowHp(oo.getNowHp() + Util.random(0, add_hp));
			}
		}
	}

	@Override
	public void toBuffUpdate(object o) {
		lifestream.toTeleport(o.getX(), o.getY(), o.getMap(), false);
	}

	@Override
	public void toBuffEnd(object o){
		lifestream.clearList(true);
		World.remove(lifestream);
	}
	
	static public void init(Character cha, Skill skill){
		cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_SPELL_NO_DIRECTION), true);
		if(SkillController.isMagic(cha, skill, true)){
			// 버프 등록
			BuffController.append(cha, LifeStream.clone(BuffController.getPool(LifeStream.class), skill, skill.getBuffDuration()));
		}
	}
	
}
