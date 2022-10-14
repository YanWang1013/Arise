package lineage.world.object.magic;

import lineage.bean.database.Skill;
import lineage.bean.lineage.BuffInterface;
import lineage.world.object.Character;
import lineage.world.object.object;

public class Magic implements BuffInterface {

	protected Character cha;	// 시전자
	protected Skill skill;		// 마법종류
	protected long time_end;	// 버프 종료될 시간값.
	
	public Magic(Character cha, Skill skill){
		this.cha = cha;
		this.skill = skill;
	}

	@Override
	public Skill getSkill() {
		return skill;
	}
	
	@Override
	public void setSkill(Skill skill){
		this.skill = skill;
	}

	@Override
	public void setTime(int time) {
		if(time>0)
			time_end = System.currentTimeMillis() + (time*1000);
		else
			time_end = time;
	}

	@Override
	public int getTime() {
		return time_end>0 ? (int)((time_end-System.currentTimeMillis())*0.001) : (int)time_end;
	}

	@Override
	public void setCharacter(Character cha) {
		this.cha = cha;
	}

	@Override
	public Character getCharacter() {
		return cha;
	}

	@Override
	public boolean isBuff(long time) {
		return time_end>=0 ? time_end>=time : true;
	}

	@Override
	public void close() {
		cha = null;
	}

	@Override
	public void toBuffStart(object o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toBuffUpdate(object o) {
		// TODO Auto-generated method stub
	}

	@Override
	public void toBuff(object o) {
		// TODO Auto-generated method stub
	}

	@Override
	public void toBuffStop(object o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toBuffEnd(object o) {
		// TODO Auto-generated method stub
		
	}

}
