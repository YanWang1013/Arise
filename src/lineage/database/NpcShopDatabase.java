package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.Npc;
import lineage.bean.database.Shop;
import lineage.share.TimeLine;

public final class NpcShopDatabase {

	static public void init(Connection con){
		TimeLine.start("NpcShopDatabase..");

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM npc_shop ORDER BY uid");
			rs = st.executeQuery();
			while(rs.next()){
				Npc n = NpcDatabase.find( rs.getString("name") );
				Item i = ItemDatabase.find( rs.getString("itemname") );
				if(n!=null && i!=null){
					Shop s = new Shop();
					s.setUid( rs.getInt("uid") );
					s.setNpcName( rs.getString("name") );
					s.setItemName( rs.getString("itemname") );
					s.setItemCount( rs.getInt("itemcount") );
					s.setItemBress( rs.getInt("itembress") );
					s.setItemEnLevel( rs.getInt("itemenlevel") );
					s.setItemSell( rs.getString("sell").equalsIgnoreCase("true") );
					s.setItemBuy( rs.getString("buy").equalsIgnoreCase("true") );
					s.setGamble( rs.getString("gamble").equalsIgnoreCase("true") );
					s.setPrice( rs.getInt("price") );
					s.setAdenType( rs.getString("aden_type") );
					// 버그 방지. 기본값 아데나로 설정.
					if(s.getAdenType()==null || s.getAdenType().length()<=0)
						s.setAdenType("아데나");
	
					n.getShop_list().add(s);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", NpcShopDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	
	public static void ShopInit()
	{
		Connection localConnection = null;
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		try
		{
			localConnection = DatabaseConnection.getLineage();
			localPreparedStatement = localConnection.prepareStatement("SELECT name FROM npc_shop GROUP BY name");
			localResultSet = localPreparedStatement.executeQuery();
			while (localResultSet.next())
			{
				Npc localNpc = NpcDatabase.find(localResultSet.getString("name"));
				if (localNpc != null)
					localNpc.getShop_list().clear();
			}
		}
		catch (Exception localException)
		{
			lineage.share.System.printf("%s : ShopInit()\r\n", new Object[] { NpcShopDatabase.class.toString() });
			lineage.share.System.println(localException);
		}
		finally
		{
			DatabaseConnection.close(localConnection, localPreparedStatement, localResultSet);
		}
	}
	
	/**
	 * 디비에 기록된 npc에 상점목록 제거처리하는 함수.
	 * @param con
	 * @param name
	 */
	static public void delete(Connection con, String name){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement( "DELETE FROM npc_shop WHERE name=?" );
			st.setString(1, name);
			st.executeUpdate();
			st.close();
		} catch (Exception e) {
			lineage.share.System.printf("%s : delete(Connection con, String name)\r\n", NpcShopDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 상점 목록 디비에 기록하는 함수.
	 * @param con
	 * @param list
	 */
	static public void insert(Connection con, List<Shop> list){
		PreparedStatement st = null;
		try {
			for(Shop s : list){
				st = con.prepareStatement( "INSERT INTO npc_shop SET " +
						"uid=?, name=?, itemname=?, itemcount=?, itembress=?, itemenlevel=?, sell=?, buy=?, gamble=?, price=?, aden_type=?" );
				st.setInt(1, s.getUid());
				st.setString(2, s.getNpcName());
				st.setString(3, s.getItemName());
				st.setInt(4, s.getItemCount());
				st.setInt(5, s.getItemBress());
				st.setInt(6, s.getItemEnLevel());
				st.setString(7, String.valueOf(s.isItemSell()));
				st.setString(8, String.valueOf(s.isItemBuy()));
				st.setString(9, String.valueOf(s.isGamble()));
				st.setInt(10, s.getPrice());
				st.setString(11, s.getAdenType());
				st.executeUpdate();
				st.close();
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : insert(Connection con, List<Shop> list)\r\n", NpcShopDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
}
