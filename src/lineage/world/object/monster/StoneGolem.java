package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.world.object.instance.MonsterInstance;

public class StoneGolem extends MonsterInstance {
	
	static public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new StoneGolem();
		return MonsterInstance.clone(mi, m);
	}

	// 스폰할때 랜덤으로 웅쿠리기.
	// 웅쿠린상태 주기적으로 체크하는 구간 필요.
	// 웅쿠린채 주기적으로 체크할때 주변에 객체가 2셀 내로 오면 일어나서 공격모드로 전환하기.
	
}
