package lineage.network.packet.client;

import java.sql.Connection;
import java.sql.PreparedStatement;

import lineage.database.CharactersDatabase;
import lineage.database.DatabaseConnection;
import lineage.network.Client;
import lineage.network.packet.BasePacket;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_CharacterDelete;
import lineage.share.Log;

public class C_CharacterDelete extends ClientBasePacket {
	
	static public BasePacket clone(BasePacket bp, byte[] data, int length){
		if(bp == null)
			bp = new C_CharacterDelete(data, length);
		else
			((C_CharacterDelete)bp).clone(data, length);
		return bp;
	}
	
	public C_CharacterDelete(byte[] data, int length){
		clone(data, length);
	}
	
	@Override
	public BasePacket init(Client c){
		String name = readS();
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			
			if(CharactersDatabase.isCharacter(con, c.getAccountUid(), name)){
				if(!CharactersDatabase.isInvalidName(con, name)){
					// 삭제하려는 케릭터에 생성시간값 추출.
					long key = CharactersDatabase.getCharacterRegisterDate(con, name);
					// 삭제하려는 객체 오브젝트 아이디 추출.
					int obj_id = CharactersDatabase.getCharacterObjectId(con, name);
					// 로그 기록.
					Log.appendConnect(key, c.getAccountIp(), c.getAccountId(), name, "삭제");
					// 디비 제거.
					PreparedStatement st = null;
					try {
						// 케릭터 제거
						st = con.prepareStatement("DELETE FROM characters WHERE objID=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 기억 제거
						st = con.prepareStatement("DELETE FROM characters_book WHERE objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 버프 제거
						st = con.prepareStatement("DELETE FROM characters_buff WHERE objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 친구 제거
						st = con.prepareStatement("DELETE FROM characters_friend WHERE object_id=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 인벤토리 제거
						st = con.prepareStatement("DELETE FROM characters_inventory WHERE cha_objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 퀘스트 제거
						st = con.prepareStatement("DELETE FROM characters_quest WHERE objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
						// 스킬 제거
						st = con.prepareStatement("DELETE FROM characters_skill WHERE cha_objId=?");
						st.setInt(1, obj_id);
						st.executeUpdate();
						st.close();
					} catch (Exception e) {
						lineage.share.System.printf("%s : init(Client c) 2\r\n", C_CharacterDelete.class.toString());
						lineage.share.System.println(e);
					} finally {
						DatabaseConnection.close(st);
					}
				}
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Client c) 1\r\n", C_CharacterDelete.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
		
		c.toSender( S_CharacterDelete.clone(BasePacketPooling.getPool(S_CharacterDelete.class)) );
		return this;
	}
}
