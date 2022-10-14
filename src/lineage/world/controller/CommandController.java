package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.StringTokenizer;

import lineage.FakeUserCount;
import lineage.bean.database.Exp;
import lineage.bean.database.Item;
import lineage.bean.lineage.Buff;
import lineage.bean.lineage.BuffInterface;
import lineage.bean.lineage.Kingdom;
import lineage.database.BadIpDatabase;
import lineage.database.DatabaseConnection;
import lineage.database.ExpDatabase;
//import lineage.database.GmCommandDatabase;
import lineage.database.ItemBundleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemSetoptionDatabase;
import lineage.database.ItemSkillDatabase;
import lineage.database.ItemTeleportDatabase;
import lineage.database.MonsterBossSpawnlistDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterDropDatabase;
import lineage.database.MonsterSkillDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.database.NpcDatabase;
import lineage.database.NpcShopDatabase;
import lineage.database.NpcTeleportDatabase;
import lineage.database.PolyDatabase;
import lineage.database.ServerDatabase;
import lineage.database.ServerNoticeDatabase;
import lineage.database.SkillDatabase;
import lineage.database.SummonListDatabase;
import lineage.database.TeleportHomeDatabase;
import lineage.database.TeleportResetDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_Ability;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectChatting;
import lineage.network.packet.server.S_ObjectEffect;
import lineage.network.packet.server.S_ObjectGfx;
import lineage.network.packet.server.S_ObjectHitratio;
import lineage.network.packet.server.S_WorldTime;
import lineage.plugin.PluginController;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Shutdown;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.AbsoluteBarrier;
import lineage.world.object.magic.AdvanceSpirit;
import lineage.world.object.magic.BlessWeapon;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.BurningWeapon;
import lineage.world.object.magic.ChattingClose;
import lineage.world.object.magic.EarthSkin;
import lineage.world.object.magic.EnchantDexterity;
import lineage.world.object.magic.EnchantMighty;
import lineage.world.object.magic.GlowingAura;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.InvisiBility;
import lineage.world.object.magic.IronSkin;
import lineage.world.object.magic.ShiningAura;

public final class CommandController {
	
	static public String TOKEN = ".";

	static public void init(){

	}

	/**
	 * 명령어 처리 함수
	 * @param pc	: 명령어 요청자
	 * @param cmd	: 명령어
	 * @return		: 명령어 수행 성공 여부
	 */
	static public boolean toCommand(object o, String cmd){
		if(o == null)
			return false;

		try {
			StringTokenizer st = new StringTokenizer(cmd);
			String key = st.nextToken(); //어라이즈
			
			Object is_check = PluginController.init(CommandController.class, "toCommand", o, cmd, st);
			if(is_check!=null && (Boolean)is_check)
			return true;
			
			// 유저 명령어 시작
			// 오브젝트 리로드 시작
			if(cmd.startsWith("..")){ //화면 오브젝트 리로딩
				o.toTeleport(o.getX(), o.getY(), o.getMap(), false);
				ChattingController.toChatting(o, "화면을 새로고침 하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
		    	return true;
		    // 오브젝트 리로드 끝
	    	
		    // 도움말 시작
			} else if (cmd.startsWith(".도움말")) {
				ChattingController.toChatting(o, "<이용자 명령어>", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "몹   템   피바   시간   ..", Lineage.CHATTING_MODE_MESSAGE);
				return true;
			// 도움말 끝
			
			}else if(cmd.startsWith(".시간")) {
				String now_date = String.format("아덴 월드 시간 : %02d:%02d:%02d", ServerDatabase.getLineageTimeHour(), ServerDatabase.getLineageTimeMinute(), ServerDatabase.getLineageTimeSeconds());
				ChattingController.toChatting(o, String.format("%s", now_date), Lineage.CHATTING_MODE_MESSAGE);
				return true;
				
				
			// 본 서버화를 위해 '틱' 주석처리
//			} else if (cmd.startsWith(".틱")) {
//				if (o instanceof Character) {
//					Character cha = (Character) o;
//					ChattingController.toChatting(cha, String.format("hp틱(%dms, %d), mp틱(%dms, %d)", cha.getHpTime(),
//							cha.hpTic(), cha.getMpTime(), cha.mpTic()), Lineage.CHATTING_MODE_MESSAGE);
//					return true;
//				}
			    
			// 본 서버화를 위해 '오토루팅' 주석처리
//			} else if (cmd.startsWith(".오토루팅")) {
//				o.setAutoPickup(!o.isAutoPickup());
//				ChattingController.toChatting(o, String.format("오토루팅이 %s활성화 되었습니다.", o.isAutoPickup() ? "" : "비"),
//						Lineage.CHATTING_MODE_MESSAGE);
//				return true;

			// 본 서버화를 위해 '맵핵 켬' 주석처리
//			} else if(cmd.startsWith(".맵핵 켬") || (cmd.startsWith("-맵핵 켬") || (cmd.startsWith(".라이트 켬") || (cmd.startsWith("-라이트 켬"))))) {
//				if (o instanceof Character) {
//					Character cha = (Character) o;
//			cha.toSender(S_Ability.clone(BasePacketPooling.getPool(S_Ability.class), 3, true));  // 3대신 2일 수도 있음 
//					ChattingController.toChatting(o, String.format("맵핵(라이트) 모드가 활성화 되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
//					return true;
//				}
				
			// 본 서버화를 위해 '맵핵 끔' 주석처리
//			} else if(cmd.startsWith(".맵핵 끔") || (cmd.startsWith("-맵핵 끔") || (cmd.startsWith(".라이트 끔") || (cmd.startsWith("-라이트 끔"))))) {
//				if (o instanceof Character) {
//					Character cha = (Character) o;
//			cha.toSender(S_Ability.clone(BasePacketPooling.getPool(S_Ability.class), 3, false));  // 3대신 2일 수도 있음 
//					ChattingController.toChatting(o, String.format("맵핵(라이트) 모드가 비활성화 되었습니다."), Lineage.CHATTING_MODE_MESSAGE);
//					return true;
//				}
			
			// 본 서버화를 위해 '버프확인' 주석처리
//			} else if (cmd.startsWith(".버프확인")) {
//				Buff buff = BuffController.find(o);
//				if (buff != null) {
//					for (BuffInterface b : buff.getList()) {
//						if (b.getSkill() != null)
//							ChattingController.toChatting(o,
//									String.format("%s : %d 입니다.", b.getSkill().getName(), b.getTime()), Lineage.CHATTING_MODE_MESSAGE);
//					}
//					return true;
//				}

			// 피바
			} else if(cmd.startsWith(".피바")) {
				// 설정.
				o.setHpbar( !o.isHpbar() );
				ChattingController.toChatting(o, String.format("체력 상태 바 : %s활성화", o.isHpbar() ? "" : "비"), 20);
				// 표현.
				o.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), (Character)o, o.isHpbar()));
				return true;
			
			// 몹드랍
			} else if (cmd.startsWith(".몹")) {
				monsterDrop(o, st);
				return true;
				
			// 템드랍
			} else if (cmd.startsWith(".템")) {
				dropItem(o, st);
				return true;
				
			// 본 서버화를 위해 '버프' 주석처리
//			} else if (cmd.startsWith(".버프")) {
//				if (o.getLevel() >= 40) { // 사용 레벨
//					ChattingController.toChatting(o, "40레벨 이상 해당 명령어를 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
//				} else {
//					lineage.world.controller.CommandController.toBuff(o);
//				}
//				return true;
			}

			// 운영자 명령어 시작
			// 운영자 명령어 권한 확인
			if(o.getGm()==0)
				return false;
			if (cmd.startsWith(".gm")){
				ChattingController.toChatting(o, "-----------------< GM COMMANDS >-------------------", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "skillmaster  fake", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "mode  recall  block  clean  speed  gfx  allbuff  reload  serverbuff", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "action  go  level  res  home  monster  shutdown  openwt  openst", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "inven  move  chatblock  die  inven  item  mobkill  allcall", Lineage.CHATTING_MODE_MESSAGE);
				return true;
				
			// 인벤 : user inventory investigate
			} else if (cmd.startsWith(".inven")) {
				String name = st.nextToken();
				PcInstance use = World.findPc(name);
				if (use != null) {
					ChattingController.toChatting(o, "----------inventory----------", 20);
					for (ItemInstance ii : use.getInventory().getList())
						ChattingController.toChatting(o, ii.toStringDB(), 20);
					ChattingController.toChatting(o, "------------------------------", 20);
				} else {
					ChattingController.toChatting(o, "'" + name + "' User is not logged in.", 20);
				}
				return true;
			// 인벤 끝
				
//			// 인벤삭제 시작
//			} else if (cmd.startsWith(".인벤삭제")) {
//				try {
//					toInventoryDelete(o, st);
//				} catch (Exception localException61) {
//					ChattingController.toChatting(o,
//							new StringBuilder().append(TOKEN).append("인벤삭제 charname").toString(), 20);
//				}
//				return true;
//			// 인벤 삭제 끝
				
			// 오픈 대기 : server open waiting (packet close)
			} else if (cmd.startsWith(".openwt")) {
				try {
					toOpentime(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".openwt", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// 오픈 시작 : server open start(packet open)
			} else if (cmd.startsWith(".openst")) {
				try {
					toOpen(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".openst", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;	
			
			// 리로드 : reload MySQL table & .conf file.
			} else if (cmd.startsWith(".reload")) {
				try {
					reLoading(o, st);
				} catch (Exception localException20) {
					if (o != null)
					ChattingController.toChatting(o, new StringBuilder().append(TOKEN).append("reload").toString(),Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "mobskill", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "notice  bundle  poly  npc  set  servernotice  tpreset  tpnpc", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "drop  boss  skill  monster  tphome  badip  itemskill", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "shop  kingdom  summon  item  config  tpitem", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
				
			// time : i don't know
			} else if (cmd.startsWith(".time")) {
				cmd = cmd.substring(6);
				ServerDatabase.LineageWorldTime = Integer.valueOf(cmd);
				o.toSender(S_WorldTime.clone(BasePacketPooling.getPool(S_WorldTime.class)));
				return true;		
						
			// op : i don't know		
			} else if (cmd.startsWith(".op")) {
				o.setDead(true);
				o.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 321));
				return true;
			// op 끝
						
			// 이동 : move to location
			} else if (cmd.startsWith(".move")) {
				try {
					int x = Integer.valueOf(st.nextToken());
					int y = Integer.valueOf(st.nextToken());
					int map = Integer.valueOf(st.nextToken());
					o.toTeleport(x, y, map, true);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".move  x  y  map", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
				
			// t 시작	(미사용)
//			}else if(cmd.startsWith(".t")){
//				for(int i=0 ; i<1000 ; ++i){
//					o.toSender(S_BuffTrueTarget.clone(BasePacketPooling.getPool(S_BuffTrueTarget.class), o, "1234"), true);
//				}
//				return true;
			// t 끝
				
			// 그래픽 : ground effect view
			} else if (cmd.startsWith(".gfx")) {
				int gfx = Integer.valueOf(st.nextToken());
				o.setGfx(gfx);
				o.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), o), true);
				return true;

			// 모드 : i don't know.
			} else if (cmd.startsWith(".mode")) {
				int mode = Integer.valueOf(st.nextToken());
				o.setGfxMode(mode);
				o.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), o), true);
				return true;
						
			// 액션 : frame action view
			} else if (cmd.startsWith(".action")) { // Felix: Replace Korean for Action later
				int action = Integer.valueOf(st.nextToken());
				o.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), o, action), true);
				return true;
						
			// 횃불 : torch create > background_spawnlist table
			} else if (cmd.startsWith(".torch")) {
				Connection con = null;
				PreparedStatement stt = null;
				try {
					int x = o.getX() + Util.getXY(o.getHeading(), true);
					int y = o.getY() + Util.getXY(o.getHeading(), false);
					int map = o.getMap();

					con = DatabaseConnection.getLineage();
					stt = con.prepareStatement("INSERT INTO background_spawnlist SET gfx=85, light=13, locX=?, locY=?, locMap=?");
					stt.setInt(1, x);
					stt.setInt(2, y);
					stt.setInt(3, map);
					stt.executeUpdate();
					return true;

				} catch (Exception e) {
					lineage.share.System.printf("%s : torch spawn completed\r\n", CommandController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, stt);
				}

			// 속도 : game master (me) speed up
			} else if (cmd.startsWith(".speed")) {
				try {
					speed(o);
					ChattingController.toChatting(o, "GM speed up", Lineage.CHATTING_MODE_MESSAGE);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".speed", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// 몬스터 : monster create
			} else if (cmd.startsWith(".monster")) {
				try {
					toMonster(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".mob name count range", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
						
			// 아이템 : item create
			} else if (cmd.startsWith(".item")) {
				try {
					toItem(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".item name count enchant", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
						
			// 소환 : recall user
			} else if (cmd.startsWith(".recall")) {
				try {
					toCall(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".recall charname", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
							
			// 출두 : go to user
			} else if (cmd.startsWith(".go")) {
				try {
					toGo(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".go charname", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;

			// 올버프 : me all buff
			} else if (cmd.startsWith(".allbuff")) {
				try {
					toBuffAll(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".allbuff", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
				
			// 전체 버프 : server user all buff
			} else if (cmd.startsWith(".serverbuff")) {
				try {
					toGmBuffAll(o);
					ChattingController.toChatting(o, "Str, Dex, Haste, Bless, Iron, Bur, Shining, glowing", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "운영자가 전체버프를 시전하였습니다.", Lineage.CHATTING_MODE_MESSAGE);
				} catch (Exception e) {

				}
			// 전체 버프 끝
						
			// 뻥 : user fake count
			} else if (cmd.startsWith(".fake")) {
				try {
					int count = Integer.parseInt(st.nextToken());
					FakeUserCount.getInstance().gmFakeUserCount = count;
				} catch (Exception e) {
					ChattingController.toChatting(o, ".뻥  number", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
						
			// 차단 : user block
			} else if (cmd.startsWith(".block")) {
				try {
					toBan(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".block  charname", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// 스킬 마스터 : skill master gift
			} else if (cmd.startsWith(".skillmaster")) {
				try {
					toSkillAllMaster(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".skillmaster  charname", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
						
			// 채금 : user chat block
			} else if (cmd.startsWith(".chatblock")) {
				try {
					toChattingClose(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".chatblock  charname time", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// 청소 : server ground clean
			} else if (cmd.startsWith(".clean")) {
				try {
					toWorldItemClear(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".clean", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// 셧다운 : server shutdown
			} else if (cmd.startsWith(".shutdown")) {
				try {
					toShutdown(o, st);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".shutdown  min", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// 몹정리 : monster all kill
			} else if (cmd.startsWith(".mobkill")) {
				try {
					toClearMonster(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".mobkill", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// 부활 : resurrection
			} else if (cmd.startsWith(".res")) {
				try {
					o.toRevival(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".res", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
		
			// 죽기 : suicide me
			} else if (cmd.startsWith(".die")) {
				try {
					o.setNowHp(0);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".die", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;

			// 전체 소환 : server user all call
			} else if (cmd.startsWith(".allcall")) {
				try {
					for (PcInstance pc : World.getPcList())
						pc.toTeleport(o.getX(), o.getY(), o.getMap(), true);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".allcall", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;

			// 레벨 선물 : level present
			} else if (cmd.startsWith(".level")) {
				try {
					toLevel(o, st);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".level  charname level", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
				
			// 귀환 : home
			} else if (cmd.startsWith(".home")) {
				try {
					comebackHome(o, st);
				} catch (Exception e) {
					if (o != null)
					ChattingController.toChatting(o, ".home number", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "1:gd  2:wd  3:kt  4:kr  5:ft  6:st  7:tk  8:ef", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "9:race  10:oasis  11:drgnv", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "100:ktk  101:wdk  102:krk  103:orck  99:gm1  9999:gm2", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// 공성 체크 : kingdom war check
			} else if (key.equalsIgnoreCase(".warck")) {
				try {
					toKingdomWarCheck(o, st);
				} catch (Exception localException69) {
					ChattingController.toChatting(o, new StringBuilder().append(TOKEN).append("warck").toString(), 20);
				}
				return true;
				
			// 공성 시작 : kingdom war start
			} else if (key.equalsIgnoreCase(".warst")) {
				try {
					toKingdomWarStart(o, st);
				} catch (Exception localException70) {
					ChattingController.toChatting(o, new StringBuilder().append(TOKEN).append("warst  [ name ]").toString(),
							20);
					ChattingController.toChatting(o, Common.HELPER_LINE, 20);
					ChattingController.toChatting(o, "[kent, orc, wind, kiran, heine, dwarf, aden]", 20);
					ChattingController.toChatting(o, Common.HELPER_LINE, 20);
				}
				return true;
			
			// 공성 종료 : kingdom war end
			} else if (key.equalsIgnoreCase(".warend")) {
				try {
					toKingdomWarStop(o, st);
				} catch (Exception localException71) {
					ChattingController.toChatting(o,
							new StringBuilder().append(TOKEN).append("warend  [ name / all ]").toString(), 20);
					ChattingController.toChatting(o, Common.HELPER_LINE, 20);
					ChattingController.toChatting(o, "[kent, orc, wind, kiran, heine, dwarf, aden]", 20);
					ChattingController.toChatting(o, Common.HELPER_LINE, 20);
				}
				return true;
				
			// 투명 : invisibility mode, only me
			} else if (cmd.startsWith(".invis")) {
				try {
					Invisible(o, st);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".invis  [ on / off ]", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;

			}
		} catch (Exception e) {
			// lineage.share.System.println(CommandController.class.toString()+"
			// : toCommand(PcInstance pc, String cmd)");
			// lineage.share.System.println(e);
		}
		return false;
	}

	// 어라이즈
	public static void toKingdomWarCheck(object paramobject, StringTokenizer paramStringTokenizer) {
		for (Kingdom localKingdom : KingdomController.getKingdomList()) {
			if (localKingdom.isWar())
				ChattingController.toChatting(paramobject,
						String.format("%s : 공성이 진행중입니다.", new Object[] { localKingdom.getName() }), 20);
			else
				ChattingController.toChatting(paramobject,
						String.format("%s : 공성시간이 아닙니다.", new Object[] { localKingdom.getName() }), 20);

		}
	}
	// 어라이즈
	public static void toKingdomWarStart(object paramobject, StringTokenizer paramStringTokenizer) {
		String str = paramStringTokenizer.nextToken();
		int i = 0;
		for (Kingdom localKingdom : KingdomController.getKingdomList()) {
			if (localKingdom.getName().trim().replaceAll(" ", "").equals(str)) {
				if (localKingdom.isWar()) {
					ChattingController.toChatting(paramobject,
							String.format("%s : 공성이 이미 진행중입니다.", new Object[] { str }), 20);
					return;
				}
				localKingdom.setWarDay(Calendar.getInstance().getTime().getTime());
				i = 1;
				if (paramobject != null)
					ChattingController.toChatting(paramobject, String.format("%s : 공성이 시작되었습니다.", new Object[] { str }),
							20);
			}
		}
		if ((paramobject != null) && (i == 0))
			ChattingController.toChatting(paramobject, String.format("%s : 존재하지 않는 성이름입니다.", new Object[] { str }), 20);
	}
	// 어라이즈
	public static void toKingdomWarStop(object paramobject, StringTokenizer paramStringTokenizer) {
		String str = paramStringTokenizer.nextToken();
		int i = 0;
		for (Kingdom localKingdom : KingdomController.getKingdomList()) {
			if ((str.equals("all")) || (localKingdom.getName().trim().replaceAll(" ", "").equals(str)))
				if (!localKingdom.isWar()) {
					ChattingController.toChatting(paramobject,
							String.format("%s : 공성이 진행중이지 않습니다.", new Object[] { localKingdom.getName() }), 20);
				} else {
					localKingdom.toStopWar(java.lang.System.currentTimeMillis());
					i = 1;
					if (paramobject != null)
						ChattingController.toChatting(paramobject,
								String.format("%s : 공성이 종료되었습니다.", new Object[] { localKingdom.getName() }), 20);
				}
		}
		if ((paramobject != null) && (!str.equals("all")) && (i == 0))
			ChattingController.toChatting(paramobject, String.format("%s : 존재하지 않는 성이름입니다.", new Object[] { str }), 20);
	}

	// 오픈 대기 : server open wait
	private static void toOpentime(object o) {
		Lineage.rate_drop = 1;
		Lineage.rate_aden = 1;
		Lineage.level_max = 1;	
		Lineage.character_delete = false;
		Lineage.account_auto_create = true;
		// server status : open wait
		ChattingController.toChatting(o, String.format("서버 상태: 오픈 대기"), Lineage.CHATTING_MODE_MESSAGE);

	}

	//오픈 시작 : server open start
	private static void toOpen(object o) {
		Lineage.rate_drop = 1;
		Lineage.rate_aden = 1;
		Lineage.level_max = 55;
		Lineage.character_delete = true;
		// server status : open start
		World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "서버 상태: 오픈 완료"));
	}
	
//	// 인벤삭제
//	public static void toInventoryDelete(object paramobject, StringTokenizer paramStringTokenizer) {
//		String str = paramStringTokenizer.nextToken();
//		PcInstance localPcInstance = World.findPc(str);
//		if (localPcInstance != null) {
//			for (ItemInstance localItemInstance : localPcInstance.getInventory().getList()) {
//				try {
//					if (localItemInstance.isEquipped()) {
//						localItemInstance.setEquipped(false);
//						localItemInstance.toSetoption(localPcInstance, true);
//						localItemInstance.toEquipped(localPcInstance, localPcInstance.getInventory());
//						localItemInstance.toOption(localPcInstance, true);
//						localPcInstance.getInventory().count(localItemInstance, 0L, true);
//					} else {
//						localPcInstance.getInventory().remove(localItemInstance, true);
//					}
//				} catch (Exception localException) {
//					lineage.share.System.printf("%s : toInventoryDelete(object o, StringTokenizer st)\r\n",
//							new Object[] { CommandController.class.toString() });
//					lineage.share.System.println(localException);
//				}
//			}
//		} else if (paramobject != null) {
//			ChattingController.toChatting(paramobject,
//					new StringBuilder().append(str).append("은(는) 월드상 접속중이지 않습니다.").toString(), 20);
//		}
//	}
	
	// 투명 : invisibility mode, only me
	static private void Invisible(object o, StringTokenizer st) {
		String text = st.nextToken();
		if (text.equalsIgnoreCase("on")) {
			o.setInvis(true);
			ChattingController.toChatting(o, "invis mode activated", Lineage.CHATTING_MODE_MESSAGE);
		} else if (text.equalsIgnoreCase("off")) {
			if (o.isInvis()) {
				o.setInvis(false);
				BuffController.remove(o, InvisiBility.class);
			}
			ChattingController.toChatting(o, "invis mode disable", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	// 귀환 : home
	static private void comebackHome(object o, StringTokenizer st) {
		String text = st.nextToken();
		// 글루딘 마을 : gludio town
		if (text.equalsIgnoreCase("1")) {
			o.toTeleport(32625, 32805, 4, false);
			// 우드벡 마을 : woodbec town
		} else if (text.equalsIgnoreCase("2")) {
			o.toTeleport(32611, 33188, 4, false);
			// 켄트 마을 : kent town
		} else if (text.equalsIgnoreCase("3")) {
			o.toTeleport(33072, 32797, 4, false);
			// 기란 마을 : kiran town
		} else if (text.equalsIgnoreCase("4")) {
			o.toTeleport(33429, 32814, 4, false);
			// 화전민 마을 : fire town
		} else if (text.equalsIgnoreCase("5")) {
			o.toTeleport(32745, 32443, 4, false);
			// 은기사 마을 : silver knight town
		} else if (text.equalsIgnoreCase("6")) {
			o.toTeleport(33081, 33394, 4, false);
			// 말하는 섬 : talking island
		} else if (text.equalsIgnoreCase("7")) {
			o.toTeleport(32575, 32942, 0, false);
			// 요정 숲 : elf forest
		} else if (text.equalsIgnoreCase("8")) {
			o.toTeleport(33058, 32339, 4, false);
			// 슬라임 경주장 : slime race
		} else if (text.equalsIgnoreCase("9")) {
			o.toTeleport(32604, 32663, 4, false);
			// 오아시스 : desert oasis
		} else if (text.equalsIgnoreCase("10")) {
			o.toTeleport(32860, 33250, 4, false);
			// 용의 계곡 (큰 용뼈) : drgon valley big bone
		} else if (text.equalsIgnoreCase("11")) {
			o.toTeleport(33385, 32350, 4, false);
			// 켄트 성 : kent kingdom
		} else if (text.equalsIgnoreCase("100")) {
			o.toTeleport(32736, 32784, 15, false);
			// 윈다우드 성 : windawood kingdom
		} else if (text.equalsIgnoreCase("101")) {
			o.toTeleport(32735, 32784, 29, false);
			// 기란 성 : kiran kingdom
		} else if (text.equalsIgnoreCase("102")) {
			o.toTeleport(32729, 32788, 52, false);
			// 오크 요새 : orcrish kingdom
		} else if (text.equalsIgnoreCase("103")) {
			o.toTeleport(32809, 32276, 4, false);
			// 상담 : gm room 1
		} else if (text.equalsIgnoreCase("99")) {
			o.toTeleport(32738, 32794, 99, false);			
			// 상담 : gm room 2
		} else if (text.equalsIgnoreCase("9999")) {
			o.toTeleport(32739, 32796, 18, false);
		} else {
			ChattingController.toChatting(o, ".home  Num", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(o, "1:gd  2:wd  3:kt  4:kr  5:ft  6:st  7:tk", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(o, "8:ef  9:race  10:oasis  11:drgnv", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(o, "100:ktk  101:wdk  102:krk  103:orck  99:gm1  9999:gm2", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	// 속도 : speed up
	static private void speed(object o) {
		if (o instanceof PcInstance) {
			PcInstance pc = (PcInstance) o;
			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 193), true);
			Haste.init(pc, 9999);
			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 751), true);
			Bravery.init(pc, 9999);
		}
	}
	
	// 리로드 : reload
	private static void reLoading(object paramobject, StringTokenizer paramStringTokenizer) {
		String str = paramStringTokenizer.nextToken();
		Connection localConnection = null;
		int i = 1;
		try {
			localConnection = DatabaseConnection.getLineage();
			// 서버설정 : lineage.config
			if (str.equals("config")) {
				Lineage.init(); // Gm.init();
				// 공지사항 : notice.txt
			} else if (str.equals("notice")) {
				NoticeController.init();
				// 엔피씨 : npc table
			} else if (str.equals("npc")) {
				NpcDatabase.init(localConnection);
				// 몬스터 : monster table
			} else if (str.equals("monster")) {
				MonsterDatabase.init(localConnection);
				// 아이템 : item table
			} else if (str.equals("item")) {
				ItemDatabase.init(localConnection);
				// 세트템 : item_setoption table
			} else if (str.equals("set")) {
				ItemSetoptionDatabase.init(localConnection);
				// 드랍 : monster_drop table
			} else if (str.equals("drop")) {
				MonsterDropDatabase.DropInit();
				MonsterDropDatabase.init(localConnection);
				// 상점 : npc_shop table
			} else if (str.equals("shop")) {
				NpcShopDatabase.ShopInit();
				NpcShopDatabase.init(localConnection);
				// 박스 : item_bundle table
			} else if (str.equals("bundle")) {
				ItemBundleDatabase.init(localConnection);
				// 보스 : monster_spawnlist_boss table
			} else if (str.equals("boss")) {
				MonsterBossSpawnlistDatabase.init(localConnection);
				// 공성 : kingdom table
			} else if (str.equals("kingdom")) {
				KingdomController.init(localConnection);
				// 변신 : poly table
			} else if (str.equals("poly")) {
				PolyDatabase.init(localConnection);
				// 스킬 : skill table
			} else if (str.equals("skill")) {
				SkillDatabase.init(localConnection);
				// 서먼 : summon_list table
			} else if (str.equals("summon")) {
				SummonListDatabase.init(localConnection);
				// 공지사항 : server_notice table
			} else if (str.equals("servernotice")) {
				ServerNoticeDatabase.init(localConnection);
				// 밴아이피 : bad_ip table
			} else if (str.equals("badip")) {
				BadIpDatabase.init(localConnection);
				// 아이템스킬: item_skill table
			} else if (str.equals("itemskill")) {
				ItemSkillDatabase.init(localConnection);
				// 몬스터스킬 : monster_skill table
			} else if (str.equals("mobskill")) {
				MonsterSkillDatabase.init(localConnection);
				// 텔포홈: teleport_home table
			} else if (str.equals("tphome")) {
				TeleportHomeDatabase.init(localConnection);
				// 텔포리셋 : teleport_reset table
			} else if (str.equals("tpreset")) {
				TeleportResetDatabase.init(localConnection);
				// 엔피씨텔포 : npc_teleport table
			} else if (str.equals("tpnpc")) {
				NpcTeleportDatabase.init(localConnection);
				// 아이템텔포 : item_teleport table
			} else if (str.equals("tpitem")) {
				ItemTeleportDatabase.init(localConnection);
				
				// below not use commands
//			} else if (str.equals("매입")) {
//				ItemShopDatabase.init(localConnection);
//			} else if (str.equals("서버메세지")) {
//				ServerMessageDatabase.init(localConnection);
//			} else if (str.equals("금지어")) {
//				BanWordDatabase.init(localConnection);
//			} else if (str.equals("레벨보상")) {
//				QuestPresentDatabase.init(localConnection);	
//			} else if (str.equals("레벨패널티")) {
//				ExpPaneltyDatabase.init(localConnection);
//			} else if (str.equals("퀴즈")) {
//				QuizQuestionDatabase.init(localConnection);
//			} else if (str.equals("아이템인챈")) {
//				ItemEnchantDatabase.init(localConnection);
//			} else if (str.equals("포인트상점")) {
//				NpcShopPointDatabase.init(localConnection);
//			} else if (str.equals("포인트드랍")) {
//				MonsterDropPointDatabase.init(localConnection);
//			} else if (str.equals("허수아비")) {
//				CrackerPresentDatabase.init(localConnection);
				
			} else {
				i = 0;
			}
			if (i != 0)
				ChattingController.toChatting(paramobject,
						new StringBuilder().append(str).append(" : reload completed").toString(), 20);
			else
				ChattingController.toChatting(paramobject, "This Reload does not exist.", 20);
		} catch (Exception localException) {
			lineage.share.System.printf("%s : toReLoad(object o, StringTokenizer st)\r\n",
					new Object[] { CommandController.class.toString() });
			lineage.share.System.println(localException);
		} finally {
			DatabaseConnection.close(localConnection);
		}
	}

	// 레벨 : level present
	static private void toLevel(object o, StringTokenizer st) {
		try {
			PcInstance targetPc = World.findPc(st.nextToken());
			int lv = Integer.parseInt(st.nextToken());
			if (targetPc == null) {
				return;
			} else {
				Exp e = null;
				double Exp = 0;
				if (targetPc.getLevel() < lv) {
					for (int i = targetPc.getLevel(); i < lv; i++) {
						e = ExpDatabase.find(i);
						Exp = e.getBonus();
						targetPc.setExp(Exp);
					}
				} else {
					for (int i = targetPc.getLevel(); i > lv; i--) {
						e = ExpDatabase.find(i);
						Exp = e.getExp();
						CharacterController.toExpDown(targetPc, Exp);
					}
				}
				ChattingController.toChatting(o, targetPc.getName() + "님의 레벨  [" + lv + "] 설정 완료", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, ".level  charname  level", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 몹정리.
	 * @param o
	 */
	static private void toClearMonster(object o) {
		for (object inside_o : o.getInsideList()) {
			if (!inside_o.isDead() && inside_o instanceof MonsterInstance && inside_o.getSummon() == null) {
				MonsterInstance mon = (MonsterInstance) inside_o;
				DamageController.toDamage((Character) o, mon, mon.getTotalHp(), Lineage.ATTACK_TYPE_MAGIC);
			}
		}
		ChattingController.toChatting(o, "몬스터 정리 완료.", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 셧다운.
	 * @param o
	 * @param st
	 */
	static private void toShutdown(object o, StringTokenizer st){
		Shutdown.getInstance();
		Shutdown.shutdown_delay = Integer.valueOf(st.nextToken());
		System.exit(0);
	}

	/**
	 * 채금.
	 *  : ScreenRenderComposite 에서 사용중
	 * @param o
	 * @param st
	 */
	static public void toChattingClose(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());
		if (pc != null) {
			int time = Integer.valueOf(st.nextToken());
			ChattingClose.init(pc, time);
			if (o != null)
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 287, pc.getName())); // %0의 채팅을 금지시켰습니다.
		}
	}

	/**
	 * 스킬 올마
	 * @param o
	 * @param st
	 */
	static private void toSkillAllMaster(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());
		if (pc != null) {
			switch (pc.getClassType()) {
			case Lineage.LINEAGE_CLASS_ROYAL:
				// 1~3
				for (int i = 1; i < 4; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				// 군주전용 스킬
				for (int i = 15; i < 16; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				// 1
				for (int i = 1; i < 2; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				// 기사전용 스킬
				for (int i = 11; i < 13; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				// 1~6
				for (int i = 1; i < 7; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				// 요정전용 스킬
				for (int i = 17; i < 23; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				break;
			case Lineage.LINEAGE_CLASS_WIZARD:
				// 1~10
				for (int i = 1; i < 11; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				break;
			case Lineage.LINEAGE_CLASS_DARKELF:
				// 1~2
				for (int i = 1; i < 3; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				// 다크엘프전용 스킬
				for (int i = 13; i < 15; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);

				break;
			case Lineage.LINEAGE_CLASS_DRAGONKNIGHT:
				break;
			case Lineage.LINEAGE_CLASS_BLACKWIZARD:
				break;
			}
			SkillController.sendList(pc);

			ChattingController.toChatting(o, "스킬올마 완료.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * .몹드랍
	 * @param o
	 * @param st
	 */
	static private void monsterDrop(object o, StringTokenizer st) {
		try {
			long l1 = java.lang.System.currentTimeMillis() / 1000L;
			if (o.getDelaytime() + 5L > l1) {
				long l2 = o.getDelaytime() + 5L - l1;
				ChattingController.toChatting(o, new StringBuilder().append(l2).append("초 간의 대기 시간이 필요합니다.").toString(), 	Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			String str1 = st.nextToken();
			Connection localConnection = null;
			PreparedStatement localPreparedStatement = null;
			ResultSet localResultSet = null;
			try {
				localConnection = DatabaseConnection.getLineage();
				localPreparedStatement = localConnection
						.prepareStatement("SELECT * FROM monster_drop WHERE REPLACE(monster_name, ' ', '') = ?");
				localPreparedStatement.setString(1, str1);
				localResultSet = localPreparedStatement.executeQuery();
				ChattingController.toChatting(o, Common.HELPER_LINE, Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, new StringBuilder().append("몬스터 명 : ").append(str1).toString(),
						Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, Common.HELPER_LINE, Lineage.CHATTING_MODE_MESSAGE);
				while (localResultSet.next()) {
					String str2 = "";
					switch (localResultSet.getInt("item_bress")) {
					case 0:
						str2 = "(축복)";
						break;
					case 2:
						str2 = "(저주)";
					}
					ChattingController.toChatting(o,
							new StringBuilder().append("아이템 명 : ").append(str2)
									.append(localResultSet.getString("item_name")).toString(),
							Lineage.CHATTING_MODE_MESSAGE);
				}
				ChattingController.toChatting(o, Common.HELPER_LINE, Lineage.CHATTING_MODE_MESSAGE);
			} catch (Exception localException2) {
				lineage.share.System.printf("%s : monsterDrop(object o, StringTokenizer st)\r\n",
						new Object[] { CommandController.class.toString() });
				lineage.share.System.println(localException2);
			} finally {
				DatabaseConnection.close(localConnection, localPreparedStatement, localResultSet);
			}
			o.setDelaytime(l1);
		} catch (Exception localException1) {
			if (o != null)
				ChattingController.toChatting(o, ".몹   몬스터", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * .템드랍
	 * @param o
	 * @param st
	 */
	static private void dropItem(object o, StringTokenizer st) {
		try {
			long l1 = java.lang.System.currentTimeMillis() / 1000L;
			if (o.getDelaytime() + 5L > l1) {
				long l2 = o.getDelaytime() + 5L - l1;
				ChattingController.toChatting(o, new StringBuilder().append(l2).append("초 간의 대기 시간이 필요합니다.").toString(), 	Lineage.CHATTING_MODE_MESSAGE);
				return;
			}
			String str = st.nextToken();
			Connection localConnection = null;
			PreparedStatement localPreparedStatement = null;
			ResultSet localResultSet = null;
			try {
				localConnection = DatabaseConnection.getLineage();
				localPreparedStatement = localConnection.prepareStatement(new StringBuilder()
						.append("SELECT * FROM monster_drop WHERE REPLACE(item_name, ' ', '') like '%").append(str)
						.append("%' group by monster_name").toString());
				localResultSet = localPreparedStatement.executeQuery();
				ChattingController.toChatting(o, Common.HELPER_LINE, Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, new StringBuilder().append("아이템 명 : ").append(str).toString(),
						Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, Common.HELPER_LINE, Lineage.CHATTING_MODE_MESSAGE);
				while (localResultSet.next())
					ChattingController
							.toChatting(o,
									new StringBuilder().append("몬스터 명 : ")
											.append(localResultSet.getString("monster_name")).toString(),
									Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, Common.HELPER_LINE, Lineage.CHATTING_MODE_MESSAGE);
			} catch (Exception localException2) {
				lineage.share.System.printf("%s : dropItemdropItem(object o, StringTokenizer st)\r\n",
						new Object[] { CommandController.class.toString() });
				lineage.share.System.println(localException2);
			} finally {
				DatabaseConnection.close(localConnection, localPreparedStatement, localResultSet);
			}
			o.setDelaytime(l1);
		} catch (Exception localException1) {
			if (o != null)
				ChattingController.toChatting(o, ".템   아이템", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * 차단.
	 *  : ScreenRenderComposite 에서 사용중
	 * @param o
	 * @param st
	 */
	@SuppressWarnings("resource")
	static public void toBan(object o, StringTokenizer st){
		boolean find = false;			// 찾앗는지 여부
		boolean account = false;		// 계정명인지 여부
		String value = st.nextToken().toLowerCase();	// 검색할 명
		
		// 케릭 찾기.
		Connection con = null;
		PreparedStatement stt = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			stt = con.prepareStatement("SELECT * FROM characters WHERE LOWER(name)=?");
			stt.setString(1, value);
			rs = stt.executeQuery();
			find = rs.next();
		} catch (Exception e) {
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, stt, rs);
		}
		// 계정 찾기.
		if(find == false){
			try {
				con = DatabaseConnection.getLineage();
				stt = con.prepareStatement("SELECT * FROM accounts WHERE LOWER(id)=?");
				stt.setString(1, value);
				rs = stt.executeQuery();
				find = rs.next();
				account = find;
			} catch (Exception e) {
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, stt, rs);
			}
		}
		
		// 찾았다면
		if(find){
			try {
				con = DatabaseConnection.getLineage();
				
				if(account){
					// 계정 차단.
					stt = con.prepareStatement("UPDATE accounts SET block_date=? WHERE LOWER(id)=?");
					stt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
					stt.setString(2, value);
					stt.executeUpdate();
					
					ChattingController.toChatting(o, "계정 차단.", 20);
				}else{
					// 케릭 차단.
					stt = con.prepareStatement("UPDATE characters SET block_date=? WHERE LOWER(name)=?");
					stt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
					stt.setString(2, value);
					stt.executeUpdate();
					
					ChattingController.toChatting(o, "케릭 차단.", 20);
				}
				
				// 접속된 사용자 차단.
				PcInstance find_use = null;
				for(PcInstance pc : World.getPcList()){
					if(account){
						if(pc.getClient().getAccountId().equalsIgnoreCase(value)){
							find_use = pc;
							break;
						}
					}else{
						if(pc.getName().equalsIgnoreCase(value)){
							find_use = pc;
							break;
						}
					}
				}
				// 찾은 사용자 종료.
				if(find_use != null)
					find_use.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
			} catch (Exception e) {
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, stt);
			}
			
			ChattingController.toChatting(o, "차단 완료.", 20);
		}
	}

	/**
	 * 청소
	 *  : GuiMain 에서 사용중
	 * @param o
	 */
	static public void toWorldItemClear(object o) {
		World.clearWorldItem();
		if (o != null)
			ChattingController.toChatting(o, "Ground clean completed", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 올버프
	 *  ; GuiMain 에서 사용중
	 * @param o
	 */
	static public void toBuffAll(object o) {
		for (PcInstance pc : World.getPcList())
			toBuff(pc);
		if (o != null)
			ChattingController.toChatting(o, "All buff completed", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 전체버프
	 * 
	 * @param o
	 */
	static public void toGmBuffAll(object o) {
		for (PcInstance pc : World.getPcList())
			toGmBuff(pc);

		if (o != null)
			ChattingController.toChatting(o, "Server user all buff completed", 20);
	}
	
	/**
	 * 버프
	 *  : ScreenRenderComposite 에서 사용중
	 * @param o
	 * @param st
	 */
	static public void toBuff(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());
		if (pc != null) {
			toBuff(pc);
			if (o != null)
				ChattingController.toChatting(o, "Private buff completed", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 중복 코드 방지용.
	 *  : 여기서 사용중.
	 *  : PcInstnace.toWorldJoin 사용중.
	 * @param o
	 */
	static public void toBuff(object o) {
		EnchantDexterity.onBuff(o, SkillDatabase.find(4, 1));
		EnchantMighty.onBuff(o, SkillDatabase.find(6, 1));
		Haste.onBuff(o, SkillDatabase.find(7, 5));
//		AbsoluteBarrier.onBuff(o, SkillDatabase.find(10, 5), 60);
//		ShiningAura.onBuff(o, SkillDatabase.find(15, 2));
		if (Lineage.server_version >= 182) { // 만약, 서버버전이 1.82라면 아래의 버프도 시전.
			BlessWeapon.onBuff(o, SkillDatabase.find(6, 7));
			EarthSkin.onBuff(o, SkillDatabase.find(19, 6));
		}
	}
	
	/**
	 * 전체버프
	 * @param o
	 */
	static public void toGmBuff(object o) {
		EnchantDexterity.onBuff(o, SkillDatabase.find(4, 1));
		EnchantMighty.onBuff(o, SkillDatabase.find(6, 1));
		Haste.onBuff(o, SkillDatabase.find(7, 5));
		BlessWeapon.onBuff(o, SkillDatabase.find(6, 7));
		IronSkin.onBuff(o, SkillDatabase.find(21, 7));
		BurningWeapon.onBuff(o, SkillDatabase.find(21, 2));
		ShiningAura.onBuff(o, SkillDatabase.find(15, 2));
		GlowingAura.onBuff(o, SkillDatabase.find(15, 1));
	}

	/**
	 * 아이템 생성
	 * @param o
	 * @param st
	 */
	static private void toItem(object o, StringTokenizer st) {
		try {
			String name = st.nextToken();
			int count = 1;
			if (st.hasMoreTokens()) {
				count = Integer.valueOf(st.nextToken());
			}
			int enchant = 0;
			if (st.hasMoreTokens()) {
				enchant = Integer.valueOf(st.nextToken());
			}
			int bress = 1;
			if (st.hasMoreTokens()) {
				bress = Integer.valueOf(st.nextToken());
			}
			int name_id = -1;
			Item i = null;
			try {
				name_id = Integer.valueOf(name);
				i = ItemDatabase.find(name_id);
			} catch (NumberFormatException e) {
				name_id = ItemDatabase.findItemByNameWithoutSpace(name);
				if (name_id == -1) {
					ChattingController.toChatting(o, "item is not found", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
				i = ItemDatabase.findItem(name);
			}
			if (i != null) {
				// 메모리 생성 및 초기화.
				ItemInstance temp = ItemDatabase.newInstance(i);
				temp.setCount(count);
				temp.setEnLevel(enchant);
				temp.setBress(bress);
				temp.setDefinite(true);
				// 인벤에 등록처리.
				o.getInventory().append(temp, count);
				// 메모리 재사용.
				ItemDatabase.setPool(temp);
				// 알림.
				ChattingController.toChatting(o, "item created", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, "item not-existing", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, ".item  name  count  enchant  bless", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 몬스터 생성.
	 * @param o
	 * @param st
	 */
	static private void toMonster(object o, StringTokenizer st) {
		try {
			String name = st.nextToken();
			int count = 1;
			if (st.hasMoreTokens()) {
				count = Integer.valueOf(st.nextToken());
			}
			int randomRange = 0;
			if (st.hasMoreTokens()) {
				randomRange = Integer.valueOf(st.nextToken(), 10);
			}

			for (int i = 0; i < count; ++i) {
				MonsterInstance mi = MonsterSpawnlistDatabase.newInstance(MonsterDatabase.findMonster(name));
				int getX = o.getX() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange);
				int getY = o.getY() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange);
				if (mi != null) {
					mi.setHomeX(getX);
					mi.setHomeY(getY);
					mi.setHomeMap(o.getMap());
					mi.setHeading(o.getHeading());
					mi.toTeleport(getX, getY, o.getMap(), false);
					mi.readDrop();

					AiThread.append(mi);
				} else {
					ChattingController.toChatting(o, "monster is not found", Lineage.CHATTING_MODE_MESSAGE);
					return;
				}
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, ".monster  name  count  range", Lineage.CHATTING_MODE_MESSAGE);
		}
		ChattingController.toChatting(o, "monster  created.", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * 소환.
	 * @param o
	 * @param st
	 */
	static private void toCall(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());
		if (pc != null) {
			pc.toPotal(o.getX(), o.getY(), o.getMap());

			ChattingController.toChatting(o, "User recall completed", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * 출두.
	 * @param o
	 * @param st
	 */
	static private void toGo(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());
		if (pc != null) {
			o.toTeleport(pc.getX(), pc.getY(), pc.getMap(), false);

			ChattingController.toChatting(o, "Go to user completed", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
}
