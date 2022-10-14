package lineage.network.packet.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.bean.database.Item;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacket;
import lineage.network.packet.Opcodes;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.instance.DwarfInstance;

public class S_WareHouse extends ServerBasePacket {

	static public BasePacket clone(BasePacket bp, Connection con, DwarfInstance dwarf, int id, int count, int dwarf_type){
		if(bp == null)
			bp = new S_WareHouse(con, dwarf, id, count, dwarf_type);
		else
			((S_WareHouse)bp).toClone(con, dwarf, id, count, dwarf_type);
		return bp;
	}
	
	public S_WareHouse(Connection con, DwarfInstance dwarf, int id, int count, int dwarf_type){
		toClone(con, dwarf, id, count, dwarf_type);
	}
	
	static int op = 0;
	public void toClone(Connection con, DwarfInstance dwarf, int id, int count, int dwarf_type){
		clear();
		
		writeC(Opcodes.S_OPCODE_WAREHOUSE);
		writeD(dwarf.getObjectId());
		writeH(count);
		switch(dwarf_type){
			case Lineage.DWARF_TYPE_CLAN:	// 혈맹창고찾기
				writeC(5);
				break;
			case Lineage.DWARF_TYPE_ELF:	// 요정창고찾기
				writeC(9);
				break;
			default:						// 일반창고찾기
				writeC(3);
				break;
		}
		readDB(con, id, dwarf_type);		// 창고 목록
	}
	
	private void readDB(Connection con, int id, int dwarf_type){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("SELECT * FROM warehouse_clan WHERE clan_id=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("SELECT * FROM warehouse_elf WHERE account_uid=?");
					break;
				default:
					st = con.prepareStatement("SELECT * FROM warehouse WHERE account_uid=?");
					break;
			}
			st.setInt(1, id);
			rs = st.executeQuery();
			while(rs.next()){
				int uid = rs.getInt(1);
				int letter_id = rs.getInt("letter_id");
				int pet_id = rs.getInt("pet_id");
				int type = rs.getInt(7);
				int gfxid = rs.getInt(8);
				int count = rs.getInt(9);
				int enlev = rs.getInt(11);
				boolean isid = rs.getInt(12)==1;
				int bress = rs.getInt(13);
				Item item = ItemDatabase.find( rs.getString(6) );
				StringBuffer item_name = new StringBuffer();

				if(item.getNameIdNumber()==1075 && item.getInvGfx()!=464){
					item_name.append( readLetterDB(con, letter_id) );
				}else{
					if(isid && (type==1 || type==2)){
						if(enlev<0)
							item_name.append("-");
						else
							item_name.append("+");
						item_name.append(enlev);
					}
					if(item!=null)
						item_name.append(item.getNameId());
					else
						item_name.append("none");
					if(count>1){
						item_name.append(" (");
						item_name.append(count);
						item_name.append(")");
					}
					if(item.getNameIdNumber()==1173)
						item_name.append( readPetDB(con, pet_id) );
				}

				writeD(uid);					// 번호
				writeC(type);					// 타입
				writeH(gfxid);					// GFX 아이디
				writeC(bress);					//	1: 보통 0: 축 2: 저주
				writeD(count);					// 현재아템 총수량
				writeC(isid ? 1 : 0);			// 1: 확인 0: 미확인
				writeS(item_name.toString());	// 이름
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : readDB(Connection con, int id, boolean clan)\r\n", S_WareHouse.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
	}
	
	/**
	 * 펫 아이디로 이름처리에 사용될 정보만 뽑아서 문자열로 리턴.
	 * @param con
	 * @param pet_id
	 * @return
	 */
	private String readPetDB(Connection con, int pet_id){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_pet WHERE objid=?");
			st.setInt(1, pet_id);
			rs = st.executeQuery();
			if(rs.next())
				return String.format(" [Lv.%d %s]", rs.getInt("level"), rs.getString("name"));
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : readPetDB(Connection con, int pet_id)\r\n", S_WareHouse.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return "";
	}
	
	/**
	 * 편지 정보 추출.
	 * @param con
	 * @param letter_id
	 * @return
	 */
	private String readLetterDB(Connection con, int letter_id){
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM characters_letter WHERE uid=?");
			st.setInt(1, letter_id);
			rs = st.executeQuery();
			if(rs.next())
				return String.format("%s : %s", rs.getString("paperFrom"), rs.getString("paperSubject"));
			
		} catch (Exception e) {
			lineage.share.System.printf("%s : readPetDB(Connection con, int pet_id)\r\n", S_WareHouse.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}
		return "";
	}
}
