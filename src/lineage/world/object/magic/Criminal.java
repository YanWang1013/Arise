package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectCriminal;
import lineage.share.Lineage;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Criminal extends Magic {

	public Criminal(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Criminal(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		o.setBuffCriminal(true);
		toBuffUpdate(o);
	}

	@Override
	public void toBuffUpdate(object o) {
		if(Lineage.server_version > 182)
			o.toSender(S_ObjectCriminal.clone(BasePacketPooling.getPool(S_ObjectCriminal.class), o, getTime()), true);
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		o.setBuffCriminal(false);
	}
	
	static public void init(Character cha){
		Skill s = SkillDatabase.find(206);
		if(s != null)
			BuffController.append(cha, Criminal.clone(BuffController.getPool(Criminal.class), s, s.getBuffDuration()));
	}

}
