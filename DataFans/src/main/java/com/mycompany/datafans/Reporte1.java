/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.datafans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jacky09
 */
public class Reporte1 extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reporte1.class.getName());
    
    // Modelo de tabla global para inicializarla vacía pero con estructura desde el inicio
    DefaultTableModel modelo = new DefaultTableModel();

    /**
     * Creates new form Reporte1
     */
    public Reporte1() {
        initComponents();
        
        // 1. Inicializar estructura visual de las columnas de la tabla
        inicializarEstructuraTabla();
        
        // 2. Poblar los JComboBox usando la estructura de ComboItem con ID de tipo long
        configurarFiltrosIniciales();
        
        // 3. Mostrar el enunciado de la consulta de forma estática en el JTextArea
        jTextArea1.setText("\n"
                + "\n Obtiene la cantidad total de fans que poseen una  \n"
                + "membresía, eligiendo el estado y su duración. \n"
                );
        jTextArea1.setEditable(false);
    }

    private void inicializarEstructuraTabla() {
        modelo.addColumn("Duración");
    modelo.addColumn("Estado");
    modelo.addColumn("Cantidad de Fans");
    tbR1.setModel(modelo);
    }
    
    private void configurarFiltrosIniciales() {
        cmbEdo.removeAllItems();
    // Agregamos opción "Todos" con ID 0
    cmbEdo.addItem(new ComboItem(0L, "Todos"));
    cmbEdo.addItem(new ComboItem(1L, "Activa"));
    cmbEdo.addItem(new ComboItem(2L, "Pago Pendiente"));

    cmbTipo.removeAllItems();
    // Agregamos opción "Todos" con ID 0
    cmbTipo.addItem(new ComboItem(0L, "Todos"));
    cmbTipo.addItem(new ComboItem(1L, "Mensual"));
    cmbTipo.addItem(new ComboItem(2L, "Anual"));
    }

    /**
     * Este es el método que se ejecutará al presionar el botón Ejecutar
     */
    private void ejecutarConsultaReporte() {
       Conexion objetoConexion = new Conexion();
    
    modelo.setRowCount(0);
    
    Object itemEstadoSelected = cmbEdo.getSelectedItem();
    Object itemTipoSelected = cmbTipo.getSelectedItem();
    
    if (itemEstadoSelected == null || itemTipoSelected == null) {
        JOptionPane.showMessageDialog(this, "Por favor seleccione filtros válidos.");
        return;
    }
    
    ComboItem itemEstado = (ComboItem) itemEstadoSelected;
    ComboItem itemTipo = (ComboItem) itemTipoSelected;
    
    String filtroEstado = itemEstado.toString();
    String filtroDuracion = itemTipo.toString();
    
    // Construimos la consulta base
    String sql = "SELECT m.Duracion, m.Estado, COUNT(DISTINCT f.ID_fan) AS TotalFans "
               + "FROM Usuario.Fan f "
               + "INNER JOIN Adquisicion.Suscripcion s ON f.ID_fan = s.ID_fan "
               + "INNER JOIN Adquisicion.Membresia m ON s.ID_suscripcion = m.ID_suscripcion "
               + "WHERE 1=1 "; // Condición siempre verdadera para facilitar la concatenación
    
    // Agregamos filtros solo si no es "Todos"
    if (!filtroEstado.equals("Todos")) {
        sql += "AND m.Estado = ? ";
    }
    if (!filtroDuracion.equals("Todos")) {
        sql += "AND m.Duracion = ? ";
    }
    
    sql += "GROUP BY m.Duracion, m.Estado ORDER BY m.Duracion, m.Estado";
    
    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        int paramIndex = 1;
        
        // Establecemos parámetros según cuales filtros estén activos
        if (!filtroEstado.equals("Todos")) {
            pst.setString(paramIndex++, filtroEstado);
        }
        if (!filtroDuracion.equals("Todos")) {
            pst.setString(paramIndex++, filtroDuracion);
        }
        
        try (ResultSet rs = pst.executeQuery()) {
            boolean tieneRegistros = false;
            
            while (rs.next()) {
                tieneRegistros = true;
                Object[] fila = new Object[3];
                fila[0] = rs.getString("Duracion");
                fila[1] = rs.getString("Estado");
                fila[2] = rs.getInt("TotalFans");
                modelo.addRow(fila);
            }
            
            if (!tieneRegistros) {
                JOptionPane.showMessageDialog(this, "No se encontraron registros con los filtros seleccionados.");
            }
        }
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al ejecutar el reporte: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }
                                        

    // ... Aquí NetBeans mantendrá de forma automática la declaración de tus componentes ...

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
        jLabel3 = new javax.swing.JLabel();
        cmbEdo = new javax.swing.JComboBox<>();
        cmbTipo = new javax.swing.JComboBox<>();
        btnEjecutar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbR1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reporte 1");

        jPanelArt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelArt.setPreferredSize(new java.awt.Dimension(330, 0));

        jLabel2.setText("Estado");

        jLabel3.setText("Duración");

        cmbEdo.addActionListener(this::cmbEdoActionPerformed);

        btnEjecutar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnEjecutar.setText("Ejecutar");
        btnEjecutar.addActionListener(this::btnEjecutarActionPerformed);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Consultar la cantidad total de fans que con una \nmembresía, permitiendo ver los resultados por el\nestado y agrupándolos según la duración de la \nmembresía.");
        jTextArea1.setToolTipText("");
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanelArtLayout = new javax.swing.GroupLayout(jPanelArt);
        jPanelArt.setLayout(jPanelArtLayout);
        jPanelArtLayout.setHorizontalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2))
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbEdo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEdo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(132, 132, 132))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Reporte 1", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

        tbR1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbR1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbR1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbR1);

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
                    .addComponent(jPanelArt, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        // TODO add your handling code here:
        
        ejecutarConsultaReporte();
        
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void tbR1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbR1MouseClicked
        
    }//GEN-LAST:event_tbR1MouseClicked

    private void cmbEdoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEdoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbEdoActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Reporte1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JComboBox<ComboItem> cmbEdo;
    private javax.swing.JComboBox<ComboItem> cmbTipo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTable tbR1;
    // End of variables declaration//GEN-END:variables
}

