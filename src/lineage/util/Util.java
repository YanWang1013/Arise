package lineage.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import lineage.bean.lineage.Map;
import lineage.share.Lineage;
import lineage.world.World;
import lineage.world.object.object;

public final class Util {
	
	static private Date date = new Date(0);
	static private DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	static private DateFormat date_format_detail = new SimpleDateFormat("yyyy-MM-dd HH시mm분ss초");
	
	/**
	 * 시간에 해당하는 년 값 추출.
	 * @param time
	 * @return
	 */
	@SuppressWarnings("deprecation")
	static public int getYear(long time) {
		date.setTime(time);
		return date.getYear();
	}
	
	/**
	 * 시간에 해당하는 월 값 추출.
	 * @param time
	 * @return
	 */
	@SuppressWarnings("deprecation")
	static public int getMonth(long time) {
		date.setTime(time);
		return date.getMonth() + 1;
	}
	
	/**
	 * 시간에 해당하는 일 값 추출.
	 * @param time
	 * @return
	 */
	@SuppressWarnings("deprecation")
	static public int getDate(long time) {
		date.setTime(time);
		return date.getDate();
	}
	
	/**
	 * 시간에 해당하는 시 값 추출.
	 * @param time
	 * @return
	 */
	@SuppressWarnings("deprecation")
	static public int getHours(long time) {
		date.setTime(time);
		return date.getHours();
	}
	
	/**
	 * 시간에 해당하는 분 값 추출.
	 * @param time
	 * @return
	 */
	@SuppressWarnings("deprecation")
	static public int getMinutes(long time) {
		date.setTime(time);
		return date.getMinutes();
	}
	
	/**
	 * 시간에 해당하는 초 값 추출.
	 * @param time
	 * @return
	 */
	@SuppressWarnings("deprecation")
	static public int getSeconds(long time) {
		date.setTime(time);
		return date.getSeconds();
	}

	static public String getLocaleString(long time, boolean detail) {
		date.setTime(time);
		return detail ? date_format_detail.format(date) : date_format
				.format(date);
	}
	
	/**
	 * 랜덤값 추출용 함수.
	 * @param lbound
	 * @param ubound
	 * @return
	 */
	static public int random(int lbound, int ubound) {
		if(ubound < 0)
			return (int) ((Math.random() * (ubound - lbound - 1)) + lbound);
		else
			return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
	}

	static public long random(long lbound, long ubound) {
		return (long) ((Math.random() * (ubound - lbound + 1)) + lbound);
	}

	static public double random(double lbound, double ubound) {
		return (Math.random() * (ubound - lbound + 1)) + lbound;
	}
	
	/**
	 * 거리안에 있다면 참
	 */
	static public boolean isDistance(int x, int y, int m, int tx, int ty, int tm, int loc) {
		int distance = getDistance(x, y, tx, ty);
		if (loc < distance)
			return false;
		if (m != tm)
			return false;
		return true;
	}

	/**
	 * 거리안에 있다면 참
	 */
	static public boolean isDistance(object o, object oo, int loc){
		return isDistance(o.getX(), o.getY(), o.getMap(), oo.getX(), oo.getY(), oo.getMap(), loc);
	}
	
	/**
	 * 거리값 추출.
	 * @param o
	 * @param oo
	 * @return
	 */
	static public int getDistance(object o, object oo){
		return getDistance(o.getX(), o.getY(), oo.getX(), oo.getY());
	}
	
	/**
	 * 거리값 추출.
	 * @param o
	 * @param oo
	 * @return
	 */
	static public int getDistance(int x, int y, int tx, int ty){
		long dx = tx-x;
		long dy = ty-y;
		return (int)Math.sqrt(dx*dx + dy*dy);
	}

	/**
	 * 해당하는 좌표로 방향을 전환할때 사용.
	 */
	static public int calcheading(int myx, int myy, int tx, int ty){
		if(tx > myx && ty > myy){
			return 3;
		}else if(tx < myx && ty < myy){
			return 7;
		}else if(tx > myx && ty == myy){
			return 2;
		}else if(tx < myx && ty == myy){
			return 6;
		}else if(tx == myx && ty < myy){
			return 0;
		}else if(tx == myx && ty > myy){
			return 4;
		}else if(tx < myx && ty > myy){
			return 5;
		}else{
			return 1;
		}
	}

	static public int calcheading(object o, int x, int y){
		return calcheading(o.getX(), o.getY(), x, y);
	}

	/**
	 * 객체를 참고로 반대 방향 리턴.
	 */
	static public int oppositionHeading(object o, object oo){
		int myx = o.getX();
		int myy = o.getY();
		int tx = oo.getX();
		int ty = oo.getY();
		if(tx > myx && ty > myy){
			return 7;
		}else if(tx < myx && ty < myy){
			return 3;
		}else if(tx > myx && ty == myy){
			return 6;
		}else if(tx < myx && ty == myy){
			return 2;
		}else if(tx == myx && ty < myy){
			return 4;
		}else if(tx == myx && ty > myy){
			return 0;
		}else if(tx < myx && ty > myy){
			return 1;
		}else{
			return 5;
		}
	}
	
	/**
	 * 방향과 타입에따라 적절하게 좌표값세팅 리턴
	 * @param h		: 방향
	 * @param type	: true ? x : y
	 * @return
	 */
	static public int getXY(final int h, final boolean type){
		int loc = 0;
		switch(h){
			case 0:
				if(!type)
					loc -= 1;
				break;
			case 1:
				if(type)
					loc += 1;
				else
					loc -= 1;
				break;
			case 2:
				if(type)
					loc += 1;
				break;
			case 3:
				loc += 1;
				break;
			case 4:
				if(!type)
					loc += 1;
				break;
			case 5:
				if(type)
					loc -= 1;
				else
					loc += 1;
				break;
			case 6:
				if(type)
					loc -= 1;
				break;
			case 7:
				loc -= 1;
				break;
		}
		return loc;
	}

	/**
	 * 원하는 타켓에게 장거리 공격 및 근거리 공격이 가능한지 체크
	 */
	static public boolean isAreaAttack(object o, object target){
		int myx = o.getX();
		int myy = o.getY();
		int map = o.getMap();
		int tax = target.getX();
		int tay = target.getY();
		int count = Lineage.SEARCH_LOCATIONRANGE;
		int h = 0;
		while(((myx!=tax)||(myy!=tay))&&(--count>0)){
			h = calcheading(myx, myy, tax, tay);
			if(!World.isThroughAttack(myx, myy, map, h))
				return false;
			switch(h){
				case 0:
					--myy;
					break;
				case 1:
					++myx;
					--myy;
					break;
				case 2:
					++myx;
					break;
				case 3:
					++myx;
					++myy;
					break;
				case 4:
					++myy;
					break;
				case 5:
					--myx;
					++myy;
					break;
				case 6:
					--myx;
					break;
				default:
					--myx;
					--myy;
					break;
			}
		}

		return true;
	}

	/**
	 * 귀환 좌표값을 랜덤으로 생성해서 정의하는 함수.
	 * @param o
	 */
	static public void toRndLocation(object o){
		Map m = World.get_map(o.getMap());
		if(m != null){
			int max = 100;
			int x1 = m.locX1;
			int x2 = m.locX2;
			int y1 = m.locY1;
			int y2 = m.locY2;
			if(m.mapid == 4){
				// 4번맵은 12시방향에 공백이 있는데 그곳을 이동가능한 지역으로 판단하는 문제가 있어서 임의로 범위를 좁힘.
				// 공식이 이을텐데 못찾아서 임시로 이렇게 처리..
				x1 = 32520;
				y1 = 32200;
			}
			do{
				o.setHomeX( random(x1, x2) );
				o.setHomeY( random(y1, y2) );
				if(--max<0) {
					o.setHomeX( o.getX() );
					o.setHomeY( o.getY() );
					break;
				}
			}while(!World.isThroughObject(o.getHomeX(), o.getHomeY()+1, m.mapid, 0));
		}else{
			o.setHomeX( o.getX() );
			o.setHomeY( o.getY() );
		}
		o.setHomeMap( o.getMap() );
	}

	/**
	 * 네임아이디의 $를 제거해서 정수를 리턴하는 함수.
	 */
	static public int NameidToNumber(final String nameid){
		StringTokenizer st = new StringTokenizer(nameid, " $ ");
		StringBuffer sb = new StringBuffer();
		while(st.hasMoreTokens()){
			sb.append(st.nextToken());
		}
		
		return Integer.valueOf(sb.toString().trim());
	}
	
	/**
	 * 패킷 출력 함수
	 * @param data
	 * @param len
	 * @return
	 */
	static public String printData(byte[] data, int len){ 
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i=0;i< len;i++){
			if (counter % 16 == 0)
				result.append( String.format("%04x: ", i) );
			result.append( String.format("%02x ", data[i] & 0xff) );
			counter++;
			if (counter == 16){
				result.append("   ");
				int charpoint = i-15;
				for (int a=0; a<16;a++){
					int t1 = data[charpoint++];
					if (t1 > 0x1f && t1 < 0x80){
						result.append((char)t1);
					}else{
						result.append('.');
					}
				}
				result.append("\n");
				counter = 0;
			}
		}

		int rest = len % 16;
		if (rest > 0 ){
			for (int i=0; i<17-rest;i++ ){
				result.append("   ");
			}

			int charpoint = len-rest;
			for (int a=0; a<rest;a++){
				int t1 = data[charpoint++];
				if (t1 > 0x1f && t1 < 0x80){
					result.append((char)t1);
				}else{
					result.append('.');
				}
			}

			result.append("\n");
		}
		return result.toString();
	}

	static public byte[] StringToByte(String line) {
		byte[] b = new byte[line.length() / 2];
		for (int i = 0, j = 0; i < line.length(); i += 2, j++) {
			b[j] = (byte) Integer.parseInt(line.substring(i, i + 2), 16);
		}
		return b;
	}
	
	/**
	 * 시스템이 이용중의 heap 사이즈를 메가바이트 단위로 돌려준다.<br>
	 * 이 값에 스택의 사이즈는 포함되지 않는다.
	 * 
	 * @return 이용중의 heap 사이즈
	 */
	static public long getMemoryMB() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
	}
	
	/**
	 * 풀링에 추가해도되는지 확인해주는 함수. : 너무 많이 등록되면 문제가 되기대문에 적정선으로 카바.. :
	 * java.lang.OutOfMemoryError: Java heap space
	 * 
	 * @return
	 */
	static public boolean isPoolAppend(List<?> pool) {
		// 전체 갯수로 체크.
		return Lineage.pool_max == 0 || pool.size() < 100000;
	}
	
	/**
	 * md5 해쉬코드 얻기.
	 * @param str
	 * @return
	 */
	public static String toMD5(String str) {
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes()); 
			StringBuffer sb = new StringBuffer(); 
			for(byte data : md.digest())
				sb.append(Integer.toString((data&0xff) + 0x100, 16).substring(1));
			return sb.toString();

		}catch(Exception e){ }
		return null;
	}
	
	public static boolean isRange(int a, int b, int range) {
		int c = a - b;
		if(c < 0)
			c = ~c + 1;
		return c <= range;
	}

	public static boolean runIExplore(String parameter) {
		//
		Runtime runtime = Runtime.getRuntime();

		try {
			//
			StringBuffer sb = new StringBuffer();
			Process prc = runtime.exec( "reg query HKEY_CLASSES_ROOT\\Applications\\iexplore.exe\\shell\\open\\command" );
			int exitValue = prc.waitFor();
			if(exitValue == 0) {
				BufferedReader br = new BufferedReader( new InputStreamReader(prc.getInputStream()) );
				//
				String temp = null;
				while((temp=br.readLine()) != null)
					sb.append( temp );
				//
				int pos_a = sb.indexOf("\"") + 1;
				int pos_b = sb.indexOf("\"", pos_a);
				String path = sb.substring(pos_a, pos_b);
				//
				String[] command = new String[2];
				command[0] = path;
				command[1] = parameter;

				Runtime.getRuntime().exec(command);
				return true;
			}
		} catch (Exception e) { }

		//
		return false;
	}
	
}

