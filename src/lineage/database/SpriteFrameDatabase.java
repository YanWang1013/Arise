package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import lineage.bean.database.SpriteFrame;
import lineage.share.TimeLine;

public final class SpriteFrameDatabase {

	static private Map<Integer, SpriteFrame> list;
	
	static public void init(Connection con){
		TimeLine.start("SpriteFrameDatabase..");
		
		list = new HashMap<Integer, SpriteFrame>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM sprite_frame");
			rs = st.executeQuery();
			while(rs.next()){
				int gfx = rs.getInt("gfx");

				SpriteFrame sf = list.get(gfx);
				if(sf == null){
					sf = new SpriteFrame();
					sf.setName(rs.getString("action_name"));
					sf.setGfx(gfx);
					list.put(gfx, sf);
				}
				
				sf.getList().put(rs.getInt("action"), rs.getInt("frame"));
				sf.getListString().put(rs.getString("action_name"), rs.getInt("frame"));
				sf.getListMode().put(rs.getString("action_name"), rs.getInt("action"));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", SpriteFrameDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	/**
	 * gfx에 해당하는action값에 프레임값을 리턴함.
	 *  : 고유 넘버로 찾음
	 */
	static public int find(int gfx, int action){
		SpriteFrame sf = list.get(gfx);
		if(sf != null){
			Integer frame = sf.getList().get(action);
			return frame==null ? 2000 : frame.intValue();
		}
		// 해당하는 모드가 없을경우 2초로 정의
		return 600;
	}
	
	/**
	 * gfx에 해당하는 action값 찾아서 프레임값 리턴.
	 *  : 문자열로 찾음.
	 * @param gfx
	 * @param mode
	 * @return
	 */
	static public int find(int gfx, String action_name){
		SpriteFrame sf = list.get(gfx);
		if(sf!=null && action_name!=null && action_name.length()>0){
			Integer frame = sf.getListString().get(action_name);
			return frame==null ? 2000 : frame.intValue();
		}
		// 해당하는 모드가 없을경우 2초로 정의
		return 600;
	}
	
	/**
	 * 액션이름에 해당하는 액션값 리턴.
	 * @param gfx
	 * @param mode
	 * @return
	 */
	static public int findMode(int gfx, String action_name){
		SpriteFrame sf = list.get(gfx);
		if(sf!=null && action_name!=null && action_name.length()>0){
			Integer action = sf.getListMode().get(action_name);
			return action==null ? 0 : action.intValue();
		}
		return -1;
	}
	
	static public int getSize(){
		return list.size();
	}
	
}
