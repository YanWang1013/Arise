package lineage.bean.lineage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.database.Item;
import lineage.bean.database.KingdomTaxLog;
import lineage.bean.database.Npc;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.database.TeleportHomeDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ClanWar;
import lineage.share.Lineage;
import lineage.thread.AiThread;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.controller.ClanController;
import lineage.world.controller.KingdomController;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.npc.kingdom.KingdomCastleTop;
import lineage.world.object.npc.kingdom.KingdomCrown;
import lineage.world.object.npc.kingdom.KingdomDoor;
import lineage.world.object.npc.kingdom.KingdomDoorman;
import lineage.world.object.npc.kingdom.KingdomGuard;


public class Kingdom {
	private int uid;				// 고유 값
	private String name;			// 성 이름
	private int x;					// 내성 좌표
	private int y;					// 내성 좌표
	private int map;				// 내성 좌표
	private int throne_x;			// 옥좌 좌표
	private int throne_y;			// 옥좌 좌표
	private int throne_map;			// 옥좌 좌표
	private int clanId;				// 성주 혈맹 아이디
	private String clanName;		// 성주 혈맹 이름
	private long agentId;			// 성주 아이디
	private String agentName;		// 성주 이름
	private int taxRate;			// 세율
	private int taxRateTomorrow;	// 다음날 적용될 세율
	private long taxTotal;			// 축적된 세금
	private long taxDay;			// 마지막으로 세팅한 세율 날자
	private boolean war;			// 전쟁중인지 여부
	private long warDay;			// 전쟁 날자
	private long warDayEnd;			// 공성전 종료 시간.
	private int war_status;			// 공성전 진행상태.
	private List<KingdomDoor> list_door;			// 성에 사용되는 문 목록.
	private List<KingdomDoorman> list_doorman;		// 성에 사용되는 문지기 목록.
	private List<KingdomCastleTop> list_castletop;	// 성에 사용되는 수호탑 목록.
	private List<KingdomGuard> list_guard;			// 근위병 목록.
	private List<KingdomTaxLog> list_taxlog;		// 해당 성에대한 세금 로그.
	private Map<Integer, Long> list_warday;			// 공성전 진행할 시간 목록.
	private List<object> list_flag;					// 깃발 목록.
	private object crown;			// 실제 픽업될 면류관
	private object crown_visual;	// 보기주기용 면류관
	private List<String> list_war;	// 전쟁 선포한 혈맹들의 이름목록.	- 동기화 필요함. 여러명이서 면류관 및 옥좌를 탈환할 수 있기 때문.
	
	public Kingdom(){
		list_war = new ArrayList<String>();
		list_flag = new ArrayList<object>();
		list_warday = new HashMap<Integer, Long>();
		list_door = new ArrayList<KingdomDoor>();
		list_doorman = new ArrayList<KingdomDoorman>();
		list_castletop = new ArrayList<KingdomCastleTop>();
		list_guard = new ArrayList<KingdomGuard>();
		list_taxlog = new ArrayList<KingdomTaxLog>();
		war_status = 0;
		
		crown_visual = new object();
		crown_visual.setObjectId(ServerDatabase.nextEtcObjId());
		crown_visual.setGfx(1482);
		crown = new KingdomCrown(this);
		crown.setObjectId(ServerDatabase.nextEtcObjId());
		crown.setGfx(462);
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getMap() {
		return map;
	}
	public void setMap(int map) {
		this.map = map;
	}
	public int getThroneX() {
		return throne_x;
	}
	public void setThroneX(int throne_) {
		this.throne_x = throne_;
	}
	public int getThroneY() {
		return throne_y;
	}
	public void setThroneY(int throne_) {
		this.throne_y = throne_;
	}
	public int getThroneMap() {
		return throne_map;
	}
	public void setThroneMap(int throne_) {
		this.throne_map = throne_;
	}
	public int getClanId() {
		return clanId;
	}
	public void setClanId(int clanId) {
		this.clanId = clanId;
		// 관리중인 객체들 클랜 아이디 갱신해주기. 경비병이나 머 그런거..
	}
	public String getClanName() {
		return clanName;
	}
	public void setClanName(String clanName) {
		this.clanName = clanName;
	}
	public long getAgentId() {
		return agentId;
	}
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public int getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(int taxRate) {
		this.taxRate = taxRate;
	}
	public long getTaxTotal() {
		return taxTotal;
	}
	public void setTaxTotal(long taxTotal) {
		this.taxTotal = taxTotal;
	}
	public long getTaxDay() {
		return taxDay;
	}
	public void setTaxDay(long taxDay) {
		this.taxDay = taxDay;
	}
	public boolean isWar() {
		return war;
	}
	public void setWar(boolean war) {
		this.war = war;
	}
	public long getWarDay() {
		return warDay;
	}
	public void setWarDay(long warDay) {
		this.warDay = warDay;
	}
	public long getWarDayEnd() {
		return warDayEnd;
	}
	public void setWarDayEnd(long warDayEnd) {
		this.warDayEnd = warDayEnd;
	}
	public List<KingdomTaxLog> getTaxLog() {
		return list_taxlog;
	}
	public int getTaxRateTomorrow() {
		return taxRateTomorrow;
	}
	public void setTaxRateTomorrow(int taxRateTomorrow) {
		this.taxRateTomorrow = taxRateTomorrow;
	}
	public int getWarStatus() {
		return war_status;
	}
	public void setWarStatus(int war_status) {
		this.war_status = war_status;
	}
	public object getCrown() {
		return crown;
	}
	public object getCrownVisual() {
		return crown_visual;
	}
	public List<KingdomDoor> getListDoor() {
		return list_door;
	}
	public Map<Integer, Long> getListWarday(){
		return list_warday;
	}
	public List<KingdomCastleTop> getListCastleTop(){
		return list_castletop;
	}
	public List<String> getListWar(){
		return list_war;
	}
	public KingdomTaxLog findTaxLog(String type){
		for(KingdomTaxLog ktl : list_taxlog){
			if(ktl.getType().equalsIgnoreCase(type))
				return ktl;
		}
		return null;
	}
	
	/**
	 * 디비에 선포된 혈맹에대한 정보를 기록하기위해 해당 함수를 사용.
	 * @return
	 */
	public String toStringListWar(){
		StringBuffer sb = new StringBuffer();
		for(String name : list_war)
			sb.append(name).append(" ");
		return sb.toString();
	}
	
	/**
	 * 수호탑 상태 확인해서 모든 수호탑이 다 쓰러진 상태인지 확인.
	 *  : 수호탑이 한개씩 무너질때마다 호출됨.
	 *  : 관리되고있는 모든 수호탑이 다 쓰러진상태라면 true 리턴.
	 * @return
	 */
	public boolean isCastleTopDead(){
		for(KingdomCastleTop kct : list_castletop){
			if(!kct.isDead())
				return false;
		}
		return true;
	}
	
	public void toWardaySetting(){
		// 공성전 시간이 설정안되어있을경우.
		if(warDayEnd==0)
			warDayEnd = System.currentTimeMillis();

		// 공성 시작할 날자 설정.
		long time = warDayEnd + ((1000 * 60 * 60 * 24)*Lineage.kingdom_war_day);
		// 시 분 초 0으로 세팅.
		time = new java.sql.Date(Util.getYear(time), Util.getMonth(time)-1, Util.getDate(time)).getTime();
		// 6시 부터 3시간 단위로 설정.
		list_warday.clear();
		for(int i=0,j=6 ; i<6 ; ++i, j+=3)
			list_warday.put(i, time+(1000*60*60*j));
	}
	
	/**
	 * 성과 연결할 문 등록처리 함수.<br/>
	 *  : 스폰처리도 함께 함.
	 * @param npc
	 * @param x
	 * @param y
	 * @param map
	 * @param h				: 방향
	 * @param field_pos		: 이동가능 및 불가능 처리할 좌표 시작점
	 * @param field_size	: 좌표 길이 값.
	 */
	public void appendDoor(Npc npc, int x, int y, int map, int h, int field_pos, int field_size){
		KingdomDoor kd = new KingdomDoor(npc, this);
		kd.setObjectId(ServerDatabase.nextEtcObjId());
		kd.setClanName(getClanName());
		kd.setClassGfx(npc.getGfx());
		kd.setClassGfxMode(npc.getGfxMode());
		kd.setGfx(npc.getGfx());
		kd.setGfxMode(npc.getGfxMode());
		kd.setName(npc.getNameId());
		kd.setMaxHp(npc.getHp());
		kd.setNowHp(kd.getTotalHp());
		kd.setHeading(h);
		kd.setFieldPos(field_pos);
		kd.setFieldSize(field_size);
		kd.toTeleport(x, y, map, false);
		list_door.add(kd);
	}
	
	/**
	 * 성과 연결할 문지기 등록처리 함수.
	 * @param npc
	 * @param x
	 * @param y
	 * @param map
	 * @param h
	 */
	public void appendDoorman(Npc npc, int x, int y, int map, int h){
		KingdomDoorman kd = new KingdomDoorman(npc, this);
		kd.setObjectId(ServerDatabase.nextEtcObjId());
		kd.setClanName(getClanName());
		kd.setClassGfx(npc.getGfx());
		kd.setClassGfxMode(npc.getGfxMode());
		kd.setGfx(npc.getGfx());
		kd.setGfxMode(npc.getGfxMode());
		kd.setName(npc.getNameId());
		kd.setHeading(h);
		kd.toTeleport(x, y, map, false);
		list_doorman.add(kd);
	}
	
	/**
	 * 수호탑
	 * @param npc
	 * @param x
	 * @param y
	 * @param map
	 * @param h
	 */
	public void appendCastleTop(Npc npc, int x, int y, int map, int h){
		KingdomCastleTop kct = new KingdomCastleTop(npc, this);
		kct.setObjectId(ServerDatabase.nextEtcObjId());
		kct.setClanName(getClanName());
		kct.setClassGfx(npc.getGfx());
		kct.setClassGfxMode(npc.getGfxMode());
		kct.setGfx(npc.getGfx());
		kct.setGfxMode(npc.getGfxMode());
		kct.setName(npc.getNameId());
		kct.setMaxHp(npc.getHp());
		kct.setNowHp(kct.getTotalHp());
		kct.setHeading(h);
		kct.toTeleport(x, y, map, false);
		list_castletop.add(kct);
		
	}
	
	/**
	 * 근위병
	 * @param npc
	 * @param x
	 * @param y
	 * @param map
	 * @param h
	 */
	public void appendGuard(Npc npc, int x, int y, int map, int h){
		KingdomGuard kg = new KingdomGuard(npc, this);
		kg.setObjectId(ServerDatabase.nextEtcObjId());
		kg.setClanName(getClanName());
		kg.setClassGfx(npc.getGfx());
		kg.setClassGfxMode(npc.getGfxMode());
		kg.setGfx(npc.getGfx());
		kg.setGfxMode(npc.getGfxMode());
		kg.setName(npc.getNameId());
		kg.setMaxHp(npc.getHp());
		kg.setNowHp(kg.getTotalHp());
		kg.setHeading(h);
		kg.setHomeX(x);
		kg.setHomeY(y);
		kg.setHomeMap(map);
		kg.setHomeHeading(h);
		kg.toTeleport(x, y, map, false);
		list_guard.add(kg);
		
		// 인공지능관리에 넣기.
		AiThread.append(kg);
	}
	
	/**
	 * 깃발 등록 처리 함수.
	 */
	public void appendFlag(){
		if(Lineage.server_version<160)
			return;
			
		int x1 = Lineage.KINGDOMLOCATION[uid][0];
		int x2 = Lineage.KINGDOMLOCATION[uid][1];
		int y1 = Lineage.KINGDOMLOCATION[uid][2];
		int y2 = Lineage.KINGDOMLOCATION[uid][3];
		int map = Lineage.KINGDOMLOCATION[uid][4];
		int x = 0;
		int y = 0;
		// 2시방향으로 그리기
		for (x = x1 , y = y1; x <= x2; x += 8)
			list_flag.add(newFlag(x, y, map));
		// 8시방향으로 그리기
		for (x = x2 , y = y1; y <= y2; y += 8)
			list_flag.add(newFlag(x, y, map));
		// 11시방향으로 그리기
		for (x = x2 , y = y2; x >= x1; x -= 8)
			list_flag.add(newFlag(x, y, map));
		// 11시방향으로 그리기
		for (x = x1 , y = y2; y >= y1; y -= 8)
			list_flag.add(newFlag(x, y, map));
	}
	
	/**
	 * 깃발 객체 만들기.
	 * @param x
	 * @param y
	 * @param map
	 * @return
	 */
	private object newFlag(int x, int y, int map){
		object flag = new object();
		flag.setObjectId(ServerDatabase.nextEtcObjId());
		flag.setGfx(1284);
		flag.setX(x);
		flag.setY(y);
		flag.setMap(map);
		return flag;
	}
	
	/**
	 * 공금 처리 함수.<br/>
	 *  : 어떤 이유에서인지 공금이 추가 및 삭제되려할때 해당함수를 통해서 처리함.<br/>
	 *  : 처리된 정보에대한 상세기록을 남기기위해 로그처리도 함께함.<br/>
	 *  : 시종인에게 보고받으려는 이벤트 처리시 로그 사용.<br/>
	 *  : type 종류 정보<br/>
	 *   -> shop 상점 세금
	 *   -> agit 아지트 세금
	 *   -> tribute 왕에의 성납
	 *   -> peace 치안유지비
	 *   -> payment_servants 시종들의 봉급
	 *   -> upkeep 성의 유지관리비
	 *   -> payment_mercenaries 용병 유지비
	 *   -> miscellaneous 기타 잡비
	 * @param aden	: 처리될 아덴
	 * @param plus	: 추가 처리 인가?
	 * @param type	: 상점에서 온건지 아디트에서 온건지 성 및 치안유지관리비처리한건지에 대한 구분용
	 */
	public void toTax(int aden, boolean plus, String type){
		if(aden==0 || type==null)
			return;
		
		// 로그 남기기.
		KingdomTaxLog ktl = findTaxLog(type);
		if(ktl == null){
			ktl = new KingdomTaxLog();
			ktl.setKingdom(uid);						// 성 고유 아이디
			ktl.setKingdomName(name);					// 성 이름
			ktl.setDate(System.currentTimeMillis());	// 세금이 적용된 시점 확인용.
			ktl.setType(type);							// 세금 종류
			list_taxlog.add(ktl);						// 관리목록에 추가.
		}
		ktl.setTax( ktl.getTax()+aden );
	}
	
	/**
	 * 공성전 시작 처리 함수.
	 * @param time
	 */
	public void toStartWar(long time){
		// 상태 변경.
		war = true;
		war_status = Lineage.KINGDOM_WARSTATUS_START;
		// 전쟁 종료시간 설정.
		warDayEnd = time + (1000*60*Lineage.kingdom_war_time);
		// 리니지 월드에 공성전 시작됫다는거 알리기.
		if(Lineage.server_version > 163)
			World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), this));
		// 성에 혈맹이 지정되있을 경우에만 처리.
		if(clanId > 0){
			// 자동 선포 처리.
			for(String clan_name : list_war){
				Clan clan = ClanController.find(clan_name);
				if(clan != null){
					clan.setWarClan(clanName);
					World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, clan_name, clanName));
				}
			}
			if(Lineage.kingdom_crown){
				// 성주 면류관 압수.
				PcInstance agent = World.findPc(agentName);
				if(agent != null){
					// 바로 압수.
					ItemInstance crown = agent.getInventory().find("면류관", false);
					if(crown != null)
						agent.getInventory().count(crown, 0, true);
				}else{
					// 디비에서 제거.
					Connection con = null;
					PreparedStatement st = null;
					try {
						con = DatabaseConnection.getLineage();
						st = con.prepareStatement( "DELETE FROM characters_inventory WHERE cha_name=? AND name=?" );
						st.setString(1, agentName);
						st.setString(2, "면류관");
						st.executeUpdate();
						st.close();
					} catch (Exception e) {
						lineage.share.System.printf("%s : toStartWar(long time)\r\n", Kingdom.class.toString());
						lineage.share.System.println(e);
					} finally {
						DatabaseConnection.close(con, st);
					}
				}
			}
		}
		// 상태 변경
		war_status = Lineage.KINGDOM_WARSTATUS_PLAY;
		// 깃발 표현.
		for(object o : list_flag)
			o.toTeleport(o.getX(), o.getY(), o.getMap(), false);
		// 주변객체 마을로 텔레포트
		toTeleport(false, true);
	}
	
	/**
	 * 공성전 종료 처리 함수.
	 * 
	 * @param time
	 */
	public void toStopWar(long time) {
		// 리니지 월드에 공성전 종료됫다는거 알리기.
		war_status = Lineage.KINGDOM_WARSTATUS_STOP;
		if (Lineage.server_version > 163)
			World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), this));
		// 성에 혈맹이 지정되있을 경우에만 처리.
		if (clanId > 0) {
			// 자동 승리 및 종료 처리.
			for (String clan_name : list_war) {
				Clan clan = ClanController.find(clan_name);
				if (clan != null) {
					clan.setWarClan(null);
					World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 3, clan_name, clanName));
					World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 4, clanName, clan_name));
				}
			}
			list_war.clear();
			// 성을 차지했다는거 혈맹원들에게 패킷으로 알리기.
			war_status = Lineage.KINGDOM_WARSTATUS_4;
			if (Lineage.server_version > 163)
				ClanController.find(clanName)
						.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), this));
			if (Lineage.kingdom_crown) {
				// 면류관 지급.
				Item i = ItemDatabase.find("면류관");
				if (i != null) {
					// 메모리 생성 및 초기화.
					ItemInstance crown = ItemDatabase.newInstance(i);
					crown.setObjectId(ServerDatabase.nextItemObjId());
					// 군주 접속되잇는지 확인.
					PcInstance agent = World.findPc(agentName);
					if (agent != null) {
						// 바로 면류관 지급.
						// 인벤에 등록처리.
						agent.getInventory().append(crown, true);
					} else {
						// 군주가 접속안되잇어서 디비로 등록처리.
						Connection con = null;
						PreparedStatement st = null;
						try {
							con = DatabaseConnection.getLineage();
							st = con.prepareStatement("INSERT INTO characters_inventory SET "
									+ "objId=?, cha_objId=?, cha_name=?, name=?, count=?, quantity=?, en=?, equipped=?, definite=?, bress=?, "
									+ "durability=?, nowtime=?, pet_objid=?, inn_key=?, letter_uid=?, slimerace=?");
							st.setLong(1, crown.getObjectId());
							st.setLong(2, agentId);
							st.setString(3, agentName);
							st.setString(4, crown.getItem().getName());
							st.setLong(5, crown.getCount());
							st.setInt(6, crown.getQuantity());
							st.setInt(7, crown.getEnLevel());
							st.setInt(8, crown.isEquipped() ? 1 : 0);
							st.setInt(9, crown.isDefinite() ? 1 : 0);
							st.setInt(10, crown.getBress());
							st.setInt(11, crown.getDurability());
							st.setInt(12, crown.getNowTime());
							st.setLong(13, crown.getPetObjectId());
							st.setLong(14, crown.getInnRoomKey());
							st.setInt(15, crown.getLetterUid());
							st.setString(16, crown.getSlimeRaceTicket());
							st.executeUpdate();
							st.close();
						} catch (Exception e) {
							lineage.share.System.printf("%s : toStopWar(long time)\r\n", Kingdom.class.toString());
							lineage.share.System.println(e);
						} finally {
							DatabaseConnection.close(con, st);
						}
						// 메모리 재사용.
						ItemDatabase.setPool(crown);
					}
				}
			}
		}
		// 깃발 제거.
		for (object o : list_flag) {
			o.clearList(true);
			World.remove(o);
		}
		// 주변객체 마을로 텔레포트
		toTeleport(false, true);
		// 수호탑 복구.
		for (KingdomCastleTop kct : list_castletop)
			kct.toRevival(null);
		// 면류관 표현 제거.
		getCrown().clearList(true);
		getCrownVisual().clearList(true);
		World.remove(getCrown());
		World.remove(getCrownVisual());
		// 성문 복구.
		for (KingdomDoor kd : list_door)
			kd.toReset(false);
		// 상태 변경.
		war = false;
		warDay = 0;
	}
	
	/**
	 * 해당 성주변에 있는 사용자들을 마을로 텔레포트시킬지 여부.
	 * @param all	: 모두다??
	 */
	public void toTeleport(boolean all, boolean packet){
		for(PcInstance pc : World.getPcList()){
			// 성혈은 제외할경우.
			if(all==false && getClanId()>0 && pc.getClanId()==getClanId())
				continue;
			// 처리
			if(KingdomController.isKingdomLocation(pc, getUid()) || throne_map==pc.getMap()){
				// 성이 존재한다면 내성으로
				if(getClanId()>0 && pc.getClanId()==getClanId()){
					pc.setHomeX( getX() );
					pc.setHomeY( getY() );
					pc.setHomeMap( getMap() );
				}else{
					TeleportHomeDatabase.toLocation(pc);
				}
				// 죽은 정보 복구.
				pc.toReset(false);
				// 마을로 이동.
				pc.toTeleport(pc.getHomeX(), pc.getHomeY(), pc.getHomeMap(), packet);
			}
		}
	}

	/**
	 * 옥좌 좌표확인해주는 함수.
	 * @param o
	 * @return
	 */
	public boolean isThrone(object o){
		return throne_x==o.getX() && throne_y==o.getY() && throne_map==o.getMap();
	}
	
	/**
	 * 성에 전쟁상태에 따라 값을 리턴함.
	 * @return
	 */
	public String getWarStatusString() {
		switch(getWarStatus()){
			case 0:
				return getName()+"에 공성전이 시작되었습니다.";
			case 1:
				return getName()+"에 공성전이 종료되었습니다.";
			case 2:
				return getName()+"에 공성전이 진행중입니다.";
			default:
				return "알수 없는 상태입니다.";
		}
	}
	
}
