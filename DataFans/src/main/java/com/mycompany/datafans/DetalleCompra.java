/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.datafans;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Jacky09
 */
public class DetalleCompra extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DetalleCompra.class.getName());

    private Conexion varConexion;
    private int idFan;
    private String nombreFan;
    private long idCompraActual = 0;
    private int currentIdProducto = 0;
private double currentPrecio = 0;
private Map<String, ProductoData> mapProductos = new HashMap<>();
    
    
    // Listas paralelas para almacenar los detalles
    private List<Integer> idsProductos = new ArrayList<>();
    private List<String> nombresProductos = new ArrayList<>();
    private List<Integer> cantidades = new ArrayList<>();
    private List<Double> preciosUnitarios = new ArrayList<>();
    private List<Double> subtotales = new ArrayList<>();
    
    private int productoSeleccionadoIndex = -1;
    private CompraFinalizadaListener listener;
   
    public interface CompraFinalizadaListener {
        void onCompraFinalizada(long idCompra, int idFan, String nombreFan, double totalCompra);
    }
    
    public DetalleCompra(int idFan, String nombreFan) {
    this(); // ya agrega listeners y carga productos
    this.idFan = idFan;
    this.nombreFan = nombreFan;
    cmbFan.setVisible(false);
    this.setTitle("Detalle Compra - Fan: " + nombreFan);
    CrearNuevaCompra();
    // NO repetir los addActionListener aquí
}
    /**
     * Creates new form DetalleCompra
     */
    
    
    public void setDatosCompra(long idCompra, int idFan, String nombreFan) {
    this.idCompraActual = idCompra;
    this.idFan = idFan;
    this.nombreFan = nombreFan;
    compre.setEnabled(false);  
    this.setTitle("Detalle Compra - #" + idCompraActual + " - Fan: " + nombreFan);
    CargarDetallesExistentes();
}
    
   public DetalleCompra() {
    initComponents();
    varConexion = new Conexion();
    
    cmbFan.setVisible(false);
    this.setTitle("Detalle Compra");
    
    CargarProductos();
    InicializarTabla();
    // NO llamar CargarDetallesExistentes() aquí

    // Eventos — solo aquí, NO en el constructor con parámetros
    cmbPro.addActionListener(this::cmbProActionPerformed);
    comCan.addChangeListener(this::comCanStateChanged);
    btnAlta.addActionListener(this::btnAltaActionPerformed);
    btnBaja.addActionListener(this::btnBajaActionPerformed);
    btnMod.addActionListener(this::btnModActionPerformed);
    btnFincompra.addActionListener(this::btnFincompraActionPerformed);
    tbDetCompra.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            CargarProductoSeleccionado();
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



    
    public void setOnCompraFinalizada(CompraFinalizadaListener listener) {
        this.listener = listener;
    }
    
    private void InicializarTabla() {
        DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    model.addColumn("Producto");
    model.addColumn("Cantidad");
    model.addColumn("Precio Unit.");
    model.addColumn("Subtotal");

    tbDetCompra.setModel(model);
    tbDetCompra.setAutoCreateColumnsFromModel(true); // ← cambiar a true

    tbDetCompra.getColumnModel().getColumn(0).setPreferredWidth(300);
    tbDetCompra.getColumnModel().getColumn(1).setPreferredWidth(70);
    tbDetCompra.getColumnModel().getColumn(2).setPreferredWidth(100);
    tbDetCompra.getColumnModel().getColumn(3).setPreferredWidth(100);
    }
    private class ProductoData {
    int id;
    double precio;
    int stock;
    
    ProductoData(int id, double precio, int stock) {
        this.id = id;
        this.precio = precio;
        this.stock = stock;
    }
}
 
    
    private void CargarProductos() {
         String query = 
        "SELECT p.ID_producto, p.Nombre_producto, p.Precio, p.Stock, " +
        "a.Nombre_artista, p.Tipo_producto, " +
        "a.Nombre_artista || ' - (' || COALESCE(p.Tipo_producto, 'General') || ') - ' || p.Nombre_producto AS ProductoDisplay " +
        "FROM Adquisicion.Producto p " +
        "INNER JOIN Usuario.Artista a ON p.ID_artista = a.ID_artista " +
        "WHERE p.Stock > 0 " +
        "ORDER BY a.Nombre_artista, p.Tipo_producto, p.Nombre_producto";
    
    try (Connection conexion = varConexion.establecerConexion();
         Statement stmt = conexion.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {
        
        cmbPro.removeAllItems();
        mapProductos.clear();  // ← Limpiar el Map
        
        while (rs.next()) {
            String display = rs.getString("ProductoDisplay");
            cmbPro.addItem(display);
            // ← Guardar en el Map
            mapProductos.put(display, new ProductoData(
                rs.getInt("ID_producto"),
                rs.getDouble("Precio"),
                rs.getInt("Stock")
            ));
        }
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        logger.log(Level.SEVERE, null, ex);
    }
}
    
    private void CargarDetallesEnGrid() {
        DefaultTableModel model = (DefaultTableModel) tbDetCompra.getModel();
        model.setRowCount(0);
        
        double totalCompra = 0;
        
        for (int i = 0; i < nombresProductos.size(); i++) {
            model.addRow(new Object[]{
                nombresProductos.get(i),
                cantidades.get(i),
                String.format("$%.2f", preciosUnitarios.get(i)),
                String.format("$%.2f", subtotales.get(i))
            });
            totalCompra += subtotales.get(i);
        }
        
        totalfin.setText(String.format("$%.2f", totalCompra));
    }
    
    private void CargarDetallesExistentes() {
        String query = 
            "SELECT dc.ID_producto, dc.Cantidad, dc.Subtotal, " +
            "a.Nombre_artista || ' - (' || COALESCE(p.Tipo_producto, 'General') || ') - ' || p.Nombre_producto AS ProductoDisplay " +
            "FROM Administrativo.DetalleCompra dc " +
            "INNER JOIN Adquisicion.Producto p ON dc.ID_producto = p.ID_producto " +
            "INNER JOIN Usuario.Artista a ON p.ID_artista = a.ID_artista " +
            "WHERE dc.ID_compra = ?";
        
        try (Connection conexion = varConexion.establecerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(query)) {
            
            pstmt.setLong(1, idCompraActual);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int cantidad = rs.getInt("Cantidad");
                double subtotal = rs.getDouble("Subtotal");
                double precioUnitario = subtotal / cantidad;
                
                idsProductos.add(rs.getInt("ID_producto"));
                nombresProductos.add(rs.getString("ProductoDisplay"));
                cantidades.add(cantidad);
                preciosUnitarios.add(precioUnitario);
                subtotales.add(subtotal);
            }
            
            CargarDetallesEnGrid();
            
            double totalCompra = 0;
            for (double subtotal : subtotales) {
                totalCompra += subtotal;
            }
            totalfin.setText(String.format("$%.2f", totalCompra));
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles existentes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void CargarProductoSeleccionado() {
        int selectedRow = tbDetCompra.getSelectedRow();
    if (selectedRow >= 0 && selectedRow < nombresProductos.size()) {
        productoSeleccionadoIndex = selectedRow;
        
        // Seleccionar el producto en el ComboBox por nombre
        String nombreProducto = nombresProductos.get(selectedRow);
        cmbPro.setSelectedItem(nombreProducto);
        
        // Cargar la cantidad actual en el spinner de cantidad (EDITABLE)
        comCan.setValue(cantidades.get(selectedRow));
        
        // Cargar el precio en el spinner de precio (NO EDITABLE - solo mostrar)
        compre.setValue(preciosUnitarios.get(selectedRow));
        
        // Cambiar modo de los botones
        btnAlta.setText("Agregar Producto");
        btnAlta.setEnabled(false);  // Deshabilitar Alta mientras se modifica
        btnMod.setVisible(true);
        btnMod.setEnabled(true);
        
        // Cambiar color para indicar modo modificación
        totalfin.setForeground(Color.BLUE);
    }
}
 private ProductoData getProductoDataSeleccionado() {
    String selected = (String) cmbPro.getSelectedItem();
    if (selected != null) {
        return mapProductos.get(selected);
    }
    return null;
}   
    private void CalcularTotal() {
        int cantidad = (Integer) comCan.getValue();
        double precio = (Double) compre.getValue();
        double total = cantidad * precio;
        totalfin.setText(String.format("$%.2f", total));
    }
    
    private void CrearNuevaCompra() {
        String query = "INSERT INTO Administrativo.Compra (ID_fan, Fecha_compra, Total_compra) " +
                       "VALUES (?, CURRENT_TIMESTAMP, 0) RETURNING ID_compra";
        
        try (Connection conexion = varConexion.establecerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(query)) {
            
            pstmt.setInt(1, idFan);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                idCompraActual = rs.getLong(1);
                this.setTitle("Detalle Compra - #" + idCompraActual);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al crear compra: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private int ObtenerStockActual(int idProducto) {
        String query = "SELECT Stock FROM Adquisicion.Producto WHERE ID_producto = ?";
        
        try (Connection conexion = varConexion.establecerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(query)) {
            
            pstmt.setInt(1, idProducto);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("Stock");
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    private void InsertarDetalleEnBD(int idProducto, int cantidad, double subtotal) {
        String query = "INSERT INTO Administrativo.DetalleCompra " +
                       "(ID_compra, ID_producto, Cantidad, Subtotal) " +
                       "VALUES (?, ?, ?, ?)";
        
        try (Connection conexion = varConexion.establecerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(query)) {
            
            pstmt.setLong(1, idCompraActual);
            pstmt.setInt(2, idProducto);
            pstmt.setInt(3, cantidad);
            pstmt.setDouble(4, subtotal);
            pstmt.executeUpdate();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void ActualizarDetalleEnBD(int idProducto, int nuevaCantidad, double nuevoSubtotal) {
        String query = "UPDATE Administrativo.DetalleCompra " +
                       "SET Cantidad = ?, Subtotal = ? " +
                       "WHERE ID_compra = ? AND ID_producto = ?";
        
        try (Connection conexion = varConexion.establecerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(query)) {
            
            pstmt.setInt(1, nuevaCantidad);
            pstmt.setDouble(2, nuevoSubtotal);
            pstmt.setLong(3, idCompraActual);
            pstmt.setInt(4, idProducto);
            pstmt.executeUpdate();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void EliminarDetalleDeBD(int idProducto) {
        String query = "DELETE FROM Administrativo.DetalleCompra " +
                       "WHERE ID_compra = ? AND ID_producto = ?";
        
        try (Connection conexion = varConexion.establecerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(query)) {
            
            pstmt.setLong(1, idCompraActual);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void FinalizarCompra() {
        if (nombresProductos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos en la compra",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double totalCompra = 0;
        for (double subtotal : subtotales) {
            totalCompra += subtotal;
        }
        
        if (listener != null) {
            listener.onCompraFinalizada(idCompraActual, idFan, nombreFan, totalCompra);
        }
        this.dispose();
    }
    
    private void EliminarCompraCompleta() {
        try (Connection conexion = varConexion.establecerConexion()) {
            conexion.setAutoCommit(false);
            
            try (PreparedStatement pstmtDetalle = conexion.prepareStatement(
                    "DELETE FROM Administrativo.DetalleCompra WHERE ID_compra = ?")) {
                pstmtDetalle.setLong(1, idCompraActual);
                pstmtDetalle.executeUpdate();
            }
            
            try (PreparedStatement pstmtCompra = conexion.prepareStatement(
                    "DELETE FROM Administrativo.Compra WHERE ID_compra = ?")) {
                pstmtCompra.setLong(1, idCompraActual);
                pstmtCompra.executeUpdate();
            }
            
            conexion.commit();
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void LimpiarCampos() {
        cmbPro.setSelectedIndex(-1);  // Seleccionar nada
    comCan.setValue(1);           // Resetear cantidad a 1
    compre.setValue(0.0);         // Resetear precio a 0
    totalfin.setText("$0.00");    // Resetear total
    totalfin.setForeground(Color.BLACK);  // Resetear color
    }
    
    private void LimpiarSeleccionProducto() {
       productoSeleccionadoIndex = -1;
    cmbPro.setSelectedIndex(-1);   // Limpiar selección del ComboBox
    comCan.setValue(1);            // Resetear cantidad
    compre.setValue(0.0);          // Resetear precio
    totalfin.setForeground(Color.BLACK);
    btnAlta.setEnabled(true);
    btnAlta.setText("Agregar Producto");  // Restaurar texto
    btnMod.setEnabled(false);
    btnMod.setVisible(true);
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
        tbDetCompra = new javax.swing.JTable();
        jPanelArt = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btnAlta = new javax.swing.JButton();
        btnBaja = new javax.swing.JButton();
        cmbFan = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        cmbPro = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        comCan = new javax.swing.JSpinner();
        compre = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        totalfin = new javax.swing.JLabel();
        btnMod = new javax.swing.JButton();
        btnFincompra = new javax.swing.JButton();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tabla Detalle Compra ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 18))); // NOI18N

        tbDetCompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbDetCompra);

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

        jLabel3.setText("Fan");

        btnAlta.setText("Alta");
        btnAlta.addActionListener(this::btnAltaActionPerformed);

        btnBaja.setText("Baja");
        btnBaja.addActionListener(this::btnBajaActionPerformed);

        cmbFan.addActionListener(this::cmbFanActionPerformed);

        jLabel4.setText("Producto");

        cmbPro.addActionListener(this::cmbProActionPerformed);

        jLabel5.setText("Cantidad");

        jLabel6.setText("Precio Unitario");

        jLabel7.setText("Total:");

        totalfin.setText("$0.00");

        btnMod.setText("Modificación");
        btnMod.addActionListener(this::btnModActionPerformed);

        btnFincompra.setText("Fin Compra");
        btnFincompra.addActionListener(this::btnFincompraActionPerformed);

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
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbPro, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbFan, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(compre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                                .addComponent(comCan, javax.swing.GroupLayout.Alignment.LEADING))))
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(totalfin)))
                .addContainerGap(131, Short.MAX_VALUE))
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addComponent(btnAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFincompra, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelArtLayout.createSequentialGroup()
                        .addComponent(btnBaja, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnMod, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelArtLayout.setVerticalGroup(
            jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArtLayout.createSequentialGroup()
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbFan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comCan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(compre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalfin, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAlta)
                    .addComponent(btnBaja)
                    .addComponent(btnMod))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFincompra)
                .addGap(49, 49, 49))
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

    private void btnAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAltaActionPerformed
        if (cmbPro.getSelectedIndex() == -1) {
        // CAMBIAR: Eliminar el mensaje y simplemente salir silenciosamente
        return;  // ← Ya no muestra el mensaje
    }
    
    int cantidad = (Integer) comCan.getValue();
    if (cantidad <= 0) {
        return;  // ← Ya no muestra mensaje
    }
    
    ProductoData productoData = getProductoDataSeleccionado();
    if (productoData == null) {
        return;  // ← Ya no muestra mensaje
    }
    
    int idProducto = productoData.id;
    String nombreProducto = (String) cmbPro.getSelectedItem();
    double precioUnitario = productoData.precio;
    double subtotal = cantidad * precioUnitario;
    
    int stockDisponible = ObtenerStockActual(idProducto);
    int cantidadExistente = 0;
    int indexExistente = -1;
    
    for (int i = 0; i < idsProductos.size(); i++) {
        if (idsProductos.get(i) == idProducto) {
            cantidadExistente = cantidades.get(i);
            indexExistente = i;
            break;
        }
    }
    
    int nuevaCantidadTotal = cantidadExistente + cantidad;
    
    if (nuevaCantidadTotal > stockDisponible) {
        JOptionPane.showMessageDialog(this,
                "STOCK INSUFICIENTE\n\n" +
                "Producto: " + nombreProducto + "\n" +
                "Stock disponible: " + stockDisponible + "\n" +
                "Ya tiene en compra: " + cantidadExistente + "\n" +
                "Intentó agregar: " + cantidad,
                "Validación de Stock", JOptionPane.WARNING_MESSAGE);
        LimpiarCampos();
        return;
    }
    
    if (indexExistente != -1) {
        int nuevaCantidad = cantidades.get(indexExistente) + cantidad;
        double nuevoSubtotal = nuevaCantidad * preciosUnitarios.get(indexExistente);
        cantidades.set(indexExistente, nuevaCantidad);
        subtotales.set(indexExistente, nuevoSubtotal);
        ActualizarDetalleEnBD(idProducto, nuevaCantidad, nuevoSubtotal);
    } else {
        idsProductos.add(idProducto);
        nombresProductos.add(nombreProducto);
        cantidades.add(cantidad);
        preciosUnitarios.add(precioUnitario);
        subtotales.add(subtotal);
        InsertarDetalleEnBD(idProducto, cantidad, subtotal);
    }
    
    CargarDetallesEnGrid();
    LimpiarCampos();  // ← Esto limpia los campos después de agregar
    
    btnAlta.setText("Agregar Producto");
    
    // ELIMINAR este mensaje también si no lo quieres:
    // JOptionPane.showMessageDialog(this, "Producto agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnAltaActionPerformed

    private void btnBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBajaActionPerformed
        // TODO add your handling code here:
         int selectedRow = tbDetCompra.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto"
               );
            return;
        }
        
        String nombreProducto = nombresProductos.get(selectedRow);
        
        int resultado = JOptionPane.showConfirmDialog(this,
                "¿Eliminar " + nombreProducto + " de la compra?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (resultado == JOptionPane.YES_OPTION) {
            int idProducto = idsProductos.get(selectedRow);
            EliminarDetalleDeBD(idProducto);
            
            idsProductos.remove(selectedRow);
            nombresProductos.remove(selectedRow);
            cantidades.remove(selectedRow);
            preciosUnitarios.remove(selectedRow);
            subtotales.remove(selectedRow);
            
            CargarDetallesEnGrid();
            LimpiarSeleccionProducto();
        }
    }//GEN-LAST:event_btnBajaActionPerformed

    private void cmbFanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbFanActionPerformed

    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
        if (productoSeleccionadoIndex == -1) {
        return;  // ← Eliminar mensaje
    }
    
    if (cmbPro.getSelectedIndex() == -1) {
        return;  // ← Eliminar mensaje
    }
    
    int nuevaCantidad = (Integer) comCan.getValue();
    if (nuevaCantidad <= 0) {
        return;  // ← Eliminar mensaje
    }
    
    int idProducto = idsProductos.get(productoSeleccionadoIndex);
    double precioUnitario = preciosUnitarios.get(productoSeleccionadoIndex);
    double nuevoSubtotal = nuevaCantidad * precioUnitario;
    
    int stockDisponible = ObtenerStockActual(idProducto);
    int cantidadActualEnCompra = 0;
    
    for (int i = 0; i < idsProductos.size(); i++) {
        if (idsProductos.get(i) == idProducto && i != productoSeleccionadoIndex) {
            cantidadActualEnCompra += cantidades.get(i);
        }
    }
    
    int nuevaCantidadTotal = cantidadActualEnCompra + nuevaCantidad;
    
    if (nuevaCantidadTotal > stockDisponible) {
        JOptionPane.showMessageDialog(this,
                "STOCK INSUFICIENTE\n\n" +
                "Stock disponible: " + stockDisponible + "\n" +
                "Ya tiene en compra (sin contar este): " + cantidadActualEnCompra,
                "Validación de Stock", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Actualizar en memoria
    cantidades.set(productoSeleccionadoIndex, nuevaCantidad);
    subtotales.set(productoSeleccionadoIndex, nuevoSubtotal);
    
    // Actualizar en BD
    ActualizarDetalleEnBD(idProducto, nuevaCantidad, nuevoSubtotal);
    
    // Refrescar tabla
    CargarDetallesEnGrid();
    
    // Limpiar selección (esto limpia los campos)
    LimpiarSeleccionProducto();
    
    // ELIMINAR este mensaje:
    // JOptionPane.showMessageDialog(this, "Producto modificado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnModActionPerformed

    private void btnFincompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFincompraActionPerformed
        // TODO add your handling code here:
        if (nombresProductos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos en la compra.\n\n" +
                    "Agregue al menos un producto antes de finalizar.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double totalCompra = 0;
        for (double subtotal : subtotales) {
            totalCompra += subtotal;
        }
        
        int resultado = JOptionPane.showConfirmDialog(this,
                "¿Finalizar compra #" + idCompraActual + "?\n\n" +
                "Fan: " + nombreFan + "\n" +
                "Productos: " + nombresProductos.size() + "\n" +
                "Total a pagar: " + String.format("$%.2f", totalCompra) + "\n\n" +
                "Los productos serán descontados del stock.\n" +
                "¿Desea continuar?",
                "Confirmar Finalización", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (resultado == JOptionPane.YES_OPTION) {
            FinalizarCompra();
        }
    }//GEN-LAST:event_btnFincompraActionPerformed

    private void cmbProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbProActionPerformed
        // TODO add your handling code here:
        if (cmbPro.getSelectedIndex() != -1) {
        ProductoData data = getProductoDataSeleccionado();
        if (data != null) {
            compre.setValue(data.precio);
            CalcularTotal();
        }
    }
    }//GEN-LAST:event_cmbProActionPerformed

    private void comCanStateChanged(javax.swing.event.ChangeEvent evt) {
        CalcularTotal();
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
        java.awt.EventQueue.invokeLater(() -> new DetalleCompra().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlta;
    private javax.swing.JButton btnBaja;
    private javax.swing.JButton btnFincompra;
    private javax.swing.JButton btnMod;
    private javax.swing.JComboBox<String> cmbFan;
    private javax.swing.JComboBox<String> cmbPro;
    private javax.swing.JSpinner comCan;
    private javax.swing.JSpinner compre;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelArt;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbDetCompra;
    private javax.swing.JLabel totalfin;
    // End of variables declaration//GEN-END:variables
}
