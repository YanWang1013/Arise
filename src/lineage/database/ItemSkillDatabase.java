package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemSkill;
import lineage.share.Lineage;
import lineage.share.TimeLine;

public class ItemSkillDatabase {

	static private List<ItemSkill> list;
	
	static public void init(Connection con){
		TimeLine.start("ItemSkillDatabase..");
		
		list = new ArrayList<ItemSkill>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_skill");
			rs = st.executeQuery();
			while(rs.next()){
				ItemSkill i = new ItemSkill();
				i.setName(rs.getString("name"));
				i.setItem(rs.getString("item"));
				i.setMinDmg(rs.getInt("min_dmg"));
				i.setMaxDmg(rs.getInt("max_dmg"));
				i.setRanged(rs.getInt("ranged"));
				i.setSkill(rs.getInt("skill_uid"));
				i.setDuration(rs.getInt("duration"));
				i.setEffect(rs.getInt("effect"));
				i.setEffectTarget(rs.getString("effect_target").equalsIgnoreCase("me"));
				i.setChance(rs.getInt("chance"));
				i.setOption(rs.getString("option"));
				if(rs.getString("element").equalsIgnoreCase("water"))
					i.setElement(Lineage.ELEMENT_WATER);
				else if(rs.getString("element").equalsIgnoreCase("wind"))
					i.setElement(Lineage.ELEMENT_WIND);
				else if(rs.getString("element").equalsIgnoreCase("earth"))
					i.setElement(Lineage.ELEMENT_EARTH);
				else if(rs.getString("element").equalsIgnoreCase("fire"))
					i.setElement(Lineage.ELEMENT_FIRE);
				else if(rs.getString("element").equalsIgnoreCase("laser"))
					i.setElement(Lineage.ELEMENT_LASER);
				else if(rs.getString("element").equalsIgnoreCase("poison"))
					i.setElement(Lineage.ELEMENT_POISON);
				else
					i.setElement(Lineage.ELEMENT_NONE);
				
				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemSkillDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public ItemSkill find(String item){
		for( ItemSkill is : list ){
			if(is.getItem().equalsIgnoreCase(item))
				return is;
		}
		return null;
	}
	
}
