package lineage.world.object.item;

import lineage.world.object.instance.ItemInstance;

public class SlimeRaceTicket extends ItemInstance {

	private int SlimeRaceUid;			// 레이스가 진행된던 uid 값.
	private int SlimeRacerIdx;			// 레이서에 고유 idx 값.
	private String SlimeRacerName;		// 레이서의 이름.
	private String db;					// 디비에 기록될때 사용되는 값.
	
	static public ItemInstance clone(ItemInstance item){
		if(item == null)
			item = new SlimeRaceTicket();
		return item;
	}

	@Override
	public void close(){
		super.close();
		SlimeRaceUid = SlimeRacerIdx = 0;
		SlimeRacerName = db = null;
	}

	public int getSlimeRaceUid() {
		return SlimeRaceUid;
	}

	public void setSlimeRaceUid(int slimeRaceUid) {
		SlimeRaceUid = slimeRaceUid;
	}

	public int getSlimeRacerIdx() {
		return SlimeRacerIdx;
	}

	public void setSlimeRacerIdx(int slimeRacerIdx) {
		SlimeRacerIdx = slimeRacerIdx;
	}

	public String getSlimeRacerName() {
		return SlimeRacerName;
	}

	public void setSlimeRacerName(String slimeRacerName) {
		SlimeRacerName = slimeRacerName;
	}
	
	@Override
	public String getName() {
		// uid-idx name
		return String.format("%d-%d %s", SlimeRaceUid, SlimeRacerIdx, SlimeRacerName);
	}
	
	@Override
	public String getSlimeRaceTicket(){
		if(db == null)
			db = getName();
		return db;
	}
	
	@Override
	public void setSlimeRaceTicket(String ticket){
		db = ticket;
		
		int pos = db.indexOf("-");
		int pos2 = db.indexOf(" ");
		SlimeRaceUid = Integer.valueOf(db.substring(0, pos));
		SlimeRacerIdx = Integer.valueOf(db.substring(pos+1, pos2));
		SlimeRacerName = db.substring(pos2);
	}
	
}
