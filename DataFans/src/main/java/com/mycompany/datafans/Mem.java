/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.datafans;

/**
 *
 * @author Jacky09
 */
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
public class Mem extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Mem.class.getName());
    
    /**
     * Creates new form Mem
     */
    public Mem() {
        
        initComponents();
        cargarSuscripciones();
        cargarMembresias();
        cargarTiposMembresia();
        cargarDuraciones();
        limpiarCampos();
        
        tbMem.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tbMemMouseClicked(evt);
        }
    });
    // ==========================================
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
    private void cargarSuscripciones() {
    Conexion objetoConexion = new Conexion();
    
    try (Connection conn = objetoConexion.establecerConexion()) {
        String query = 
            "SELECT " +
            "    s.ID_suscripcion, " +
            "    a.Nombre_Artista || ' - Suscriptor: ' || f.NombreFan AS DisplayText " +
            "FROM Adquisicion.Suscripcion s " +
            "INNER JOIN Usuario.Fan f ON s.ID_fan = f.ID_fan " +
            "INNER JOIN Usuario.Artista a ON s.ID_artista = a.ID_artista " +
            "ORDER BY a.Nombre_Artista, f.NombreFan";
        
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        java.util.ArrayList<Integer> ids = new java.util.ArrayList<>();
        
        while (rs.next()) {
            model.addElement(rs.getString("DisplayText"));
            ids.add(rs.getInt("ID_suscripcion"));
        }
        
        cmbSus.setModel(model);
        cmbSus.putClientProperty("ids", ids);
        
        if (model.getSize() == 0) {
            cmbSus.setEnabled(false);
            JOptionPane.showMessageDialog(null, 
                "No hay suscripciones registradas.\nDebe crear una suscripción primero.",
                "Sin Suscripciones", JOptionPane.INFORMATION_MESSAGE);
        } else {
            cmbSus.setEnabled(true);
            cmbSus.setSelectedIndex(-1);
        }
        
        rs.close();
        st.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar suscripciones: " + e.toString());
    }
}

    private int getSelectedSuscripcionId() {
        int idx = cmbSus.getSelectedIndex();
        if (idx >= 0) {
            @SuppressWarnings("unchecked")
            java.util.ArrayList<Integer> ids = (java.util.ArrayList<Integer>) cmbSus.getClientProperty("ids");
            return ids.get(idx);
        }
        return -1;
    }
    private void cargarMembresias() {
    Conexion objetoConexion = new Conexion();
    DefaultTableModel modelo = new DefaultTableModel();
    
    modelo.addColumn("ID Membresía");
    modelo.addColumn("ID Suscripción");
    modelo.addColumn("Suscripción");
    modelo.addColumn("Nombre Membresía");
    modelo.addColumn("Precio");
    modelo.addColumn("Duración");
    modelo.addColumn("Fecha Inicio");
    modelo.addColumn("Fecha Fin");
    modelo.addColumn("Estado");
    
    tbMem.setModel(modelo);
    
    // Ocultar columna ID Suscripción
    tbMem.getColumnModel().getColumn(1).setMinWidth(0);
    tbMem.getColumnModel().getColumn(1).setMaxWidth(0);
    tbMem.getColumnModel().getColumn(1).setWidth(0);
    
    String sql = 
        "SELECT " +
        "    m.ID_membresia, " +
        "    m.ID_suscripcion, " +
        "    a.Nombre_Artista || ' - Suscriptor: ' || f.NombreFan AS Suscripcion, " +
        "    m.NombreMembresia, " +
        "    m.Precio, " +
        "    m.Duracion, " +
        "    m.Fecha_inicio, " +
        "    m.Fecha_fin, " +
        "    m.Estado " +
        "FROM Adquisicion.Membresia m " +
        "INNER JOIN Adquisicion.Suscripcion s ON m.ID_suscripcion = s.ID_suscripcion " +
        "INNER JOIN Usuario.Fan f ON s.ID_fan = f.ID_fan " +
        "INNER JOIN Usuario.Artista a ON s.ID_artista = a.ID_artista " +
        "ORDER BY m.ID_membresia DESC";
    
    try (Connection conn = objetoConexion.establecerConexion();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
        
        while (rs.next()) {
            Object[] datos = new Object[9];
            datos[0] = rs.getLong("ID_membresia");
            datos[1] = rs.getLong("ID_suscripcion");
            datos[2] = rs.getString("Suscripcion");
            datos[3] = rs.getString("NombreMembresia");
            datos[4] = rs.getBigDecimal("Precio");
            datos[5] = rs.getString("Duracion");
            datos[6] = rs.getDate("Fecha_inicio");
            datos[7] = rs.getDate("Fecha_fin");
            datos[8] = rs.getString("Estado");
            modelo.addRow(datos);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar membresías: " + e.toString());
    }
}
     private void cargarTiposMembresia() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("SuperFan");
        model.addElement("VIP");
        cmbMem.setModel(model);
        cmbMem.setSelectedIndex(-1);
    }
      private void cargarDuraciones() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Mensual");
        model.addElement("Anual");
        cmbDur.setModel(model);
        cmbDur.setSelectedIndex(0);
    }
       private double calcularPrecio() {
        if (cmbMem.getSelectedIndex() == -1 || cmbDur.getSelectedIndex() == -1) {
            return 0;
        }
        
        String tipoMembresia = cmbMem.getSelectedItem().toString();
        String duracion = cmbDur.getSelectedItem().toString();
        
        if (tipoMembresia.equals("SuperFan")) {
            return duracion.equals("Mensual") ? 200 : 2000;
        } else if (tipoMembresia.equals("VIP")) {
            return duracion.equals("Mensual") ? 350 : 3800;
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbMem = new javax.swing.JComboBox<>();
        cmbSus = new javax.swing.JComboBox<>();
        cmbDur = new javax.swing.JComboBox<>();
        btnMod = new javax.swing.JButton();
        btnAlta = new javax.swing.JButton();
        btnBaja = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbMem = new javax.swing.JTable();

        setTitle("Membresía");

        jPanelArt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelArt.setPreferredSize(new java.awt.Dimension(330, 0));

        jLabel2.setText("Suscripción ");

        jLabel3.setText("Nombre Membresia");

        jLabel4.setText("Duración");

        cmbSus.addActionListener(this::cmbSusActionPerformed);

        cmbDur.addActionListener(this::cmbDurActionPerformed);

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
                    .addComponent(jLabel3)
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(btnAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMod)))
                .addContainerGap(34, Short.MAX_VALUE))
            .addComponent(cmbDur, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(cmbSus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cmbMem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelArtLayout.setVerticalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbSus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbMem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbDur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAlta)
                    .addComponent(btnBaja)
                    .addComponent(btnMod))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tabla Membresía", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

        tbMem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbMem);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
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
                .addComponent(jPanelArt, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void cmbSusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSusActionPerformed

    private void cmbDurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDurActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbDurActionPerformed
 private void limpiarCampos() {
        cmbMem.setSelectedIndex(-1);
        cmbDur.setSelectedIndex(0);
        cmbSus.setSelectedIndex(-1);
    }
    private void tbMemMouseClicked(java.awt.event.MouseEvent evt) {
        int fila = tbMem.getSelectedRow();
        if (fila >= 0) {
            // Obtener ID de suscripción
            long idSuscripcion = Long.parseLong(tbMem.getValueAt(fila, 1).toString());
            
            // Seleccionar en cmbSus
            @SuppressWarnings("unchecked")
            java.util.ArrayList<Integer> ids = (java.util.ArrayList<Integer>) cmbSus.getClientProperty("ids");
            for (int i = 0; i < ids.size(); i++) {
                if (ids.get(i) == idSuscripcion) {
                    cmbSus.setSelectedIndex(i);
                    break;
                }
            }
            
            // Seleccionar tipo de membresía
            String nombreMembresia = tbMem.getValueAt(fila, 3).toString();
            cmbMem.setSelectedItem(nombreMembresia);
            
            // Seleccionar duración
            String duracion = tbMem.getValueAt(fila, 5).toString();
            cmbDur.setSelectedItem(duracion);
        }
    }
    
    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
        // TODO add your handling code here:
        int fila = tbMem.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una membresía de la lista para poder modificarla.", 
                    "Ninguna Selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Validar que la suscripción seleccionada coincida con la membresía
        long idSuscripcionMembresia = Long.parseLong(tbMem.getValueAt(fila, 1).toString());
        int idSuscripcionSeleccionada = getSelectedSuscripcionId();
        
        if (idSuscripcionSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione la suscripción correspondiente en el campo superior.", 
                    "Validación Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (idSuscripcionMembresia != idSuscripcionSeleccionada) {
            JOptionPane.showMessageDialog(null, "OPERACIÓN NO PERMITIDA\n" +
                "La membresía seleccionada pertenece a una suscripción diferente.\n" +
                "Para modificar esta membresía, debe seleccionar la suscripción:\n" +
                tbMem.getValueAt(fila, 2).toString(),
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (cmbMem.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione el tipo de membresía.", 
                    "Tipo Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        long id = Long.parseLong(tbMem.getValueAt(fila, 0).toString());
        String nombreActual = tbMem.getValueAt(fila, 3).toString();
        String nombreNuevo = cmbMem.getSelectedItem().toString();
        double precioActual = Double.parseDouble(tbMem.getValueAt(fila, 4).toString());
        double precioNuevo = calcularPrecio();
        String duracionActual = tbMem.getValueAt(fila, 5).toString();
        String duracionNueva = cmbDur.getSelectedItem().toString();
        
        // Validar downgrade
        if (nombreActual.equals("VIP") && nombreNuevo.equals("SuperFan")) {
            JOptionPane.showMessageDialog(null, 
                "No se puede cambiar de VIP a SuperFan.\n\nSolo se permiten upgrades (SuperFan → VIP).",
                "Downgrade No Permitido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (precioNuevo < precioActual) {
            JOptionPane.showMessageDialog(null, 
                "No se puede reducir el precio de la membresía.\n\nSolo se permiten upgrades (aumentos de precio).",
                "Downgrade No Permitido", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Conexion objetoConexion = new Conexion();
        try (Connection conn = objetoConexion.establecerConexion()) {
            String sql = "UPDATE Adquisicion.Membresia SET NombreMembresia=?, Precio=?, Duracion=? WHERE ID_membresia=?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombreNuevo);
            ps.setDouble(2, precioNuevo);
            ps.setString(3, duracionNueva);
            ps.setLong(4, id);
            
            ps.executeUpdate();
            
            String mensaje = "Los datos de la membresía se han actualizado correctamente.";
            if (precioNuevo > precioActual) {
                mensaje += "\n\nIMPORTANTE: Se requiere procesar un pago adicional de $" + (precioNuevo - precioActual);
            }
            JOptionPane.showMessageDialog(null, mensaje);
            cargarMembresias();
            limpiarCampos();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudieron actualizar los datos.\nDetalle técnico: " + e.toString(), 
                    "Error al Actualizar", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnModActionPerformed

    private void btnAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaActionPerformed
        // TODO add your handling code here:
        int idSuscripcion = getSelectedSuscripcionId();
        if (idSuscripcion == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una suscripción.", 
                    "Suscripción Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (cmbMem.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione el tipo de membresía.", 
                    "Tipo Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double precioCalculado = calcularPrecio();
        if (precioCalculado <= 0) {
            JOptionPane.showMessageDialog(null, "Error al calcular el precio de la membresía.", 
                    "Error de Cálculo", JOptionPane.WARNING_MESSAGE);
            return;
            }
        
        Conexion objetoConexion = new Conexion();
        try (Connection conn = objetoConexion.establecerConexion()) {
            // Validar si ya existe una membresía para esta suscripción
            String valSql = "SELECT COUNT(*) FROM Adquisicion.Membresia WHERE ID_suscripcion = ?;";
            PreparedStatement psVal = conn.prepareStatement(valSql);
            psVal.setInt(1, idSuscripcion);
            ResultSet rs = psVal.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Ya existe una membresía registrada para esta suscripción.\n" +
                    "Cada suscripción solo puede tener una membresía activa.", 
                    "Registro Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Insertar nueva membresía (el trigger se encarga de Fecha_fin y Estado)
            String sql = "INSERT INTO Adquisicion.Membresia " +
                "(ID_suscripcion, NombreMembresia, Precio, Duracion, Fecha_inicio) " +
                "VALUES (?, ?, ?, ?, CURRENT_DATE);";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idSuscripcion);
            ps.setString(2, cmbMem.getSelectedItem().toString());
            ps.setDouble(3, precioCalculado);
            ps.setString(4, cmbDur.getSelectedItem().toString());
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "La membresía se ha registrado con éxito.");
            cargarMembresias();
            limpiarCampos();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudo completar el registro.\nDetalle técnico: " + e.toString(), 
                    "Error al Guardar", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAltaActionPerformed

    private void btnBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajaActionPerformed
        // TODO add your handling code here:
        int fila = tbMem.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una membresía de la lista para poder eliminarla.", 
                    "Ninguna Selección", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String nombreMembresia = tbMem.getValueAt(fila, 3).toString();
        String suscripcion = tbMem.getValueAt(fila, 2).toString();
        long id = Long.parseLong(tbMem.getValueAt(fila, 0).toString());
        
        int confirm = JOptionPane.showConfirmDialog(null, 
                "¿Está seguro de que desea eliminar esta membresía?\n\n" +
                "Membresía: " + nombreMembresia + "\n" +
                "Suscripción: " + suscripcion + "\n\n" +
                "ADVERTENCIA: Esta acción no se puede deshacer y se eliminarán también todos los pagos asociados.", 
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Conexion objetoConexion = new Conexion();
            try (Connection conn = objetoConexion.establecerConexion()) {
                conn.setAutoCommit(false);
                
                try {
                    // Eliminar pagos asociados primero
                    String sqlPagos = "DELETE FROM Administrativo.Pago WHERE ID_membresia = ?;";
                    PreparedStatement psPagos = conn.prepareStatement(sqlPagos);
                    psPagos.setLong(1, id);
                    int pagosEliminados = psPagos.executeUpdate();
                    
                    // Eliminar la membresía
                    String sqlMembresia = "DELETE FROM Adquisicion.Membresia WHERE ID_membresia = ?;";
                    PreparedStatement psMembresia = conn.prepareStatement(sqlMembresia);
                    psMembresia.setLong(1, id);
                    psMembresia.executeUpdate();
                    
                    conn.commit();
                    
                    String mensaje = "La membresía se ha eliminado correctamente del sistema.";
                    if (pagosEliminados > 0) {
                        mensaje += "\nSe eliminaron " + pagosEliminados + " pago(s) asociado(s).";
                    }
                    JOptionPane.showMessageDialog(null, mensaje);
                    cargarMembresias();
                    limpiarCampos();
                    
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "No se pudo eliminar la membresía.\nDetalle técnico: " + e.toString(), 
                        "Error al Eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnBajaActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Mem().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlta;
    private javax.swing.JButton btnBaja;
    private javax.swing.JButton btnMod;
    private javax.swing.JComboBox<String> cmbDur;
    private javax.swing.JComboBox<String> cmbMem;
    private javax.swing.JComboBox<String> cmbSus;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbMem;
    // End of variables declaration//GEN-END:variables
}
