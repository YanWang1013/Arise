package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.database.SkillDatabase;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Blue extends Magic {

	public Blue(Skill skill){
		super(null, skill);
	}
	
	static public BuffInterface clone(BuffInterface bi, Skill skill, int time){
		if(bi == null)
			bi = new Blue(skill);
		bi.setSkill(skill);
		bi.setTime(time);
		return bi;
	}
		
	@Override
	public void toBuffStart(object o){
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicTicMp(cha.getDynamicTicMp()+8);
		}
	}

	@Override
	public void toBuffStop(object o){
		toBuffEnd(o);
	}

	@Override
	public void toBuffEnd(object o){
		if(o instanceof Character){
			Character cha = (Character)o;
			cha.setDynamicTicMp(cha.getDynamicTicMp()-8);
		}
	}
	
	static public void init(Character cha, int time){
		BuffController.append(cha, Blue.clone(BuffController.getPool(Blue.class), SkillDatabase.find(204), time));
	}
}
