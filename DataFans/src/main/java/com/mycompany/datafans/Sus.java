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
public class Sus extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Sus.class.getName());

    /**
     * Creates new form Sus
     */
    public Sus() {
        initComponents();
        llenarCombos();
        cargarSuscripciones();
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
    
    private void llenarCombos() {
        Conexion objetoConexion = new Conexion();
        cmbFans.removeAllItems();
        cmbArt.removeAllItems();

        String sqlFans = "SELECT ID_fan, NombreFan FROM Usuario.Fan;";
        String sqlArtistas = "SELECT ID_artista, Nombre_Artista FROM Usuario.Artista;";

        try (Connection conn = objetoConexion.establecerConexion()) {
            // Cargar Fans
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sqlFans)) {
                while (rs.next()) {
                    long id = rs.getLong("ID_fan");
                    String display = id + " - " + rs.getString("NombreFan");
                    cmbFans.addItem(new ComboItem(id, display));
                }
            }

            // Cargar Artistas
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sqlArtistas)) {
                while (rs.next()) {
                    cmbArt.addItem(new ComboItem(rs.getLong("ID_artista"), rs.getString("Nombre_Artista")));
                }
            }

            // Inicializar sin selección previa
            cmbFans.setSelectedIndex(-1);
            cmbArt.setSelectedIndex(-1);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al inicializar listas de selección: " + e.toString(), 
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void cargarSuscripciones() {
        Conexion objetoConexion = new Conexion();
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Evita edición directa
        };

        // Mapeamos las columnas ocultando los IDs directos al usuario
        modelo.addColumn("ID Suscripción");
        modelo.addColumn("Fan");
        modelo.addColumn("Artista");
        modelo.addColumn("ID_fan_oculto");
        modelo.addColumn("ID_artista_oculto");

        tbSus.setModel(modelo);

        // Ocultar las columnas de IDs del renderizado visual, pero mantenerlas en el modelo de datos
        tbSus.getColumnModel().getColumn(3).setMinWidth(0);
        tbSus.getColumnModel().getColumn(3).setMaxWidth(0);
        tbSus.getColumnModel().getColumn(3).setPreferredWidth(0);
        tbSus.getColumnModel().getColumn(4).setMinWidth(0);
        tbSus.getColumnModel().getColumn(4).setMaxWidth(0);
        tbSus.getColumnModel().getColumn(4).setPreferredWidth(0);

        String sql = "SELECT s.ID_suscripcion, s.ID_fan, s.ID_artista, "
                   + "(s.ID_fan || ' - ' || f.NombreFan) AS FanDisplay, a.Nombre_Artista "
                   + "FROM Adquisicion.Suscripcion s "
                   + "INNER JOIN Usuario.Fan f ON s.ID_fan = f.ID_fan "
                   + "INNER JOIN Usuario.Artista a ON s.ID_artista = a.ID_artista;";

        try (Connection conn = objetoConexion.establecerConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Object[] datos = new Object[5];
                datos[0] = rs.getLong("ID_suscripcion");
                datos[1] = rs.getString("FanDisplay");
                datos[2] = rs.getString("Nombre_Artista");
                datos[3] = rs.getLong("ID_fan");
                datos[4] = rs.getLong("ID_artista");
                modelo.addRow(datos);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar las suscripciones: " + e.toString(), 
                    "Error de Conexión", JOptionPane.ERROR_MESSAGE);
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
        jLabel3 = new javax.swing.JLabel();
        cmbFans = new javax.swing.JComboBox<>();
        cmbArt = new javax.swing.JComboBox<>();
        btnMod = new javax.swing.JButton();
        btnAlta = new javax.swing.JButton();
        btnBaja = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbSus = new javax.swing.JTable();

        setTitle("Suscripción");

        jPanelArt.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Suscripción", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N
        jPanelArt.setPreferredSize(new java.awt.Dimension(330, 0));

        jLabel2.setText("Fan");

        jLabel3.setText("Artista");

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
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbFans, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbArt, 0, 160, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelArtLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(btnAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMod)
                .addContainerGap())
        );
        jPanelArtLayout.setVerticalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbFans, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbArt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMod)
                    .addComponent(btnAlta)
                    .addComponent(btnBaja))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tabla Suscripción", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

        tbSus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbSus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbSusMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbSus);

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

    private void limpiarCampos() {
    cmbFans.setSelectedIndex(-1);
    cmbArt.setSelectedIndex(-1);
    tbSus.clearSelection();
    }
    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
        // TODO add your handling code here:
        int fila = tbSus.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una suscripción de la lista para poder modificarla.", 
                    "Ninguna Selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (cmbFans.getSelectedIndex() == -1 || cmbArt.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Ambas listas de selección deben poseer un valor asignado.", 
                    "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idActual = Long.parseLong(tbSus.getValueAt(fila, 0).toString());
        long idFan = ((ComboItem) cmbFans.getSelectedItem()).getId();
        long idArtista = ((ComboItem) cmbArt.getSelectedItem()).getId();

        Conexion objetoConexion = new Conexion();
        try (Connection conn = objetoConexion.establecerConexion()) {
            // Validar duplicados cruzados en otros registros
            String queryValidacion = "SELECT COUNT(*) FROM Adquisicion.Suscripcion WHERE ID_fan = ? AND ID_artista = ? AND ID_suscripcion <> ?;";
            PreparedStatement psVal = conn.prepareStatement(queryValidacion);
            psVal.setLong(1, idFan);
            psVal.setLong(2, idArtista);
            psVal.setLong(3, idActual);
            ResultSet rs = psVal.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Ya existe otra suscripción registrada con este Fan y Artista.", 
                        "Suscripción Duplicada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Ejecutar UPDATE
            String sql = "UPDATE Adquisicion.Suscripcion SET ID_fan = ?, ID_artista = ? WHERE ID_suscripcion = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, idFan);
            ps.setLong(2, idArtista);
            ps.setLong(3, idActual);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Suscripción modificada con éxito.");
            cargarSuscripciones();
            limpiarCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar la información: " + e.toString(), 
                    "Error al Actualizar", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnModActionPerformed

    private void btnAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaActionPerformed
        // TODO add your handling code here:
        if (cmbFans.getSelectedIndex() == -1 || cmbArt.getSelectedIndex() == -1) {
        JOptionPane.showMessageDialog(null, "Por favor, seleccione un Fan y un Artista de las listas.", 
                "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
        return;
        }

        long idFan = ((ComboItem) cmbFans.getSelectedItem()).getId();
        long idArtista = ((ComboItem) cmbArt.getSelectedItem()).getId();

        Conexion objetoConexion = new Conexion();
        try (Connection conn = objetoConexion.establecerConexion()) {
            // Validar si la suscripción ya existe
            String queryValidacion = "SELECT COUNT(*) FROM Adquisicion.Suscripcion WHERE ID_fan = ? AND ID_artista = ?;";
            PreparedStatement psVal = conn.prepareStatement(queryValidacion);
            psVal.setLong(1, idFan);
            psVal.setLong(2, idArtista);
            ResultSet rs = psVal.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Ya existe una suscripción activa para este fan con este artista.\nNo se permiten duplicados.", 
                        "Suscripción Duplicada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Insertar Registro
            String sql = "INSERT INTO Adquisicion.Suscripcion (ID_fan, ID_artista) VALUES (?, ?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, idFan);
            ps.setLong(2, idArtista);

            ps.executeUpdate();
            cargarSuscripciones();
            limpiarCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la suscripción: " + e.toString(), 
                    "Error al Guardar", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAltaActionPerformed

    private void btnBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajaActionPerformed
        // TODO add your handling code here:
        int fila = tbSus.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una suscripción de la lista para poder eliminarla.", 
                    "Ninguna Selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long idEliminar = Long.parseLong(tbSus.getValueAt(fila, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(null, 
                "¿Está seguro de eliminar esta suscripción?\nSe removerán también todas las membresías asociadas.", 
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Conexion objetoConexion = new Conexion();
            String sql = "DELETE FROM Adquisicion.Suscripcion WHERE ID_suscripcion = ?;";
            try (Connection conn = objetoConexion.establecerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEliminar);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "La suscripción se ha eliminado correctamente.");
                cargarSuscripciones();
                limpiarCampos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar. Verifique dependencias de llaves foráneas.\nDetalle técnico: " + e.toString(), 
                        "Error al Eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnBajaActionPerformed

    private void tbSusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbSusMouseClicked
        int fila = tbSus.getSelectedRow();
        if (fila >= 0) {
            long idFanTarget = Long.parseLong(tbSus.getModel().getValueAt(fila, 3).toString());
            long idArtTarget = Long.parseLong(tbSus.getModel().getValueAt(fila, 4).toString());

            // Sincronizar Combo Fans
            for (int i = 0; i < cmbFans.getItemCount(); i++) {
                if (((ComboItem) cmbFans.getItemAt(i)).getId() == idFanTarget) {
                    cmbFans.setSelectedIndex(i);
                    break;
                }
            }

            // Sincronizar Combo Artistas
            for (int i = 0; i < cmbArt.getItemCount(); i++) {
                if (((ComboItem) cmbArt.getItemAt(i)).getId() == idArtTarget) {
                    cmbArt.setSelectedIndex(i);
                    break;
                }
            }
        }
    }//GEN-LAST:event_tbSusMouseClicked

    
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
        java.awt.EventQueue.invokeLater(() -> new Sus().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlta;
    private javax.swing.JButton btnBaja;
    private javax.swing.JButton btnMod;
    private javax.swing.JComboBox<ComboItem> cmbArt;
    private javax.swing.JComboBox<ComboItem> cmbFans;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbSus;
    // End of variables declaration//GEN-END:variables
}


