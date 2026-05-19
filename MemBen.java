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
    
    
    // Guarda los IDs del registro seleccionado en la tabla
    private long oldIdMem = -1;
    private long oldIdBen = -1;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MemBen.class.getName());

    /**
     * Creates new form MemBen
     */
    public MemBen() {
    initComponents();
    cargarMembresias();
    cargarBeneficios();
    cargarMemBen();
}
    
    private void cargarMembresias() {
    cmbMem.removeAllItems();
    Conexion objetoConexion = new Conexion();
    String sql =
        "SELECT M.ID_membresia, " +
        "M.NombreMembresia || ' - ' || A.Nombre_artista || ' - Suscripción: ' || F.NombreFan AS Descripcion " +
        "FROM Adquisicion.Membresia M " +
        "INNER JOIN Adquisicion.Suscripcion S ON M.ID_suscripcion = S.ID_suscripcion " +
        "INNER JOIN Usuario.Fan F ON S.ID_fan = F.ID_fan " +
        "INNER JOIN Usuario.Artista A ON S.ID_artista = A.ID_artista " +
        "ORDER BY M.ID_membresia";
 
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
 
        while (rs.next()) {
            cmbMem.addItem(rs.getLong("ID_membresia") + " | " + rs.getString("Descripcion"));
        }
        cmbMem.setSelectedIndex(-1);
 
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar membresías: " + e.toString());
    }
}
    
    private void cargarBeneficios() {
    cmbTipo.removeAllItems();
    Conexion objetoConexion = new Conexion();
    String sql =
        "SELECT ID_beneficio, " +
        "TipoBeneficio || ' - (' || NombreBeneficio || ')' AS BeneficioCompleto " +
        "FROM Adquisicion.Beneficio " +
        "ORDER BY ID_beneficio";
 
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
 
        while (rs.next()) {
            cmbTipo.addItem(rs.getLong("ID_beneficio") + " | " + rs.getString("BeneficioCompleto"));
        }
        cmbTipo.setSelectedIndex(-1);
 
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar beneficios: " + e.toString());
    }
}
    
    private void cargarMemBen() {
    Conexion objetoConexion = new Conexion();
    DefaultTableModel modelo = new DefaultTableModel();
    modelo.addColumn("ID Membresía");
    modelo.addColumn("ID Beneficio");
    modelo.addColumn("Beneficio");
    modelo.addColumn("Membresía");
 
    tbMemBen.setModel(modelo);
 
    String sql =
        "SELECT " +
        "MB.ID_membresia, " +
        "MB.ID_beneficio, " +
        "B.TipoBeneficio || ' - (' || B.NombreBeneficio || ')' AS BeneficioCompleto, " +
        "M.NombreMembresia || ' - ' || A.Nombre_artista || ' - Suscripción: ' || F.NombreFan AS Membresia " +
        "FROM Adquisicion.MembresiaBeneficio MB " +
        "INNER JOIN Adquisicion.Beneficio B ON MB.ID_beneficio = B.ID_beneficio " +
        "INNER JOIN Adquisicion.Membresia M ON MB.ID_membresia = M.ID_membresia " +
        "INNER JOIN Adquisicion.Suscripcion S ON M.ID_suscripcion = S.ID_suscripcion " +
        "INNER JOIN Usuario.Fan F ON S.ID_fan = F.ID_fan " +
        "INNER JOIN Usuario.Artista A ON S.ID_artista = A.ID_artista";
 
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
 
        while (rs.next()) {
            Object[] datos = {
                rs.getLong("ID_membresia"),
                rs.getLong("ID_beneficio"),
                rs.getString("BeneficioCompleto"),
                rs.getString("Membresia")
            };
            modelo.addRow(datos);
        }
 
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar tabla: " + e.toString());
    }
}
    
    private void limpiarSeleccion() {
    cmbMem.setSelectedIndex(-1);
    cmbTipo.setSelectedIndex(-1);
    oldIdMem = -1;
    oldIdBen = -1;
}
    
    private long getIdFromCombo(javax.swing.JComboBox<String> combo) {
    if (combo.getSelectedIndex() == -1) return -1;
    String seleccion = combo.getSelectedItem().toString();
    return Long.parseLong(seleccion.split(" \\| ")[0].trim());
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
        cmbTipo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cmbMem = new javax.swing.JComboBox<>();
        btnMod = new javax.swing.JButton();
        btnAlta = new javax.swing.JButton();
        btnBaja = new javax.swing.JButton();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tabla Membresía Beneficio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

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

        jPanelArt.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Membresía Beneficio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N
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
                            .addComponent(cmbTipo, 0, 201, Short.MAX_VALUE)
                            .addComponent(cmbMem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(btnAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMod)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanelArtLayout.setVerticalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    int filaMod = tbMemBen.getSelectedRow();
    if (filaMod < 0) {
        JOptionPane.showMessageDialog(null, "Seleccione un registro de la tabla.");
        return;
    }
 
    if (cmbMem.getSelectedIndex() == -1 || cmbTipo.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione los nuevos valores en los combos.");
        return;
    }
 
    // Valores OLD (fila seleccionada en la tabla)
    long oldMem = Long.parseLong(tbMemBen.getValueAt(filaMod, 0).toString());
    long oldBen = Long.parseLong(tbMemBen.getValueAt(filaMod, 1).toString());
 
    // Valores NEW (seleccionados en los combos)
    long newMem = getIdFromCombo(cmbMem);
    long newBen = getIdFromCombo(cmbTipo);
 
    Conexion objConMod = new Conexion();
 
    // Validar duplicado excluyendo el registro actual
    String sqlVal =
        "SELECT COUNT(*) FROM Adquisicion.MembresiaBeneficio " +
        "WHERE ID_membresia = ? AND ID_beneficio = ? " +
        "AND NOT (ID_membresia = ? AND ID_beneficio = ?)";
 
    try (Connection conn = objConMod.establecerConexion();
         PreparedStatement psVal = conn.prepareStatement(sqlVal)) {
 
        psVal.setLong(1, newMem);
        psVal.setLong(2, newBen);
        psVal.setLong(3, oldMem);
        psVal.setLong(4, oldBen);
        ResultSet rs = psVal.executeQuery();
        rs.next();
        if (rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null, "Ya existe esta relación.");
            return;
        }
 
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al validar: " + e.toString());
        return;
    }
 
    // Actualizar con transacción (igual que en C#)
    String sqlMod =
        "UPDATE Adquisicion.MembresiaBeneficio " +
        "SET ID_membresia = ?, ID_beneficio = ? " +
        "WHERE ID_membresia = ? AND ID_beneficio = ?";
 
    try (Connection conn = objConMod.establecerConexion()) {
        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement(sqlMod)) {
            ps.setLong(1, newMem);
            ps.setLong(2, newBen);
            ps.setLong(3, oldMem);
            ps.setLong(4, oldBen);
            ps.executeUpdate();
            conn.commit();
 
            cargarMemBen();
            limpiarSeleccion();
 
        } catch (Exception e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Error al modificar: " + e.toString());
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error de conexión: " + e.toString());
    }
    }//GEN-LAST:event_btnModActionPerformed

    private void btnAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaActionPerformed
    if (cmbMem.getSelectedIndex() == -1 || cmbTipo.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una membresía y un beneficio.");
        return;
    }
 
    long idMem = getIdFromCombo(cmbMem);
    long idBen = getIdFromCombo(cmbTipo);
 
    Conexion objConAlta = new Conexion();
 
    // Validar que no exista ya la relación
    String sqlVal =
        "SELECT COUNT(*) FROM Adquisicion.MembresiaBeneficio " +
        "WHERE ID_membresia = ? AND ID_beneficio = ?";
 
    try (Connection conn = objConAlta.establecerConexion();
         PreparedStatement psVal = conn.prepareStatement(sqlVal)) {
 
        psVal.setLong(1, idMem);
        psVal.setLong(2, idBen);
        ResultSet rs = psVal.executeQuery();
        rs.next();
        if (rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null, "Ya existe esta relación entre membresía y beneficio.");
            return;
        }
 
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al validar: " + e.toString());
        return;
    }
 
    // Insertar la relación
    String sqlIns =
        "INSERT INTO Adquisicion.MembresiaBeneficio (ID_membresia, ID_beneficio) VALUES (?, ?)";
 
    try (Connection conn = objConAlta.establecerConexion();
         PreparedStatement ps = conn.prepareStatement(sqlIns)) {
 
        ps.setLong(1, idMem);
        ps.setLong(2, idBen);
        ps.executeUpdate();
 
        cargarMemBen();
        limpiarSeleccion();
 
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al registrar relación: " + e.toString());
    }
    }//GEN-LAST:event_btnAltaActionPerformed

    private void btnBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajaActionPerformed
    int filaBaja = tbMemBen.getSelectedRow();
    if (filaBaja < 0) {
        JOptionPane.showMessageDialog(null, "Seleccione un registro de la tabla.");
        return;
    }
 
    long idMemBaja = Long.parseLong(tbMemBen.getValueAt(filaBaja, 0).toString());
    long idBenBaja = Long.parseLong(tbMemBen.getValueAt(filaBaja, 1).toString());
 
    int confirm = JOptionPane.showConfirmDialog(null,
        "¿Está seguro de eliminar esta relación?",
        "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
 
    if (confirm != JOptionPane.YES_OPTION) return;
 
    Conexion objConBaja = new Conexion();
    String sqlDel =
        "DELETE FROM Adquisicion.MembresiaBeneficio " +
        "WHERE ID_membresia = ? AND ID_beneficio = ?";
 
    try (Connection conn = objConBaja.establecerConexion();
         PreparedStatement ps = conn.prepareStatement(sqlDel)) {
 
        ps.setLong(1, idMemBaja);
        ps.setLong(2, idBenBaja);
        ps.executeUpdate();
 
        cargarMemBen();
        limpiarSeleccion();
 
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.toString());
    }
    }//GEN-LAST:event_btnBajaActionPerformed

    private void tbMemBenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbMemBenMouseClicked
    int fila = tbMemBen.getSelectedRow();
    if (fila >= 0) {
        long idMemSel = Long.parseLong(tbMemBen.getValueAt(fila, 0).toString());
        long idBenSel = Long.parseLong(tbMemBen.getValueAt(fila, 1).toString());
 
        // Seleccionar en cmbMem el item que corresponde al ID
        for (int i = 0; i < cmbMem.getItemCount(); i++) {
            long id = Long.parseLong(cmbMem.getItemAt(i).split(" \\| ")[0].trim());
            if (id == idMemSel) {
                cmbMem.setSelectedIndex(i);
                break;
            }
        }
 
        // Seleccionar en cmbTipo el item que corresponde al ID
        for (int i = 0; i < cmbTipo.getItemCount(); i++) {
            long id = Long.parseLong(cmbTipo.getItemAt(i).split(" \\| ")[0].trim());
            if (id == idBenSel) {
                cmbTipo.setSelectedIndex(i);
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
    private javax.swing.JComboBox<String> cmbMem;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbMemBen;
    // End of variables declaration//GEN-END:variables
}
