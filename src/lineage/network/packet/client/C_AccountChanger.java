package lineage.network.packet.client;

import java.sql.Connection;

import lineage.database.AccountDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.Client;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_LoginFail;

public class C_AccountChanger extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_AccountChanger(data, length);
		else
			((C_AccountChanger)bp).clone(data, length);
		return bp;
	}
	
	public C_AccountChanger(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(Client c){
		String id = readS();
		String pw = readS();
		String npw = readS();
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			
			int uid = AccountDatabase.getUid(con, id);
			if(AccountDatabase.isAccount(con, uid, id, pw)){
				// 패스워드 변경.
				AccountDatabase.changePw(con, uid, npw);
				c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.RETURN) );
			}else{
				// 패스워드가 일치하지 않음.
				c.toSender( S_LoginFail.clone(BasePacketPooling.getPool(S_LoginFail.class), S_LoginFail.LOGIN_USER_OR_PASS_WRONG) );
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Client c)\r\n", C_AccountChanger.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
		
		return this;
	}
}
