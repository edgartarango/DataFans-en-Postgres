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
        jTextArea2.setText("Consulta 2:\n"
                + "Enunciado:\nConsultar la lista de Fans registrados que están vinculados "
                + "mediante una suscripción al Artista seleccionado en la interfaz, "
                + "restringiendo los resultados con una subconsulta interna.");
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
    // Ajustado a 'Nombre_artista' según tu script de BD
    String sql = "SELECT ID_artista, Nombre_artista FROM Usuario.Artista ORDER BY Nombre_artista ASC;";
    
    cmbArtista.removeAllItems();
    
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
        
        int contador = 0;
        while (rs.next()) {
            long id = rs.getLong("ID_artista");
            String nombre = rs.getString("Nombre_artista");
            
            // Agregamos usando tu constructor: ComboItem(long, String)
            cmbArtista.addItem(new ComboItem(id, nombre));
            contador++;
        }
        
        // Rastreo en consola para verificar si Java realmente está leyendo la tabla Artista
        System.out.println(">> [DEBUG] Artistas cargados en el combo: " + contador);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al cargar los artistas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    
    private void ejecutarReporteSubconsulta() {
    // Limpiamos las filas previas manteniendo los encabezados intactos
    modeloTabla.setRowCount(0);
    
    Conexion objetoConexion = new Conexion();
    Object itemSeleccionado = cmbArtista.getSelectedItem();
    
    if (itemSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Por favor, seleccione un artista válido.");
        return;
    }
    
    // Casteamos a tu clase ComboItem
    ComboItem artista = (ComboItem) itemSeleccionado;
    long idArtista = artista.getId(); 
    
    // Impresión de control para validar qué ID se está mandando al query
    System.out.println(">> [DEBUG] Buscando suscripciones para el id_artista: " + idArtista + " (" + artista + ")");
    
    // Query reestructurado con la conversión implícita a minúsculas de Postgres
    String sql = "SELECT f.id_fan, f.nombrefan, f.telefono, f.email "
               + "FROM usuario.fan f "
               + "WHERE f.id_fan IN ( "
               + "    SELECT s.id_fan "
               + "    FROM adquisicion.suscripcion s "
               + "    WHERE s.id_artista = ? "
               + ");";
    
    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        // En PostgreSQL, BIGSERIAL/BIGINT equivalen a un Long de Java de manera estricta
        pst.setLong(1, idArtista);
        
        try (ResultSet rs = pst.executeQuery()) {
            boolean registrosEncontrados = false;
            int filasAgregadas = 0;
            
            while (rs.next()) {
                registrosEncontrados = true;
                Object[] fila = new Object[4];
                // Extracción modificada a minúsculas para evitar colapsos del ResultSet
                fila[0] = rs.getLong("id_fan");
                fila[1] = rs.getString("nombrefan"); 
                fila[2] = rs.getString("telefono");  
                fila[3] = rs.getString("email");     
                
                modeloTabla.addRow(fila);
                filasAgregadas++;
            }
            
            System.out.println(">> [DEBUG] Filas encontradas y enviadas a la JTable: " + filasAgregadas);
            
            if (!registrosEncontrados) {
                JOptionPane.showMessageDialog(this, "El artista '" + artista + "' no cuenta con fans asociados actualmente.");
            }
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al ejecutar el reporte: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace(); // Esto pintará el error detallado en la consola si colapsa internamente
    }
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
        jTextArea2.setToolTipText("");
        jScrollPane2.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanelArtLayout = new javax.swing.GroupLayout(jPanelArt);
        jPanelArt.setLayout(jPanelArtLayout);
        jPanelArtLayout.setHorizontalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2))
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
        );
        jPanelArtLayout.setVerticalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
        ejecutarReporteSubconsulta();
        
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void tbR2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbR2MouseClicked
        
    }//GEN-LAST:event_tbR2MouseClicked

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
