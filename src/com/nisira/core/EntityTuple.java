package com.nisira.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTuple {
	Map<String, Object> datos;

	public EntityTuple() {
		datos = new HashMap<String, Object>();
	}

	public void addEntity(String alias, Object entidad) {
		datos.put(alias, entidad);
	}

	public void remove(String alias) {
		datos.remove(alias);
	}

	public Object get(String alias) {
		return datos.get(alias);
	}

	public static List<?> getListForAlias(List<EntityTuple> list, String alias) {
		List<Object> l = new ArrayList<Object>();

		for (EntityTuple e : list) {
			Object o = e.get(alias);
			l.add(o);
		}

		return l;
	}

}
