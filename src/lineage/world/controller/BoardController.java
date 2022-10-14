package lineage.world.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Board;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_BoardView;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.instance.BoardInstance;
import lineage.world.object.instance.PcInstance;

public final class BoardController {

	static private List<Board> pool;
	
	static public void init(){
		TimeLine.start("BoardController..");
		
		pool = new ArrayList<Board>();
		
		TimeLine.end();
	}
	
	static public Board getPool(){
		Board b = null;
		if(pool.size()>0){
			b = pool.get(0);
			pool.remove(0);
		}else{
			b = new Board();
		}
		return b;
	}
	
	static public void setPool(Board b){
		pool.add(b);
	}
	
	static public int getPoolSize(){
		return pool.size();
	}
	
	/**
	 * 해당 게시판에 게시판리스트를 만들어서 리턴.
	 * @param type		: 어떤 마을에 게시판인지 구분용
	 * @param uid_pos	: 표현할 uid에 위치값. 해당 위치에서 아래로 8개 뽑음.
	 * @param r_list	: 리턴될 목록을 담을 변수
	 */
	static public void getList(String type, int uid_pos, List<Board> r_list){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM boards WHERE type=? AND uid<? ORDER BY uid DESC LIMIT 8");
			st.setString(1, type);
			st.setInt(2, uid_pos);
			rs = st.executeQuery();
			while(rs.next()){
				Board b = getPool();
				b.setUid(rs.getInt("uid"));
				b.setType(rs.getString("type"));
				b.setAccountId(rs.getString("account_id"));
				b.setName(rs.getString("name"));
				try { b.setDays(rs.getTimestamp("days").getTime()); } catch (Exception e) {}
				b.setSubject(rs.getString("subject"));
				b.setMemo(rs.getString("memo"));
				
				r_list.add(b);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : getList(String type, int uid_pos, List<Board> r_list)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
	}
	
	/**
	 * uid 해당하는 정보 추출하여 리턴함.
	 * @param type
	 * @param uid
	 * @return
	 */
	static public Board find(String type, int uid){
		Board b = null;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT * FROM boards WHERE type=? AND uid=?");
			st.setString(1, type);
			st.setInt(2, uid);
			rs = st.executeQuery();
			if(rs.next()){
				b = getPool();
				b.setUid(rs.getInt("uid"));
				b.setType(rs.getString("type"));
				b.setAccountId(rs.getString("account_id"));
				b.setName(rs.getString("name"));
				try { b.setDays(rs.getTimestamp("days").getTime()); } catch (Exception e) { }
				b.setSubject(rs.getString("subject"));
				b.setMemo(rs.getString("memo"));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : find(String type, int uid)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return b;
	}
	
	/**
	 * 글쓰기 처리 함수.
	 * @param pc
	 * @param bi
	 * @param subject
	 * @param content
	 */
	static public void toWrite(PcInstance pc, BoardInstance bi, String subject, String content){
		if(pc.getInventory().isAden(Lineage.board_write_price, true)){
			Connection con = null;
			PreparedStatement st = null;
			ResultSet rs = null;
			try{
				con = DatabaseConnection.getLineage();
				
				// uid 추출
				int uid = getMaxUid(bi.getType());
				// 등록
				st = con.prepareStatement("INSERT INTO boards SET uid=?, type=?, account_id=?, name=?, days=?, subject=?, memo=?");
				st.setInt(1, uid);
				st.setString(2, bi.getType());
				st.setString(3, pc.getClient().getAccountId());
				st.setString(4, pc.getName());
				st.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
				st.setString(6, subject);
				st.setString(7, content);
				st.executeUpdate();
				st.close();
				
				// 페이지 보기.
				toView(pc, bi, uid);
			} catch(Exception e) {
				lineage.share.System.printf("%s : toWrite(PcInstance pc, BoardInstance bi, String subject, String content)\r\n", BoardController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
		}else{
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
		}
	}
	
	/**
	 * 게시판 읽기 처리 함수.
	 * @param pc
	 * @param bi
	 * @param uid
	 */
	static public void toView(PcInstance pc, BoardInstance bi, int uid){
		Board b = find(bi.getType(), uid);
		if(b != null)
			pc.toSender(S_BoardView.clone(BasePacketPooling.getPool(S_BoardView.class), b));
	}
	
	/**
	 * 게시판에 게시물 제거 처리 함수.
	 * @param pc
	 * @param bi
	 * @param uid
	 */
	static public void toDelete(PcInstance pc, BoardInstance bi, int uid){
		Board b = find(bi.getType(), uid);
		if(b != null){
			Connection con = null;
			PreparedStatement st = null;
			try{
				con = DatabaseConnection.getLineage();
				
				// 제거.
				st = con.prepareStatement( "DELETE FROM boards WHERE uid=? AND account_id=? AND name=?" );
				st.setInt(1, uid);
				st.setString(2, pc.getClient().getAccountId());
				st.setString(3, pc.getName());
				st.executeUpdate();
				st.close();
				// uid 갱신.
				st = con.prepareStatement("UPDATE boards SET uid=uid-1 WHERE type=? AND uid>?");
				st.setString(1, bi.getType());
				st.setInt(2, uid);
				st.executeUpdate();
				st.close();
			} catch(Exception e) {
				lineage.share.System.printf("%s : toDelete(PcInstance pc, BoardInstance bi, int uid)\r\n", BoardController.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}
	
	/**
	 * 타입에 맞는 게시물들에 최대 uid값 추출.
	 * @param type
	 * @return
	 */
	static public int getMaxUid(String type){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try{
			con = DatabaseConnection.getLineage();
			st = con.prepareStatement("SELECT MAX(uid) FROM boards WHERE type=?");
			st.setString(1, type);
			rs = st.executeQuery();
			if(rs.next())
				return rs.getInt(1) + 1;
		} catch(Exception e) {
			lineage.share.System.printf("%s : getMaxUid(String type)\r\n", BoardController.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con, st, rs);
		}
		return 0;
	}
}
