package lineage.network.packet.client;

import java.sql.Connection;

import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.Client;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Disconnect;
import lineage.network.packet.server.S_Unknow;
import lineage.world.World;
import lineage.world.object.instance.PcInstance;

public class C_WorldtoJoin extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_WorldtoJoin(data, length);
		else
			((C_WorldtoJoin)bp).clone(data, length);
		return bp;
	}
	
	public C_WorldtoJoin(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(Client c){
		String name = readS();
		
		if(name==null || name.length()<=0)
			return this;
		
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			
			if(CharactersDatabase.isCharacter(con, c.getAccountUid(), name) && World.findPc(name)==null){
				// 접속 성공 알림
				c.toSender( S_Unknow.clone(BasePacketPooling.getPool(S_Unknow.class)) );
				// 월드 접속한 날자 업데이트
				CharactersDatabase.updateCharacterJoinTimeStamp(con, name);
				// pc객체 정보 추출
				PcInstance pc = CharactersDatabase.readCharacter(con, c, name);
				if(pc != null){
					// 월드 진입 알리기.
					pc.toWorldJoin(con);
				}
				
				// 테스트
//				if(Lineage.server_version>=340){
//					pc.toSender(S_Unknow2.clone(BasePacketPooling.getPool(S_Unknow2.class), 1));
//					pc.toSender(S_Unknow2.clone(BasePacketPooling.getPool(S_Unknow2.class), 2));
//					pc.toSender(S_Unknow2.clone(BasePacketPooling.getPool(S_Unknow2.class), 3));
//				}
			}else{
				c.toSender(S_Disconnect.clone(BasePacketPooling.getPool(S_Disconnect.class), 0x0A));
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Client c)\r\n", C_WorldtoJoin.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
		
		return this;
	}
}
