package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.TeleportHome;
import lineage.bean.database.TeleportHome.Location;
import lineage.plugin.PluginController;
import lineage.share.TimeLine;
import lineage.util.Util;
import lineage.world.object.object;

public class TeleportHomeDatabase {

	static private List<TeleportHome> list;
	
	static public void init(Connection con){
		TimeLine.start("TeleportHomeDatabase..");
		
		list = new ArrayList<TeleportHome>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM teleport_home");
			rs = st.executeQuery();
			while(rs.next()){
				TeleportHome g = new TeleportHome();
				g.setName(rs.getString("name"));
				g.setClassType(rs.getInt("classtype"));
				g.setX1(rs.getInt("x1"));
				g.setX2(rs.getInt("x2"));
				g.setY1(rs.getInt("y1"));
				g.setY2(rs.getInt("y2"));
				g.setMap(rs.getInt("map"));
				g.appendLocation(rs.getString("goto1"));
				g.appendLocation(rs.getString("goto2"));
				g.appendLocation(rs.getString("goto3"));
				g.appendLocation(rs.getString("goto4"));
				g.appendLocation(rs.getString("goto5"));
				g.appendLocation(rs.getString("goto6"));
				
				list.add(g);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", TeleportHomeDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	/**
	 * 근처 마을로 좌표변경하는 메서드.
	 */
	static public void toLocation(object o) {
		//
		if(PluginController.init(TeleportHomeDatabase.class, "toLocation", o) != null)
			return;
		
		TeleportHome find = null;
		Location l = null;
		
		// 검색
		for(TeleportHome g : list){
			if(g.getX1() == 0){
				if(g.getMap()==o.getMap()){
					if(g.getClassType() == -1){
						find = g;
						break;
					}
					if(g.getClassType() == o.getClassType()){
						find = g;
						break;
					}
				}
			}else{
				if((o.getX()<g.getX2())&&(o.getX()>g.getX1())&&(o.getY()<g.getY2())&&(o.getY()>g.getY1())){
					find = g;
					break;
				}
			}
		}
		
		// 처리
		if(find != null)
			l = find.getListGoto().get( Util.random(0, find.getListGoto().size()-1) );
		if(l != null){
			o.setHomeMap( l.map );
			o.setHomeX( l.x );
			o.setHomeY( l.y );
		}else{
			// 그외에는 그냥 기란으로..
			o.setHomeMap( 4 );
			o.setHomeX( 33428 );
			o.setHomeY( 32823 );
		}
		
	}
	
	/**
	 * 이름과 동일한 객체 찾기.
	 * @param name
	 * @return
	 */
	static public TeleportHome find(String name) {
		for(TeleportHome t : list) {
			if(t.getName().equalsIgnoreCase(name))
				return t;
		}
		return null;
	}
}
