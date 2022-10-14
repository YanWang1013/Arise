package lineage.world.object.magic.monster;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.MonsterSkill;
import lineage.bean.lineage.Buff;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAttackMagic;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.BuffController;
import lineage.world.controller.DamageController;
import lineage.world.controller.SkillController;
import lineage.world.object.object;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.ChillTouch;
import lineage.world.object.magic.CurseBlind;
import lineage.world.object.magic.CurseParalyze;
import lineage.world.object.magic.CursePoison;
import lineage.world.object.magic.EnergyBolt;
import lineage.world.object.magic.Heal;
import lineage.world.object.magic.Lightning;
import lineage.world.object.magic.SummonMonster;
import lineage.world.object.magic.Teleport;
import lineage.world.object.magic.Tornado;

public class ShareMagic {
	
	static private List<object> list = new ArrayList<object>();
	static private int x;
	static private int y;
	
	/**
	 * 몬스터 스킬 처리 함수.
	 *  : MonsterInstance 에 toAiAttack(long time) 에서 호출됨.
	 *  : monster_skill 에 등록된 정보를 참고로해서 처리함.
	 * @param mi
	 * @param o
	 * @param skill
	 * @return
	 */
	static public boolean init(MonsterInstance mi, object o, MonsterSkill skill, int x, int y, boolean bow){
		synchronized (list) {
			// 확율확인. 100이면 걍 무조건 시전 시도.
			if(skill.getChance()==100 || Util.random(1, 100)<=skill.getChance()){
				// 시전가능한 hp/mp 인지 확인. 거리안에있는지 확인.
				if( SkillController.isHpMpCheck(mi, skill) && Util.isDistance(mi, o, skill.getRange()) ){
					int action = SpriteFrameDatabase.findMode(mi.getGfx(), skill.getActionName());
					// 액션 취하는 딜레이 주기.
					mi.setAiTime( SpriteFrameDatabase.find(mi.getGfx(), action) );
					// 마법 시전.
					if(skill.getType().equalsIgnoreCase("magic_attack")){
						// 스킬이 안잡혀 있다면 마법 공식에 정상적인 처리를위해 기본으로 잡기.
						if(skill.getSkill()==null)
							// 에너지볼트
							skill.setSkill(SkillDatabase.find(1, 3));
						onBuff(mi, o, skill, action, skill.getCastGfx()==0 ? skill.getSkill().getCastGfx() : skill.getCastGfx(), true);
						
					}else if(skill.getType().equalsIgnoreCase("magic_none_range")){
						// 스킬이 안잡혀 있다면 마법 공식에 정상적인 처리를위해 기본으로 잡기.
						if(skill.getSkill()==null)
							// 토네이도
							skill.setSkill(SkillDatabase.find(7, 4));
						onBuff(mi, o, skill, action, skill.getCastGfx()==0 ? skill.getSkill().getCastGfx() : skill.getCastGfx(), true);
						
					}else if(skill.getType().equalsIgnoreCase("magic_target_range")){
						// 스킬이 안잡혀 있다면 마법 공식에 정상적인 처리를위해 기본으로 잡기.
						if(skill.getSkill()==null)
							// 라이트닝
							skill.setSkill(SkillDatabase.find(3, 0));
						onBuff(mi, o, skill, action, skill.getCastGfx()==0 ? skill.getSkill().getCastGfx() : skill.getCastGfx(), true);
						
					}else if(skill.getType().equalsIgnoreCase("magic_attack_and_buff")){
						// 스킬이 안잡혀 있다면 마법 공식에 정상적인 처리를위해 기본으로 잡기.
						if(skill.getSkill()==null)
							// 에너지볼트
							skill.setSkill(SkillDatabase.find(1, 3));
						EnergyBolt.init(mi, o, skill, action, skill.getCastGfx());
						onBuff(mi, o, skill, -1, 0, false);
						
					}
					// 물리 시전.
					else if(skill.getType().equalsIgnoreCase("attack")){
						mi.toAttack(o, x, y, bow, action, Util.random(skill.getMindmg(), skill.getMaxdmg()));
						
					}else if(skill.getType().equalsIgnoreCase("attack_none_range")){
						init(mi, action, skill.getRange(), skill.getCastGfx()==0 ? Lineage.GFX_MODE_EFFECT_NONE : skill.getCastGfx(), Util.random(skill.getMindmg(), skill.getMaxdmg()));
						
					}else if(skill.getType().equalsIgnoreCase("attack_target_range")){
						init(mi, o, action, skill.getRange(), skill.getCastGfx()==0 ? Lineage.GFX_MODE_EFFECT_NONE : skill.getCastGfx(), Util.random(skill.getMindmg(), skill.getMaxdmg()));
						
					}else if(skill.getType().equalsIgnoreCase("attack_target_range_and_buff")){
						init(mi, o, action, skill.getRange(), skill.getCastGfx()==0 ? Lineage.GFX_MODE_EFFECT_NONE : skill.getCastGfx(), Util.random(skill.getMindmg(), skill.getMaxdmg()));
						if(skill.getSkill()!=null)
							onBuff(mi, skill, -1, 0, false, list);
					}
					// 버프 시전.
					else if(skill.getType().equalsIgnoreCase("magic_buff")){
						if(skill.getSkill()!=null){
							// 텔레포트일때 범위내로 잇을경우 무시.
							if(skill.getSkill().getUid()==5 && Util.isDistance(mi, o, 3))
								return false;
							// 시전.
							onBuff(mi, o, skill, action, skill.getCastGfx(), true);
						}
						
					}else if(skill.getType().equalsIgnoreCase("magic_buff_range")){
						if(skill.getSkill()!=null)
							onBuff(mi, skill, action, skill.getRange());
						
					}else if(skill.getType().equalsIgnoreCase("magic_buff_me")){
						if(skill.getSkill()!=null)
							onBuff(mi, mi, skill, action, skill.getCastGfx(), true);
					}else if(skill.getType().equalsIgnoreCase("call")){
						toCall(mi, o, skill);
						
					}else if(skill.getType().equalsIgnoreCase("summon")){
						// 스킬이 안잡혀있다면 기본으로 서먼몬스터로 설정.
						if(skill.getSkill() == null)
							skill.setSkill(SkillDatabase.find(7, 2));
						// 이미 시전된 상태라면 무시.
						Buff buff = BuffController.find(mi);
						if(buff!=null && buff.find(SummonMonster.class)!=null)
							return false;
						// 처리.
						onBuff(mi, o, skill, action, skill.getCastGfx()==0 ? skill.getSkill().getCastGfx() : skill.getCastGfx(), true);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 몬스터를 중심으로 area 만큼 객체들에게 데미지 입힘.
	 * @param mi
	 * @param gfx_mode
	 * @param area
	 * @param effect
	 */
	static private void init(MonsterInstance mi, int gfx_mode, int area, int effect, int alpha_dmg){
		// 초기화
		x = 0;
		y = 0;
		list.clear();
		// 검색
		for(object o : mi.getInsideList()){
			if( Util.isDistance(mi, o, area) )
				onBuff(mi, o, o, alpha_dmg);
		}
		
		// 패킷 처리.
		if(x!=0 && y!=0)
			mi.setHeading( Util.calcheading(mi, x, y) );
		mi.toSender(S_ObjectAttackMagic.clone(BasePacketPooling.getPool(S_ObjectAttackMagic.class), mi, null, list, true, gfx_mode, 0, effect, x, y), false);
	}
	
	/**
	 * target 중심으로 area 만큼 객체들에게 데미지 입힘.
	 * @param mi
	 * @param o
	 * @param gfx_mode
	 * @param area
	 * @param effect
	 */
	static private void init(MonsterInstance mi, object target, int gfx_mode, int area, int effect, int alpha_dmg){
		// 초기화
		x = 0;
		y = 0;
		list.clear();
		// 검색
		onBuff(mi, target, target, alpha_dmg);
		for(object o : target.getInsideList()){
			if(o instanceof PcInstance && Util.isDistance(target, o, area))
				onBuff(mi, target, o, alpha_dmg);
		}
		
		// 패킷 처리.
		if(x!=0 && y!=0)
			mi.setHeading( Util.calcheading(mi, x, y) );
		mi.toSender(S_ObjectAttackMagic.clone(BasePacketPooling.getPool(S_ObjectAttackMagic.class), mi, null, list, true, gfx_mode, 0, effect, x, y), false);
		
	}
	
	/**
	 * 중복 코드 방지용.
	 * @param mi
	 * @param o
	 */
	static private void onBuff(MonsterInstance mi, object target, object o, int alpha_dmg){
		int dmg = DamageController.getDamage(mi, o, false, null, null, alpha_dmg);
		DamageController.toDamage(mi, o, dmg, Lineage.ATTACK_TYPE_WEAPON);
		if(dmg > 0){
			if(x==0 && y==0){
				x = o.getX();
				y = o.getY();
			}
			list.add(o);
		}
	}
	
	/**
	 * 중복코드 방지용.
	 * @param mi
	 * @param ms
	 * @param action
	 * @param effect
	 * @param check
	 * @param list
	 */
	static public void onBuff(MonsterInstance mi, MonsterSkill ms, int action, int effect, boolean check, List<object> list){
		for(object o : list)
			onBuff(mi, o, ms, -1, effect, false);
	}
	
	/**
	 * 중복 코드 방지용.
	 * @param mi
	 * @param o
	 * @param skill
	 * @param action
	 * @param area
	 */
	static public void onBuff(MonsterInstance mi, MonsterSkill ms, int action, int area){
		// 액션값이 정해졌을때만 패킷처리.
		if(action != -1)
			mi.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), mi, action), false);
		// 주변객체 검색해서 범위안에있는 애들만 처리.
		for(object o : mi.getInsideList()){
			if(Util.isDistance(mi, o, area))
				// 몬스터 액션 취할필요 없기때문에 -1로 지정.
				onBuff(mi, o, ms, -1, ms.getCastGfx(), false);
		}
	}
	
	/**
	 * 중복 코드 방지용.
	 * @param mi
	 * @param o
	 * @param ms
	 * @param action
	 * @param effect
	 * @param check
	 */
	static public void onBuff(MonsterInstance mi, object o, MonsterSkill ms, int action, int effect, boolean check){
		switch(ms.getSkill().getSkillLevel()){
			case 1:
				switch(ms.getSkill().getSkillNumber()){
					case 0:
						Heal.init(mi, o, ms, action, effect);
						break;
					case 3:
					case 5:
					case 6:
						EnergyBolt.init(mi, o, ms, action, effect);
						break;
					case 4:
						Teleport.init(mi, o, ms, action, effect, check);
						break;
				}
				break;
			case 2:
				switch(ms.getSkill().getSkillNumber()){
					case 1:
						ChillTouch.init(mi, ms, o, action, effect);
						break;
					case 2:
						CursePoison.init(mi, o, ms, action, effect, check);
						break;
					case 6:
					case 7:
						EnergyBolt.init(mi, o, ms, action, effect);
						break;
				}
				break;
			case 3:
				switch(ms.getSkill().getSkillNumber()){
					case 0:
					case 5:
						Lightning.init(mi, ms, o, action, effect);
						break;
					case 2:
						Heal.init(mi, o, ms, action, effect);
						break;
					case 3:
						CurseBlind.init(mi, o, ms, action, effect);
						break;
				}
				break;
			case 4:
				switch(ms.getSkill().getSkillNumber()){
					case 0:
						Lightning.init(mi, ms, o, action, effect);
						break;
					case 3:
						ChillTouch.init(mi, ms, o, action, effect);
						break;
					case 5:
						EnergyBolt.init(mi, o, ms, action, effect);
						break;
				}
				break;
			case 5:
				switch(ms.getSkill().getSkillNumber()){
					case 0:
						CurseParalyze.init(mi, o, ms, action, effect, check);
						break;
					case 1:
					case 5:
						EnergyBolt.init(mi, o, ms, action, effect);
						break;
					case 2:
						Heal.init(mi, o, ms, action, effect);
						break;
					case 7:
						CurseBlind.init(mi, o, ms, action, effect);
						break;
				}
				break;
			case 6:
				switch(ms.getSkill().getSkillNumber()){
					case 4:
					case 5:
						EnergyBolt.init(mi, o, ms, action, effect);
						break;
				}
				break;
			case 7:
				switch(ms.getSkill().getSkillNumber()){
					case 2:
						SummonMonster.init(mi, o, ms, action, effect, check);
						break;
					case 4:
						Tornado.init(mi, ms, action, effect);
						break;
				}
				break;
			case 8:
				switch(ms.getSkill().getSkillNumber()){
					case 0:
						Heal.init(mi, o, ms, action, effect);
						break;
					case 2:
						Tornado.init(mi, ms, action, effect);
						break;
					case 5:
						Tornado.init(mi, ms, action, effect);
						break;
				}
				break;
			case 10:
				switch(ms.getSkill().getSkillNumber()){
					case 1:
						Lightning.init(mi, ms, o, action, effect);
						break;
					case 3:
						Tornado.init(mi, ms, action, effect);
						break;
					case 4:
						EnergyBolt.init(mi, o, ms, action, effect);
						break;
				}
				break;
			case 100:
				switch(ms.getSkill().getSkillNumber()){
					case 0:
						CurseGhoul.init(mi, o, ms, action, effect, check);
						break;
				}
				break;
		}
	}
	
	/**
	 * 주변객체를 검색하여 자신에게로 텔레포트 시키는 함수.
	 * @param mi
	 * @param o
	 * @param skill
	 */
	static private void toCall(MonsterInstance mi, object o, MonsterSkill skill){
		// 초기화
		list.clear();
		list.add(o);
		// 호출하는객체에 좌표는 몬스터좌표내에 +-2 정도로
		int x = mi.getX();
		int y = mi.getY();
		int map = mi.getMap();
		
		// 선책된 객체 범위값을 참고해서 검색하기.
		// 범위값이 1이상이라면 그주변에 객체도 같이 호출하기위해 검색후 등록처리.
		// 유저만 소환하기.
		if(skill.getRange()>1){
			for(object oo : o.getInsideList()){
				if(!oo.isDead() && oo.getGm()==0 && oo instanceof PcInstance && Util.isDistance(o, oo, skill.getRange()) && !list.contains(oo))
					list.add(oo);
			}
		}
		// 찾은 객체들 처리하기.
		for(object oo : list){
			// 이팩트값이 존재한다면 그것으로 표현하기.
			if(skill.getCastGfx() > 0)
				oo.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), oo, skill.getCastGfx()), true);
			// 이팩트값이 없다면 기본 텔레포트 이팩트 처리.
			oo.toTeleport(x, y, map, skill.getCastGfx()==0);
		}
	}
}
