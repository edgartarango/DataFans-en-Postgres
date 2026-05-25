/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.datafans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jacky09
 */
public class Reporte2 extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reporte2.class.getName());
    DefaultTableModel modeloTabla = new DefaultTableModel();
    /**
     * Creates new form Reporte2
     */
    public Reporte2() {
        initComponents();
        // 1. Inicializar la estructura de la tabla de forma inmediata (se mostrará vacía al inicio)
        inicializarEstructuraTabla();
        
        // 2. Cargar los artistas en el ComboBox desde la Base de Datos
        cargarComboArtistas();
        
        // 3. Configurar el enunciado descriptivo del reporte
        jTextArea2.setText("\n"
                + "Obtiene la lista de Fans registrados que esten vinculados "
                + "\n mediante una suscripción a un Artista \n seleccionado. "
                );
        jTextArea2.setEditable(false);
    }

    private void inicializarEstructuraTabla() {
        modeloTabla.addColumn("ID Fan");
        modeloTabla.addColumn("Nombre del Fan");
        modeloTabla.addColumn("Teléfono");
        modeloTabla.addColumn("Email");
        tbR2.setModel(modeloTabla);
    }
    
    private void cargarComboArtistas() {
        Conexion objetoConexion = new Conexion();
    String sql = "SELECT ID_artista, Nombre_artista FROM Usuario.Artista ORDER BY Nombre_artista ASC";
    
    cmbArtista.removeAllItems();
    
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
        
        int contador = 0;
        while (rs.next()) {
            // Asegúrate de que el ID no sea nulo
            long id = rs.getLong("ID_artista");
            String nombre = rs.getString("Nombre_artista");
            
            // Depuración: Imprimir cada artista cargado
            System.out.println("Cargando artista: ID=" + id + ", Nombre=" + nombre);
            
            // Verificar que el ID sea válido (mayor que 0)
            if (id > 0) {
                cmbArtista.addItem(new ComboItem(id, nombre));
                contador++;
            } else {
                System.out.println("ADVERTENCIA: Artista con ID inválido: " + id);
            }
        }
        
        System.out.println(">> [DEBUG] Artistas cargados en el combo: " + contador);
        
        // Si no hay artistas, mostrar mensaje
        if (contador == 0) {
            JOptionPane.showMessageDialog(this, "No se encontraron artistas en la base de datos.");
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al cargar los artistas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }
    
    private void ejecutarReporteSubconsulta() {
    modeloTabla.setRowCount(0);
    
    Conexion objetoConexion = new Conexion();
    Object itemSeleccionado = cmbArtista.getSelectedItem();
    
    if (itemSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Por favor, seleccione un artista válido.");
        return;
    }
    
    ComboItem artista = (ComboItem) itemSeleccionado;
    long idArtista = artista.getId();
    
    // IMPORTANTE: Usa los mismos nombres de columnas que en tu BD
    // Pueden ser: ID_fan, NombreFan, Telefono, Email (con mayúsculas)
    String sql = "SELECT f.ID_fan, f.NombreFan, f.Telefono, f.Email "
               + "FROM Usuario.Fan f "
               + "WHERE f.ID_fan IN ( "
               + "    SELECT s.ID_fan "
               + "    FROM Adquisicion.Suscripcion s "
               + "    WHERE s.ID_artista = ? "
               + ") "
               + "ORDER BY f.NombreFan";
    
    System.out.println("Consultando artista ID: " + idArtista);
    System.out.println("SQL: " + sql);
    
    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setLong(1, idArtista);
        
        try (ResultSet rs = pst.executeQuery()) {
            boolean registrosEncontrados = false;
            int filasAgregadas = 0;
            
            while (rs.next()) {
                registrosEncontrados = true;
                Object[] fila = new Object[4];
                // Usa los mismos nombres que en el SELECT
                fila[0] = rs.getLong("ID_fan");
                fila[1] = rs.getString("NombreFan");
                fila[2] = rs.getString("Telefono");
                fila[3] = rs.getString("Email");
                
                modeloTabla.addRow(fila);
                filasAgregadas++;
            }
            
            System.out.println("Filas agregadas a la tabla: " + filasAgregadas);
            
            if (!registrosEncontrados) {
                JOptionPane.showMessageDialog(this, 
                    "El artista '" + artista + "' no cuenta con fans asociados actualmente.");
            }
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error al ejecutar el reporte: " + e.getMessage(), 
            "Error SQL", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }
   
    private void depurarConexion() {
    Conexion objetoConexion = new Conexion();
    Object itemSeleccionado = cmbArtista.getSelectedItem();
    
    if (itemSeleccionado == null) {
        System.out.println("No hay artista seleccionado");
        return;
    }
    
    ComboItem artista = (ComboItem) itemSeleccionado;
    long idArtista = artista.getId();
    
    System.out.println("=== DEPURACIÓN ===");
    System.out.println("ID Artista seleccionado: " + idArtista);
    System.out.println("Nombre Artista: " + artista.toString());
    
    // 1. Verificar si el artista existe en la tabla Artista
    String sqlArtista = "SELECT ID_artista, Nombre_artista FROM Usuario.Artista WHERE ID_artista = ?";
    
    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement pst = conn.prepareStatement(sqlArtista)) {
        
        pst.setLong(1, idArtista);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                System.out.println("✓ Artista encontrado en BD: " + rs.getString("Nombre_artista"));
            } else {
                System.out.println("✗ Artista NO encontrado en BD");
            }
        }
    } catch (Exception e) {
        System.out.println("Error verificando artista: " + e.getMessage());
    }
    
    // 2. Verificar si hay suscripciones para ese artista
    String sqlSuscripciones = "SELECT COUNT(*) as total FROM Adquisicion.Suscripcion WHERE ID_artista = ?";
    
    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement pst = conn.prepareStatement(sqlSuscripciones)) {
        
        pst.setLong(1, idArtista);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("Suscripciones encontradas para el artista: " + total);
            }
        }
    } catch (Exception e) {
        System.out.println("Error verificando suscripciones: " + e.getMessage());
    }
    
    // 3. Verificar la consulta completa con los nombres de columnas exactos
    String sqlCompleto = "SELECT f.ID_fan, f.NombreFan, f.Telefono, f.Email "
                       + "FROM Usuario.Fan f "
                       + "WHERE f.ID_fan IN ( "
                       + "    SELECT s.ID_fan "
                       + "    FROM Adquisicion.Suscripcion s "
                       + "    WHERE s.ID_artista = ? "
                       + ")";
    
    System.out.println("Ejecutando consulta: " + sqlCompleto);
    
    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement pst = conn.prepareStatement(sqlCompleto)) {
        
        pst.setLong(1, idArtista);
        
        try (ResultSet rs = pst.executeQuery()) {
            int contador = 0;
            while (rs.next()) {
                contador++;
                System.out.println("Fan " + contador + ": ID=" + rs.getLong("ID_fan") + 
                                 ", Nombre=" + rs.getString("NombreFan"));
            }
            System.out.println("Total de fans encontrados: " + contador);
        }
    } catch (Exception e) {
        System.out.println("Error ejecutando consulta completa: " + e.getMessage());
        e.printStackTrace();
    }
    
    System.out.println("=== FIN DEPURACIÓN ===");
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelArt = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbArtista = new javax.swing.JComboBox<>();
        btnEjecutar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbR2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reporte 2");

        jPanelArt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelArt.setPreferredSize(new java.awt.Dimension(330, 0));

        jLabel2.setText("Artista");

        btnEjecutar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnEjecutar.setText("Ejecutar");
        btnEjecutar.addActionListener(this::btnEjecutarActionPerformed);

        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setTabSize(15);
        jTextArea2.setText("Obtiene la lista de Fans registrados que esten vinculados\nmediante una suscripción a un Artista \\n seleccionando.");
        jTextArea2.setToolTipText("");
        jScrollPane2.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanelArtLayout = new javax.swing.GroupLayout(jPanelArt);
        jPanelArt.setLayout(jPanelArtLayout);
        jPanelArtLayout.setHorizontalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(cmbArtista, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelArtLayout.createSequentialGroup()
                .addContainerGap(59, Short.MAX_VALUE)
                .addComponent(btnEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanelArtLayout.setVerticalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbArtista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addComponent(btnEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(132, 132, 132))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Reporte 2", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

        tbR2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbR2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbR2MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbR2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelArt, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelArt, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        // TODO add your handling code here:
      
        depurarConexion();  // Temporal para ver qué pasa
    ejecutarReporteSubconsulta();
        
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void tbR2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbR2MouseClicked
        
    }//GEN-LAST:event_tbR2MouseClicked
public class ComboItem {
    private long id;
    private String descripcion;
    
    public ComboItem(long id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
    
    public long getId() {
        return id;
    }
    
    public String getDescripcion() {
        return descripcion;
    }public String toString() {
        return descripcion;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ComboItem that = (ComboItem) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Reporte2().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JComboBox<ComboItem> cmbArtista;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTable tbR2;
    // End of variables declaration//GEN-END:variables
}
