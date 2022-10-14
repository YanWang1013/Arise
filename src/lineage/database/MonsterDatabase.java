package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Exp;
import lineage.bean.database.Monster;
import lineage.share.Lineage;
import lineage.share.System;
import lineage.share.TimeLine;

public final class MonsterDatabase {
	
	static private List<Monster> list;

	static public void init(Connection con){
		TimeLine.start("MonsterDatabase..");

		list = new ArrayList<Monster>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster");
			rs = st.executeQuery();
			while(rs.next()){
				Monster m = new Monster();
				m.setName(rs.getString("name"));
				m.setNameId(rs.getString("name_id"));
				m.setGfx(rs.getInt("gfx"));
				m.setGfxMode(rs.getInt("gfx_mode"));
				m.setLevel(rs.getInt("level"));
				m.setHp(rs.getInt("hp"));
				m.setMp(rs.getInt("mp"));
				m.setStealHp(rs.getInt("steal_hp"));
				m.setStealMp(rs.getInt("steal_mp"));
				m.setTicHp(rs.getInt("tic_hp"));
				m.setTicMp(rs.getInt("tic_mp"));
				m.setStr(rs.getInt("str"));
				m.setDex(rs.getInt("dex"));
				m.setCon(rs.getInt("con"));
				m.setInt(rs.getInt("int"));
				m.setWis(rs.getInt("wis"));
				m.setCha(rs.getInt("cha"));
				m.setMr(rs.getInt("mr"));
				m.setAc(rs.getInt("ac"));
				m.setExp(rs.getInt("exp"));
				m.setLawful(rs.getInt("lawful"));
				m.setSize(rs.getString("size"));
				m.setFamily(rs.getString("family"));
				m.setAtkType(rs.getInt("atk_type"));
				m.setAtkRange(rs.getInt("atk_range"));
				m.setAtkInvis(rs.getString("atk_invis").equalsIgnoreCase("true"));
				m.setAtkPoly(rs.getString("atk_poly").equalsIgnoreCase("true"));
				m.setDie(rs.getString("is_die").equalsIgnoreCase("true"));
				m.setPickup(rs.getString("is_pickup").equalsIgnoreCase("true"));
				m.setTame(rs.getString("is_tame").equalsIgnoreCase("true"));
				m.setRevival(rs.getString("is_revival").equalsIgnoreCase("true"));
				m.setToughskin(rs.getString("is_toughskin").equalsIgnoreCase("true"));
				m.setAdenDrop(rs.getString("is_adendrop").equalsIgnoreCase("true"));
				m.setBuff(rs.getString("is_buff").equalsIgnoreCase("true"));
				m.setResistanceEarth(rs.getInt("resistance_earth"));
				m.setResistanceFire(rs.getInt("resistance_fire"));
				m.setResistanceWind(rs.getInt("resistance_wind"));
				m.setResistanceWater(rs.getInt("resistance_water"));
				m.setResistanceUndead(rs.getInt("resistance_undead"));
				m.setArrowGfx( rs.getInt("arrowGfx") );
				
				m.setMsgAtkTime( init(rs.getString("msg_atk"), m.getMsgAtk()) * 1000 );
				m.setMsgDieTime( init(rs.getString("msg_die"), m.getMsgDie()) * 1000 );
				m.setMsgSpawnTime( init(rs.getString("msg_spawn"), m.getMsgSpawn()) * 1000 );
				m.setMsgEscapeTime( init(rs.getString("msg_escape"), m.getMsgEscape()) * 1000 );
				m.setMsgWalkTime( init(rs.getString("msg_walk"), m.getMsgWalk()) * 1000 );

				// exp 테이블값 의존한 경험치지급 값 설정.
				if(Lineage.monster_level_exp){
					Exp exp = ExpDatabase.find(m.getLevel());
					if(exp != null){
						int i = 1;
						if(exp.getLevel() < 48)
							i = 1;
						else if(exp.getLevel() < 65)
							i = 2;
						else if(exp.getLevel() < 70)
							i = 3;
						else if(exp.getLevel() < 75)
							i = 4;
						else if(exp.getLevel() < 79)
							i = 5;
						else
							i = 6+(exp.getLevel()-79);
						// 레벨에따라 해당경험치에 반을 계속 나누고
						// 그값에 2%에 해당하는 값만 지급하기.
						double gab = exp.getExp();
						for(;i>1 ; --i)
							gab /= 2;
						gab /= 98;
						m.setExp( m.getExp() + (int)(Math.round(gab)) );
					}
				}
				// 라우풀 값 확인.
				if(m.getLawful()<Lineage.CHAOTIC)
					m.setLawful( m.getLawful() + Lineage.NEUTRAL );
				
				try {
					StringBuffer sb = new StringBuffer();
					StringTokenizer stt = new StringTokenizer(rs.getString("name_id"), " $ ");
					while(stt.hasMoreTokens())
						sb.append(stt.nextToken());
					m.setNameIdNumber( Integer.valueOf(sb.toString().trim()) );
				} catch (Exception e) { }

				list.add( m );
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	

	
	/**
	 * 중복 코드 방지용.
	 * @param db_msg
	 * @param m
	 */
	static private int init(String db_msg, List<String> list){
		int time = -1;
		StringTokenizer stt = new StringTokenizer(db_msg, "|");
		if(stt.countTokens() > 0){
			try {
				time = Integer.valueOf(stt.nextToken());
				while(stt.hasMoreTokens()){
					String msg = stt.nextToken().trim();
					if(msg.length()>0)
						list.add(msg);
				}
			} catch (Exception e) {
				return -1;
			}
		}
		return time;
	}
	
	
	/**
	 * 띄어쓰기 무시한 이름으로 해당 객체 name_id 찾기 함수.
	 * @param name
	 * @return
	 */
	static public int findMonsterByNameWithoutSpace(String name) {
		int name_id = 0;
		for (Monster m : list) {
			if (m != null && m.getName().replace(" ", "").equals(name)) {
				name_id = m.getNameIdNumber();
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
	static public Monster findMonster(String name) {
		for (Monster m : list) {
			if (m != null && m.getName().replace(" ", "").equals(name)) {
				return m;
			}
		}
		return null;
	}
	
	

	/**
	 * 이름으로 원하는 객체 찾기.
	 * @param name
	 * @return
	 */
	static public Monster find(String name){
		for(Monster m : list){
			if(m.getName().equalsIgnoreCase(name))
				return m;
		}
		return null;
	}
	
	static public Monster find(int name_id){
		for(Monster m : list){
			if(m.getNameIdNumber() == name_id)
				return m;
		}
		return null;
	}
	
	static public Monster findNameid(String nameid){
		for(Monster m : list){
			if(m.getNameId().equalsIgnoreCase(nameid))
				return m;
		}
		return null;
	}
	
	static public int getSize(){
		return list.size();
	}
	
	static public List<Monster> getList(){
		return list;
	}
}
