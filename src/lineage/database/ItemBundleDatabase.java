package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemBundle;
import lineage.share.TimeLine;

public class ItemBundleDatabase {

	static private List<ItemBundle> list;
	
	static public void init(Connection con){
		TimeLine.start("ItemBundleDatabase..");
		
		//
		list = new ArrayList<ItemBundle>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_bundle");
			rs = st.executeQuery();
			while(rs.next()){
				ItemBundle ib = new ItemBundle();
				ib.setName(rs.getString("name"));
				ib.setItem(rs.getString("item"));
				ib.setItemBless(rs.getInt("item_bless"));
				ib.setItemEnchant(rs.getInt("item_enchant"));
				ib.setItemCountMin(rs.getInt("item_count_min"));
				ib.setItemCountMax(rs.getInt("item_count_max"));
				ib.setItemChance(rs.getInt("item_chance"));
				if(ib.getItemCountMin() <= 0)
					ib.setItemCountMin(1);
				if(ib.getItemCountMax() <= 0)
					ib.setItemCountMax(1);
				
				list.add(ib);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemBundleDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 해당하는 아이템을 찾아서 목록을 만든후 리턴함.
	 * @param r_list
	 * @param name
	 */
	static public void find(List<ItemBundle> r_list, String name){
		r_list.clear();
		for(ItemBundle ib : list){
			if(ib.getName().equalsIgnoreCase(name))
				r_list.add(ib);
		}
	}
	
}
