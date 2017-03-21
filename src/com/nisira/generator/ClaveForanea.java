package com.nisira.generator;

import java.io.Serializable;
import java.util.List;


public class ClaveForanea extends ObjetoSQL implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5548029034271723479L;
	private String tablaForanea;
	private List<String[]> campos;
	
	public String getTablaForanea() {
		return tablaForanea;
	}
	public void setTablaForanea(String tablaForanea) {
		this.tablaForanea = tablaForanea;
	}
	public List<String[]> getCampos() {
		return campos;
	}
	public void setCampos(List<String[]> campos) {
		this.campos = campos;
	}
	
	public boolean igualEstructura(ClaveForanea fk){
		if(getTablaForanea().equalsIgnoreCase(fk.getTablaForanea())) {
			if (campos.size() == fk.getCampos().size()) {
				
				for (int i = 0; i< campos.size(); i++) {
					String[] c = campos.get(i);
					String[] oc = fk.getCampos().get(i);
					
					if (!c[0].equalsIgnoreCase(oc[0]) || !c[1].equalsIgnoreCase(oc[1]) ) {
						return false;
					}
				}
				return true;
				
			}
		}
		return false;
	}
	
}
