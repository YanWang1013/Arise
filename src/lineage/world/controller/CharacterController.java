package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Exp;
import lineage.database.ExpDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.item.cloak.ElvenCloak;
import lineage.world.object.magic.Meditation;

public final class CharacterController {

	static private List<object> list;
	static private List<ItemInstance> list_itemdrop;
	
	static public void init(){
		TimeLine.start("CharacterController..");
		
		list_itemdrop = new ArrayList<ItemInstance>();
		list = new ArrayList<object>();
		
		TimeLine.end();
	}
	
	static public void toWorldJoin(object o){
		if(!list.contains(o))
			list.add(o);
		
//		lineage.share.System.println("CharacterController toWorldJoin : "+list.size()+" -> "+cha.toString());
	}
	
	static public void toWorldOut(object o){
		list.remove(o);
		
//		lineage.share.System.println("CharacterController toWorldOut : "+list.size()+" -> "+cha.toString());
	}
	
	/**
	 * 객체가 이동할대마다 호출됨.
	 * @param cha
	 */
	static public void toMoving(Character cha){
		// 관련 버프 제거.
		if(cha.isBuffMeditation())
			BuffController.remove(cha, Meditation.class);
	}
	
	/**
	 * 타이머에서 주기적으로 호출.
	 * @param time	: 진행중인 현재 시간.
	 */
	static public void toTimer(long time){
		try {
			// 자연회복 처리.
			ticHpMp();
		} catch (Exception e) {
			lineage.share.System.println("자연회복 처리.");
			lineage.share.System.println(e);
		}
		try {
			// 주기적으로 호출에 사용.
			for(object o : list)
				o.toTimer(time);
		} catch (Exception e) {
			lineage.share.System.println("주기적으로 호출에 사용.");
			lineage.share.System.println(e);
		}
	}
	
	/**
	 * 자연회복 처리 함수.
	 */
	static private void ticHpMp(){
		for(object o : list){
			if(!o.isDead()){
				if(o instanceof Character){
					Character cha = (Character)o;
					ItemInstance item = null;
					int tic_hp = cha.isHpTic() ? cha.hpTic() : 0;
					int tic_mp = cha.isMpTic() ? cha.mpTic() : 0;
					
					// 사용자일때 확인하기.
					if(cha instanceof PcInstance){
						// 인벤토리 무게오바일때
						if(cha.getInventory()!=null && cha.getInventory().getWeightPercent()>=15){
							// 여관맵이라면 피 차게해야됨.
							// 여관맵이 아닐때.
							if(!InnController.isInnMap(cha)){
								tic_hp = tic_mp = 0;
								// 요정족 망토를 착용중이라면 피차게 해야됨.
								item = cha.getInventory().getSlot(Lineage.SLOT_CLOAK);
								if( item!=null && item instanceof ElvenCloak )
									tic_hp = 1;
							}
						}
					}
					
					// 버서커상태 무시.
					if(o.isBuffBerserks())
						tic_hp = tic_mp = 0;
					
					// 틱 처리.
					if(tic_hp>0 && cha.getTotalHp()!=cha.getNowHp())
						cha.setNowHp(cha.getNowHp() + tic_hp);
					if(tic_mp>0 && cha.getTotalMp()!=cha.getNowMp())
						cha.setNowMp(cha.getNowMp() + tic_mp);
				}
			}
		}
	}
	
	/**
	 * 객체에 경험치 하향 처리 함수.
	 * @param cha
	 */
	static public void toExpDown(Character cha, double exp){
			// hp & mp 하향.	
		cha.setLevel(cha.getLevel()-1);
		cha.setExp(cha.getExp() - exp);
			int hp = cha.getMaxHp()-toStatusUP(cha, true);
			int mp = cha.getMaxMp()-toStatusUP(cha, false);
			// 클레스별 최소값 체크.
			switch(cha.getClassType()){
				case Lineage.LINEAGE_CLASS_ROYAL:
					if(hp < Lineage.royal_hp)
						hp = Lineage.royal_hp;
					if(mp < Lineage.royal_mp)
						mp = Lineage.royal_mp;
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					if(hp < Lineage.knight_hp)
						hp = Lineage.knight_hp;
					if(mp < Lineage.knight_mp)
						mp = Lineage.knight_mp;
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					if(hp < Lineage.elf_hp)
						hp = Lineage.elf_hp;
					if(mp < Lineage.elf_mp)
						mp = Lineage.elf_mp;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					if(hp < Lineage.wizard_hp)
						hp = Lineage.wizard_hp;
					if(mp < Lineage.wizard_mp)
						mp = Lineage.wizard_mp;
					break;
				case Lineage.LINEAGE_CLASS_DARKELF:
					if(hp < Lineage.darkelf_hp)
						hp = Lineage.darkelf_hp;
					if(mp < Lineage.darkelf_mp)
						mp = Lineage.darkelf_mp;
					break;
				case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
					if(hp < Lineage.dragonknight_hp)
						hp = Lineage.dragonknight_hp;
					if(mp < Lineage.dragonknight_mp)
						mp = Lineage.dragonknight_mp;
					break;
				default:
					if(hp < Lineage.blackwizard_hp)
						hp = Lineage.blackwizard_hp;
					if(mp < Lineage.blackwizard_mp)
						mp = Lineage.blackwizard_mp;
					break;
			}
			cha.setMaxHp(hp);
			cha.setMaxMp(mp);
		// 경험치 하향.
		
		if(cha instanceof PcInstance){
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}
	
	
	
	/**
	 * 객체에 경험치 하향 처리 함수.
	 * @param cha
	 */
	static public void toExpDown(Character cha){
		// 공격자에게 경험치 주기. toAttackObject
		Exp e = ExpDatabase.find(cha.getLevel());
		double exp = 0;
		
		// 경험치 감소할 값 추출 부분
		if(cha.getLevel()<45) {
			exp = e.getExp() * 0.1;
		}else if(cha.getLevel()<46) {
			exp = e.getExp() * 0.09;
		}else if(cha.getLevel()<47) {
			exp = e.getExp() * 0.08;
		}else if(cha.getLevel()<48) {
			exp = e.getExp() * 0.07;
		}else if(cha.getLevel()<49) {
			exp = e.getExp() * 0.06;
		}else{
			exp = e.getExp() * 0.05;
		}
		// 레벨다운됫는지 확인부분.
		if(e.getBonus()-e.getExp() > cha.getExp()-exp){
			// 레벨 하향
			cha.setLevel(cha.getLevel()-1);
			// hp & mp 하향.
			int hp = cha.getMaxHp()-toStatusUP(cha, true);
			int mp = cha.getMaxMp()-toStatusUP(cha, false);
			// 클레스별 최소값 체크.
			switch(cha.getClassType()){
				case Lineage.LINEAGE_CLASS_ROYAL:
					if(hp < Lineage.royal_hp)
						hp = Lineage.royal_hp;
					if(mp < Lineage.royal_mp)
						mp = Lineage.royal_mp;
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					if(hp < Lineage.knight_hp)
						hp = Lineage.knight_hp;
					if(mp < Lineage.knight_mp)
						mp = Lineage.knight_mp;
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					if(hp < Lineage.elf_hp)
						hp = Lineage.elf_hp;
					if(mp < Lineage.elf_mp)
						mp = Lineage.elf_mp;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					if(hp < Lineage.wizard_hp)
						hp = Lineage.wizard_hp;
					if(mp < Lineage.wizard_mp)
						mp = Lineage.wizard_mp;
					break;
				case Lineage.LINEAGE_CLASS_DARKELF:
					if(hp < Lineage.darkelf_hp)
						hp = Lineage.darkelf_hp;
					if(mp < Lineage.darkelf_mp)
						mp = Lineage.darkelf_mp;
					break;
				case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
					if(hp < Lineage.dragonknight_hp)
						hp = Lineage.dragonknight_hp;
					if(mp < Lineage.dragonknight_mp)
						mp = Lineage.dragonknight_mp;
					break;
				default:
					if(hp < Lineage.blackwizard_hp)
						hp = Lineage.blackwizard_hp;
					if(mp < Lineage.blackwizard_mp)
						mp = Lineage.blackwizard_mp;
					break;
			}
			cha.setMaxHp(hp);
			cha.setMaxMp(mp);
		}
		// 경험치 하향.
		cha.setExp(cha.getExp()-exp);
		if(cha instanceof PcInstance){
			PcInstance pc = (PcInstance)cha;
			// 경험치 기록.
			pc.setLostExp(exp);
			// 패킷 처리.
			cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}
	
	/**
	 * 해당 객체에 아이템을 드랍처리할때 사용하는 함수.
	 *  : 현재는 사용자가 호출해서 사용함.
	 *  : 추후에는 팻도 이구간을 이용할 수 있도록 작업해두면 좋을듯.
	 * @param cha
	 */
	static public void toItemDrop(Character cha){
		int dropCount = 0;
		int dropChance = 0;
		list_itemdrop.clear();

		if(cha.getLawful()>=Lineage.NEUTRAL && cha.getLawful()<Lineage.LAWFUL-2767){
			dropCount = Util.random(0, 1);
			dropChance = 10;
		}else if(cha.getLawful()<Lineage.NEUTRAL){
			dropCount = Util.random(0, 3);
			dropChance = 50;
		}else{
			dropCount = 0;
			dropChance = 0;
		}
		
		for(ItemInstance item : cha.getInventory().getList()){
			if(	item.getItem().isDrop() && item.getItem().getNameIdNumber()!=4 && dropChance>Util.random(1, 100) ){
				list_itemdrop.add(item);
				if(--dropCount<=0)
					break;
			}
		}
		for(ItemInstance item : list_itemdrop){
			if(item.isEquipped())
				item.toClick(cha, null);
			cha.getInventory().toDrop(item, item.getCount(), cha.getX(), cha.getY(), false);
		}
	}
	
	/**
	 * 레벨업시 호출하며, hp&mp상승값 리턴함.
	 * @param HpMp	: hp-true mp-false
	 * @return		: 상승값.
	 */
	static public int toStatusUP(Character cha, boolean HpMp){
//		if(Common.OS_NAME.startsWith("Windows")){
//			String object = cha instanceof PcInstance ? "PcInstance" : "";
//			int con = cha.getCon() + cha.getLvCon();
//			int wis = cha.getWis() + cha.getLvWis();
//			int class_type = cha.getClassType();
//			int gab = LuaScriptController.toInteger(LuaScriptController.getCharacter(), String.format("toStatusUP('%s', '%s', %d, %d, %d)", object, String.valueOf(HpMp), con, wis, class_type));
//			return gab;
//		}else{
			int con = cha.getCon() + cha.getLvCon();
			int wis = cha.getWis() + cha.getLvWis();
			int start_hp = 0;
			int start_mp = 0;
			int temp = 0;
			int HPMP = 0;

			// 시즌3 hp/mp 증가량 (콘18 기준)
			// 군주 17.5 / 2.5
			// 기사 23.3 / 0.9
			// 요정 15.5 / 4.5
			// 마법사 12.4 / 5.9
			// 다크엘프 15.4 / 3.5
			// 용기사 19.4 / 1.7
			// 환술사 14.6 / 4.8
			if(HpMp){	// hp
				if(cha instanceof PcInstance){
					switch(cha.getClassType()){
						case Lineage.LINEAGE_CLASS_ROYAL:	// 군주
						case Lineage.LINEAGE_CLASS_ELF:		// 요정
						case Lineage.LINEAGE_CLASS_DARKELF:
							temp = Util.random(1, 32);

							if(con <= 15) start_hp = 5;
							else start_hp = con - 10;

							if(temp <= 6) {
								start_hp += 1;
							} else if(temp <= 16) {
								start_hp += 2;
							} else if(temp <= 26) {
								start_hp += 3;
							} else if(temp <= 31) {
								start_hp += 4;
							} else {
								start_hp += 5;
							}
							HPMP = start_hp;
							break;
						case Lineage.LINEAGE_CLASS_KNIGHT:	// 기사
						case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
							temp = Util.random(1, 64);
							if(con <= 15) {
								start_hp = 6;
							} else {
								start_hp = con - 9;
							}

							if(temp <= 7) {
								start_hp += 1;
							} else if(temp <= 22) {
								start_hp += 2;
							} else if(temp <= 42) {
								start_hp += 3;
							} else if(temp <= 57) {
								start_hp += 4;
							} else if(temp <= 63) {
								start_hp += 5;
							} else {
								start_hp += 6;
							}

							HPMP = start_hp;
							break;
						case Lineage.LINEAGE_CLASS_WIZARD:	// 법사
						case Lineage.LINEAGE_CLASS_BLACKWIZARD:
							temp = Util.random(1, 8);
							if(con <= 15) {
								start_hp = 3;
							} else {
								start_hp = con - 12;
							}

							if(temp <= 4) {
								start_hp += 1;
							} else if(temp <= 7) {
								start_hp += 2;
							} else {
								start_hp += 3;
							}

							HPMP = start_hp;
							break;
					}
				}else if(cha instanceof SummonInstance){
					temp = Util.random(1, 32);
					start_hp = 5;
					if(temp <= 6) {
						start_hp += 1;
					} else if(temp <= 16) {
						start_hp += 2;
					} else if(temp <= 26) {
						start_hp += 3;
					} else if(temp <= 31) {
						start_hp += 4;
					} else {
						start_hp += 5;
					}

					HPMP = start_hp;
				}
				
			}else{	// mp
				if(cha instanceof PcInstance){
					switch(cha.getClassType()){
						case Lineage.LINEAGE_CLASS_ROYAL:	// 군주
						case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
							if(wis <= 11) {
								start_mp = Util.random(1, 2);
							} else if(wis >= 12 && wis <= 14){
								temp = Util.random(1, 4);
								if(temp == 1) {
									start_mp = 2;
								} else if(temp <= 3) {
									start_mp = 3;
								} else {
									start_mp = 4;
								}
							}else if(wis >= 15 && wis <= 17){
								temp = Util.random(1, 4);
								if(temp == 1) {
									start_mp = 3;
								} else if(temp <= 3) {
									start_mp = 4;
								} else {
									start_mp = 5;
								}
							}else if(wis >= 18 && wis <= 20){
								temp = Util.random(1, 6);
								if(temp == 1) {
									start_mp = 3;
								} else if(temp <= 3) {
									start_mp = 4;
								} else if(temp <= 5) {
									start_mp = 5;
								} else {
									start_mp = 6;
								}
							}else if(wis >= 21 && wis <= 23){
								temp = Util.random(1, 10);
								if(temp == 1) {
									start_mp = 3;
								} else if(temp <= 3) {
									start_mp = 4;
								} else if(temp <= 7) {
									start_mp = 5;
								} else if(temp <= 9) {
									start_mp = 6;
								} else {
									start_mp = 7;
								}
							}else if(wis >= 24 && wis <= 26){
								temp = Util.random(1, 14);
								if(temp == 1) {
									start_mp = 3;
								} else if(temp <= 3) {
									start_mp = 4;
								} else if(temp <= 7) {
									start_mp = 5;
								} else if(temp <= 11) {
									start_mp = 6;
								} else if(temp <= 13) {
									start_mp = 7;
								} else {
									start_mp = 8;
								}
							}else{// if(wis >= 27 && wis <= 29){
								temp = Util.random(1, 22);
								if(temp == 1) {
									start_mp = 3;
								} else if(temp <= 3) {
									start_mp = 4;
								} else if(temp <= 7) {
									start_mp = 5;
								} else if(temp <= 15) {
									start_mp = 6;
								} else if(temp <= 19) {
									start_mp = 7;
								} else if(temp <= 21) {
									start_mp = 8;
								} else {
									start_mp = 9;
								}
							}

							HPMP = start_mp;
							break;
						case Lineage.LINEAGE_CLASS_KNIGHT:	// 기사
							if(wis <= 9){
								temp = Util.random(1, 4);
								if(temp == 1) {
									start_mp = 0;
								} else if(temp <= 3) {
									start_mp = 1;
								} else {
									start_mp = 2;
								}
							}else{
								temp = Util.random(1, 4);
								if(temp == 1) {
									start_mp = 1;
								} else if(temp <= 3) {
									start_mp = 2;
								} else {
									start_mp = 3;
								}
							}

							HPMP = start_mp;
							break;
						case Lineage.LINEAGE_CLASS_ELF:	// 요정
						case Lineage.LINEAGE_CLASS_DARKELF:
							if(wis <= 14){
								temp = Util.random(1, 6);
								if(temp == 1) {
									start_mp = 3;
								} else if(temp <= 3) {
									start_mp = 4;
								} else if(temp <= 5) {
									start_mp = 5;
								} else {
									start_mp = 6;
								}
							}else if(wis >= 15 && wis <= 17){
								temp = Util.random(1, 6);
								if(temp == 1) {
									start_mp = 4;
								} else if(temp <= 3) {
									start_mp = 5;
								} else if(temp <= 5) {
									start_mp = 6;
								} else {
									start_mp = 7;
								}
							}else if(wis >= 18 && wis <= 20){
								temp = Util.random(1, 14);
								if(temp == 1) {
									start_mp = 4;
								} else if(temp <= 3) {
									start_mp = 5;
								} else if(temp <= 7) {
									start_mp = 6;
								} else if(temp <= 11) {
									start_mp = 7;
								} else if(temp <= 13) {
									start_mp = 8;
								} else {
									start_mp = 9;
								}
							}else if(wis >= 21 && wis <= 23){
								temp = Util.random(1, 30);
								if(temp == 1) {
									start_mp = 4;
								} else if(temp <= 3) {
									start_mp = 5;
								} else if(temp <= 7) {
									start_mp = 6;
								} else if(temp <= 15) {
									start_mp = 7;
								} else if(temp <= 23) {
									start_mp = 8;
								} else if(temp <= 27) {
									start_mp = 9;
								} else if(temp <= 29) {
									start_mp = 10;
								} else {
									start_mp = 11;
								}
							}else if(wis >= 24 && wis <= 26){
								temp = Util.random(1, 62);
								if(temp == 1) {
									start_mp = 4;
								} else if(temp <= 3) {
									start_mp = 5;
								} else if(temp <= 7) {
									start_mp = 6;
								} else if(temp <= 15) {
									start_mp = 7;
								} else if(temp <= 31) {
									start_mp = 8;
								} else if(temp <= 47) {
									start_mp = 9;
								} else if(temp <= 55) {
									start_mp = 10;
								} else if(temp <= 59) {
									start_mp = 11;
								} else if(temp <= 61) {
									start_mp = 12;
								} else {
									start_mp = 13;
								}
							}else{	// else if(wis >= 27 && wis <= 29)
								temp = Util.random(1, 126);
								if(temp == 1) {
									start_mp = 4;
								} else if(temp <= 3) {
									start_mp = 5;
								} else if(temp <= 7) {
									start_mp = 6;
								} else if(temp <= 15) {
									start_mp = 7;
								} else if(temp <= 31) {
									start_mp = 8;
								} else if(temp <= 63) {
									start_mp = 9;
								} else if(temp <= 95) {
									start_mp = 10;
								} else if(temp <= 111) {
									start_mp = 11;
								} else if(temp <= 119) {
									start_mp = 12;
								} else if(temp <= 123) {
									start_mp = 13;
								} else if(temp <= 125) {
									start_mp = 14;
								} else {
									start_mp = 15;
								}
							}

							HPMP = start_mp;
							break;
						case Lineage.LINEAGE_CLASS_WIZARD:	// 법사
						case Lineage.LINEAGE_CLASS_BLACKWIZARD:
							if(wis <= 14){
								temp = Util.random(1, 10);
								if(temp == 1) {
									start_mp = 4;
								} else if(temp <= 3) {
									start_mp = 5;
								} else if(temp <= 7) {
									start_mp = 6;
								} else if(temp <= 9) {
									start_mp = 7;
								} else {
									start_mp = 8;
								}
							}else if(wis >= 15 && wis <= 17){
								temp = Util.random(1, 10);
								if(temp == 1) {
									start_mp = 6;
								} else if(temp <= 3) {
									start_mp = 7;
								} else if(temp <= 7) {
									start_mp = 8;
								} else if(temp <= 9) {
									start_mp = 9;
								} else {
									start_mp = 10;
								}
							}else if(wis >= 18 && wis <= 20){
								temp = Util.random(1, 22);
								if(temp == 1) {
									start_mp = 6;
								} else if(temp <= 3) {
									start_mp = 7;
								} else if(temp <= 7) {
									start_mp = 8;
								} else if(temp <= 15) {
									start_mp = 9;
								} else if(temp <= 19) {
									start_mp = 10;
								} else if(temp <= 21) {
									start_mp = 11;
								} else {
									start_mp = 12;
								}
							}else if(wis >= 21 && wis <= 23){
								temp = Util.random(1, 46);
								if(temp == 1) {
									start_mp = 6;
								} else if(temp <= 3) {
									start_mp = 7;
								} else if(temp <= 7) {
									start_mp = 8;
								} else if(temp <= 15) {
									start_mp = 9;
								} else if(temp <= 31) {
									start_mp = 10;
								} else if(temp <= 39) {
									start_mp = 11;
								} else if(temp <= 43) {
									start_mp = 12;
								} else if(temp <= 45) {
									start_mp = 13;
								} else {
									start_mp = 14;
								}
							}else if(wis >= 24 && wis <= 26){
								temp = Util.random(1, 94);
								if(temp == 1) {
									start_mp = 6;
								} else if(temp <= 3) {
									start_mp = 7;
								} else if(temp <= 7) {
									start_mp = 8;
								} else if(temp <= 15) {
									start_mp = 9;
								} else if(temp <= 31) {
									start_mp = 10;
								} else if(temp <= 63) {
									start_mp = 11;
								} else if(temp <= 79) {
									start_mp = 12;
								} else if(temp <= 87) {
									start_mp = 13;
								} else if(temp <= 91) {
									start_mp = 14;
								} else if(temp <= 93) {
									start_mp = 15;
								} else {
									start_mp = 16;
								}
							}else{ // else if(wis >= 27 && wis <= 29)
								temp = Util.random(1, 190);
								if(temp == 1) {
									start_mp = 6;
								} else if(temp <= 3) {
									start_mp = 7;
								} else if(temp <= 7) {
									start_mp = 8;
								} else if(temp <= 15) {
									start_mp = 9;
								} else if(temp <= 31) {
									start_mp = 10;
								} else if(temp <= 63) {
									start_mp = 11;
								} else if(temp <= 127) {
									start_mp = 12;
								} else if(temp <= 159) {
									start_mp = 13;
								} else if(temp <= 175) {
									start_mp = 14;
								} else if(temp <= 183) {
									start_mp = 15;
								} else if(temp <= 187) {
									start_mp = 16;
								} else if(temp <= 189) {
									start_mp = 17;
								} else {
									start_mp = 18;
								}
							}

							HPMP = start_mp;
							break;
					}
				}else if(cha instanceof SummonInstance){
					HPMP = Util.random(1, 2);
				}
			}
			return HPMP;
//		}
	}
	
}
