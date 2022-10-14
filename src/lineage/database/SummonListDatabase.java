package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Monster;
import lineage.bean.database.SummonList;
import lineage.share.TimeLine;
import lineage.world.object.Character;

public class SummonListDatabase {

	static private List<SummonList> list;
	
	static public void init(Connection con){
		TimeLine.start("SummonListDatabase..");
		
		list = new ArrayList<SummonList>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM summon_list");
			rs = st.executeQuery();
			while(rs.next()){
				Monster monster = MonsterDatabase.find(rs.getString("name"));
				if(monster == null)
					continue;
				
				SummonList s = new SummonList();
				s.setUid(rs.getInt("uid"));
				s.setName(monster.getName());
				s.setMinLv(rs.getInt("minLv"));
				s.setMaxLv(rs.getInt("maxLv"));
				s.setNeedCha(rs.getInt("needCha"));
				s.setSummonLv(rs.getInt("summon_lv"));
				s.setSummonHp(rs.getInt("summon_hp"));
				s.setSummonMp(rs.getInt("summon_mp"));
				s.setSummonStr(rs.getInt("summon_str"));
				s.setSummonDex(rs.getInt("summon_dex"));
				s.setSummonCon(rs.getInt("summon_con"));
				s.setSummonWis(rs.getInt("summon_wis"));
				s.setSummonInt(rs.getInt("summon_int"));
				s.setSummonCha(rs.getInt("summon_cha"));
				s.setMonster(monster);
				
				list.add(s);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", SummonListDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 서먼몬스터시전시 호출됨.
	 *  : SummonController.toSummonMonster(Character cha, int time)
	 *  : cha객체에 레벨에 맞는 SummonList 를 찾아서 리턴함
	 * @param cha
	 * @return
	 */
	static public SummonList summon(Character cha){
		SummonList sl = null;
		// 검색.
		for(SummonList s : list){
			if(s.getMinLv()==0){
				sl = s.getMaxLv()>=cha.getLevel() ? s : null;
			}else if(s.getMaxLv()==0){
				sl = s.getMinLv()<=cha.getLevel() ? s : null;
			}else if(s.getMinLv()<=cha.getLevel() && s.getMaxLv()>=cha.getLevel()){
				sl = s;
			}
			// 찾앗다면 종료.
			if(sl != null)
				break;
		}
		return sl;
	}
}
