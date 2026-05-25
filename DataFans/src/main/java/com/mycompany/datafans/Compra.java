/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.datafans;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jacky09
 */
public class Compra extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Compra.class.getName());
   private Conexion varConexion;
    private DefaultTableModel dtCompras;
    /**
     * Creates new form Compra
     */
    public Compra() {
        
        initComponents();
        varConexion = new Conexion();
        CargarFans();
        InicializarTabla();

        CargarComprasExistentes();
        aplicarPermisosPorRol();
    }

    private void aplicarPermisosPorRol() {
        String rol = Conexion.rolActual;
        
        if (rol.equals("Editor")) {
            // El editor puede dar de Alta y Modificar, pero NO puede dar de Baja (Delete)
            btnBaja.setEnabled(false);
            //btnDetVen.setEnabled(false);
        } else if (rol.equals("Lector")) {
            // El lector SOLO puede consultar. No puede dar de Alta, Baja ni Modificar
            btnAlta.setEnabled(false);
            btnBaja.setEnabled(false);
            btnDetVen.setEnabled(false);
        }
        // Si es "Admin", no entra a ninguna condición y conserva todos los botones activos.

        CargarComprasExistentes();
        //aplicarPermisosPorRol();

    }

   
    
    private void InicializarTabla() {
         dtCompras = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        dtCompras.addColumn("ID_compra");
        dtCompras.addColumn("Fan");
        dtCompras.addColumn("Fecha_compra");
        dtCompras.addColumn("Total");
        
        tbCompra.setModel(dtCompras);
        
        tbCompra.getColumnModel().getColumn(0).setPreferredWidth(80);
        tbCompra.getColumnModel().getColumn(1).setPreferredWidth(200);
        tbCompra.getColumnModel().getColumn(2).setPreferredWidth(150);
        tbCompra.getColumnModel().getColumn(3).setPreferredWidth(120);
        
        javax.swing.table.TableColumn colTotal = tbCompra.getColumnModel().getColumn(3);
        colTotal.setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.RIGHT);
                return c;
            }
        });
        
        btnDetVen.setEnabled(false);
        
        tbCompra.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String rol = Conexion.rolActual;
        
        if (rol.equals("Lector")) {
            // El editor puede dar de Alta y Modificar, pero NO puede dar de Baja (Delete)
            btnBaja.setEnabled(false);
            btnDetVen.setEnabled(false);}
            else
                btnDetVen.setEnabled(tbCompra.getSelectedRow() != -1);
            }
        });
    }
    
    private void CargarComprasExistentes() {
         String query = 
        "SELECT c.ID_compra, c.ID_fan, f.NombreFan, c.Fecha_compra, COALESCE(c.Total_compra, 0) AS Total " +
        "FROM Administrativo.Compra c " +
        "INNER JOIN Usuario.Fan f ON c.ID_fan = f.ID_fan " +
        "ORDER BY c.Fecha_compra DESC, c.ID_compra DESC";
    
    try (Connection conexion = varConexion.establecerConexion();  // ← Cambiar aquí también
         Statement stmt = conexion.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        dtCompras.setRowCount(0);
        
        while (rs.next()) {
            long idCompra = rs.getLong("ID_compra");
            int idFan = rs.getInt("ID_fan");
            String nombreFan = rs.getString("NombreFan");
            Timestamp fecha = rs.getTimestamp("Fecha_compra");
            double total = rs.getDouble("Total");
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String fechaStr = sdf.format(fecha);
            
            dtCompras.addRow(new Object[]{
                idCompra,
                idFan + " - " + nombreFan,
                fechaStr,
                total
            });
        }
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al cargar compras existentes: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        logger.log(Level.SEVERE, null, ex);
    }
    }
    
 private void CargarFans() {
     String query = "SELECT ID_fan, NombreFan, CAST(ID_fan AS VARCHAR) || ' - ' || NombreFan AS FanDisplay " +
                   "FROM Usuario.Fan ORDER BY NombreFan";
    
    try (Connection conexion = varConexion.establecerConexion();  // ← Cambiar aquí
         Statement stmt = conexion.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        while (rs.next()) {
            cmbFan.addItem(rs.getString("FanDisplay"));  // ← Cambiar a String directamente
        }
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al cargar fans: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        logger.log(Level.SEVERE, null, ex);
    }
    }
 
  private long CrearCompraVacia(int idFan) {
      String query = "INSERT INTO Administrativo.Compra (ID_fan, Fecha_compra, Total_compra) " +
                   "VALUES (?, CURRENT_TIMESTAMP, 0) RETURNING ID_compra";
    
    try (Connection conexion = varConexion.establecerConexion();  // ← Cambiar conectar() por establecerConexion()
         PreparedStatement pstmt = conexion.prepareStatement(query)) {
        
        pstmt.setInt(1, idFan);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return rs.getLong(1);
        }
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al crear la compra: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        logger.log(Level.SEVERE, null, ex);
    }
    return 0;
    }
  
  private String ExtraerNombreFan(String fanText) {
        String[] partes = fanText.split(" - ");
        if (partes.length > 1) {
            return partes[1];
        }
        return fanText;
    }
 private int ExtraerIdFan(String fanText) {
        String[] partes = fanText.split(" - ");
        if (partes.length > 0) {
            try {
                return Integer.parseInt(partes[0]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
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
        btnDetVen = new javax.swing.JButton();
        btnAlta = new javax.swing.JButton();
        btnBaja = new javax.swing.JButton();
        cmbFan = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbCompra = new javax.swing.JTable();

        setTitle("Compra");

        jPanelArt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelArt.setPreferredSize(new java.awt.Dimension(330, 0));

        jLabel2.setText("Fan");

        btnDetVen.setText("Detalle Venta");
        btnDetVen.addActionListener(this::btnDetVenActionPerformed);

        btnAlta.setText("Alta");
        btnAlta.addActionListener(this::btnAltaActionPerformed);

        btnBaja.setText("Baja");
        btnBaja.addActionListener(this::btnBajaActionPerformed);

        javax.swing.GroupLayout jPanelArtLayout = new javax.swing.GroupLayout(jPanelArt);
        jPanelArt.setLayout(jPanelArtLayout);
        jPanelArtLayout.setHorizontalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbFan, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelArtLayout.createSequentialGroup()
                        .addComponent(btnAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(btnBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(btnDetVen)))
                .addContainerGap())
        );
        jPanelArtLayout.setVerticalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbFan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAlta)
                    .addComponent(btnBaja)
                    .addComponent(btnDetVen))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registro Compra", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

        tbCompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbCompra);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void btnDetVenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetVenActionPerformed
        int selectedRow = tbCompra.getSelectedRow();
    if (Conexion.rolActual.equals("Lector")) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "No tienes permiso para acceder a Detalle Compra.");
        return;
    }
        if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione una compra para ver sus detalles",
                "Validación", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        long idCompra = (long) dtCompras.getValueAt(selectedRow, 0);
        String fanText = (String) dtCompras.getValueAt(selectedRow, 1);
        int idFan = ExtraerIdFan(fanText);
        String nombreFan = ExtraerNombreFan(fanText);

        DetalleCompra formDetalle = new DetalleCompra();
        formDetalle.setDatosCompra(idCompra, idFan, nombreFan);
        formDetalle.setOnCompraFinalizada((idCompraFinal, idFanFinal, nombreFanFinal, totalCompra) -> {
            dtCompras.setValueAt(totalCompra, selectedRow, 3);
            JOptionPane.showMessageDialog(this,
                    "Compra #" + idCompraFinal + " registrada exitosamente\nTotal: " + String.format("$%.2f", totalCompra),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });
        
        formDetalle.setVisible(true);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Error al abrir DetalleCompra:\n" + ex.getClass().getName() + "\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    
    }//GEN-LAST:event_btnDetVenActionPerformed

    private void btnAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaActionPerformed
        // TODO add your handling code here:
        if (cmbFan.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un fan", 
                "Validación", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String selectedText = (String) cmbFan.getSelectedItem();  // ← Cambiar a String
    int idFan = ExtraerIdFan(selectedText);
    String nombreFanCompleto = selectedText;
    String nombreFanLimpio = ExtraerNombreFan(nombreFanCompleto);
    
    long nuevoIdCompra = CrearCompraVacia(idFan);
    
    if (nuevoIdCompra > 0) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String fechaActual = sdf.format(new java.util.Date());
        
        dtCompras.addRow(new Object[]{
            nuevoIdCompra,
            idFan + " - " + nombreFanLimpio,
            fechaActual,
            0.0
        });
        
        int nuevaFilaIndex = dtCompras.getRowCount() - 1;
        tbCompra.setRowSelectionInterval(nuevaFilaIndex, nuevaFilaIndex);
        
        JOptionPane.showMessageDialog(this, 
                "Compra #" + nuevoIdCompra + " creada exitosamente.\n" +
                "Seleccione la compra y presione 'Detalle' para agregar productos.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        
        btnDetVen.setEnabled(true);
    }
    }//GEN-LAST:event_btnAltaActionPerformed

    private void btnBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajaActionPerformed
        // TODO add your handling code here:
       int selectedRow = tbCompra.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una compra",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        long idCompra = (long) dtCompras.getValueAt(selectedRow, 0);
        String fan = (String) dtCompras.getValueAt(selectedRow, 1);
        double totalCompra = (double) dtCompras.getValueAt(selectedRow, 3);
        
        if (totalCompra > 0) {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar la compra #" + idCompra + "\n\n" +
                    "Motivo: La compra ya tiene productos asociados (Total: " + 
                    String.format("$%.2f", totalCompra) + ")",
                    "Eliminación no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int resultado = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la compra #" + idCompra + " de " + fan + "?\n" +
                "La compra está vacía (Total: " + String.format("$%.2f", totalCompra) + ").\n" +
                "Se eliminará permanentemente.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (resultado == JOptionPane.YES_OPTION) {
            try (Connection conexion = varConexion.establecerConexion()) {
                conexion.setAutoCommit(false);
                
                String queryVerificar = "SELECT COUNT(*) FROM Administrativo.DetalleCompra WHERE ID_compra = ?";
                try (PreparedStatement pstmtVerif = conexion.prepareStatement(queryVerificar)) {
                    pstmtVerif.setLong(1, idCompra);
                    ResultSet rs = pstmtVerif.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);
                    
                    if (count > 0) {
                        JOptionPane.showMessageDialog(this, 
                                "La compra ya tiene productos asociados. No se puede eliminar.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        conexion.rollback();
                        return;
                    }
                }
                
                String queryCompra = "DELETE FROM Administrativo.Compra WHERE ID_compra = ?";
                try (PreparedStatement pstmtCompra = conexion.prepareStatement(queryCompra)) {
                    pstmtCompra.setLong(1, idCompra);
                    pstmtCompra.executeUpdate();
                }
                
                conexion.commit();
                dtCompras.removeRow(selectedRow);
                
                JOptionPane.showMessageDialog(this, "Compra #" + idCompra + " eliminada correctamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnBajaActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
       try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
        logger.log(Level.SEVERE, null, ex);
    }
    
    // CORREGIR: Cambiar DetalleCompra por Compra
    java.awt.EventQueue.invokeLater(() -> new Compra().setVisible(true));  // ← Cambiar a Compra
    }
        //</editor-fold>

        /* Create and display the form */
        

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlta;
    private javax.swing.JButton btnBaja;
    private javax.swing.JButton btnDetVen;
    private javax.swing.JComboBox<String> cmbFan;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbCompra;
    // End of variables declaration//GEN-END:variables
}
