package lineage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Poly;
import lineage.bean.lineage.Inventory;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public final class PolyDatabase {
	
	static private List<Poly> list;
	
	static public void init(Connection con){
		TimeLine.start("PolyDatabase..");
		
		list = new ArrayList<Poly>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement("SELECT * FROM poly");
			rs = st.executeQuery();
			while(rs.next()){
				Poly p = new Poly();
				p.setUid( rs.getInt("id") );
				p.setName( rs.getString("name") );
				p.setPolyName( rs.getString("db") );
				p.setGfxId( rs.getInt("polyid") );
				p.setGfxMode( rs.getInt("polymode") );
				p.setMinLevel( rs.getInt("minlevel") );
				p.setWeapon( rs.getInt("isWeapon") );
				p.setHelm( rs.getInt("isHelm") == 1 );
				p.setEarring( rs.getInt("isEarring") == 1 );
				p.setNecklace( rs.getInt("isNecklace") == 1 );
				p.setT( rs.getInt("isT") == 1 );
				p.setArmor( rs.getInt("isArmor") == 1 );
				p.setCloak( rs.getInt("isCloak") == 1 );
				p.setRing( rs.getInt("isRing") == 1 );
				p.setBelt( rs.getInt("isBelt") == 1 );
				p.setGlove( rs.getInt("isGlove") == 1 );
				p.setShield( rs.getInt("isShield") == 1 );
				p.setBoots( rs.getInt("isBoots") == 1 );
				p.setSkill( rs.getInt("isSkill") == 1 );

				list.add(p);
			}
		} catch (Exception e) {
			lineage.share.System.printf("%s : init(Connection con)\r\n", PolyDatabase.class.toString());
			lineage.share.System.println(e);
		} finally {
			DatabaseConnection.close(st, rs);
		}

		TimeLine.end();
	}
	
	/**
	 * 고유 이름으로 추출하기.
	 * @param name
	 * @return
	 */
	static public Poly getName(String name){
		for( Poly p : list ){
			if(p.getName().equalsIgnoreCase(name))
				return p;
		}
		return null;
	}

	/**
	 * 변신 이름으로 추출하기.
	 */
	static public Poly getPolyName(String polyName){
		if(polyName==null || polyName.length()==0) {
			return null;
		}
		for( Poly p : list ){
			if(p.getPolyName().equalsIgnoreCase(polyName))
				return p;
		}
		return null;
	}

	/**
	 * 필드 uid값으로 추출하기.
	 */
	static public Poly getPolyId(int uid){
		for( Poly p : list ){
			if(p.getUid()==uid)
				return p;
		}
		return null;
	}

	/**
	 * gfx정보와 일치하는 클레스 찾아서 리턴.
	 */
	static public Poly getPolyGfx(int gfx){
		for( Poly p : list ){
			if(p.getGfxId()==gfx)
				return p;
		}
		return null;
	}

	/**
	 * 변신에따른 아이템 해제처리 메서드.
	 */
	static public void toEquipped(Character cha, Poly p){
		Inventory inv = cha.getInventory();
		if(inv != null){
			for(int i=0 ; i<=Lineage.SLOT_NONE ; ++i){
				ItemInstance slot = inv.getSlot(i);
				if(slot!=null && slot.isEquipped()){
					if(!toEquipped(slot, p)){
						// 강제 착용 해제 처리.
						slot.setEquipped(false);
						slot.toSetoption(cha, true);
						slot.toEquipped(cha, inv);
						slot.toOption(cha, true);
					}
				}
			}
		}
	}

	/**
	 * polys를 참고해서 해당 아이템이 착용해도 되는지 판단하는 메서드.
	 */
	static private boolean toEquipped(ItemInstance item, Poly p){
		if(item instanceof ItemWeaponInstance){
			// 무기 확인
			// 1: 모든무기착용가능 2:한손무기만착용가능 3:양손무기만착용가능 4:활만착용가능 5:창만 6:지팡이만 7:장거리공격을뺀모든무기만 
			// 8:이도류만가능 9:클라우만가능 10:건들렛만가능 11:체인소드만가능 12:모든 종류
			switch(p.getWeapon()){
				case 0:
					return false;
				case 1:	// 칼종류
					return 	item.getItem().getGfxMode()==Lineage.WEAPON_SWORD ||
							item.getItem().getGfxMode()==Lineage.WEAPON_DAGGER ||
							item.getItem().getGfxMode()==Lineage.WEAPON_TOHANDSWORD;
				case 2:	// 한손무기만
					return 	item.getItem().getGfxMode()==Lineage.WEAPON_SWORD ||
							item.getItem().getGfxMode()==Lineage.WEAPON_DAGGER ||
							item.getItem().getGfxMode()==Lineage.WEAPON_AXE;
				case 3:	// 양손무기만
					return 	item.getItem().getGfxMode()==Lineage.WEAPON_TOHANDSWORD ||
							item.getItem().getGfxMode()==Lineage.WEAPON_BOW ||
							item.getItem().getGfxMode()==Lineage.WEAPON_SPEAR ||
							item.getItem().getGfxMode()==Lineage.WEAPON_WAND;
				case 4:	// 활만
					return item.getItem().getGfxMode()==Lineage.WEAPON_BOW;
				case 5:	// 창만
					return item.getItem().getGfxMode()==Lineage.WEAPON_SPEAR;
				case 6:	// 지팡이만
					return item.getItem().getGfxMode() == Lineage.WEAPON_WAND;
				case 7: // 장거리공격 아이템을뺀 모든 무기
					return 	item.getItem().getGfxMode()==Lineage.WEAPON_AXE ||
							item.getItem().getGfxMode()==Lineage.WEAPON_SPEAR ||
							item.getItem().getGfxMode()==Lineage.WEAPON_WAND ||
							item.getItem().getGfxMode()==Lineage.WEAPON_SWORD ||
							item.getItem().getGfxMode()==Lineage.WEAPON_DAGGER ||
							item.getItem().getGfxMode()==Lineage.WEAPON_TOHANDSWORD;
				case 8:	// 이도류
					return item.getItem().getGfxMode()==Lineage.WEAPON_EDORYU;
				case 9:	// 크로우
					return item.getItem().getGfxMode()==Lineage.WEAPON_CLAW;
				case 10:	// 건들렛
					return item.getItem().getGfxMode()==Lineage.WEAPON_GAUNTLET;
				case 11:	// 체인소드
					return item.getItem().getGfxMode()==Lineage.WEAPON_CHAINSWORD;
				case 12:	// 모든 종류에 무기 착용.
					return true;
			}
		}else{
			// 방어구 확인
			switch(item.getItem().getSlot()){
				case Lineage.SLOT_ARMOR:
					return p.isArmor();
				case Lineage.SLOT_CLOAK:
					return p.isCloak();
				case Lineage.SLOT_GLOVE:
					return p.isGlove();
				case Lineage.SLOT_HELM:
					return p.isHelm();
				case Lineage.SLOT_RING_LEFT:
					return p.isRing();
				case Lineage.SLOT_SHIRT:
					return p.isT();
				case Lineage.SLOT_SHIELD:
					return p.isShield();
				case Lineage.SLOT_BOOTS:
					return p.isBoots();
				case Lineage.SLOT_BELT:
					return p.isBelt();
			}
		}
		return true;
	}

	/**
	 * 아이템을 착용할때 호출해서 해당 아이템을 착용해도 되는지 확인하는 메서드.
	 * gfx에 따라 poly객체를 추출. 확인해봄.
	 */
	static public boolean toEquipped(Character cha, ItemInstance item){
		for( Poly p : list ){
			if(p.getGfxId() == cha.getGfx())
				return toEquipped(item, p);
		}
		return true;
	}
	
	static public int getSize(){
		return list.size();
	}
	
}
