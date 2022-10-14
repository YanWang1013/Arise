package lineage.world.object.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Kingdom;
import lineage.database.DatabaseConnection;
import lineage.database.ItemDatabase;
import lineage.database.ServerDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Html;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_WareHouse;
import lineage.share.Lineage;
import lineage.world.object.object;

public class DwarfInstance extends object {
	private Npc npc;
	protected Kingdom kingdom;
	
	public DwarfInstance(Npc npc){
		this.npc = npc;
	}

	/**
	 * 창고를 이용할 수 있는 레벨인지 확인하는 메서드.
	 */
	static public boolean isLevel(int level){
		return level>=Lineage.warehouse_level;
	}

	@Override
	public void toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp){
		int dwarf_type = Lineage.DWARF_TYPE_NONE;
		if(action.indexOf("pledge")>0)
			dwarf_type = Lineage.DWARF_TYPE_CLAN;
		else if(action.indexOf("elven")>0)
			dwarf_type = Lineage.DWARF_TYPE_ELF;
		
		int id = dwarf_type==Lineage.DWARF_TYPE_CLAN ? pc.getClanId() : pc.getClient().getAccountUid();
		
		if(dwarf_type==Lineage.DWARF_TYPE_CLAN && pc.getClanId()==0){
			// \f1혈맹창고를 사용하려면 혈맹이 있어야 합니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 208));
		}else if(dwarf_type==Lineage.DWARF_TYPE_CLAN && pc.getClassType()!=Lineage.LINEAGE_CLASS_ROYAL && (pc.getTitle()==null || pc.getTitle().length()==0)){
			// 호칭을 받지 못한 혈맹원이나 견습 혈맹원은 혈맹창고를 사용할 수 없습니다.
			pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 728));
		}else{
			Connection con = null;
			try {
				con = DatabaseConnection.getLineage();
				
				int cnt = getCount(con, id, dwarf_type);
				if(cnt == 0){
					// 아이템 없음
					pc.toSender(S_Html.clone(BasePacketPooling.getPool(S_Html.class), this, "noitemret"));
				}else{
					// 창고 목록 열람.
					pc.toSender(S_WareHouse.clone(BasePacketPooling.getPool(S_WareHouse.class), con, this, id, cnt, dwarf_type));
				}
			} catch (Exception e) {
				lineage.share.System.println(DwarfInstance.class.toString()+" : toTalk(PcInstance pc, String action, String type, ClientBasePacket cbp)");
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(con);
			}
		}
	}
	
	@Override
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp){
		int type = cbp.readC();
		switch(type){
			case 2:	// 창고 맡기기
				insert(pc, Lineage.DWARF_TYPE_NONE, cbp);
				break;
			case 3:	// 창고 찾기
				select(pc, Lineage.DWARF_TYPE_NONE, cbp);
				break;
			case 4:	// 혈맹창고 맡기기
				insert(pc, Lineage.DWARF_TYPE_CLAN, cbp);
				break;
			case 5:	// 혈맹창고 찾기
				select(pc, Lineage.DWARF_TYPE_CLAN, cbp);
				break;
			case 8:	// 요정창고 맡기기
				insert(pc, Lineage.DWARF_TYPE_ELF, cbp);
				break;
			case 9:	// 요정창고 찾기
				select(pc, Lineage.DWARF_TYPE_ELF, cbp);
				break;
		}
	}

	/**
	 * 창고에서 아이템 꺼낼때 사용하는 메서드.
	 */
	private void select(PcInstance pc, int dwarf_type, ClientBasePacket cbp){
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement st2 = null;
		ResultSet rs2 = null;
		try {
			con = DatabaseConnection.getLineage();
			
			int count = cbp.readH();
			int id = dwarf_type==Lineage.DWARF_TYPE_CLAN ? pc.getClanId() : pc.getClient().getAccountUid();
			int w_Count = getCount(con, id, dwarf_type);

			if(count>0 && count<=w_Count){
				int item_id = 0;
				int item_count = 0;

				for(int i=0 ; i<count ; ++i){
					item_id = cbp.readD();
					item_count = cbp.readD();

					switch(dwarf_type){
						case Lineage.DWARF_TYPE_CLAN:
							st = con.prepareStatement("SELECT * FROM warehouse_clan WHERE uid=? AND clan_id=?");
							break;
						case Lineage.DWARF_TYPE_ELF:
							st = con.prepareStatement("SELECT * FROM warehouse_elf WHERE uid=? AND account_uid=?");
							break;
						default:
							st = con.prepareStatement("SELECT * FROM warehouse WHERE uid=? AND account_uid=?");
							break;
					}
					st.setInt(1, item_id);
					st.setInt(2, id);
					rs = st.executeQuery();
					if(rs.next()){
						int db_uid = rs.getInt(1);
						int db_inv_id = rs.getInt(3);
						int pet_objid = rs.getInt(4);
						int letter_id = rs.getInt(5);
						String db_name = rs.getString(6);
						int db_count = rs.getInt(9);
						int db_quantity = rs.getInt(10);
						int db_en = rs.getInt(11);
						boolean db_definite = rs.getInt(12)==1;
						int db_bress = rs.getInt(13);
						int db_durability = rs.getInt(14);
						int db_time = rs.getInt(15);

						ItemInstance temp = ItemDatabase.newInstance(ItemDatabase.find(db_name));
						if(temp!=null && item_count>0 && item_count<=db_count){
							temp.setCount(item_count);
							if(pc.getInventory().isAppend(temp, temp.getCount(), false)){
								boolean aden = dwarf_type==Lineage.DWARF_TYPE_ELF ? pc.getInventory().isMeterial(Lineage.warehouse_price_elf, true) : pc.getInventory().isAden(Lineage.warehouse_price, true);
								if( aden ){
									ItemInstance temp2 = pc.getInventory().find(db_name, db_bress, true);
									if(temp2==null){
										// insert
										temp.setObjectId(db_inv_id);
										temp.setQuantity(db_quantity);
										temp.setEnLevel(db_en);
										temp.setDefinite(db_definite);
										temp.setBress(db_bress);
										temp.setDurability(db_durability);
										temp.setTime(db_time);
										temp.setPetObjectId(pet_objid);
										temp.setLetterUid(letter_id);
										pc.getInventory().append(temp, true);
										// 아이템 정보 갱신.
										temp.toWorldJoin(con, pc);
									}else{
										// update
										pc.getInventory().count(temp2, temp2.getCount()+temp.getCount(), true);
										ItemDatabase.setPool(temp);
									}
									
									db_count -= item_count;
									if(db_count<=0){
										// delete
										switch(dwarf_type){
											case Lineage.DWARF_TYPE_CLAN:
												st2 = con.prepareStatement("DELETE FROM warehouse_clan WHERE uid=?");
												break;
											case Lineage.DWARF_TYPE_ELF:
												st2 = con.prepareStatement("DELETE FROM warehouse_elf WHERE uid=?");
												break;
											default:
												st2 = con.prepareStatement("DELETE FROM warehouse WHERE uid=?");
												break;
										}
										st2.setInt(1, db_uid);
										st2.executeUpdate();
										st2.close();
									}else{
										// update
										switch(dwarf_type){
											case Lineage.DWARF_TYPE_CLAN:
												st2 = con.prepareStatement("UPDATE warehouse_clan SET count=? WHERE uid=?");
												break;
											case Lineage.DWARF_TYPE_ELF:
												st2 = con.prepareStatement("UPDATE warehouse_elf SET count=? WHERE uid=?");
												break;
											default:
												st2 = con.prepareStatement("UPDATE warehouse SET count=? WHERE uid=?");
												break;
										}
										st2.setInt(1, db_count);
										st2.setInt(2, db_uid);
										st2.executeUpdate();
										st2.close();
									}
								}else{
									if(dwarf_type==Lineage.DWARF_TYPE_ELF)
										// \f1%0%s 부족합니다.
										pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 337, "미스릴"));
									else
										// \f1아데나가 충분치 않습니다.
										pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189));
									ItemDatabase.setPool(temp);
									break;
								}
							}else{
								ItemDatabase.setPool(temp);
								break;
							}
						}
					}
					rs.close();
					st.close();
				}

			}
		} catch (Exception e) {
			lineage.share.System.println(DwarfInstance.class.toString()+" : select(PcInstance pc, boolean clan, ClientBasePacket cbp)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st2, rs2);
			DatabaseConnection.close(con, st, rs);
		}
	}

	/**
	 * 창고에 아이템 맡길때 처리하는 메서드.
	 */
	private void insert(PcInstance pc, int dwarf_type, ClientBasePacket cbp){
		Connection con = null;
		try {
			con = DatabaseConnection.getLineage();
			
			int uid = dwarf_type==Lineage.DWARF_TYPE_CLAN ? pc.getClanId() : pc.getClient().getAccountUid();
			int Count = cbp.readH();
			int w_Count = getCount(con, uid, dwarf_type);
			boolean is = dwarf_type==Lineage.DWARF_TYPE_CLAN ? pc.getClanId()>0 : true;

			if(Count>0){
				if(is && w_Count+Count <= Lineage.warehouse_max){

					for(int i=0 ; i<Count ; ++i){
						ItemInstance temp = pc.getInventory().value(cbp.readD());
						int count = cbp.readD();

						if(temp!=null && !temp.isEquipped() && temp.getItem().isWarehouse() && pc.getInventory().isRemove(temp, count, true, true)){
							if(count>0 && count<=temp.getCount()){

								// 등록하려는 아이템이 겹쳐지는 아이템이라면 디비에 겹칠 수 있는 것이 존재하는지 확인.
								boolean piles = isPiles(con, temp, uid, dwarf_type);
								if(piles){
									// update
									update(con, temp, uid, count, dwarf_type);
								}else{
									// insert
									if(count==temp.getCount()){
										insert(con, temp, temp.getObjectId(), count, uid, dwarf_type);
									}else{
										insert(con, temp, ServerDatabase.nextItemObjId(), count, uid, dwarf_type);
									}
								}
								pc.getInventory().count(temp, temp.getCount()-count, true);

							}
						}

					}

				}else{
					// \f1더이상 물건을 넣을 자리가 없습니다.
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 75));
				}
			}
			
		} catch (Exception e) {
			lineage.share.System.println(DwarfInstance.class.toString()+" : insert(PcInstance pc, int dwarf_type, ClientBasePacket cbp)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(con);
		}
	}

	/**
	 * 창고에 아이템이 몇개있는지 추출하는 메서드.
	 */
	private int getCount(Connection con, int uid, int dwarf_type) {
		int count = 0;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("SELECT COUNT(*) FROM warehouse_clan WHERE clan_id=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("SELECT COUNT(*) FROM warehouse_elf WHERE account_uid=?");
					break;
				default:
					st = con.prepareStatement("SELECT COUNT(*) FROM warehouse WHERE account_uid=?");
					break;
			}
			st.setInt(1, uid);
			rs = st.executeQuery();
			if(rs.next())
				count = rs.getInt(1);
		} catch (Exception e) {
			lineage.share.System.println(DwarfInstance.class.toString()+" : getCount(Connection con, int uid, int dwarf_type)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		return count;
	}

	/**
	 * 창고에 겹쳐질 수 있는 아이템이 존재하는지 확인하는 메서드.
	 */
		//
	private boolean isPiles(Connection con, ItemInstance item, int uid, int dwarf_type){
		boolean piles = false;
		
		if(item.getItem().isPiles()){
			PreparedStatement st = null;
			ResultSet rs = null;
			try {
				switch(dwarf_type){
					case Lineage.DWARF_TYPE_CLAN:
						st = con.prepareStatement("SELECT * FROM warehouse_clan WHERE clan_id=? AND name=? AND bress=?");
						break;
					case Lineage.DWARF_TYPE_ELF:
						st = con.prepareStatement("SELECT * FROM warehouse_elf WHERE account_uid=? AND name=? AND bress=?");
						break;
					default:
						st = con.prepareStatement("SELECT * FROM warehouse WHERE account_uid=? AND name=? AND bress=?");
						break;
				}
				st.setInt(1, uid);
				st.setString(2, item.getItem().getName());
				st.setInt(3, item.getBress());
				rs = st.executeQuery();
				piles = rs.next();
			} catch (Exception e) {
				lineage.share.System.println(DwarfInstance.class.toString()+" : isPiles(Connection con, ItemInstance item, int uid, int dwarf_type)");
				lineage.share.System.println(e);
			} finally {
				DatabaseConnection.close(st, rs);
			}
		}

		return piles;
	}

	/**
	 * 디비에 아이템 등록할때 처리하는 메서드.
	 */
	private void insert(Connection con, ItemInstance item, long inv_id, int count, int uid, int dwarf_type){
		int type = 0;
		if(item instanceof ItemWeaponInstance)
			type = 1;
		else if(item instanceof ItemArmorInstance)
			type = 2;
		else
			type = 3;

		PreparedStatement st = null;
		try {
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("INSERT INTO warehouse_clan SET clan_id=?, inv_id=?, name=?, type=?, gfxid=?, count=?, quantity=?, en=?, definite=?, bress=?, durability=?, time=?, pet_id=?, letter_id=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("INSERT INTO warehouse_elf SET account_uid=?, inv_id=?, name=?, type=?, gfxid=?, count=?, quantity=?, en=?, definite=?, bress=?, durability=?, time=?, pet_id=?, letter_id=?");
					break;
				default:
					st = con.prepareStatement("INSERT INTO warehouse SET account_uid=?, inv_id=?, name=?, type=?, gfxid=?, count=?, quantity=?, en=?, definite=?, bress=?, durability=?, time=?, pet_id=?, letter_id=?");
					break;
			}
			st.setInt(1, uid);
			st.setLong(2, inv_id);
			st.setString(3, item.getItem().getName());
			st.setInt(4, type);
			st.setInt(5, item.getItem().getInvGfx());
			st.setInt(6, count);
			st.setInt(7, item.getQuantity());
			st.setInt(8, item.getEnLevel());
			st.setInt(9, item.isDefinite()?1:0);
			st.setInt(10, item.getBress());
			st.setInt(11, item.durability);
			st.setInt(12, item.getTime());
			st.setLong(13, item.getPetObjectId());
			st.setInt(14, item.getLetterUid());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.println(DwarfInstance.class.toString()+" : insert(Connection con, ItemInstance item, int inv_id, int count, int uid, int dwarf_type)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
		
	}

	/**
	 * 창고에 같은 종류에 아이템이 존재한다는걸 미리 확인 했기때문에
	 * 그 정보를 토대로 count값만 갱신함.
	 */
	private void update(Connection con, ItemInstance item, int uid, int count, int dwarf_type){
		PreparedStatement st = null;
		try {
			switch(dwarf_type){
				case Lineage.DWARF_TYPE_CLAN:
					st = con.prepareStatement("UPDATE warehouse_clan SET count=count+? WHERE clan_id=? AND name=? AND bress=?");
					break;
				case Lineage.DWARF_TYPE_ELF:
					st = con.prepareStatement("UPDATE warehouse_elf SET count=count+? WHERE account_uid=? AND name=? AND bress=?");
					break;
				default:
					st = con.prepareStatement("UPDATE warehouse SET count=count+? WHERE account_uid=? AND name=? AND bress=?");
					break;
			}
			st.setInt(1, count);
			st.setInt(2, uid);
			st.setString(3, item.getItem().getName());
			st.setInt(4, item.getBress());
			st.executeUpdate();
		} catch (Exception e) {
			lineage.share.System.println(DwarfInstance.class.toString()+" : update(Connection con, ItemInstance item, int uid, int count, boolean clan)");
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st);
		}
	}
	
}
