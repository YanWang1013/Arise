package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.ItemSetoption;
import lineage.bean.lineage.Inventory;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.share.TimeLine;
import lineage.world.controller.BuffController;
import lineage.world.object.Character;
import lineage.world.object.magic.Bravery;
import lineage.world.object.magic.Haste;
import lineage.world.object.magic.ShapeChange;
import lineage.world.object.magic.Wafer;

public final class ItemSetoptionDatabase {

	static private List<ItemSetoption> list;

	static public void init(Connection con){
		TimeLine.start("ItemSetopionDatabase..");

		list = new ArrayList<ItemSetoption>();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM item_setoption");
			rs = st.executeQuery();
			while(rs.next()){
				ItemSetoption i = new ItemSetoption();
				i.setUid( rs.getInt(1) );
				i.setName( rs.getString(2) );
				i.setCount( rs.getInt(3) );
				i.setAdd_hp( rs.getInt(4) );
				i.setAdd_mp( rs.getInt(5) );
				i.setAdd_str( rs.getInt(6) );
				i.setAdd_dex( rs.getInt(7) );
				i.setAdd_con( rs.getInt(8) );
				i.setAdd_int( rs.getInt(9) );
				i.setAdd_wis( rs.getInt(10) );
				i.setAdd_cha( rs.getInt(11) );
				i.setAdd_ac( rs.getInt(12) );
				i.setAdd_mr( rs.getInt(13) );
				i.setTic_hp( rs.getInt(14) );
				i.setTic_mp( rs.getInt(15) );
				i.setPolymorph( rs.getInt(16) );
				i.setWindress( rs.getInt(17) );
				i.setWateress( rs.getInt(18) );
				i.setFireress( rs.getInt(19) );
				i.setEarthress( rs.getInt(20) );
				i.setHaste( rs.getString(21).equalsIgnoreCase("true") );
				i.setBrave( rs.getString(22).equalsIgnoreCase("true") );
				i.setWafer( rs.getString(23).equalsIgnoreCase("true") );

				list.add(i);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", ItemSetoptionDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}

	static public ItemSetoption find(int uid){
		for( ItemSetoption is : list ){
			if(is.getUid() == uid)
				return is;
		}
		return null;
	}

	static public void setting(Character cha, ItemSetoption i, boolean equipped, boolean sendPacket){
		Inventory inv = cha.getInventory();
		if(inv != null){
			// 초기화
			int Hp = 0;
			int Mp = 0;
			int Str = 0;
			int Dex = 0;
			int Con = 0;
			int Int = 0;
			int Wis = 0;
			int Cha = 0;
			int Ac = 0;
			int Mr = 0;
			int Tichp = 0;
			int Ticmp = 0;
			int Gm = 0;
			int poly = 0;
			boolean haste = false;
			boolean brave = false;
			boolean wafer = false;
			// 정리
			for( ItemSetoption is : inv.getSetitemList() ){
				Hp += is.getAdd_hp();
				Mp += is.getAdd_mp();
				Str += is.getAdd_str();
				Dex += is.getAdd_dex();
				Con += is.getAdd_con();
				Int += is.getAdd_int();
				Wis += is.getAdd_wis();
				Cha += is.getAdd_cha();
				Ac += is.getAdd_ac();
				Mr += is.getAdd_mr();
				Tichp += is.getTic_hp();
				Ticmp += is.getTic_mp();
				if(is.getPolymorph() > 0)
					poly = is.getPolymorph();
				if(is.isHaste())
					haste = true;
				if(is.isBrave())
					brave = true;
				if(is.isWafer())
					wafer = true;
			}
			// 적용
			cha.setSetitemHp( Hp );
			cha.setSetitemMp( Mp );
			cha.setSetitemStr( Str );
			cha.setSetitemDex( Dex );
			cha.setSetitemCon( Con );
			cha.setSetitemInt( Int );
			cha.setSetitemWis( Wis );
			cha.setSetitemCha( Cha );
			cha.setSetitemAc( Ac );
			cha.setSetitemMr( Mr );
			cha.setSetitemTicHp( Tichp );
			cha.setSetitemTicMp( Ticmp );
			cha.setGm( Gm );
			if(poly > 0)
				ShapeChange.onBuff(cha, cha, PolyDatabase.getPolyGfx(poly), -1, false, sendPacket);
			else if(!equipped && i.getPolymorph()>0)
				BuffController.remove(cha, ShapeChange.class);
			if(haste)
				Haste.init(cha, -1);
			else if(!equipped && i.isHaste())
				BuffController.remove(cha, Haste.class);
			if(brave)
				Bravery.init(cha, -1);
			else if(!equipped && i.isBrave())
				BuffController.remove(cha, Bravery.class);
			if(wafer)
				Wafer.init(cha, -1);
			else if(!equipped && i.isWafer())
				BuffController.remove(cha, Wafer.class);
			if(sendPacket)
				cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
		}
	}
	
	static public int getSize(){
		return list.size();
	}
}
