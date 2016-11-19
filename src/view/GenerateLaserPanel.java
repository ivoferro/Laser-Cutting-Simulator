/*
 * Package location for UI classes.
 */
package view;

import controller.GenerateLaserController;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import model.Gas;
import model.Material;
import model.Simulator;

/**
 * Represents a panel with the fields to generate the laser.
 *
 * @author Eric Amaral 1141570
 * @author Daniel Gonçalves 1151452
 * @author Ivo Ferro 1151159
 * @author Tiago Correia 1151031
 */
public class GenerateLaserPanel extends JPanel {

    /**
     * Inner class to render the gas.
     */
    private static class GasRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            value = ((Gas) value).getName();
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    /**
     * Inner class to render the gas.
     */
    private static class MaterialRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            value = ((Material) value).getName();
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    /**
     * The simulator with all data.
     */
    private final Simulator simulator;

    /**
     * The controller to generate the laser.
     */
    private final GenerateLaserController controller;

    /**
     * The selected wavelength.
     */
    private Double wavelength;

    /**
     * The gases.
     */
    private Set<Gas> gases;

    /**
     * The selected gas.
     */
    private Gas gas;

    /**
     * The combo box for gases.
     */
    private JComboBox<Gas> gasesComboBox;

    /**
     * The selected focal point diameter.
     */
    private Double focalPointDiameter;

    /**
     * The materials.
     */
    private Set<Material> materials;

    /**
     * The selected material.
     */
    private Material material;

    /**
     * Creates an instance of GenerateLaserPanel.
     *
     * @param simulator the simulator with all data
     */
    public GenerateLaserPanel(Simulator simulator) {
        super();

        this.simulator = simulator;
        this.controller = new GenerateLaserController(simulator);
        setDefaultValues();

        createComponents();
    }

    /**
     * Creates the UI components.
     */
    private void createComponents() {
        setLayout(new GridLayout(7, 1, 0, 10));

        add(createWavelengthPanel());
        add(createGasPanel());
        add(createFocalPointPanel());
        add(createMaterialPanel());
        add(createMaterialThickness());
        add(createCalculateButtonPanel());
        add(createMaxPowerPanel());
    }

    /**
     * Creates the panel for wavelength.
     *
     * @return wavelength panel
     */
    private JPanel createWavelengthPanel() {
        JPanel wavelengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        this.wavelength = 10600d;
        this.controller.setWavelength(wavelength * 1e-9);
        JLabel wavelengthLabel = new JLabel(String.format("Comprimento de onda:   %-5.0f nm", wavelength));

        JSlider wavelengthSlider = new JSlider(200, 20000, 10600);
        wavelengthSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                wavelength = (double) wavelengthSlider.getValue();
                controller.setWavelength(wavelength * 1e-9);
                wavelengthLabel.setText(String.format("Comprimento de onda:   %-5.0f nm", wavelength));
                gases = controller.getGasesByWavelength();
                updateGasComboBox();
            }
        });

        wavelengthPanel.add(wavelengthLabel);
        wavelengthPanel.add(wavelengthSlider); //nm

        return wavelengthPanel;
    }

    /**
     * Creates the panel for gas.
     *
     * @return gas panel
     */
    private JPanel createGasPanel() {
        JPanel gasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        gasesComboBox = new JComboBox<>();
        updateGasComboBox();

        gasesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                gas = (Gas) gasesComboBox.getSelectedItem();
                controller.setGas(gas);
            }
        });

        gasPanel.add(new JLabel("Gás:"));
        gasPanel.add(gasesComboBox);

        return gasPanel;
    }

    /**
     * Creastes the panel for focal point.
     *
     * @return focal point panel
     */
    private JPanel createFocalPointPanel() {
        JPanel focalPointPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        focalPointDiameter = 4d;
        JLabel focalPointLabel = new JLabel(String.format("Diâmetro do corte:   %.0f mm", focalPointDiameter));

        JSlider focalPointSlider = new JSlider(1, 8, 4);
        focalPointSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                focalPointDiameter = (double) focalPointSlider.getValue();
                focalPointLabel.setText(String.format("Diâmetro do corte:   %.0f mm", focalPointDiameter));
            }
        });

        focalPointPanel.add(focalPointLabel);
        focalPointPanel.add(focalPointSlider);

        return focalPointPanel;
    }

    /**
     * Creates the panel for material.
     *
     * @return material panel
     */
    private JPanel createMaterialPanel() {
        JPanel materialPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JComboBox<Material> materialComboBox = new JComboBox<>();
        materials.stream().forEach((material) -> {
            materialComboBox.addItem(material);
        });
        materialComboBox.setRenderer(new MaterialRenderer());

        materialComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                material = (Material) materialComboBox.getSelectedItem();
                controller.setMaterial(material);
            }
        });

        materialPanel.add(new JLabel("Material:"));
        materialPanel.add(materialComboBox);

        return materialPanel;
    }

    /**
     * Creates the panel for material thickness.
     * 
     * @return material tickness panel
     */
    private JPanel createMaterialThickness() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        buttonPanel.add(new JLabel("Material Tickness:"));
        buttonPanel.add(new JTextField(6));
        buttonPanel.add(new JLabel("m"));
        
        
        return buttonPanel;
    }
    
    /**
     * Creates the panel for calculate button.
     *
     * @return calculate button panel
     */
    private JPanel createCalculateButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        buttonPanel.add(new JButton("Calcular poder máximo"));

        return buttonPanel;
    }

    /**
     * Creates the panel for max power.
     *
     * @return max power panel
     */
    private JPanel createMaxPowerPanel() {
        JPanel maxPowerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        maxPowerPanel.add(new JLabel("Poder máximo:"));

        return maxPowerPanel;
    }

    /**
     * Sets all fields with default values.
     */
    private void setDefaultValues() {
        this.wavelength = 10600d;
        this.controller.setWavelength(wavelength * 1e-9);
        this.gases = this.controller.getGasesByWavelength();
        this.gas = this.gases.iterator().next();
        this.controller.setGas(gas);
        this.materials = this.controller.getMaterials();
        this.material = this.materials.iterator().next();
        this.controller.setMaterial(material);
        this.focalPointDiameter = 4d;
        this.controller.setFocalPointArea(focalPointDiameter);
    }

    /**
     * Updates the fas combo box.
     */
    private void updateGasComboBox() {
        gasesComboBox.removeAllItems();
        
        Iterator<Gas> gasesIterator = gases.iterator();
        while(gasesIterator.hasNext()) {
            gasesComboBox.addItem(gasesIterator.next());
        }
        
        gasesComboBox.setRenderer(new GasRenderer());
        gas = (Gas) gasesComboBox.getSelectedItem();
        controller.setGas(gas);
    }

    /**
     * Method to test this UI.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Gas g1 = new Gas("gas1", 200e-9, 12000e-9);
        Gas g2 = new Gas("gas2", 800e-9, 20000e-9);
        Set<Gas> gases = new HashSet<>();
        Material m1 = new Material("Wood", 1e100, 1e100, 1e100, 1e100, true);
        Material m2 = new Material("Iron", 1e200, 1e200, 1e200, 1e200, false);
        Set<Material> materials = new HashSet<>();
        materials.add(m1);
        materials.add(m2);
        gases.add(g1);
        gases.add(g2);
        Simulator simulator = new Simulator();
        simulator.setGases(gases);
        simulator.setMaterials(materials);

        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Laser cutting simulator");

        frame.add(new GenerateLaserPanel(simulator), BorderLayout.WEST);

        frame.setVisible(true);
    }
}