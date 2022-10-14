package lineage.bean.database;

import java.util.ArrayList;
import java.util.List;

public class Boss {
	private String name;
	int x;
	int y;
	private List<Integer> map = new ArrayList<Integer>();
	int[][] time;
	Monster mon;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public List<Integer> getMap() {
		return map;
	}
	public Monster getMon() {
		return mon;
	}
	public void setMon(Monster mon) {
		this.mon = mon;
	}
	public int[][] getTime(){
		return time;
	}
	public void setTime(int[][] time){
		this.time = time;
	}
	
	/**
	 * 스폰해야할 시간인지 확인해주는 함수.
	 * @param h
	 * @param m
	 * @return
	 */
	public boolean isSpawnTime(int h, int m){
		for(int[] t : time){
			if(t[0]==h && t[1]==m)
				return true;
		}
		return false;
	}
}
