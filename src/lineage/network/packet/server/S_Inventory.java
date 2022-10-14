package lineage.network.packet.server;

import lineage.bean.database.Item;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.item.Candle;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.Letter;
import lineage.world.object.item.wand.EbonyWand;
import lineage.world.object.item.wand.MapleWand;
import lineage.world.object.item.wand.PineWand;

public class S_Inventory extends ServerBasePacket {

	protected void toArmor(ItemInstance item){
		toArmor(item.getItem(), item.getDurability(), item.getEnLevel(), item.getWeight(), item.getDynamicMr());
	}

	protected void toWeapon(ItemInstance item){
		toWeapon(item.getItem(), item.getDurability(), item.getEnLevel(), item.getWeight());
	}

	protected void toEtc(ItemInstance item){
		if(item.getItem().getNameIdNumber()==1173){
			DogCollar dc = (DogCollar)item;
			writeC(0x0f);	// 15
			writeC(0x19);
			writeH(dc.getPetClassId());
			writeC(0x1a);
			writeH(dc.getPetLevel());
			writeC(0x1f);
			writeH(dc.getPetHp());
			writeC(0x17);
			writeC(item.getItem().getMaterial());
			writeD(item.getWeight());
		}else{
			toEtc(item.getItem(), item.getWeight());
		}
	}

	protected void toArmor(Item item, int durability, int enlevel, int weight, int dynamic_mr){
		writeC(getOptionSize(item, durability, enlevel));
		writeC(19);
		writeC(item.getAc());
		writeC(item.getMaterial());
		if(Lineage.server_version > 300)
			writeC(-1);	// Grade
		writeD(weight);
		if(enlevel != 0){
			writeC(0x02);
			writeC(enlevel);
		}
		// 강화수 2(cc)
		// 손상도 3(cc)
		int type = item.getRoyal() != 1 ? 0 : 1;
		type += item.getKnight() != 1 ? 0 : 2;
		type += item.getElf() != 1 ? 0 : 4;
		type += item.getWizard() != 1 ? 0 : 8;
		type += item.getDarkElf() != 1 ? 0 : 16;
		type += item.getDragonKnight() != 1 ? 0 : 32;
		type += item.getBlackWizard() != 1 ? 0 : 64;
		writeC(7);
		writeC(type);
		if(item.getAddStr() != 0){
			writeC(8);
			writeC(item.getAddStr());
		}
		if(item.getAddDex() != 0){
			writeC(9);
			writeC(item.getAddDex());
		}
		if(item.getAddCon() != 0){
			writeC(10);
			writeC(item.getAddCon());
		}
		if(item.getAddWis() != 0){
			writeC(11);
			writeC(item.getAddWis());
		}
		if(item.getAddInt() != 0){
			writeC(12);
			writeC(item.getAddInt());
		}
		if(item.getAddCha() != 0){
			writeC(13);
			writeC(item.getAddCha());
		}
		if(item.getAddHp() != 0){
			writeC(14);
			writeH(item.getAddHp());
		}
		if(item.getAddMr()!=0 || dynamic_mr!=0){
			writeC(15);
			writeH(item.getAddMr() + dynamic_mr);
		}
		if(item.getAddSp() != 0){
			writeC(17);
			writeC(item.getAddSp());
		}
//		if(item.isHaste())
//			writeC(18);
		if(item.getLevelMin() != 0){
			writeC(26);
			writeH(item.getLevelMin());
		}
		if(item.getFireress() != 0){
			writeC(27);		// 불
			writeC(item.getFireress());
		}
		if(item.getWaterress() != 0){
			writeC(28);	// 물
			writeC(item.getWaterress());
		}
		if(item.getWindress() != 0){
			writeC(29);	// 바람
			writeC(item.getWindress());
		}
		if(item.getEarthress() != 0){
			writeC(30);	// 땅
			writeC(item.getEarthress());
		}
	}

	protected void toWeapon(Item item, int durability, int enlevel, int weight){
		writeC(getOptionSize(item, durability, enlevel));
		writeC(0x01);
		writeC(item.getDmgMin());
		writeC(item.getDmgMax());
		writeC(item.getMaterial());
		writeD(weight);
		if(enlevel != 0){
			writeC(0x02);
			writeC(enlevel);
		}
		if(durability != 0){
			writeC(3);
			writeC(durability);
		}
		if(item.isTohand())
			writeC(4);
		if(item.getAddHit() != 0){
			writeC(5);
			writeC(item.getAddHit());
		}
		if(item.getAddDmg() != 0){
			writeC(6);
			writeC(item.getAddDmg());
		}
		int type = item.getRoyal() != 1 ? 0 : 1;
		type += item.getKnight() != 1 ? 0 : 2;
		type += item.getElf() != 1 ? 0 : 4;
		type += item.getWizard() != 1 ? 0 : 8;
		type += item.getDarkElf() != 1 ? 0 : 16;
		type += item.getDragonKnight() != 1 ? 0 : 32;
		type += item.getBlackWizard() != 1 ? 0 : 64;
		writeC(7);
		writeC(type);
		if(item.getAddStr() != 0){
			writeC(8);
			writeC(item.getAddStr());
		}
		if(item.getAddDex() != 0){
			writeC(9);
			writeC(item.getAddDex());
		}
		if(item.getAddCon() != 0){
			writeC(10);
			writeC(item.getAddCon());
		}
		if(item.getAddWis() != 0){
			writeC(11);
			writeC(item.getAddWis());
		}
		if(item.getAddInt() != 0){
			writeC(12);
			writeC(item.getAddInt());
		}
		if(item.getAddCha() != 0){
			writeC(13);
			writeC(item.getAddCha());
		}
		if(item.getAddHp() != 0){
			writeC(14);
			writeH(item.getAddHp());
		}
		if(item.getAddMr() != 0){
			writeC(15);
			writeH(item.getAddMr());
		}
		//	if(item->getItem().getSteal_mp() != 0){
		//		writeC(16);
		//	}
		if(item.getAddSp() != 0){
			writeC(17);
			writeC(item.getAddSp());
		}
//		if(item.isHaste())
//			writeC(18);
		// 활명중율 24(cc)
		// AddMp 32(cc)
		// HP흡수 34(c)
		//활추가타격치 35(cc)
	}

	protected void toEtc(Item item, int weight){
		writeC(0x06);
		writeC(0x17);
		writeC(item.getMaterial());
		writeD(weight);
	}

	protected String getName(ItemInstance item){
		StringBuffer sb = new StringBuffer();
		if(item.getItem().getNameIdNumber()==1075 && item.getItem().getInvGfx()!=464){
			Letter letter = (Letter)item;
			sb.append(letter.getFrom());
			sb.append(" : ");
			sb.append(letter.getSubject());
		}else{
			if(item.isDefinite() && (item instanceof ItemWeaponInstance || item instanceof ItemArmorInstance)){
				if(item.getEnLevel() >= 0){
					sb.append("+");
				}
				sb.append(item.getEnLevel());
				sb.append(" ");
			}
			sb.append(item.getName());
			// 환상 아이템 시간 표현.
//			if(item instanceof ItemIllusionInstance){
//				long time = item.getTime();
//				if(time > 0){
//					time *= 1000;
//					sb.append(" [");
//					sb.append( String.format("%02d:%02d", Util.getHours(time), Util.getMinutes(time)) );
//					sb.append("]");
//				}
//			}
			// 착용중인 아이템 표현
			if(item.isEquipped()){
				if(item instanceof ItemWeaponInstance){
					sb.append(" ($9)");
				}else if(item instanceof ItemArmorInstance){
					sb.append(" ($117)");
				}else if(item instanceof Candle){
					// 양초, 등잔
					sb.append(" ($10)");
				}
			}
			if(item.getCount()>1){
				sb.append(" (");
				sb.append(item.getCount());
				sb.append(")");
			}
			if(	item.isDefinite() && (item instanceof MapleWand || item instanceof PineWand || item instanceof EbonyWand) ){
				sb.append(" (");
				sb.append(item.getQuantity());
				sb.append(")");
			}
			if(item.getItem().getNameIdNumber()==1173){
				DogCollar dc = (DogCollar)item;
				sb.append(" [Lv.");
				sb.append(dc.getPetLevel());
				sb.append(" ");
				sb.append(dc.getPetName());
				sb.append("]");
			}
			if(item.getInnRoomKey()>0){
				sb.append(" #");
				sb.append(item.getInnRoomKey());
			}
		}

		return sb.toString();
	}

	protected int getOptionSize(ItemInstance item){
		return getOptionSize(item.getItem(), item.getDurability(), item.getEnLevel());
	}

	protected int getOptionSize(Item item, int durability, int enlevel){
		int size = 0;
		if(item.getType1().equalsIgnoreCase("armor")){
			if(Lineage.server_version > 300)
				size += 10;
			else
				size += 9;
			if(item.getLevelMin() != 0)
				size += 3;
			if(item.getFireress() != 0)
				size += 2;
			if(item.getWaterress() != 0)
				size += 2;
			if(item.getWindress() != 0)
				size += 2;
			if(item.getEarthress() != 0)
				size += 2;
		}else if(item.getType1().equalsIgnoreCase("weapon")){
			size += 10;
			if(durability != 0)
				size += 2;
			if(item.isTohand())
				size += 1;
			if(item.getAddHit() != 0)
				size += 2;
			if(item.getAddDmg() != 0)
				size += 2;
		}else{
			return 0;
		}
		if(enlevel != 0)
			size += 2;
		if(item.getAddStr() != 0)
			size += 2;
		if(item.getAddDex() != 0)
			size += 2;
		if(item.getAddCon() != 0)
			size += 2;
		if(item.getAddInt() != 0)
			size += 2;
		if(item.getAddCha() != 0)
			size += 2;
		if(item.getAddWis() != 0)
			size += 2;
		if(item.getAddMr() != 0)
			size += 3;
		if(item.getAddSp() != 0)
			size += 2;
//		if(item.isHaste())
//			size += 1;
		if(item.getAddHp() != 0)
			size += 3;
		return size;
	}
	
}
