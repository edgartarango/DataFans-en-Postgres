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
public class MemCont extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MemCont.class.getName());

    /**
     * Creates new form MemCont
     */
    public MemCont() {
        initComponents();
        cargarMembresias();
        cargarRelaciones();

        // Listener para el cambio de selección en el JComboBox de membresías
        cmbMem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMemActionPerformed(evt);
            }
        });
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
    Conexion objetoConexion = new Conexion();
    cmbMem.removeAllItems();
    
    // Modificado para usar el operador de concatenación '||' de PostgreSQL
    String sql = "SELECT M.ID_membresia, "
               + "M.NombreMembresia || ' - ' || A.Nombre_artista || ' - Suscripción: ' || F.NombreFan AS Descripcion "
               + "FROM Adquisicion.Membresia M "
               + "INNER JOIN Adquisicion.Suscripcion S ON M.ID_suscripcion = S.ID_suscripcion "
               + "INNER JOIN Usuario.Artista A ON S.ID_artista = A.ID_artista "
               + "INNER JOIN Usuario.Fan F ON S.ID_fan = F.ID_fan "
               + "ORDER BY M.NombreMembresia;";
               
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
         
        while (rs.next()) {
            cmbMem.addItem(new ComboItem(rs.getLong("ID_membresia"), rs.getString("Descripcion")));
        }
        cmbMem.setSelectedIndex(-1);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar membresías: " + e.toString(), 
                "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    
    private void cargarContenidosPorMembresia(long idMembresia) {
    Conexion objetoConexion = new Conexion();
    cmbCont.removeAllItems();
    
    String sql = "SELECT C.ID_contenido, "
               + "A.Nombre_artista || ' - ' || C.TipoContenido || ' - (' || C.Titulo || ')' AS Descripcion "
               + "FROM Adquisicion.Contenido C "
               + "INNER JOIN Usuario.Artista A ON C.ID_artista = A.ID_artista "
               + "INNER JOIN Adquisicion.Suscripcion S ON S.ID_artista = A.ID_artista "
               + "INNER JOIN Adquisicion.Membresia M ON M.ID_suscripcion = S.ID_suscripcion "
               + "WHERE M.ID_membresia = ? "
               + "ORDER BY C.TipoContenido, C.Titulo;";
               
    try (Connection conn = objetoConexion.establecerConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
         
        ps.setLong(1, idMembresia);
        try (ResultSet rs = ps.executeQuery()) {
            int contador = 0;
            while (rs.next()) {
                cmbCont.addItem(new ComboItem(rs.getLong("ID_contenido"), rs.getString("Descripcion")));
                contador++;
            }
            
            if (contador == 0) {
                cmbCont.setSelectedIndex(-1);
                cmbCont.setEnabled(false);
                btnAlta.setEnabled(false);
                btnMod.setEnabled(false);
            } else {
                cmbCont.setEnabled(true);
                btnAlta.setEnabled(true);
                btnMod.setEnabled(true);
                cmbCont.setSelectedIndex(-1);
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar contenidos por membresía: " + e.toString(), 
                "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    
    private void cargarRelaciones() {
    Conexion objetoConexion = new Conexion();
    DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    
    modelo.addColumn("ID Membresia Oculto");
    modelo.addColumn("ID Contenido Oculto");
    modelo.addColumn("Membresía");
    modelo.addColumn("Contenido");
    
    tbMemCont.setModel(modelo);
    
    // Ocultar columnas técnicas de IDs
    tbMemCont.getColumnModel().getColumn(0).setMinWidth(0);
    tbMemCont.getColumnModel().getColumn(0).setMaxWidth(0);
    tbMemCont.getColumnModel().getColumn(0).setPreferredWidth(0);
    tbMemCont.getColumnModel().getColumn(1).setMinWidth(0);
    tbMemCont.getColumnModel().getColumn(1).setMaxWidth(0);
    tbMemCont.getColumnModel().getColumn(1).setPreferredWidth(0);
    
    String sql = "SELECT MC.ID_membresia, MC.ID_contenido, "
               + "M.NombreMembresia || ' - ' || A2.Nombre_artista || ' - Suscripción: ' || F.NombreFan AS Membresia, "
               + "A.Nombre_artista || ' - ' || C.TipoContenido || ' - (' || C.Titulo || ')' AS Contenido "
               + "FROM Adquisicion.MembresiaContenido MC "
               + "INNER JOIN Adquisicion.Membresia M ON MC.ID_membresia = M.ID_membresia "
               + "INNER JOIN Adquisicion.Suscripcion S ON M.ID_suscripcion = S.ID_suscripcion "
               + "INNER JOIN Usuario.Fan F ON S.ID_fan = F.ID_fan "
               + "INNER JOIN Usuario.Artista A2 ON S.ID_artista = A2.ID_artista "
               + "INNER JOIN Adquisicion.Contenido C ON MC.ID_contenido = C.ID_contenido "
               + "INNER JOIN Usuario.Artista A ON C.ID_artista = A.ID_artista "
               + "ORDER BY Membresia, Contenido;";
               
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
         
        while (rs.next()) {
            Object[] datos = new Object[4];
            datos[0] = rs.getLong("ID_membresia");
            datos[1] = rs.getLong("ID_contenido");
            datos[2] = rs.getString("Membresia");
            datos[3] = rs.getString("Contenido");
            modelo.addRow(datos);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar relaciones: " + e.toString(), 
                "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    
    private void cmbMemActionPerformed(java.awt.event.ActionEvent evt) {                                       
    if (cmbMem.getSelectedIndex() == -1) return;
    
    Object item = cmbMem.getSelectedItem();
    if (item instanceof ComboItem) {
        long idMembresia = ((ComboItem) item).getId();
        cargarContenidosPorMembresia(idMembresia);
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

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbMemCont = new javax.swing.JTable();
        jPanelArt = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cmbCont = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cmbMem = new javax.swing.JComboBox<>();
        btnMod = new javax.swing.JButton();
        btnAlta = new javax.swing.JButton();
        btnBaja = new javax.swing.JButton();

        setTitle("Membresia Contenido");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos Membresía Contenido", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

        tbMemCont.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbMemCont.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbMemContMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbMemCont);

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

        jLabel3.setText("Contenido");

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
                            .addComponent(cmbCont, 0, 201, Short.MAX_VALUE)
                            .addComponent(cmbMem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addGap(22, 22, 22)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbCont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
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
        // TODO add your handling code here:
        int fila = tbMemCont.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(null, "Seleccione un registro de la tabla", 
                "Ninguna Selección", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    if (cmbMem.getSelectedIndex() == -1 || cmbCont.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione membresía y contenido", 
                "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    long oldM = Long.parseLong(tbMemCont.getModel().getValueAt(fila, 0).toString());
    long oldC = Long.parseLong(tbMemCont.getModel().getValueAt(fila, 1).toString());
    
    long newM = ((ComboItem) cmbMem.getSelectedItem()).getId();
    long newC = ((ComboItem) cmbCont.getSelectedItem()).getId();
    
    Conexion objetoConexion = new Conexion();
    try (Connection conn = objetoConexion.establecerConexion()) {
        conn.setAutoCommit(false);
        
        try {
            // Validar Duplicados excluyendo el registro original antes del cambio
            String sqlVal = "SELECT COUNT(*) FROM Adquisicion.MembresiaContenido "
                          + "WHERE ID_membresia = ? AND ID_contenido = ? "
                          + "AND NOT (ID_membresia = ? AND ID_contenido = ?);";
            PreparedStatement psVal = conn.prepareStatement(sqlVal);
            psVal.setLong(1, newM);
            psVal.setLong(2, newC);
            psVal.setLong(3, oldM);
            psVal.setLong(4, oldC);
            ResultSet rs = psVal.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "La relación ya existe para otra membresía/contenido.", 
                        "Duplicado", JOptionPane.WARNING_MESSAGE);
                conn.rollback();
                return;
            }
            
            // Actualizar relación compuesta
            String sqlUpdate = "UPDATE Adquisicion.MembresiaContenido SET ID_membresia = ?, ID_contenido = ? "
                             + "WHERE ID_membresia = ? AND ID_contenido = ?;";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setLong(1, newM);
            psUpdate.setLong(2, newC);
            psUpdate.setLong(3, oldM);
            psUpdate.setLong(4, oldC);
            
            psUpdate.executeUpdate();
            conn.commit();
            
            //JOptionPane.showMessageDialog(null, "Relación modificada con éxito.");
            cargarRelaciones();
            limpiarCampos();
            
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al modificar la relación: " + e.toString(), 
                "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnModActionPerformed

    private void btnAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaActionPerformed
        // TODO add your handling code here:
        if (cmbMem.getSelectedIndex() == -1 || cmbCont.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione membresía y contenido", 
                "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    long idMembresia = ((ComboItem) cmbMem.getSelectedItem()).getId();
    long idContenido = ((ComboItem) cmbCont.getSelectedItem()).getId();
    
    Conexion objetoConexion = new Conexion();
    try (Connection conn = objetoConexion.establecerConexion()) {
        conn.setAutoCommit(false);
        
        try {
            // Validar Duplicados
            String sqlVal = "SELECT COUNT(*) FROM Adquisicion.MembresiaContenido WHERE ID_membresia = ? AND ID_contenido = ?;";
            PreparedStatement psVal = conn.prepareStatement(sqlVal);
            psVal.setLong(1, idMembresia);
            psVal.setLong(2, idContenido);
            ResultSet rs = psVal.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "La relación ya existe.", 
                        "Duplicado", JOptionPane.WARNING_MESSAGE);
                conn.rollback();
                return;
            }
            
            // Insertar relación
            String sqlInsert = "INSERT INTO Adquisicion.MembresiaContenido (ID_membresia, ID_contenido) VALUES (?, ?);";
            PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setLong(1, idMembresia);
            psInsert.setLong(2, idContenido);
            
            psInsert.executeUpdate();
            conn.commit();
            
           // JOptionPane.showMessageDialog(null, "Relación insertada correctamente.");
            cargarRelaciones();
            limpiarCampos();
            
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al insertar relación: " + e.toString(), 
                "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnAltaActionPerformed

    private void btnBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajaActionPerformed
        // TODO add your handling code here:
        int fila = tbMemCont.getSelectedRow();
    if (fila < 0) {
        JOptionPane.showMessageDialog(null, "Seleccione un registro de la tabla", 
                "Ninguna Selección", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    long idMembresia = Long.parseLong(tbMemCont.getModel().getValueAt(fila, 0).toString());
    long idContenido = Long.parseLong(tbMemCont.getModel().getValueAt(fila, 1).toString());
    
    int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar esta relación?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            
    if (confirm == JOptionPane.YES_OPTION) {
        Conexion objetoConexion = new Conexion();
        String sql = "DELETE FROM Adquisicion.MembresiaContenido WHERE ID_membresia = ? AND ID_contenido = ?;";
        
        try (Connection conn = objetoConexion.establecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setLong(1, idMembresia);
            ps.setLong(2, idContenido);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Relación eliminada de manera conforme.");
            cargarRelaciones();
            limpiarCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar relación: " + e.toString(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_btnBajaActionPerformed

    private void tbMemContMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbMemContMouseClicked
        int fila = 0;
        // TODO add your handling code here:int fila = tbMemCont.getSelectedRow();
    if (fila >= 0) {
        long idMembresiaTarget = Long.parseLong(tbMemCont.getModel().getValueAt(fila, 0).toString());
        long idContenidoTarget = Long.parseLong(tbMemCont.getModel().getValueAt(fila, 1).toString());
        
        // 1. Sincronizar Combo Membresía
        for (int i = 0; i < cmbMem.getItemCount(); i++) {
            Object item = cmbMem.getItemAt(i);
            if (item instanceof ComboItem && ((ComboItem) item).getId() == idMembresiaTarget) {
                cmbMem.setSelectedIndex(i);
                break;
            }
        }
        
        // 2. Cargar forzadamente los contenidos de esa membresía
        cargarContenidosPorMembresia(idMembresiaTarget);
        
        // 3. Sincronizar Combo Contenido
        for (int i = 0; i < cmbCont.getItemCount(); i++) {
            Object item = cmbCont.getItemAt(i);
            if (item instanceof ComboItem && ((ComboItem) item).getId() == idContenidoTarget) {
                cmbCont.setSelectedIndex(i);
                break;
            }
        }
    }
    }//GEN-LAST:event_tbMemContMouseClicked

    private void limpiarCampos() {
    cmbMem.setSelectedIndex(-1);
    cmbCont.removeAllItems();
    cmbCont.setSelectedIndex(-1);
    tbMemCont.clearSelection();
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
        java.awt.EventQueue.invokeLater(() -> new MemCont().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlta;
    private javax.swing.JButton btnBaja;
    private javax.swing.JButton btnMod;
    private javax.swing.JComboBox<ComboItem> cmbCont;
    private javax.swing.JComboBox<ComboItem> cmbMem;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbMemCont;
    // End of variables declaration//GEN-END:variables
}
