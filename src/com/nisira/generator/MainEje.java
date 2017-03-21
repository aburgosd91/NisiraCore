package com.nisira.generator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nisira.core.CoreUtil;
import com.nisira.core.EConexion;
import com.nisira.generator.ClaveForanea;
import com.nisira.generator.Columna;
import com.nisira.generator.ConexionGen;
import com.nisira.generator.Tabla;
import com.nisira.utils.nisiracore.Constantes;

public class MainEje {

	public static List<String[]> listaErrores = null;
	public static EConexion eConexion;
	public static String evento = "UPDATE";//LOAD:CARGAR DESDE LA PRINCIPAL, UPDATE: ACTUALIZAR EN LA BASE
	public static String TipoBD = Constantes.MSSQL;//TIPO DE BASE
	
	static {
		listaErrores = new ArrayList<String[]>();

		eConexion = new EConexion();
		eConexion.SERVIDOR = "localhost";
		eConexion.INSTANCIA = "";
		eConexion.BASE_DATOS = "GESTALM";
		eConexion.CLAVE = "amadeus2010";
		eConexion.USUARIO = "sa";
		eConexion.TIPO = TipoBD;
		
//		eConexion.SERVIDOR = "jcuzco";
//		eConexion.USUARIO = "sa";
		CoreUtil.conexiones.put("default", eConexion);
	}

	public static void main(String[] arg) throws IOException, SQLException {
		
		
		ConexionGen cGen = new ConexionGen(TipoBD);

		boolean cnnExito = true;

		if (cnnExito) {
			List<Tabla> lista = cGen.retornaEstructura(1);

			if (evento.equalsIgnoreCase("LOAD")) {
				System.out.println("Actualizando el archivo file.nsrsync");
				ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream("file.nsrsync"));
				salida.writeObject(lista);
				salida.close();
				
			} else {
				ObjectInputStream ingreso = new ObjectInputStream(new FileInputStream("file.nsrsync"));

				try {
					@SuppressWarnings("unchecked")
					List<Tabla> lstUpd = (List<Tabla>) ingreso.readObject();

					System.out.println("Evaluando Lista Circular");
					List<Tabla> listaCircular = getClavesCirculares(lstUpd);

					System.out.println("Fin Evaluando Lista Circular");
					List<Tabla> lstOrden = new ArrayList<Tabla>();

					boolean continuar = lstUpd.size() > 0;
					while (continuar) {
						for (int i = 0; i < lstUpd.size(); i++) {
							Tabla t = lstUpd.get(i);
							if (!existeReferencia(t, lstUpd, listaCircular)) {
								lstOrden.add(t);
								lstUpd.remove(t);
								System.out.print("Colocando en Fila de Actualización: " + t.getNombre());
								break;
							}
						}
						System.out.println(" - Quedan " + lstUpd.size() + " en Cola");
						continuar = lstUpd.size() > 0;
					}

					System.out.println("\n\n\nIniciando Actualización\n\n");

					// Con lista ordenada realizar ejecución de
					// Scripts

					for (Tabla t : lstOrden) {

						System.out.println("Actualizando Tabla: " + t.getNombre());

						// Buscar T en BD actual
						Tabla tblAct = null;

						for (Tabla o : lista) {
							if (t.getNombre().equalsIgnoreCase(o.getNombre())) {
								tblAct = o;
								break;
							}
						}

						if (tblAct != null) { // Existe
							// Buscar si existe campos
							for (Columna c : t.getColumnas()) {
								Columna oc = null;
								for (Columna o : tblAct.getColumnas()) {
									if (c.getNombre().equalsIgnoreCase(o.getNombre())) {
										oc = o;
									}
								}

								if (oc != null) {
									if (!c.igualEstructura(oc)) {
										cGen.ejecutar(t.queryModificaColumna(TipoBD, c),1);
									}
								} else { // Crear Columna
									cGen.ejecutar(t.queryCreaColumna(TipoBD, c),1);
								}
							}

							// Si Existe clave

							if (tblAct.getClavePrimaria() != null) {
								if (tblAct.getClavePrimaria().getNombre().isEmpty()) {
									cGen.ejecutar(t.queryCreaClaveForaneas(),1);
								}
							} else {
								cGen.ejecutar(t.queryCreaClaveForaneas(),1);
							}

							// Buscar si existe Claves Foraneas

							for (ClaveForanea fk : t.getClavesForaneas()) {
								ClaveForanea rfk = null;

								for (ClaveForanea ofk : tblAct.getClavesForaneas()) {
									if (fk.igualEstructura(ofk)) {
										rfk = ofk;
									}
								}
								if (rfk == null) {
									cGen.ejecutar(t.queryCreaClaveForanea(fk),1);
								}
							}

						} else { // Crear Tabla
							cGen.ejecutar(t.queryCrear(TipoBD),1);
							cGen.ejecutar(t.queryCreaClavePrimaria(),1);
							cGen.ejecutar(t.queryCreaClaveForaneas(),1);

						}
					}

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ingreso.close();
			}

			String ruta = "errores.txt";
			File archivo = new File(ruta);
			BufferedWriter bw;
			if (archivo.exists()) {
				archivo.delete();
			}

			bw = new BufferedWriter(new FileWriter(archivo));
			for (String[] d : listaErrores) {
				bw.write(d[0] + "\n");
				bw.write("--" + d[1] + "\n");
				bw.write("-----------------------");
			}
			bw.close();

			System.out.print("Presione una tecla para salir");
		} else {
			System.out.print("Presione una tecla para salir");
		}

	}

	// Si tiene claves foreaneas circulares

	public static List<Tabla> getClavesCirculares(List<Tabla> list) {
		List<Tabla> l = new ArrayList<Tabla>();

		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.size(); j++) {

				if (i != j) {
					Tabla t1 = list.get(i), t2 = list.get(j);

					List<int[]> fks = new ArrayList<int[]>();

					boolean revisar = true;

					while (revisar) {
						int clave1 = -1, clave2 = -1;
						if (t1.getNombre().equalsIgnoreCase("CONSUMIDOR")
								&& t2.getNombre().equalsIgnoreCase("ACTIVO")) {

							@SuppressWarnings("unused")
							int rrr = 0;
						}

						if (t1.getNombre().equalsIgnoreCase("ACTIVO")
								&& t2.getNombre().equalsIgnoreCase("CONSUMIDOR")) {

							@SuppressWarnings("unused")
							int rrr = 0;
						}
						if (t1.getClavesForaneas() == null) {
							t1.setClavesForaneas(new ArrayList<ClaveForanea>());
						}
						if (t2.getClavesForaneas() == null) {
							t2.setClavesForaneas(new ArrayList<ClaveForanea>());
						}
						for (int k = 0; k < t1.getClavesForaneas().size(); k++) {
							ClaveForanea fk = t1.getClavesForaneas().get(k);
							if (fk.getTablaForanea().equalsIgnoreCase(t2.getNombre())) {
								// Buscar en Refs
								boolean existe = false;
								for (int[] d : fks) {
									if (d[0] == k) {
										existe = true;
									}
								}
								if (!existe) {
									clave1 = k;
									break;
								}
							}
						}

						for (int k = 0; k < t2.getClavesForaneas().size(); k++) {
							ClaveForanea fk = t2.getClavesForaneas().get(k);
							if (fk.getTablaForanea().equalsIgnoreCase(t1.getNombre())) {
								// Buscar en Refs
								boolean existe = false;
								for (int[] d : fks) {
									if (d[1] == k) {
										existe = true;
									}
								}
								if (!existe) {
									clave2 = k;
									break;
								}
							}
						}

						if (clave1 > -1 && clave2 > -1) {

							fks.add(new int[] { clave1, clave2 });

							Tabla r1 = new Tabla();
							r1.setNombre(t1.getNombre());

							r1.setClavesForaneas(new ArrayList<ClaveForanea>());
							r1.getClavesForaneas().add(t1.getClavesForaneas().get(clave1));

							Tabla r2 = new Tabla();
							r2.setNombre(t2.getNombre());

							r2.setClavesForaneas(new ArrayList<ClaveForanea>());
							r2.getClavesForaneas().add(t2.getClavesForaneas().get(clave2));
							l.add(r1);
							l.add(r2);
						} else {
							revisar = false;
						}
					}
				}
			}
		}

		return l;
	}

	// Tiene Clave Foranea en Lista?
	public static boolean existeReferencia(Tabla t, List<Tabla> list) {

		for (ClaveForanea fk : t.getClavesForaneas()) {

			for (Tabla r : list) {
				if (!r.getNombre().equalsIgnoreCase(t.getNombre())
						&& r.getNombre().equalsIgnoreCase(fk.getTablaForanea())) {
					return true;
				}
			}
		}

		return false;
	}

	// Tiene Clave Foranea en Lista?
	public static boolean existeReferencia(Tabla t, List<Tabla> list, List<Tabla> lCircular) {

		for (ClaveForanea fk : t.getClavesForaneas()) {

			for (Tabla r : list) {
				if (!r.getNombre().equalsIgnoreCase(t.getNombre())
						&& r.getNombre().equalsIgnoreCase(fk.getTablaForanea())) {

					for (Tabla tr : lCircular) {

						if (tr.getNombre().equalsIgnoreCase(t.getNombre())) {
							for (ClaveForanea rfk : tr.getClavesForaneas()) {
								if (fk.getNombre().equalsIgnoreCase(rfk.getNombre())) {
									return false;
								}
							}
						}
					}

					return true;
				}
			}
		}

		return false;
	}

	public static boolean esReferenciada(Tabla t, List<Tabla> list) {

		for (Tabla r : list) {
			if (!r.getNombre().equalsIgnoreCase(t.getNombre())) {
				for (ClaveForanea fk : r.getClavesForaneas()) {
					if (fk.getTablaForanea().equalsIgnoreCase(t.getNombre())) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
