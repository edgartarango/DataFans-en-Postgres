/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.datafans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class Conexion {

    Connection conectar = null; 

    String usuario ="postgres";
    String contrasenia = "postgres";
    String ip= "localhost";
    String bd="DataFans";
    String puerto = "5432";

    String cadena ="jdbc:postgresql://"+ip+":"+puerto+"/"+bd;

    public static String rolActual = "Admin";
    public Connection establecerConexion(){
        try {
            Class.forName("org.postgresql.Driver");
            conectar = DriverManager.getConnection(cadena, usuario, contrasenia);
            // Opcional: System.out.println("Conexión exitosa a DataFans");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error al conectar a la base de datos: "+ e.toString());
        }
        return conectar;
    }
    
    public boolean validarUsuarioPorCatalogo(String usuarioLogin, String contraLogin) {
        boolean esValido = false;
        
        // Consulta segura al catálogo de Postgres para verificar si el rol existe en el servidor
        String sql = "SELECT rolname FROM pg_roles WHERE rolname = ?;";
        
        // 1. Usamos tu conexión fija normal
        try (Connection conn = establecerConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            // Forzamos a minúsculas o tal cual está en la BD para comparar
            pst.setString(1, usuarioLogin);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // El usuario existe en PostgreSQL.
                    // Ahora validamos de forma segura la contraseña correspondiente al script que creaste.
                    // Como la conexión es fija, validamos las contraseñas exactas asignadas en tu script
                    String usuarioReal = rs.getString("rolname").toLowerCase();
                    
                    if (usuarioReal.equals("admin") && contraLogin.equals("1234")) {
                        esValido = true;
                    } else if (usuarioReal.equals("editor") && contraLogin.equals("1234")) {
                        esValido = true;
                    } else if (usuarioReal.equals("lector") && contraLogin.equals("1234")) {
                        esValido = true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al validar el usuario en el catálogo: " + e.getMessage());
        }
        
        return esValido;
    }
}
