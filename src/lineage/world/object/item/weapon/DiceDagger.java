package lineage.world.object.item.weapon;

import lineage.util.Util;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;

public class DiceDagger extends ItemWeaponInstance {
	
	private object temp;
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new DiceDagger();
		return item;
	}

	@Override
	public boolean toDamage(Character cha, object o){
		temp = o;
		return Util.random(0, 100)<10;
	}

	@Override
	public int toDamage(int dmg){
		if(temp!=null && !temp.isDead() && !temp.isWorldDelete()){
			// 데미지 추가.
			dmg = temp.getNowHp()/2;
			// 무기 소멸.
			if(cha!=null){
				if(isEquipped()){
					setEquipped(false);
					toSetoption(cha, true);
					toEquipped(cha, cha.getInventory());
					toOption(cha, true);
				}
				cha.getInventory().count(this, 0, true);
			}
		}
		return dmg;
	}

}
