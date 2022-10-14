package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lineage.bean.lineage.Agit;
import lineage.bean.lineage.Clan;
import lineage.bean.lineage.Kingdom;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ClanInfo;
import lineage.network.packet.server.S_ClanMark;
import lineage.network.packet.server.S_ClanWar;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_MessageYesNo;
import lineage.network.packet.server.S_ObjectTitle;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

public final class ClanController {

	static private List<Clan> pool;				// 사용 다된 혈맹 목록
	static private Map<Integer, Clan> list;		// 혈맹 목록
	static private int next_uid;				// 혈맹 고유 키값에 사용되는 마지막 값

	/**
	 * 초기화 함수.
	 *  : 서버 가동될때 한번 호출됨.
	 *    디비 정보를 읽어서 메모리에 상주하는 역활.
	 * @param con
	 */
	static public void init(Connection con){
		TimeLine.start("ClanController..");
		
		list = new HashMap<Integer, Clan>();
		pool = new ArrayList<Clan>();

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM clan_list ORDER BY uid");
			rs = st.executeQuery();
			while(rs.next()){
				Clan c = new Clan();
				c.setUid( rs.getInt("uid") );
				c.setName( rs.getString("name") );
				c.setLord( rs.getString("lord") );
				c.setIcon( Util.StringToByte(rs.getString("icon")) );
				for(String member : rs.getString("list").split(" "))
					c.getMemberList().add(member);

				if(c.getUid()>next_uid)
					next_uid = c.getUid();
				list.put(c.getUid(), c);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ClanController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	/**
	 * 종료 함수.
	 *  : 서버가 종료될때 호출됨.
	 *    혈맹 정보를 디비에 기록하는 역활을 담당.
	 *  : 메모리 제거 처리도 덤으로
	 */
	static public void close(Connection con){
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("DELETE FROM clan_list");
			st.executeUpdate();
			st.close();

			// close 함수에서도 요청하기 때문에 동기화 작업을 함.
			synchronized (list) {
				for(Clan c : list.values()){
					StringBuffer icon = new StringBuffer();
					if(c.getIcon() != null){
						for (int i = 0; i < c.getIcon().length; ++i)
							icon.append( String.format("%02x", c.getIcon()[i] & 0xff) );
					}
	
					st = con.prepareStatement("INSERT INTO clan_list SET uid=?, name=?, lord=?, icon=?, list=?");
					st.setInt(1, c.getUid());
					st.setString(2, c.getName());
					st.setString(3, c.getLord());
					st.setString(4, icon.toString());
					st.setString(5, c.getMemberNameList());
					st.executeUpdate();
					st.close();
				}
	
				list.clear();
			}
		} catch(Exception e) {
			lineage.share.System.printf("%s : close(Connection con)\r\n", ClanController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}

	/**
	 * 월드 접속시 호출됨.
	 * @param pc
	 */
	static public void toWorldJoin(PcInstance pc){
		Clan c = find(pc);
		if(c!=null)
			c.toWorldJoin(pc);
	}

	/**
	 * 월드 나갈때 호출됨.
	 * @param pc
	 */
	static public void toWorldOut(PcInstance pc){
		try {
			Clan c = find(pc);
			if(c!=null)
				c.toWorldOut(pc);
		} catch (Exception e) {
			lineage.share.System.printf("%s : toWorldOut(PcInstance pc)\r\n", ClanController.class.toString());
			lineage.share.System.println(e);
		}
	}

	/**
	 * 혈맹 창설 뒤처리 함수.
	 * @param pc
	 * @param clan_name
	 */
	static public void toCreate(PcInstance pc, String clan_name){
		if(!isClanName(clan_name)){
			// 혈맹 객체 풀에서 꺼내기. null이라면 새로 생성.
			Clan c = getPool();
			if(c==null)
				c = new Clan();
			// 정보 세팅.
			c.setUid(nextUid());
			c.setName(clan_name);
			c.setLord(pc.getName());
			c.getMemberList().add(pc.getName());
			c.toWorldJoin(pc);
			// 사용자에게 혈맹이름와 uid 넣기.
			pc.setClanId(c.getUid());
			pc.setClanName(clan_name);
			// 호칭 초기화
			pc.setTitle(null);
			pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), pc), true);
			//84 \f1%0 혈맹이 창설되었습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 84, clan_name), true);
			// 메모리에 등록.
			list.put(c.getUid(), c);
		}else{
			//99 \f1같은 이름의 혈맹이 존재합니다.
			pc.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 99) );
		}
	}

	/**
	 * 혈맹 탈퇴 처리 함수.
	 * @param pc
	 */
	static public void toOut(PcInstance pc){
		Clan c = find(pc);
		if(c!=null){
			if(pc.getClassType()==Lineage.LINEAGE_CLASS_ROYAL){
				
				//665 \f1성이나 아지트를 소유한 상태에서는 혈맹을 해산할 수 없습니다.
				if(AgitController.find(pc)!=null || KingdomController.find(pc)!=null){
					c.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 665) );
					return;
				}
				
				//269 %1혈맹의 혈맹주 %0%s 혈맹을 해산시켰습니다.
				c.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 269, c.getLord(), c.getName()) );
				// 접속한 혈맹원들 정보 변경.
				for(PcInstance use : c.getList()){
					use.setClanId(0);
					use.setClanName(null);
					use.setTitle(null);
					use.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), use), true);
				}
				// 멤버들 이름을 참고해서 디비에 있는 회원정보 변경.
				Connection con = null;
				PreparedStatement st = null;
				try {
					con = DatabaseConnection.getLineage();
					for(String member : c.getMemberList()){
						st = con.prepareStatement("UPDATE characters SET clanID=0, clanNAME='', title='' WHERE LOWER(name)=?");
						st.setString(1, member);
						st.executeUpdate();
						st.close();
					}
				} catch (Exception e) {
					lineage.share.System.printf("%s : toOut(PcInstance pc)\r\n", ClanController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st);
				}
				// 관리중이던 목록에서 제거.
				list.remove(c.getUid());
				// 정보 초기화.
				c.close();
				// 재사용을 위해 풀에 넣기.
				setPool(c);

			}else{
				//178 \f1%0%s %1 혈맹을 탈퇴했습니다.
				c.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 178, pc.getName(), c.getName()) );
				// 관리목록에서 제거.
				c.toWorldOut(pc);
				c.getMemberList().remove(pc.getName());
				// 탈퇴 회원 정보 변경.
				pc.setClanId(0);
				pc.setClanName(null);
				pc.setTitle(null);
				pc.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), pc), true);
			}
		}
	}

	/**
	 * 혈맹원 추방처리 함수.
	 * @param pc
	 * @param member
	 */
	static public void toKin(PcInstance pc, String member){
		Clan c = find(pc);
		if(c!=null){
			if(pc.getClassType()==Lineage.LINEAGE_CLASS_ROYAL){
				if(c.getMemberList().contains(member)){
					// 혈맹원 정보 변경.
					PcInstance use = World.findPc(member);
					if(use!=null){
						use.setClanId(0);
						use.setClanName(null);
						use.setTitle(null);
						use.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), use), true);
						//238 당신은 %0 혈맹으로부터 추방되었습니다.
						use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 238, c.getName()));
					}
					// 디비 정보 변경.
					Connection con = null;
					PreparedStatement st = null;
					try {
						con = DatabaseConnection.getLineage();
						st = con.prepareStatement("UPDATE characters SET clanID=0, clanNAME='', title='' WHERE LOWER(name)=?");
						st.setString(1, member);
						st.executeUpdate();
						st.close();
					} catch (Exception e) {
						lineage.share.System.printf("%s : toKin(PcInstance pc, String member)\r\n", ClanController.class.toString());
						lineage.share.System.println(e);
					} finally {
						DatabaseConnection.close(con, st);
					}
					// 혈맹 정보 변경.
					c.getList().remove(use);
					c.getMemberList().remove(member);
					//240 %0%o 당신의 혈맹에서 추방하였습니다.
					c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 240, member));
				}else{
					// 혈맹원이 존재하지 않음..
				}
			}else{
				// 군주만 할수 있어야됨..
			}
		}
	}

	/**
	 * 가입 요청 처리 함수.
	 * @param pc	: 요청자
	 * @param use	: 찾은 사용자
	 */
	static public void toJoin(PcInstance pc, PcInstance use){
		if(pc.getClassType()==Lineage.LINEAGE_CLASS_ROYAL){
			if(pc.getClassGfx()==0)
				//87 \f1다른 혈맹에 가입하다니요! 당신은 왕자입니다!
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 87));
			else
				//88 \f1다른 혈맹에 가입하다니요! 당신은 공주입니다!
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 88));
		}else{
			if(pc.getClanId()>0){
				//89 \f1이미 혈맹에 가입했습니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 89));
			}else{
				if(use.getClassType()==Lineage.LINEAGE_CLASS_ROYAL){
					Clan c = find(use);
					if(c!=null){
//						if(c.getTempPc() == null){
							//97 %0%s 혈맹에 가입하기를 원합니다. 승낙하시겠습니까? (Y/N)
							use.toSender(S_MessageYesNo.clone(BasePacketPooling.getPool(S_MessageYesNo.class), 97, pc.getName()));
							c.setTempPc(pc);
//						}
					}else{
						// \f1%0%d 혈맹을 창설하지 않은 상태입니다.
						pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 90, use.getName()));
					}
				}else{
					// \f1%0%d 왕자나 공주가 아닙니다.
					use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 92));
				}
			}
		}
	}

	/**
	 * 가입 요청에 대한 마지막 승낙여부 처리 함수.
	 * @param pc
	 * @param yes
	 */
	static public void toJoinFinal(PcInstance pc, boolean yes){
		Clan c = find(pc);
		if(c!=null){
			PcInstance use = c.getTempPc();
			if(use!=null && !use.isWorldDelete()){
				if(yes){
					if(c.getMemberList().size()<getClanMemberMaxSize(pc) && use.getLevel()>4){
						// 가입자 정보 갱신.
						use.setClanId(c.getUid());
						use.setClanName(c.getName());
						use.setTitle(null);
						// 패킷 처리
						use.toSender(S_ObjectTitle.clone(BasePacketPooling.getPool(S_ObjectTitle.class), use), true);
						//94 \f1%0%o 혈맹의 일원으로 받아들였습니다.
						c.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 94, use.getName()));
						//95 \f1%0 혈맹에 가입하였습니다.
						use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 95, c.getName()));
						// 혈맹 관리목록 갱신
						c.getMemberList().add(use.getName());
						c.getList().add(use);
					}else{
						//188 %0%s 당신을 혈맹 구성원으로 받아들일 수 없습니다.
						use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 188, c.getLord()));
					}

				}else{
					//237 %0 혈맹이 당신의 제안을 거절하였습니다.
					use.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 237, c.getName()));
				}
			}
			c.setTempPc(null);
		}
	}
	
	/**
	 * 콜클랜에 대한 응답 처리 함수.
	 * @param pc
	 * @param yes
	 */
	static public void toCallPledgeMember(PcInstance pc, boolean yes){
		Clan c = find(pc);
		if(c!=null){
			PcInstance royal = c.getRoyal();
			if(royal != null){
				// 좌표 검색.
				
				// 처리.
				if(yes){
					pc.toTeleport(royal.getX(), royal.getY(), royal.getMap(), true);
				}else{
					// \f1%0%s 당신의 요청을 거절하였습니다.
					royal.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 96, pc.getName()));
				}
			}
		}
	}
	
	/**
	 * 혈맹 문장 업로드 처리 함수.
	 * @param pc
	 * @param icon
	 */
	static public void toMarkUpload(PcInstance pc, byte[] icon){
		Clan c = find(pc);
		if(c!=null){
			// 전쟁중에는 업로드 안되도록 작업해야됨.
			if(c.getLord().equalsIgnoreCase(pc.getName())){
				int old_uid = c.getUid();
				int new_uid = nextUid();
				// 이전 전체 관리목록에서 제거.
				list.remove(old_uid);
				// uid값 새로 갱신
				c.setUid(new_uid);
				c.setIcon(icon);
				// 아지트 갱신
				Agit agit = AgitController.find(pc);
				if(agit != null)
					agit.setClanId(c.getUid());
				// 성 갱신
				Kingdom kingdom = KingdomController.find(pc);
				if(kingdom != null)
					kingdom.setClanId(c.getUid());
				// 혈맹원들 uid갱신
				for(PcInstance use : c.getList())
					use.setClanId(c.getUid());
				// 디비 정보 변경.
				Connection con = null;
				PreparedStatement st = null;
				try {
					con = DatabaseConnection.getLineage();
					// 케릭터 클랜아이디 갱신.
					// old
					/*for(String member : c.getMemberList()){
						st = con.prepareStatement("UPDATE characters SET clanID=? WHERE LOWER(name)=?");
						st.setInt(1, c.getUid());
						st.setString(2, member);
						st.executeUpdate();
						st.close();
					}*/
					// new
					st = con.prepareStatement("UPDATE characters SET clanID=? WHERE clanID=?");
					st.setInt(1, new_uid);
					st.setInt(2, old_uid);
					st.executeUpdate();
					st.close();
					// 창고 클랜아이디 갱신.
					st = con.prepareStatement("UPDATE warehouse_clan SET clan_id=? WHERE clan_id=?");
					st.setInt(1, new_uid);
					st.setInt(2, old_uid);
					st.executeUpdate();
					st.close();
				} catch (Exception e) {
					lineage.share.System.printf("%s : toMarkUpload(PcInstance pc, byte[] icon)\r\n", ClanController.class.toString());
					lineage.share.System.println(e);
				} finally {
					DatabaseConnection.close(con, st);
				}
				// 관리목록에 재 등록.
				list.put(c.getUid(), c);
			}else{
				// 219 \f1문장을 업로드할 수 있는 것은 왕자 혹은 공주뿐입니다.
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 219));
			}
		}
	}
	
	/**
	 * uid에 맞는 혈맹 찾아서 해당 하는 혈맹의 문장 요청 처리하는 함수.
	 * @param pc
	 * @param uid
	 */
	static public void toMarkDownload(PcInstance pc, int uid){
		Clan c = find(uid);
		if(c!=null && c.getIcon()!=null && c.getIcon().length>0)
			pc.toSender(S_ClanMark.clone(BasePacketPooling.getPool(S_ClanMark.class), c));
	}

	/**
	 *  /혈맹 커맨드입력히 호출되는 함수.
	 */
	static public void toInfo(PcInstance pc){
		Clan c = find(pc);
		if(c != null){
			if(pc.getClassType() == Lineage.LINEAGE_CLASS_ROYAL)
				pc.toSender(S_ClanInfo.clone(BasePacketPooling.getPool(S_ClanInfo.class), c, "pledgeM"));
			else
				pc.toSender(S_ClanInfo.clone(BasePacketPooling.getPool(S_ClanInfo.class), c, "pledge"));
		}
	}
	
	/**
	 * 전쟁 선포 처리 함수.
	 * @param pc
	 * @param name
	 */
	static public void toWar(PcInstance pc, String name){
		// \f1오직 왕자와 공주만이 전쟁을 선언할 수 있습니다.
		if(pc.getClassType() != Lineage.LINEAGE_CLASS_ROYAL){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 478));
			return;
		}
		// \f1레벨 15 이하의 군주는 전쟁을 선포할 수 없습니다.
		if(pc.getLevel()<15){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 232));
			return;
		}
		Clan clan = find(pc);
		// \f1전쟁을 하기 위해서는 먼저 혈맹을 창설하셔야 합니다.
		if(clan == null){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 272));
			return;
		}
		// \f1당신의 혈맹은 이미 전쟁 중입니다.
		if(clan.getWarClan() != null){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 234));
			return;
		}
		Kingdom kingdom = KingdomController.findClanName(name);
		// 공성전을 선언하기 위해서는 적어도 25 레벨이 되어야 합니다.
		if(kingdom!=null && pc.getLevel()<25){
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 475));
			return;
		}
		
		if(kingdom == null){
			// 혈전
			
		}else{
			// 공성전
			// 당신은 이미 성을 소유하고 있으므로 다른 성에 도전할 수 없습니다.
			if(AgitController.find(pc)!=null || KingdomController.find(pc)!=null){
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 474));
				return;
			}
			// 이미 공성전에 참여하셨습니다.
			if(kingdom.getListWar().contains(pc.getClanName())){
				pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 522));
				return;
			}
			// 혈맹원중 한명이라도 해당성에 내외성존에 있는지 확인.
			for(PcInstance use : ClanController.find(pc).getList()){
				if(kingdom.getMap()==use.getMap() || KingdomController.isKingdomLocation(use, kingdom.getUid()))
					return;
			}
			// 전쟁 처리 목록에 추가하기.
			kingdom.getListWar().add(pc.getClanName());
			// 전쟁중일경우 패킷 처리.
			if(kingdom.isWar())
				World.toSender(S_ClanWar.clone(BasePacketPooling.getPool(S_ClanWar.class), 1, pc.getClanName(), name));
		}
	}
	
	/**
	 * 혈맹이름으로 혈맹객체 찾기.
	 * @param clan_name
	 * @return
	 */
	static public Clan find(String clan_name){
		for(Clan c : list.values()){
			if(c.getName().equalsIgnoreCase(clan_name))
				return c;
		}
		return null;
	}

	/**
	 * 사용자와 연결된 혈맹객체 찾아서 리턴.
	 * @param pc
	 * @return
	 */
	static public Clan find(PcInstance pc){
		return list.get(pc.getClanId());
	}
	
	/**
	 * 혈맹 고유 아이디로 혈맹 객체 찾기.
	 * @param uid
	 * @return
	 */
	static public Clan find(int uid){
		return list.get(uid);
	}
	
	/**
	 * 창설된 혈맹 목록에서 동일한 혈맹 이름이 존재하는지 확인해주는 함수.
	 * @param con
	 * @param name
	 * @return
	 */
	static private boolean isClanName(String name){
		for(Clan c : list.values()){
			if(c.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	/**
	 * 혈맹 고유uid값의 다음값을 추출.
	 * @return
	 */
	static private int nextUid(){
		return ++next_uid;
	}

	/**
	 * 풀에 있는 혈맹객체 재사용하기 위해 꺼내기.
	 * @return
	 */
	static private Clan getPool(){
		Clan c = null;
		if(pool.size()>0){
			c = pool.get(0);
			pool.remove(0);
		}
//		lineage.share.System.println("remove : "+pool.size());
		return c;
	}

	/**
	 * 사용 완료된 객체 풀에 넣기.
	 * @param c
	 */
	static private void setPool(Clan c){
		pool.add(c);
		
//		lineage.share.System.println("append : "+pool.size());
	}

	/**
	 * 해당 사용자에 카리 및 퀘스트수행 정보를 참고해서
	 * 받아 들일수 있는 최대 혈맹원수 연산후 리턴.
	 */
	static private int getClanMemberMaxSize(PcInstance pc){
		if(pc.getClassType()==Lineage.LINEAGE_CLASS_ROYAL){
			int size = pc.getTotalCha() * 2;
			if(pc.getLevel() >= 50)
				size = pc.getTotalCha() * 3;
			/*if(pc.getQuest45() == -1)
				size = size * 2;
			if(pc.getQuest50() == -1)
				size = size * 3;*/
			return size;
		}
		return 0;
	}
	
	static public int getPoolSize(){
		return pool.size();
	}

}
