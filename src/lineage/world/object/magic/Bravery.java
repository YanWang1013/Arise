package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectSpeed;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Bravery extends Magic {

	public Bravery(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Bravery(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBrave(true);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, Lineage.bravery_potion_frame ? Lineage.LINEAGE_CLASS_KNIGHT : 3, getTime()), true);
	}

	@Override
	public void toBuffUpdate(object o) {
		o.setBrave(true);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, Lineage.bravery_potion_frame ? Lineage.LINEAGE_CLASS_KNIGHT : 3, getTime()), true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o.isWorldDelete())
			return;
		o.setBrave(false);
		o.toSender(S_ObjectSpeed.clone(BasePacketPooling.getPool(S_ObjectSpeed.class), o, 1, 0, 0), true);
	}
	
	static public void init(Character cha, int time){
		if(cha.getSpeed() != 2)
			BuffController.append(cha, Bravery.clone(BuffController.getPool(Bravery.class), SkillDatabase.find(201), time));
		else
			BuffController.remove(cha, Slow.class);
	}
}
