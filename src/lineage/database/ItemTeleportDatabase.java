package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemTeleport;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_ObjectLock;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.controller.ChattingController;
import lineage.world.controller.LocationController;
import lineage.world.object.object;

public class ItemTeleportDatabase {

	static private List<ItemTeleport> list;
	
	/**
	 * 초기화 처리 함수.
	 */
	static public void init(Connection con){
		TimeLine.start("ItemTeleport..");
		
		list = new ArrayList<ItemTeleport>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_teleport");
			rs = st.executeQuery();
			while(rs.next()){
				ItemTeleport i = new ItemTeleport();
				i.setUid(rs.getInt("uid"));
				i.setName(rs.getString("name"));
				i.setX(rs.getInt("goto_x"));
				i.setY(rs.getInt("goto_y"));
				i.setMap(rs.getInt("goto_map"));
				i.setHeading(rs.getInt("goto_heading"));
				i.setLevel(rs.getInt("if_level"));
				i.setClassType(rs.getInt("if_class"));
				
				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemTeleportDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		TimeLine.end();
	}
	
	static public ItemTeleport find(int uid){
		for( ItemTeleport it : list ){
			if(it.getUid() == uid)
				return it;
		}
		return null;
	}
	
	static public void toTeleport(ItemTeleport it, object o, boolean message){
		if(it==null || o==null){
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return;
		}
		
		// 클레스 확인.
		if((it.getClassType() & Lineage.getClassType(o.getClassType())) == 0){
			if(message)
				ChattingController.toChatting(o, "해당 클레스로는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return;
		}
		
		// 레벨 확인.
		if(it.getLevel()!=0 && it.getLevel()>o.getLevel()){
			if(message)
				ChattingController.toChatting(o, "해당 레벨로는 사용할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
			o.toSender(S_ObjectLock.clone(BasePacketPooling.getPool(S_ObjectLock.class), 0x09));
			return;
		}
		
		// 이동가능한 부분만.
		if(LocationController.isTeleportZone(o, true, true))
			o.toTeleport(it.getX(), it.getY(), it.getMap(), true);
	}
}
