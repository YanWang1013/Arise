package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Item;
import lineage.plugin.Plugin;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.share.TimeLine;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemBookInstance;
import lineage.world.object.instance.ItemCrystalInstance;
import lineage.world.object.instance.ItemDarkSpiritCrystalInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.item.Arrow;
import lineage.world.object.item.BlessEva;
import lineage.world.object.item.Bundle;
import lineage.world.object.item.Candle;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.ElementalStone;
import lineage.world.object.item.ElvenWafer;
import lineage.world.object.item.Firework;
import lineage.world.object.item.InnRoomKey;
import lineage.world.object.item.Lamp;
import lineage.world.object.item.Lantern;
import lineage.world.object.item.LanternOil;
import lineage.world.object.item.Letter;
import lineage.world.object.item.MagicFlute;
import lineage.world.object.item.Meat;
import lineage.world.object.item.MiniMap;
import lineage.world.object.item.MonsterEyeMeat;
import lineage.world.object.item.PetWhistle;
import lineage.world.object.item.PledgeLetter;
import lineage.world.object.item.RedSock;
import lineage.world.object.item.SlimeRaceTicket;
import lineage.world.object.item.StomWalk;
import lineage.world.object.item.ThrowingKnife;
import lineage.world.object.item.Whetstone;
import lineage.world.object.item.armor.ArmorOfIllusion;
import lineage.world.object.item.armor.ChainMailMagicResistance;
import lineage.world.object.item.bow.BowOfIllusion;
import lineage.world.object.item.cloak.CloakInvisibility;
import lineage.world.object.item.cloak.CloakMagicResistance;
import lineage.world.object.item.cloak.ElvenCloak;
import lineage.world.object.item.cloak.HolyProtectiveCloak;
import lineage.world.object.item.etc.ChangeSexPotion;
import lineage.world.object.item.etc.SpellPotion;
import lineage.world.object.item.helm.HelmInfravision;
import lineage.world.object.item.helm.HelmMagicHealing;
import lineage.world.object.item.helm.HelmMagicPower;
import lineage.world.object.item.helm.HelmMagicSpeed;
import lineage.world.object.item.helm.HelmetMagicResistance;
import lineage.world.object.item.helm.HolyBlessOfElm;
import lineage.world.object.item.helm.HolyMagicDefenseHelm;
import lineage.world.object.item.potion.BlindingPotion;
import lineage.world.object.item.potion.BluePotion;
import lineage.world.object.item.potion.BraveryPotion;
import lineage.world.object.item.potion.CurePoisonPotion;
import lineage.world.object.item.potion.ElixirPotion;
import lineage.world.object.item.potion.ExpReStorePowerPotion;
import lineage.world.object.item.potion.ExpRisePotion;
import lineage.world.object.item.potion.GreaterHastePotion;
import lineage.world.object.item.potion.HastePotion;
import lineage.world.object.item.potion.HealingPotion;
import lineage.world.object.item.potion.ManaPotion;
import lineage.world.object.item.potion.WisdomPotion;
import lineage.world.object.item.quest.AriaReward;
import lineage.world.object.item.quest.ElvenTreasure;
import lineage.world.object.item.quest.SecretRoomKey;
import lineage.world.object.item.ring.RingTeleportControl;
import lineage.world.object.item.scroll.BlankScroll;
import lineage.world.object.item.scroll.LevelUpScroll;
import lineage.world.object.item.scroll.ScrollLabeledDaneFools;
import lineage.world.object.item.scroll.ScrollLabeledKernodwel;
import lineage.world.object.item.scroll.ScrollLabeledPratyavayah;
import lineage.world.object.item.scroll.ScrollLabeledVenzarBorgavve;
import lineage.world.object.item.scroll.ScrollLabeledVerrYedHorae;
import lineage.world.object.item.scroll.ScrollLabeledVerrYedHoraePledgeHouse;
import lineage.world.object.item.scroll.ScrollLabeledZelgoMer;
import lineage.world.object.item.scroll.ScrollLocationReset;
import lineage.world.object.item.scroll.ScrollOfEnchantArmorIllusion;
import lineage.world.object.item.scroll.ScrollPolymorph;
import lineage.world.object.item.scroll.ScrollResurrection;
import lineage.world.object.item.scroll.ScrollReturnHiddenValley;
import lineage.world.object.item.scroll.ScrollReturnSingingIsland;
import lineage.world.object.item.scroll.ScrollTeleport;
import lineage.world.object.item.scroll.ScrollofEnchantWeaponIllusion;
import lineage.world.object.item.scroll.SealedTOITeleportCharm;
import lineage.world.object.item.scroll.SpellScrollAbsoluteBarrier;
import lineage.world.object.item.scroll.SpellScrollBlessedArmor;
import lineage.world.object.item.scroll.SpellScrollCallLightning;
import lineage.world.object.item.scroll.SpellScrollChillTouch;
import lineage.world.object.item.scroll.SpellScrollConeOfCold;
import lineage.world.object.item.scroll.SpellScrollCounterMagic;
import lineage.world.object.item.scroll.SpellScrollCurePoison;
import lineage.world.object.item.scroll.SpellScrollCurseBlind;
import lineage.world.object.item.scroll.SpellScrollCurseParalyze;
import lineage.world.object.item.scroll.SpellScrollCursePoison;
import lineage.world.object.item.scroll.SpellScrollDarkness;
import lineage.world.object.item.scroll.SpellScrollDecreaseWeight;
import lineage.world.object.item.scroll.SpellScrollDestroy;
import lineage.world.object.item.scroll.SpellScrollDetection;
import lineage.world.object.item.scroll.SpellScrollEarthJail;
import lineage.world.object.item.scroll.SpellScrollEnchantWeapon;
import lineage.world.object.item.scroll.SpellScrollEnergyBolt;
import lineage.world.object.item.scroll.SpellScrollFireArrow;
import lineage.world.object.item.scroll.SpellScrollFireball;
import lineage.world.object.item.scroll.SpellScrollFrozenCloud;
import lineage.world.object.item.scroll.SpellScrollGreaterHeal;
import lineage.world.object.item.scroll.SpellScrollHeal;
import lineage.world.object.item.scroll.SpellScrollHolyWeapon;
import lineage.world.object.item.scroll.SpellScrollIceDagger;
import lineage.world.object.item.scroll.SpellScrollImmunetoHarm;
import lineage.world.object.item.scroll.SpellScrollLesserHeal;
import lineage.world.object.item.scroll.SpellScrollLight;
import lineage.world.object.item.scroll.SpellScrollLightning;
import lineage.world.object.item.scroll.SpellScrollManaDrain;
import lineage.world.object.item.scroll.SpellScrollMeditation;
import lineage.world.object.item.scroll.SpellScrollPhysicalEnchantDex;
import lineage.world.object.item.scroll.SpellScrollRemoveCurse;
import lineage.world.object.item.scroll.SpellScrollShield;
import lineage.world.object.item.scroll.SpellScrollSlow;
import lineage.world.object.item.scroll.SpellScrollStalac;
import lineage.world.object.item.scroll.SpellScrollTameMonster;
import lineage.world.object.item.scroll.SpellScrollTeleport;
import lineage.world.object.item.scroll.SpellScrollTurnUndead;
import lineage.world.object.item.scroll.SpellScrollVampiricTouch;
import lineage.world.object.item.scroll.SpellScrollWeaponBreak;
import lineage.world.object.item.scroll.SpellScrollWindShuriken;
import lineage.world.object.item.scroll.TOITeleportCharm;
import lineage.world.object.item.scroll.TOITeleportScroll;
import lineage.world.object.item.scroll.TalkingScroll;
import lineage.world.object.item.shield.ElvenShield;
import lineage.world.object.item.shield.ShieldoftheSilverKnight;
import lineage.world.object.item.sp.ScrollTOITeleport;
import lineage.world.object.item.sp.경험치아이템;
import lineage.world.object.item.sp.더블경험치아이템;
//import lineage.world.object.item.sp.마력증강아이템;
//import lineage.world.object.item.sp.버프물약;
//import lineage.world.object.item.sp.전투강화아이템;
//import lineage.world.object.item.sp.전투의식아이템;
//import lineage.world.object.item.sp.체력증강아이템;
import lineage.world.object.item.wand.EbonyWand;
import lineage.world.object.item.wand.ExpulsionWand;
import lineage.world.object.item.wand.MapleWand;
import lineage.world.object.item.wand.PineWand;
import lineage.world.object.item.wand.StormWalk;
import lineage.world.object.item.weapon.Claw;
import lineage.world.object.item.weapon.DiceDagger;
import lineage.world.object.item.weapon.Edoryu;
import lineage.world.object.item.weapon.SwordOfIllusion;

public final class ItemDatabase {

	static private List<Item> list;
	static private List<ItemInstance> pool;

	/**
	 * 초기화 함수
	 * @param con
	 */
	static public void init(Connection con){
		TimeLine.start("ItemDatabase..");

		pool = new ArrayList<ItemInstance>();
		list = new ArrayList<Item>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item");
			rs = st.executeQuery();
			while(rs.next()){
				Item i = new Item();
				i.setName( rs.getString("아이템이름") );
				i.setType1( rs.getString("구분1") );
				i.setType2( rs.getString("구분2") );
				i.setNameId( rs.getString("NAMEID") );
				i.setMaterial( getMaterial(rs.getString("재질")) );
				i.setDmgMin( rs.getInt("최소데미지") );
				i.setDmgMax( rs.getInt("최대데미지") );
				i.setWeight( rs.getFloat("무게") );
				i.setInvGfx( rs.getInt("인벤ID") );
				i.setGroundGfx( rs.getInt("GFXID") );
				i.setAction1( rs.getInt("ACTION1") );
				i.setAction2( rs.getInt("ACTION2") );
				i.setSell( rs.getString("판매").equalsIgnoreCase("true") );
				i.setPiles( rs.getString("겹침").equalsIgnoreCase("true") );
				i.setTrade( rs.getString("거래").equalsIgnoreCase("true") );
				i.setDrop( rs.getString("드랍").equalsIgnoreCase("true") );
				i.setWarehouse( rs.getString("창고").equalsIgnoreCase("true") );
				i.setEnchant( rs.getString("인첸트").equalsIgnoreCase("true") );
				i.setSafeEnchant( rs.getInt("안전인첸트") );
				i.setRoyal( rs.getInt("군주") );
				i.setKnight( rs.getInt("기사") );
				i.setElf( rs.getInt("요정") );
				i.setWizard( rs.getInt("마법사") );
				i.setDarkElf( rs.getInt("다크엘프") );
				i.setDragonKnight( rs.getInt("용기사") );
				i.setBlackWizard( rs.getInt("환술사") );
				i.setAddHit( rs.getInt("공격성공율") );
				i.setAddDmg( rs.getInt("추가타격치") );
				i.setAc( rs.getInt("ac") );
				i.setAddStr( rs.getInt("add_str") );
				i.setAddDex( rs.getInt("add_dex") );
				i.setAddCon( rs.getInt("add_con") );
				i.setAddInt( rs.getInt("add_int") );
				i.setAddWis( rs.getInt("add_wis") );
				i.setAddCha( rs.getInt("add_cha") );
				i.setAddHp( rs.getInt("HP증가") );
				i.setAddMp( rs.getInt("MP증가") );
				i.setAddSp( rs.getInt("SP증가") );
				i.setAddMr( rs.getInt("MR증가") );
				i.setCanbedmg( rs.getString("손상").equalsIgnoreCase("true") );
				i.setLevelMin( rs.getInt("level_min") );
				i.setLevelMax( rs.getInt("level_max") );
				i.setEffect( rs.getInt("이펙트ID") );
				i.setSetId( rs.getInt("셋트아이템ID") );
				i.setContinuous( rs.getInt("continuous") );
				i.setWaterress( rs.getInt("waterress") );
				i.setWindress( rs.getInt("windress") );
				i.setEarthress( rs.getInt("earthress") );
				i.setFireress( rs.getInt("fireress") );
				i.setAddWeight( rs.getFloat("add_weight") );
				i.setTicHp( rs.getInt("tic_hp") );
				i.setTicMp( rs.getInt("tic_mp") );
				i.setShopPrice( rs.getInt("shop_price") );
				i.setDropChance( rs.getInt("drop_chance") );
				i.setGfxMode( getWeaponGfx(i.getType2()) );
				i.setSlot( getSlot(i.getType2()) );
				i.setEquippedSlot( getEquippedSlot(i.getType2()) );
				i.setSolvent( rs.getInt("solvent") );
				i.setBookChaoticZone( rs.getString("book_chaotic_zone").equalsIgnoreCase("true") );
				i.setBookLawfulZone( rs.getString("book_lawful_zone").equalsIgnoreCase("true") );
				i.setBookMomtreeZone( rs.getString("book_momtree_zone").equalsIgnoreCase("true") );
				i.setBookNeutralZone( rs.getString("book_neutral_zone").equalsIgnoreCase("true") );
				i.setBookTowerZone( rs.getString("book_tower_zone").equalsIgnoreCase("true") );
				if(rs.getString("attribute_crystal").equalsIgnoreCase("earth"))
					i.setAttributeCrystal(Lineage.ELEMENT_EARTH);
				else if(rs.getString("attribute_crystal").equalsIgnoreCase("fire"))
					i.setAttributeCrystal(Lineage.ELEMENT_FIRE);
				else if(rs.getString("attribute_crystal").equalsIgnoreCase("wind"))
					i.setAttributeCrystal(Lineage.ELEMENT_WIND);
				else if(rs.getString("attribute_crystal").equalsIgnoreCase("water"))
					i.setAttributeCrystal(Lineage.ELEMENT_WATER);
				i.setPolyName( rs.getString("poly_name") );
				i.setInventorySave( rs.getString("is_inventory_save").equalsIgnoreCase("true") );
				i.setAqua( rs.getString("is_aqua").equalsIgnoreCase("true") );
				i.setStealHp( rs.getInt("steal_hp") );
				i.setStealMp( rs.getInt("steal_mp") );
				i.setTohand( rs.getString("is_tohand").equalsIgnoreCase("true") );

				try {
					StringBuffer sb = new StringBuffer();
					StringTokenizer stt = new StringTokenizer(i.getNameId(), " $ ");
					while(stt.hasMoreTokens())
						sb.append(stt.nextToken());
					i.setNameIdNumber( Integer.valueOf(sb.toString().trim()) );
				} catch (Exception e) { }

				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}



	static public Item find(String type1, String type2){
		for(Item i : list){
			if(i.getType1().equalsIgnoreCase(type1) && i.getType2().equalsIgnoreCase(type2))
				return i;
		}
		return null;
	}

	/**
	 * 이름으로 해당 객체 찾기 함수.
	 * @param name
	 * @return
	 */
	static public Item find(String name){
		for(Item i : list){
			if(i.getName().equalsIgnoreCase(name))
				return i;
		}
		return null;
	}

	/**
	 * 띄어쓰기 무시한 이름으로 해당 객체 name_id 찾기 함수.
	 * @param name
	 * @return
	 */
	static public int findItemByNameWithoutSpace(String name) {
		int name_id = 0;
		for (Item i : list) {
			if (i != null && i.getName().replace(" ", "").equals(name)) {
				name_id = i.getNameIdNumber();
				break;
			}
		}
		return name_id;
	}

	/**
	 * 띄어쓰기 무시한 이름으로 해당 객체 찾기 함수.
	 * @param name
	 * @return
	 */
	static public Item findItem(String name) {
		for (Item i : list) {
			if (i != null && i.getName().replace(" ", "").equals(name)) {
				return i;
			}
		}
		return null;
	}

	static public Item find(int name_id){
		for(Item i : list){
			if(i.getNameIdNumber() == name_id)
				return i;
		}
		return null;
	}

	/**
	 * 아이템 객체 생성해주는 함수
	 *  : 아이템 고유 이름번호를 이용해 구분해서 클레스 생성
	 *    풀에 등록되어있는 객체 가져와서 재사용.
	 * @param item
	 * @return
	 */
	static public ItemInstance newInstance(Item item){
		// 버그 방지.
		if(item==null)
			return null;

		// 플러그인 확인.
		Plugin p = PluginController.find(ItemDatabase.class);
		if(p != null){
			ItemInstance ii = ((lineage.plugin.bean.ItemDatabase)p).newInstance(item);
			if(ii != null)
				return ii;
		}

		// 생성 처리.
		switch(item.getNameIdNumber()){
		case 2:		// 등잔
			return Lamp.clone( getPool(Lamp.class) ).clone(item);
		case 23:	// 고기
		case 68:	// 당근
		case 72:	// 달걀
		case 82:	// 레몬
		case 85:	// 오렌지
		case 106:	// 사과
		case 107:	// 바나나
		case 1179:	// 사탕
			return Meat.clone( getPool(Meat.class) ).clone(item);
		case 27:	// 단풍나무 막대
		case 260:	// 변신 막대
			return MapleWand.clone( getPool(MapleWand.class) ).clone(item);
		case 28:	// 소나무 막대
		case 258:	// 괴물 생성 막대
			return PineWand.clone( getPool(PineWand.class) ).clone(item);
		case 30:	// 갑옷 마법 주문서 `젤고 머'
		case 249:		// 갑옷 마법 주문서
		case 228249:	// 저주받은 갑옷 마법 주문서
		case 227249:	// 축복받은 갑옷 마법 주문서
			return ScrollLabeledZelgoMer.clone( getPool(ScrollLabeledZelgoMer.class) ).clone(item);
		case 34:	// 저주 풀기 주문서 `프라탸바야'
		case 243:
		case 3299:	// 상아탑의 저주풀기 주문서
			return ScrollLabeledPratyavayah.clone( getPool(ScrollLabeledPratyavayah.class) ).clone(item);
		case 35:	// 무기 마법 주문서 `데이엔 푸엘스'
		case 244:		// 무기 마법 주문서
		case 228244:	// 저주받은 무기 마법 주문서
		case 227244:	// 축복받은 무기 마법 주문서
			return ScrollLabeledDaneFools.clone( getPool(ScrollLabeledDaneFools.class) ).clone(item);
		case 39:	// 귀환 주문서 `베르 예드 호레'
		case 505:
		case 3297:	// 상아탑의 귀환 주문서
			return ScrollLabeledVerrYedHorae.clone( getPool(ScrollLabeledVerrYedHorae.class) ).clone(item);
		case 40:	// 순간이동 주문서 `벤자르 보르가브'
		case 230:
		case 3296:
			return ScrollLabeledVenzarBorgavve.clone( getPool(ScrollLabeledVenzarBorgavve.class) ).clone(item);
		case 43:	// 확인 주문서 `케르노드 웰'
		case 55:
		case 3298:	// 상아탑의 확인 주문서
			return ScrollLabeledKernodwel.clone( getPool(ScrollLabeledKernodwel.class) ).clone(item);
		case 61:	// 화살
		case 93:	// 은 화살
		case 773:	// 미스릴 화살
		case 2377:	// 고대인의 화살
		case 77161: // 오리하르콘 화살
			return Arrow.clone( getPool(Arrow.class) ).clone(item);
		case 2463:	// 스팅
		case 2516:	// 실버 스팅
		case 2517:	// 헤비 스팅
			return ThrowingKnife.clone( getPool(ThrowingKnife.class) ).clone(item);
		case 67:	// 양초
			return Candle.clone( getPool(Candle.class) ).clone(item);
		case 180:	// 투명망토
			return CloakInvisibility.clone( getPool(CloakInvisibility.class) ).clone(item);
		case 26:	// 체력 회복제
		case 235:	// 주홍 물약
		case 237:	// 빨간 물약
		case 238:	// 맑은 물약
		case 255:	// 고급 체력 회복제
		case 328:	// 강력 체력 회복제
		case 794:	// 엔트의 열매
		case 1251:	// 농축 체력 회복제
		case 1252:	// 농축 고급 체력 회복제
		case 1253:	// 농축 강력 체력 회복제
		case 2575:	// 고대의 체력 회복제
		case 2576:	// 고대의 고급 체력 회복제
		case 2577:	// 고대의 강력 체력 회복제
		case 3301:	// 상아탑의 체력 회복제
			return HealingPotion.clone( getPool(HealingPotion.class) ).clone(item);
		case 110:	// 엘븐 와퍼
			return ElvenWafer.clone( getPool(ElvenWafer.class) ).clone(item);
		case 170:	// 요정족 망토
			return ElvenCloak.clone( getPool(ElvenCloak.class) ).clone(item);
		case 182:	// 마법 망토
			return CloakMagicResistance.clone( getPool(CloakMagicResistance.class) ).clone(item);
		case 187:	// 요정족 방패
			return ElvenShield.clone( getPool(ElvenShield.class) ).clone(item);
		case 232:	// 파란물약
		case 507:	// 마력 회복 물약
			return BluePotion.clone( getPool(BluePotion.class) ).clone(item);
		case 233:	// 비취물약
		case 763:	// 엔트의 줄기.
			return CurePoisonPotion.clone( getPool(CurePoisonPotion.class) ).clone(item);
		case 234:		// 초록물약
		case 264:		// 속도향상물약
		case 3302:		// 상아탑의 속도향상 물약
		case 3405:		// 와인
		case 3406:		// 위스키
			return HastePotion.clone( getPool(HastePotion.class) ).clone(item);
		case 239:	// 불투명 물약
			return BlindingPotion.clone( getPool(BlindingPotion.class) ).clone(item);
		case 241:	// 순간이동 조종 반지
			return RingTeleportControl.clone( getPool(RingTeleportControl.class) ).clone(item);
		case 49:	// 벨록스 넵
		case 257:	// 부활 주문서
			return ScrollResurrection.clone( getPool(ScrollResurrection.class) ).clone(item);
		case 263:	// 흑단 막대
			return EbonyWand.clone( getPool(EbonyWand.class) ).clone(item);
		case 326:	// 랜턴
			return Lantern.clone( getPool(Lantern.class) ).clone(item);
		case 327:	// 랜턴용 기름
			return LanternOil.clone( getPool(LanternOil.class) ).clone(item);
		case 343:	// 슬라임 레이스표
			return SlimeRaceTicket.clone( getPool(SlimeRaceTicket.class) ).clone(item);
		case 416:	// 악운의 단검
			return DiceDagger.clone( getPool(DiceDagger.class) ).clone(item);
		case 516:	// 마법 방어 사슬 갑옷
			return ChainMailMagicResistance.clone( getPool(ChainMailMagicResistance.class) ).clone(item);
		case 569:	// 마법 방어 투구
			return HelmetMagicResistance.clone( getPool(HelmetMagicResistance.class) ).clone(item);
		case 617:	// 빨간 양말
			return RedSock.clone( getPool(RedSock.class) ).clone(item);
		case 623:	// 괴물 눈 고기
			return MonsterEyeMeat.clone( getPool(MonsterEyeMeat.class) ).clone(item);
		case 762:	// 정령의 돌
			return ElementalStone.clone( getPool(ElementalStone.class) ).clone(item);
		case 777:	// 마법의 플룻
			return MagicFlute.clone( getPool(MagicFlute.class) ).clone(item);
		case 922:	// 은기사의 방패
			return ShieldoftheSilverKnight.clone( getPool(ShieldoftheSilverKnight.class) ).clone(item);
		case 954:	// 여관 열쇠
			return InnRoomKey.clone( getPool(InnRoomKey.class) ).clone(item);
			
		case 10944:	// 신성한 마법 방어 투구
			return HolyMagicDefenseHelm.clone( getPool(HolyMagicDefenseHelm.class)).clone(item);
		case 10945:	// 신성한 엘름의 축복
			return HolyBlessOfElm.clone( getPool(HolyBlessOfElm.class)).clone(item);
		case 10947:	// 신성한 보호 망토
			return HolyProtectiveCloak.clone( getPool(HolyProtectiveCloak.class)).clone(item);	
			
		case 938:	// 마법의 투구: 치유
			return HelmMagicHealing.clone( getPool(HelmMagicHealing.class) ).clone(item);
		case 939:	// 마법의 투구: 신속
			return HelmMagicSpeed.clone( getPool(HelmMagicSpeed.class) ).clone(item);
		case 940:	// 마법의 투구: 힘
			return HelmMagicPower.clone( getPool(HelmMagicPower.class) ).clone(item);
		case 1008:	// 인프라비젼 투구
			return HelmInfravision.clone( getPool(HelmInfravision.class) ).clone(item);
		case 943:	// 용기의 물약
		case 3372:	// 악마의 피
			return BraveryPotion.clone( getPool(BraveryPotion.class) ).clone(item);
		case 944:	// 지혜의 물약
			return WisdomPotion.clone( getPool(WisdomPotion.class) ).clone(item);
		case 971:	// 변신 주문서
		case 3300:	// 상아탑의 변신 주문서
			return ScrollPolymorph.clone( getPool(ScrollPolymorph.class) ).clone(item);
		case 975:	// 추방 막대
			return ExpulsionWand.clone( getPool(ExpulsionWand.class) ).clone(item);
		case 1086:	// 펫 호루라기
			return PetWhistle.clone( getPool(PetWhistle.class) ).clone(item);
		case 1100:	// 숫돌
			return Whetstone.clone( getPool(Whetstone.class) ).clone(item);
		case 1486:	// 빈 주문서 (레벨 1)
		case 1892:	// 2
		case 1893:	// 3
		case 1894:	// 4
		case 1895:	// 5
			return BlankScroll.clone( getPool(BlankScroll.class) ).clone(item);
		case 1507:	// 에바의 물약
		case 1508:	// 인어의 비늘
			return BlessEva.clone( getPool(BlessEva.class) ).clone(item);
		case 1652234:	// 강화 초록 물약
		case 1652264:	// 강화 속도향상 물약
			return GreaterHastePotion.clone( getPool(GreaterHastePotion.class) ).clone(item);
		case 517:	// 마법서 (힐)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 1, 0 ).clone(item);
		case 518:	// 마법서 (라이트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 1, 1 ).clone(item);
		case 519:	// 마법서 (실드)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 1, 2 ).clone(item);
		case 520:	// 마법서 (에너지 볼트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 1, 3 ).clone(item);
		case 521:	// 마법서 (텔리포트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 1, 4 ).clone(item);
		case 522:	// 마법서 (큐어 포이즌)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 2, 0 ).clone(item);
		case 523:	// 마법서 (칠 터치)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 2, 1 ).clone(item);
		case 524:	// 마법서 (커스: 포이즌)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 2, 2 ).clone(item);
		case 525:	// 마법서 (인챈트 웨폰)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 2, 3 ).clone(item);
		case 526:	// 마법서 (디텍션)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 2, 4 ).clone(item);
		case 527:	// 마법서 (라이트닝)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 3, 0 ).clone(item);
		case 528:	// 마법서 (턴 언데드)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 3, 1 ).clone(item);
		case 529:	// 마법서 (익스트라 힐)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 3, 2 ).clone(item);
		case 530:	// 마법서 (커스: 블라인드)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 3, 3 ).clone(item);
		case 531:	// 마법서 (블레스드 아머)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 3, 4 ).clone(item);
		case 532:	// 마법서 (파이어볼)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 4, 0 ).clone(item);
		case 533:	// 마법서 (피지컬 인챈트: DEX)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 4, 1 ).clone(item);
		case 534:	// 마법서 (웨폰 브레이크)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 4, 2 ).clone(item);
		case 535:	// 마법서 (뱀파이어릭 터치)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 4, 3 ).clone(item);
		case 536:	// 마법서 (슬로우)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 4, 4 ).clone(item);
		case 537:	// 마법서 (커스: 패럴라이즈)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 5, 0 ).clone(item);
		case 538:	// 마법서 (콜 라이트닝)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 5, 1 ).clone(item);
		case 539:	// 마법서 (그레이터 힐)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 5, 2 ).clone(item);
		case 540:	// 마법서 (테이밍 몬스터)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 5, 3 ).clone(item);
		case 541:	// 마법서 (리무브 커스)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 5, 4 ).clone(item);
		case 542:	// 마법서 (크리에이트 좀비)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 6, 0 ).clone(item);
		case 543:	// 마법서 (피지컬 인챈트: STR)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 6, 1 ).clone(item);
		case 544:	// 마법서 (헤이스트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 6, 2 ).clone(item);
		case 545:	// 마법서 (캔슬레이션)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 6, 3 ).clone(item);
		case 546:	// 마법서 (이럽션)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 6, 4 ).clone(item);
		case 547:	// 마법서 (힐 올)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 7, 0 ).clone(item);
		case 548:	// 마법서 (아이스 랜스)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 7, 1 ).clone(item);
		case 549:	// 마법서 (서먼 몬스터)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 7, 2 ).clone(item);
		case 550:	// 마법서 (홀리 서클)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 7, 3 ).clone(item);
		case 551:	// 마법서 (토네이도)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 7, 4 ).clone(item);
		case 552:	// 마법서 (풀 힐)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 8, 0 ).clone(item);
		case 553:	// 마법서 (파이어월)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 8, 1 ).clone(item);
		case 554:	// 마법서 (블리자드)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 8, 2 ).clone(item);
		case 555:	// 마법서 (인비지블리티)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 8, 3 ).clone(item);
		case 556:	// 마법서 (리절렉션)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 8, 4 ).clone(item);
		case 557:	// 마법서 (라이트닝 스톰)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 9, 0 ).clone(item);
		case 558:	// 마법서 (포그 오브 슬리핑)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 9, 1 ).clone(item);
		case 559:	// 마법서 (셰이프 체인지)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 9, 2 ).clone(item);
		case 560:	// 마법서 (이뮨 투 함)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 9, 3 ).clone(item);
		case 561:	// 마법서 (매스 텔리포트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 9, 4 ).clone(item);
		case 562:	// 마법서 (크리에이트 매지컬 웨폰)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 10, 0 ).clone(item);
		case 563:	// 마법서 (미티어 스트라이크)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 10, 1 ).clone(item);
		case 564:	// 마법서 (리플렉팅 풀)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 10, 2 ).clone(item);
		case 565:	// 마법서 (스톱)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 10, 3 ).clone(item);
		case 566:	// 마법서 (디스인티그레이트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 10, 4 ).clone(item);
		case 1581:	// 마법서 (아이스 대거)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 1, 5 ).clone(item);
		case 1582:	// 마법서 (윈드 커터)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 1, 6 ).clone(item);
		case 1583:	// 마법서 (파이어 애로우)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 2, 6 ).clone(item);
		case 1584:	// 마법서 (스탈락)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 2, 7 ).clone(item);
		case 1585:	// 마법서 (프로즌 클라우드)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 3, 5 ).clone(item);
		case 1586:	// 마법서 (어스 재일)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 4, 5 ).clone(item);
		case 1587:	// 마법서 (콘 오브 콜드)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 5, 5 ).clone(item);
		case 1588:	// 마법서 (선 버스트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 6, 5 ).clone(item);
		case 1589:	// 마법서 (어스 퀘이크)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 8, 5 ).clone(item);
		case 1590:	// 마법서 (파이어 스톰)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 9, 5 ).clone(item);
		case 1651:	// 마법서 (그레이터 헤이스트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 7, 5 ).clone(item);
		case 1857:	// 마법서 (홀리 웨폰)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 1, 7 ).clone(item);
		case 1858:	// 마법서 (디크리즈 웨이트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 2, 5 ).clone(item);
		case 1859:	// 마법서 (위크 엘리멘트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 3, 6 ).clone(item);
		case 1860:	// 마법서 (카운터 매직)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 4, 6 ).clone(item);
		case 1861:	// 마법서 (메디테이션)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 4, 7 ).clone(item);
		case 1862:	// 마법서 (마나 드레인)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 5, 6 ).clone(item);
		case 1863:	// 마법서 (다크니스)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 5, 7 ).clone(item);
		case 1864:	// 마법서 (위크니스)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 6, 6 ).clone(item);
		case 1865:	// 마법서 (블레스 웨폰)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 6, 7 ).clone(item);
		case 1866:	// 마법서 (매지컬 마인)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 7, 6 ).clone(item);
		case 1867:	// 마법서 (디지즈)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 7, 7 ).clone(item);
		case 1868:	// 마법서 (라이프 스트림)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 8, 6 ).clone(item);
		case 1869:	// 마법서 (사일런스)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 8, 7 ).clone(item);
		case 1870:	// 마법서 (디케이 포션)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 9, 6 ).clone(item);
		case 1871:	// 마법서 (이너 사이트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 9, 7 ).clone(item);
		case 1872:	// 마법서 (앱솔루트 배리어)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 10, 5 ).clone(item);
		case 1873:	// 마법서 (채인지 포지션)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 10, 6 ).clone(item);
		case 1874:	// 마법서 (어드밴스드 매스 텔레포트)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 10, 7 ).clone(item);
		case 1959:	// 마법서 (트루 타겟)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 15, 0 ).clone(item);
		case 1960:	// 마법서 (글로잉 오라)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 15, 1 ).clone(item);
		case 2089:	// 마법서 (콜 클렌)
			return ItemBookInstance.clone( getPool(ItemBookInstance.class), 15, 3 ).clone(item);
		case 1829:	// 정령의 수정 (레지스트 매직)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 17, 0 ).clone(item);
		case 1830:	// 정령의 수정 (바디 투 마인드)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 17, 1 ).clone(item);
		case 1831:	// 정령의 수정 (텔레포트 투 마더)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 17, 2 ).clone(item);
		case 1832:	// 정령의 수정 (클리어 마인드)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 18, 0 ).clone(item);
		case 1833:	// 정령의 수정 (레지스트 엘리멘트)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 18, 1 ).clone(item);
		case 1834:	// 정령의 수정 (리턴 투 네이쳐)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 19, 0 ).clone(item);
		case 1835:	// 정령의 수정 (블러드 투 소울)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 19, 1 ).clone(item);
		case 1836:	// 정령의 수정 (프로텍션 프롬 엘리멘트)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 19, 2 ).clone(item);
		case 1837:	// 정령의 수정 (파이어 웨폰)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 19, 3 ).clone(item);
		case 1838:	// 정령의 수정 (윈드 샷)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 19, 4 ).clone(item);
		case 1839:	// 정령의 수정 (윈드 워크)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 19, 5 ).clone(item);
		case 1840:	// 정령의 수정 (어스 스킨)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 19, 6 ).clone(item);
		case 1841:	// 정령의 수정 (인탱글)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 19, 7 ).clone(item);
		case 1842:	// 정령의 수정 (이레이즈 매직)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 20, 0 ).clone(item);
		case 1843:	// 정령의 수정 (서먼 레서 엘리멘탈)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 20, 1 ).clone(item);
		case 1844:	// 정령의 수정 (브레스 오브 파이어)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 20, 2 ).clone(item);
		case 1845:	// 정령의 수정 (아이 오브 스톰)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 20, 3 ).clone(item);
		case 1846:	// 정령의 수정 (어스 바인드)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 20, 4 ).clone(item);
		case 1847:	// 정령의 수정 (네이쳐스 터치)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 20, 5 ).clone(item);
		case 1848:	// 정령의 수정 (블레스 오브 어스)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 20, 6 ).clone(item);
		case 1849:	// 정령의 수정 (에어리어 오브 사일런스)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 21, 0 ).clone(item);
		case 1850:	// 정령의 수정 (서먼 그레이터 엘리멘탈)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 21, 1 ).clone(item);
		case 1851:	// 정령의 수정 (버닝 웨폰)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 21, 2 ).clone(item);
		case 1852:	// 정령의 수정 (네이쳐스 블레싱)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 21, 3 ).clone(item);
		case 1853:	// 정령의 수정 (콜 오브 네이쳐)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 21, 4 ).clone(item);
		case 1854:	// 정령의 수정 (스톰 샷)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 21, 5 ).clone(item);
		case 1855:	// 정령의 수정 (스톰 워크)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 21, 6 ).clone(item);
		case 1856:	// 정령의 수정 (아이언 스킨)
			return ItemCrystalInstance.clone( getPool(ItemCrystalInstance.class), 21,7 ).clone(item);
		case 1173:	// 개목걸이
			return DogCollar.clone( getPool(DogCollar.class) ).clone(item);
		case 1101:	// 지도 본토
			return MiniMap.clone( getPool(MiniMap.class), 16 ).clone(item);
		case 1102:	// 지도 말섬
			return MiniMap.clone( getPool(MiniMap.class), 1 ).clone(item);
		case 1103:	// 지도 글루딘
			return MiniMap.clone( getPool(MiniMap.class), 2 ).clone(item);
		case 1104:	// 지도 켄트
			return MiniMap.clone( getPool(MiniMap.class), 3 ).clone(item);
		case 1105:	// 지도 화민촌
			return MiniMap.clone( getPool(MiniMap.class), 4 ).clone(item);
		case 1106:	// 지도 요숲
			return MiniMap.clone( getPool(MiniMap.class), 5 ).clone(item);
		case 1107:	// 지도 우드벡
			return MiniMap.clone( getPool(MiniMap.class), 6 ).clone(item);
		case 1108:	// 지도 은기사
			return MiniMap.clone( getPool(MiniMap.class), 7 ).clone(item);
		case 1109:	// 지도 용계
			return MiniMap.clone( getPool(MiniMap.class), 8 ).clone(item);
		case 1188:	// 지도 기란
			return MiniMap.clone( getPool(MiniMap.class), 9 ).clone(item);
		case 1533:	// 지도 노섬
			return MiniMap.clone( getPool(MiniMap.class), 10 ).clone(item);
		case 1534:	// 지도 숨계
			return MiniMap.clone( getPool(MiniMap.class), 11 ).clone(item);
		case 1535:	// 지도 하이네
			return MiniMap.clone( getPool(MiniMap.class), 12 ).clone(item);
		case 1607:	// 지도 웰던
			return MiniMap.clone( getPool(MiniMap.class), 13 ).clone(item);
		case 1889:	// 지도 오렌
			return MiniMap.clone( getPool(MiniMap.class), 14 ).clone(item);
		case 1411:	// 노래하는 섬 귀환 주문서
			return ScrollReturnSingingIsland.clone( getPool(ScrollReturnSingingIsland.class) ).clone(item);
		case 1424:	// 숨겨진 계곡 귀환 주문서
			return ScrollReturnHiddenValley.clone( getPool(ScrollReturnHiddenValley.class) ).clone(item);
		case 2169:	// 오만의 탑 11층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 11 ).clone(item);
		case 2168:	// 오만의 탑 21층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 21 ).clone(item);
		case 2404:	// 오만의 탑 31층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 31 ).clone(item);
		case 2405:	// 오만의 탑 41층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 41 ).clone(item);
		case 2673:	// 오만의 탑 51층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 51 ).clone(item);
		case 2674:	// 오만의 탑 61층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 61 ).clone(item);
		case 2675:	// 오만의 탑 71층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 71 ).clone(item);
		case 2676:	// 오만의 탑 81층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 81 ).clone(item);
		case 2677:	// 오만의 탑 91층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 91 ).clone(item);
		case 2862:	// 오만의 탑 100층 이동 주문서
			return TOITeleportScroll.clone( getPool(TOITeleportScroll.class), 100 ).clone(item);
		case 2203:	// 지도 아덴
			return MiniMap.clone( getPool(MiniMap.class), 15 ).clone(item);
		case 2543:	// 지도 침동
			return MiniMap.clone( getPool(MiniMap.class), 17 ).clone(item);
		case 2582:	// 봉인된 오만의 탑 11층 이동 부적
			return SealedTOITeleportCharm.clone( getPool(SealedTOITeleportCharm.class), 11 ).clone(item);
		case 2583:	// 봉인된 오만의 탑 21층 이동 부적
			return SealedTOITeleportCharm.clone( getPool(SealedTOITeleportCharm.class), 21 ).clone(item);
		case 2584:	// 봉인된 오만의 탑 31층 이동 부적
			return SealedTOITeleportCharm.clone( getPool(SealedTOITeleportCharm.class), 31 ).clone(item);
		case 2585:	// 봉인된 오만의 탑 41층 이동 부적
			return SealedTOITeleportCharm.clone( getPool(SealedTOITeleportCharm.class), 41 ).clone(item);
		case 2668:	// 봉인된 오만의 탑 51층 이동 부적
			return SealedTOITeleportCharm.clone( getPool(SealedTOITeleportCharm.class), 51 ).clone(item);
		case 2669:	// 봉인된 오만의 탑 61층 이동 부적
			return SealedTOITeleportCharm.clone( getPool(SealedTOITeleportCharm.class), 61 ).clone(item);
		case 2670:	// 봉인된 오만의 탑 71층 이동 부적
			return SealedTOITeleportCharm.clone( getPool(SealedTOITeleportCharm.class), 71 ).clone(item);
		case 2671:	// 봉인된 오만의 탑 81층 이동 부적
			return SealedTOITeleportCharm.clone( getPool(SealedTOITeleportCharm.class), 81 ).clone(item);
		case 2672:	// 봉인된 오만의 탑 91층 이동 부적
			return SealedTOITeleportCharm.clone( getPool(SealedTOITeleportCharm.class), 91 ).clone(item);
		case 2400:	// 오만의 탑 11층 이동 부적
			return TOITeleportCharm.clone( getPool(TOITeleportCharm.class), 11 ).clone(item);
		case 2401:	// 오만의 탑 21층 이동 부적
			return TOITeleportCharm.clone( getPool(TOITeleportCharm.class), 21 ).clone(item);
		case 2402:	// 오만의 탑 31층 이동 부적
			return TOITeleportCharm.clone( getPool(TOITeleportCharm.class), 31 ).clone(item);
		case 2403:	// 오만의 탑 41층 이동 부적
			return TOITeleportCharm.clone( getPool(TOITeleportCharm.class), 41 ).clone(item);
		case 2678:	// 오만의 탑 51층 이동 부적
			return TOITeleportCharm.clone( getPool(TOITeleportCharm.class), 51 ).clone(item);
		case 2679:	// 오만의 탑 61층 이동 부적
			return TOITeleportCharm.clone( getPool(TOITeleportCharm.class), 61 ).clone(item);
		case 2680:	// 오만의 탑 71층 이동 부적
			return TOITeleportCharm.clone( getPool(TOITeleportCharm.class), 71 ).clone(item);
		case 2681:	// 오만의 탑 81층 이동 부적
			return TOITeleportCharm.clone( getPool(TOITeleportCharm.class), 81 ).clone(item);
		case 2682:	// 오만의 탑 91층 이동 부적
			return TOITeleportCharm.clone( getPool(TOITeleportCharm.class), 91 ).clone(item);
		case 3616:	// 지도 해적섬
			return MiniMap.clone( getPool(MiniMap.class), 18 ).clone(item);
		case 1075:	// 편지지
		case 1606:	// 크리스마스 카드
			return Letter.clone( getPool(Letter.class) ).clone(item);
		case 1146:	// 혈맹 편지지
			return PledgeLetter.clone( getPool(PledgeLetter.class) ).clone(item);
		case 2022:	// 비밀방 열쇠
			return SecretRoomKey.clone( getPool(SecretRoomKey.class) ).clone(item);
		case 2090:	// 아리아의 보답
			return AriaReward.clone( getPool(AriaReward.class) ).clone(item);
		case 2091:	// 요정족 보물
			return ElvenTreasure.clone( getPool(ElvenTreasure.class) ).clone(item);
		case 2380:	// 혈맹 귀환 주문서
			return ScrollLabeledVerrYedHoraePledgeHouse.clone( getPool(ScrollLabeledVerrYedHoraePledgeHouse.class) ).clone(item);
		case 1997:	// 환상의 검
			return SwordOfIllusion.clone( getPool(SwordOfIllusion.class) ).clone(item);
		case 1998:	// 환상의 갑옷
			return ArmorOfIllusion.clone( getPool(ArmorOfIllusion.class) ).clone(item);
		case 1999:	// 환상의 활
			return BowOfIllusion.clone( getPool(BowOfIllusion.class) ).clone(item);
		case 2000:	// 환상의 무기 마법 주문서
			return ScrollofEnchantWeaponIllusion.clone( getPool(ScrollofEnchantWeaponIllusion.class) ).clone(item);
		case 2001:	// 환상의 갑옷 마법 주문서
			return ScrollOfEnchantArmorIllusion.clone( getPool(ScrollOfEnchantArmorIllusion.class) ).clone(item);
		case 14871436:	// 마법주문서 (힐)
			return SpellScrollLesserHeal.clone( getPool(SpellScrollLesserHeal.class) ).clone(item);
		case 14871437:	// 마법주문서 (라이트)
			return SpellScrollLight.clone( getPool(SpellScrollLight.class) ).clone(item);
		case 14871438:	// 마법주문서 (실드)
			return SpellScrollShield.clone( getPool(SpellScrollShield.class) ).clone(item);
		case 14871439:	// 마법주문서 (에너지 볼트)
			return SpellScrollEnergyBolt.clone( getPool(SpellScrollEnergyBolt.class) ).clone(item);
		case 14871966:	// 마법주문서 (아이스 대거)
			return SpellScrollIceDagger.clone( getPool(SpellScrollIceDagger.class) ).clone(item);
		case 14871967:	// 마법주문서 (윈드커터)
			return SpellScrollWindShuriken.clone( getPool(SpellScrollWindShuriken.class) ).clone(item);
		case 14871440:	// 마법주문서 (텔레포트)
			return SpellScrollTeleport.clone( getPool(SpellScrollTeleport.class) ).clone(item);
		case 14871977:	// 마법주문서 (홀리웨폰)
			return SpellScrollHolyWeapon.clone( getPool(SpellScrollHolyWeapon.class) ).clone(item);
		case 14871441:	// 마법주문서 (큐어포이즌)
			return SpellScrollCurePoison.clone( getPool(SpellScrollCurePoison.class) ).clone(item);
		case 14871442:	// 마법주문서 (칠터치)
			return SpellScrollChillTouch.clone( getPool(SpellScrollChillTouch.class) ).clone(item);
		case 14871443:	// 마법주문서 (커스: 포이즌)
			return SpellScrollCursePoison.clone( getPool(SpellScrollCursePoison.class) ).clone(item);
		case 14871444:	// 마법주문서 (인첸트 웨폰)
			return SpellScrollEnchantWeapon.clone( getPool(SpellScrollEnchantWeapon.class) ).clone(item);
		case 14871445:	// 마법주문서 (디텍션)
			return SpellScrollDetection.clone( getPool(SpellScrollDetection.class) ).clone(item);
		case 14871978:	// 마법주문서 (디크리즈 웨이트)
			return SpellScrollDecreaseWeight.clone( getPool(SpellScrollDecreaseWeight.class) ).clone(item);
		case 14871968:	// 마법주문서 (파이어 애로우)
			return SpellScrollFireArrow.clone( getPool(SpellScrollFireArrow.class) ).clone(item);
		case 14871969:	// 마법주문서 (스탈락)
			return SpellScrollStalac.clone( getPool(SpellScrollStalac.class) ).clone(item);
		case 14871446:	// 마법주문서 (라이트닝)
			return SpellScrollLightning.clone( getPool(SpellScrollLightning.class) ).clone(item);
		case 14871447:	// 마법주문서 (턴 언데드)
			return SpellScrollTurnUndead.clone( getPool(SpellScrollTurnUndead.class) ).clone(item);
		case 14871448:	// 마법주문서 (익스트라 힐)
			return SpellScrollHeal.clone( getPool(SpellScrollHeal.class) ).clone(item);
		case 14871449:	// 마법주문서 (커스: 블라인드)
			return SpellScrollCurseBlind.clone( getPool(SpellScrollCurseBlind.class) ).clone(item);
		case 14871450:	// 마법주문서 (블레스드 아머)
			return SpellScrollBlessedArmor.clone( getPool(SpellScrollBlessedArmor.class) ).clone(item);
		case 14871451:	// 마법주문서 (파이어볼)
			return SpellScrollFireball.clone( getPool(SpellScrollFireball.class) ).clone(item);
		case 14871452:	// 마법주문서 (피치컬 인챈트 DEX)
			return SpellScrollPhysicalEnchantDex.clone( getPool(SpellScrollPhysicalEnchantDex.class) ).clone(item);
		case 14871453:	// 마법주문서 (웨폰 브레이크)
			return SpellScrollWeaponBreak.clone( getPool(SpellScrollWeaponBreak.class) ).clone(item);
		case 14871454:	// 마법주문서 (뱀파이어릭 터치)
			return SpellScrollVampiricTouch.clone( getPool(SpellScrollVampiricTouch.class) ).clone(item);
		case 14871455:	// 마법주문서 (슬로우)
			return SpellScrollSlow.clone( getPool(SpellScrollSlow.class) ).clone(item);
		case 14871971:	// 마법주문서 (어스 재일)
			return SpellScrollEarthJail.clone( getPool(SpellScrollEarthJail.class) ).clone(item);
		case 14871980:	// 마법주문서 (카운터 매직)
			return SpellScrollCounterMagic.clone( getPool(SpellScrollCounterMagic.class) ).clone(item);
		case 14871981:	// 마법주문서 (메디테이션)
			return SpellScrollMeditation.clone( getPool(SpellScrollMeditation.class) ).clone(item);
		case 14871485:	// 마법주문서 (디스인그레이트)
			return SpellScrollDestroy.clone( getPool(SpellScrollDestroy.class) ).clone(item);
		case 14871479:	// 마법주문서 (이뮨 투 함)
			return SpellScrollImmunetoHarm.clone( getPool(SpellScrollImmunetoHarm.class) ).clone(item);
		case 14871992:	// 마법주문서 (앱솔루트 베리어)
			return SpellScrollAbsoluteBarrier.clone( getPool(SpellScrollAbsoluteBarrier.class) ).clone(item);
		case 14871970:	// 마법주문서 (프로즌클라우드)
			return SpellScrollFrozenCloud.clone( getPool(SpellScrollFrozenCloud.class) ).clone(item);
		case 14871456:	// 마법주문서 (커스: 패럴라이즈)
			return SpellScrollCurseParalyze.clone( getPool(SpellScrollCurseParalyze.class) ).clone(item);
		case 14871457:	// 마법주문서 (콜 라이트닝)
			return SpellScrollCallLightning.clone( getPool(SpellScrollCallLightning.class) ).clone(item);
		case 14871458:	// 마법주문서 (그레이터 힐)
			return SpellScrollGreaterHeal.clone( getPool(SpellScrollGreaterHeal.class) ).clone(item);
		case 14871459:	// 마법주문서 (테이밍 몬스터)
			return SpellScrollTameMonster.clone( getPool(SpellScrollTameMonster.class) ).clone(item);
		case 14871460:	// 마법주문서 (리무브 커스)
			return SpellScrollRemoveCurse.clone( getPool(SpellScrollRemoveCurse.class) ).clone(item);
		case 14871972:	// 마법주문서 (콘 오브 콜드)
			return SpellScrollConeOfCold.clone( getPool(SpellScrollConeOfCold.class) ).clone(item);
		case 14871982:	// 마법주문서 (마나 드레인)
			return SpellScrollManaDrain.clone( getPool(SpellScrollManaDrain.class) ).clone(item);
		case 14871983:	// 마법주문서 (다크니스)
			return SpellScrollDarkness.clone( getPool(SpellScrollDarkness.class) ).clone(item);
		case 3278:	// 말하는 두루마리
			return TalkingScroll.clone( getPool(TalkingScroll.class) ).clone(item);
		case 2530:	// 엘릭서-STR
		case 2532:	// dex
		case 2531:	// con
		case 2534:	// wis
		case 2533:	// int
		case 2535:	// cha
			return ElixirPotion.clone( getPool(ElixirPotion.class) ).clone(item);
		case 2578:	// 송편
		case 2579:	// 쑥송편
		case 3370:	// 정신력의 물약
			return ManaPotion.clone( getPool(ManaPotion.class) ).clone(item);
		case 2518:	// 흑정령의 수정 (블라인드 하이딩)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 13, 0 ).clone(item);
		case 2519:	// 흑정령의 수정 (인챈트 베놈)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 13, 1 ).clone(item);
		case 2520:	// 흑정령의 수정 (쉐도우 아머)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 13, 2 ).clone(item);
		case 2521:	// 흑정령의 수정 (브링 스톤)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 13, 3 ).clone(item);
		case 2522:	// 흑정령의 수정 (무빙 악셀레이션)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 13, 4 ).clone(item);
		case 2523:	// 흑정령의 수정 (버닝 스피릿츠)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 13, 5 ).clone(item);
		case 2524:	// 흑정령의 수정 (다크 블라인드)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 13, 6 ).clone(item);
		case 2525:	// 흑정령의 수정 (베놈 레지스트)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 13, 7 ).clone(item);
		case 2526:	// 흑정령의 수정 (더블 브레이크)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 14, 0 ).clone(item);
		case 2527:	// 흑정령의 수정 (언케니 닷지)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 14, 1 ).clone(item);
		case 2528:	// 흑정령의 수정 (쉐도우 팽)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 14, 2 ).clone(item);
		case 2529:	// 흑정령의 수정 (파이널 번)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 14, 3 ).clone(item);
		case 3172:	// 흑정령의 수정 (드레스 마이티)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 14, 4 ).clone(item);
		case 3173:	// 흑정령의 수정 (드레스 덱스터리티)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 14, 5 ).clone(item);
		case 3174:	// 흑정령의 수정 (드레스 이베이젼)
			return ItemDarkSpiritCrystalInstance.clone( getPool(ItemDarkSpiritCrystalInstance.class), 14, 6 ).clone(item);
		default:
			if(item.getType1().equalsIgnoreCase("weapon")){
				if(item.getType2().equalsIgnoreCase("edoryu")){
					return Edoryu.clone( getPool(Edoryu.class) ).clone(item);
				}else if(item.getType2().equalsIgnoreCase("claw")){
					return Claw.clone( getPool(Claw.class) ).clone(item);
				}else{
					return ItemWeaponInstance.clone( getPool(ItemWeaponInstance.class) ).clone(item);
				}
			}else if(item.getType1().equalsIgnoreCase("armor")){
				return ItemArmorInstance.clone( getPool(ItemArmorInstance.class) ).clone(item);
			}else if(item.getType1().equalsIgnoreCase("item")){
				return newDefaultItem(item);
			}else{
				return ItemInstance.clone( getPool(ItemInstance.class) ).clone(item);
			}
		}
	}

	/**
	 * 아이템 생성처리 함수.
	 *  : 관리를 위해 함수 따로 뺌.
	 *  : item타입에 type2값에 의한 생성처리.
	 * @param item
	 * @return
	 */
	static private ItemInstance newDefaultItem(Item item) {

		// 용기 물약
		if (item.getType2().equalsIgnoreCase("bravery potion")) {
			return BraveryPotion.clone(getPool(BraveryPotion.class)).clone(item);

			// 경험치 물약
		} else if (item.getType2().equalsIgnoreCase("exp rise")) {
			return ExpRisePotion.clone(getPool(ExpRisePotion.class)).clone(item);

			// 경험치 물약 제거
		} else if (item.getType2().equalsIgnoreCase("exp restore power")) {
			return ExpReStorePowerPotion.clone(getPool(ExpReStorePowerPotion.class)).clone(item);

			// 레벨업 주문서
		} else if (item.getType2().equalsIgnoreCase("scroll_levelup")) {
			return LevelUpScroll.clone(getPool(LevelUpScroll.class)).clone(item);

			// 혈맹 귀환 주문서
		} else if (item.getType2().equalsIgnoreCase("$2380")) {
			return ScrollLabeledVerrYedHoraePledgeHouse.clone(getPool(ScrollLabeledVerrYedHoraePledgeHouse.class)).clone(item);

			// 폭죽
		} else if (item.getType2().equalsIgnoreCase("firework")) {
			return Firework.clone(getPool(Firework.class)).clone(item);

			// 마법 포션
		} else if (item.getType2().startsWith("spell_potion_")) {
			return SpellPotion.clone(getPool(SpellPotion.class)).clone(item);

			// 성별 전환 아이템
		} else if (item.getType2().equalsIgnoreCase("change_sex")) {
			return ChangeSexPotion.clone(getPool(ChangeSexPotion.class)).clone(item);

			// 번들아이템
		} else if (item.getType2().equalsIgnoreCase("bundle")) {
			return Bundle.clone(getPool(Bundle.class)).clone(item);

			// 이동 주문서
		} else if (item.getType2().startsWith("teleport_")) {
			return ScrollTeleport.clone(getPool(ScrollTeleport.class)).clone(item);

			// 부적개념에 이동 주문서
		} else if (item.getType2().startsWith("TOIteleport_")) {
			return ScrollTOITeleport.clone(getPool(ScrollTOITeleport.class)).clone(item);

			// 펫 목걸이
		} else if (item.getType2().equalsIgnoreCase("dog collar")) {
			return DogCollar.clone(getPool(DogCollar.class)).clone(item);

			// 펫 호루라기
		} else if (item.getType2().equalsIgnoreCase("dog whistle")) {
			return PetWhistle.clone(getPool(PetWhistle.class)).clone(item);

			// 체력 회복 물약
		} else if (item.getType2().startsWith("healing potion")) {
			return HealingPotion.clone(getPool(HealingPotion.class)).clone(item);

			// 초록 물약
		} else if (item.getType2().startsWith("haste potion")) {
			return HastePotion.clone(getPool(HastePotion.class)).clone(item);

			// 용기 물약
		} else if (item.getType2().startsWith("bravery potion")) {
			return BraveryPotion.clone(getPool(BraveryPotion.class)).clone(item);

			// 파란 물약
		} else if (item.getType2().startsWith("blue potion")) {
			return BluePotion.clone(getPool(BluePotion.class)).clone(item);

			// 엘븐와퍼
		} else if (item.getType2().startsWith("elven wafer")) {
			return ElvenWafer.clone(getPool(ElvenWafer.class)).clone(item);

			// 귀환 주문서
		} else if (item.getType2().startsWith("verr yed horae")) {
			return ScrollLabeledVerrYedHorae.clone(getPool(ScrollLabeledVerrYedHorae.class)).clone(item);

			// 확인 주문서
		} else if (item.getType2().startsWith("kernodwel")) {
			return ScrollLabeledKernodwel.clone(getPool(ScrollLabeledKernodwel.class)).clone(item);

			// 강화 속도향상 물약
		} else if (item.getType2().startsWith("greater haste potion")) {
			return GreaterHastePotion.clone(getPool(GreaterHastePotion.class)).clone(item);

			// 변신 주문서
		} else if (item.getType2().startsWith("polymorph")) {
			return ScrollPolymorph.clone(getPool(ScrollPolymorph.class)).clone(item);

			// 순간이동 주문서
		} else if (item.getType2().startsWith("venzar borgavve")) {
			return ScrollLabeledVenzarBorgavve.clone(getPool(ScrollLabeledVenzarBorgavve.class)).clone(item);
			
		// 캐릭터 좌표 복구 주문서
		} else if (item.getType2().startsWith("location scroll")) {
			return ScrollLocationReset.clone(getPool(ScrollLocationReset.class)).clone(item);

			// 스톰 워크
		} else if (item.getType2().startsWith("storm walk")) {
			return StormWalk.clone(getPool(StormWalk.class)).clone(item);

		} else if (item.getType2().startsWith("[giro]")) {
			String key = item.getType2().substring(0, 6).trim();
			String value = item.getType2().substring(key.length()).trim();
			
			// 경험치 아이템
			if (value.equalsIgnoreCase("경험치")) {
				return 경험치아이템.clone(getPool(경험치아이템.class)).clone(item);
				
			// 더블 경험치 아이템
			} else if (value.equalsIgnoreCase("더블경치")) {
				return 더블경험치아이템.clone(getPool(더블경험치아이템.class)).clone(item);

			} else {
				return ItemInstance.clone(getPool(ItemInstance.class)).clone(item);
			}

		} else {

			return ItemInstance.clone(getPool(ItemInstance.class)).clone(item);
		}
	}

	/**
	 * 아이템 객체 정보를 그대로 복사해서 객체 생성.
	 * object_id 새로 할당 
	 * 해당 객체 리턴.
	 * @param item
	 * @return
	 */
	static public ItemInstance newInstance(ItemInstance item){
		if(item != null){
			ItemInstance temp = newInstance(item.getItem());
			if(temp != null){
				temp.setObjectId( ServerDatabase.nextItemObjId() );
				temp.setDefinite(item.isDefinite());
				temp.setCount(item.getCount());
				temp.setBress(item.getBress());
				temp.setQuantity(item.getQuantity());
				temp.setEnLevel(item.getEnLevel());
				temp.setDurability(item.getDurability());
				temp.setDynamicMr(item.getDynamicMr());
				temp.setTime(item.getTime());
				temp.setNowTime(item.getNowTime());
				temp.setEquipped(item.isEquipped());
				temp.setTimeDrop(item.getTimeDrop());
				temp.setDynamicLight(item.getDynamicLight());
				temp.setDynamicAc(item.getDynamicAc());
				temp.setUsershopBuyPrice(item.getUsershopBuyPrice());
				temp.setUsershopSellPrice(item.getUsershopSellPrice());
				temp.setUsershopBuyCount(item.getUsershopBuyCount());
				temp.setUsershopSellCount(item.getUsershopSellCount());
				temp.setUsershopIdx(item.getUsershopIdx());
				temp.setSkill(item.getSkill());
				temp.setCharacter(item.getCharacter());

				// InnRoomKey
				temp.setInnRoomKey(item.getInnRoomKey());
				// SlimeRaceTicket
				temp.setSlimeRaceTicket(item.getSlimeRaceTicket());
				// DogCollar
				if(item instanceof DogCollar){
					DogCollar dc = (DogCollar)item;
					DogCollar temp_dc = (DogCollar)temp;
					temp_dc.setPetObjectId(dc.getPetObjectId());
					temp_dc.setPetName(dc.getPetName());
					temp_dc.setPetClassId(dc.getPetClassId());
					temp_dc.setPetLevel(dc.getPetLevel());
					temp_dc.setPetHp(dc.getPetHp());
					temp_dc.setPetSpawn(dc.isPetSpawn());
					temp_dc.setPetDel(dc.isPetDel());
				}
				// Letter
				if(item instanceof Letter){
					Letter l = (Letter)item;
					Letter temp_l = (Letter)temp;
					temp_l.setFrom(l.getFrom());
					temp_l.setTo(l.getTo());
					temp_l.setSubject(l.getSubject());
					temp_l.setMemo(l.getMemo());
					temp_l.setDate(l.getDate());
					temp_l.setLetterUid(l.getLetterUid());
				}

				return temp;
			}
		}
		return null;
	}

	/**
	 * 사용다된 아이템 풀에 다시 넣는 함수.
	 * @param item
	 */
	static public void setPool(ItemInstance item){
		item.close();
		pool.add(item);
		//		lineage.share.System.println("append : "+pool.size());
	}

	/**
	 * 아이템 재사용을위해 풀에서 필요한 객체 찾아서 리턴.
	 * @param c
	 * @return
	 */
	static public ItemInstance getPool(Class<?> c){
		ItemInstance item = findPool(c);
		if(item!=null)
			pool.remove(item);

		//		lineage.share.System.println("remove : "+pool.size());
		return item;
	}

	static private ItemInstance findPool(Class<?> c){
		for(ItemInstance item : pool){
			if(item.getClass().equals(c))
				return item;
		}
		return null;
	}

	static private int getMaterial(final String meterial){
		if(meterial.equalsIgnoreCase("액체"))
			return 1;
		else if(meterial.equalsIgnoreCase("밀랍"))
			return 2;
		else if(meterial.equalsIgnoreCase("식물성"))
			return 3;
		else if(meterial.equalsIgnoreCase("동물성"))
			return 4;
		else if(meterial.equalsIgnoreCase("종이"))
			return 5;
		else if(meterial.equalsIgnoreCase("천"))
			return 6;
		else if(meterial.equalsIgnoreCase("가죽"))
			return 7;
		else if(meterial.equalsIgnoreCase("나무"))
			return 8;
		else if(meterial.equalsIgnoreCase("뼈"))
			return 9;
		else if(meterial.equalsIgnoreCase("용 비늘"))
			return 10;
		else if(meterial.equalsIgnoreCase("철"))
			return 11;
		else if(meterial.equalsIgnoreCase("금속"))
			return 12;
		else if(meterial.equalsIgnoreCase("구리"))
			return 13;
		else if(meterial.equalsIgnoreCase("은"))
			return 14;
		else if(meterial.equalsIgnoreCase("금"))
			return 15;
		else if(meterial.equalsIgnoreCase("백금"))
			return 16;
		else if(meterial.equalsIgnoreCase("미스릴"))
			return 17;
		else if(meterial.equalsIgnoreCase("블랙 미스릴"))
			return 18;
		else if(meterial.equalsIgnoreCase("유리"))
			return 19;
		else if(meterial.equalsIgnoreCase("보석"))
			return 20;
		else if(meterial.equalsIgnoreCase("광석"))
			return 21;
		else if(meterial.equalsIgnoreCase("오리하루콘"))
			return 22;
		return 0;
	}

	static private int getWeaponGfx(final String type2){
		if(type2.equalsIgnoreCase("sword"))
			return Lineage.WEAPON_SWORD;
		else if(type2.equalsIgnoreCase("tohandsword"))
			return Lineage.server_version>230 ? Lineage.WEAPON_TOHANDSWORD : Lineage.WEAPON_SWORD;
			else if(type2.equalsIgnoreCase("axe"))
				return Lineage.WEAPON_AXE;
			else if(type2.equalsIgnoreCase("bow"))
				return Lineage.WEAPON_BOW;
			else if(type2.equalsIgnoreCase("spear"))
				return Lineage.WEAPON_SPEAR;
			else if(type2.equalsIgnoreCase("wand"))
				return Lineage.WEAPON_WAND;
			else if(type2.equalsIgnoreCase("staff"))
				return Lineage.WEAPON_WAND;
			else if(type2.equalsIgnoreCase("dagger"))
				return Lineage.server_version>230 ? Lineage.WEAPON_DAGGER : Lineage.WEAPON_SWORD;
				else if(type2.equalsIgnoreCase("blunt"))
					return Lineage.WEAPON_BLUNT;
				else if(type2.equalsIgnoreCase("edoryu"))
					return Lineage.WEAPON_EDORYU;
				else if(type2.equalsIgnoreCase("claw"))
					return Lineage.WEAPON_CLAW;
				else if(type2.equalsIgnoreCase("throwingknife"))
					return Lineage.WEAPON_THROWINGKNIFE;
				else if(type2.equalsIgnoreCase("arrow"))
					return Lineage.WEAPON_ARROW;
				else if(type2.equalsIgnoreCase("gauntlet"))
					return Lineage.server_version>200 ? Lineage.WEAPON_GAUNTLET : Lineage.WEAPON_AXE;
					else if(type2.equalsIgnoreCase("chainsword"))
						return Lineage.WEAPON_CHAINSWORD;
					else if(type2.equalsIgnoreCase("keyrink"))
						return Lineage.WEAPON_KEYRINK;
					else if(type2.equalsIgnoreCase("tohandblunt"))
						return Lineage.WEAPON_BLUNT;
					else if(type2.equalsIgnoreCase("tohandstaff"))
						return Lineage.WEAPON_WAND;
					else if(type2.equalsIgnoreCase("tohandspear"))
						return Lineage.WEAPON_SPEAR;

		return Lineage.WEAPON_NONE;
	}

	static private int getSlot(final String type2){
		if(type2.equalsIgnoreCase("helm"))
			return Lineage.SLOT_HELM;
		else if(type2.equalsIgnoreCase("earring"))
			return Lineage.SLOT_EARRING;
		else if(type2.equalsIgnoreCase("necklace"))
			return Lineage.SLOT_NECKLACE;
		else if(type2.equalsIgnoreCase("t"))
			return Lineage.SLOT_SHIRT;
		else if(type2.equalsIgnoreCase("armor"))
			return Lineage.SLOT_ARMOR;
		else if(type2.equalsIgnoreCase("cloak"))
			return Lineage.SLOT_CLOAK;
		else if(type2.equalsIgnoreCase("belt"))
			return Lineage.SLOT_BELT;
		else if(type2.equalsIgnoreCase("glove"))
			return Lineage.SLOT_GLOVE;
		else if(type2.equalsIgnoreCase("shield"))
			return Lineage.SLOT_SHIELD;
		else if(type2.equalsIgnoreCase("boot"))
			return Lineage.SLOT_BOOTS;
		else if(type2.equalsIgnoreCase("ring"))
			return Lineage.SLOT_RING_LEFT;
		else if(type2.equalsIgnoreCase("sword"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("axe"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("bow"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("wand"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("tohandsword"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("spear"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("dagger"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("blunt"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("claw"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("edoryu"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("gauntlet"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("speer"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("staff"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("chainsword"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("keyrink"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("tohandblunt"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("tohandstaff"))
			return Lineage.SLOT_WEAPON;
		else if(type2.equalsIgnoreCase("tohandspear"))
			return Lineage.SLOT_WEAPON;

		return Lineage.SLOT_NONE;
	}

	static private int getEquippedSlot(final String type2){
		if(type2.equalsIgnoreCase("armor"))
			return 2;
		else if(type2.equalsIgnoreCase("cloak"))
			return 10;
		else if(type2.equalsIgnoreCase("t"))
			return 18;
		else if(type2.equalsIgnoreCase("glove"))
			return 20;
		else if(type2.equalsIgnoreCase("boot"))
			return 21;
		else if(type2.equalsIgnoreCase("helm"))
			return 22;
		else if(type2.equalsIgnoreCase("ring"))
			return 23;
		else if(type2.equalsIgnoreCase("necklace"))
			return 24;
		else if(type2.equalsIgnoreCase("shield"))
			return 25;
		else if(type2.equalsIgnoreCase("belt"))
			return 37;
		else if(type2.equalsIgnoreCase("earring"))
			return 40;
		return 1;
	}

	static public int getSize(){
		return list.size();
	}

	static public int getPoolSize(){
		return pool.size();
	}

	static public List<Item> getList(){
		return list;
	}
}
