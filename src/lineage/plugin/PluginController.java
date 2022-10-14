package lineage.plugin;

import java.util.HashMap;
import java.util.Map;

public class PluginController {

	static private Plugin plugin;
	
	static public void setPlugin(Plugin plugin){
		PluginController.plugin = plugin;
	}

	static private Map<Class<?>, Plugin> list;
	
	static {
		list = new HashMap<Class<?>, Plugin>();
	}
	
	static public Object init(Class<?> c, Object ... opt){
		if(plugin != null)
			return plugin.init(c, opt);
		
		return null;
	}
	
	/**
	 * 플러그인 찾는 함수.
	 * @param key
	 * @return
	 */
	static public Plugin find(Class<?> key){
		return list.get(key);
	}
	
	/**
	 * 플러그인 등록 처리 함수.
	 * @param key
	 * @param value
	 */
	static public void append(Class<?> key, Plugin value){
		list.put(key, value);
	}
	
	static public void toTimer(long time){
		
	}
}
