package lineage.world.controller;

import lineage.bean.database.Skill;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.ServerDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ClanWar;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BackgroundInstance;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.GuardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.magic.AbsoluteBarrier;
import lineage.world.object.magic.CursePoison;
import lineage.world.object.magic.Detection;
import lineage.world.object.magic.EraseMagic;
import lineage.world.object.magic.FogOfSleeping;
import lineage.world.object.magic.Meditation;
import lineage.world.object.npc.background.Cracker;
import lineage.world.object.npc.background.PigeonGroup;
import lineage.world.object.npc.guard.ElvenGuard;
import lineage.world.object.npc.kingdom.KingdomDoor;
import lineage.world.object.npc.kingdom.KingdomGuard;

public final class DamageController {

	static public void init(){
		TimeLine.start("DamageController..");
		TimeLine.end();
	}

//	private static final int[] strDmg = new int[128];
//	static { 		// STR 데미지 보정
//		for (int str = 0; str <= 8; str++) { strDmg[str] = -2; }  // 1~8는 -2
//		for (int str = 9; str <= 10; str++) { strDmg[str] = -1; } // 9~10는 -1
//		strDmg[11] = 0; strDmg[12] = 0; strDmg[13] = 1; strDmg[14] = 1; strDmg[15] = 2; strDmg[16] = 2;
//		strDmg[17] = 3; strDmg[18] = 3; strDmg[19] = 4; strDmg[20] = 4; strDmg[21] = 5; strDmg[22] = 5;
//		strDmg[23] = 6; strDmg[24] = 6; strDmg[25] = 6; strDmg[26] = 7; strDmg[27] = 7; strDmg[28] = 7;
//		strDmg[29] = 8; strDmg[30] = 8; strDmg[31] = 9; strDmg[32] = 9; strDmg[33] = 10; strDmg[34] = 11;
//		for (int str = 35; str <= 38; str++){ strDmg[str] = 12; } // 35 ~38는 12
//		for (int str = 39; str <= 42; str++){ strDmg[str] = 13; } // 39 ~42는 13
//		for (int str = 43; str <= 46; str++){ strDmg[str] = 14; } // 43 ~46는 14
//		for (int str = 47; str <= 48; str++){ strDmg[str] = 15; } // 47 ~48는 15
//		strDmg[49] = 16; strDmg[50] = 17;
//		int dmg = 18;
//		for (int str = 51; str <= 127; str++) { // 51~127은 2마다＋1
//			if (str % 2 == 1) { dmg++; }
//			strDmg[str] = dmg;
//		}
//	}
//
//	private static final int[] dexDmg = new int[128];
//	static { 	// DEX 데미지 보정
//		for (int dex = 0; dex <= 14; dex++) { dexDmg[dex] = 0; } // 0~14는 0
//		dexDmg[15] = 1; dexDmg[16] = 2; dexDmg[17] = 3; dexDmg[18] = 4; dexDmg[19] = 4;
//		dexDmg[20] = 4; dexDmg[21] = 5; dexDmg[22] = 5; dexDmg[23] = 5; dexDmg[24] = 6;
//		dexDmg[25] = 6; dexDmg[26] = 6; dexDmg[27] = 7; dexDmg[28] = 7; dexDmg[29] = 7;
//		dexDmg[30] = 8; dexDmg[31] = 8; dexDmg[32] = 8; dexDmg[33] = 9; dexDmg[34] = 9;
//		dexDmg[35] = 9;
//		for (int dex = 36; dex <= 39; dex++) { dexDmg[dex] = 10; } // 36~39는 10
//		for (int dex = 40; dex <= 43; dex++) { dexDmg[dex] = 11; } // 40~43는 11
//		dexDmg[44] = 12;
//		int dmg = 13;
//		for (int dex = 44; dex <= 127; dex++) { // 44~127은 3마다＋1 
//			if (dex % 3 == 1) { dmg++; }
//			dexDmg[dex] = dmg;
//		}
//	}


	/**
	 * 공격이 가해졌을때 공격성공여부를 처리하고
	 * 그후 이곳으로 오게 되는데 이부분에서는 데미지공식을 연산해도 되는지 여부를 판단하는 메서드.
	 * 예로 활일경우 화살이 없다면 false를 리턴하며, 사이하활인데 화살이 없을경우 true를 리턴한다.
	 * - PcInstance쪽에서도 호출해서 사용중. 패킷중에 화살이 없더라도 사이하활은 이팩트를 그려줘야 하기때문에 해당 메서드를 활용중.
	 */
	static public boolean isAttack(boolean bow, ItemInstance weapon, ItemInstance arrow) {
		if (bow) {
			if (weapon != null) {
				switch (weapon.getItem().getNameIdNumber()) {
				case 1821: // 사이하활
					return true;
				default:
					return arrow != null;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * cha 로부터 o 가 데미지를 입었을때 뒷처리를 담당.
	 * @param cha		: 가격자
	 * @param target	: 마법지정된 타격자
	 * @param o			: 데미지 줄 타격자
	 * @param dmg		: 데미지
	 * @param type		: 공격 방식
	 */
	static public void toDamage(Character cha, object o, int dmg, int type) {
		// Felix: Add o.isRecess ( check if target is recess mode and is 901 - Hide Dupelgenon)
	    if (dmg <= 0 || cha == null || o == null || cha.isDead() || o.isDead() || o.isLockHigh() || o.isBuffAbsoluteBarrier() || o.isRecess() || o.getGfx() == 901)
	        return;
	    
	    // 데미지 입었다는거 알리기.
	    o.toDamage(cha, dmg, type);

	    // hp 처리
	    o.setNowHp(o.getNowHp() - dmg);

	    // 투망상태 해제
	    Detection.onBuff(cha);
	    Detection.onBuff(o);

	    // 관련 버프 제거.
	    if (o.isBuffEraseMagic())
	        BuffController.remove(o, EraseMagic.class);
	    if (o.isBuffMeditation())
	        BuffController.remove(o, Meditation.class);
	    if (o.isBuffFogOfSleeping())
	        BuffController.remove(o, FogOfSleeping.class);

	    if (o.isDead()) {

	        // 죽엇을경우.
	        toDead(cha, o);

	        // GM: 타격 시 대미지 확인 출력
	        if (cha.getGm() >= Lineage.GMCODE) {
	            if (o instanceof Character) {
	                Character target = (Character) o;
	                String chaName = cha.getName();
	                String targetName = null;
	                if (target instanceof MonsterInstance) {
	                    MonsterInstance mon = (MonsterInstance) target;
	                    targetName = mon.getMonster().getName();
	                } else if (target instanceof NpcInstance) {
	                    NpcInstance npc = (NpcInstance) target;
	                    targetName = npc.getNpc().getName();
	                }
	                String damage = "DMG:" + dmg;
	                String Hp = "HP: " + o.getNowHp();
	                String msg = "\\fY[ " + chaName + " -> " + targetName + " ] " + damage + " / " + Hp;
	                ChattingController.toChatting(cha, msg, Lineage.CHATTING_MODE_MESSAGE);
	            }
	        }

	    } else {
	        // 공격자 분류별로 처리
	        if (cha instanceof PcInstance) {
	            PcInstance pc = (PcInstance) cha;
	            // 소환객체에게 알리기.
	            SummonController.toDamage(pc, o, dmg);
	            // 요정이라면 근처 경비에게 도움 요청.
	            toElven(pc, o);
	        }
	    }
	}

	/**
	 * 대미지 시스템 중심 부분.
	 */
	static public int getDamage(Character cha, object target, boolean bow, ItemInstance weapon, ItemInstance arrow, int alpha_dmg){
		double dmg = 0;

		// 앱솔상태 해제
		if(cha.isBuffAbsoluteBarrier())
			BuffController.remove(cha, AbsoluteBarrier.class);

		if(target == null)
			return 0;

		// 기본적으로 공격 불가능한 객체
		if(target instanceof ItemInstance || (!(target instanceof Cracker || target instanceof PigeonGroup) && target instanceof BackgroundInstance) || target instanceof BoardInstance)
			return 0;

		// 굳은상태라면 패스
		if(target.isLockHigh())
			return 0;

		// 앱솔 상태라면 패스
		if(target.isBuffAbsoluteBarrier())
			return 0;

		// 타켓이 죽은상태라면 패스
		if(target.isDead())
			return 0;

		// 자기자신을 공격할 순 없음.
		if(cha.getObjectId() == target.getObjectId())
			return 0;

		// 장애물 방해하는지 확인.
		if(!Util.isAreaAttack(cha, target))
			return 0;

		// 내성문이라면 공성중일때만 가능.
		if(target instanceof KingdomDoor){
			KingdomDoor kd = (KingdomDoor)target;
			if(kd.getKingdom()==null || kd.getNpc()==null)
				return 0;
			if(!kd.getKingdom().isWar() && kd.getNpc().getName().indexOf("내성문")>0)
				return 0;
		}

		if(cha instanceof PcInstance){
			dmg += toPcInstance((PcInstance)cha, target, bow, weapon, arrow);

		}else if(cha instanceof SummonInstance){
			dmg += toSummonInstance((SummonInstance)cha, target, bow);

		}else if(cha instanceof MonsterInstance){
			dmg += toMonsterInstance((MonsterInstance)cha, target, bow);

		}else if(cha instanceof GuardInstance){
			dmg += toGuardInstance((GuardInstance)cha, target, bow);
		}

		if(dmg <= 0){
			return 0;
		} else {
			if (alpha_dmg > 0)
				dmg += Util.random(0, alpha_dmg);

			// 버닝스피릿 1.5배
			if (cha.isBuffBurningSpirit() && Util.random(0, 99) < 50)
				dmg += dmg * 1.5;

			// 더블 브레이크 2배
			if (cha.isBuffBurningSpirit() && Util.random(0, 99) < 50)
				dmg += dmg * 2;

			// 이문투함 일땐 데미지 50% 만.
			if (target.isBuffImmuneToHarm())
				dmg *= Lineage.immune_to_harm;

			// 레벨에 따른 대미지 감소 주기
			if (target.getLevel() >= 50)
				dmg -= (target.getLevel() - 46) / 4;

			if (target instanceof Character) {
				Character c = (Character) target;
				// 방어력에 따른 데미지 감소.
				dmg -= Util.random(0, c.getTotalAc() * 0.25);
			}

			return (int) dmg;
		}
	}

	/**
	 * 사용자 데미지 추출 함수.
	 * @param pc
	 * @param target
	 * @param bow
	 * @param weapon
	 * @param arrow
	 * @return
	 */
	static private double toPcInstance(PcInstance pc, object target, boolean bow, ItemInstance weapon, ItemInstance arrow){
		boolean Small = true;
		double dmg = 0;
		if(World.isAttack(pc, target)){
			// 장로 변신상태라면 데미지 일정한거 주기.
			if (pc.getGfx() == 32)
				return Util.random(3, 9);

			// 데미지 연산
			if (isAttack(bow, weapon, arrow) && isHitFigure(pc, target, bow, weapon)) {
				if (weapon == null) {
					dmg += Util.random(0, 3);
					
				}else{
					// 큰몹인지 작은몹인지 설정
					if(target instanceof MonsterInstance){
						MonsterInstance mon = (MonsterInstance)target;
						Small = mon.getMonster().getSize().equalsIgnoreCase("small");
					}
					
					// 대미지 산출
					dmg += DmgFigure(pc, bow);
					dmg += DmgWeaponFigure(bow, weapon, arrow, Small, target);
					dmg += DmgPlus(pc, weapon, target, bow);

					if(target instanceof MonsterInstance){
						MonsterInstance mon = (MonsterInstance)target;
						if(mon.getMonster().getResistanceUndead()<0){
							double undead_dmg = Util.random(1, 10);
							int material = bow && arrow!=null ? arrow.getItem().getMaterial() : weapon.getItem().getMaterial();
							// 언덴드 몬스터일경우
							switch(material){
							case 14:	// 은
							case 17:	// 미스릴
							case 22:	// 오리하루콘
							case 2377:	// 블랙미스릴
								int a = mon.getMonster().getResistanceUndead();
								a = (~a) + 1;
								dmg += (int)(undead_dmg*(a*0.01) );
								break;
							}
						}
						// 손상을 시키는 몬스터일경우 장거리공격시 데미지 80%만 들어가도록 하기.
						if (mon.getMonster().isToughskin() && bow)
							dmg = (int) (dmg * 0.8);
					}
					// 무기 손상도 데미지 감소
					dmg -= weapon.getDurability();
				}
			}
		}
		return dmg;
	}

	static private double toSummonInstance(SummonInstance si, object target, boolean bow) {
		double dmg = 0;
		// 데미지 연산
		if (World.isAttack(si, target) && isHitFigure(si, target, bow, null)) {
			// 대미지 산출
			dmg += DmgFigure(si, bow);
			dmg += DmgPlus(si, null, target, bow);
			dmg += getMonsterLeveltoDamage(si);
		}
		return dmg * 0.5;
	}

	static private double toMonsterInstance(MonsterInstance mi, object target, boolean bow) {
		double dmg = 0;
		// 데미지 연산
		if (isHitFigure(mi, target, bow, null)) {
			// 대미지 산출
			dmg += DmgFigure(mi, bow);
			dmg += DmgPlus(mi, null, target, bow);
			dmg += getMonsterLeveltoDamage(mi);
		}
		return dmg * 0.6;
	}

	static private double toGuardInstance(GuardInstance gi, object target, boolean bow) {
		double dmg = 0;
		// 데미지 연산
		if (isHitFigure(gi, target, bow, null))
			dmg = gi instanceof KingdomGuard ? Util.random(10, 30) : Util.random(200, 300);
		return dmg;
	}

	/**
	 * 객체가 죽엇을때 그에따른 처리를 하는 함수.
	 * @param cha	: 가해자
	 * @param o		: 피해자
	 */
	static private void toDead(Character cha, object o){

		// 객체별 처리 구간.
		if(o instanceof PcInstance){
			PcInstance use = (PcInstance)o;
			Kingdom kingdom = KingdomController.findKingdomLocation(use);
			boolean exp_drop = o.getLevel()>9 && 
					(	cha instanceof NpcInstance || 
							cha instanceof MonsterInstance || 
							World.isNormalZone(o.getX(), o.getY(), o.getMap())
							);
			boolean item_drop = cha instanceof NpcInstance || 
					cha instanceof MonsterInstance || 
					!World.isCombatZone(o.getX(), o.getY(), o.getMap());
			boolean magic_drop = o.getLawful()<Lineage.NEUTRAL && !World.isCombatZone(o.getX(), o.getY(), o.getMap());
			boolean kingdom_war = 	use.getClassType()==Lineage.LINEAGE_CLASS_ROYAL && 	// 군주면서
					use.getClanId()>0 && // 혈이 있으면서
					kingdom!=null && // 외성 좌표 안쪽에 있으면서
					kingdom.isWar() && // 해당성이 공성전중이면서
					kingdom.getListWar().contains(use.getClanName()); // 공성전 선포를 한 상태라면.
					
					// 죽어도 경험치를 잃어버리지 않는 맵
					for(int map : Lineage.MAP_EXP_NOT_LOSE) {
						if(map == use.getMap())
							exp_drop = false;
					}
					
					// 죽어도 아이템을 드랍하지 않는 맵
					for(int map : Lineage.MAP_ITEM_NOT_DROP) {
						if(map == use.getMap())
							item_drop = false;
					}

					// 경험치 드랍 처리.
					if(exp_drop){
						if(kingdom!=null && kingdom.isWar()){
							if(Lineage.kingdom_player_dead_expdown)
								CharacterController.toExpDown(use);
						}else if(Lineage.player_dead_expdown){
							CharacterController.toExpDown(use);
						}
					}
					// 아이템 드랍 처리.
					if(item_drop){
						if(kingdom!=null && kingdom.isWar()){
							if(Lineage.kingdom_player_dead_itemdrop)
								CharacterController.toItemDrop(use);
						}else if(Lineage.player_dead_itemdrop){
							CharacterController.toItemDrop(use);
						}
					}
					// 마법 드랍 처리.
					if(magic_drop){
						for(int i=0 ; i<3 ; ++i){
							Skill s = SkillController.find(use, Util.random(-200, 200), false);
							if(s != null)
								SkillController.remove(use, s, false);
						}
					}
					// 공성전 패배 처리.
					if(kingdom_war){
						// 전쟁 관리 목록에서 제거.
						kingdom.getListWar().remove(use.getClanName());
						// 
						World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, kingdom.getClanName(), use.getClanName()));
					}
		}

		// pvp 처리
		if(cha instanceof PcInstance && o instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			PcInstance use = (PcInstance)o;

			// 컴뱃존이 아니면서 카오틱이 아니라면 pc를 피커로 판단. 보라돌이도.
			boolean is = !World.isCombatZone(use.getX(), use.getY(), use.getMap()) && use.getLawful()>=Lineage.NEUTRAL && !use.isBuffCriminal();
			// 공성존 인지 확인.
			Kingdom kingdom = KingdomController.findKingdomLocation(use);
			// 성존이고 공성중일경우 환경설정에서 피커 처리 하도록 되있을때만 처리하게 유도.
			if(kingdom!=null && kingdom.isWar())
				is = Lineage.kingdom_pvp_pk;
			// 혈전상태 확인. (혈전 상태일때 카오처리 할지 여부.)
			Clan clan = ClanController.find(pc);
			if(clan!=null && clan.getWarClan()!=null && clan.getWarClan().equalsIgnoreCase(use.getClanName()))
				is = false;
			// 피커 처리를 해도 된다면.
			if(is){
				// pkcount 상승
				pc.setPkCount(pc.getPkCount()+1);
				use.setDeathCount(use.getDeathCount()+1);
				// pk한 최근 시간값 기록. 만라였다면 한번 바줌.
				if(pc.getLawful() != Lineage.LAWFUL)
					pc.setPkTime( System.currentTimeMillis() );
				// 라우풀값 하향.
				if(pc.getLawful() >= Lineage.NEUTRAL)
					pc.setLawful(Lineage.NEUTRAL - (pc.getLevel() * 250));
				else
					pc.setLawful(pc.getLawful() - (pc.getLevel() * 250));
			}
		}

		// gvp 처리
		if (cha instanceof GuardInstance && o instanceof PcInstance) {
			GuardInstance gi = (GuardInstance) cha;
			PcInstance pc = (PcInstance) o;

			// 피케이한 이전기록이 남았을경우 그값을 초기화함.
			if (pc.getPkTime() > 0)
				pc.setPkTime(0);
		}
	}

	/**
	 * 몬스터의 레벨에따른 데미지 산출 메서드.
	 */
	
//	static private int getMonsterLeveltoDamage(MonsterInstance mi){
//		return ((mi.getLevel()/10) * 5) + 5;
//	}
	
	static private int getMonsterLeveltoDamage(MonsterInstance mi) {
		if (mi.getMaxHp() < 101) {
			return (int) Math.round(Util.random(mi.getLevel() * 1, mi.getLevel() * 1.3));
		} else if (mi.getMaxHp() < 201) {
			return (int) Math.round(Util.random(mi.getLevel() * 1.3, mi.getLevel() * 1.7));
		} else if (mi.getMaxHp() < 301) {
			return (int) Math.round(Util.random(mi.getLevel() * 1.7, mi.getLevel() * 2));
		} else if (mi.getMaxHp() < 401) {
			return (int) Math.round(Util.random(mi.getLevel() * 2, mi.getLevel() * 2.3));
		} else if (mi.getMaxHp() < 501) {
			return (int) Math.round(Util.random(mi.getLevel() * 2.3, mi.getLevel() * 2.5));
		} else if (mi.getMaxHp() < 1000) {
			return (int) Math.round(Util.random(mi.getLevel() * 2.5, mi.getLevel() * 3));
		} else if (mi.getMaxHp() < 5000) {
			return (int) Math.round(Util.random(mi.getLevel() * 3, mi.getLevel() * 6));
		} else {
			return (int) Math.round(Util.random(mi.getLevel() * 5, mi.getLevel() * 10));

		}
	}

	/**
	 * 추가적으로 적용되는 대미지 처리 부분
	 * - 버프상태에 따른처리
	 * @param cha		: 가격자
	 * @param item		: 착용한 무기
	 * @param target	: 몬스터인지 유저인지 구분 처리하기위한 객체정보
	 * @return			: 계산된 대미지 리턴
	 */
	static private double DmgPlus(Character cha, ItemInstance item, object target, boolean bow){
		double dmg = 0;
		if(item != null){
			ItemWeaponInstance weapon = (ItemWeaponInstance)item;
			// 인첸트 웨폰
			if(weapon.isBuffEnchantWeapon())
				dmg += 2;
			// 블레스웨폰 상태라면 랜타 데미지 2 추가
			if(weapon.isBuffBlessWeapon())
				dmg += Util.random(0, 2);
			// 쉐도우팽
			if(weapon.isBuffShadowFang())
				dmg += 5;
		}
		// 위크니스 상태라면 데미지 6 감소
		if(cha.isBuffWeakness())
			dmg -= 6;
		// 홀리써클 상태라면 랜타 데미지 2 추가
		//		if(buff->findMagic(52))
		//			dmg += Common::random(0, 2);
		// 글로잉오라
		if(cha.isBuffGlowingAura())
			dmg += 2;
		// 인첸트베놈
		if(cha.isBuffEnchantVenom() && Util.random(0, 99)<20)
			BuffController.append(target, CursePoison.clone(BuffController.getPool(CursePoison.class), null, SkillDatabase.find(2, 2), 32));

		// 장거리 공격일때.
		if (bow) {
			dmg += cha.getDynamicAddDmgBow();
			// 아이오브스톰
			if (cha.isBuffEyeOfStorm())
				dmg += 3;
			// 근접 공격일때.
		} else {
			dmg += cha.getDynamicAddDmg();
			// 버서커스
			if (cha.isBuffBerserks())
				dmg += 5;
		}
		// 홀리웨폰 상태라면 랜타 데미지 2 추가
		if (cha.isBuffHolyWeapon()) {
			if (target instanceof MonsterInstance) {
				MonsterInstance mon = (MonsterInstance) target;
				if (mon.getMonster().getResistanceUndead() < 0)
					dmg += 2;
			}
		}

		return dmg;
	}

	/**
	 * 인벤토리에서 무기를 찾은후 무기의 대미지 토탈값 리턴
	 */
	static private double DmgWeaponFigure(boolean bow, ItemInstance weapon, ItemInstance arrow, boolean Small, object target){
		double dmg = 0;
		dmg += weapon.getEnLevel();
		dmg += weapon.getItem().getAddDmg();
		
		// 축복무기 특화
		if (weapon.getBress() == 0 && Util.random(0, 100) <= 30)
			dmg += Util.random(0, 2);
		
		// 안전 인챈트 0 무기의 인챈트별 대미지 추가
		if (weapon.getItem().getSafeEnchant() == 0) {
			switch (weapon.getEnLevel()) {
			case 1:
				dmg += Util.random(0, dmg + 1);
				break;
			case 2:
				dmg += Util.random(0, dmg + 3);
				break;
			case 3:
				dmg += Util.random(0, dmg + 5);
				break;
			case 4:
				dmg += Util.random(0, dmg + 7);
				break;
			case 5:
				dmg += Util.random(0, dmg + 9);
				break;
			case 6:
				dmg += Util.random(0, dmg + 11);
				break;
			case 7:
				dmg += Util.random(0, dmg + 13);
				break;
			case 8:
				dmg += Util.random(0, dmg + 15);
				break;
			case 9:
				dmg += Util.random(0, dmg + 17);
				break;
			case 10:
				dmg += Util.random(0, dmg + 19);
				break;
			}
			
		// 안전 인챈트 6 무기의 인챈트별 대미지 추가	
		} else {
			switch (weapon.getEnLevel()) {
			case 7:
				dmg += Util.random(0, dmg + 1);
				break;
			case 8:
				dmg += Util.random(0, dmg + 3);
				break;
			case 9:
				dmg += Util.random(0, dmg + 5);
				break;
			case 10:
				dmg += Util.random(0, dmg + 7);
				break;
			case 11:
				dmg += Util.random(0, dmg + 9);
				break;
			case 12:
				dmg += Util.random(0, dmg + 11);
				break;
			case 13:
				dmg += Util.random(0, dmg + 13);
				break;
			case 14:
				dmg += Util.random(0, dmg + 15);
				break;
			case 15:
				dmg += Util.random(0, dmg + 17);
				break;
			default:
				dmg += Util.random(0, dmg * weapon.getEnLevel() - 7) * 0.4;
				break;
			}
		}
		
		// 기본 데미지 연산값 저장 변수
		int d = 0;
		// 큰몹 및 작은몹 구분하여 무기의 기본 데미지 추출
		if (Small)
			d += weapon.getItem().getDmgMin();
		else
			d += weapon.getItem().getDmgMax();

		// 활일경우 화살에 데미지 추출
		if( bow ){
			// 화살이 있을경우.
			if(arrow != null){
				if(Small)
					d += arrow.getItem().getDmgMin();
				else
					d += arrow.getItem().getDmgMax();
			}
		}

		d -= weapon.getDurability();
		
		// 추출된 무기데미지를 랜덤값으로 추출 [기본]
		if(d > 0) {
			d = Util.random(0, d);
		}
		// 사이하활일 경우 마법 데미지 추가
		if(weapon.getItem().getNameIdNumber()==1821) {
			d = Util.random(0, 6);
		}
		
		dmg += d;
		
		if (target instanceof PcInstance) dmg *= 1.2;

		return dmg;
	}

	/**
	 * 해당 객체의 스탯정보만으로 총 대미지 산출.
	 */
	static private double DmgFigure(Character cha, boolean bow) {
		double dmg = 0;
		if (bow) {
			// 활일경우
			// 순수스탯에 따른 보너스
			if (cha.getClassType() == Lineage.LINEAGE_CLASS_ELF)
				dmg += Util.random(0, cha.getLevel() / 10);
			// 스탯에따른 +@ 데미지
			if (cha.getTotalDex() <= 14)
				dmg += 0;
			else if (cha.getTotalDex() <= 15)
				dmg += 1;
			else if (cha.getTotalDex() <= 16)
				dmg += 2;
			else if (cha.getTotalDex() <= 17)
				dmg += 3;
			else if (cha.getTotalDex() <= 18)
				dmg += 4;
			else if (cha.getTotalDex() <= 19)
				dmg += 4;
			else if (cha.getTotalDex() <= 20)
				dmg += 4;
			else if (cha.getTotalDex() <= 21)
				dmg += 5;
			else if (cha.getTotalDex() <= 22)
				dmg += 5;
			else if (cha.getTotalDex() <= 23)
				dmg += 5;
			else if (cha.getTotalDex() <= 24)
				dmg += 6;
			else if (cha.getTotalDex() <= 25)
				dmg += 6;
			else if (cha.getTotalDex() <= 26)
				dmg += 6;
			else if (cha.getTotalDex() <= 27)
				dmg += 7;
			else if (cha.getTotalDex() <= 28)
				dmg += 7;
			else if (cha.getTotalDex() <= 29)
				dmg += 7;
			else if (cha.getTotalDex() <= 30)
				dmg += 8;
			else if (cha.getTotalDex() <= 31)
				dmg += 8;
			else if (cha.getTotalDex() <= 32)
				dmg += 8;
			else if (cha.getTotalDex() <= 33)
				dmg += 9;
			else if (cha.getTotalDex() <= 34)
				dmg += 9;
			else if (cha.getTotalDex() <= 35)
				dmg += 9;
			else if (cha.getTotalDex() <= 39)
				dmg += 10;
			else if (cha.getTotalDex() <= 43)
				dmg += 10;
			else if (cha.getTotalDex() <= 47)
				dmg += 10;
			else if (cha.getTotalDex() <= 50)
				dmg += 12;
			else if (cha.getTotalDex() <= 53)
				dmg += 12;
			else if (cha.getTotalDex() <= 55)
				dmg += 12;
			else if (cha.getTotalDex() <= 57)
				dmg += 13;
			else if (cha.getTotalDex() <= 60)
				dmg += 13;
			else if (cha.getTotalDex() <= 62)
				dmg += 14;
			else if (cha.getTotalDex() <= 65)
				dmg += 14;
			else if (cha.getTotalDex() <= 68)
				dmg += 15;
			else if (cha.getTotalDex() <= 70)
				dmg += 16;
			else if (cha.getTotalDex() <= 72)
				dmg += 17;
			else if (cha.getTotalDex() <= 75)
				dmg += 18;
			else if (cha.getTotalDex() <= 80)
				dmg += 19;
			else if (cha.getTotalDex() <= 82)
				dmg += 20;
			else if (cha.getTotalDex() <= 85)
				dmg += 21;
			else if (cha.getTotalDex() <= 88)
				dmg += 22;
			else if (cha.getTotalDex() <= 90)
				dmg += 23;
			else if (cha.getTotalDex() <= 93)
				dmg += 24;
			else if (cha.getTotalDex() <= 95)
				dmg += 25;
			else if (cha.getTotalDex() <= 97)
				dmg += 26;
			else if (cha.getTotalDex() <= 99)
				dmg += 27;
			else if (cha.getTotalDex() <= 100)
				dmg += 28;
			else
				dmg += 28 + (cha.getTotalStr() - 100);
			// 순수에따른 데미지 추가
			dmg += toOriginalStatBowDamage(cha);

			// 레벨에 따른 추타
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ELF:
				dmg += cha.getLevel() / 15;
				break;
			}
		} else {
			// 근접무기일경우
			// 순수스탯에 따른 보너스
			if (cha.getClassType() == Lineage.LINEAGE_CLASS_KNIGHT)
				dmg += Util.random(0, cha.getLevel() / 5);
			// 스탯에따른 +@ 데미지
			if (cha.getTotalStr() <= 8)
				dmg -= 2;
			else if (cha.getTotalStr() <= 10)
				dmg -= 1;
			else if (cha.getTotalStr() <= 12)
				dmg += 0;
			else if (cha.getTotalStr() <= 14)
				dmg += 1;
			else if (cha.getTotalStr() <= 16)
				dmg += 2;
			else if (cha.getTotalStr() <= 18)
				dmg += 3;
			else if (cha.getTotalStr() <= 20)
				dmg += 4;
			else if (cha.getTotalStr() <= 22)
				dmg += 5;
			else if (cha.getTotalStr() <= 25)
				dmg += 6;
			else if (cha.getTotalStr() <= 28)
				dmg += 7;
			else if (cha.getTotalStr() <= 30)
				dmg += 8;
			else if (cha.getTotalStr() <= 32)
				dmg += 9;
			else if (cha.getTotalStr() <= 33)
				dmg += 10;
			else if (cha.getTotalStr() <= 34)
				dmg += 11;
			else if (cha.getTotalStr() <= 38)
				dmg += 12;
			else if (cha.getTotalStr() <= 42)
				dmg += 13;
			else if (cha.getTotalStr() <= 46)
				dmg += 14;
			else if (cha.getTotalStr() <= 50)
				dmg += 15;
			else if (cha.getTotalStr() <= 54)
				dmg += 16;
			else if (cha.getTotalStr() <= 58)
				dmg += 17;
			else if (cha.getTotalStr() <= 60)
				dmg += 18;
			else if (cha.getTotalStr() <= 62)
				dmg += 19;
			else if (cha.getTotalStr() <= 65)
				dmg += 20;
			else if (cha.getTotalStr() <= 68)
				dmg += 21;
			else if (cha.getTotalStr() <= 70)
				dmg += 22;
			else if (cha.getTotalStr() <= 72)
				dmg += 23;
			else if (cha.getTotalStr() <= 75)
				dmg += 24;
			else if (cha.getTotalStr() <= 78)
				dmg += 25;
			else if (cha.getTotalStr() <= 80)
				dmg += 26;
			else if (cha.getTotalStr() <= 82)
				dmg += 27;
			else if (cha.getTotalStr() <= 85)
				dmg += 29;
			else if (cha.getTotalStr() <= 88)
				dmg += 30;
			else if (cha.getTotalStr() <= 90)
				dmg += 31;
			else if (cha.getTotalStr() <= 93)
				dmg += 32;
			else if (cha.getTotalStr() <= 95)
				dmg += 33;
			else if (cha.getTotalStr() <= 97)
				dmg += 35;
			else if (cha.getTotalStr() <= 99)
				dmg += 37;
			else if (cha.getTotalStr() <= 100)
				dmg += 40;
			else
				dmg += 40 + (cha.getTotalStr() - 100);

			// 순수에따른 데미지 추가
			dmg += toOriginalStatDamage(cha);

			// 레벨에 따른 추타
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_KNIGHT:
				dmg += cha.getLevel();
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				dmg += cha.getLevel() / 10;
				break;
			}
		}
		// 경험치 획득 페널티 부분에서 데미지 추가 혜택 적용 (본섭도 이와 같은 상황)
		if (cha.getLevel() >= 50)
			dmg += (cha.getLevel() - 50) * 0.3; // 0.7
		return dmg;
	}


	/**
	 * 공격 성공여부 처리
	 */
	static private boolean isHitFigure(Character cha, object target, boolean bow, ItemInstance weapon){
		int basic_flee = 0;		// 기본이되는 공격성공율
		int target_flee = 0;	// 타켓에 방어력 및 er에따른 회피율
		int max_flee = 95;
		int min_flee = 5;

		// 클레스별
		basic_flee += toHitLv(cha);
		
		// 무기 인첸
		if (weapon != null) {
			basic_flee += weapon.getEnLevel();
		}
		
		// 경비병의 공격 성공 여부 처리
		if (cha instanceof GuardInstance) {
			basic_flee = 100;
		}
		
		// 스탯에 따른 보정
		basic_flee += toHitDex(cha) + toHitStr(cha);

//		// 아래는 모르겠음.
//		int defenderDice = 0;
//		int defenderValue = 0;
//		int targetAc = 0;
//		if (target instanceof Character) {
//			Character c = (Character) target;
//			targetAc = 10 - c.getTotalAc();
//			defenderValue = (int) (targetAc * 1.2) * -1; // 105
//		}

		if (bow) {
			// 활착용상태라면
			basic_flee += toOriginalStatBowHit(cha) + cha.getDynamicAddHitBow();
			if (target instanceof Character) {
				
			// 활공격시 타켓에 er값을 추출함.
			Character c = (Character) target;
			target_flee = getEr(c) + toOriginalStatER(c);
		}
			// 윈드샷 상태일때 공격 성공률 적용 (활 공격에만 적용)
			if (cha.isBuffWindShot())
				basic_flee += 6;
			// 스톰샷 상태일때 공격 성공률 적용 (활 공격에만 적용)
			if (cha.isBuffStormShot())
				basic_flee -= 1;
			// 아이오브스톰
			if (cha.isBuffEyeOfStorm())
				basic_flee += 2;
			// 글로잉오라
			if (cha.isBuffGlowingAura())
				basic_flee += 1;
			// 레벨에따른 명중
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ELF:
				basic_flee += cha.getLevel() / 5;
				break;
			}
		}else{
			// 근접무기착용상태라면
			basic_flee += toOriginalStatHit(cha) + cha.getDynamicAddHit();
			if(target instanceof Character){
			
			// 근거리공격시 타켓에 ac따른 값 추출
			Character c = (Character)target;
			target_flee = c.getTotalAc()==0 ? 0 : c.getTotalAc() / 3;

			// 상대 레벨에따른 감소처리부분
			if(c.getLevel() >= cha.getLevel()+5)
				target_flee += c.getLevel() - (cha.getLevel()+5);

			// 언케니닷지 상태일때 DG(근거리 회피)+50의 효과
			if (target.isBuffUncannyDodge())
				basic_flee -= 50;
			}
			// 버닝웨폰 상태일때 공격 성공률 적용 (활이 아닌 경우)
			if(cha.isBuffBurningWeapon())
				basic_flee += 3;
			
			// 레벨에따른 명중
			switch (cha.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
			case Lineage.LINEAGE_CLASS_KNIGHT:
			case Lineage.LINEAGE_CLASS_DARKELF:
				basic_flee += cha.getLevel() / 3;
				break;
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
				basic_flee += cha.getLevel() / 4;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				basic_flee += cha.getLevel() / 5;
				break;
			// 법사 명중률 하향시키기
			case Lineage.LINEAGE_CLASS_WIZARD:
				basic_flee -= 1;
				break;
			}
		}

		// 블레스웨폰 상태일때 공격 성공률 증가 적용
		if(weapon!=null && ((ItemWeaponInstance)weapon).isBuffBlessWeapon())
			basic_flee += 2;
		
		// 위크니스 상태일때 공격 성공률 감소 적용
		if(cha.isBuffWeakness())
			basic_flee -= 1;
		
		// 홀리써클 상태일때 공격 성공률 증가 적용
		//		if(buff->findMagic(52))
		//			basic_flee += 2;
		
		// 디지즈 상태일때 공격 성공률 감소 적용
		if(cha.isBuffDisease())
			basic_flee -= 6;

		// 타켓에 회피율값에 따라 감소
		basic_flee -= target_flee;
		
		if(max_flee < basic_flee)
			basic_flee = max_flee;
		if(min_flee > basic_flee)
			basic_flee = min_flee;
		
		if(max_flee < target_flee)
			target_flee = max_flee;
		if(min_flee > target_flee)
			target_flee = min_flee;
		
		return Util.random(target_flee, 100) <= Util.random(basic_flee, 100);
	}
	
	/**
	 * 클레스별 추가 타격치 +@ 리턴
	 */
	static private double toOriginalStatDamage(Character cha){
		double sum = 0;
		int str = cha.getStr() + cha.getLvStr();
		switch(cha.getClassType()){
		case Lineage.LINEAGE_CLASS_ROYAL:
		case Lineage.LINEAGE_CLASS_KNIGHT:
		case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
		case Lineage.LINEAGE_CLASS_BLACKWIZARD:
			str -= 16;
			if(str>=2)
				sum += 1;
			if(str>=4)
				sum += 1;
			if(str>=6)
				sum += 1;
			if(str>=8)
				sum += 1;
			if(str>=10)
				sum += 1;
			if(str>=12)
				sum += 1;
			if(str>=14)
				sum += 1;
			if(str>=16)
				sum += 1;
			if(str>=18)
				sum += 1;
			if(str>=20)
				sum += 2;
			if(str>=22)
				sum += 1;
			if(str>=24)
				sum += 2;
			if(str>=26)
				sum += 1;				
			if(str>=28)
				sum += 2;
			if(str>=30)
				sum += 1;
			if(str>=32)
				sum += 2;
			if(str>=34)
				sum += 1;
			if(str>=36)
				sum += 2;
			if(str>=38)
				sum += 1;
			if(str>=40)
				sum += 2;
			if(str>=42)
				sum += 1;
			if(str>=44)
				sum += 2;
			if(str>=46)
				sum += 1;
			if(str>=48)
				sum += 2;
			if(str>=50)
				sum += 1;
			if(str>=52)
				sum += 2;
			if(str>=54)
				sum += 1;
			if(str>=55)
				sum += 2;
			if(str>=57)
				sum += 1;
			if(str>=58)
				sum += 2;
			if(str>=60)
				sum += 1;
			if(str>=62)
				sum += 2;
			if(str>=64)
				sum += 1;
			if(str>=66)
				sum += 2;
			if(str>=68)
				sum += 1;
			if(str>=70)
				sum += 2;
			if(str>=72)
				sum += 1;
			if(str>=74)
				sum += 2;
			if(str>=76)
				sum += 1;
			if(str>=78)
				sum += 2;
			if(str>=80)
				sum += 1;
			if(str>=82)
				sum += 2;
			if(str>=84)
				sum += 1;
			if(str>=86)
				sum += 2;
			if(str>=88)
				sum += 1;
			if(str>=90)
				sum += 2;
			if(str>=92)
				sum += 1;
			if(str>=93)
				sum += 2;
			if(str>=95)
				sum += 1;
			if(str>=96)
				sum += 2;
			if(str>=97)
				sum += 1;
			if(str>=98)
				sum += 2;
			if(str>=99)
				sum += 1;
			if(str>=100)
				sum += 2;
			if(str>=102)
				sum += 1;
			if(str>=105)
				sum += 2;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			str -= 14;
			if(str>=2)
				sum += 1;
			if(str>=4)
				sum += 1;
			if(str>=6)
				sum += 1;
			if(str>=8)
				sum += 1;
			if(str>=10)
				sum += 1;
			if(str>=12)
				sum += 1;
			if(str>=14)
				sum += 1;
			if(str>=16)
				sum += 1;
			if(str>=18)
				sum += 1;
			if(str>=20)
				sum += 2;
			if(str>=22)
				sum += 1;
			if(str>=24)
				sum += 2;
			if(str>=26)
				sum += 1;				
			if(str>=28)
				sum += 2;
			if(str>=30)
				sum += 1;
			if(str>=32)
				sum += 2;
			if(str>=34)
				sum += 1;
			if(str>=36)
				sum += 2;
			if(str>=38)
				sum += 1;
			if(str>=40)
				sum += 2;
			if(str>=42)
				sum += 1;
			if(str>=44)
				sum += 2;
			if(str>=46)
				sum += 1;
			if(str>=48)
				sum += 2;
			if(str>=50)
				sum += 1;
			if(str>=52)
				sum += 2;
			if(str>=54)
				sum += 1;
			if(str>=55)
				sum += 2;
			if(str>=57)
				sum += 1;
			if(str>=58)
				sum += 2;
			if(str>=60)
				sum += 1;
			if(str>=62)
				sum += 2;
			if(str>=64)
				sum += 1;
			if(str>=66)
				sum += 2;
			if(str>=68)
				sum += 1;
			if(str>=70)
				sum += 2;
			if(str>=72)
				sum += 1;
			if(str>=74)
				sum += 2;
			if(str>=76)
				sum += 1;
			if(str>=78)
				sum += 2;
			if(str>=80)
				sum += 1;
			if(str>=82)
				sum += 2;
			if(str>=84)
				sum += 1;
			if(str>=86)
				sum += 2;
			if(str>=88)
				sum += 1;
			if(str>=90)
				sum += 2;
			if(str>=92)
				sum += 1;
			if(str>=93)
				sum += 2;
			if(str>=95)
				sum += 1;
			if(str>=96)
				sum += 2;
			if(str>=97)
				sum += 1;
			if(str>=98)
				sum += 2;
			if(str>=99)
				sum += 1;
			if(str>=100)
				sum += 2;
			if(str>=102)
				sum += 1;
			if(str>=105)
				sum += 2;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			str -= 10;
			if(str>=2)
				sum += 1;
			if(str>=4)
				sum += 1;
			if(str>=6)
				sum += 1;
			if(str>=8)
				sum += 1;
			if(str>=10)
				sum += 1;
			if(str>=12)
				sum += 1;
			if(str>=14)
				sum += 1;
			if(str>=16)
				sum += 1;
			if(str>=18)
				sum += 1;
			if(str>=20)
				sum += 2;
			if(str>=22)
				sum += 1;
			if(str>=24)
				sum += 2;
			if(str>=26)
				sum += 1;				
			if(str>=28)
				sum += 2;
			if(str>=30)
				sum += 1;
			if(str>=32)
				sum += 2;
			if(str>=34)
				sum += 1;
			if(str>=36)
				sum += 2;
			if(str>=38)
				sum += 1;
			if(str>=40)
				sum += 2;
			if(str>=42)
				sum += 1;
			if(str>=44)
				sum += 2;
			if(str>=46)
				sum += 1;
			if(str>=48)
				sum += 2;
			if(str>=50)
				sum += 1;
			if(str>=52)
				sum += 2;
			if(str>=54)
				sum += 1;
			if(str>=55)
				sum += 2;
			if(str>=57)
				sum += 1;
			if(str>=58)
				sum += 2;
			if(str>=60)
				sum += 1;
			if(str>=62)
				sum += 2;
			if(str>=64)
				sum += 1;
			if(str>=66)
				sum += 2;
			if(str>=68)
				sum += 1;
			if(str>=70)
				sum += 2;
			if(str>=72)
				sum += 1;
			if(str>=74)
				sum += 2;
			if(str>=76)
				sum += 1;
			if(str>=78)
				sum += 2;
			if(str>=80)
				sum += 1;
			if(str>=82)
				sum += 2;
			if(str>=84)
				sum += 1;
			if(str>=86)
				sum += 2;
			if(str>=88)
				sum += 1;
			if(str>=90)
				sum += 2;
			if(str>=92)
				sum += 1;
			if(str>=93)
				sum += 2;
			if(str>=95)
				sum += 1;
			if(str>=96)
				sum += 2;
			if(str>=97)
				sum += 1;
			if(str>=98)
				sum += 2;
			if(str>=99)
				sum += 1;
			if(str>=100)
				sum += 2;
			if(str>=102)
				sum += 1;
			if(str>=105)
				sum += 2;
			break;
		default:
			break;
	}
	return sum;
}

	/**
	 * 클레스별 활추가데미지 +@ 리턴
	 */
	static private double toOriginalStatBowDamage(Character cha){
		double sum = 0;
		int dex = cha.getDex() + cha.getLvDex();
		switch(cha.getClassType()){
		case Lineage.LINEAGE_CLASS_ROYAL:
			dex -= 10;
			if(dex>=3)
				sum += 1;
			if(dex>=6)
				sum += 1;
			if(dex>=9)
				sum += 1;
			if(dex>=12)
				sum += 1;
			if(dex>=15)
				sum += 1;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
		case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
		case Lineage.LINEAGE_CLASS_BLACKWIZARD:
			dex -= 12;
			if(dex>=2)
				sum += 1;
			if(dex>=4)
				sum += 2;
			if(dex>=6)
				sum += 1;
			if(dex>=8)
				sum += 2;
			if(dex>=10)
				sum += 1;
			if(dex>=12)
				sum += 2;
			if(dex>=14)
				sum += 1;
			if(dex>=16)
				sum += 2;
			if(dex>=18)
				sum += 1;
			if(dex>=20)
				sum += 2;
			if(dex>=22)
				sum += 1;
			if(dex>=24)
				sum += 2;
			if(dex>=26)
				sum += 1;
			if(dex>=28)
				sum += 2;
			if(dex>=30)
				sum += 1;
			if(dex>=32)
				sum += 2;
			if(dex>=34)
				sum += 1;
			if(dex>=36)
				sum += 2;
			if(dex>=38)
				sum += 1;
			if(dex>=40)
				sum += 2;
			if(dex>=42)
				sum += 1;
			if(dex>=44)
				sum += 2;
			if(dex>=46)
				sum += 1;
			if(dex>=48)
				sum += 2;
			if(dex>=50)
				sum += 1;
			if(dex>=52)
				sum += 2;
			if(dex>=54)
				sum += 1;
			if(dex>=56)
				sum += 2;
			if(dex>=58)
				sum += 1;
			if(dex>=60)
				sum += 2;
			if(dex>=62)
				sum += 1;
			if(dex>=64)
				sum += 2;
			if(dex>=66)
				sum += 1;
			if(dex>=68)
				sum += 2;
			if(dex>=70)
				sum += 1;
			if(dex>=72)
				sum += 2;
			if(dex>=74)
				sum += 1;
			if(dex>=76)
				sum += 2;
			if(dex>=78)
				sum += 1;
			if(dex>=80)
				sum += 2;
			if(dex>=82)
				sum += 1;
			if(dex>=84)
				sum += 2;
			if(dex>=86)
				sum += 1;
			if(dex>=88)
				sum += 2;
			if(dex>=90)
				sum += 1;
			if(dex>=92)
				sum += 2;
			if(dex>=94)
				sum += 1;
			if(dex>=96)
				sum += 2;
			if(dex>=98)
				sum += 1;
			if(dex>=100)
				sum += 2;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			dex -= 12;
			if(dex>=2)
				sum += 1;
			if(dex>=4)
				sum += 2;
			if(dex>=6)
				sum += 1;
			if(dex>=8)
				sum += 2;
			if(dex>=10)
				sum += 1;
			if(dex>=12)
				sum += 2;
			if(dex>=14)
				sum += 1;
			if(dex>=16)
				sum += 2;
			if(dex>=18)
				sum += 1;
			if(dex>=20)
				sum += 2;
			if(dex>=22)
				sum += 1;
			if(dex>=24)
				sum += 2;
			if(dex>=26)
				sum += 1;
			if(dex>=28)
				sum += 2;
			if(dex>=30)
				sum += 1;
			if(dex>=32)
				sum += 2;
			if(dex>=34)
				sum += 1;
			if(dex>=36)
				sum += 2;
			if(dex>=38)
				sum += 1;
			if(dex>=40)
				sum += 2;
			if(dex>=42)
				sum += 1;
			if(dex>=44)
				sum += 2;
			if(dex>=46)
				sum += 1;
			if(dex>=48)
				sum += 2;
			if(dex>=50)
				sum += 1;
			if(dex>=52)
				sum += 2;
			if(dex>=54)
				sum += 1;
			if(dex>=56)
				sum += 2;
			if(dex>=58)
				sum += 1;
			if(dex>=60)
				sum += 2;
			if(dex>=62)
				sum += 1;
			if(dex>=64)
				sum += 2;
			if(dex>=66)
				sum += 1;
			if(dex>=68)
				sum += 2;
			if(dex>=70)
				sum += 1;
			if(dex>=72)
				sum += 2;
			if(dex>=74)
				sum += 1;
			if(dex>=76)
				sum += 2;
			if(dex>=78)
				sum += 1;
			if(dex>=80)
				sum += 2;
			if(dex>=82)
				sum += 1;
			if(dex>=84)
				sum += 2;
			if(dex>=86)
				sum += 1;
			if(dex>=88)
				sum += 2;
			if(dex>=90)
				sum += 1;
			if(dex>=92)
				sum += 2;
			if(dex>=94)
				sum += 1;
			if(dex>=96)
				sum += 2;
			if(dex>=98)
				sum += 1;
			if(dex>=100)
				sum += 2;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			dex -= 10;
			if(dex>=2)
				sum += 1;
			if(dex>=4)
				sum += 2;
			if(dex>=6)
				sum += 1;
			if(dex>=8)
				sum += 2;
			if(dex>=10)
				sum += 1;
			if(dex>=12)
				sum += 2;
			if(dex>=14)
				sum += 1;
			if(dex>=16)
				sum += 2;
			if(dex>=18)
				sum += 1;
			if(dex>=20)
				sum += 2;
			if(dex>=22)
				sum += 1;
			if(dex>=24)
				sum += 2;
			if(dex>=26)
				sum += 1;
			if(dex>=28)
				sum += 2;
			if(dex>=30)
				sum += 1;
			if(dex>=32)
				sum += 2;
			if(dex>=34)
				sum += 1;
			if(dex>=36)
				sum += 2;
			if(dex>=38)
				sum += 1;
			if(dex>=40)
				sum += 2;
			if(dex>=42)
				sum += 1;
			if(dex>=44)
				sum += 2;
			if(dex>=46)
				sum += 1;
			if(dex>=48)
				sum += 2;
			if(dex>=50)
				sum += 1;
			if(dex>=52)
				sum += 2;
			if(dex>=54)
				sum += 1;
			if(dex>=56)
				sum += 2;
			if(dex>=58)
				sum += 1;
			if(dex>=60)
				sum += 2;
			if(dex>=62)
				sum += 1;
			if(dex>=64)
				sum += 2;
			if(dex>=66)
				sum += 1;
			if(dex>=68)
				sum += 2;
			if(dex>=70)
				sum += 1;
			if(dex>=72)
				sum += 2;
			if(dex>=74)
				sum += 1;
			if(dex>=76)
				sum += 2;
			if(dex>=78)
				sum += 1;
			if(dex>=80)
				sum += 2;
			if(dex>=82)
				sum += 1;
			if(dex>=84)
				sum += 2;
			if(dex>=86)
				sum += 1;
			if(dex>=88)
				sum += 2;
			if(dex>=90)
				sum += 1;
			if(dex>=92)
				sum += 2;
			if(dex>=94)
				sum += 1;
			if(dex>=96)
				sum += 2;
			if(dex>=98)
				sum += 1;
			if(dex>=100)
				sum += 2;
			break;
	}
	return sum;
}

	/**
	 * 클레스별 공격 성공율 +@ 리턴
	 */
	static private int toOriginalStatHit(Character cha){
		int sum = 0;
		int str = cha.getStr() + cha.getLvStr();
		switch(cha.getClassType()){
		case Lineage.LINEAGE_CLASS_ROYAL:
			str -= 13;
			if(str>=3)
				sum += 1;
			if(str>=6)
				sum += 1;
			if(str>=9)
				sum += 1;
			if(str>=12)
				sum += 1;
			break;
		case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
		case Lineage.LINEAGE_CLASS_KNIGHT:
			str -= 16;
			if(str>=1)
				sum += 2;
			if(str>=3)
				sum += 2;
			if(str>=5)
				sum += 2;
			if(str>=7)
				sum += 2;
			if(str>=9)
				sum += 2;
			if(str>=11)
				sum += 2;
			if(str>=13)
				sum += 2;
			if(str>=15)
				sum += 2;
			if(str>=18)
				sum += 2;
			if(str>=21)
				sum += 2;
			if(str>=23)
				sum += 2;
			if(str>=24)
				sum += 2;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			str -= 11;
			if(str>=2)
				sum += 1;
			if(str>=4)
				sum += 1;
			if(str>=6)
				sum += 1;
			if(str>=8)
				sum += 1;
			if(str>=10)
				sum += 1;
			if(str>=12)
				sum += 1;
			if(str>=14)
				sum += 1;
			if(str>=16)
				sum += 1;
			if(str>=18)
				sum += 1;
			if(str>=21)
				sum += 1;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
		case Lineage.LINEAGE_CLASS_BLACKWIZARD:
			str -= 8;
			if(str>=3)
				sum += 1;
			if(str>=5)
				sum += 1;
			if(str>=7)
				sum += 1;
			if(str>=9)
				sum += 1;
			if(str>=11)
				sum += 1;
			if(str>=13)
				sum += 1;
			if(str>=15)
				sum += 1;
			if(str>=18)
				sum += 1;
			if(str>=22)
				sum += 1;
			break;
	}
	return sum;
}

	/**
	 * 힘에 따른 공격성공율 +@ 리턴
	 */
	static private int toHitStr(Character cha){
		int[] strHit = { -2, -2, -2, -2, -2, -2, -2,
				-2, -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6,
				7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12,
				13, 13, 13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 17, 17};

		try {
			if(cha.getTotalStr() > 59)
				return strHit[58];
			else
				return strHit[cha.getTotalStr()-1];
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 덱스에 따른 활 전용 공격성공율 +@ 리턴
	 */
	static private int toHitDex(Character cha){
		int[] dexHit = { -2, -2, -2, -2, -2, -2, -1, -1, 0, 0,
				1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19, 19, 19, 20, 20, 20, 21, 21, 21, 22, 22, 22, 23,
				23, 23, 24, 24, 24, 25, 25, 25, 26, 26, 26, 27, 27, 27, 28 };

		try {
			if(cha.getTotalDex() > 100)
				return dexHit[99];
			else
				return dexHit[cha.getTotalDex()-1];
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 레벨에 따른 공격성공율 +@ 리턴
	 */
	static private int toHitLv(Character cha){
		return cha.getLevel();
	}

	/**
	 * 클레스별 활명중률 +@ 리턴
	 */
	static private int toOriginalStatBowHit(Character cha){
		int sum = 0;
		int dex = cha.getDex() + cha.getLvDex();
		switch(cha.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				break;
			case Lineage.LINEAGE_CLASS_ELF:
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				dex -= 12;
				if(dex>=1)
					sum += 2;
				if(dex>=4)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				break;
		}
		return sum;
	}

	/**
	 * 장거리 공격 회피율 리턴
	 */
	static private int getEr(Character cha){
		// 8부터 +1된값만큼 1씩 증가함.
		// 레벨 10부터 1씩 증가함.
		int er = 0;
		if(cha.getTotalDex()<8)
			er = -1;
		else
			er = (cha.getTotalDex()-8) / 2;
		er += cha.getLevel() / 5;
		return er;
	}

	/**
	 * 클레스별 ER +@ 리턴
	 */
	static private int toOriginalStatER(Character cha){
		int sum = 0;
		int dex = cha.getTotalDex();
		switch(cha.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				dex -= 10;
				if(dex>=2)
					sum += 1;
				if(dex>=4)
					sum += 2;
				if(dex>=6)
					sum += 1;
				if(dex>=8)
					sum += 1;
				if(dex>=10)
					sum += 1;
				if(dex>=12)
					sum += 1;
				if(dex>=14)
					sum += 1;
				if(dex>=16)
					sum += 1;
				if(dex>=18)
					sum += 1;
				if(dex>=20)
					sum += 1;
				if(dex>=22)
					sum += 1;
				if(dex>=24)
					sum += 1;
				if(dex>=26)
					sum += 1;
				if(dex>=28)
					sum += 1;
				if(dex>=30)
					sum += 1;
				if(dex>=32)
					sum += 2;
				if(dex>=34)
					sum += 1;
				if(dex>=36)
					sum += 1;
				if(dex>=38)
					sum += 1;
				if(dex>=40)
					sum += 1;
				if(dex>=42)
					sum += 1;
				if(dex>=44)
					sum += 1;
				if(dex>=46)
					sum += 1;
				if(dex>=48)
					sum += 1;
				if(dex>=50)
					sum += 1;
				if(dex>=52)
					sum += 2;
				if(dex>=54)
					sum += 1;
				if(dex>=56)
					sum += 1;
				if(dex>=58)
					sum += 1;
				if(dex>=60)
					sum += 1;
				if(dex>=62)
					sum += 1;
				if(dex>=64)
					sum += 1;
				if(dex>=66)
					sum += 1;
				if(dex>=68)
					sum += 1;
				if(dex>=70)
					sum += 1;
				if(dex>=72)
					sum += 2;
				if(dex>=74)
					sum += 1;
				if(dex>=76)
					sum += 1;
				if(dex>=78)
					sum += 1;
				if(dex>=80)
					sum += 1;
				if(dex>=82)
					sum += 1;
				if(dex>=84)
					sum += 1;
				if(dex>=86)
					sum += 1;
				if(dex>=88)
					sum += 1;
				if(dex>=90)
					sum += 1;
				if(dex>=92)
					sum += 1;
				if(dex>=94)
					sum += 1;
				if(dex>=96)
					sum += 1;
				if(dex>=98)
					sum += 1;
				if(dex>=100)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
				dex -= 12;
				if(dex>=2)
					sum += 1;
				if(dex>=4)
					sum += 2;
				if(dex>=6)
					sum += 1;
				if(dex>=8)
					sum += 1;
				if(dex>=10)
					sum += 1;
				if(dex>=12)
					sum += 1;
				if(dex>=14)
					sum += 1;
				if(dex>=16)
					sum += 1;
				if(dex>=18)
					sum += 1;
				if(dex>=20)
					sum += 1;
				if(dex>=22)
					sum += 1;
				if(dex>=24)
					sum += 1;
				if(dex>=26)
					sum += 1;
				if(dex>=28)
					sum += 1;
				if(dex>=30)
					sum += 1;
				if(dex>=32)
					sum += 2;
				if(dex>=34)
					sum += 1;
				if(dex>=36)
					sum += 1;
				if(dex>=38)
					sum += 1;
				if(dex>=40)
					sum += 1;
				if(dex>=42)
					sum += 1;
				if(dex>=44)
					sum += 1;
				if(dex>=46)
					sum += 1;
				if(dex>=48)
					sum += 1;
				if(dex>=50)
					sum += 1;
				if(dex>=52)
					sum += 2;
				if(dex>=54)
					sum += 1;
				if(dex>=56)
					sum += 1;
				if(dex>=58)
					sum += 1;
				if(dex>=60)
					sum += 1;
				if(dex>=62)
					sum += 1;
				if(dex>=64)
					sum += 1;
				if(dex>=66)
					sum += 1;
				if(dex>=68)
					sum += 1;
				if(dex>=70)
					sum += 1;
				if(dex>=72)
					sum += 2;
				if(dex>=74)
					sum += 1;
				if(dex>=76)
					sum += 1;
				if(dex>=78)
					sum += 1;
				if(dex>=80)
					sum += 1;
				if(dex>=82)
					sum += 1;
				if(dex>=84)
					sum += 1;
				if(dex>=86)
					sum += 1;
				if(dex>=88)
					sum += 1;
				if(dex>=90)
					sum += 1;
				if(dex>=92)
					sum += 1;
				if(dex>=94)
					sum += 1;
				if(dex>=96)
					sum += 1;
				if(dex>=98)
					sum += 1;
				if(dex>=100)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				dex -= 12;
				if(dex>=2)
					sum += 1;
				if(dex>=4)
					sum += 2;
				if(dex>=6)
					sum += 1;
				if(dex>=8)
					sum += 1;
				if(dex>=10)
					sum += 1;
				if(dex>=12)
					sum += 1;
				if(dex>=14)
					sum += 1;
				if(dex>=16)
					sum += 1;
				if(dex>=18)
					sum += 1;
				if(dex>=20)
					sum += 1;
				if(dex>=22)
					sum += 1;
				if(dex>=24)
					sum += 1;
				if(dex>=26)
					sum += 1;
				if(dex>=28)
					sum += 1;
				if(dex>=30)
					sum += 1;
				if(dex>=32)
					sum += 2;
				if(dex>=34)
					sum += 1;
				if(dex>=36)
					sum += 1;
				if(dex>=38)
					sum += 1;
				if(dex>=40)
					sum += 1;
				if(dex>=42)
					sum += 1;
				if(dex>=44)
					sum += 1;
				if(dex>=46)
					sum += 1;
				if(dex>=48)
					sum += 1;
				if(dex>=50)
					sum += 1;
				if(dex>=52)
					sum += 2;
				if(dex>=54)
					sum += 1;
				if(dex>=56)
					sum += 1;
				if(dex>=58)
					sum += 1;
				if(dex>=60)
					sum += 1;
				if(dex>=62)
					sum += 1;
				if(dex>=64)
					sum += 1;
				if(dex>=66)
					sum += 1;
				if(dex>=68)
					sum += 1;
				if(dex>=70)
					sum += 1;
				if(dex>=72)
					sum += 2;
				if(dex>=74)
					sum += 1;
				if(dex>=76)
					sum += 1;
				if(dex>=78)
					sum += 1;
				if(dex>=80)
					sum += 1;
				if(dex>=82)
					sum += 1;
				if(dex>=84)
					sum += 1;
				if(dex>=86)
					sum += 1;
				if(dex>=88)
					sum += 1;
				if(dex>=90)
					sum += 1;
				if(dex>=92)
					sum += 1;
				if(dex>=94)
					sum += 1;
				if(dex>=96)
					sum += 1;
				if(dex>=98)
					sum += 1;
				if(dex>=100)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				dex -= 7;
				if(dex>=2)
					sum += 1;
				if(dex>=4)
					sum += 2;
				if(dex>=6)
					sum += 1;
				if(dex>=8)
					sum += 1;
				if(dex>=10)
					sum += 1;
				if(dex>=12)
					sum += 1;
				if(dex>=14)
					sum += 1;
				if(dex>=16)
					sum += 1;
				if(dex>=18)
					sum += 1;
				if(dex>=20)
					sum += 1;
				if(dex>=22)
					sum += 1;
				if(dex>=24)
					sum += 1;
				if(dex>=26)
					sum += 1;
				if(dex>=28)
					sum += 1;
				if(dex>=30)
					sum += 1;
				if(dex>=32)
					sum += 2;
				if(dex>=34)
					sum += 1;
				if(dex>=36)
					sum += 1;
				if(dex>=38)
					sum += 1;
				if(dex>=40)
					sum += 1;
				if(dex>=42)
					sum += 1;
				if(dex>=44)
					sum += 1;
				if(dex>=46)
					sum += 1;
				if(dex>=48)
					sum += 1;
				if(dex>=50)
					sum += 1;
				if(dex>=52)
					sum += 2;
				if(dex>=54)
					sum += 1;
				if(dex>=56)
					sum += 1;
				if(dex>=58)
					sum += 1;
				if(dex>=60)
					sum += 1;
				if(dex>=62)
					sum += 1;
				if(dex>=64)
					sum += 1;
				if(dex>=66)
					sum += 1;
				if(dex>=68)
					sum += 1;
				if(dex>=70)
					sum += 1;
				if(dex>=72)
					sum += 2;
				if(dex>=74)
					sum += 1;
				if(dex>=76)
					sum += 1;
				if(dex>=78)
					sum += 1;
				if(dex>=80)
					sum += 1;
				if(dex>=82)
					sum += 1;
				if(dex>=84)
					sum += 1;
				if(dex>=86)
					sum += 1;
				if(dex>=88)
					sum += 1;
				if(dex>=90)
					sum += 1;
				if(dex>=92)
					sum += 1;
				if(dex>=94)
					sum += 1;
				if(dex>=96)
					sum += 1;
				if(dex>=98)
					sum += 1;
				if(dex>=100)
					sum += 1;
				break;
		}
		return sum;
	}

	/**
	 * 요정 클레스 요숲경비병에게 도움처리 함수.
	 *  : 요정은 요숲에서 사냥시 근처 요숲경비가잇을경우 도움을 줌.
	 */
	static private void toElven(PcInstance pc, object o){
		if(pc.getClassType()==Lineage.LINEAGE_CLASS_ELF && o instanceof MonsterInstance && !(o instanceof SummonInstance)){
			for(object inside : pc.getInsideList()){
				if(inside instanceof ElvenGuard)
					inside.toDamage((Character)o, 0, Lineage.ATTACK_TYPE_DIRECT);
			}
		}
	}

	/**
	 * 근처 경비병에게 도움요청처리하는 함수.
	 *  : 다른 놈에게 pk를 당하거나 할때 처리하는 함수.
	 * @param pc	: 요청자
	 * @param cha	: 공격자
	 */
	static public void toGuardHelper(PcInstance pc, Character cha){
		// 요청자가 카오라면 무시.
		if(pc.getLawful() < Lineage.NEUTRAL)
			return;
		// 요청자가 보라돌이 상태라면 무시. 단! 가격자가 카오가 아닐때만. 
		if(pc.isBuffCriminal() && cha.getLawful()>=Lineage.NEUTRAL)
			return;
		// 사용자가 가격햇고 노말존에 잇엇을경우.
		if(World.isNormalZone(pc.getX(), pc.getY(), pc.getMap())){
			for(object inside : pc.getInsideList()){
				if(inside instanceof GuardInstance)
					inside.toDamage(cha, 0, Lineage.ATTACK_TYPE_DIRECT);
			}
		}
	}

	/**
	 * 해당 몬스터가 언데드 인지 확인해주는 함수.
	 * @param mon
	 * @return
	 */
	static private boolean isUndeadDamage(Character cha) {
		if (cha instanceof MonsterInstance) {
			MonsterInstance mon = (MonsterInstance) cha;
			return mon.getMonster().getResistanceUndead() < 0 && ServerDatabase.isNight();
		}
		return false;
	}
}
