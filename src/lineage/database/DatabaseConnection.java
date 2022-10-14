package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.share.Common;
import lineage.share.Mysql;
import lineage.share.TimeLine;
import lineage.share.Web;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public final class DatabaseConnection {
	
	private static ComboPooledDataSource lineage_pool;
	private static ComboPooledDataSource web_pool;
	
	static public void init(){
		TimeLine.start("DatabaseConnection..");
		
		try {
			lineage_pool = new ComboPooledDataSource();
			web_pool = new ComboPooledDataSource();
			
			lineage_pool.setAutoCommitOnClose(true);
			lineage_pool.setInitialPoolSize(10);				// 최초의 풀 크기
			lineage_pool.setMinPoolSize(10);					// 최소 풀 크기
			lineage_pool.setMaxPoolSize(100);					// 최대 풀 크기
			lineage_pool.setAcquireRetryAttempts(0);			// 재접속 시도할 횟수
			lineage_pool.setAcquireRetryDelay(500);				// 재접속 시도하는 딜레이
			lineage_pool.setCheckoutTimeout(0);					// 
			lineage_pool.setAcquireIncrement(5);				// 재접속 증가 값??
			lineage_pool.setIdleConnectionTestPeriod(60); 		// 쉴때 접속테스트 시간
			lineage_pool.setMaxIdleTime(0);						// 최대 쉬는 시간
			lineage_pool.setMaxStatementsPerConnection(100);	// Statements의대한 접속 최대 갯수
			lineage_pool.setBreakAfterAcquireFailure(false);
			lineage_pool.setDriverClass(Mysql.driver);
			lineage_pool.setJdbcUrl(Mysql.url);
			lineage_pool.setUser(Mysql.id);
			lineage_pool.setPassword(Mysql.pw);
			lineage_pool.getConnection().close();
			
			if(Web.is){
				web_pool.setAutoCommitOnClose(true);
				web_pool.setInitialPoolSize(5);					// 최초의 풀 크기
				web_pool.setMinPoolSize(5);						// 최소 풀 크기
				web_pool.setMaxPoolSize(10);					// 최대 풀 크기
				web_pool.setAcquireRetryAttempts(0);			// 재접속 시도할 횟수
				web_pool.setAcquireRetryDelay(500);				// 재접속 시도하는 딜레이
				web_pool.setCheckoutTimeout(0);					// 
				web_pool.setAcquireIncrement(5);				// 재접속 증가 값??
				web_pool.setIdleConnectionTestPeriod(60); 		// 쉴때 접속테스트 시간
				web_pool.setMaxIdleTime(0);						// 최대 쉬는 시간
				web_pool.setMaxStatementsPerConnection(10);		// Statements의대한 접속 최대 갯수
				web_pool.setBreakAfterAcquireFailure(false);
				web_pool.setDriverClass(Web.driver);
				web_pool.setJdbcUrl(Web.url);
				web_pool.setUser(Web.id);
				web_pool.setPassword(Web.pw);
				web_pool.getConnection().close();
			}
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : init()\r\n", DatabaseConnection.class.toString());
			lineage.share.System.println(e);
		}
		
		TimeLine.end();
	}
	
	static public void close(){
		lineage_pool.close();
		if(Web.is)
			web_pool.close();
	}
	
	/**
	 * 풀에 등록된 컨넥션 한개 추출하기.
	 * @return
	 * @throws Exception
	 */
	static public Connection getLineage() throws Exception {
		Connection con = null;
		do{
			con = lineage_pool.getConnection();
			Thread.sleep(Common.THREAD_SLEEP);
		}while(con == null);
		return con;
	}
	
	static public Connection getWeb() throws Exception {
		Connection con = null;
		do{
			con = web_pool.getConnection();
			Thread.sleep(Common.THREAD_SLEEP);
		}while(con == null);
		return con;
	}
	
	static public void close(Connection con) {
		try { con.close(); } catch (Exception e) {}
	}
	
	static public void close(ResultSet rs) {
		try { rs.close(); } catch (Exception e) {}
	}
	
	static public void close(Connection con, PreparedStatement st) {
		close(st);
		close(con);
	}
	
	static public void close(Connection con, PreparedStatement st, ResultSet rs) {
		close(st, rs);
		close(con);
	}
	
	static public void close(PreparedStatement st) {
		try { st.close(); } catch (Exception e) {}
	}
	
	static public void close(PreparedStatement st, ResultSet rs) {
		try { rs.close(); } catch (Exception e) {}
		close(st);
	}
}
