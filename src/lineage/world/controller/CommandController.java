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
	 * ????????? ?????? ??????
	 * @param pc	: ????????? ?????????
	 * @param cmd	: ?????????
	 * @return		: ????????? ?????? ?????? ??????
	 */
	static public boolean toCommand(object o, String cmd){
		if(o == null)
			return false;

		try {
			StringTokenizer st = new StringTokenizer(cmd);
			String key = st.nextToken(); //????????????
			
			Object is_check = PluginController.init(CommandController.class, "toCommand", o, cmd, st);
			if(is_check!=null && (Boolean)is_check)
			return true;
			
			// ?????? ????????? ??????
			// ???????????? ????????? ??????
			if(cmd.startsWith("..")){ //?????? ???????????? ?????????
				o.toTeleport(o.getX(), o.getY(), o.getMap(), false);
				ChattingController.toChatting(o, "????????? ???????????? ???????????????.", Lineage.CHATTING_MODE_MESSAGE);
		    	return true;
		    // ???????????? ????????? ???
	    	
		    // ????????? ??????
			} else if (cmd.startsWith(".?????????")) {
				ChattingController.toChatting(o, "<????????? ?????????>", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "???   ???   ??????   ??????   ..", Lineage.CHATTING_MODE_MESSAGE);
				return true;
			// ????????? ???
			
			}else if(cmd.startsWith(".??????")) {
				String now_date = String.format("?????? ?????? ?????? : %02d:%02d:%02d", ServerDatabase.getLineageTimeHour(), ServerDatabase.getLineageTimeMinute(), ServerDatabase.getLineageTimeSeconds());
				ChattingController.toChatting(o, String.format("%s", now_date), Lineage.CHATTING_MODE_MESSAGE);
				return true;
				
				
			// ??? ???????????? ?????? '???' ????????????
//			} else if (cmd.startsWith(".???")) {
//				if (o instanceof Character) {
//					Character cha = (Character) o;
//					ChattingController.toChatting(cha, String.format("hp???(%dms, %d), mp???(%dms, %d)", cha.getHpTime(),
//							cha.hpTic(), cha.getMpTime(), cha.mpTic()), Lineage.CHATTING_MODE_MESSAGE);
//					return true;
//				}
			    
			// ??? ???????????? ?????? '????????????' ????????????
//			} else if (cmd.startsWith(".????????????")) {
//				o.setAutoPickup(!o.isAutoPickup());
//				ChattingController.toChatting(o, String.format("??????????????? %s????????? ???????????????.", o.isAutoPickup() ? "" : "???"),
//						Lineage.CHATTING_MODE_MESSAGE);
//				return true;

			// ??? ???????????? ?????? '?????? ???' ????????????
//			} else if(cmd.startsWith(".?????? ???") || (cmd.startsWith("-?????? ???") || (cmd.startsWith(".????????? ???") || (cmd.startsWith("-????????? ???"))))) {
//				if (o instanceof Character) {
//					Character cha = (Character) o;
//			cha.toSender(S_Ability.clone(BasePacketPooling.getPool(S_Ability.class), 3, true));  // 3?????? 2??? ?????? ?????? 
//					ChattingController.toChatting(o, String.format("??????(?????????) ????????? ????????? ???????????????."), Lineage.CHATTING_MODE_MESSAGE);
//					return true;
//				}
				
			// ??? ???????????? ?????? '?????? ???' ????????????
//			} else if(cmd.startsWith(".?????? ???") || (cmd.startsWith("-?????? ???") || (cmd.startsWith(".????????? ???") || (cmd.startsWith("-????????? ???"))))) {
//				if (o instanceof Character) {
//					Character cha = (Character) o;
//			cha.toSender(S_Ability.clone(BasePacketPooling.getPool(S_Ability.class), 3, false));  // 3?????? 2??? ?????? ?????? 
//					ChattingController.toChatting(o, String.format("??????(?????????) ????????? ???????????? ???????????????."), Lineage.CHATTING_MODE_MESSAGE);
//					return true;
//				}
			
			// ??? ???????????? ?????? '????????????' ????????????
//			} else if (cmd.startsWith(".????????????")) {
//				Buff buff = BuffController.find(o);
//				if (buff != null) {
//					for (BuffInterface b : buff.getList()) {
//						if (b.getSkill() != null)
//							ChattingController.toChatting(o,
//									String.format("%s : %d ?????????.", b.getSkill().getName(), b.getTime()), Lineage.CHATTING_MODE_MESSAGE);
//					}
//					return true;
//				}

			// ??????
			} else if(cmd.startsWith(".??????")) {
				// ??????.
				o.setHpbar( !o.isHpbar() );
				ChattingController.toChatting(o, String.format("?????? ?????? ??? : %s?????????", o.isHpbar() ? "" : "???"), 20);
				// ??????.
				o.toSender(S_ObjectHitratio.clone(BasePacketPooling.getPool(S_ObjectHitratio.class), (Character)o, o.isHpbar()));
				return true;
			
			// ?????????
			} else if (cmd.startsWith(".???")) {
				monsterDrop(o, st);
				return true;
				
			// ?????????
			} else if (cmd.startsWith(".???")) {
				dropItem(o, st);
				return true;
				
			// ??? ???????????? ?????? '??????' ????????????
//			} else if (cmd.startsWith(".??????")) {
//				if (o.getLevel() >= 40) { // ?????? ??????
//					ChattingController.toChatting(o, "40?????? ?????? ?????? ???????????? ????????? ??? ????????????.", Lineage.CHATTING_MODE_MESSAGE);
//				} else {
//					lineage.world.controller.CommandController.toBuff(o);
//				}
//				return true;
			}

			// ????????? ????????? ??????
			// ????????? ????????? ?????? ??????
			if(o.getGm()==0)
				return false;
			if (cmd.startsWith(".gm")){
				ChattingController.toChatting(o, "-----------------< GM COMMANDS >-------------------", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "skillmaster  fake", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "mode  recall  block  clean  speed  gfx  allbuff  reload  serverbuff", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "action  go  level  res  home  monster  shutdown  openwt  openst", Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, "inven  move  chatblock  die  inven  item  mobkill  allcall", Lineage.CHATTING_MODE_MESSAGE);
				return true;
				
			// ?????? : user inventory investigate
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
			// ?????? ???
				
//			// ???????????? ??????
//			} else if (cmd.startsWith(".????????????")) {
//				try {
//					toInventoryDelete(o, st);
//				} catch (Exception localException61) {
//					ChattingController.toChatting(o,
//							new StringBuilder().append(TOKEN).append("???????????? charname").toString(), 20);
//				}
//				return true;
//			// ?????? ?????? ???
				
			// ?????? ?????? : server open waiting (packet close)
			} else if (cmd.startsWith(".openwt")) {
				try {
					toOpentime(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".openwt", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// ?????? ?????? : server open start(packet open)
			} else if (cmd.startsWith(".openst")) {
				try {
					toOpen(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".openst", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;	
			
			// ????????? : reload MySQL table & .conf file.
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
			// op ???
						
			// ?????? : move to location
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
				
			// t ??????	(?????????)
//			}else if(cmd.startsWith(".t")){
//				for(int i=0 ; i<1000 ; ++i){
//					o.toSender(S_BuffTrueTarget.clone(BasePacketPooling.getPool(S_BuffTrueTarget.class), o, "1234"), true);
//				}
//				return true;
			// t ???
				
			// ????????? : ground effect view
			} else if (cmd.startsWith(".gfx")) {
				int gfx = Integer.valueOf(st.nextToken());
				o.setGfx(gfx);
				o.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), o), true);
				return true;

			// ?????? : i don't know.
			} else if (cmd.startsWith(".mode")) {
				int mode = Integer.valueOf(st.nextToken());
				o.setGfxMode(mode);
				o.toSender(S_ObjectGfx.clone(BasePacketPooling.getPool(S_ObjectGfx.class), o), true);
				return true;
						
			// ?????? : frame action view
			} else if (cmd.startsWith(".action")) { // Felix: Replace Korean for Action later
				int action = Integer.valueOf(st.nextToken());
				o.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), o, action), true);
				return true;
						
			// ?????? : torch create > background_spawnlist table
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

			// ?????? : game master (me) speed up
			} else if (cmd.startsWith(".speed")) {
				try {
					speed(o);
					ChattingController.toChatting(o, "GM speed up", Lineage.CHATTING_MODE_MESSAGE);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".speed", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// ????????? : monster create
			} else if (cmd.startsWith(".monster")) {
				try {
					toMonster(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".mob name count range", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
						
			// ????????? : item create
			} else if (cmd.startsWith(".item")) {
				try {
					toItem(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".item name count enchant", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
						
			// ?????? : recall user
			} else if (cmd.startsWith(".recall")) {
				try {
					toCall(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".recall charname", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
							
			// ?????? : go to user
			} else if (cmd.startsWith(".go")) {
				try {
					toGo(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".go charname", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;

			// ????????? : me all buff
			} else if (cmd.startsWith(".allbuff")) {
				try {
					toBuffAll(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".allbuff", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
				
			// ?????? ?????? : server user all buff
			} else if (cmd.startsWith(".serverbuff")) {
				try {
					toGmBuffAll(o);
					ChattingController.toChatting(o, "Str, Dex, Haste, Bless, Iron, Bur, Shining, glowing", Lineage.CHATTING_MODE_MESSAGE);
					ChattingController.toChatting(o, "???????????? ??????????????? ?????????????????????.", Lineage.CHATTING_MODE_MESSAGE);
				} catch (Exception e) {

				}
			// ?????? ?????? ???
						
			// ??? : user fake count
			} else if (cmd.startsWith(".fake")) {
				try {
					int count = Integer.parseInt(st.nextToken());
					FakeUserCount.getInstance().gmFakeUserCount = count;
				} catch (Exception e) {
					ChattingController.toChatting(o, ".???  number", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
						
			// ?????? : user block
			} else if (cmd.startsWith(".block")) {
				try {
					toBan(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".block  charname", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// ?????? ????????? : skill master gift
			} else if (cmd.startsWith(".skillmaster")) {
				try {
					toSkillAllMaster(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".skillmaster  charname", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
						
			// ?????? : user chat block
			} else if (cmd.startsWith(".chatblock")) {
				try {
					toChattingClose(o, st);
				} catch (Exception e) {
					ChattingController.toChatting(o, ".chatblock  charname time", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// ?????? : server ground clean
			} else if (cmd.startsWith(".clean")) {
				try {
					toWorldItemClear(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".clean", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// ????????? : server shutdown
			} else if (cmd.startsWith(".shutdown")) {
				try {
					toShutdown(o, st);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".shutdown  min", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// ????????? : monster all kill
			} else if (cmd.startsWith(".mobkill")) {
				try {
					toClearMonster(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".mobkill", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
			
			// ?????? : resurrection
			} else if (cmd.startsWith(".res")) {
				try {
					o.toRevival(o);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".res", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
		
			// ?????? : suicide me
			} else if (cmd.startsWith(".die")) {
				try {
					o.setNowHp(0);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".die", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;

			// ?????? ?????? : server user all call
			} else if (cmd.startsWith(".allcall")) {
				try {
					for (PcInstance pc : World.getPcList())
						pc.toTeleport(o.getX(), o.getY(), o.getMap(), true);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".allcall", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;

			// ?????? ?????? : level present
			} else if (cmd.startsWith(".level")) {
				try {
					toLevel(o, st);
				} catch (Exception e) {
					if (o != null)
						ChattingController.toChatting(o, ".level  charname level", Lineage.CHATTING_MODE_MESSAGE);
				}
				return true;
				
			// ?????? : home
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
			
			// ?????? ?????? : kingdom war check
			} else if (key.equalsIgnoreCase(".warck")) {
				try {
					toKingdomWarCheck(o, st);
				} catch (Exception localException69) {
					ChattingController.toChatting(o, new StringBuilder().append(TOKEN).append("warck").toString(), 20);
				}
				return true;
				
			// ?????? ?????? : kingdom war start
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
			
			// ?????? ?????? : kingdom war end
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
				
			// ?????? : invisibility mode, only me
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

	// ????????????
	public static void toKingdomWarCheck(object paramobject, StringTokenizer paramStringTokenizer) {
		for (Kingdom localKingdom : KingdomController.getKingdomList()) {
			if (localKingdom.isWar())
				ChattingController.toChatting(paramobject,
						String.format("%s : ????????? ??????????????????.", new Object[] { localKingdom.getName() }), 20);
			else
				ChattingController.toChatting(paramobject,
						String.format("%s : ??????????????? ????????????.", new Object[] { localKingdom.getName() }), 20);

		}
	}
	// ????????????
	public static void toKingdomWarStart(object paramobject, StringTokenizer paramStringTokenizer) {
		String str = paramStringTokenizer.nextToken();
		int i = 0;
		for (Kingdom localKingdom : KingdomController.getKingdomList()) {
			if (localKingdom.getName().trim().replaceAll(" ", "").equals(str)) {
				if (localKingdom.isWar()) {
					ChattingController.toChatting(paramobject,
							String.format("%s : ????????? ?????? ??????????????????.", new Object[] { str }), 20);
					return;
				}
				localKingdom.setWarDay(Calendar.getInstance().getTime().getTime());
				i = 1;
				if (paramobject != null)
					ChattingController.toChatting(paramobject, String.format("%s : ????????? ?????????????????????.", new Object[] { str }),
							20);
			}
		}
		if ((paramobject != null) && (i == 0))
			ChattingController.toChatting(paramobject, String.format("%s : ???????????? ?????? ??????????????????.", new Object[] { str }), 20);
	}
	// ????????????
	public static void toKingdomWarStop(object paramobject, StringTokenizer paramStringTokenizer) {
		String str = paramStringTokenizer.nextToken();
		int i = 0;
		for (Kingdom localKingdom : KingdomController.getKingdomList()) {
			if ((str.equals("all")) || (localKingdom.getName().trim().replaceAll(" ", "").equals(str)))
				if (!localKingdom.isWar()) {
					ChattingController.toChatting(paramobject,
							String.format("%s : ????????? ??????????????? ????????????.", new Object[] { localKingdom.getName() }), 20);
				} else {
					localKingdom.toStopWar(java.lang.System.currentTimeMillis());
					i = 1;
					if (paramobject != null)
						ChattingController.toChatting(paramobject,
								String.format("%s : ????????? ?????????????????????.", new Object[] { localKingdom.getName() }), 20);
				}
		}
		if ((paramobject != null) && (!str.equals("all")) && (i == 0))
			ChattingController.toChatting(paramobject, String.format("%s : ???????????? ?????? ??????????????????.", new Object[] { str }), 20);
	}

	// ?????? ?????? : server open wait
	private static void toOpentime(object o) {
		Lineage.rate_drop = 1;
		Lineage.rate_aden = 1;
		Lineage.level_max = 1;	
		Lineage.character_delete = false;
		Lineage.account_auto_create = true;
		// server status : open wait
		ChattingController.toChatting(o, String.format("?????? ??????: ?????? ??????"), Lineage.CHATTING_MODE_MESSAGE);

	}

	//?????? ?????? : server open start
	private static void toOpen(object o) {
		Lineage.rate_drop = 1;
		Lineage.rate_aden = 1;
		Lineage.level_max = 55;
		Lineage.character_delete = true;
		// server status : open start
		World.toSender(S_ObjectChatting.clone(BasePacketPooling.getPool(S_ObjectChatting.class), "?????? ??????: ?????? ??????"));
	}
	
//	// ????????????
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
//					new StringBuilder().append(str).append("???(???) ????????? ??????????????? ????????????.").toString(), 20);
//		}
//	}
	
	// ?????? : invisibility mode, only me
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
	
	// ?????? : home
	static private void comebackHome(object o, StringTokenizer st) {
		String text = st.nextToken();
		// ????????? ?????? : gludio town
		if (text.equalsIgnoreCase("1")) {
			o.toTeleport(32625, 32805, 4, false);
			// ????????? ?????? : woodbec town
		} else if (text.equalsIgnoreCase("2")) {
			o.toTeleport(32611, 33188, 4, false);
			// ?????? ?????? : kent town
		} else if (text.equalsIgnoreCase("3")) {
			o.toTeleport(33072, 32797, 4, false);
			// ?????? ?????? : kiran town
		} else if (text.equalsIgnoreCase("4")) {
			o.toTeleport(33429, 32814, 4, false);
			// ????????? ?????? : fire town
		} else if (text.equalsIgnoreCase("5")) {
			o.toTeleport(32745, 32443, 4, false);
			// ????????? ?????? : silver knight town
		} else if (text.equalsIgnoreCase("6")) {
			o.toTeleport(33081, 33394, 4, false);
			// ????????? ??? : talking island
		} else if (text.equalsIgnoreCase("7")) {
			o.toTeleport(32575, 32942, 0, false);
			// ?????? ??? : elf forest
		} else if (text.equalsIgnoreCase("8")) {
			o.toTeleport(33058, 32339, 4, false);
			// ????????? ????????? : slime race
		} else if (text.equalsIgnoreCase("9")) {
			o.toTeleport(32604, 32663, 4, false);
			// ???????????? : desert oasis
		} else if (text.equalsIgnoreCase("10")) {
			o.toTeleport(32860, 33250, 4, false);
			// ?????? ?????? (??? ??????) : drgon valley big bone
		} else if (text.equalsIgnoreCase("11")) {
			o.toTeleport(33385, 32350, 4, false);
			// ?????? ??? : kent kingdom
		} else if (text.equalsIgnoreCase("100")) {
			o.toTeleport(32736, 32784, 15, false);
			// ???????????? ??? : windawood kingdom
		} else if (text.equalsIgnoreCase("101")) {
			o.toTeleport(32735, 32784, 29, false);
			// ?????? ??? : kiran kingdom
		} else if (text.equalsIgnoreCase("102")) {
			o.toTeleport(32729, 32788, 52, false);
			// ?????? ?????? : orcrish kingdom
		} else if (text.equalsIgnoreCase("103")) {
			o.toTeleport(32809, 32276, 4, false);
			// ?????? : gm room 1
		} else if (text.equalsIgnoreCase("99")) {
			o.toTeleport(32738, 32794, 99, false);			
			// ?????? : gm room 2
		} else if (text.equalsIgnoreCase("9999")) {
			o.toTeleport(32739, 32796, 18, false);
		} else {
			ChattingController.toChatting(o, ".home  Num", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(o, "1:gd  2:wd  3:kt  4:kr  5:ft  6:st  7:tk", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(o, "8:ef  9:race  10:oasis  11:drgnv", Lineage.CHATTING_MODE_MESSAGE);
			ChattingController.toChatting(o, "100:ktk  101:wdk  102:krk  103:orck  99:gm1  9999:gm2", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	// ?????? : speed up
	static private void speed(object o) {
		if (o instanceof PcInstance) {
			PcInstance pc = (PcInstance) o;
			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 193), true);
			Haste.init(pc, 9999);
			pc.toSender(S_ObjectEffect.clone(BasePacketPooling.getPool(S_ObjectEffect.class), pc, 751), true);
			Bravery.init(pc, 9999);
		}
	}
	
	// ????????? : reload
	private static void reLoading(object paramobject, StringTokenizer paramStringTokenizer) {
		String str = paramStringTokenizer.nextToken();
		Connection localConnection = null;
		int i = 1;
		try {
			localConnection = DatabaseConnection.getLineage();
			// ???????????? : lineage.config
			if (str.equals("config")) {
				Lineage.init(); // Gm.init();
				// ???????????? : notice.txt
			} else if (str.equals("notice")) {
				NoticeController.init();
				// ????????? : npc table
			} else if (str.equals("npc")) {
				NpcDatabase.init(localConnection);
				// ????????? : monster table
			} else if (str.equals("monster")) {
				MonsterDatabase.init(localConnection);
				// ????????? : item table
			} else if (str.equals("item")) {
				ItemDatabase.init(localConnection);
				// ????????? : item_setoption table
			} else if (str.equals("set")) {
				ItemSetoptionDatabase.init(localConnection);
				// ?????? : monster_drop table
			} else if (str.equals("drop")) {
				MonsterDropDatabase.DropInit();
				MonsterDropDatabase.init(localConnection);
				// ?????? : npc_shop table
			} else if (str.equals("shop")) {
				NpcShopDatabase.ShopInit();
				NpcShopDatabase.init(localConnection);
				// ?????? : item_bundle table
			} else if (str.equals("bundle")) {
				ItemBundleDatabase.init(localConnection);
				// ?????? : monster_spawnlist_boss table
			} else if (str.equals("boss")) {
				MonsterBossSpawnlistDatabase.init(localConnection);
				// ?????? : kingdom table
			} else if (str.equals("kingdom")) {
				KingdomController.init(localConnection);
				// ?????? : poly table
			} else if (str.equals("poly")) {
				PolyDatabase.init(localConnection);
				// ?????? : skill table
			} else if (str.equals("skill")) {
				SkillDatabase.init(localConnection);
				// ?????? : summon_list table
			} else if (str.equals("summon")) {
				SummonListDatabase.init(localConnection);
				// ???????????? : server_notice table
			} else if (str.equals("servernotice")) {
				ServerNoticeDatabase.init(localConnection);
				// ???????????? : bad_ip table
			} else if (str.equals("badip")) {
				BadIpDatabase.init(localConnection);
				// ???????????????: item_skill table
			} else if (str.equals("itemskill")) {
				ItemSkillDatabase.init(localConnection);
				// ??????????????? : monster_skill table
			} else if (str.equals("mobskill")) {
				MonsterSkillDatabase.init(localConnection);
				// ?????????: teleport_home table
			} else if (str.equals("tphome")) {
				TeleportHomeDatabase.init(localConnection);
				// ???????????? : teleport_reset table
			} else if (str.equals("tpreset")) {
				TeleportResetDatabase.init(localConnection);
				// ??????????????? : npc_teleport table
			} else if (str.equals("tpnpc")) {
				NpcTeleportDatabase.init(localConnection);
				// ??????????????? : item_teleport table
			} else if (str.equals("tpitem")) {
				ItemTeleportDatabase.init(localConnection);
				
				// below not use commands
//			} else if (str.equals("??????")) {
//				ItemShopDatabase.init(localConnection);
//			} else if (str.equals("???????????????")) {
//				ServerMessageDatabase.init(localConnection);
//			} else if (str.equals("?????????")) {
//				BanWordDatabase.init(localConnection);
//			} else if (str.equals("????????????")) {
//				QuestPresentDatabase.init(localConnection);	
//			} else if (str.equals("???????????????")) {
//				ExpPaneltyDatabase.init(localConnection);
//			} else if (str.equals("??????")) {
//				QuizQuestionDatabase.init(localConnection);
//			} else if (str.equals("???????????????")) {
//				ItemEnchantDatabase.init(localConnection);
//			} else if (str.equals("???????????????")) {
//				NpcShopPointDatabase.init(localConnection);
//			} else if (str.equals("???????????????")) {
//				MonsterDropPointDatabase.init(localConnection);
//			} else if (str.equals("????????????")) {
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

	// ?????? : level present
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
				ChattingController.toChatting(o, targetPc.getName() + "?????? ??????  [" + lv + "] ?????? ??????", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, ".level  charname  level", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * ?????????.
	 * @param o
	 */
	static private void toClearMonster(object o) {
		for (object inside_o : o.getInsideList()) {
			if (!inside_o.isDead() && inside_o instanceof MonsterInstance && inside_o.getSummon() == null) {
				MonsterInstance mon = (MonsterInstance) inside_o;
				DamageController.toDamage((Character) o, mon, mon.getTotalHp(), Lineage.ATTACK_TYPE_MAGIC);
			}
		}
		ChattingController.toChatting(o, "????????? ?????? ??????.", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * ?????????.
	 * @param o
	 * @param st
	 */
	static private void toShutdown(object o, StringTokenizer st){
		Shutdown.getInstance();
		Shutdown.shutdown_delay = Integer.valueOf(st.nextToken());
		System.exit(0);
	}

	/**
	 * ??????.
	 *  : ScreenRenderComposite ?????? ?????????
	 * @param o
	 * @param st
	 */
	static public void toChattingClose(object o, StringTokenizer st) {
		PcInstance pc = World.findPc(st.nextToken());
		if (pc != null) {
			int time = Integer.valueOf(st.nextToken());
			ChattingClose.init(pc, time);
			if (o != null)
				o.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 287, pc.getName())); // %0??? ????????? ?????????????????????.
		}
	}

	/**
	 * ?????? ??????
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
				// ???????????? ??????
				for (int i = 15; i < 16; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				break;
			case Lineage.LINEAGE_CLASS_KNIGHT:
				// 1
				for (int i = 1; i < 2; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				// ???????????? ??????
				for (int i = 11; i < 13; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				break;
			case Lineage.LINEAGE_CLASS_ELF:
				// 1~6
				for (int i = 1; i < 7; ++i)
					for (int j = 0; j < 8; ++j)
						SkillController.append(pc, i, j, false);
				// ???????????? ??????
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
				// ?????????????????? ??????
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

			ChattingController.toChatting(o, "???????????? ??????.", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * .?????????
	 * @param o
	 * @param st
	 */
	static private void monsterDrop(object o, StringTokenizer st) {
		try {
			long l1 = java.lang.System.currentTimeMillis() / 1000L;
			if (o.getDelaytime() + 5L > l1) {
				long l2 = o.getDelaytime() + 5L - l1;
				ChattingController.toChatting(o, new StringBuilder().append(l2).append("??? ?????? ?????? ????????? ???????????????.").toString(), 	Lineage.CHATTING_MODE_MESSAGE);
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
				ChattingController.toChatting(o, new StringBuilder().append("????????? ??? : ").append(str1).toString(),
						Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, Common.HELPER_LINE, Lineage.CHATTING_MODE_MESSAGE);
				while (localResultSet.next()) {
					String str2 = "";
					switch (localResultSet.getInt("item_bress")) {
					case 0:
						str2 = "(??????)";
						break;
					case 2:
						str2 = "(??????)";
					}
					ChattingController.toChatting(o,
							new StringBuilder().append("????????? ??? : ").append(str2)
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
				ChattingController.toChatting(o, ".???   ?????????", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * .?????????
	 * @param o
	 * @param st
	 */
	static private void dropItem(object o, StringTokenizer st) {
		try {
			long l1 = java.lang.System.currentTimeMillis() / 1000L;
			if (o.getDelaytime() + 5L > l1) {
				long l2 = o.getDelaytime() + 5L - l1;
				ChattingController.toChatting(o, new StringBuilder().append(l2).append("??? ?????? ?????? ????????? ???????????????.").toString(), 	Lineage.CHATTING_MODE_MESSAGE);
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
				ChattingController.toChatting(o, new StringBuilder().append("????????? ??? : ").append(str).toString(),
						Lineage.CHATTING_MODE_MESSAGE);
				ChattingController.toChatting(o, Common.HELPER_LINE, Lineage.CHATTING_MODE_MESSAGE);
				while (localResultSet.next())
					ChattingController
							.toChatting(o,
									new StringBuilder().append("????????? ??? : ")
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
				ChattingController.toChatting(o, ".???   ?????????", Lineage.CHATTING_MODE_MESSAGE);
		}
	}
	
	/**
	 * ??????.
	 *  : ScreenRenderComposite ?????? ?????????
	 * @param o
	 * @param st
	 */
	@SuppressWarnings("resource")
	static public void toBan(object o, StringTokenizer st){
		boolean find = false;			// ???????????? ??????
		boolean account = false;		// ??????????????? ??????
		String value = st.nextToken().toLowerCase();	// ????????? ???
		
		// ?????? ??????.
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
		// ?????? ??????.
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
		
		// ????????????
		if(find){
			try {
				con = DatabaseConnection.getLineage();
				
				if(account){
					// ?????? ??????.
					stt = con.prepareStatement("UPDATE accounts SET block_date=? WHERE LOWER(id)=?");
					stt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
					stt.setString(2, value);
					stt.executeUpdate();
					
					ChattingController.toChatting(o, "?????? ??????.", 20);
				}else{
					// ?????? ??????.
					stt = con.prepareStatement("UPDATE characters SET block_date=? WHERE LOWER(name)=?");
					stt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
					stt.setString(2, value);
					stt.executeUpdate();
					
					ChattingController.toChatting(o, "?????? ??????.", 20);
				}
				
				// ????????? ????????? ??????.
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
				// ?????? ????????? ??????.
				if(find_use != null)
					find_use.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
			} catch (Exception e) {
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, stt);
			}
			
			ChattingController.toChatting(o, "?????? ??????.", 20);
		}
	}

	/**
	 * ??????
	 *  : GuiMain ?????? ?????????
	 * @param o
	 */
	static public void toWorldItemClear(object o) {
		World.clearWorldItem();
		if (o != null)
			ChattingController.toChatting(o, "Ground clean completed", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * ?????????
	 *  ; GuiMain ?????? ?????????
	 * @param o
	 */
	static public void toBuffAll(object o) {
		for (PcInstance pc : World.getPcList())
			toBuff(pc);
		if (o != null)
			ChattingController.toChatting(o, "All buff completed", Lineage.CHATTING_MODE_MESSAGE);
	}

	/**
	 * ????????????
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
	 * ??????
	 *  : ScreenRenderComposite ?????? ?????????
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
	 * ?????? ?????? ?????????.
	 *  : ????????? ?????????.
	 *  : PcInstnace.toWorldJoin ?????????.
	 * @param o
	 */
	static public void toBuff(object o) {
		EnchantDexterity.onBuff(o, SkillDatabase.find(4, 1));
		EnchantMighty.onBuff(o, SkillDatabase.find(6, 1));
		Haste.onBuff(o, SkillDatabase.find(7, 5));
//		AbsoluteBarrier.onBuff(o, SkillDatabase.find(10, 5), 60);
//		ShiningAura.onBuff(o, SkillDatabase.find(15, 2));
		if (Lineage.server_version >= 182) { // ??????, ??????????????? 1.82?????? ????????? ????????? ??????.
			BlessWeapon.onBuff(o, SkillDatabase.find(6, 7));
			EarthSkin.onBuff(o, SkillDatabase.find(19, 6));
		}
	}
	
	/**
	 * ????????????
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
	 * ????????? ??????
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
				// ????????? ?????? ??? ?????????.
				ItemInstance temp = ItemDatabase.newInstance(i);
				temp.setCount(count);
				temp.setEnLevel(enchant);
				temp.setBress(bress);
				temp.setDefinite(true);
				// ????????? ????????????.
				o.getInventory().append(temp, count);
				// ????????? ?????????.
				ItemDatabase.setPool(temp);
				// ??????.
				ChattingController.toChatting(o, "item created", Lineage.CHATTING_MODE_MESSAGE);
			} else {
				ChattingController.toChatting(o, "item not-existing", Lineage.CHATTING_MODE_MESSAGE);
			}
		} catch (Exception e) {
			ChattingController.toChatting(o, ".item  name  count  enchant  bless", Lineage.CHATTING_MODE_MESSAGE);
		}
	}

	/**
	 * ????????? ??????.
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
	 * ??????.
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
	 * ??????.
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
