package lineage.world.object.monster;

import lineage.bean.database.Monster;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectMode;
import lineage.share.Lineage;
import lineage.world.object.Character;
import lineage.world.object.instance.MonsterInstance;
public class Spartoi extends MonsterInstance {
	
	static public MonsterInstance clone(MonsterInstance mi, Monster m){
		if(mi == null)
			mi = new Spartoi();
		return MonsterInstance.clone(mi, m);
	}

	@Override
	public void readDrop(){
		super.readDrop();
		
	}
	
	@Override
	public void toTeleport(final int x, final int y, final int map, final boolean effect){
		// 땅속에 박힌 모드로 변경.
		// standby
		setGfxMode(Lineage.GFX_MODE_OPEN);
		// 처리
		super.toTeleport(x, y, map, effect);
		// 땅속에서 나오는 액션 취하기.
		// rise
		if(getGfxMode() == Lineage.GFX_MODE_OPEN){
			toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), this, Lineage.GFX_MODE_RISE), false);
			setGfxMode(Lineage.GFX_MODE_WALK);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), false);
			super.setRecess(false); // Felix : make sure set recess as false when rise at first
		}
	}
	// Felix : 2022-01-13
	// When Spartoi's Hp is less 30%, hide him into the ground
	@Override
	public void toDamage(Character cha, int dmg, int type){
		super.toDamage(cha, dmg, type);
		
		// When Spartoi's Hp is less 30%, hide him into the ground
		if(getNowHp() <= (getMaxHp()*0.3)){
			//if(Util.random(0, 100)<10){
				setRecess(true);
			//}
		}
	}
	
	@Override
	public void toAiRecess(long time){
		
		// user search
//		if(getNowHp() > (getMaxHp()*0.5)){
//			object o = SearchPlayer();
//			if(o != null){
//				toMagicalAttackEncounters((character)o);
//				return;
//			}
//		}
	}
	
	@Override
	public void setRecess(boolean recess) {
		if(isRecess() && !recess){
			setGfxMode(Lineage.GFX_MODE_RISE);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), true);
			setGfxMode(Lineage.GFX_MODE_WALK);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), true);
		}else{
			setGfxMode(Lineage.GFX_MODE_HIDE);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), true);
			setGfxMode(Lineage.GFX_MODE_OPEN);
			toSender(S_ObjectMode.clone(BasePacketPooling.getPool(S_ObjectMode.class), this), true);
		}
		super.setRecess(recess);
	}
	
	@Override
	// Felix: wizard magic "Detection" > hide monster search skill..
	public void toMagicalAttackEncounters(Character cha){
		if(!isDead() && isRecess()){
			setRecess(false);
			addAttackList(cha);
			setFight(true);
		}
	}
}
