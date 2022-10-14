package lineage.world.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import lineage.bean.database.KingdomTaxLog;
import lineage.bean.lineage.Kingdom;
import lineage.database.DatabaseConnection;
import lineage.database.NpcDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ClanWar;
import lineage.network.packet.server.S_KingdomAgent;
import lineage.network.packet.server.S_KingdomWarTimeSelect;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.object;
import lineage.world.object.instance.PcInstance;

public final class KingdomController {

	static private List<Kingdom> list;
	static private Date now_date;					// toTimer 함수에서 사용.
	static private Date kingdom_date;				// toTimer 함수에서 사용.
	static private List<KingdomTaxLog> list_temp;	// toTimer 함수에서 사용.
	
	public static List<Kingdom> getKingdomList() {
		synchronized (list) {
			return list;
		}
	}
	
	/**
	 * 초기화 함수
	 */
	static public void init(Connection con){
		TimeLine.start("KingdomController..");
		
		now_date = new Date(System.currentTimeMillis());
		kingdom_date = new Date(0);
		list_temp = new ArrayList<KingdomTaxLog>();
		list = new ArrayList<Kingdom>();
		// 각 성에대한 정보 불러오고.
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// 성 정보 추출.
			st = con.prepareStatement("SELECT * FROM kingdom");
			rs = st.executeQuery();
			while(rs.next()){
				Kingdom k = new Kingdom();
				k.setUid(rs.getInt("uid"));
				k.setName(rs.getString("name"));
				k.setX(rs.getInt("x"));
				k.setY(rs.getInt("y"));
				k.setMap(rs.getInt("map"));
				k.setThroneX(rs.getInt("throne_x"));
				k.setThroneY(rs.getInt("throne_y"));
				k.setThroneMap(rs.getInt("throne_map"));
				k.setClanId(rs.getInt("clan_id"));
				k.setClanName(rs.getString("clan_name"));
				k.setAgentId(rs.getInt("agent_id"));
				k.setAgentName(rs.getString("agent_name"));
				k.setTaxRate(rs.getInt("tax_rate"));
				k.setTaxRateTomorrow(rs.getInt("tax_rate_tomorrow"));
				k.setTaxTotal(rs.getLong("tax_total"));
				k.setWar(rs.getString("war").equalsIgnoreCase("true"));
				try { k.setTaxDay(rs.getTimestamp("tax_day").getTime());  } catch (Exception e) { }
				try { k.setWarDay(rs.getTimestamp("war_day").getTime());  } catch (Exception e) { }
				try { k.setWarDayEnd(rs.getTimestamp("war_day_end").getTime());  } catch (Exception e) { }
				
				// 전쟁을 선포한 혈맹이름 목록 등록.
				StringTokenizer tok = new StringTokenizer(rs.getString("war_list"));
				while(tok.hasMoreTokens())
					k.getListWar().add(tok.nextToken());
				if(Lineage.server_version <= 163)
					// 옥좌 좌표 필드값 변경.
					World.set_map(k.getThroneX(), k.getThroneY(), k.getThroneMap(), 127);
				// 관리목록에 등록.
				list.add(k);
			}
			rs.close();
			st.close();
			
			// 세율 로그 정보 추출.
			for(Kingdom k : list){
				// 세율 설정한 날자로 확인하기. 그래야 버그유발을 막음.
				// toTimer에서는 taxday갑과 현재 date 값을 비교해서 일 자 값이 다르면 해당 성에대한 세금을 지급하는데
				// 서버종료 시간차(8일 종료해서 9일 오픈)로 인해 지급을 못하는 현상이 생길수 있음(now_date를 서버오픈시간으로 했을경우)
				// 그래서 세율설정한 시간으로 검색을 시도함.
				kingdom_date.setTime(k.getTaxDay());
				st = con.prepareStatement("SELECT * FROM kingdom_tax_log WHERE kingdom=? AND date>=?");
				st.setInt(1, k.getUid());
				st.setDate(2, kingdom_date);
				rs = st.executeQuery();
				while(rs.next()){
					KingdomTaxLog ktl = new KingdomTaxLog();
					ktl.setKingdom(rs.getInt("kingdom"));
					ktl.setKingdomName(rs.getString("kingdom_name"));
					ktl.setType(rs.getString("type"));
					ktl.setTax(rs.getInt("tax"));
					ktl.setDate(rs.getDate("date").getTime());
					
					k.getTaxLog().add(ktl);
				}
				rs.close();
				st.close();
			}
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", KingdomController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public void close(Connection con){
		PreparedStatement st = null;
		try{
			long time = System.currentTimeMillis();
			// 0시로 맞추기위해 time 으로 생성안하고 일자 로 생성.
			now_date.setTime(new java.sql.Date(Util.getYear(time), Util.getMonth(time)-1, Util.getDate(time)).getTime());
			synchronized (list) {
				for(Kingdom k : list){
					// 킹덤 정보 저장.
					toSaveKingdom(con, k);
					// 로그 정보 저장.
					toSaveTaxLog(con, k, now_date);
				}
			}
		}catch(Exception e){
			lineage.share.System.printf("%s : close(Connection con)\r\n", KingdomController.class.toString());
			lineage.share.System.println(e);
		}finally{
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 사용자 월드에 진입할때 호출됨.
	 */
	static public void toWorldJoin(PcInstance pc){
		// 성주 왕관 표현.
		for(Kingdom k : list)
			pc.toSender(S_KingdomAgent.clone(BasePacketPooling.getPool(S_KingdomAgent.class), k.getUid(), k.getAgentId()));
		// 공성전이 진행중이라는거 표현.
		for(Kingdom k : list){
			if(k.getClanId()!=0 && k.isWar()){
				// 선포혈 처리.
				if(Lineage.server_version > 163)
					pc.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), k));
				if(k.getListWar().contains(pc.getClanName()))
					pc.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 8, pc.getClanName(), k.getClanName()));
				// 수성혈측 처리.
				if(k.getClanId() == pc.getClanId()){
					for(String clan : k.getListWar())
						pc.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 8, k.getClanName(), clan));
				}
			}
		}
	}
	
	/**
	 * 해당 성에 대한 정보 갱신 함수.
	 * @param con
	 * @param k
	 */
	static public void toSaveKingdom(Connection con, Kingdom k){
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("UPDATE kingdom SET clan_id=?, clan_name=?, agent_id=?, agent_name=?, tax_rate=?, tax_rate_tomorrow=?, tax_total=?, tax_day=?, war=?, war_day=?, war_day_end=?, war_list=? WHERE uid=?");
			st.setInt(1, k.getClanId());
			st.setString(2, k.getClanName());
			st.setLong(3, k.getAgentId());
			st.setString(4, k.getAgentName());
			st.setInt(5, k.getTaxRate());
			st.setInt(6, k.getTaxRateTomorrow());
			st.setLong(7, k.getTaxTotal());
			st.setTimestamp(8, new java.sql.Timestamp(k.getTaxDay()));
			st.setString(9, String.valueOf(k.isWar()));
			st.setTimestamp(10, new java.sql.Timestamp(k.getWarDay()));
			st.setTimestamp(11, new java.sql.Timestamp(k.getWarDayEnd()));
			st.setString(12, k.toStringListWar());
			st.setInt(13, k.getUid());
			st.executeUpdate();
			st.close();
		}catch(Exception e){
			lineage.share.System.printf("%s : toSaveKingdom(Connection con, Kingdom k)\r\n", KingdomController.class.toString());
			lineage.share.System.println(e);
		}finally{
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 세율 처리에대한 로그기록 함수.
	 * @param con
	 */
	static public void toSaveTaxLog(Connection con, Kingdom k, Date date){
		PreparedStatement st = null;
		try{
			// 이전 정보 제거.
			st = con.prepareStatement( "DELETE FROM kingdom_tax_log WHERE kingdom=? AND date>=?" );
			st.setInt(1, k.getUid());
			st.setDate(2, date);
			st.executeUpdate();
			st.close();
			// 정보 저장.
			for(KingdomTaxLog ktl : k.getTaxLog()){
				st = con.prepareStatement("INSERT INTO kingdom_tax_log SET kingdom=?, kingdom_name=?, type=?, tax=?, date=?");
				st.setInt(1, ktl.getKingdom());
				st.setString(2, ktl.getKingdomName());
				st.setString(3, ktl.getType());
				st.setInt(4, ktl.getTax());
				st.setDate(5, new java.sql.Date(ktl.getDate()));
				st.executeUpdate();
				st.close();
			}
		}catch(Exception e){
			lineage.share.System.printf("%s : toSaveTaxLog(Connection con, Kingdom k, Date date)\r\n", KingdomController.class.toString());
			lineage.share.System.println(e);
		}finally{
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 공성전 시간설정 요청처리 함수.
	 * @param pc
	 */
	static public void toWarTimeSelect(PcInstance pc){
		Kingdom k = find(pc);
		if(k==null || k.isWar() || k.getWarDay()!=0 || pc.getClassType()!=Lineage.LINEAGE_CLASS_ROYAL)
			return;

		// 공성전 진행될 시간값 갱신.
		k.toWardaySetting();
		// 표현.
		pc.toSender(S_KingdomWarTimeSelect.clone(BasePacketPooling.getPool(S_KingdomWarTimeSelect.class), k));
	}
	
	/**
	 * 공성전 진행할 시간을 선택했을경우 호출되서 처리하는 함수.
	 * @param pc
	 * @param idx
	 */
	static public void toWarTimeSelectFinal(PcInstance pc, int idx){
		Kingdom k = find(pc);
		if(k==null || k.isWar() || k.getWarDay()!=0 || pc.getClassType()!=Lineage.LINEAGE_CLASS_ROYAL)
			return;
		
		k.setWarDay( k.getListWarday().get(idx) );
	}
	
	/**
	 * 각 성에 객체 읽는 함수.
	 *  : 문이나 경비병등 스폰처리.
	 */
	static public void readKingdom(){
		
		try {
			for(Kingdom k : list){
				switch(k.getUid()){
					case Lineage.KINGDOM_KENT:
						// 내성문
						k.appendDoor(NpcDatabase.find("[켄트] 내성문"), 33171, 32759, 4, 4, 33170, 2);
						// 외성문
						if(Lineage.server_version>=230){
							k.appendDoor(NpcDatabase.find("[켄트] 외성문 7시"), 33112, 32771, 4, 6, 32769, 4);
							k.appendDoor(NpcDatabase.find("[켄트] 외성문 4시"), 33152, 32807, 4, 4, 33150, 4);
						}else{
							k.appendDoor(NpcDatabase.find("[켄트] 외성문 7시"), 33112, 32770, 4, 6, 32770, 2);
						}
						// 문지기
						k.appendDoorman(NpcDatabase.find("[켄트] 외성 문지기"), 33110, 32769, 4, 6);
						if(Lineage.server_version>=230)
							k.appendDoorman(NpcDatabase.find("[켄트] 외성 문지기"), 33153, 32809, 4, 4);
						k.appendDoorman(NpcDatabase.find("[켄트] 내성 문지기"), 33172, 32760, 4, 4);
						// 수호탑
						if(Lineage.server_version>163)
							k.appendCastleTop(NpcDatabase.findNameid("$1435"), 33169, 32773, 4, 0);
						// 외성 근위병
						k.appendGuard(NpcDatabase.findNameid("$475"), 33107, 32768, 4, 6);	// 7시
						k.appendGuard(NpcDatabase.findNameid("$475"), 33107, 32774, 4, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 33166, 32766, 4, 4);	// 내성문 근처
						k.appendGuard(NpcDatabase.findNameid("$475"), 33173, 32766, 4, 4);
						if(Lineage.server_version>=230){
							k.appendGuard(NpcDatabase.findNameid("$475"), 33148, 32812, 4, 4);	// 4시
							k.appendGuard(NpcDatabase.findNameid("$475"), 33155, 32812, 4, 4);
						}
						// 성벽 근위병
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 33113, 32760, 4, 6);	// 7시
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 33113, 32762, 4, 6);
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 33113, 32764, 4, 6);
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 33113, 32778, 4, 6);
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 33113, 32780, 4, 6);
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 33113, 32782, 4, 6);
						if(Lineage.server_version>=230){
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 33140, 32806, 4, 4);	// 4시
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 33142, 32806, 4, 4);
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 33144, 32806, 4, 4);
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 33158, 32806, 4, 4);
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 33160, 32806, 4, 4);
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 33162, 32806, 4, 4);
						}
						// 깃발 등록.
						k.appendFlag();
						// 내성 근위병
						k.appendGuard(NpcDatabase.findNameid("$475"), 32738, 32787, 15, 4);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32734, 32787, 15, 4);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32739, 32796, 15, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32739, 32792, 15, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32739, 32800, 15, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32732, 32792, 15, 2);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32732, 32796, 15, 2);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32732, 32800, 15, 2);
						break;
					case Lineage.KINGDOM_WINDAWOOD:
						if(Lineage.server_version<144)
							continue;
						// 내성문
						k.appendDoor(NpcDatabase.find("[윈다우드] 내성문"), 32678, 33392, 4, 4, 32677, 2);
						// 외성문
						if(Lineage.server_version>=230){
							k.appendDoor(NpcDatabase.find("[윈다우드] 외성문 7시"), 32590, 33409, 4, 6, 33407, 4);
							k.appendDoor(NpcDatabase.find("[윈다우드] 외성문 4시"), 32625, 33436, 4, 4, 32623, 4);
						}else{
							k.appendDoor(NpcDatabase.find("[윈다우드] 외성문 7시"), 32590, 33408, 4, 6, 33408, 2);
						}
						// 문지기
						k.appendDoorman(NpcDatabase.find("[윈다우드] 외성 문지기"), 32588, 33407, 4, 6);
						if(Lineage.server_version>=230)
							k.appendDoorman(NpcDatabase.find("[윈다우드] 외성 문지기"), 32626, 33438, 4, 4);
						k.appendDoorman(NpcDatabase.find("[윈다우드] 내성 문지기"), 32679, 33393, 4, 4);
						// 수호탑
						if(Lineage.server_version>163)
							k.appendCastleTop(NpcDatabase.findNameid("$1435"), 32669, 33409, 4, 0);
						// 외성 근위병
						k.appendGuard(NpcDatabase.findNameid("$475"), 32584, 33404, 4, 6);	// 7시
						k.appendGuard(NpcDatabase.findNameid("$475"), 32584, 33414, 4, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32673, 33399, 4, 4);	// 내성문 근처
						k.appendGuard(NpcDatabase.findNameid("$475"), 32679, 33399, 4, 4);
						if(Lineage.server_version>=230){
							k.appendGuard(NpcDatabase.findNameid("$475"), 32619, 33442, 4, 4);	// 4시
							k.appendGuard(NpcDatabase.findNameid("$475"), 32630, 33442, 4, 4);
						}
						// 성벽 근위병
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 32591, 33398, 4, 6);	// 7시
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 32591, 33400, 4, 6);
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 32591, 33402, 4, 6);
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 32591, 33416, 4, 6);
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 32591, 33418, 4, 6);
						k.appendGuard(NpcDatabase.find("성지기 경비병"), 32591, 33420, 4, 6);
						if(Lineage.server_version>=230){
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 32613, 33435, 4, 4);	// 4시
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 32615, 33435, 4, 4);
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 32617, 33435, 4, 4);
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 32631, 33435, 4, 4);
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 32633, 33435, 4, 4);
							k.appendGuard(NpcDatabase.find("성지기 경비병"), 32635, 33435, 4, 4);
						}
						// 깃발 등록.
						k.appendFlag();
						// 내성 근위병
						k.appendGuard(NpcDatabase.findNameid("$475"), 32736, 32789, 29, 4);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32733, 32789, 29, 4);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32737, 32794, 29, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32737, 32798, 29, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32737, 32802, 29, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32731, 32794, 29, 2);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32731, 32798, 29, 2);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32731, 32802, 29, 2);
						break;
					case Lineage.KINGDOM_GIRAN:
						if(Lineage.server_version<163)
							continue;
						// 내성문
						k.appendDoor(NpcDatabase.find("[기란성] 내성문"), 33632, 32660, 4, 4, 33631, 2);
						// 외성문
						k.appendDoor(NpcDatabase.find("[기란성] 외성문 4시 외부"), 33632, 32734, 4, 4, 33630, 4);
						k.appendDoor(NpcDatabase.find("[기란성] 외성문 8시 외부"), 33580, 32677, 4, 6, 32676, 4);
						k.appendDoor(NpcDatabase.find("[기란성] 외성문 4시 내부"), 33632, 32702, 4, 4, 33630, 4);
						k.appendDoor(NpcDatabase.find("[기란성] 외성문 8시 내부"), 33609, 32677, 4, 6, 32676, 4);
						k.appendDoor(NpcDatabase.find("[기란성] 외성문 2시 내부"), 33655, 32677, 4, 2, 32676, 4);
						// 문지기
						k.appendDoorman(NpcDatabase.find("[기란성] 내성 문지기"), 33633, 32662, 4, 4);
						k.appendDoorman(NpcDatabase.find("[기란성] 외성 4시 외부 문지기"), 33635, 32738, 4, 4);
						k.appendDoorman(NpcDatabase.find("[기란성] 외성 8시 외부 문지기"), 33578, 32675, 4, 6);
						k.appendDoorman(NpcDatabase.find("[기란성] 외성 4시 내부 문지기"), 33635, 32704, 4, 4);
						k.appendDoorman(NpcDatabase.find("[기란성] 외성 8시 내부 문지기"), 33607, 32679, 4, 6);
						k.appendDoorman(NpcDatabase.find("[기란성] 외성 2시 내부 문지기"), 33657, 32679, 4, 2);
						// 수호탑
						if(Lineage.server_version>163)
							k.appendCastleTop(NpcDatabase.findNameid("$1435"), 33632, 32677, 4, 0);
						// 깃발 등록.
						k.appendFlag();
						// 내성 근위병
						k.appendGuard(NpcDatabase.findNameid("$475"), 32725, 32792, 52, 2);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32725, 32796, 52, 2);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32725, 32803, 52, 2);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32725, 32807, 52, 2);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32732, 32792, 52, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32732, 32796, 52, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32732, 32803, 52, 6);
						k.appendGuard(NpcDatabase.findNameid("$475"), 32732, 32807, 52, 6);
						break;
					case Lineage.KINGDOM_ABYSS:
						if(Lineage.server_version<200)
							continue;
						// 외성문
						k.appendDoor(NpcDatabase.find("[지저성] 외성문 7시"), 32780, 32858, 66, 6, 32857, 4);
						k.appendDoor(NpcDatabase.find("[지저성] 외성문 4시"), 32812, 32887, 66, 4, 32810, 4);
						// 문지기
						k.appendDoorman(NpcDatabase.find("[지저성] 외성 문지기"), 32814, 32895, 66, 6);
						k.appendDoorman(NpcDatabase.find("[지저성] 외성 문지기"), 32810, 32877, 66, 2);
						k.appendDoorman(NpcDatabase.find("[지저성] 외성 문지기"), 32789, 32859, 66, 0);
						k.appendDoorman(NpcDatabase.find("[지저성] 외성 문지기"), 32774, 32857, 66, 4);
						k.appendDoorman(NpcDatabase.find("[지저성] 내성 문지기"), 32852, 32806, 66, 2);
						k.appendDoorman(NpcDatabase.find("[지저성] 내성 문지기"), 32843, 32814, 66, 6);
						// 수호탑
						if(Lineage.server_version>163)
							k.appendCastleTop(NpcDatabase.findNameid("$1435"), 32829, 32818, 66, 0);
						// 난쟁이 경비병
						k.appendGuard(NpcDatabase.findNameid("$58 $240"), 32807, 32893, 66, 4);
						k.appendGuard(NpcDatabase.findNameid("$58 $240"), 32815, 32892, 66, 4);
						k.appendGuard(NpcDatabase.findNameid("$58 $240"), 32815, 32822, 66, 6);
						k.appendGuard(NpcDatabase.findNameid("$58 $240"), 32840, 32810, 66, 6);
						k.appendGuard(NpcDatabase.findNameid("$58 $240"), 32840, 32818, 66, 6);
						k.appendGuard(NpcDatabase.findNameid("$58 $240"), 32825, 32833, 66, 4);
						k.appendGuard(NpcDatabase.findNameid("$58 $240"), 32775, 32865, 66, 6);
						k.appendGuard(NpcDatabase.findNameid("$58 $240"), 32775, 32855, 66, 6);
						// 깃발 등록.
						k.appendFlag();
						break;
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readKingdom()\r\n", KingdomController.class.toString());
			lineage.share.System.println(e);
		}
	}
	
	/**
	 * 사용자와 연결된 성객체 찾아서 리턴.
	 * @param pc
	 * @return
	 */
	static public Kingdom find(PcInstance pc){
		for(Kingdom k : list){
			if(k.getClanId()!=0 && k.getClanId()==pc.getClanId())
				return k;
		}
		return null;
	}
	
	/**
	 * 사용자 이름과 연결된 성객체 찾아서 리턴.
	 * @param name
	 * @return
	 */
	static public Kingdom find(String name){
		for(Kingdom k : list){
			if(k.getAgentName().equalsIgnoreCase(name))
				return k;
		}
		return null;
	}
	
	/**
	 * 혈맹 이름으로 성객체 찾아서 리턴.
	 * @param name
	 * @return
	 */
	static public Kingdom findClanName(String name){
		for(Kingdom k : list){
			if(k.getClanName().equalsIgnoreCase(name))
				return k;
		}
		return null;
	}
	
	/**
	 * 성 고유값을 이용해 객체 찾기.
	 * @param uid
	 * @return
	 */
	static public Kingdom find(int uid){
		for(Kingdom k : list){
			if(k.getUid() == uid)
				return k;
		}
		return null;
	}
	
	/**
	 * 외성 내부에 있는지 확인하고 해당 성객체 리턴.
	 * @param o
	 * @return
	 */
	static public Kingdom findKingdomLocation(object o){
		try {
			for(int[] i : Lineage.KINGDOMLOCATION){
				// 초기화 안된건 무시.
				if(i[0]==0)
					continue;
				// 외성 내부 좌표 확인.
				if(i[0]<=o.getX() && i[1]>=o.getX() && i[2]<=o.getY() && i[3]>=o.getY() && i[4]==o.getMap())
					return find(i[5]);
				// 내성 맵 확인. (4번맵은 무시.)
				Kingdom k = find(i[5]);
				if(k!=null && k.getMap()==o.getMap() && k.getMap()!=4)
					return k;
			}
		} catch (Exception e) { }
		return null;
	}
	
	/**
	 * 내성에 있는지 확인.
	 * @param o
	 * @return
	 */
	static public boolean isKingdomInsideLocation(object o){
		for(int[] i : Lineage.KINGDOMLOCATION){
			// 내성 맵 확인. (4번맵은 무시.)
			Kingdom k = find(i[5]);
			if(k!=null && k.getMap()==o.getMap() && k.getMap()!=4)
				return true;
		}
		return false;
	}
	
	/**
	 * 외성 내부에 잇는지 체크
	 * @return
	 */
	static public boolean isKingdomLocation(object o){
		for(int[] i : Lineage.KINGDOMLOCATION){
			if(i[0]==0)
				continue;
			if(i[0]<=o.getX() && i[1]>=o.getX() && i[2]<=o.getY() && i[3]>=o.getY() && i[4]==o.getMap())
				return true;
		}
		return false;
	}
	
	/**
	 * 외성 내부에 잇는지 체크
	 * @return
	 */
	static public boolean isKingdomLocation(object o, int idx){
		if(Lineage.KINGDOMLOCATION[idx][0]<=o.getX() && Lineage.KINGDOMLOCATION[idx][1]>=o.getX() && Lineage.KINGDOMLOCATION[idx][2]<=o.getY() && Lineage.KINGDOMLOCATION[idx][3]>=o.getY() && Lineage.KINGDOMLOCATION[idx][4]==o.getMap())
			return true;
		return false;
	}
	
	/**
	 * 공금 입출금에 대한 로그기록 추출 함수.
	 *  : 어제에 대한 수입 및 지출값 추출.
	 * @return
	 */
	static public void getTaxLogYesterday(Kingdom kingdom, Map<String, Integer> r_list){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		java.sql.Date date = new java.sql.Date(System.currentTimeMillis() - (1000*60*60*24));
		try {
			con = DatabaseConnection.getLineage();
			// 존재여부 확인.
			st = con.prepareStatement("SELECT * FROM kingdom_tax_log WHERE kingdom=? AND date=?");
			st.setInt(1, kingdom.getUid());
			st.setDate(2, date);
			rs = st.executeQuery();
			while(rs.next())
				r_list.put(rs.getString("type"), rs.getInt("tax"));
		} catch (Exception e) {
			lineage.share.System.printf("%s : getTaxLogYesterday(Kingdom kingdom, Map<String, Integer> r_list)\r\n", KingdomController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * 타이머에서 주기적으로 호출함.
	 */
	static public void toTimer(long time){
		Connection con = null;
		try {
			now_date.setTime(time);
			for(Kingdom k : list){
				// 공성전 진행중일땐 종료 시간만 체크.
				if(k.isWar()){
					// 종료시간일경우 처리.
					if(k.getWarDayEnd() <= time)
						k.toStopWar(time);
					
				}else{
					kingdom_date.setTime(k.getTaxDay());
					
					// 세율설정후 하루가 지났는지 확인.
					if(now_date.getDate() != kingdom_date.getDate()){
						if(con == null)
							con = DatabaseConnection.getLineage();
						// 수정된 세율 적용하기.
						k.setTaxRate(k.getTaxRateTomorrow());
						k.setTaxRateTomorrow(Lineage.min_tax);
						// 적용된 세율 날자 변경.
						k.setTaxDay(time);
						// 작성된 로그 디비에 기록.
						toSaveTaxLog(con, k, kingdom_date);
						// 걷어들인 세금 tax_total 에 +@
						list_temp.clear();
						int a = 0;
						for(KingdomTaxLog ktl : k.getTaxLog()){
							kingdom_date.setTime(ktl.getDate());
							// 오늘날자와 다른것만 처리하기 위해.
							if(now_date.getDate() != kingdom_date.getDate()){
								// 지급될값 증가
								a += ktl.getTax();
								// 제거목록에 등록.
								list_temp.add(ktl);
							}
						}
						k.setTaxTotal( k.getTaxTotal()+a );
						// 작성된 로그 메모리 제거.
						for(KingdomTaxLog ktl : list_temp)
							k.getTaxLog().remove(ktl);
					}
					
					// 공성치룬후 하루가 지났는지 체크.
					if(k.getWarDayEnd()+(1000*60*60*24) <= time){
						// 공성 시간을 아직 설정하지 않았다면 공성시간 젤 마지막 값으로 지정하기.
						if(k.getWarDay() == 0){
							k.toWardaySetting();
							k.setWarDay(k.getListWarday().get(5));
						}
					}
					
					// 공성전 시간인지 체크.
					if(k.getWarDay()!=0 && k.getWarDay()<=time)
						k.toStartWar(time);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : toTimer(long time)\r\n", KingdomController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
	}
	
}
