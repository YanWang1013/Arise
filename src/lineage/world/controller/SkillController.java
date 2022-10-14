package lineage.world.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Poly;
import lineage.bean.database.Skill;
import lineage.bean.lineage.Kingdom;
import lineage.database.PolyDatabase;
import lineage.database.SkillDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_SkillDelete;
import lineage.network.packet.server.S_SkillList;
import lineage.plugin.Plugin;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.GuardInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.instance.SummonInstance;
import lineage.world.object.magic.AbsoluteBarrier;
import lineage.world.object.magic.AdvanceSpirit;
import lineage.world.object.magic.AreaOfSilence;
import lineage.world.object.magic.Berserks;
import lineage.world.object.magic.BlessOfEarth;
import lineage.world.object.magic.BlessOfFire;
import lineage.world.object.magic.BlessWeapon;
import lineage.world.object.magic.BlessedArmor;
import lineage.world.object.magic.BodyToMind;
import lineage.world.object.magic.BurningSpirit;
import lineage.world.object.magic.BurningWeapon;
import lineage.world.object.magic.CallPledgeMember;
import lineage.world.object.magic.Cancellation;
import lineage.world.object.magic.ChangePosition;
import lineage.world.object.magic.ChillTouch;
import lineage.world.object.magic.ClearMind;
import lineage.world.object.magic.CounterDetection;
import lineage.world.object.magic.CounterMagic;
import lineage.world.object.magic.CreateMagicalWeapon;
import lineage.world.object.magic.CreateZombie;
import lineage.world.object.magic.Criminal;
import lineage.world.object.magic.CurePoison;
import lineage.world.object.magic.CurseBlind;
import lineage.world.object.magic.CurseParalyze;
import lineage.world.object.magic.CursePoison;
import lineage.world.object.magic.DecayPotion;
import lineage.world.object.magic.DecreaseWeight;
import lineage.world.object.magic.Detection;
import lineage.world.object.magic.Disease;
import lineage.world.object.magic.DoubleBreak;
import lineage.world.object.magic.EarthBind;
import lineage.world.object.magic.EarthSkin;
import lineage.world.object.magic.EnchantDexterity;
import lineage.world.object.magic.EnchantMighty;
import lineage.world.object.magic.EnchantVenom;
import lineage.world.object.magic.EnchantWeapon;
import lineage.world.object.magic.EnergyBolt;
import lineage.world.object.magic.EraseMagic;
//import lineage.world.object.magic.ExpPotion;
import lineage.world.object.magic.EyeOfStorm;
import lineage.world.object.magic.FinalBurn;
import lineage.world.object.magic.FireWeapon;
import lineage.world.object.magic.Firewall;
import lineage.world.object.magic.FogOfSleeping;
import lineage.world.object.magic.FreezingBlizzard;
import lineage.world.object.magic.GlowingAura;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.Heal;
import lineage.world.object.magic.HealAll;
import lineage.world.object.magic.HolyWalk;
import lineage.world.object.magic.HolyWeapon;
import lineage.world.object.magic.IceLance;
import lineage.world.object.magic.ImmuneToHarm;
import lineage.world.object.magic.InvisiBility;
import lineage.world.object.magic.IronSkin;
import lineage.world.object.magic.LifeStream;
import lineage.world.object.magic.Light;
import lineage.world.object.magic.Lightning;
import lineage.world.object.magic.ManaDrain;
import lineage.world.object.magic.MassSlow;
import lineage.world.object.magic.MassTeleport;
import lineage.world.object.magic.Meditation;
import lineage.world.object.magic.NatureMiracle;
import lineage.world.object.magic.NaturesBlessing;
import lineage.world.object.magic.NaturesTouch;
import lineage.world.object.magic.ProtectionFromElemental;
import lineage.world.object.magic.PurifyStone;
import lineage.world.object.magic.RemoveCurse;
import lineage.world.object.magic.ResistElemental;
import lineage.world.object.magic.ResistMagic;
import lineage.world.object.magic.Resurrection;
import lineage.world.object.magic.ReturnToNature;
import lineage.world.object.magic.RevealWeakness;
import lineage.world.object.magic.ShadowArmor;
import lineage.world.object.magic.ShadowFang;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.Shield;
import lineage.world.object.magic.ShiningAura;
import lineage.world.object.magic.Silence;
import lineage.world.object.magic.Slow;
import lineage.world.object.magic.StormShot;
import lineage.world.object.magic.SummonGreaterElemental;
import lineage.world.object.magic.SummonLesserElemental;
import lineage.world.object.magic.SummonMonster;
import lineage.world.object.magic.TameMonster;
import lineage.world.object.magic.Teleport;
import lineage.world.object.magic.TeleportToMother;
import lineage.world.object.magic.Tornado;
import lineage.world.object.magic.TrueTarget;
import lineage.world.object.magic.TurnUndead;
import lineage.world.object.magic.UncannyDodge;
import lineage.world.object.magic.VenomResist;
import lineage.world.object.magic.Weakness;
import lineage.world.object.magic.WeaponBreak;
import lineage.world.object.magic.WindShot;
import lineage.world.object.magic.WindWalk;
import lineage.world.object.magic.sp.ExpPotion;
import lineage.world.object.npc.kingdom.KingdomDoor;

public final class SkillController {

	// 패킷 전송에 사용됨.
	static private int lv[];
	// 지속형 마법 목록
	static private Map<Character, List<Skill>> list;
	// 마법투구를 통해 익힌 마법
	static private Map<Character, List<Skill>> temp_list;

	// int에 따른 mp패널티 적용값 변수. 해당 값만큼 mp소모를 감소.
	static int intMP[][] = {
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
		{0, 1, 2, 2, 2, 2, 2, 2, 2, 2},
		{0, 1, 2, 3, 3, 3, 3, 3, 3, 3},
		{0, 1, 2, 3, 4, 4, 4, 4, 4, 4},
		{0, 1, 2, 3, 4, 5, 5, 5, 5, 5},
		{0, 1, 2, 3, 4, 5, 6, 6, 6, 6},
		{0, 1, 2, 3, 4, 5, 6, 7, 7, 7}
	};

	static public void init(){
		TimeLine.start("SkillController..");

		temp_list = new HashMap<Character, List<Skill>>();
		list = new HashMap<Character, List<Skill>>();
		lv = new int[28];

		TimeLine.end();
	}

	/**
	 * 스킬 관리가 필요할때 호출해서 등록해주는 함수.
	 *  : 사용자는 월드 접속햇을때
	 *  : 몬스터는 객체 생성될때
	 * @param cha
	 */
	static public void toWorldJoin(Character cha){
		if(find(cha)==null)
			list.put(cha, new ArrayList<Skill>());
		if(findTemp(cha)==null)
			temp_list.put(cha, new ArrayList<Skill>());
	}

	/**
	 * 스킬 관리목록에서 제거처리하는 함수.
	 * @param cha
	 */
	static public void toWorldOut(Character cha){
		try {
			// 관리되고있는 자료구조 찾기.
			List<Skill> skill_list = find(cha);
			List<Skill> skill_temp_list = findTemp(cha);
			// 메모리에서 제거.
			skill_list.clear();
			skill_list = null;
			skill_temp_list.clear();
			skill_temp_list = null;
			list.remove(cha);
			temp_list.remove(cha);
		} catch (Exception e) {
			lineage.share.System.println(SkillController.class+" : toWorldOut(Character cha)");
			lineage.share.System.println(e);
		}
	}

	/**
	 * 마법 딜레이 확인하는 함수.
	 * 	: 딜레이 확인한후 마법 시전해도 되는지 리턴함.
	 * @param cha
	 * @param skill
	 * @return
	 */
	static private boolean isDelay(Character cha, Skill skill){
		// 초기화.
		long time = System.currentTimeMillis();

		// 딜레이 확인.
		if(cha.delay_magic==0 || cha.delay_magic<=time){
			cha.delay_magic = time + skill.getDelay();
			return true;
		}

		// 텔레포트마법일경우 락걸리기때문에 해당처리를 통해 풀기.
		Teleport.unLock(cha, false);
		// 실패.
		return false;
	}

	/**
	 * 스킬 사용 요청 처리 함수.
	 *  : 무기에서 마법이 발동될때 처리하는 함수.
	 *  : 리턴형을 이용해 이곳에서 실패할경우 skill클레스에 의존하여 데미지 산출함.
	 *  : ItemWeaponInstance.toDamage(Character cha, object o)
	 *  : SpellPotion 아이템쪽에서도 호출해서 사용함.
	 *  : SpellPotion.toClick(Character cha, ClientBasePacket cbp)
	 * @param cha
	 * @param o
	 * @param skill
	 * @return
	 */
	static public boolean toSkill(Character cha, object o, Skill skill, boolean me, int duration, String option){
		switch(skill.getUid()){
		case 1:
		case 19:
		case 35:
		case 57:
			if(me)
				Heal.onBuff(cha, cha, skill, skill.getCastGfx(), 0);
			else
				Heal.onBuff(cha, o, skill, skill.getCastGfx(), 0);
			return true;
		case 10:
		case 28:
			ChillTouch.toBuff(cha, skill, o, 0, 0, 0);
			return true;
		case 11:
			if(me)
				CursePoison.onBuff(cha, cha, skill);
			else
				CursePoison.onBuff(cha, o, skill);
			return true;
		case 20:
		case 40:
			if(me)
				CurseBlind.onBuff(cha, cha, skill);
			else
				CurseBlind.onBuff(cha, o, skill);
			return true;
		case 27:
			if(me)
				WeaponBreak.onBuff(cha, cha, skill);
			else
				WeaponBreak.onBuff(cha, o, skill);
			return true;
		case 29:
			Slow.onBuff(me ? cha : o, skill);
			return true;
		case 33:
			int counter = 0;
			try {
				if(option!=null && option.length()>0)
					counter = Integer.valueOf(option);
			} catch (Exception e) {
				counter = 0;
			}
			if(me)
				CurseParalyze.onBuff(cha, cha, skill, counter);
			else
				CurseParalyze.onBuff(cha, o, skill, counter);
			return true;
		case 37:
			RemoveCurse.onBuff(me ? cha : o, skill);
			return true;
		case 39:
			ManaDrain.onBuff(cha, o, skill);
			return true;
		case 43:
		case 54:
			Haste.onBuff(me ? cha : o, skill);
			return true;
		case 44:
			Cancellation.onBuff(me ? cha : o, skill);
			return true;
		case 48:
			BlessWeapon.onBuff(me ? cha : o, skill);
			return true;
		case 49:
			HealAll.onBuff(me ? cha : o, skill);
			return true;
		case 50:
			IceLance.init(me ? cha : o, duration);
			return true;
		case 56:
			Disease.onBuff(me ? cha : o, skill, duration);
			return true;
		case 64:
			Silence.onBuff(o, skill, duration);
			return true;
		case 68:
			ImmuneToHarm.onBuff(me ? cha : o, skill, duration);
			return true;
		case 78:
			AbsoluteBarrier.onBuff(me ? cha : o, skill, duration);
			return true;
		case 210:
		case 211:
		case 212:
		case 213:
			ExpPotion.onBuff(me ? cha : o, skill);
			return true;
		}
		return false;
	}

	/**
	 * 스킬 사용 요청 처리 함수.
	 * @param cha
	 * @param level
	 * @param number
	 */
	static public void toSkill(Character cha, final ClientBasePacket cbp){
		int level = cbp.readC() + 1;
		int number = cbp.readC();

		Plugin p = PluginController.find(SkillController.class);
		if(p!=null && ((lineage.plugin.bean.SkillController)p).toSkill(cha, cbp, level, number))
			return;

		Skill skill = find(cha, level, number);
		if(skill!=null && isDelay(cha, skill)){
			switch(skill.getSkillLevel()){
			case 1:
				switch(skill.getSkillNumber()){
				case 0:	// 힐
					Heal.init(cha, skill, cbp.readD());
					break;
				case 1:	// 라이트
					Light.init(cha, skill);
					break;
				case 2:	// 실드
					Shield.init(cha, skill);
					break;
				case 3:	// 에너지볼트
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 4:	// 텔레포트
					Teleport.init(cha, skill, cbp);
					break;
				case 5:	// 아이스 대거
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 6:	// 윈드 커터
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 7:	// 홀리 웨폰
					HolyWeapon.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 2:
				switch(skill.getSkillNumber()){
				case 0:
					CurePoison.init(cha, skill, cbp.readD());
					break;
				case 1:
					ChillTouch.init(cha, skill, cbp.readD());
					break;
				case 2:
					CursePoison.init(cha, skill, cbp.readD());
					break;
				case 3:
					EnchantWeapon.init(cha, skill, cbp.readD());
					break;
				case 4:
					Detection.init(cha, skill);
					break;
				case 5:
					DecreaseWeight.init(cha, skill);
					break;
				case 6:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 7:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 3:
				switch(skill.getSkillNumber()){
				case 0:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 1:
					TurnUndead.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 2:
					Heal.init(cha, skill, cbp.readD());
					break;
				case 3:
					CurseBlind.init(cha, skill, cbp.readD());
					break;
				case 4:
					BlessedArmor.init(cha, skill, cbp.readD());
					break;
				case 5:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 6:
				case 7:
					RevealWeakness.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 4:
				switch(skill.getSkillNumber()){
				case 0:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 1:
					EnchantDexterity.init(cha, skill, cbp.readD());
					break;
				case 2:
					WeaponBreak.init(cha, skill, cbp.readD());
					break;
				case 3:
					ChillTouch.init(cha, skill, cbp.readD());
					break;
				case 4:
					Slow.init(cha, skill, cbp.readD());
					break;
				case 5:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 6:
					CounterMagic.init(cha, skill);
					break;
				case 7:
					Meditation.init(cha, skill);
					break;
				}
				break;
			case 5:
				switch(skill.getSkillNumber()){
				case 0:
					CurseParalyze.init(cha, skill, cbp.readD());
					break;
				case 1:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 2:
					Heal.init(cha, skill, cbp.readD());
					break;
				case 3:
					TameMonster.init(cha, skill, cbp.readD());
					break;
				case 4:
					RemoveCurse.init(cha, skill, cbp.readD());
					break;
				case 5:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 6:
					ManaDrain.init(cha, skill, cbp.readD());
					break;
				case 7:
					CurseBlind.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 6:
				switch(skill.getSkillNumber()){
				case 0:
					CreateZombie.init(cha, skill, cbp.readD());
					break;
				case 1:
					EnchantMighty.init(cha, skill, cbp.readD());
					break;
				case 2:
					Haste.init(cha, skill, cbp.readD());
					break;
				case 3:
					Cancellation.init(cha, skill, cbp.readD());
					break;
				case 4:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 5:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 6:
					Weakness.init(cha, skill, cbp.readD());
					break;
				case 7:
					BlessWeapon.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 7:
				switch(skill.getSkillNumber()){
				case 0:
					HealAll.init(cha, skill);
					break;
				case 1:
					IceLance.init(cha, skill, cbp.readD());
					break;
				case 2:
					SummonMonster.init(cha, skill);
					break;
				case 3:
					HolyWalk.init(cha, skill);
					break;
				case 4:
					Tornado.init(cha, skill);
					break;
				case 5:
					Haste.init(cha, skill, cha.getObjectId());
					break;
				case 6:
					Berserks.init(cha, skill, cha.getObjectId());
					break;
				case 7:
					Disease.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 8:
				switch(skill.getSkillNumber()){
				case 0:
					Heal.init(cha, skill, cbp.readD());
					break;
				case 1:
					Firewall.init(cha, skill, cbp.readH(), cbp.readH());
					break;
				case 2:
					Tornado.init(cha, skill);
					break;
				case 3:
					InvisiBility.init(cha, skill);
					break;
				case 4:
					Resurrection.init(cha, skill, cbp.readD());
					break;
				case 5:
					Tornado.init(cha, skill);
					break;
				case 6:
					LifeStream.init(cha, skill);
					break;
				case 7:
					Silence.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 9:
				switch(skill.getSkillNumber()){
				case 0:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 1:
					FogOfSleeping.init(cha, skill, cbp.readH(), cbp.readH());
					break;
				case 2:
					ShapeChange.init(cha, skill, cbp.readD());
					break;
				case 3:
					ImmuneToHarm.init(cha, skill, cbp.readD());
					break;
				case 4:
					MassTeleport.init(cha, skill, cbp);
					break;
				case 5:
					Tornado.init(cha, skill);
					break;
				case 6:
					DecayPotion.init(cha, skill, cbp.readD());
					break;
				case 7:
					CounterDetection.init(cha, skill);
					break;
				}
				break;
			case 10:
				switch(skill.getSkillNumber()){
				case 0:
					CreateMagicalWeapon.init(cha, skill, cbp.readD());
					break;
				case 1:
					Lightning.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH());
					break;
				case 2:
					break;
				case 3:
					MassSlow.init(cha, skill, cbp.readD());
					break;
				case 4:
					EnergyBolt.init(cha, skill, cbp.readD());
					break;
				case 5:
					AbsoluteBarrier.init(cha, skill);
					break;
				case 6:
					if(Lineage.server_version>230)
						AdvanceSpirit.init(cha, skill, cbp.readD());
					else
						ChangePosition.init(cha, skill, cbp.readD());
					break;
				case 7:	// 프리징 블리자드
					FreezingBlizzard.init(cha, skill);
					break;
				}
				break;
			case 13:
				switch(skill.getSkillNumber()){
				case 0:
					InvisiBility.init(cha, skill);
					break;
				case 1:
					EnchantVenom.init(cha, skill, cbp.readD());
					break;
				case 2:
					ShadowArmor.init(cha, skill);
					break;
				case 3:
					PurifyStone.init(cha, skill, cbp.readD());
					break;
				case 4:
					HolyWalk.init(cha, skill);
					break;
				case 5:
					BurningSpirit.init(cha, skill);
					break;
				case 6:
					CurseBlind.init(cha, skill, cbp.readD());
					break;
				case 7:
					VenomResist.init(cha, skill);
					break;
				}
				break;
			case 14:
				switch(skill.getSkillNumber()){
				case 0:
					DoubleBreak.init(cha, skill);
					break;
				case 1:
					UncannyDodge.init(cha, skill);
					break;
				case 2:
					ShadowFang.init(cha, skill, cbp.readD());
					break;
				case 3:
					FinalBurn.init(cha, skill, cbp.readD());
					break;
					//						case 4:
					//							EnchantMighty.init(cha, skill, (int)cha.getObjectId());
					//							break;
				}
				break;
			case 15:
				switch(skill.getSkillNumber()){
				case 0:
					TrueTarget.init(cha, skill, cbp.readD(), cbp.readH(), cbp.readH(), cbp.readS());
					break;
				case 1:
					GlowingAura.init(cha, skill);
					break;
				case 2:
					ShiningAura.init(cha, skill);
					break;
				case 3:
					CallPledgeMember.init(cha, skill, cbp.readS());
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					break;
				}
				break;
			case 17:
				switch(skill.getSkillNumber()){
				case 0:
					ResistMagic.init(cha, skill);
					break;
				case 1:
					BodyToMind.init(cha, skill);
					break;
				case 2:
					TeleportToMother.init(cha, skill);
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				}
				break;
			case 18:
				switch(skill.getSkillNumber()){
				case 0:
					ClearMind.init(cha, skill);
					break;
				case 1:
					ResistElemental.init(cha, skill);
					break;
				}
				break;
			case 19:
				switch(skill.getSkillNumber()){
				case 0:
					ReturnToNature.init(cha, skill, cbp.readD());
					break;
				case 1:
					BodyToMind.init(cha, skill);
					break;
				case 2:
					ProtectionFromElemental.init(cha, skill);
					break;
				case 3:
					FireWeapon.init(cha, skill, cbp.readD());
					break;
				case 4:
					WindShot.init(cha, skill, cbp.readD());
					break;
				case 5:
					WindWalk.init(cha, skill);
					break;
				case 6:
					EarthSkin.init(cha, skill, cbp.readD());
					break;
				case 7:
					Slow.init(cha, skill, cbp.readD());
					break;
				}
				break;
			case 20:
				switch(skill.getSkillNumber()){
				case 0:
					EraseMagic.init(cha, skill, cbp.readD());
					break;
				case 1:
					SummonLesserElemental.init(cha, skill);
					break;
				case 2:
					BlessOfFire.init(cha, skill);
					break;
				case 3:
					EyeOfStorm.init(cha, skill);
					break;
				case 4:
					EarthBind.init(cha, skill, cbp.readD());
					break;
				case 5:
					NaturesTouch.init(cha, skill, cbp.readD());
					break;
				case 6:
					BlessOfEarth.init(cha, skill);
					break;
				case 7:
					break;
				}
				break;
			case 21:
				switch(skill.getSkillNumber()){
				case 0:
					AreaOfSilence.init(cha, skill);
					break;
				case 1:
					SummonGreaterElemental.init(cha, skill);
					break;
				case 2:
					BurningWeapon.init(cha, skill, cbp.readD());
					break;
				case 3:
					NaturesBlessing.init(cha, skill);
					break;
				case 4:
					NatureMiracle.init(cha, skill, cbp.readD());
					break;
				case 5:
					StormShot.init(cha, skill, cbp.readD());
					break;
				case 6:
					break;
				case 7:
					IronSkin.init(cha, skill, cbp.readD());
					break;
				}
				break;
				/*case 0:
					switch(skill.getSkillNumber()){
						case 0:
							break;
						case 1:
							break;
						case 2:
							break;
						case 3:
							break;
						case 4:
							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							break;
					}
					break;*/
			}
		}
	}

	/**
	 * npc로부터 배울수 있는 스킬 갯수 리턴하는 메서드.
	 */
	static public int getBuySkillCount(Character cha){
		int count = 0;
		int idx = getBuySkillIdx(cha);
		// 최대 배울수 잇는 범위내에 가지고잇는 스킬 갯수 추출
		for( Skill s : find(cha) ) {
			if(s.getUid() <= idx)
				count += 1;
		}
		// 최대배울수잇는갯수 - 가지고잇는 갯수 = 남은갯수를 리턴함.
		return idx - count;
	}

	/**
	 * 클레스별로 레벨에 맞춰서 최대 배울수 잇는 갯수 추출.
	 */
	static public int getBuySkillIdx(Character cha){
		int level = cha.getLevel();
		int gab = 0;
		switch(cha.getClassType()){
		case Lineage.LINEAGE_CLASS_ROYAL:
			if(cha.getLevel()>20)
				level = 20;
			gab = 10;
			break;
		case Lineage.LINEAGE_CLASS_KNIGHT:
			if(cha.getLevel()>50)
				level = 50;
			gab = 50;
			break;
		case Lineage.LINEAGE_CLASS_ELF:
			if(cha.getLevel()>24)
				level = 24;
			gab = 8;
			break;
		case Lineage.LINEAGE_CLASS_WIZARD:
			if(cha.getLevel()>12)
				level = 12;
			gab = 4;
			break;
		case Lineage.LINEAGE_CLASS_DARKELF:
			if(cha.getLevel()>24)
				level = 24;
			gab = 12;
			break;
		}
		return ((level/gab) * 8);
	}

	/**
	 * 스킬 제거 처리 함수.
	 * @param cha
	 * @param uid
	 * @param temp
	 */
	static public void remove(Character cha, int uid, boolean temp){
		remove( cha, find(cha, uid, temp), temp);
	}

	/**
	 * 스킬 제거 처리 함수.
	 * @param cha
	 * @param level
	 * @param number
	 */
	static public void remove(Character cha, int level, int number){
		remove( cha, find(cha, level, number, false), false);
	}

	/**
	 * 스킬 제거 처리 함수.
	 *  : 중복 코드 방지용.
	 * @param cha
	 * @param s
	 * @param temp
	 */
	static public void remove(Character cha, Skill s, boolean temp){
		if(s == null)
			return;
		if(temp)
			findTemp(cha).remove(s);
		else
			find(cha).remove(s);
		// 메모리 초기화
		for(int i=lv.length-1 ; i>=0 ; --i)
			lv[i] = 0;
		// 가지고있는 스킬이라면 갱신 안하기.
		if(find(cha, s.getUid(), false) == null)
			lv[s.getSkillLevel()-1] += s.getId();
		// 패킷 전송.
		cha.toSender(S_SkillDelete.clone(BasePacketPooling.getPool(S_SkillDelete.class), lv));
	}

	/**
	 * 스킬 추가 처리 함수.
	 * @param cha
	 * @param uid
	 * @param temp
	 */
	static public void append(Character cha, int uid, boolean temp){
		Skill s = find(cha, uid, temp);
		if(s == null){
			s = SkillDatabase.find(uid);
			if(s != null){
				if(temp)
					findTemp(cha).add( s );
				else
					find(cha).add( s );
			}
		}
	}

	/**
	 * 스킬 추가 처리 함수.
	 * @param cha
	 * @param level
	 * @param number
	 * @param temp
	 */
	static public void append(Character cha, int level, int number, boolean temp){
		Skill s = find(cha, level, number, temp);
		if(s == null){
			s = SkillDatabase.find(level, number);
			if(s != null){
				if(temp)
					findTemp(cha).add( s );
				else
					find(cha).add( s );
			}
		}
	}

	/**
	 * 연결된 객체 찾아서 리턴.
	 * @param pc
	 * @return
	 */
	static public List<Skill> find(Character cha){
		return list.get(cha);
	}

	/**
	 * 연결된 객체 찾아서 리턴.
	 * @param pc
	 * @return
	 */
	static public List<Skill> findTemp(Character cha){
		return temp_list.get(cha);
	}

	/**
	 * 같은 스킬이 존재하는지 확인하는 메서드.
	 */
	static public Skill find(Character cha, int uid, boolean temp){
		if(temp){
			for( Skill s : findTemp(cha) ) {
				if(uid == s.getUid())
					return s;
			}
		}else{
			for( Skill s : find(cha) ) {
				if(uid == s.getUid())
					return s;
			}
		}
		return null;
	}

	/**
	 * 같은 스킬이 존재하는지 확인하는 메서드.
	 */
	static public Skill find(Character cha, int level, int number){
		for( Skill s : find(cha) ) {
			if(s.getSkillLevel()==level && s.getSkillNumber()==number)
				return s;
		}
		for( Skill s : findTemp(cha) ) {
			if(s.getSkillLevel()==level && s.getSkillNumber()==number)
				return s;
		}
		return null;
	}

	/**
	 * 일치하는 마법 리턴함.
	 */
	static public Skill find(Character cha, int level, int number, boolean temp){
		if(temp){
			for( Skill s : findTemp(cha) ) {
				if(s.getSkillLevel()==level && s.getSkillNumber()==number)
					return s;
			}
		}else{
			for( Skill s : find(cha) ) {
				if(s.getSkillLevel()==level && s.getSkillNumber()==number)
					return s;
			}
		}
		return null;
	}

	/**
	 * 습득한 스킬정보 전송하는 함수.
	 */
	static public void sendList(Character cha){
		// 메모리 초기화
		for(int i=lv.length-1 ; i>=0 ; --i)
			lv[i] = 0;
		// 실제 습득을 통해 익힌 마법목록
		for( Skill s : find(cha) )
			lv[s.getSkillLevel()-1] += s.getId();
		// 마법투구를 통해 습득한 임시 마법. 실제습득한 마법에 없을경우 그값을 갱신.
		for( Skill s : findTemp(cha) ) {
			if(find(cha, s.getUid(), false)==null)
				lv[s.getSkillLevel()-1] += s.getId();
		}
		// 패킷 전송.
		cha.toSender(S_SkillList.clone(BasePacketPooling.getPool(S_SkillList.class), lv));
	}

	/**
	 * 마법을 시전하기전에 hp, mp, item 및 상태에 따라 마법시전해도 되는지 확인해보는 메서드.
	 *  : skill 이 null 로 올수도 잇기 때문에 gamso 값이 true일때만 사용하기.
	 */
	static public boolean isMagic(Character cha, Skill skill, boolean gamso){
		if(cha.isDead() || cha.isWorldDelete())
			return false;
		// 운영자는 무조건 성공.
		if(cha.getGm()>=Lineage.GMCODE)
			return true;
		// 앱솔상태 해제.
		if(cha.isBuffAbsoluteBarrier())
			BuffController.remove(cha, AbsoluteBarrier.class);
		// 사일런스 상태라면 마법을 사용 할 수 없도록 차단
		if(cha.isBuffSilence())
			return false;
		// 무게 확인
		if(cha instanceof PcInstance && cha.getInventory()!=null && cha.getInventory().getWeightPercent()>=24){
			// \f1짐이 너무 무거워 마법을 사용할 수 없습니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 316));
			return false;
		}
		// 변신상태일경우 마법 시전가능한지 확인.
		if(cha.getGfx() != cha.getClassGfx()){
			Poly p = PolyDatabase.getPolyGfx(cha.getGfx());
			if(p!=null && !p.isSkill()){
				// \f1그 상태로는 마법을 사용할 수 없습니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 285));
				return false;
			}
		}
		
		// 감소 처리를 할경우.
		if(gamso){
			if(skill == null)
				return false;
			
		// 처리.
		int hp = skill.getHpConsume();
		int mp = skill.getMpConsume();
		int lawful = cha instanceof MonsterInstance ? 0 : skill.getLawfulConsume();
		int intmpNum = cha.getTotalInt()-12;
		int skilllev = skill.getSkillLevel()-1;
		ItemInstance item = null;
		// int값에따른 mp소모량 감소
		if(intmpNum>7)
			intmpNum = 7;
		else if(intmpNum<=0)
			intmpNum = 0;
		if(skilllev>9)
			skilllev = 0;
		else if(skilllev<=0)
			skilllev = 0;
		mp -= intMP[intmpNum][skilllev];
		// 기사 패널티 적용.
		if(cha.getClassType()==Lineage.LINEAGE_CLASS_KNIGHT){
			mp = (int)(mp*0.5);
			hp = (int)(hp*0.5);
		}
		// hp확인
		if(hp>0 && cha.getNowHp()<=hp){
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 279));
			return false;
		}
		// mp확인
		if(mp>0 && cha.getNowMp()<mp){
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 278));
			return false;
		}
		// 사용자들만 재료 확인.
		if(skill.getItemConsume()>0 && cha instanceof PcInstance){
			item = cha.getInventory().findDbNameId(skill.getItemConsume());
			if(item==null || item.getCount()<skill.getItemConsumeCount()){
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 299));
				return false;
			}
		}

		if(hp>0)
			cha.setNowHp( cha.getNowHp()-hp );
		if(mp>0)
			cha.setNowMp( cha.getNowMp()-mp );
		if(lawful > 0)
			cha.setLawful( cha.getLawful()-lawful );
		if(item != null){
			//
			cha.getInventory().count(item, item.getCount()-skill.getItemConsumeCount(), true);
		}
	}
	return true;
}

	/**
	 * 해당 스킬에 필요한 hp, mp가 있는지 확인해주는 함수.
	 * @param cha
	 * @param skill
	 * @return
	 */
	static public boolean isHpMpCheck(Character cha, Skill skill){
		int hp = skill.getHpConsume();
		int mp = skill.getMpConsume();
		if(hp>0 && cha.getNowHp()<=hp)
			return false;
		if(mp>0 && cha.getNowMp()<mp)
			return false;

		return true;
	}

	/**
	 * 마법시전 성공여부 확인하는 함수.
	 * @param cha
	 * @param o
	 * @param skill
	 * @return
	 */
	static public boolean isFigure(Character cha, object o, Skill skill, boolean is_rate, boolean is_clan){
		// 영자는 무조건 성공
		if(cha.getGm()>=Lineage.GMCODE)
			return true;
		// 없거나 죽은상태 패스
		if(o==null || o.isDead())
			return false;
		// 앱솔 무시.
		if(o.isBuffAbsoluteBarrier())
			return false;
		// 몬스터끼리는 모두 실패시키기.
		if(cha instanceof MonsterInstance && o instanceof MonsterInstance)
			return false;
		// 공격존체크해야되는 마법시 공격가능존 체크.
		if(isZoneCheckMagic(skill) && !World.isAttack(cha, o))
			return false;
		// 캔슬레이션을 제외한 모든확율마법은 석화상태 패스하기.
		if(skill.getUid()!=44 && o.isLockHigh())
			return false;
		// 마법시전이 불가능한 몬스터는 무시.
		if(o instanceof MonsterInstance && !((MonsterInstance)o).getMonster().isBuff())
			return false;
		// 독계열 마법일경우 저항력 체크.
		if(skill.getElement() == Lineage.ELEMENT_POISON){
			if(o.isBuffVenomResist())
				return false;
		}
		// 힐계열이 아닐때만 카운터매직 확인.
		if(!isHeal(skill) && o.isBuffCounterMagic() && cha.getObjectId()!=o.getObjectId()) {
			BuffController.remove(o, CounterMagic.class);
			return false;
		}
		// 같은 혈맹원끼리 시전하는거라면 무조건 성공.
		if(is_clan)
			return true;
		// 확율 체크 안할경우 그냥 성공으로 리턴.
		if(!is_rate)
			return true;
		// 캔슬레이션 확률만 따로 체크.
		if(skill.getSkillLevel()==6 && skill.getSkillNumber()==3) {
			if(cha instanceof PcInstance && o instanceof PcInstance) {
				if(o.isInvis())
					
					return true;
				if(cha.getPartyId()>0 && cha.getPartyId()==o.getPartyId())
					return true;
				if(World.isSafetyZone(cha.getX(), cha.getY(), cha.getMap()) || World.isSafetyZone(o.getX(), o.getY(), o.getMap()))
					return true;
			}
			if(cha instanceof PcInstance && o instanceof MonsterInstance)
				return true;
			if(cha instanceof MonsterInstance && o instanceof PcInstance)
				return true;
			if(cha instanceof MonsterInstance && o instanceof MonsterInstance)
				return true;
			
			if(o instanceof Character) {
				Character target = (Character)o;
				int mr = getMr(target, false);
				if(mr >= 150)
					return false;
				// 
				double rate = (1 - ((mr*0.25) * 0.01)) * 100;
				return Util.random(0, 99) < Util.random(0, (int)rate);
			}
		}

		boolean is = Util.random(1, 100) > Util.random(1, 100);
		// 확율 체크.
		if(o instanceof Character){
			Character target = (Character)o;
			// 공격측 포인트：LV + ((MagicBonus * 3) * 마법 고유 계수)
			// 방어측 포인트：((LV / 2) + (MR * 3)) / 2
			// 공격 성공율：공격측 포인트 - 방어측 포인트
			int attker_rate = cha.getLevel() + toOriginalStatMagicHit(cha) + toMagicFigure(cha)
					+ ((getMagicBonus(cha) * 3) * getMagicLevel(cha));
			int defence_rate = (target.getLevel() / 2) + (getMr(target, false) * 3);
			is = Util.random(0, attker_rate) > Util.random(0, defence_rate);
		}

		// 성공했을때 존체크마법일경우라면 보라돌이로 처리.
		if (is && isZoneCheckMagic(skill) && cha instanceof PcInstance && o instanceof PcInstance) {
			// 공격자가 보라돌이상태가 아니라면 보라도리로 변경하기.
			if (Lineage.server_version > 182 && !cha.isBuffCriminal())
				Criminal.init(cha);
		}

		return is;
	}

	/**
	 * 같은 혈맹원인지 체크
	 * @param cha
	 * @param o
	 * @return
	 */
	static public boolean isClan(Character cha, object o){
		if(o==null)
			return false;
		if(cha.getObjectId()!=o.getObjectId()){
			if(o.getClanId()==0)
				return false;
			if(cha.getClanId()!=o.getClanId())
				return false;
		}
		return true;
	}

	/**
	 * 같은 파티원인지 체크.
	 * @param cha
	 * @param o
	 * @return
	 */
	static public boolean isParty(Character cha, object o){
		return cha.getObjectId()==o.getObjectId() || (cha.getPartyId()!=0 && o.getPartyId()==cha.getPartyId());
	}

	/**
	 * 데미지 추출 처리 함수.
	 * @param cha
	 * @param target
	 * @param o
	 * @param skill
	 * @param alpha_dmg
	 * @param skill_element
	 * @return
	 */
	static public int getDamage(Character cha, object target, object o, Skill skill, int alpha_dmg, int skill_element){
		// 버그 방지
		if(o==null || skill==null)
			return 0;
		// 기본적으로 공격 불가능한 객체
		if(o instanceof ItemInstance || o instanceof BoardInstance)
			return 0;
		// Felix: Ignore 901 (Hide Dupelgenon)
		if (o.getGfx() == 901) 
			return 0;
		// Felix: Ignore Recess mode
		if (o.isRecess()) 
			return 0;
		// 죽은거 무시.
		if(o.isDead())
			return 0;
		// 굳은거 무시.
		if(o.isLockHigh())
			return 0;
		// 앱솔 무시.
		if(o.isBuffAbsoluteBarrier())
			return 0;
		// 힐계열이 아닐때만 카운터매직 확인.
		if(!isHeal(skill) && o.isBuffCounterMagic() && cha.getObjectId()!=o.getObjectId()){
			BuffController.remove(o, CounterMagic.class);
			return 0;
		}
		// 공격마법일때 공격 가능존인지 확인.
		if(isAttackMagic(skill) && !World.isAttack(cha, o))
			return 0;
		if(isAttackRangeMagic(skill) && !isMagicAttackRange(cha, o) && target.getObjectId()!=o.getObjectId())
			return 0;
		// 장애물이 막고있으면 무시.
		if(!Util.isAreaAttack(cha, o))
			return 0;
		// 내성문이라면 공성중일때만 가능.
		if(target instanceof KingdomDoor){
			KingdomDoor kd = (KingdomDoor)target;
			if(kd.getKingdom()==null || kd.getNpc()==null)
				return 0;
			if(!kd.getKingdom().isWar() && kd.getNpc().getName().indexOf("내성문")>0)
				return 0;
		}
		
		double o_mr = 0;

		if(o instanceof Character){
			Character t = (Character)o;
			o_mr = getMr(t, false);
			ItemInstance shield = null;
			// 레이저종류의 마법일경우 타켓의 방패종류에따라 데미지를 적용 안함.
			if(skill.getElement()==Lineage.ELEMENT_LASER && t.getInventory()!=null){
				shield = t.getInventory().getSlot(Lineage.SLOT_SHIELD);
				if(shield != null){
					switch(shield.getItem().getNameIdNumber()){
					case 196:	// 반사 방패
						if(shield.getEnLevel()>4 && Util.random(1, 100)<=50)
							return 0;
						break;
					case 2035:	// 붉은 기사의 방패
						if(Util.random(1, 100)<=50)
							return 0;
						break;
					}
				}
			}
			// 굳는 마법 일경우 반사방패 반사 처리효과 넣기.
			if(skill.getLock().equalsIgnoreCase("low") || skill.getLock().equalsIgnoreCase("high")){
				shield = t.getInventory().getSlot(Lineage.SLOT_SHIELD);
				if(shield != null){
					switch(shield.getItem().getNameIdNumber()){
					case 196:	// 반사 방패
						if(shield.getEnLevel()>4)
							return 0;
						break;
					}
				}
			}
		}
		
		int o_fire = 0;
		int o_warter = 0;
		int o_earh = 0;
		int o_wind = 0;
		int o_element = 0;
		if(o instanceof Character){
			Character o_cha = (Character)o;
			o_fire = o_cha.getTotalFireress();
			o_earh = o_cha.getTotalEarthress();
			o_wind = o_cha.getTotalWindress();
			o_warter = o_cha.getTotalWaterress();
		}
		
		double dmg = alpha_dmg;
		// 데미지 연산
		double stat_dmg = (double)toOriginalStatMagicDamage(cha);
		double sp_dmg = (double)getSp(cha);
		double int_dmg = (double)getIntDamage(cha);
		double ml_dmg = (double)getMagicLevel(cha);
		double mb_dmg = (double)getMagicBonus(cha);
		double mindmg = skill.getMindmg() * ((sp_dmg*0.1)+ (stat_dmg*0.1) + (int_dmg*0.1) + (ml_dmg*0.1) + (mb_dmg*0.1) + 1);
		double maxdmg = skill.getMaxdmg() * ((sp_dmg*0.11) + (stat_dmg*0.11) + (int_dmg*0.11) + (ml_dmg*0.11) + (mb_dmg*0.11) + 1);
		// 데미지 추출.
		dmg += Util.random(mindmg, maxdmg);
		
		// 힐계열 마법이라면 라우풀값에 따라 데미지 영향주기.
		if (isHeal(skill))
			dmg *= (double)cha.getLawful()/(double)Lineage.NEUTRAL;

		// 카오틱 마법이라면 라우풀 수치에따라 데미지에 영향주기.
		if (isChaoticMagic(skill))
			// 뉴트럴/만카 는 데미지에 2배가됨. 그2배에 반만 반영하기 위해 0.75를 곱함.
			dmg *= ((double)Lineage.NEUTRAL/(double)cha.getLawful()) * 0.75;

		// 지혜물약 복용상태
		if (cha.isBuffWisdom())
			dmg *= 1.2;
		
		// 힐계열 마법이 아닐때
		// : mr체크해서 데미지 하향.
		// : 기타 버프 상태따라 처리.
		if (!isHeal(skill)) {
			// 속성저항력값 추출.
			switch(skill_element){
				case Lineage.ELEMENT_FIRE:
					o_element = o_fire;
					break;
				case Lineage.ELEMENT_WATER:
					o_element = o_warter;
					break;
				case Lineage.ELEMENT_EARTH:
					o_element = o_earh;
					break;
				case Lineage.ELEMENT_WIND:
					o_element = o_wind;
					break;
			}
			// 속성마법 데미지 하향처리.
			if(o_element > 0) {
				double el_dmg = o_element * 0.6;
				if(el_dmg > 100)
					el_dmg = 100;
				el_dmg = el_dmg * 0.01;
				dmg -= dmg * el_dmg;
			}
			// mr 값에 따른 감소값 추출. 퍼센트
			double mr_dmg = (o_mr*0.45) * 0.01;	// mr에 45%만 감소에 반영하기.
			// 감소.
			dmg -= dmg * mr_dmg;				// mr에따른 데미지 감소.
			dmg = Util.random(dmg*0.8, dmg);	// 최종 추출된 데미지에서 10%정도만 낮춰서 랜덤 추출.
			
			// 이뮨 투 함
			if(o.isBuffImmuneToHarm())
				dmg = dmg<=0 ? 0 : dmg / 2;
			
		// 힐계열 마법일때.
		// : 기타 버프상태에 따라 처리.
		} else {
			// 폴루트워터 상태일때 반으로 처리.
			if(o.isBuffPolluteWater())
				dmg = dmg<=0 ? 0 : dmg / 2;
		}
		
		return (int)(dmg<0 ? 0 : dmg);
	}

	/**
	 * 카오틱 마법인지 확인.
	 */
	static private boolean isChaoticMagic(Skill skill){
		switch(skill.getUid()){
		case 10:	// 칠터치
		case 28:	// 뱀파이어릭터치
		case 59:	// 블리자드
			return true;
		}
		return false;
	}

	/**
	 * 힐 계열 마법인지 확인.
	 * @param skill
	 * @return
	 */
	static private boolean isHeal(Skill skill){
		switch(skill.getUid()){
		case 1:		// 힐
		case 19:	// 익스트라 힐
		case 35:	// 그레이터 힐
		case 57:	// 풀 힐
		case 133:	// 네이쳐스블레싱
			return true;
		}
		return false;
	}

	/**
	 * 공격형 마법인지 확인.
	 */
	static private boolean isAttackMagic(Skill skill){
		switch(skill.getUid()){
		case 4:		// 에너지 볼트
		case 6:		// 아이스 대거
		case 7:		// 윈드 커터
		case 10:	// 칠 터치
		case 15:	// 파이어 애로우
		case 16:	// 스탈락
		case 17:	// 라이트닝
		case 18:	// 턴 언데드
		case 22:	// 프로즌 클라우드
		case 25:	// 파이어 볼
		case 28:	// 뱀파이어릭 터치
		case 30:	// 어스 재일
		case 34:	// 콜 라이트닝
		case 38:	// 콘 오브 콜드
		case 45:	// 이럽션
		case 46:	// 선 버스트
		case 50:	// 아이스 랜스
		case 53:	// 토네이도
		case 58:	// 파이어 월
		case 59:	// 블리자드
		case 62:	// 어스 퀘이크
		case 65:	// 라이트닝 스톰
		case 70:	// 스톰
		case 74:	// 미티어 스트라이크
		case 77:	// 디스인티 그레이트
		case 80:	// 프리징 블리자드
			return true;
		}
		return false;
	}

	/**
	 * 범위 공격형 마법인지 확인.
	 */
	static private boolean isAttackRangeMagic(Skill skill){
		switch(skill.getUid()){
		case 17:	// 라이트닝
		case 22:	// 프로즌 클라우드
		case 25:	// 파이어 볼
		case 53:	// 토네이도
		case 59:	// 블리자드
		case 62:	// 어스 퀘이크
		case 65:	// 라이트닝 스톰
		case 70:	// 스톰
		case 74:	// 미티어 스트라이크
		case 80:	// 프리징 블리자드
			return true;
		}
		return false;
	}

	/**
	 * 확률 계산시 존을 체크해서 처리해야되는 마법인지 확인용 함수.
	 *  : 보라돌이 처리할 마법인지 확인할때도 사용중.
	 */
	static private boolean isZoneCheckMagic(Skill skill){
		switch(skill.getUid()){
		case 11:	// 커스:포이즌
		case 18:	// 턴 언데드
		case 20:	// 커스:블라인드
		case 27:	// 웨폰브레이크
		case 29:	// 슬로우
		case 33:	// 커스:패럴라이즈
		case 39:	// 마나드레인
		case 40:	// 다크니스
		case 44:	// 캔슬레이션
		case 47:	// 위크니스
		case 50:	// 아이스랜스
		case 56:	// 디지즈
		case 64:	// 사일런스
		case 66:	// 포그오브슬리핑
		case 71:	// 디케이포션
		case 115:	// 리턴투네이처
		case 127:	// 어스 바인드
		case 130:	// 에어리어 오브 사일런스
			return true;
		}
		return false;
	}

	/**
	 * 범위 공격형 마법시 데미지를 연산 해도되는 타켓인지 검색하는 함수.
	 * @param cha	: 공격자
	 * @param o		: 대상자
	 * @return
	 */
	static private boolean isMagicAttackRange(Character cha, object o){
		// 사용자 일경우
		if( cha instanceof PcInstance ){
			// 성존 부분 따로 구분.
			Kingdom k = KingdomController.findKingdomLocation(cha);
			if(k!=null && k.isWar()){
				// 성존에 공성중일때 같은 혈맹 소속들은 무시.
				if(cha.getClanId()>0 && cha.getClanId()==o.getClanId())
					return false;
				return true;
			}
			return !(o instanceof PcInstance) && !(o instanceof SummonInstance) && !(o instanceof GuardInstance);
		}
		// 몬스터일 경우
		if( cha instanceof MonsterInstance)
			return o instanceof PcInstance || o instanceof SummonInstance;
		// 그외엔 걍 성공하기.
		return true;
	}

	/**
	 * 클레스별 순수스탯에따른 마법데미지 +@ 리턴
	 */
	static private int toOriginalStatMagicDamage(Character cha){
		int sum = 0;
		int _int = cha.getTotalInt();
		switch(cha.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				_int -= 10;
				if(_int>=1)
					sum += 1;
				if(_int>=3)
					sum += 1;
				if(_int>=5)
					sum += 1;
				if(_int>=7)
					sum += 1;
				if(_int>=9)
					sum += 1;
				if(_int>=11)
					sum += 1;
				if(_int>=13)
					sum += 1;
				if(_int>=15)
					sum += 1;
				if(_int>=17)
					sum += 1;
				if(_int>=19)
					sum += 1;
				if(_int>=21)
					sum += 1;
				if(_int>=23)
					sum += 1;
				if(_int>=25)
					sum += 1;
				if(_int>=27)
					sum += 1;
				if(_int>=29)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				_int -= 8;
				if(_int>=1)
					sum += 1;
				if(_int>=3)
					sum += 1;
				if(_int>=5)
					sum += 1;
				if(_int>=7)
					sum += 1;
				if(_int>=9)
					sum += 1;
				if(_int>=11)
					sum += 1;
				if(_int>=13)
					sum += 1;
				if(_int>=15)
					sum += 1;
				if(_int>=17)
					sum += 1;
				if(_int>=19)
					sum += 1;
				if(_int>=21)
					sum += 1;
				if(_int>=23)
					sum += 1;
				if(_int>=25)
					sum += 1;
				if(_int>=27)
					sum += 1;
				if(_int>=29)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				_int -= 12;
				if(_int>=1)
					sum += 1;
				if(_int>=3)
					sum += 1;
				if(_int>=5)
					sum += 1;
				if(_int>=7)
					sum += 1;
				if(_int>=9)
					sum += 1;
				if(_int>=11)
					sum += 1;
				if(_int>=13)
					sum += 1;
				if(_int>=15)
					sum += 1;
				if(_int>=17)
					sum += 1;
				if(_int>=19)
					sum += 1;
				if(_int>=21)
					sum += 1;
				if(_int>=23)
					sum += 1;
				if(_int>=25)
					sum += 1;
				if(_int>=27)
					sum += 1;
				if(_int>=29)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				_int -= 12;
				if(_int>=1)
					sum += 2;
				if(_int>=3)
					sum += 2;
				if(_int>=5)
					sum += 2;
				if(_int>=7)
					sum += 2;
				if(_int>=9)
					sum += 2;
				if(_int>=11)
					sum += 2;
				if(_int>=13)
					sum += 2;
				if(_int>=15)
					sum += 2;
				if(_int>=17)
					sum += 2;
				if(_int>=19)
					sum += 2;
				if(_int>=21)
					sum += 2;
				if(_int>=23)
					sum += 2;
				if(_int>=25)
					sum += 2;
				if(_int>=27)
					sum += 2;
				if(_int>=29)
					sum += 2;
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				_int -= 11;
				if(_int>=1)
					sum += 1;
				if(_int>=3)
					sum += 1;
				if(_int>=5)
					sum += 1;
				if(_int>=7)
					sum += 1;
				if(_int>=9)
					sum += 1;
				if(_int>=11)
					sum += 1;
				if(_int>=13)
					sum += 1;
				if(_int>=15)
					sum += 1;
				if(_int>=17)
					sum += 1;
				if(_int>=19)
					sum += 1;
				if(_int>=21)
					sum += 1;
				if(_int>=23)
					sum += 1;
				if(_int>=25)
					sum += 1;
				if(_int>=27)
					sum += 1;
				if(_int>=29)
					sum += 1;
				break;
		}
		return sum;
	}
	
	/**
	 * int 수치에 따른 마법 추가 타격치
	 * @return
	 */
	static private int getIntDamage(Character cha){
		int dmg = 0;
		int adddmg = cha.getTotalInt() - 11;
		if(cha.getTotalInt() < 12){
			dmg = 0;
		}else{
			dmg = adddmg;
		}
		return dmg;
	}
	
	/**
	 * 스펠파워 리턴
	 */
	static public int getSp(Character cha){
		int sp = cha.getDynamicSp() + cha.getSetitemSp();
		
		switch(cha.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				if(cha.getLevel()<10)
					sp += 0;
				else if(cha.getLevel()<20)
					sp += 1;
				else
					sp += 2;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				if(cha.getLevel()<50)
					sp += 0;
				else
					sp += 1;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				if(cha.getLevel()<8)
					sp += 0;
				else if(cha.getLevel()<16)
					sp += 1;
				else if(cha.getLevel()<24)
					sp += 2;
				else if(cha.getLevel()<32)
					sp += 3;
				else if(cha.getLevel()<40)
					sp += 4;
				else if(cha.getLevel()<48)
					sp += 5;
				else
					sp += 6;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				if(cha.getLevel()<4)
					sp += 0;
				else if(cha.getLevel()<8)
					sp += 1;
				else if(cha.getLevel()<12)
					sp += 2;
				else if(cha.getLevel()<16)
					sp += 3;
				else if(cha.getLevel()<20)
					sp += 4;
				else if(cha.getLevel()<24)
					sp += 5;
				else if(cha.getLevel()<28)
					sp += 6;
				else if(cha.getLevel()<32)
					sp += 7;
				else if(cha.getLevel()<36)
					sp += 8;
				else if(cha.getLevel()<40)
					sp += 9;
				else
					sp += 10;
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				if(cha.getLevel()>=12) sp += 1;
				if(cha.getLevel()>=24) sp += 1;
				break;
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
				if(cha.getLevel()>=20) sp += 1;
				if(cha.getLevel()>=40) sp += 1;
				break;
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				if(cha.getLevel()>=6) sp += 1;
				if(cha.getLevel()>=12) sp += 1;
				if(cha.getLevel()>=18) sp += 1;
				if(cha.getLevel()>=24) sp += 1;
				if(cha.getLevel()>=30) sp += 1;
				if(cha.getLevel()>=36) sp += 1;
				if(cha.getLevel()>=42) sp += 1;
				if(cha.getLevel()>=48) sp += 1;
				break;
		}
		
		switch(cha.getTotalInt()){
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				sp += -1;
				break;
			case 9:
			case 10:
			case 11:
				sp += 0;
				break;
			case 12:
			case 13:
			case 14:
				sp += 1;
				break;
			case 15:
			case 16:
			case 17:
				sp += 2;
				break;
			case 18:
				sp += 3;
				break;
			default:
				sp += 3 + (cha.getTotalInt()-18);
				break;
		}
		return sp;
	}
	
	/**
	 * 마법방어력 리턴
	 * @param cha
	 * @param packet	: 패킷용과 일반 연산용 구분을 위해.
	 * 					: 패킷에선 클라자체 내부 공식과 리턴한 mr 이 함께 연산됨.
	 * @return
	 */
	static public int getMr(Character cha, boolean packet){
		// 기본 mr 추출.
		int mr = cha.getDynamicMr() + cha.getSetitemMr();
		// 패킷 처리용 이 아니라면 연산용 이므로 실제 mr값 추출.
		if(!packet){
			// 클레스별 보너스 mr 추출.
			switch(cha.getClassType()){
				case Lineage.LINEAGE_CLASS_ROYAL:
				case Lineage.LINEAGE_CLASS_DARKELF:
					mr += 10;
					break;
				case Lineage.LINEAGE_CLASS_KNIGHT:
					break;
				case Lineage.LINEAGE_CLASS_ELF:
					mr += 25;
					break;
				case Lineage.LINEAGE_CLASS_WIZARD:
					mr += 15;
					break;
			}
			// 레벨 보너스 mr
			mr += cha.getLevel() / 2;
			// wis 에 따른 추가
			if(cha.getTotalWis()>14){
				switch(cha.getTotalWis()){
					case 15:
					case 16:
						mr += 3;
						break;
					case 17:
						mr += 6;
						break;
					case 18:
						mr += 10;
						break;
					case 19:
						mr += 15;
						break;
					case 20:
						mr += 21;
						break;
					case 21:
						mr += 28;
						break;
					case 22:
						mr += 37;
						break;
					case 23:
						mr += 47;
						break;
					default:
						mr += 50;
						break;
				}
			}
			
			// 이레이즈매직
			if(cha.isBuffEraseMagic())
				mr /= 4;
		}
		
		// 버그 방지.
		if(mr < 0)
			mr = 0;
		
		// 패킷 용이라면 최대 mr값 확인.
		// 리턴.
		if(packet) {
			int max_packet_mr = 100;
			if(Lineage.max_mr < max_packet_mr)
				return mr>Lineage.max_mr ? Lineage.max_mr : mr;
			else
				return mr>max_packet_mr ? max_packet_mr : mr;
		} else {
			return mr>Lineage.max_mr ? Lineage.max_mr : mr;
		}
	}
	
	/**
	 * 스텟에 따른 MAGIC BONUS 값
	 * @return
	 */
	static private int getMagicBonus(Character cha){
		int i = cha.getTotalInt();
		if (i <= 5) 		return -1;
		else if (i <= 7) 	return 0;
		else if (i <= 9) 	return 1;
		else if (i <= 11) 	return 2;
		else if (i <= 13) 	return 3;
		else if (i <= 15) 	return 4;
		else if (i <= 17) 	return 5;
		else if (i <= 19) 	return 6;
		else if (i <= 21) 	return 7;
		else if (i <= 23) 	return 8;
		else if (i <= 25) 	return 9;
		else if (i <= 27) 	return 10;
		else if (i <= 29) 	return 12;
		else if (i <= 31) 	return 13;
		else if (i <= 33) 	return 14;
		else if (i <= 35) 	return 15;
		else if (i <= 37) 	return 16;
		else if (i <= 39) 	return 17;
		else 				return 18;
	}
	
	/**
	 * 클레스별 렙에 최대 마법레벨값 추출.
	 * @param cha
	 * @return
	 */
	static private int getMagicLevel(Character cha){
		if(cha instanceof PcInstance){
			switch(cha.getClassType()){
				case Lineage.LINEAGE_CLASS_ROYAL:
					return Math.min(2, cha.getLevel()/10);
				case Lineage.LINEAGE_CLASS_KNIGHT:
					return cha.getLevel()/50;
				case Lineage.LINEAGE_CLASS_ELF:
					return Math.min(6, cha.getLevel()/8);
				case Lineage.LINEAGE_CLASS_MONSTER:
				case Lineage.LINEAGE_CLASS_WIZARD:
					return Math.min(10, cha.getLevel()/4);
				case Lineage.LINEAGE_CLASS_DARKELF:
					return Math.min(2, cha.getLevel()/12);
				case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
					return Math.min(4, cha.getLevel()/9);
				case Lineage.LINEAGE_CLASS_BLACKWIZARD:
					return Math.min(10, cha.getLevel()/6);
			}
		}
		return cha.getLevel() / 4;
	}
	
	/**
	 * 클래스별 마법 성공률 기본값 리턴
	 * @return
	 */
	static private int toMagicFigure(Character cha){
		int sum = 0;
		switch(cha.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				sum += 2;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				sum += 5;
			case Lineage.LINEAGE_CLASS_DARKELF:
				break;
			case Lineage.LINEAGE_CLASS_MONSTER:
				sum += 10;
				break;
		}
		return sum;
	}
	
	/**
	 * 클레스별 순수스탯에 따른 마법성공율 +@ 리턴
	 * @return
	 */
	static private int toOriginalStatMagicHit(Character cha){
		int sum = 0;
		int wis = cha.getTotalInt();
		switch(cha.getClassType()){
			case Lineage.LINEAGE_CLASS_ROYAL:
				wis -= 10;
				if(wis>=2)
					sum += 1;
				if(wis>=4)
					sum += 1;
				if(wis>=6)
					sum += 1;
				if(wis>=8)
					sum += 1;
				if(wis>=10)
					sum += 1;
				if(wis>=12)
					sum += 1;
				if(wis>=14)
					sum += 1;
				if(wis>=16)
					sum += 1;
				if(wis>=18)
					sum += 1;
				if(wis>=20)
					sum += 1;
				if(wis>=22)
					sum += 1;
				if(wis>=24)
					sum += 1;
				if(wis>=26)
					sum += 1;
				if(wis>=28)
					sum += 1;
				if(wis>=30)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				wis -= 8;
				if(wis>=2)
					sum += 1;
				if(wis>=4)
					sum += 1;
				if(wis>=6)
					sum += 1;
				if(wis>=8)
					sum += 1;
				if(wis>=10)
					sum += 1;
				if(wis>=12)
					sum += 1;
				if(wis>=14)
					sum += 1;
				if(wis>=16)
					sum += 1;
				if(wis>=18)
					sum += 1;
				if(wis>=20)
					sum += 1;
				if(wis>=22)
					sum += 1;
				if(wis>=24)
					sum += 1;
				if(wis>=26)
					sum += 1;
				if(wis>=28)
					sum += 1;
				if(wis>=30)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				wis -= 12;
				if(wis>=1)
					sum += 1;
				if(wis>=3)
					sum += 1;
				if(wis>=5)
					sum += 1;
				if(wis>=7)
					sum += 1;
				if(wis>=9)
					sum += 1;
				if(wis>=11)
					sum += 1;
				if(wis>=13)
					sum += 1;
				if(wis>=15)
					sum += 1;
				if(wis>=17)
					sum += 1;
				if(wis>=19)
					sum += 1;
				if(wis>=21)
					sum += 1;
				if(wis>=23)
					sum += 1;
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				wis -= 10;
				if(wis>=2)
					sum += 2;
				if(wis>=4)
					sum += 2;
				if(wis>=6)
					sum += 2;
				if(wis>=8)
					sum += 2;
				if(wis>=10)
					sum += 2;
				if(wis>=12)
					sum += 2;
				if(wis>=14)
					sum += 2;
				if(wis>=16)
					sum += 2;
				if(wis>=18)
					sum += 2;
				if(wis>=20)
					sum += 2;
				if(wis>=22)
					sum += 2;
				if(wis>=24)
					sum += 2;
				if(wis>=26)
					sum += 2;
				if(wis>=28)
					sum += 2;
				if(wis>=30)
					sum += 2;
				break;
				
			case Lineage.LINEAGE_CLASS_DARKELF:
				wis -= 11;
				if(wis>=1)
					sum += 1;
				if(wis>=3)
					sum += 1;
				if(wis>=5)
					sum += 1;
				if(wis>=7)
					sum += 1;
				if(wis>=9)
					sum += 1;
				if(wis>=11)
					sum += 1;
				if(wis>=13)
					sum += 1;
				if(wis>=15)
					sum += 1;
				if(wis>=17)
					sum += 1;
				if(wis>=19)
					sum += 1;
				if(wis>=21)
					sum += 1;
				if(wis>=23)
					sum += 1;
				break;
		}
		return sum;
	}
	
}
