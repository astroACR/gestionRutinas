package backend;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexion {
    static String bd = "sistema_ejercicios";
    static String url = "jdbc:mysql://localhost:3306/sistema_ejercicios";
    static String user = "root";
    static String password = "1234";
    static String driver = "com.mysql.cj.jdbc.Driver";
    
    Connection con;
    public Conexion() {
        
    }
    
    public Connection conectar() {
        try{
           Class.forName(driver);
           con=DriverManager.getConnection(url+bd,user,password);
           System.out.println("Conectado a "+bd);
        }catch(ClassNotFoundException | SQLException ex) {
           System.out.println("Conexion fallida a "+bd);
           System.out.println(ex);
        }
        return con;
    }
    
    public void desconectar() {
        try{
            con.close();
        } catch(SQLException ex){
            
        }
    }
}
