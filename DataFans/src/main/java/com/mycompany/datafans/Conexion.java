/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.datafans;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Conexion {

    Connection conectar = null; 

    String usuario ="postgres";
    String contrasenia = "postgres"; // Verifica que sea tu clave real
    String ip= "localhost";
    String bd="datafans"; // Cambiado a tu BD actual
    String puerto = "5432";

    String cadena ="jdbc:postgresql://"+ip+":"+puerto+"/"+bd;

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
}
