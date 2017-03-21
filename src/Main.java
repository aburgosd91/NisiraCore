import java.sql.SQLException;

import com.nisira.core.CoreUtil;
import com.nisira.core.EConexion;
import com.nisira.utils.nisiracore.Constantes;
import com.nisira.utils.nisiracore.Utilitarios;

public class Main {

	public static void main(String[] args) throws SQLException {
		
		EConexion e = new EConexion();
		e.BASE_DATOS = "MASTEREDOC_MOVIL";
		e.CLAVE = "amadeus2010";
		e.INSTANCIA = "patos";
		e.USUARIO = "sa";
		e.SERVIDOR = "192.168.0.90";
		e.TIPO = Constantes.MSSQL;
		
		CoreUtil.conexiones.put("default", e);

		Utilitarios.getInstance(1);
		for (int i = 0; i < 5000; i++)
			System.out.println(Utilitarios.getID("MASTEREDOC_MOVIL"));

		// System.out.println("aa");
		//
		// Empresa t = new Empresa();
		// t.setId("3");
		// t.setDireccion("direccion2");
		// t.setRazonSocial("razon2");
		// t.setRUC("ruc2");
		// t.setRUTA_EXPORTAR("export2");
		// t.setRUTA_REPORTES("reporte2");
		//
		// EmpresaDao edao = new EmpresaDao();
		// edao.mezclar(t);

		// Empresa em = new Empresa();
		//
		// em.setId("0");
		// em.setRazonSocial("123213");
		// em.setDireccion("sadaASDASDsd");
		// em.setRUC("1231ASDASD23");
		//
		// EmpresaDao edao = new EmpresaDao();
		//
		// try {
		// edao.mezclar(em);
		// } catch (SQLException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// SysGrupoDao sysGrupoDAO = new SysGrupoDao();

		// try {
		// sysGrupoDAO.listar();
		// } catch (SQLException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		// new FrmGeneraEntidades(e.TIPO).setVisible(true);;
	}
}
