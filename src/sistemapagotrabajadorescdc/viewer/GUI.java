package sistemapagotrabajadorescdc.viewer;

import sistemapagotrabajadorescdc.model.Proyecto;
import sistemapagotrabajadorescdc.model.Trabajador;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import sistemapagotrabajadorescdc.controller.SistemaPagoTrabajadoresCDC;
import sistemapagotrabajadorescdc.utils.GestorPersistencia;

public class GUI extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTextArea infoArea;
    private JTextArea historialArea;
    private final SistemaPagoTrabajadoresCDC sistema = new SistemaPagoTrabajadoresCDC();
    
    public GUI() {
        initUI();
    }

    private void initUI() {
        setTitle("Sistema de Pago Trabajadores - CDC S.A");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        crearMenuPrincipal();
        crearPanelMostrarTrabajadores();
        crearPanelResumen();
        crearPanelModificacion();
        crearPanelHistorial();

        add(cardPanel);
        cardLayout.show(cardPanel, "menu");
    }
            
    public static void reiniciarAplicacion(JFrame frameActual) {
        frameActual.dispose();
        SwingUtilities.invokeLater(() -> {
            GUI nuevaInstancia = new GUI();
            nuevaInstancia.setVisible(true);
        });
    }

    private void crearMenuPrincipal() {
        JPanel menuPanel = new JPanel(new BorderLayout());

        JLabel tituloLabel = new JLabel("Sistema de Pago para Trabajadores - CDC S.A", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 24));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(10, 0,0,0));
        menuPanel.add(tituloLabel, BorderLayout.NORTH);

        JPanel botonesPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JButton btnMostrar = new JButton("Mostrar trabajadores");
        JButton btnResumen = new JButton("Mostrar informe de resumen");
        JButton btnModificar = new JButton("Modificar trabajadores");
        JButton btnHistorial = new JButton("Historial de cambios");
        JButton btnSalir = new JButton("Guardar y salir");

        btnMostrar.addActionListener(e -> cardLayout.show(cardPanel, "mostrar"));
        btnResumen.addActionListener(e -> {
            infoArea.setText(sistema.generarResumenTrabajadores());
            cardLayout.show(cardPanel, "resumen");
        });
        btnModificar.addActionListener(e -> cardLayout.show(cardPanel, "modificar"));
        btnHistorial.addActionListener(e -> {
            historialArea.setText(sistema.obtenerHistorialCambios());
            cardLayout.show(cardPanel, "historial");
        });
        btnSalir.addActionListener(e -> System.exit(0));

        eventoOyenteDeRaton(btnHistorial);
        eventoOyenteDeRaton(btnSalir);
        eventoOyenteDeRaton(btnMostrar);
        eventoOyenteDeRaton(btnResumen);
        eventoOyenteDeRaton(btnModificar);

        botonesPanel.add(btnMostrar);
        botonesPanel.add(btnResumen);
        botonesPanel.add(btnModificar);
        botonesPanel.add(btnHistorial);
        botonesPanel.add(btnSalir);

        menuPanel.add(botonesPanel, BorderLayout.CENTER);
        cardPanel.add(menuPanel, "menu");
    }

    private void crearPanelMostrarTrabajadores() {
        JPanel panel = new JPanel(new BorderLayout());
        
        if (sistema.getTrabajadores().isEmpty()){
            infoArea = new JTextArea();
            infoArea.setEditable(false);
            infoArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
            infoArea.setText("No hay trabajadores registrados en el sistema.");
            panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
            
            JPanel navPanel = new JPanel();
            JButton btnVolver = new JButton("Volver al menú");
            btnVolver.addActionListener(e -> reiniciarAplicacion(this));
            eventoOyenteDeRaton(btnVolver);
            navPanel.add(btnVolver);
            panel.add(navPanel, BorderLayout.NORTH);
            
            cardPanel.add(panel, "mostrar");
            return;
        }

        JPanel navPanel = new JPanel();
        JButton btnVolver = new JButton("Volver al menú");
        JButton btnResetBD = new JButton("Eliminar Base de Ddatos");
        btnVolver.addActionListener(e -> reiniciarAplicacion(this));
        btnResetBD.addActionListener(e -> {
                int confirmacion = JOptionPane.showConfirmDialog(null,
                "¿Está seguro que desea eliminar la Base de Datos?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
                    try {
                      GestorPersistencia.resetBaseDatos();
                     JOptionPane.showMessageDialog(null, 
                    "Base de Datos eliminada con éxito",
                    "Eliminación exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                        reiniciarAplicacion(this);
                    }
                    catch (IOException E){
                    JOptionPane.showMessageDialog(null,
                            "Error al eliminar la Base de Datos: " + E.getMessage(),
                            "Eliminación fallida",
                            JOptionPane.ERROR_MESSAGE);                        
                    }
                }
        }
        );
        eventoOyenteDeRaton(btnVolver);
        eventoOyenteDeRaton(btnResetBD);       
        navPanel.add(btnVolver);
        navPanel.add(btnResetBD);
        panel.add(navPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        Map<String, List<Trabajador>> trabajadoresPorDepto = sistema.agruparTrabajadoresPorDepartamento();

        for (Map.Entry<String, List<Trabajador>> entry : trabajadoresPorDepto.entrySet()) {
            String departamento = entry.getKey();
            List<Trabajador> trabajadoresDepto = entry.getValue();

            JPanel deptoPanel = new JPanel(new BorderLayout());
            JPanel sinDerechoPanel = new JPanel();
            sinDerechoPanel.setLayout(new BoxLayout(sinDerechoPanel, BoxLayout.Y_AXIS)); // Distribución de componentes de arriba a abajo
            sinDerechoPanel.setBorder(BorderFactory.createTitledBorder("Trabajadores sin derecho a cobro"));

            trabajadoresDepto.stream()
                .filter(t -> !t.tieneDerechoACobro())
                .forEach(t -> sinDerechoPanel.add(crearPanelTrabajador(t)));

            if (sinDerechoPanel.getComponentCount() == 0) {
                sinDerechoPanel.add(new JLabel("No hay trabajadores sin derecho a cobro."));
            }

            JPanel conDerechoPanel = new JPanel();
            conDerechoPanel.setLayout(new BoxLayout(conDerechoPanel, BoxLayout.Y_AXIS)); // Distribución de componentes de arriba a abajo
            conDerechoPanel.setBorder(BorderFactory.createTitledBorder("Trabajadores con derecho a cobro"));

            trabajadoresDepto.stream()
                .filter(Trabajador::tieneDerechoACobro)
                .forEach(t -> conDerechoPanel.add(crearPanelTrabajador(t)));

            if (conDerechoPanel.getComponentCount() == 0) {
                conDerechoPanel.add(new JLabel("No hay trabajadores con derecho a cobro."));
            }

            JPanel contenidoDepto = new JPanel(new GridLayout(2, 1));
            contenidoDepto.add(sinDerechoPanel);
            contenidoDepto.add(conDerechoPanel);

            deptoPanel.add(new JScrollPane(contenidoDepto), BorderLayout.CENTER);
            tabbedPane.addTab(departamento, deptoPanel);
        }

        panel.add(tabbedPane, BorderLayout.CENTER);
        cardPanel.add(panel, "mostrar");
    }

    private JPanel crearPanelTrabajador(Trabajador t) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));

        JPanel infoPanel = new JPanel(new GridLayout(1, 4));
        infoPanel.add(new JLabel("Nombre: " + t.getNombre()));
        infoPanel.add(new JLabel("Código: " + t.getCodigo()));
        infoPanel.add(new JLabel("Cargo: " + t.getCargo()));
        infoPanel.add(new JLabel("Pago total: " + t.getPagoTotal() + " CUP"));

        JButton btnDetalles = new JButton("Ver detalles");
        btnDetalles.addActionListener(e -> mostrarDetallesTrabajador(t));
        eventoOyenteDeRaton(btnDetalles);

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(btnDetalles, BorderLayout.EAST);

        return panel;
    }

    private void mostrarDetallesTrabajador(Trabajador t) {
        JDialog dialog = new JDialog(this, "Detalles de " + t.getNombre(), false);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);

        JTextArea detallesArea = new JTextArea();
        detallesArea.setEditable(false);
        detallesArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        StringBuilder sb = new StringBuilder();
        t.mostrarInformacionCompleta(sb);
        t.mostrarEstadoPago(sb);

        detallesArea.setText(sb.toString());
        dialog.add(new JScrollPane(detallesArea));
        dialog.setVisible(true);
    }

    private void crearPanelResumen() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel navPanel = new JPanel();
        JButton btnVolver = new JButton("Volver al menú");
        btnVolver.addActionListener(e -> reiniciarAplicacion(this));
        eventoOyenteDeRaton(btnVolver);
        navPanel.add(btnVolver);
        if (!sistema.getTrabajadores().isEmpty()){
        JButton btnExportar = new JButton("Exportar Resumen");
        btnExportar.addActionListener(e -> sistema.imprimirResumen());
        navPanel.add(btnExportar);
        eventoOyenteDeRaton(btnExportar);
        }
        panel.add(navPanel, BorderLayout.NORTH);

        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        cardPanel.add(panel, "resumen");
    }

    private void crearPanelModificacion() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel navPanel = new JPanel();
        JButton btnVolver = new JButton("Volver al menú");
        btnVolver.addActionListener(e -> reiniciarAplicacion(this));
        eventoOyenteDeRaton(btnVolver);
        navPanel.add(btnVolver);
        panel.add(navPanel, BorderLayout.NORTH);

        JPanel opcionesPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        opcionesPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JButton btnAgregar = new JButton("Agregar trabajador");
        JButton btnModificar = new JButton("Modificar trabajador");
        JButton btnEliminar = new JButton("Eliminar trabajador");

        btnAgregar.addActionListener(e -> mostrarDialogoAgregarTrabajador());
        btnModificar.addActionListener(e -> mostrarDialogoModificarTrabajador());
        btnEliminar.addActionListener(e -> mostrarDialogoEliminarTrabajador());

        eventoOyenteDeRaton(btnAgregar);
        eventoOyenteDeRaton(btnModificar);
        eventoOyenteDeRaton(btnEliminar);

        opcionesPanel.add(btnAgregar);
        opcionesPanel.add(btnModificar);
        opcionesPanel.add(btnEliminar);
        panel.add(opcionesPanel, BorderLayout.CENTER);
        cardPanel.add(panel, "modificar");
    }

    private void mostrarDialogoAgregarTrabajador() {
        JDialog dialog = new JDialog(this, "Agregar Trabajador", true);
        dialog.setSize(600, 700);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campos del formulario
        JTextField txtDepto = new JTextField();
        JTextField txtCodigo = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtCargo = new JTextField();
        JTextField txtGrupoEscala = new JTextField();
        JTextField txtOcupBarco = new JTextField();
        JCheckBox chkBaja = new JCheckBox();
        JCheckBox chkClave271 = new JCheckBox();
        JCheckBox chkClave278 = new JCheckBox();
        JSpinner spnClave269 = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));

        // Proyectos
        DefaultListModel<Proyecto> proyectosModel = new DefaultListModel<>();
        JList<Proyecto> proyectosList = new JList<>(proyectosModel);
        JButton btnAgregarProyecto = new JButton("Agregar proyecto");
        JButton btnEliminarProyecto = new JButton("Eliminar proyecto");

        btnAgregarProyecto.addActionListener(e -> {
            JDialog proyectoDialog = new JDialog(dialog, "Agregar Proyecto", true);
            proyectoDialog.setSize(400, 300);
            proyectoDialog.setLayout(new GridLayout(0, 2, 10, 10));

            JTextField txtNumProyecto = new JTextField();
            JCheckBox chkCertificado = new JCheckBox();
            JTextField txtHorasTrab = new JTextField();
            JTextField txtHorasExtra = new JTextField();

            proyectoDialog.add(new JLabel("Número de proyecto:"));
            proyectoDialog.add(txtNumProyecto);
            proyectoDialog.add(new JLabel("Certificado:"));
            proyectoDialog.add(chkCertificado);
            proyectoDialog.add(new JLabel("Horas trabajadas:"));
            proyectoDialog.add(txtHorasTrab);
            proyectoDialog.add(new JLabel("Horas extra:"));
            proyectoDialog.add(txtHorasExtra);

            JButton btnGuardarProyecto = new JButton("Guardar");
            btnGuardarProyecto.addActionListener(ev -> {
                try {
                    int numProyecto = Integer.parseInt(txtNumProyecto.getText());
                    boolean certificado = chkCertificado.isSelected();
                    double horasTrab = Double.parseDouble(txtHorasTrab.getText());
                    double horasExtra = Double.parseDouble(txtHorasExtra.getText());

                    proyectosModel.addElement(new Proyecto(numProyecto, certificado, horasTrab, horasExtra));
                    proyectoDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(proyectoDialog, "Por favor ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            proyectoDialog.add(btnGuardarProyecto);
            proyectoDialog.setVisible(true);
        });

        eventoOyenteDeRaton(btnAgregarProyecto);

        btnEliminarProyecto.addActionListener(e -> {
            int selectedIndex = proyectosList.getSelectedIndex();
            if (selectedIndex != -1) {
                proyectosModel.remove(selectedIndex);
            }
            else {
                JOptionPane.showMessageDialog(null, "Por favor seleccione el proyecto a eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        eventoOyenteDeRaton(btnEliminarProyecto);

        // Agregar campos al formulario
        formPanel.add(new JLabel("Departamento:"));
        formPanel.add(txtDepto);
        formPanel.add(new JLabel("Código:"));
        formPanel.add(txtCodigo);
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Cargo:"));
        formPanel.add(txtCargo);
        formPanel.add(new JLabel("Grupo Escala:"));
        formPanel.add(txtGrupoEscala);
        formPanel.add(new JLabel("Ocupación Barco:"));
        formPanel.add(txtOcupBarco);
        formPanel.add(new JLabel("Es baja:"));
        formPanel.add(chkBaja);
        formPanel.add(new JLabel("Tiene clave 271:"));
        formPanel.add(chkClave271);
        formPanel.add(new JLabel("Tiene clave 278:"));
        formPanel.add(chkClave278);
        formPanel.add(new JLabel("Cantidad claves 269:"));
        formPanel.add(spnClave269);
        formPanel.add(new JLabel("Proyectos:"));
        formPanel.add(new JScrollPane(proyectosList));
        formPanel.add(btnAgregarProyecto);
        formPanel.add(btnEliminarProyecto);

        // Botones de acción
        JPanel buttonPanel = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            try {
                // Validar campos obligatorios
                if (txtDepto.getText().isEmpty() || txtCodigo.getText().isEmpty() ||
                        txtNombre.getText().isEmpty() || txtCargo.getText().isEmpty() ||
                        txtGrupoEscala.getText().isEmpty() || txtOcupBarco.getText().isEmpty()) {
                    throw new IllegalArgumentException("Todos los campos son obligatorios");
                }

                // Validar Ocupación Barco
                if ((!txtOcupBarco.getText().trim().equalsIgnoreCase("DA")) && (!txtOcupBarco.getText().trim().equalsIgnoreCase("D"))){
                    throw new IllegalArgumentException("Ocupación Barco incorrecta. Debe introducir 'D' o 'DA'");
                }

                // Verificar nombre y código únicos
                int codigo = Integer.parseInt(txtCodigo.getText());
                String nombre = txtNombre.getText();
                for (Trabajador t : sistema.getTrabajadores()) {
                    if (t.getCodigo() == codigo) {
                        throw new IllegalArgumentException("Ya existe un trabajador con el código " + codigo);
                    }
                    else if (t.getNombre().equalsIgnoreCase(nombre)) {
                            throw new IllegalArgumentException("Ya existe un trabajador con ese nombre");
                        }
                }

                // Crear lista de proyectos
                ArrayList<Proyecto> proyectos = new ArrayList<>();
                for (int i = 0; i < proyectosModel.size(); i++) {
                    proyectos.add(proyectosModel.getElementAt(i));
                }

                // Crear nuevo trabajador
                Trabajador nuevo = new Trabajador(
                        txtGrupoEscala.getText(),
                        txtCargo.getText(),
                        txtDepto.getText(),
                        codigo,
                        txtNombre.getText(),
                        txtOcupBarco.getText(),
                        proyectos,
                        chkBaja.isSelected(),
                        chkClave271.isSelected(),
                        chkClave278.isSelected(),
                        (Integer)spnClave269.getValue() > 0
                );

                nuevo.setCantidadClaves269((Integer)spnClave269.getValue());

                // Validar tarifa antes de crear el trabajador
                String grupoEscala = txtGrupoEscala.getText();
                String ocupBarco = txtOcupBarco.getText().trim();

                // Llamada de prueba para validar
                int tarifaPrueba = nuevo.calcularTarifa(grupoEscala, ocupBarco);
                if (tarifaPrueba == 0) {
                    throw new IllegalArgumentException("Error: Combinación de Grupo Escala y Ocupación Barco no válida. Verifique los datos del trabajador");
                }

                sistema.agregarTrabajador(nuevo);

                JOptionPane.showMessageDialog(dialog, "Trabajador agregado exitosamente");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Por favor ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        eventoOyenteDeRaton(btnCancelar);
        eventoOyenteDeRaton(btnGuardar);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void mostrarDialogoModificarTrabajador() {
        if (sistema.getTrabajadores().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay trabajadores registrados en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Seleccionar trabajador
        Trabajador seleccionado = (Trabajador) JOptionPane.showInputDialog(
                null,
                "Seleccione el trabajador a modificar:",
                "Modificar Trabajador",
                JOptionPane.QUESTION_MESSAGE,
                null,
                sistema.getTrabajadores().toArray(),
                null
        );

        if (seleccionado == null) return;

        JDialog dialog = new JDialog(this, "Modificar Trabajador", true);
        dialog.setSize(600, 700);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Campos del formulario
        JTextField txtDepto = new JTextField(seleccionado.getDepto());
        JTextField txtCodigo = new JTextField(String.valueOf(seleccionado.getCodigo()));
        JTextField txtNombre = new JTextField(seleccionado.getNombre());
        JTextField txtCargo = new JTextField(seleccionado.getCargo());
        JTextField txtGrupoEscala = new JTextField(String.valueOf(seleccionado.getGrupoEscala()));
        JTextField txtOcupBarco = new JTextField(seleccionado.getOcupBarco());
        JCheckBox chkBaja = new JCheckBox("", seleccionado.isEsBaja());
        JCheckBox chkClave271 = new JCheckBox("", seleccionado.isTieneClave271());
        JCheckBox chkClave278 = new JCheckBox("", seleccionado.isTieneClave278());
        JSpinner spnClave269 = new JSpinner(new SpinnerNumberModel(seleccionado.getCantidadClaves269(), 0, 10, 1));

        // Proyectos
        DefaultListModel<Proyecto> proyectosModel = new DefaultListModel<>();
        seleccionado.getProyectos().forEach(proyectosModel::addElement);
        JList<Proyecto> proyectosList = new JList<>(proyectosModel);
        JButton btnAgregarProyecto = new JButton("Agregar proyecto");
        JButton btnEliminarProyecto = new JButton("Eliminar proyecto");
        JButton btnModificarProyecto = new JButton("Modificar proyecto");

        btnAgregarProyecto.addActionListener(e -> {
            JDialog proyectoDialog = new JDialog(dialog, "Agregar Proyecto", true);
            proyectoDialog.setSize(400, 300);
            proyectoDialog.setLayout(new GridLayout(0, 2, 10, 10));

            JTextField txtNumProyecto = new JTextField();
            JCheckBox chkCertificado = new JCheckBox();
            JTextField txtHorasTrab = new JTextField();
            JTextField txtHorasExtra = new JTextField();

            proyectoDialog.add(new JLabel("Número de proyecto:"));
            proyectoDialog.add(txtNumProyecto);
            proyectoDialog.add(new JLabel("Certificado:"));
            proyectoDialog.add(chkCertificado);
            proyectoDialog.add(new JLabel("Horas trabajadas:"));
            proyectoDialog.add(txtHorasTrab);
            proyectoDialog.add(new JLabel("Horas extra:"));
            proyectoDialog.add(txtHorasExtra);

            JButton btnGuardarProyecto = new JButton("Guardar");
            btnGuardarProyecto.addActionListener(ev -> {
                try {
                    Proyecto nuevoProyecto = new Proyecto(
                            Integer.parseInt(txtNumProyecto.getText()),
                            chkCertificado.isSelected(),
                            Double.parseDouble(txtHorasTrab.getText()),
                            Double.parseDouble(txtHorasExtra.getText()));
                    seleccionado.getProyectos().add(nuevoProyecto);
                    proyectosModel.addElement(nuevoProyecto);
                    proyectoDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(proyectoDialog, "Por favor ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            eventoOyenteDeRaton(btnGuardarProyecto);
            proyectoDialog.add(btnGuardarProyecto);
            proyectoDialog.setVisible(true);
        });

        btnEliminarProyecto.addActionListener(e -> {
            Proyecto proyectoSeleccionado = proyectosList.getSelectedValue();
            if (proyectoSeleccionado != null) {
                boolean eliminado = seleccionado.getProyectos().remove(proyectoSeleccionado);
                if (eliminado) {
                    proyectosModel.removeElement(proyectoSeleccionado);
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Por favor seleccione el proyecto a eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        eventoOyenteDeRaton(btnEliminarProyecto);

        btnModificarProyecto.addActionListener(e -> {
            int selectedIndex = proyectosList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(null, "Por favor seleccione el proyecto a modificar", "Error", JOptionPane.ERROR_MESSAGE);
            };

            Proyecto proyecto = proyectosModel.getElementAt(selectedIndex);

            JDialog proyectoDialog = new JDialog(dialog, "Modificar Proyecto", true);
            proyectoDialog.setSize(400, 300);
            proyectoDialog.setLayout(new GridLayout(0, 2, 10, 10));

            JTextField txtNumProyecto = new JTextField(String.valueOf(proyecto.getNumeroProyecto()));
            JCheckBox chkCertificado = new JCheckBox("", proyecto.isEsCertificado());
            JTextField txtHorasTrab = new JTextField(String.valueOf(proyecto.getHorasTrab()));
            JTextField txtHorasExtra = new JTextField(String.valueOf(proyecto.getHorasExtra()));

            proyectoDialog.add(new JLabel("Número de proyecto:"));
            proyectoDialog.add(txtNumProyecto);
            proyectoDialog.add(new JLabel("Certificado:"));
            proyectoDialog.add(chkCertificado);
            proyectoDialog.add(new JLabel("Horas trabajadas:"));
            proyectoDialog.add(txtHorasTrab);
            proyectoDialog.add(new JLabel("Horas extra:"));
            proyectoDialog.add(txtHorasExtra);

            JButton btnGuardarProyecto = new JButton("Guardar");
            btnGuardarProyecto.addActionListener(ev -> {
                try {
                    // Actualizar el proyecto existente en lugar de crear uno nuevo
                    proyecto.setEsCertificado(chkCertificado.isSelected());
                    proyecto.setHorasTrab(Double.parseDouble(txtHorasTrab.getText()));
                    proyecto.setHorasExtra(Double.parseDouble(txtHorasExtra.getText()));

                    // Actualizar el modelo de la lista
                    proyectosModel.setElementAt(proyecto, selectedIndex);

                    // Actualizar los cálculos del trabajador
                    seleccionado.setTarifa(seleccionado.calcularTarifa(seleccionado.getGrupoEscala(), seleccionado.getOcupBarco()));
                    seleccionado.setPagoTotal(seleccionado.calcularPagoTotal());

                    proyectoDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(proyectoDialog, "Por favor ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            eventoOyenteDeRaton(btnGuardarProyecto);
            proyectoDialog.add(btnGuardarProyecto);
            proyectoDialog.setVisible(true);
        });

        eventoOyenteDeRaton(btnModificarProyecto);

        // Agregar campos al formulario
        formPanel.add(new JLabel("Departamento:"));
        formPanel.add(txtDepto);
        formPanel.add(new JLabel("Código:"));
        formPanel.add(txtCodigo);
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Cargo:"));
        formPanel.add(txtCargo);
        formPanel.add(new JLabel("Grupo Escala:"));
        formPanel.add(txtGrupoEscala);
        formPanel.add(new JLabel("Ocupación Barco:"));
        formPanel.add(txtOcupBarco);
        formPanel.add(new JLabel("Es baja:"));
        formPanel.add(chkBaja);
        formPanel.add(new JLabel("Tiene clave 271:"));
        formPanel.add(chkClave271);
        formPanel.add(new JLabel("Tiene clave 278:"));
        formPanel.add(chkClave278);
        formPanel.add(new JLabel("Cantidad claves 269:"));
        formPanel.add(spnClave269);
        formPanel.add(new JLabel("Proyectos:"));
        formPanel.add(new JScrollPane(proyectosList));
        formPanel.add(btnAgregarProyecto);
        formPanel.add(btnEliminarProyecto);
        formPanel.add(btnModificarProyecto);

        // Botones de acción
        JPanel buttonPanel = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            try {
                // Validar campos obligatorios
                if (txtDepto.getText().isEmpty() || txtNombre.getText().isEmpty() || txtCodigo.getText().isEmpty() || txtCargo.getText().isEmpty() || txtGrupoEscala.getText().isEmpty() || txtOcupBarco.getText().isEmpty()) {
                    throw new IllegalArgumentException("Todos los campos son obligatorios");
                }

                // Validar Ocupación Barco
                if ((!txtOcupBarco.getText().trim().equalsIgnoreCase("DA")) && (!txtOcupBarco.getText().trim().equalsIgnoreCase("D"))) {
                    throw new IllegalArgumentException("Ocupación Barco incorrecta. Debe introducir 'D' o 'DA'");
                }

                // Validar tarifa antes de modificar el trabajador
                String grupoEscala = txtGrupoEscala.getText();
                String ocupBarco = txtOcupBarco.getText().trim();

                // Llamada de prueba para validar
                int tarifaPrueba = seleccionado.calcularTarifa(grupoEscala, ocupBarco);
                if (tarifaPrueba == 0) {
                    throw new IllegalArgumentException("Error: Combinación de Grupo Escala y Ocupación Barco no válida. Verifique los datos del trabajador");
                }

                // Actualizar trabajador
                seleccionado.setDepto(txtDepto.getText());
                seleccionado.setCodigo(Integer.parseInt(txtCodigo.getText()));
                seleccionado.setNombre(txtNombre.getText());
                seleccionado.setCargo(txtCargo.getText());
                seleccionado.setGrupoEscala(txtGrupoEscala.getText());
                seleccionado.setOcupBarco(txtOcupBarco.getText());
                seleccionado.setEsBaja(chkBaja.isSelected());
                seleccionado.setTieneClave271(chkClave271.isSelected());
                seleccionado.setTieneClave278(chkClave278.isSelected());
                seleccionado.setCantidadClaves269((Integer)spnClave269.getValue());

                // Actualizar proyectos
                ArrayList<Proyecto> nuevosProyectos = new ArrayList<>();
                for (int i = 0; i < proyectosModel.size(); i++) {
                    nuevosProyectos.add(proyectosModel.getElementAt(i));
                }
                seleccionado.setProyectos(nuevosProyectos);

                sistema.modificarTrabajador(seleccionado);

                JOptionPane.showMessageDialog(dialog, "Trabajador modificado exitosamente");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Por favor ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        eventoOyenteDeRaton(btnGuardar);
        eventoOyenteDeRaton(btnCancelar);

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void mostrarDialogoEliminarTrabajador() {
        if (sistema.getTrabajadores().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay trabajadores registrados en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Seleccionar trabajador
        Trabajador seleccionado = (Trabajador) JOptionPane.showInputDialog(
                null,
                "Seleccione el trabajador a eliminar:",
                "Eliminar Trabajador",
                JOptionPane.QUESTION_MESSAGE,
                null,
                sistema.getTrabajadores().toArray(),
                null
        );

        if (seleccionado == null) return;

        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro que desea eliminar al trabajador " + seleccionado.getNombre() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            sistema.eliminarTrabajador(seleccionado);

            JOptionPane.showMessageDialog(null, "Trabajador eliminado exitosamente");
        }
    }

    private void crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel navPanel = new JPanel();
        JButton btnVolver = new JButton("Volver al menú");
        JButton btnLimpiar = new JButton("Limpiar Historial");

        btnVolver.addActionListener(e -> reiniciarAplicacion(this));
        btnLimpiar.addActionListener(e -> {
            sistema.limpiarHistorial();
            historialArea.setText(sistema.obtenerHistorialCambios());
        });

        eventoOyenteDeRaton(btnVolver);
        eventoOyenteDeRaton(btnLimpiar);

        navPanel.add(btnVolver);
        navPanel.add(btnLimpiar);
        panel.add(navPanel, BorderLayout.NORTH);

        historialArea = new JTextArea();
        historialArea.setEditable(false);
        historialArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        panel.add(new JScrollPane(historialArea), BorderLayout.CENTER);

        cardPanel.add(panel, "historial");
    }

    public void eventoOyenteDeRaton(JButton boton) {
        Color colorTextoOriginal = boton.getForeground();
        boton.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setForeground(Color.BLUE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setForeground(colorTextoOriginal);
            }
        });
    }
}