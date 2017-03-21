/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nisira.core;

/**
 *
 * @author jpretel
 */
public class EConexion {
	public String TIPO;
    public String SERVIDOR;
    public String BASE_DATOS;
    public String INSTANCIA;
    public String USUARIO;
    public String CLAVE;
    public String URLSINCRO;
    public String TIPOSINCRO;/*[1] XML , [2] JSON*/
    public String IDEMPRESA;
    public String IDSUCURSAL;
    public String RUTAPROYECTO;
    public String RUTAINICIO;
    public String RUTAFIN;
    public String RUTAERRINICIO;
    public String RUTAERRFIN;
    public String IDMONTACARGA;
    public String PUERTOCOM;
    public String READERMAC;
    public String IDCPUMOVIL;
    @Override
    public String toString() {
    	return TIPO + " " + SERVIDOR + " " + BASE_DATOS + " " + INSTANCIA + " " + USUARIO + " " + CLAVE + " " + URLSINCRO + " " + TIPOSINCRO+" "+IDEMPRESA+" "+IDSUCURSAL+
    			" "+RUTAPROYECTO+" "+RUTAINICIO+" "+RUTAFIN+" "+
    			RUTAERRINICIO+" "+RUTAERRFIN+" "+IDMONTACARGA+" "+
    			PUERTOCOM+" "+READERMAC+" "+IDCPUMOVIL;
    }
}
