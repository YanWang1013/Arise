package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.Ip;
import lineage.share.TimeLine;

public final class BadIpDatabase {

	static private List<Ip> list;
	
	static public void init(Connection con){
		TimeLine.start("BadIpDatabase..");

		list = new ArrayList<Ip>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM bad_ip");
			rs = st.executeQuery();
			while(rs.next()){
				Ip i = new Ip();
				i.ip = rs.getString("ip");
				try{ i.time = rs.getTimestamp("register_date").getTime(); }catch(Exception e){}
				
				list.add( i );
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : void init(Connection con)\r\n", BadIpDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	static public Ip find(String ip){
		// 검색.
		for(Ip i : list){
			String check_ip = i.ip;
			// 광역밴 포인트 확인.
			int idx = check_ip.indexOf("+");
			if(idx>=0)
				check_ip = check_ip.substring(0, idx);
			if(ip.startsWith(check_ip))
				return i;
		}
		return null;
	}
	
	static public int getSize(){
		return list.size();
	}
}
