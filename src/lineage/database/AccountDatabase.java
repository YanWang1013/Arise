package lineage.database;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.share.Lineage;
import lineage.share.Web;

public final class AccountDatabase {
	
	static public void updateNoticeUid(Connection con, int uid, int notice_uid){
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("UPDATE accounts SET notice_uid=? WHERE uid=?");
			st.setInt(1, notice_uid);
			st.setInt(2, uid);
			st.executeUpdate();
		}catch(Exception e){
			lineage.share.System.printf("%s : updateNoticeUid(Connection con, int uid, int notice_uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		}finally{
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 공지사항 확인된 칼럼값 추출.
	 * @param con
	 * @param uid
	 * @return
	 */
	static public int getNoticeUid(Connection con, int uid){
		PreparedStatement st = null;
		ResultSet rs = null;
		if(con != null){
			try {
				st = con.prepareStatement("SELECT notice_uid FROM accounts WHERE uid=?");
				st.setInt(1, uid);
				rs = st.executeQuery();
				if(rs.next())
					return rs.getInt(1);
			} catch (Exception e) {
				lineage.share.System.printf("%s : getNoticeUid(Connection con, int uid)\r\n", AccountDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(st, rs);
			}
		}
		return 0;
	}
	
	/**
	 * 아이피당 소유가능한 갯수 확인하여 생성 가능여부 리턴함.
	 * @param ip
	 * @return
	 */
	static public boolean isAccountCount(Connection con, String ip){
		if(Lineage.account_ip_count<=0 || ip==null || ip.length()<=0)
			return true;
		
		PreparedStatement st = null;
		ResultSet rs = null;
		if(con != null){
			try {
				st = con.prepareStatement("SELECT COUNT(*) FROM accounts WHERE last_ip=?");
				st.setString(1, ip);
				rs = st.executeQuery();
				if(rs.next())
					return rs.getInt(1) < Lineage.account_ip_count;
			} catch (Exception e) {
				lineage.share.System.printf("%s : isAccountCount(Connection con, String ip)\r\n", AccountDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(st, rs);
			}
		}
		
		return true;
	}
	
	/**
	 * 게시판모듈에 등록된 회원아이디에 해당하는 고유값 추출.
	 * @param con
	 * @param id
	 * @return
	 */
	static public int getUidWeb(Connection con, String id){
		int uid = 0;
		PreparedStatement st = null;
		ResultSet rs = null;
		if(con != null){
			try {
				if(Web.board_module.equalsIgnoreCase("gnu"))
					st = con.prepareStatement("SELECT mb_no FROM g4_member WHERE LOWER(mb_id)=?");
				else if(Web.board_module.equalsIgnoreCase("kimsq"))
					// 킴스큐 옛날버전
//					st = con.prepareStatement( String.format("SELECT uid FROM %s_members WHERE LOWER(id)=?", Web.kimsq_identifier) );
					// 킴스큐 최근버전
					st = con.prepareStatement( String.format("SELECT uid FROM %s_s_mbrid WHERE LOWER(id)=?", Web.kimsq_identifier) );
				
				if(st != null){
					st.setString(1, id.toLowerCase());
					rs = st.executeQuery();
					if(rs.next())
						uid = rs.getInt(1);
				}
			} catch (Exception e) {
				lineage.share.System.printf("%s : getUidWeb(Connection con, String id)\r\n", AccountDatabase.class.toString());
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(st, rs);
			}
		}
		return uid;
	}

	/**
	 * 계정 아이디를 통한 uid값 추출하는 함수.
	 * @param con
	 * @param id
	 * @return
	 */
	static public int getUid(Connection con, String id) {
		int uid = 0;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM accounts WHERE LOWER(id)=?");
			st.setString(1, id.toLowerCase());
			rs = st.executeQuery();
			if(rs.next())
				uid = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getUid(Connection con, String id)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return uid;
	}
	
	/**
	 * 게시판 모듈에서 아이디 및 패스워드 확인하기.
	 * @param con
	 * @param uid
	 * @param id
	 * @param pw
	 * @return
	 */
	static public boolean isAccountWeb(Connection con, int uid, String id, String pw){
		if(!Web.is)
			return false;
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			if(Web.board_module.equalsIgnoreCase("gnu")){
				st = con.prepareStatement(String.format("SELECT * FROM g4_member WHERE mb_no=? AND LOWER(mb_id)=? AND mb_password=%s(?)", Web.old_password ? "OLD_PASSWORD" : "PASSWORD"));
			}else if(Web.board_module.equalsIgnoreCase("kimsq")){
				// 킴스큐는 mysql자체 지원하는 함수를 사용하지 않음. md5방식 적용.
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(pw.getBytes());
				byte[] md5bytes = md5.digest();
				StringBuffer sb = new StringBuffer();
				for(int i=0 ; i<md5bytes.length ; ++i)
					sb.append( String.format("%02x", 0xff & md5bytes[i]) );
				pw = sb.toString();
				// 킴스큐 옛날버전
//				st = con.prepareStatement(String.format("SELECT * FROM %s_members WHERE uid=? AND LOWER(id)=? AND pw=?", Web.kimsq_identifier));
				// 킴스큐 최근버전
				st = con.prepareStatement(String.format("SELECT * FROM %s_s_mbrid WHERE uid=? AND LOWER(id)=? AND pw=?", Web.kimsq_identifier));
			}
			
			st.setInt(1, uid);
			st.setString(2, id.toLowerCase());
			st.setString(3, pw.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isAccountWeb(Connection con, int uid, String id, String pw)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		return false;
	}
	
	/**
	 * uid값에 필드에 id와 pw가 일치하는지 확인.
	 * @param con
	 * @param uid
	 * @param id
	 * @param pw
	 * @return
	 */
	static public boolean isAccount(Connection con, int uid, String id, String pw){
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = con.prepareStatement("SELECT * FROM accounts WHERE uid=? AND LOWER(id)=? AND LOWER(pw)=?");
			st.setInt(1, uid);
			st.setString(2, id.toLowerCase());
			st.setString(3, pw.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isAccount(Connection con, int uid, String id, String pw)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		return false;
	}
	
	/**
	 * 예전 슈롬에서 사용하던 디비값 패스워드 처리를 위해 사용할 함수.
	 * @param con
	 * @param uid
	 * @param id
	 * @param pw
	 * @return
	 */
	static public boolean isAccountOld(Connection con, int uid, String id, String pw){
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = con.prepareStatement("SELECT * FROM accounts WHERE uid=? AND LOWER(id)=? AND old_pw=OLD_PASSWORD(?)");
			st.setInt(1, uid);
			st.setString(2, id.toLowerCase());
			st.setString(3, pw.toLowerCase());
			rs = st.executeQuery();
			return rs.next();
		} catch (Exception e) {
			lineage.share.System.printf("%s : isAccountOld(Connection con, int uid, String id, String pw)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		return false;
	}
	
	/**
	 * 블럭된 계정인지 확인.
	 * @param con
	 * @param uid
	 * @param id
	 * @return
	 */
	static public boolean isBlock(Connection con, int uid){
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = con.prepareStatement("SELECT * FROM accounts WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next()){
				try{ return rs.getTimestamp("block_date").getTime()>0; }catch(Exception e){}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : isBlock(Connection con, int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		
		return false;
	}
	
	/**
	 * uid와 연결된 계정에 소유중인 케릭터 갯수 리턴.
	 * @param con
	 * @param uid
	 * @return
	 */
	static public int getCharacterLength(Connection con, int uid){
		int length = 0;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT COUNT(*) as cnt FROM characters WHERE account_uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next())
				length = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getCharacterLength(Connection con, int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return length;
	}
	
	/**
	 * uid와 연결된 계정의 패스워드를 변경하는 메서드.
	 * @param con
	 * @param uid
	 * @param nPw
	 */
	static public void changePw(Connection con, int uid, String nPw){
		PreparedStatement st = null;
		try{
			st = con.prepareStatement("UPDATE accounts SET pw=? WHERE uid=?");
			st.setString(1, nPw);
			st.setInt(2, uid);
			st.executeUpdate();
		}catch(Exception e){
			lineage.share.System.printf("%s : changePw(Connection con, int uid, String nPw)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		}finally{
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 계정의 남은시간 추출.
	 * @param con
	 * @param uid
	 * @return
	 */
	static public int getTime(Connection con, int uid){
		int time = 0;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT time FROM accounts WHERE uid=?");
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next())
				time = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.printf("%s : getTime(Connection con, int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return time;
	}
	
	/**
	 * 계정 추가 처리 함수.
	 * @param con
	 * @param id
	 * @param pw
	 */
	static public void insert(Connection con, String id, String pw){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("INSERT INTO accounts SET id=?, pw=?, register_date=?, time=?");
			st.setString(1, id);
			st.setString(2, pw);
			st.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			st.setInt(4, Lineage.flat_rate_price);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : insert(Connection con, String id, String pw)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 로그인한 계정에 대한 아이피 업데이트 함수.
	 * @param con
	 * @param uid
	 * @param ip
	 */
	static public void updateIp(Connection con, int uid, String ip){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE accounts SET last_ip=? WHERE uid=?");
			st.setString(1, ip);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateIp(Connection con, int uid, String ip)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 로그인한 시간 갱신 처리 함수.
	 * @param con
	 * @param uid
	 */
	static public void updateLoginsDate(Connection con, int uid){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE accounts SET logins_date=? WHERE uid=?");
			st.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateLoginsDate(Connection con, int uid)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
	/**
	 * 계정 시간 갱신하는 함수.
	 * @param con
	 * @param uid
	 * @param time
	 */
	static public void updateTime(Connection con, int uid, int time){
		PreparedStatement st = null;
		try {
			st = con.prepareStatement("UPDATE accounts SET time=? WHERE uid=?");
			st.setInt(1, time);
			st.setInt(2, uid);
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.printf("%s : updateTime(Connection con, int uid, int time)\r\n", AccountDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
}
