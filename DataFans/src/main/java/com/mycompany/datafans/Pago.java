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
public class Pago extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Pago.class.getName());
    private Long idMembresia = null;

    /**
     * Creates new form Pago
     */
    public Pago() {
        initComponents();
        cargarBancos();
        cargarMembresiasEnCombo();
        limpiarInfoMembresia();
        tbPago.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tbPagoMouseClicked(evt);
        }
    });
    }
    private boolean ignorarEventoCombo = false;
    
public Pago(long idMembresiaNueva) {
        initComponents();
    cargarBancos();
    cargarMembresiasEnCombo();  // ← Primero cargar el combo
    limpiarInfoMembresia();
    idMembresia = idMembresiaNueva;
    seleccionarMembresiaEnCombo(idMembresiaNueva);  // ← Luego seleccionar
    cargarInfoMembresia(idMembresiaNueva);
    cargarPagos();
    }

  private void cargarMembresiasEnCombo() {
      ignorarEventoCombo = true;  
      cmbmem.removeAllItems();
        Conexion objetoConexion = new Conexion();
        String sql = 
            "SELECT m.ID_membresia, m.NombreMembresia, a.Nombre_artista, f.NombreFan " +
            "FROM Adquisicion.Membresia m " +
            "INNER JOIN Adquisicion.Suscripcion s ON m.ID_suscripcion = s.ID_suscripcion " +
            "INNER JOIN Usuario.Fan f ON s.ID_fan = f.ID_fan " +
            "INNER JOIN Usuario.Artista a ON s.ID_artista = a.ID_artista " +
            "ORDER BY m.ID_membresia";
        
        try (Connection conn = objetoConexion.establecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                long id = rs.getLong("ID_membresia");
                String tipoMembresia = rs.getString("NombreMembresia");
                String artista = rs.getString("Nombre_artista");
                String fan = rs.getString("NombreFan");
                
                // Formato: TipoMembresia - [Artista] - Suscriptor: Fan
                String displayText = tipoMembresia + " - [" + artista + "] - Suscriptor: " + fan;
                cmbmem.addItem(id + "|" + displayText);
            }
            
            if (cmbmem.getItemCount() > 0) {
                cmbmem.setSelectedIndex(-1);
            }
            if (cmbmem.getItemCount() > 0) {
        cmbmem.setSelectedIndex(-1);
    }
    ignorarEventoCombo = false; 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar membresías: " + e.toString());
        }
    }
private void cargarBancos() {
        cmbBanco1.removeAllItems();
        String[] bancos = {"BBVA", "BANAMEX", "BanBajio", "Banorte", "HSBC",
                           "Scotiabank", "Santander", "Inbursa", "IXE", "Interacciones"};
        for (String b : bancos) {
            cmbBanco1.addItem(b);
        }
        cmbBanco1.setSelectedIndex(-1);
    }
private void cargarInfoMembresia(long idMem) {
        Conexion objetoConexion = new Conexion();
        String sql =
            "SELECT m.ID_membresia, m.NombreMembresia, m.Precio, m.Duracion, " +
            "m.Fecha_inicio, m.Fecha_fin, m.Estado, f.NombreFan, a.Nombre_artista " +
            "FROM Adquisicion.Membresia m " +
            "INNER JOIN Adquisicion.Suscripcion s ON m.ID_suscripcion = s.ID_suscripcion " +
            "INNER JOIN Usuario.Fan f ON s.ID_fan = f.ID_fan " +
            "INNER JOIN Usuario.Artista a ON s.ID_artista = a.ID_artista " +
            "WHERE m.ID_membresia = ?";
     
        try (Connection conn = objetoConexion.establecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
     
            ps.setLong(1, idMem);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String fan = rs.getString("NombreFan");
                    String artista = rs.getString("Nombre_artista");
                    String tipoMembresia = rs.getString("NombreMembresia");
                    
                    // Actualizar lblFan con el formato: TipoMembresia - [Artista] - Suscriptor: Fan
                    lblFan.setText(tipoMembresia + " - [" + artista + "] - Suscriptor: " + fan);
                    lblTipo.setText(rs.getString("NombreMembresia") + " ($" + rs.getString("Precio") + ") - " + rs.getString("Duracion"));
     
                    java.sql.Date fi = rs.getDate("Fecha_inicio");
                    java.sql.Date ff = rs.getDate("Fecha_fin");
                    lblInicio.setText(fi != null ? fi.toString() : "No establecida");
                    lblFin.setText(ff != null ? ff.toString() : "No establecida");
     
                    String estado = rs.getString("Estado");
                    lblEdo.setText(estado);
     
                    if (estado.equals("Activa")) {
                        lblEdo.setForeground(java.awt.Color.GREEN.darker());
                    } else if (estado.equals("Pago Pendiente")) {
                        lblEdo.setForeground(java.awt.Color.ORANGE.darker());
                    } else {
                        lblEdo.setForeground(java.awt.Color.RED);
                    }
     
                    btnAlta.setEnabled(estado.equals("Pago Pendiente"));
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró la membresía con ID: " + idMem);
                    limpiarInfoMembresia();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar membresía: " + e.toString());
            limpiarInfoMembresia();
        }
    }
private void seleccionarMembresiaEnCombo(long idMem) {
        for (int i = 0; i < cmbmem.getItemCount(); i++) {
            String item = cmbmem.getItemAt(i);
            String[] partes = item.split("\\|", 2);
            if (partes.length > 0) {
                try {
                    long id = Long.parseLong(partes[0]);
                    if (id == idMem) {
                        cmbmem.setSelectedIndex(i);
                        break;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            }
        }
    }

private void limpiarInfoMembresia() {
        lblFan.setText("-");
        lblTipo.setText("-");
        lblInicio.setText("-");
        lblFin.setText("-");
        lblEdo.setText("-");
        lblEdo.setForeground(java.awt.Color.BLACK);
        btnAlta.setEnabled(false);
        idMembresia = null;
    }
    
    private void cargarPagos() {
        if (idMembresia == null) {
            tbPago.setModel(new DefaultTableModel());
            return;
        }
     
        Conexion objetoConexion = new Conexion();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Pago");
        modelo.addColumn("Fecha Pago");
        modelo.addColumn("Num. Tarjeta");
        modelo.addColumn("Banco");
        modelo.addColumn("CVV");
        modelo.addColumn("Membresía");
     
        tbPago.setModel(modelo);
     
        String sql =
            "SELECT p.ID_pago, p.Fecha_pago, p.NumTarjeta, p.Banco, p.Cvv, " +
            "m.NombreMembresia || ' - ' || a.Nombre_artista || ' - Suscriptor: ' || f.NombreFan || ' (' || m.Estado || ')' AS MembresiaInfo " +
            "FROM Administrativo.Pago p " +
            "INNER JOIN Adquisicion.Membresia m ON p.ID_membresia = m.ID_membresia " +
            "INNER JOIN Adquisicion.Suscripcion s ON m.ID_suscripcion = s.ID_suscripcion " +
            "INNER JOIN Usuario.Fan f ON s.ID_fan = f.ID_fan " +
            "INNER JOIN Usuario.Artista a ON s.ID_artista = a.ID_artista " +
            "WHERE p.ID_membresia = ? " +
            "ORDER BY p.Fecha_pago DESC";
     
        try (Connection conn = objetoConexion.establecerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
     
            ps.setLong(1, idMembresia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] datos = new Object[6];
                    datos[0] = rs.getLong("ID_pago");
                    datos[1] = rs.getDate("Fecha_pago");
                    datos[2] = rs.getLong("NumTarjeta");
                    datos[3] = rs.getString("Banco");
                    datos[4] = rs.getInt("Cvv");
                    datos[5] = rs.getString("MembresiaInfo");
                    modelo.addRow(datos);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar pagos: " + e.toString());
        }
    }
    private void limpiarCampos() {
        txtTarjeta.setText("");
        txtCvv.setText("");
        //cmbmem.setSelectedIndex(-1);
    }
    private Long getIdMembresiaDesdeCombo() {
        if (cmbmem.getSelectedIndex() == -1) return null;
        String seleccion = cmbmem.getSelectedItem().toString();
        String[] partes = seleccion.split("\\|", 2);
        try {
            return Long.parseLong(partes[0]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private String getDisplayTextDesdeCombo() {
        if (cmbmem.getSelectedIndex() == -1) return "";
        String seleccion = cmbmem.getSelectedItem().toString();
        String[] partes = seleccion.split("\\|", 2);
        if (partes.length > 1) {
            return partes[1];
        }
        return "";
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
        txtTarjeta = new javax.swing.JTextField();
        btnMod = new javax.swing.JButton();
        btnAlta = new javax.swing.JButton();
        btnBaja = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblFan = new javax.swing.JLabel();
        lblTipo = new javax.swing.JLabel();
        lblFin = new javax.swing.JLabel();
        lblInicio = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblEdo = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtCvv = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        cmbBanco1 = new javax.swing.JComboBox<>();
        cmbmem = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbPago = new javax.swing.JTable();

        setTitle("Pago");

        jPanelArt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelArt.setPreferredSize(new java.awt.Dimension(330, 0));

        jLabel2.setText("ID Membresía:");

        jLabel3.setText("Número de Tarjeta:");

        btnMod.setText("Modificación");
        btnMod.addActionListener(this::btnModActionPerformed);

        btnAlta.setText("Alta");
        btnAlta.addActionListener(this::btnAltaActionPerformed);

        btnBaja.setText("Baja");
        btnBaja.addActionListener(this::btnBajaActionPerformed);

        jLabel1.setText("Fan:");

        jLabel4.setText("Información de Membresía");

        jLabel5.setText("Tipo:");

        jLabel6.setText("Fecha Inicio:");

        jLabel7.setText("Fecha Fin:");

        lblFan.setText("-");

        lblTipo.setText("-");

        lblFin.setText("-");

        lblInicio.setText("-");

        jLabel12.setText("Estado: ");

        lblEdo.setText("-");

        jLabel14.setText("CVV: ");

        jLabel15.setText("Banco:");

        cmbBanco1.addActionListener(this::cmbBanco1ActionPerformed);

        cmbmem.addActionListener(this::cmbmemActionPerformed);

        javax.swing.GroupLayout jPanelArtLayout = new javax.swing.GroupLayout(jPanelArt);
        jPanelArt.setLayout(jPanelArtLayout);
        jPanelArtLayout.setHorizontalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbBanco1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCvv, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmbmem, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(btnAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMod)
                        .addContainerGap())
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(lblFin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2))))
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(lblEdo))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTipo))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFan))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblInicio))
                    .addComponent(jLabel12))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelArtLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanelArtLayout.setVerticalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbmem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblFan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lblTipo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(lblInicio))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblFin))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(lblEdo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCvv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbBanco1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMod)
                    .addComponent(btnAlta)
                    .addComponent(btnBaja))
                .addGap(42, 42, 42))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registro Pago", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

        tbPago.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbPago);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelArt, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelArt, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
        // TODO add your handling code here:
        int filaMod = tbPago.getSelectedRow();
        if (filaMod < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un pago de la tabla.");
            return;
        }
        
        if (cmbBanco1.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un banco.");
            return;
        }
        if (txtCvv.getText().trim().isEmpty() || txtCvv.getText().trim().length() != 3) {
            JOptionPane.showMessageDialog(null, "Ingrese un CVV de exactamente 3 dígitos.");
            return;
        }
     
        long idPagoMod = Long.parseLong(tbPago.getValueAt(filaMod, 0).toString());
     
        Conexion objetoConexionMod = new Conexion();
        String sqlMod =
            "UPDATE Administrativo.Pago SET NumTarjeta = ?, Banco = ?, Cvv = ? WHERE ID_pago = ?";
     
        try (Connection conn = objetoConexionMod.establecerConexion();
             PreparedStatement ps = conn.prepareStatement(sqlMod)) {
     
            ps.setLong(1, Long.parseLong(txtTarjeta.getText().trim()));
            ps.setString(2, cmbBanco1.getSelectedItem().toString());
            ps.setInt(3, Integer.parseInt(txtCvv.getText().trim()));
            ps.setLong(4, idPagoMod);
            ps.executeUpdate();
     
            JOptionPane.showMessageDialog(null, "Pago modificado correctamente.");
            cargarInfoMembresia(idMembresia);
            cargarPagos();
            limpiarCampos();
     
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al modificar pago: " + e.toString());
        }
    }//GEN-LAST:event_btnModActionPerformed

    private void btnAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaActionPerformed
        // TODO add your handling code here:
        if (idMembresia == null) {
    idMembresia = getIdMembresiaDesdeCombo();
    if (idMembresia == null) {
        JOptionPane.showMessageDialog(null, "Seleccione una membresía primero.");
        return;
    }
}
        if (txtTarjeta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el número de tarjeta.");
            return;
        }
        if (txtTarjeta.getText().trim().length() != 16) {
            JOptionPane.showMessageDialog(null, "El número de tarjeta debe tener al menos 16 dígitos.");
            return;
        }
        if (cmbBanco1.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un banco.");
            return;
        }
        if (txtCvv.getText().trim().isEmpty() || txtCvv.getText().trim().length() != 3) {
            JOptionPane.showMessageDialog(null, "Ingrese un CVV de exactamente 3 dígitos.");
            return;
        }
     
        Conexion objetoConexionAlta = new Conexion();
        String sqlAlta =
            "INSERT INTO Administrativo.Pago (ID_membresia, Fecha_pago, NumTarjeta, Banco, Cvv) " +
            "VALUES (?, CURRENT_DATE, ?, ?, ?)";
     
       try (Connection conn = objetoConexionAlta.establecerConexion();
     PreparedStatement ps = conn.prepareStatement(sqlAlta)) {
    
    // Asegurar que idMembresia no sea null antes de usarlo
    if (idMembresia == null) {
        JOptionPane.showMessageDialog(null, "Error: ID de membresía no válido.");
        return;
    }
    
    ps.setLong(1, idMembresia);  // Esto ahora funciona porque ya verificamos que no es null
    ps.setLong(2, Long.parseLong(txtTarjeta.getText().trim()));
    ps.setString(3, cmbBanco1.getSelectedItem().toString());
    ps.setInt(4, Integer.parseInt(txtCvv.getText().trim()));
    ps.executeUpdate();
    

long idTemp = idMembresia; // guardar antes

JOptionPane.showMessageDialog(null, "Pago registrado correctamente.\nLa membresía ahora está Activa.");

ignorarEventoCombo = true;  // ← bloquear eventos
cargarInfoMembresia(idTemp);
cargarPagos();
limpiarCampos();
cargarMembresiasEnCombo();
seleccionarMembresiaEnCombo(idTemp);
ignorarEventoCombo = false; // ← reactivar eventos
idMembresia = idTemp;
     
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar pago, tarjeta duplicada ");
        }
    }//GEN-LAST:event_btnAltaActionPerformed

    private void btnBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajaActionPerformed
        // TODO add your handling code here:
        int filaBaja = tbPago.getSelectedRow();
        if (filaBaja < 0) {
            JOptionPane.showMessageDialog(null, "Seleccione un pago de la tabla.");
            return;
        }
     
        long idPagoBaja = Long.parseLong(tbPago.getValueAt(filaBaja, 0).toString());
     
        Conexion objetoConexionBaja = new Conexion();
        try (Connection conn = objetoConexionBaja.establecerConexion()) {
            conn.setAutoCommit(false);
     
            String sqlDelPago = "DELETE FROM Administrativo.Pago WHERE ID_pago = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDelPago)) {
                ps.setLong(1, idPagoBaja);
                ps.executeUpdate();
            }
     
            String sqlUpdMem =
                "UPDATE Adquisicion.Membresia SET Estado = 'Pago Pendiente', " +
                "Fecha_inicio = NULL, Fecha_fin = NULL WHERE ID_membresia = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdMem)) {
                ps.setLong(1, idMembresia);
                ps.executeUpdate();
            }
     
            conn.commit();
            JOptionPane.showMessageDialog(null, "Pago eliminado.\nLa membresía volvió a 'Pago Pendiente'.");
            cargarInfoMembresia(idMembresia);
            cargarPagos();
            limpiarCampos();
     
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar pago: " + e.toString());
        }
    }//GEN-LAST:event_btnBajaActionPerformed

    private void cmbBanco1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBanco1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbBanco1ActionPerformed

    private void cmbmemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbmemActionPerformed
        // TODO add your handling code here:
       if (ignorarEventoCombo) return;  // ← ignorar si lo cambiamos desde código
    Long id = getIdMembresiaDesdeCombo();
    if (id != null) {
        idMembresia = id;
        cargarInfoMembresia(id);
        cargarPagos();
    } else {
        limpiarInfoMembresia();
        cargarPagos();
    }
    }//GEN-LAST:event_cmbmemActionPerformed

    private void tbPagoMouseClicked(java.awt.event.MouseEvent evt) {
        int fila = tbPago.getSelectedRow();
    
    if (fila >= 0) {
        txtTarjeta.setText(tbPago.getValueAt(fila, 2).toString());
        
        String banco = tbPago.getValueAt(fila, 3).toString();
        // Buscar el banco en el combo
        for (int i = 0; i < cmbBanco1.getItemCount(); i++) {
            if (cmbBanco1.getItemAt(i).equals(banco)) {
                cmbBanco1.setSelectedIndex(i);
                break;
            }
        }
        
        txtCvv.setText(tbPago.getValueAt(fila, 4).toString());
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
        java.awt.EventQueue.invokeLater(() -> new Pago().setVisible(true));

      
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlta;
    private javax.swing.JButton btnBaja;
    private javax.swing.JButton btnMod;
    private javax.swing.JComboBox<String> cmbBanco1;
    private javax.swing.JComboBox<String> cmbmem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblEdo;
    private javax.swing.JLabel lblFan;
    private javax.swing.JLabel lblFin;
    private javax.swing.JLabel lblInicio;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JTable tbPago;
    private javax.swing.JTextField txtCvv;
    private javax.swing.JTextField txtTarjeta;
    // End of variables declaration//GEN-END:variables
}
