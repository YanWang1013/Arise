package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.world.object.instance.MonsterInstance;

public class Gremlin extends MonsterInstance {
	
	static public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Gremlin();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void toAiAttack(long time){
		// 도망모드로 전환.
		setAiStatus( Lineage.AI_STATUS_ESCAPE );
	}

}
