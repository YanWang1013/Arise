package lineage.bean.database;

import java.util.HashMap;
import java.util.Map;

public class SpriteFrame {

	private Map<Integer, Integer> list;			// mode넘버값에 해당하는 프레임
	private Map<String, Integer> list_string;	// mode문자값에 해당하는 프레임
	private Map<String, Integer> list_mode;		// mode문자값에 해당하는 mode넘버
	private String name;
	private int gfx;
	
	public SpriteFrame(){
		list = new HashMap<Integer, Integer>();
		list_string = new HashMap<String, Integer>();
		list_mode = new HashMap<String, Integer>();
	}

	public Map<Integer, Integer> getList() {
		return list;
	}

	public Map<String, Integer> getListString() {
		return list_string;
	}

	public Map<String, Integer> getListMode() {
		return list_mode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGfx() {
		return gfx;
	}

	public void setGfx(int gfx) {
		this.gfx = gfx;
	}
}
