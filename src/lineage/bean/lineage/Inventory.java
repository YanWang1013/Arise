package lineage.bean.lineage;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Item;
import lineage.bean.database.ItemSetoption;
import lineage.database.ItemDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.server.S_CharacterStat;
import lineage.network.packet.server.S_InventoryAdd;
import lineage.network.packet.server.S_InventoryCount;
import lineage.network.packet.server.S_InventoryDelete;
import lineage.network.packet.server.S_InventoryEquipped;
import lineage.network.packet.server.S_InventoryStatus;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAdd;
import lineage.plugin.Plugin;
import lineage.plugin.PluginController;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.World;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.MonsterInstance;
import lineage.world.object.instance.NpcInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Arrow;
import lineage.world.object.item.Candle;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.SlimeRaceTicket;
import lineage.world.object.item.ThrowingKnife;
import lineage.world.object.npc.kingdom.KingdomCrown;

public class Inventory {

	// 인벤토리아이템 목록
	private List<ItemInstance> list;
	// 셋트아이템 목록
	private List<ItemSetoption> setitem_list;
	// 착용된 아이템 묵음
	private ItemInstance slot[];
	private Character cha;
	private double weight;
	
	public Inventory(){
		setitem_list = new ArrayList<ItemSetoption>();
		list = new ArrayList<ItemInstance>();
		slot = new ItemInstance[14];
	}
	
	public Inventory clone(Character cha){
		this.cha = cha;
		return this;
	}
	
	/**
	 * 다시 풀에 객체가 들어갈때 호출됨.
	 *  : 관리중인 아이템 객체을 아이템관리쪽 풀에 넣기위함.
	 *    그외 메모리 관리 처리 담당.
	 */
	public void close(){
		for(int i=0 ; i<=Lineage.SLOT_NONE ; ++i)
			slot[i] = null;
		for(ItemInstance i : list)
			ItemDatabase.setPool(i);
		list.clear();
		setitem_list.clear();
		cha = null;
	}
	
	/**
	 * 관리중인 아이템 리턴.
	 * @return
	 */
	public List<ItemInstance> getList(){
		return list;
	}
	
	/**
	 * 적용된 셋트옵션 리턴처리 함수.
	 * @return
	 */
	public List<ItemSetoption> getSetitemList(){
		return setitem_list;
	}

	/**
	 * 착용중인 슬롯내에 아이템 리턴.
	 */
	public ItemInstance getSlot(int slot){
		return this.slot[slot];
	}
	
	public void setSlot(int slot, ItemInstance item){
		this.slot[slot] = item;
	}
	
	/**
	 * 아이템 드랍시 처리되는 메서드.
	 */
	public void toDrop(ItemInstance item, long count, int x, int y, boolean packet){
		if(!cha.isInvis() && isRemove(item, count, packet, false)){
			if(item.getCount()-count<=0){
				remove(item, true);
			}else{
				count(item, item.getCount()-count, true);
				item = ItemDatabase.newInstance(item);
				item.setCount(count);
			}
			// 월드에 등록.
			item.toTeleport(x, y, cha.getMap(), false);
			// 드랍됫다는거 알리기.
			item.toDrop(cha);

		}
	}
	
	/**
	 * 아이템 픽업시 처리하는 메서드.
	 */
	public void toPickup(object o, long count){
		// 면류관 픽업은 따로 처리.
		if(o instanceof KingdomCrown){
			o.toPickup(cha);
			return;
		}
		
		// 추가 가능여부 확인.
		if(!cha.isInvis() && o instanceof ItemInstance && isAppend((ItemInstance)o, count, false)){
			ItemInstance item = (ItemInstance)o;
			cha.setHeading( Util.calcheading(cha, item.getX(), item.getY()) );
			cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_GET), true);

			if(item.getCount()==count){
				// 모두 주울때

				//----- 방식 1
				// 월드에서 제거.
				item.clearList(true);
				World.remove(item);
				
				ItemInstance temp = null;
				if(item.getItem().isPiles()){
					// 겹쳐지는 아이템일경우 같은 종류에 아이템이 존재하는지 확인 및 추출.
					temp = find(item);
				}
				if(temp != null){
					// 수량 증가.
					count(temp, temp.getCount()+item.getCount(), true);
					// 픽업된거 알리기
					temp.toPickup(cha);
					// 메모리 정리
					ItemDatabase.setPool(item);
				}else{
					// 추가.
					append(item, true);
				}
				
				//----- 방식 2 (픽업할때 버프적용된 item객체가 날라가는 문제가 생김. 그래서 방식1 사용함.)
				/*ItemInstance temp = append(item, count);
				// 양초 때문에 넣은 소스. 픽업후에도 켜짐상태 유지하기 위해.
				if(temp.isEquipped()){
					temp.setEquipped(false);
					temp.toClick(cha, null);
				}

				// 메모리 정리
				ItemDatabase.setPool(item);*/
				
			}else{
				// 일부분만 주울때.

				// 추가.
				append(item, count);
				
				// 갯수 갱신 표현.
				item.setCount(item.getCount()-count);
				item.toSender(S_ObjectAdd.clone(BasePacketPooling.getPool(S_ObjectAdd.class), item, item), false);
			}
		}
	}
	
	/**
	 * 오브젝트아이디로 해당하는 아이템 찾는 함수.
	 * @param object_id
	 * @return
	 */
	public ItemInstance value(int object_id){
		for(ItemInstance i : list){
			if(i.getObjectId()==object_id)
				return i;
		}
		return null;
	}
	
	/**
	 * 인벤토리에 아이템 등록처리하는 함수.
	 *  : 겹쳐지는 아이템 존재하는지 확인해서 처리도 함.
	 *  : 참고용으로 사용된 item 객체는 반드시 메모리 해제처리를 고려해야함.
	 * @param ii
	 */
	public ItemInstance append(ItemInstance item, long count){
		ItemInstance temp = null;
		if(item.getItem().isPiles()){
			// 겹쳐지는 아이템일경우 같은 종류에 아이템이 존재하는지 확인 및 추출.
			temp = find(item);
		}
		if(temp != null){
			// 수량 증가.
			count(temp, temp.getCount()+count, true);
			// 픽업된거 알리기
			temp.toPickup(cha);
		}else{
			if(item.getItem().isPiles()){
				// 객체 생성.
				temp = ItemDatabase.newInstance(item);
				// 갯수 갱신
				temp.setCount(count);
				// 추가.
				append(temp, true);
			}else{
				for(int i=0 ; i<count ; ++i){
					// 객체 생성.
					temp = ItemDatabase.newInstance(item);
					// 갯수 갱신
					temp.setCount(1);
					// 추가.
					append(temp, true);
				}
			}
		}
		
		return temp;
	}
	
	/**
	 * 아이템 인벤에 추가하는 함수
	 * @param item		: 등록할 아이템
	 * @param packet	: 패킷 전송 여부
	 */
	public void append(ItemInstance item, boolean packet){
		if(item!=null && item.getCount()>=0){
			list.add(item);
			if(packet && cha!=null){
				if(cha instanceof PcInstance)
					cha.toSender(S_InventoryAdd.clone(BasePacketPooling.getPool(S_InventoryAdd.class), item));
				// 사용자가 월드 접속할때에도 해당 메서드를 사용함. 그렇기 때문에 월드에 접속을 다 완료한후 그러니깐
				// 일반 필드에서 아이템 픽업하거나 할때에만 해당 사항을 수행하도록 유도.
				if(!cha.isWorldDelete()){
					updateWeight();
					if(cha instanceof PcInstance)
						cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
				}
			}
			// 픽업된거 알리기.
			item.toPickup(cha);
		}
	}

	/**
	 * 인벤토리에 있는 아이템 제거.
	 */
	public void remove(ItemInstance item, boolean packet){
		if(item != null){
			// 인벤에 제거.
			list.remove(item);
			if(packet && cha!=null){
				if(cha instanceof PcInstance)
					cha.toSender(S_InventoryDelete.clone(BasePacketPooling.getPool(S_InventoryDelete.class), item));
				if(!cha.isWorldDelete()){
					updateWeight();
					if(cha instanceof PcInstance)
						cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
				}
			}
		}
	}
	
	/**
	 * 해당 아이템 갯수 변환.
	 * @param item
	 * @param packet
	 */
	public void count(ItemInstance item, long count, boolean packet){
		item.setCount(count);
		if(count <= 0){
			remove(item, packet);
			ItemDatabase.setPool(item);
		}else{
			if(packet && cha!=null && !cha.isWorldDelete()){
				updateWeight();
				if(cha instanceof PcInstance){
					if(Lineage.server_version<=144){
						cha.toSender(S_InventoryEquipped.clone(BasePacketPooling.getPool(S_InventoryEquipped.class), item));
						cha.toSender(S_InventoryCount.clone(BasePacketPooling.getPool(S_InventoryCount.class), item));
					}else{
						cha.toSender(S_InventoryStatus.clone(BasePacketPooling.getPool(S_InventoryStatus.class), item));
					}
					cha.toSender(S_CharacterStat.clone(BasePacketPooling.getPool(S_CharacterStat.class), cha));
				}
			}
		}
	}

	/**
	 * 아이템을 제거하기전 조건검색하는 메서드.
	 */
	public boolean isRemove(ItemInstance item, long count, boolean packet, boolean dustbin){
		if(item==null || item.getCount()<count || count<=0 || item.getItem()==null)
			return false;

		if(item.isEquipped()){
			// 양초 등잔 랜턴은 무시하기.
			if( !(item instanceof Candle) ){
				if(packet && cha!=null && cha instanceof PcInstance)
					// \f1착용하고 있는 것을 버릴수 없습니다.
					cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 125) );
				return false;
			}
		}
		if(!dustbin && (!item.getItem().isDrop() || !item.getItem().isTrade())){
			if(packet && cha!=null && cha instanceof PcInstance)
				// \f1%0%d 버리거나 남에게 줄 수 없습니다.
				cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 210, item.toString()) );
			return false;
		}
		// 펫목걸이 확인처리 구간.
		if(item instanceof DogCollar){
			DogCollar dc = (DogCollar)item;
			// 현재 펫이 스폰된 상태일경우 무시하기.
			if(dc.isPetSpawn()){
				// \f1%0%d 버리거나 남에게 줄 수 없습니다.
				cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 210, item.toString()) );
				return false;
			}
		}
		return true;
	}

	/**
	 * 아이템을 등록하기전에 해당 아이템을 추가가 가능한지 확인하는 메서드.
	 * 패킷 처리도 함께함.
	 */
	public boolean isAppend(Item item, long count){
		if(count<=0)
			return false;
		if(list.size()>=Lineage.inventory_max){
			if(cha instanceof PcInstance){
				// \f1한 캐릭터가 들고 다닐 수 있는 아이템의 최대 가짓수는 180개입니다.
				cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 263) );
				return false;
			}else if(cha instanceof MonsterInstance){
				// 몬스터라면 마지막에 등록한 아이템 제거하기.
				ItemInstance ii = list.get( list.size()-1 );
				if(ii != null){
					list.remove(ii);
					ItemDatabase.setPool( ii );
				}
			}
		}
		if(item!=null && !isWeight( item.getWeight()*count) ){
			// 사용자만 무게 체크하기.
			if(cha instanceof PcInstance){
				// 소지품이 너무 무거워서 더 들 수 없습니다.
				cha.toSender( S_Message.clone(BasePacketPooling.getPool(S_Message.class), 82) );
				return false;
			}
		}
		return true;
	}

	/**
	 * 아이템을 등록하기전에 해당 아이템을 추가가 가능한지 확인하는 메서드.
	 * 패킷 처리도 함께함.
	 */
	public boolean isAppend(ItemInstance item, long count, boolean shop){
		if(!shop && (item!=null && item.getCount()<count)){
			// 갯수가 잘못 됫을때.
			return false;
		}
		return isAppend(item.getItem(), count);
	}

	/**
	 * 인벤토리에 최대허용할수 있는 무게를 확인하는 메서드.
	 * 30%를 맥스게이지로 잡고 연산함.
	 */
	private boolean isWeight(double weight){
		weight += this.weight;
		return (weight/getMaxWeight())*30 < 30;
	}

	/**
	 * 30을 백분율로 잡고 그값을 리턴함.
	 */
	public int getWeightPercent(){
		updateWeight();
		int p = (int)((weight/getMaxWeight())*30);
		if(p>29)
			p = 29;
		return p;
	}

	public void appendSetoption(ItemSetoption is){
		setitem_list.add(is);
	}

	public void removeSetoption(ItemSetoption is){
		setitem_list.remove(is);
	}

	public boolean isSetoption(ItemSetoption is){
		for( ItemSetoption i : setitem_list ){
			if(i.getUid() == is.getUid())
				return true;
		}
		return false;
	}

	/**
	 * 현재 소유하고있는 아이템들의 전체 무게를 갱신한다.
	 */
	private void updateWeight(){
		weight = 0;
		for( ItemInstance item : list )
			weight += item.getWeight();
	}

	/**
	 * 아이템을 들수있는 최대값을 추출.
	 */
	private double getMaxWeight(){
		// 플러그인 에서 추출.
		Plugin p = PluginController.find(Inventory.class);
		if(p != null)
			return ((lineage.plugin.bean.Inventory)p).getMaxWeight(cha);
		
		double max_weight = 0;
		if(cha instanceof PcInstance)
			max_weight = ((cha.getTotalStr()+cha.getTotalCon()+cha.toOriginalStatConWeight()+cha.toOriginalStatStrWeight())/2) * 250;
		else if(cha instanceof NpcInstance)
			max_weight = 2000;
		// 디크리즈웨이트 마법에 따른 최대소지무게 연산
		if(cha.isBuffDecreaseWeight())
			max_weight += 800;
		// 설정파일에 대한 최대무게 연산
		max_weight += Lineage.inventory_weight_max;
		// 퍼센트값을 이용한 최대소지무게 연산
		max_weight += max_weight * cha.getItemWeight();
		
		return max_weight;
//		double str = cha.getTotalStr();
//		double con = cha.getTotalCon();
//		double max_weight = ((str+con+1+cha.toOriginalStatConWeight()+cha.toOriginalStatStrWeight())/2) * 150;
//		max_weight += max_weight * cha.getItemWeight();
//		max_weight *= cha.getItemWeight();
//		return max_weight;
	}
	
	/**
	 * 객체와 같은 클레스 찾아서 리턴.
	 * @param c
	 * @return
	 */
	public ItemInstance find(Class<?> c){
		for( ItemInstance item : list ){
			if(item.getClass().equals(c))
				return item;
		}
		return null;
	}
	
	/**
	 * 겹쳐지는 아이템 찾기.
	 *  : 중복코드 방지용.
	 * @param ii
	 * @return
	 */
	public ItemInstance find(ItemInstance ii){
		if(ii instanceof SlimeRaceTicket){
			SlimeRaceTicket ticket = (SlimeRaceTicket)ii;
			return findSlimeRaceTicket(ticket.getSlimeRaceUid(), ticket.getSlimeRacerIdx());
		}
		return find(ii.getItem().getName(), ii.getBress(), true);
	}
	
	/**
	 * 해당 객체와 같은 아이템 찾아서 리턴.
	 * @param i
	 * @return
	 */
	public ItemInstance find(Item i){
		for( ItemInstance item : list ){
			if(item.getItem() == i)
				return item;
		}
		return null;
	}
	
	/**
	 * 디비에 이름을 가진 아이템이 존재하는지 확인하여 리턴하는 함수.
	 * @param name
	 * @return
	 */
	public ItemInstance find(String name, boolean piles){
		for( ItemInstance item : list ){
			if(item.getItem().getName().equalsIgnoreCase(name)){
				if(piles){
					if(item.getItem().isPiles())
						return item;
				}else{
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 겹쳐지는 아이템이 존재하는지 체크
	 */
	public ItemInstance find(String name, int bress, boolean piles){
		for( ItemInstance item : list ){
			if(item.getItem().getName().equalsIgnoreCase(name) && item.getBress()==bress){
				if(piles){
					if(item.getItem().isPiles())
						return item;
				}else{
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 아이템이 존재하는지 체크
	 */
	public ItemInstance find(String name, int enLev, int bress){
		for( ItemInstance item : list ){
			if(item.getItem().getName().equalsIgnoreCase(name) && item.getBress()==bress && item.getEnLevel()==enLev)
				return item;
		}
		return null;
	}

	/**
	 * 인벤토리 목록에서 화살을 찾은후 리턴하는 메서드.
	 */
	public ItemInstance findArrow(){
		for( ItemInstance item : list ){
			if(item instanceof Arrow)
				return item;
		}
		return null;
	}
	
	/**
	 * 인벤토리 목록에서 스팅을 찾아 리턴하는 함수.
	 * @return
	 */
	public ItemInstance findThrowingKnife(){
		for( ItemInstance item : list ){
			if(item instanceof ThrowingKnife)
				return item;
		}
		return null;
	}

	/**
	 * 아데나를 찾아서 리턴하는 메서드.
	 */
	public ItemInstance findAden(){
		for( ItemInstance item : list ){
			if(item.getItem().getNameIdNumber()==4)
				return item;
		}
		return null;
	}
	
	/**
	 * 네임아이디넘버로 일치하는 아이템 찾기.
	 * @param name_id
	 * @return
	 */
	public ItemInstance findDbNameId(int name_id){
		for( ItemInstance item : list ){
			if(item.getItem().getNameIdNumber()==name_id)
				return item;
		}
		return null;
	}
	
	public void findDbNameId(int name_id, List<ItemInstance> list){
		for( ItemInstance item : this.list ){
			if(item.getItem().getNameIdNumber()==name_id)
				list.add(item);
		}
	}
	
	/**
	 * 이름이 동일한 아이템 찾아서 list에 넣음.
	 * @param name
	 * @param list
	 */
	public void findDbName(String name, List<ItemInstance> list){
		for( ItemInstance item : this.list ){
			if(item.getItem().getName().equalsIgnoreCase(name))
				list.add(item);
		}
	}
	
	/**
	 * 밝기 값이 존재하는 아이템들 찾아서 리턴.
	 * @param list
	 */
	public void findLighter(List<ItemInstance> list){
		for( ItemInstance item : this.list ){
			if(item.getLight() > 0)
				list.add(item);
		}
	}
	
	/**
	 * 슬라임 레이스티켓 같은거 찾아서 리턴.
	 * @param item
	 * @return
	 */
	public SlimeRaceTicket findSlimeRaceTicket(int uid, int idx){
		for( ItemInstance ii : list ){
			if(ii instanceof SlimeRaceTicket){
				SlimeRaceTicket ticket = (SlimeRaceTicket)ii;
				if(ticket.getSlimeRaceUid()==uid && ticket.getSlimeRacerIdx()==idx)
					return ticket;
			}
		}
		return null;
	}
	
	/**
	 * 슬라임 레이스티켓 같은거 찾아서 리턴.
	 * @param item
	 * @return
	 */
	public void findSlimeRaceTicket(int uid, int idx, List<ItemInstance> r_list){
		for( ItemInstance ii : list ){
			if(ii instanceof SlimeRaceTicket){
				SlimeRaceTicket ticket = (SlimeRaceTicket)ii;
				if(ticket.getSlimeRaceUid()==uid && ticket.getSlimeRacerIdx()==idx)
					r_list.add(ticket);
			}
		}
	}
	
	/**
	 * 인벤토리에서 미스릴의 수량을 확인하는 메서드.
	 * @param count
	 * @param remove
	 * @return
	 */
	public boolean isMeterial(long count, boolean remove){
		if(count<=0)
			return true;

		ItemInstance meterial = findDbNameId(767);
		if(meterial != null){
			if(meterial.getCount() >= count){
				if(remove)
					count(meterial, meterial.getCount()-count, true);
				return true;
			}
		}

		return false;
	}

	/**
	 * 인벤토리에 아데나의 수량을 확인하는 메서드.
	 */
	public boolean isAden(long count, boolean remove){
		return isAden("아데나", count, remove);
	}
	
	/**
	 * 이름과 일치하는 아이템을 찾은후 그아이템에 갯수가 확인하려는 갯수만큼 존재하는지 확인하는 함수.
	 *  : 변수값에 따라 처리함.
	 * @param name
	 * @param count
	 * @param remove
	 * @return
	 */
	public boolean isAden(String name, long count, boolean remove){
		// 버그방지.
		if(name==null)
			return false;
		
		if(count<=0)
			return true;

		ItemInstance aden = find(name, true);
		if(aden != null){
			if(aden.getCount() >= count){
				if(remove)
					count(aden, aden.getCount()-count, true);
				return true;
			}
		}

		return false;
	}

	/**
	* 변신 조종반지 착용여부 리턴
	*/
	public boolean isRingOfPolymorphControl(){
		ItemInstance r1 = getSlot(Lineage.SLOT_RING_LEFT);
		ItemInstance r2 = getSlot(Lineage.SLOT_RING_RIGHT);
		if( (r1!=null && r1.getItem().getNameIdNumber()==261) ||
			(r2!=null && r2.getItem().getNameIdNumber()==261) ){
			return true;
		}
		return false;
	}

	/**
	* 순간이동 조종반지 착용여부 리턴
	*/
	public boolean isRingOfTeleportControl(){
		ItemInstance r1 = getSlot(Lineage.SLOT_RING_LEFT);
		ItemInstance r2 = getSlot(Lineage.SLOT_RING_RIGHT);
		if( (r1!=null && r1.getItem().getNameIdNumber()==241) ||
			(r2!=null && r2.getItem().getNameIdNumber()==241) ){
			return true;
		}
		return false;
	}
	
	/**
	 * 수중에서 숨쉴수 있는 아이템 착용중인지 확인해주는 함수.
	 * @return
	 */
	public boolean isAquaEquipped(){
		for(int slot=Lineage.SLOT_HELM ; slot<Lineage.SLOT_NONE ; ++slot){
			ItemInstance ii = getSlot(slot);
			if(ii!=null && ii.getItem().isAqua())
				return true;
		}
		return false;
	}
	
	/**
	 * 착용중인 아이템들중 셋트아이템옵션에서 헤이스트가 적용된게 있는지 확인해주는 함수.
	 * @return
	 */
	public boolean isSetOptionHaste(){
		for( ItemSetoption is : setitem_list ){
			if(is.isHaste())
				return true;
		}
		return false;
	}
	
	/**
	 * 착용중인 아이템들중 셋트아이템옵션에서 2단가속(용기)이 적용된게 있는지 확인해주는 함수.
	 * @return
	 */
	public boolean isSetOptionBrave(){
		for( ItemSetoption is : setitem_list ){
			if(is.isBrave())
				return true;
		}
		return false;
	}
	
	/**
	 * 착용중인 아이템들중 셋트아이템옵션에서 2단가속(와퍼)이 적용된게 있는지 확인해주는 함수.
	 * @return
	 */
	public boolean isSetOptionWafer(){
		for( ItemSetoption is : setitem_list ){
			if(is.isWafer())
				return true;
		}
		return false;
	}
}
