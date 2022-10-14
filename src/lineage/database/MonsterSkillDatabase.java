package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.bean.database.Monster;
import lineage.bean.database.MonsterSkill;
import lineage.share.Lineage;
import lineage.share.TimeLine;

public class MonsterSkillDatabase {

	static public void init(Connection con){
		TimeLine.start("MonsterSkillDatabase..");
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster_skill ORDER BY monster, ranged DESC");
			rs = st.executeQuery();
			while(rs.next()){
				Monster m = MonsterDatabase.find(rs.getString("monster"));
				if(m != null){
					MonsterSkill ms = new MonsterSkill();

					ms.setUid( rs.getInt("uid") );
					ms.setName( rs.getString("name") );
					ms.setMonster( rs.getString("monster") );
					ms.setActionName( rs.getString("action_name") );
					ms.setCastGfx( rs.getInt("effect") );
					ms.setRange( rs.getInt("ranged") );
					ms.setType( rs.getString("type") );
					ms.setChance( rs.getInt("chance") );
					ms.setMsg( rs.getString("msg") );
					ms.setSkill( SkillDatabase.find(rs.getInt("skill_uid")) );
					ms.setHpConsume( rs.getInt("hpConsume") );
					ms.setMpConsume( rs.getInt("mpConsume") );
					ms.setMindmg( rs.getInt("dmgMin") );
					ms.setMaxdmg( rs.getInt("dmgMax") );
					ms.setBuffDuration( rs.getInt("duration") );
					if(rs.getString("element").equalsIgnoreCase("none"))
						ms.setElement( Lineage.ELEMENT_NONE );
					else if(rs.getString("element").equalsIgnoreCase("wind"))
						ms.setElement( Lineage.ELEMENT_WIND );
					else if(rs.getString("element").equalsIgnoreCase("water"))
						ms.setElement( Lineage.ELEMENT_WATER );
					else if(rs.getString("element").equalsIgnoreCase("earth"))
						ms.setElement( Lineage.ELEMENT_EARTH );
					else if(rs.getString("element").equalsIgnoreCase("fire"))
						ms.setElement( Lineage.ELEMENT_FIRE );
					else if(rs.getString("element").equalsIgnoreCase("laser"))
						ms.setElement( Lineage.ELEMENT_LASER );
					ms.setOption( rs.getString("option") );
					
					m.getSkillList().add(ms);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterSkillDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}

	
}
