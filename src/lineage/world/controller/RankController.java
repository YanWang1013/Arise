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
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.instance.PcInstance;


public class RankController {
	
	// 랭킹 리스트.
	static private List<Board> list;
	// 랭킹 주기적으로 읽게 하기위해 사용될 변수.
	static private long time_last;
	
	static public void init(Connection con){
		TimeLine.start("RankController..");
		
		list = new ArrayList<Board>();
		time_last = System.currentTimeMillis();
		toRankRead(con, time_last);
		
		TimeLine.end();
	}
	
	static public List<Board> getList(){
		return list;
	}
	
	static public void toTimer(long time){
		if(time_last+Lineage.board_rank_update_delay < time){
			time_last = time;
			
			// 메모리 반환.
			for(Board b : list)
				BoardController.setPool(b);
			list.clear();
			// 정보 추출.
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				toRankRead(con, time);
			} catch (Exception e) {
			} finally {
				DatabaseConnection.close(con);
			}
		}
	}
	
	/**
	 * 게시판 읽기 처리 함수.
	 * @param pc
	 * @param bi
	 * @param uid
	 */
	static public void toView(PcInstance pc, int uid){
		Board b = list.get(uid);
		if(b != null)
			pc.toSender(S_BoardView.clone(BasePacketPooling.getPool(S_BoardView.class), b));
	}
	
	/**
	 * 랭킹 정보 추출 후 리턴.
	 * @param uid
	 */
	static public Board getView(int uid){
		return list.get(uid);
	}
	
	/**
	 * 랭킹정보 추출.
	 * @param con
	 */
	static private void toRankRead(Connection con, long time){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			// 전체 랭킹.
			int i = 1;
			Board b = BoardController.getPool();
			b.setUid(0);
			b.setSubject("전체 랭킹");
			b.setName("메티스");
			b.setDays(time);
			StringBuffer sb = new StringBuffer();
			st = con.prepareStatement("SELECT * FROM characters WHERE isGm = 0 ORDER BY exp DESC LIMIT 10");
			rs = st.executeQuery();
			i = 1;
			while(rs.next())
				sb.append( String.format("%d위 %s\r\n", i++, rs.getString("name")) );
			rs.close();
			st.close();
			b.setMemo(sb.toString());
			list.add(b);

			// 군주 랭킹.
			b = BoardController.getPool();
			b.setUid(1);
			b.setSubject("군주 랭킹");
			b.setName("메티스");
			b.setDays(time);
			sb = new StringBuffer();
			st = con.prepareStatement("SELECT * FROM characters WHERE class=0 AND isGm=0 ORDER BY exp DESC LIMIT 10");
			rs = st.executeQuery();
			i = 1;
			while(rs.next())
				sb.append( String.format("%d위 %s\r\n", i++, rs.getString("name")) );
			rs.close();
			st.close();
			b.setMemo(sb.toString());
			list.add(b);
			
			// 기사 랭킹.
			b = BoardController.getPool();
			b.setUid(2);
			b.setSubject("기사 랭킹");
			b.setName("메티스");
			b.setDays(time);
			sb = new StringBuffer();
			st = con.prepareStatement("SELECT * FROM characters WHERE class=1 AND isGm=0 ORDER BY exp DESC LIMIT 10");
			rs = st.executeQuery();
			i = 1;
			while(rs.next())
				sb.append( String.format("%d위 %s\r\n", i++, rs.getString("name")) );
			rs.close();
			st.close();
			b.setMemo(sb.toString());
			list.add(b);
			
			// 요정 랭킹.
			b = BoardController.getPool();
			b.setUid(3);
			b.setSubject("요정 랭킹");
			b.setName("메티스");
			b.setDays(time);
			sb = new StringBuffer();
			st = con.prepareStatement("SELECT * FROM characters WHERE class=2 AND isGm=0 ORDER BY exp DESC LIMIT 10");
			rs = st.executeQuery();
			i = 1;
			while(rs.next())
				sb.append( String.format("%d위 %s\r\n", i++, rs.getString("name")) );
			rs.close();
			st.close();
			b.setMemo(sb.toString());
			list.add(b);
			
			// 마법사 랭킹.
			b = BoardController.getPool();
			b.setUid(4);
			b.setSubject("마법사 랭킹");
			b.setName("메티스");
			b.setDays(time);
			sb = new StringBuffer();
			st = con.prepareStatement("SELECT * FROM characters WHERE class=3 AND isGm=0 ORDER BY exp DESC LIMIT 10");
			rs = st.executeQuery();
			i = 1;
			while(rs.next())
				sb.append( String.format("%d위 %s\r\n", i++, rs.getString("name")) );
			rs.close();
			st.close();
			b.setMemo(sb.toString());
			list.add(b);
			
		} catch (Exception e) {
			lineage.share.System.println(RankController.class+" : void init(Connection con)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
}
