/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.datafans;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

 
/**
 *
 * @author Jacky09
 */
public class MemBen extends javax.swing.JFrame {
    
    /**
     * Creates new form MemBen
     */
    public MemBen() {
    initComponents();
    // Cargar los ComboBoxes
    cargarMembresias();
    cargarBeneficios();
    
    // Cargar la tabla principal
    cargarMemBen();
    aplicarPermisosPorRol();
    }

    private void aplicarPermisosPorRol() {
        String rol = Conexion.rolActual;
        
        if (rol.equals("Editor")) {
            // El editor puede dar de Alta y Modificar, pero NO puede dar de Baja (Delete)
            btnBaja.setEnabled(false);
        } else if (rol.equals("Lector")) {
            // El lector SOLO puede consultar. No puede dar de Alta, Baja ni Modificar
            btnAlta.setEnabled(false);
            btnBaja.setEnabled(false);
            btnMod.setEnabled(false);
        }
        // Si es "Admin", no entra a ninguna condición y conserva todos los botones activos.
    }    
private void cargarMembresias() {
    cmbMembresia.removeAllItems();
    Conexion objetoConexion = new Conexion();
    
    String sql = "SELECT M.ID_membresia, "
               + "M.NombreMembresia || ' - ' || A.Nombre_artista || ' - Suscripción: ' || F.NombreFan AS Descripcion "
               + "FROM Adquisicion.Membresia M "
               + "INNER JOIN Adquisicion.Suscripcion S ON M.ID_suscripcion = S.ID_suscripcion "
               + "INNER JOIN Usuario.Fan F ON S.ID_fan = F.ID_fan "
               + "INNER JOIN Usuario.Artista A ON S.ID_artista = A.ID_artista;";
               
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
         
        while (rs.next()) {
            cmbMembresia.addItem(new ComboItem(rs.getLong("ID_membresia"), rs.getString("Descripcion")));
        }
        cmbMembresia.setSelectedIndex(-1);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar membresías: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void cargarBeneficios() {
    cmbBeneficio.removeAllItems();
    Conexion objetoConexion = new Conexion();
    
    String sql = "SELECT ID_beneficio, "
               + "TipoBeneficio || ' - ' || '(' || NombreBeneficio || ')' AS Beneficio "
               + "FROM Adquisicion.Beneficio;";
               
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
         
        while (rs.next()) {
            cmbBeneficio.addItem(new ComboItem(rs.getLong("ID_beneficio"), rs.getString("Beneficio")));
        }
        cmbBeneficio.setSelectedIndex(-1);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar beneficios: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void cargarMemBen() {
    Conexion objetoConexion = new Conexion();
    DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    
    modelo.addColumn("ID Membresia");
    modelo.addColumn("ID Beneficio");
    modelo.addColumn("Beneficio");
    modelo.addColumn("Membresía");
    
    tbMemBen.setModel(modelo);
    
    // Ocultar columnas de IDs técnicos
    tbMemBen.getColumnModel().getColumn(0).setMinWidth(0);
    tbMemBen.getColumnModel().getColumn(0).setMaxWidth(0);
    tbMemBen.getColumnModel().getColumn(0).setPreferredWidth(0);
    tbMemBen.getColumnModel().getColumn(1).setMinWidth(0);
    tbMemBen.getColumnModel().getColumn(1).setMaxWidth(0);
    tbMemBen.getColumnModel().getColumn(1).setPreferredWidth(0);
    
    String sql = "SELECT MB.ID_membresia, MB.ID_beneficio, "
               + "B.TipoBeneficio || ' - ' || '(' || B.NombreBeneficio || ')' AS Beneficio, "
               + "M.NombreMembresia || ' - ' || A.Nombre_artista || ' - ' || F.NombreFan AS Membresia "
               + "FROM Adquisicion.MembresiaBeneficio MB "
               + "INNER JOIN Adquisicion.Beneficio B ON MB.ID_beneficio = B.ID_beneficio "
               + "INNER JOIN Adquisicion.Membresia M ON MB.ID_membresia = M.ID_membresia "
               + "INNER JOIN Adquisicion.Suscripcion S ON M.ID_suscripcion = S.ID_suscripcion "
               + "INNER JOIN Usuario.Fan F ON S.ID_fan = F.ID_fan "
               + "INNER JOIN Usuario.Artista A ON S.ID_artista = A.ID_artista;";
               
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
         
        while (rs.next()) {
            Object[] fila = new Object[4];
            fila[0] = rs.getLong("ID_membresia");
            fila[1] = rs.getLong("ID_beneficio");
            fila[2] = rs.getString("Beneficio");
            fila[3] = rs.getString("Membresia");
            modelo.addRow(fila);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar relaciones: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void limpiarCampos() {
    cmbMembresia.setSelectedIndex(-1);
    cmbBeneficio.setSelectedIndex(-1);
    tbMemBen.clearSelection();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbMemBen = new javax.swing.JTable();
        jPanelArt = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cmbBeneficio = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cmbMembresia = new javax.swing.JComboBox<>();
        btnMod = new javax.swing.JButton();
        btnAlta = new javax.swing.JButton();
        btnBaja = new javax.swing.JButton();

        setTitle("Membresia Beneficio");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Membresía Beneficio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

        tbMemBen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbMemBen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbMemBenMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbMemBen);

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

        jPanelArt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelArt.setPreferredSize(new java.awt.Dimension(330, 0));

        jLabel3.setText("Beneficio");

        jLabel4.setText("Membresía");

        btnMod.setText("Modificación");
        btnMod.addActionListener(this::btnModActionPerformed);

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
                        .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbBeneficio, 0, 201, Short.MAX_VALUE)
                            .addComponent(cmbMembresia, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(btnAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMod)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanelArtLayout.setVerticalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbBeneficio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMembresia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMod)
                    .addComponent(btnAlta)
                    .addComponent(btnBaja))
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

    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
    int fila = tbMemBen.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(null, "Seleccione un registro de la tabla para modificar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    if (cmbMembresia.getSelectedIndex() == -1 || cmbBeneficio.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione los nuevos valores.", "Validación", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Valores anteriores (Vienen de la fila seleccionada)
    long oldMem = Long.parseLong(tbMemBen.getValueAt(fila, 0).toString());
    long oldBen = Long.parseLong(tbMemBen.getValueAt(fila, 1).toString());
    
    // Nuevos valores (Vienen de los combos seleccionados)
    long newMem = ((ComboItem) cmbMembresia.getSelectedItem()).getId();
    long newBen = ((ComboItem) cmbBeneficio.getSelectedItem()).getId();
    
    Conexion objetoConexion = new Conexion();
    try (Connection conn = objetoConexion.establecerConexion()) {
        conn.setAutoCommit(false);
        
        try {
            // Validar Duplicado excluyendo el registro actual que se está modificando
            String sqlVal = "SELECT COUNT(*) FROM Adquisicion.MembresiaBeneficio "
                          + "WHERE ID_membresia = ? AND ID_beneficio = ? "
                          + "AND NOT (ID_membresia = ? AND ID_beneficio = ?);";
                          
            try (PreparedStatement psVal = conn.prepareStatement(sqlVal)) {
                psVal.setLong(1, newMem);
                psVal.setLong(2, newBen);
                psVal.setLong(3, oldMem);
                psVal.setLong(4, oldBen);
                ResultSet rs = psVal.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null, "Ya existe otra relación registrada con estos valores.", "Conflicto", JOptionPane.WARNING_MESSAGE);
                    conn.rollback();
                    return;
                }
            }
            
            // Actualizar la tabla puente
            String sqlUpdate = "UPDATE Adquisicion.MembresiaBeneficio SET ID_membresia = ?, ID_beneficio = ? "
                             + "WHERE ID_membresia = ? AND ID_beneficio = ?;";
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setLong(1, newMem);
                psUpdate.setLong(2, newBen);
                psUpdate.setLong(3, oldMem);
                psUpdate.setLong(4, oldBen);
                psUpdate.executeUpdate();
            }
            
            conn.commit();
           // JOptionPane.showMessageDialog(null, "Relación modificada exitosamente.");
            cargarMemBen();
            limpiarCampos();
            
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al modificar: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnModActionPerformed

    private void btnAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaActionPerformed
    if (cmbMembresia.getSelectedIndex() == -1 || cmbBeneficio.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione datos válidos en ambos campos.", "Validación", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    long idMem = ((ComboItem) cmbMembresia.getSelectedItem()).getId();
    long idBen = ((ComboItem) cmbBeneficio.getSelectedItem()).getId();
    
    Conexion objetoConexion = new Conexion();
    try (Connection conn = objetoConexion.establecerConexion()) {
        
        // Validar si la relación ya existe
        String sqlVal = "SELECT COUNT(*) FROM Adquisicion.MembresiaBeneficio WHERE ID_membresia = ? AND ID_beneficio = ?;";
        try (PreparedStatement psVal = conn.prepareStatement(sqlVal)) {
            psVal.setLong(1, idMem);
            psVal.setLong(2, idBen);
            ResultSet rs = psVal.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Ya existe esta relación asignada.", "Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Insertar relación
        String sqlInsert = "INSERT INTO Adquisicion.MembresiaBeneficio (ID_membresia, ID_beneficio) VALUES (?, ?);";
        try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
            psInsert.setLong(1, idMem);
            psInsert.setLong(2, idBen);
            psInsert.executeUpdate();
        }
        
        //JOptionPane.showMessageDialog(null, "Relación guardada exitosamente.");
        cargarMemBen();
        limpiarCampos();
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al insertar relación: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnAltaActionPerformed

    private void btnBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajaActionPerformed
    int fila = tbMemBen.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    int confirmar = JOptionPane.showConfirmDialog(null, "¿Está seguro de remover este beneficio de la membresía?", "Confirmar", JOptionPane.YES_NO_OPTION);
    if (confirmar != JOptionPane.YES_OPTION) return;
    
    long idMem = Long.parseLong(tbMemBen.getValueAt(fila, 0).toString());
    long idBen = Long.parseLong(tbMemBen.getValueAt(fila, 1).toString());
    
    Conexion objetoConexion = new Conexion();
    String sqlDelete = "DELETE FROM Adquisicion.MembresiaBeneficio WHERE ID_membresia = ? AND ID_beneficio = ?;";
    
    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
         
        ps.setLong(1, idMem);
        ps.setLong(2, idBen);
        ps.executeUpdate();
        
       // JOptionPane.showMessageDialog(null, "Relación eliminada de forma exitosa.");
        cargarMemBen();
        limpiarCampos();
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar relación: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnBajaActionPerformed

    private void tbMemBenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbMemBenMouseClicked
    int fila = tbMemBen.getSelectedRow();
    if (fila >= 0) {
        long idMem = Long.parseLong(tbMemBen.getValueAt(fila, 0).toString());
        long idBen = Long.parseLong(tbMemBen.getValueAt(fila, 1).toString());
        
        // Sincronizar Combo Membresia
        for (int i = 0; i < cmbMembresia.getItemCount(); i++) {
            ComboItem item = (ComboItem) cmbMembresia.getItemAt(i);
            if (item.getId() == idMem) {
                cmbMembresia.setSelectedIndex(i);
                break;
            }
        }
        
        // Sincronizar Combo Beneficio
        for (int i = 0; i < cmbBeneficio.getItemCount(); i++) {
            ComboItem item = (ComboItem) cmbBeneficio.getItemAt(i);
            if (item.getId() == idBen) {
                cmbBeneficio.setSelectedIndex(i);
                break;
            }
        }
    }
    }//GEN-LAST:event_tbMemBenMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new MemBen().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlta;
    private javax.swing.JButton btnBaja;
    private javax.swing.JButton btnMod;
    private javax.swing.JComboBox<ComboItem> cmbBeneficio;
    private javax.swing.JComboBox<ComboItem> cmbMembresia;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbMemBen;
    // End of variables declaration//GEN-END:variables
}


