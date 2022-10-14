package lineage.network.packet.client;

import java.sql.Connection;

import lineage.database.AccountDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.Client;
import lineage.network.Server;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Login;
import lineage.network.packet.server.S_LoginFail;
import lineage.network.packet.server.S_TimeLeft;
import lineage.share.Lineage;
import lineage.share.Web;

public class C_Login extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_Login(data, length);
		else
			((C_Login)bp).clone(data, length);
		return bp;
	}
	
	public C_Login(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(Client c){
		String id = readS();
		String pw = readS();
		
		if(id==null || pw==null || id.length()<=0 || pw.length()<=0)
			return this;
		
		Connection con = null;
		Connection con_web = null;
		try {
			con = DatabaseConnection.getLineage();
			if(Web.is)
				con_web = DatabaseConnection.getWeb();
			
			int uid = AccountDatabase.getUid(con, id);
			int web_uid = AccountDatabase.getUidWeb(con_web, id);
			if(uid>0){
				if((AccountDatabase.isAccount(con, uid, id, pw) || AccountDatabase.isAccountOld(con, uid, id, pw) || AccountDatabase.isAccountWeb(con_web, web_uid, id, pw)) && !AccountDatabase.isBlock(con, uid)){
					Client find_c = Server.find(uid);
					if(find_c!=null){
						// 다른사람이 해당 계정을 사용중일경우.
						c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.LOGIN_USER_ON) );
						find_c.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x16));
					}else{
						// 패스워드 기록.
						AccountDatabase.changePw(con, uid, pw);
						// 처리
						toLogin(c, con, id, uid, web_uid);
					}
				}else{
					// 패스워드가 일치하지 않음.
					c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.LOGIN_USER_OR_PASS_WRONG) );
				}
			}else{
				if(web_uid>0){
					// 웹에 아이디가 존재할경우.
					if(AccountDatabase.isAccountWeb(con_web, web_uid, id, pw) && AccountDatabase.isAccountCount(con, c.getAccountIp())){
						// 리니지 계정 추가.
						AccountDatabase.insert(con, id, pw);
						// uid 재 추출
						uid = AccountDatabase.getUid(con, id);
						// 로그인 처리.
						toLogin(c, con, id, uid, web_uid);
					}else{
						// 패스워드가 일치하지 않음.
						c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.LOGIN_USER_OR_PASS_WRONG) );
					}
				}else{
					if(Lineage.account_auto_create && AccountDatabase.isAccountCount(con, c.getAccountIp())){
						// 계정 자동생성 처리.
						AccountDatabase.insert(con, id, pw);
						// uid 재 추출
						uid = AccountDatabase.getUid(con, id);
						// 로그인 처리.
						toLogin(c, con, id, uid, web_uid);
					}else{
						// 계정이 존재하지 않음.
						c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.LOGIN_USER_OR_ID_AND_PASS_WRONG) );
					}
				}
			}
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Client c)\r\n", C_Login.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
			DatabaseConnection.close(con_web);
		}
		return this;
	}
	
	private void toLogin(Client c, Connection con, String id, int uid, int uid_web){
		int time = 0;
		if(Lineage.flat_rate && Lineage.server_version>=163){
			// 남은 시간 추출
			time = AccountDatabase.getTime(con, uid);
			if(time <= 0){
				// 정액 시간이 완료됨.
				c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.REASON_ACCESS_END) );
				return;
			}
			c.setAccountTime(time);
		}

		// 로그인 처리.
		c.setAccountUid(uid);
		c.setAccountId(id);
		c.setWebMemberUid(uid_web);
		c.setAccountNoticeStaticUid(0);
		c.setAccountNoticeUid( AccountDatabase.getNoticeUid(con, uid) );
		// 로그인한 아이피 갱신.
		AccountDatabase.updateIp(con, c.getAccountUid(), c.getAccountIp());
		// 로그이한 시간 갱신.
		AccountDatabase.updateLoginsDate(con, c.getAccountUid());
		// 패킷 처리.
		if(time > 0){
			if(Lineage.server_version < 300)
				c.toSender( S_Login.clone(BasePacketPooling.getPool(S_Login.class), S_LoginFail.REASON_ACCESS_OK, time) );
			c.toSender( S_TimeLeft.clone(BasePacketPooling.getPool(S_TimeLeft.class)) );
		}else{
			BasePacketPooling.setPool( C_NoticeOk.clone(BasePacketPooling.getPool(C_NoticeOk.class), null, 0).init(c) );
		}
	}
}
