package lineage.world.object.item;

import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_InventoryBress;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemIllusionInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public class Enchant extends ItemIllusionInstance {

	private String EnMsg[];

	public Enchant(){
		EnMsg = new String[3];
	}

	/**
	 * 인첸트 확율계산할지 여부를 리턴함.
	 */
	protected boolean isChance(ItemInstance item){
		return item.getEnLevel()>=item.getItem().getSafeEnchant() && bress!=2;
	}

	/**
	 * 인첸트를 처리할 메서드.
	 */

	protected int toEnchant(Character cha, ItemInstance item){
		if(Lineage.item_enchant_weapon_bless_min == 0){
			return toEnchantOld(cha, item);
		}else{
			boolean chance = isChance(item);
			boolean enchant = true;
			double enchant_chance = 30;
			int rnd = 1;
	
			EnMsg[0] = item.toString();
			// 검게, 파랗게, 은색으로
			EnMsg[1] = bress==2 ? "$246" : item instanceof ItemWeaponInstance ? "$245" : "$252";	// 검게
			EnMsg[2] = "$247";	// 한 순간
			
			// 인첸 범위값 추출.
			if(item instanceof ItemWeaponInstance){
				switch(bress){
					case 0:
						rnd = Util.random(Lineage.item_enchant_weapon_bless_min, Lineage.item_enchant_weapon_bless_max);
						break;
					case 1:
						rnd = Util.random(Lineage.item_enchant_weapon_normal_min, Lineage.item_enchant_weapon_normal_max);
						break;
					default:
						rnd = Util.random(Lineage.item_enchant_weapon_curse_min, Lineage.item_enchant_weapon_curse_max);
						break;
				} 
			}else{
				switch(bress){
					case 0:
						rnd = Util.random(Lineage.item_enchant_weapon_bless_min, Lineage.item_enchant_weapon_bless_max);
						break;
					case 1:
						rnd = Util.random(Lineage.item_enchant_weapon_normal_min, Lineage.item_enchant_weapon_normal_max);
						break;
					default:
						rnd = Util.random(Lineage.item_enchant_weapon_curse_min, Lineage.item_enchant_weapon_curse_max);
						break;
				} 
			}
			
			// 확률계산 해야한다면.
			if(chance){
				// 안전인첸값을 기준으로 인첸값 만큼 확률 낮추기.
				for(int i=item.getItem().getSafeEnchant() ; i<item.getEnLevel() ; ++i)
					enchant_chance /= 2;
				// 성공여부 계산.
				if(item.getEnLevel()>=0){
					// 인첸트 성공 확율 추출.
					enchant = Util.random(0.0D, enchant_chance*Lineage.rate_enchant) >= Util.random(0.0D, 100D);
				}else{
					// 인첸트값이 0보다 작은 -1 이하 일경우.
					enchant = Util.random(1, 100) >= 50;
				}
			}
	
			// 인첸값이 1이상 떳거나 감소햇을경우
			if(rnd>1 || rnd<-1)
				EnMsg[2] = "$248";	// 잠시 (+2)
	
			// 인첸 성공시 아이템에 실제 변수 설정하는 부분. (저주 주문서는 바로바로 처리.)
			if(enchant || bress==2){
				item.setEnLevel(item.getEnLevel() + rnd); // 아이템 인첸트값 set
				if(Lineage.server_version<=144){
					cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
					cha.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), item));
				}else{
					cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
				}
				// \f1%0%s %2 %1 빛납니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 161, EnMsg[0], EnMsg[1], EnMsg[2]));
				// 월드에 메세지 표현처리.
				if(item instanceof ItemWeaponInstance && Lineage.world_message_enchant_weapon>0 && Lineage.world_message_enchant_weapon<item.getEnLevel()){
					ChattingController.toChatting(null, String.format("%s님께서 %d인첸에 성공하였습니다.", cha.getName(), item.getEnLevel()), Lineage.CHATTING_MODE_GLOBAL);
				}
				if(item instanceof ItemArmorInstance && Lineage.world_message_enchant_armor>0 && Lineage.world_message_enchant_armor<item.getEnLevel()){
					ChattingController.toChatting(null, String.format("%s님께서 %d인첸에 성공하였습니다.", cha.getName(), item.getEnLevel()), Lineage.CHATTING_MODE_GLOBAL);
				}
				// 0% 확율로 축으로 변경하기.
				if(Lineage.item_enchant_bless && bress==0 && item.getBress()!=0 && Util.random(0, 100)==0){
					item.setBress(0);
					cha.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), item));
				}
			}else{
				rnd = 0;
				// \f1%0%s 강렬하게%1 빛나더니 증발되어 사라집니다.
				cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 164, item.toString(), " 붉게"));
			}
	
			return rnd;
		}
	}

	protected int toEnchantOld(Character cha, ItemInstance item){
		boolean chance = isChance(item);
		boolean enchant = false;
		boolean double_enchant = false;
		double enchant_chance = 30;

		EnMsg[0] = item.toString();
		EnMsg[2] = "$247";	// 한 순간
		int rnd = 1;

		if(chance){
			// 인첸트 성공여부 확율을 체크해야 할경우
			if(item instanceof ItemWeaponInstance){
				// 무기
				EnMsg[1] = "$245";	// 파랗게
				for(int i=6 ; i<item.getEnLevel() ; ++i)
					enchant_chance /= 2;
				if(bress == 0){
					// 축 주문서일경우.
					if(item.getEnLevel()<8)
						// 인첸트레벨이 8보다작은 0~7 사이라면 더블찬스 체크.
						double_enchant = Util.random(1, 100)<=30;
				}
			}else{
				// 방어구
				EnMsg[1] = "$252";	// 은색으로
				if(item.getItem().getName().startsWith("요정족")){
					// 요정족 아이템일 경우.
					enchant_chance = 15;
					for(int i=6 ; i<item.getEnLevel() ; ++i)
						enchant_chance /= 2;
					if(bress == 0){
						// 축 주문서일경우.
						if(item.getEnLevel()<9)
							// 인첸트레벨이 9보다작은 0~8 사이라면 더블찬스 체크.
							double_enchant = Util.random(1, 100)<=15;
					}
				}else if(item.getItem().getMaterial() == 9){
					// 뼈재질아이템일 경우.
					for(int i=0 ; i<item.getEnLevel() ; ++i)
						enchant_chance /= 2;
				}else{
					// 일반 아이템일 경우.
					for(int i=4 ; i<item.getEnLevel() ; ++i)
						enchant_chance /= 2;
					if(bress == 0){
						// 축 주문서일경우.
						if(item.getEnLevel()<7)
							// 인첸트레벨이 7보다작은 0~6 사이라면 더블찬스 체크.
							double_enchant = Util.random(1, 100)<=30;
					}
				}
			}
			if(item.getEnLevel()>=0){
				// 인첸트 성공 확율 추출.
				enchant = Util.random(0.0D, enchant_chance*Lineage.rate_enchant) >= Util.random(0.0D, 100D);
			}else{
				// 인첸트값이 0보다 작은 -1 이하 일경우.
				enchant = Util.random(1, 100) >= 50;
			}
		}else{
			enchant = true;
			if(item instanceof ItemWeaponInstance)
				EnMsg[1] = "$245";	// 파랗게
			else
				EnMsg[1] = "$252";	// 은색으로
			if(bress == 0){
				// 축 주문서이면서 무조건 성공일경우 더블찬스 체크.
				double_enchant = Util.random(1, 100)<=30;
			}else if(bress == 2){
				EnMsg[1] = "$246";	// 검게
				rnd = -1;
			}
		}

		// 인첸값이 더블로 떳을경우 실제 인첸적용할 변수 2로 변경.
		if(double_enchant){
			EnMsg[2] = "$248";	// 잠시 (+2)
			rnd = 2;
		}

		// 인첸 성공시 아이템에 실제 변수 설정하는 부분.
		if(enchant){
			item.setEnLevel(item.getEnLevel() + rnd); // 아이템 인첸트값 set
			if(Lineage.server_version<=144){
				cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
				cha.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), item));
			}else{
				cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
			}
			// \f1%0%s %2 %1 빛납니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 161, EnMsg[0], EnMsg[1], EnMsg[2]));
			// 월드에 메세지 표현처리.
			if(item instanceof ItemWeaponInstance && Lineage.world_message_enchant_weapon>0 && Lineage.world_message_enchant_weapon<item.getEnLevel()){
				ChattingController.toChatting(null, String.format("%s님께서 %d인첸에 성공하였습니다.", cha.getName(), item.getEnLevel()), Lineage.CHATTING_MODE_GLOBAL);
			}
			if(item instanceof ItemArmorInstance && Lineage.world_message_enchant_armor>0 && Lineage.world_message_enchant_armor<item.getEnLevel()){
				ChattingController.toChatting(null, String.format("%s님께서 %d인첸에 성공하였습니다.", cha.getName(), item.getEnLevel()), Lineage.CHATTING_MODE_GLOBAL);
			}
			// 0% 확율로 축으로 변경하기.
			if(Lineage.item_enchant_bless && bress==0 && item.getBress()!=0 && Util.random(0, 100)==0){
				item.setBress(0);
				cha.toSender(S_InventoryBress.clone(BasePacketPooling.getPool(S_InventoryBress.class), item));
			}
		}else{
			rnd = 0;
			// \f1%0%s 강렬하게%1 빛나더니 증발되어 사라집니다.
			cha.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 164, item.toString(), " 붉게"));
		}

		return rnd;
	}

}
