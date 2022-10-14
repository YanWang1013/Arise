package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.share.Web;
import lineage.world.object.instance.PcInstance;

public final class WebDatabase {

	static public void init(){
		//
	}
	
	/**
	 * 월드 진입시 호출됨.
	 * @param con
	 * @param pc
	 */
	static public void toWorldJoin(PcInstance pc){
		if(Web.is){
			// 홈페이지에서 해당 계정 및 케릭에 대한 정보를 확인 할 수 있도록 디비 에 정보 갱신.
			Connection con = null;
			PreparedStatement st = null;
			ResultSet rs = null;
			try {
				con = DatabaseConnection.getWeb();
				
				// 정보 등록.
				try {
					st = con.prepareStatement("INSERT INTO lineage_dynamic_character SET mb_uid=?, character_name=?, x=?, y=?, map=?");
					st.setInt(1, pc.getClient().getWebMemberUid());
					st.setString(2, pc.getName());
					st.setInt(3, pc.getX());
					st.setInt(4, pc.getY());
					st.setInt(5, pc.getMap());
					st.executeUpdate();
				} catch (Exception e) {
					
				} finally {
					DatabaseConnection.close(st);
				}
				
				// 정보 추출
				if(Web.board_module.equalsIgnoreCase("gnu"))
					st = con.prepareStatement("SELECT mb_id, mb_name, mb_nick, mb_sex, mb_birth FROM g4_member WHERE mb_no=?");
				else if(Web.board_module.equalsIgnoreCase("kimsq"))
					// 옛날버전
//					st = con.prepareStatement("SELECT id, name, nic, sex, birth2 FROM q_members WHERE uid=?");
					// 킴스큐 최근버전
					st = con.prepareStatement( String.format("SELECT id, name, nic, sex, birth2 FROM %s_s_mbrdata a, %s_s_mbrid b WHERE a.memberuid=? AND b.uid=?", Web.kimsq_identifier, Web.kimsq_identifier) );
				st.setInt(1, pc.getClient().getWebMemberUid());
				if(Web.board_module.equalsIgnoreCase("kimsq"))
					st.setInt(2, pc.getClient().getWebMemberUid());
				rs = st.executeQuery();
				if(rs.next()){
					pc.setWebId( rs.getString(1) );
					pc.setWebName( rs.getString(2) );
					pc.setWebNick( rs.getString(3) );
					pc.setWebSex( rs.getInt(4) );
					pc.setWebBirth( rs.getString(5) );
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : toWorldJoin(PcInstance pc)\r\n", WebDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st, rs);
			}
			// 홈페이지 회원정보 추출. 리니지 월드에서 여러 시스템에 혜택 및 불이익 처리를 위해.
			
		}
	}
	
	/**
	 * 월드 나갈때 호출됨.
	 * @param pc
	 */
	static public void toWorldOut(PcInstance pc){
		if(Web.is){
			// 정보 삭제.
			Connection con = null;
			PreparedStatement st = null;
			try {
				con = DatabaseConnection.getWeb();
				st = con.prepareStatement( "DELETE FROM lineage_dynamic_character WHERE mb_uid=?" );
				st.setInt(1, pc.getClient().getWebMemberUid());
				st.executeUpdate();
			} catch (Exception e) {
				lineage.share.System.printf("%s : toWorldOut(PcInstance pc)\r\n", WebDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con, st);
			}
		}
	}
}
