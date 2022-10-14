package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.database.Boss;
import lineage.bean.database.Monster;
import lineage.share.TimeLine;

public final class MonsterBossSpawnlistDatabase {

	static private List<Boss> list;
	
	static public void init(Connection con){
		TimeLine.start("MonsterBossSpawnlistDatabase..");
		
		list = new ArrayList<Boss>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM monster_spawnlist_boss");
			rs = st.executeQuery();
			while(rs.next()){
				String name = rs.getString("name");
				String monster = rs.getString("monster");
				int spawn_x = rs.getInt("spawn_x");
				int spawn_y = rs.getInt("spawn_y");
				String spawn_map = rs.getString("spawn_map");
				String spawn_time = rs.getString("spawn_time");
				Monster mon = MonsterDatabase.find(monster);
				
				if(mon != null){
					Boss b = new Boss();
					b.setName(name);
					b.setMon( mon );
					b.setX(spawn_x);
					b.setY(spawn_y);
					// 맵 구분 추출.
					StringTokenizer stt = new StringTokenizer(spawn_map, "|");
					while(stt.hasMoreTokens()){
						String map = stt.nextToken();
						if(map.length() > 0)
							b.getMap().add(Integer.valueOf(map));
					}
					// 스폰시간 구분 추출.
					stt = new StringTokenizer(spawn_time, ",");
					int[][] time = new int[stt.countTokens()][2];
					int idx = 0;
					while(stt.hasMoreTokens()){
						String boss_time = stt.nextToken();
						String boss_h = boss_time.substring(0, boss_time.indexOf(":"));
						String boss_m = boss_time.substring(boss_h.length()+1, boss_time.length());
						time[idx][0] = Integer.valueOf(boss_h);
						time[idx][1] = Integer.valueOf(boss_m);
						idx += 1;
					}
					b.setTime(time);
					
					list.add(b);
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", MonsterBossSpawnlistDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public List<Boss> getList(){
		return list;
	}
	
	static public int getSize(){
		return list.size();
	}
}
