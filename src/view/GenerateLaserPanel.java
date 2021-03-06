/*
 * Package location for UI classes.
 */
package view;

import controller.GenerateLaserController;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import view.components.DoubleJTextField;

/**
 * Represents a panel with the fields to generate the laser.
 *
 * @author Daniel Gonçalves 1151452
 * @author Eric Amaral 1141570
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
            if (value != null) {
                value = ((Gas) value).getName();
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    /**
     * Inner class to render the gas.
     */
    private static class MaterialRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                value = ((Material) value).getName();
            }
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
     * The sselected material thickness.
     */
    private Double materialThickness;

    /**
     * The materialThicknessTextField.
     */
    private JTextField materialThicknessTextField;

    /**
     * The button to calculate maximum power.
     */
    private JButton calculateMaxPowerButton;

    /**
     * The max power.
     */
    private Double maxPower;

    /**
     * The label with maximum power.
     */
    private JLabel maxPowerLabel;

    /**
     * The parent simulator frame.
     */
    private final SimulatorFrame simulatorFrame;

    /**
     * Creates an instance of GenerateLaserPanel.
     *
     * @param simulatorFrame the parent simulator frame
     * @param simulator the simulator with all data
     */
    public GenerateLaserPanel(SimulatorFrame simulatorFrame, Simulator simulator) {
        super();

        this.simulatorFrame = simulatorFrame;
        this.simulator = simulator;
        this.controller = new GenerateLaserController(simulator);
        setDefaultValues();

        createComponents();
    }

    /**
     * Creates the UI components.
     */
    private void createComponents() {
        setLayout(new GridLayout(7, 1, 0, 0));

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
        JPanel wavelengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel wavelengthLabel = new JLabel(String.format("Comprimento de onda:"));
        wavelengthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        wavelengthLabel.setPreferredSize(new Dimension(180, 20));

        JTextField wavelengthTextField = new JTextField("200");
        wavelengthTextField.setText(wavelength.toString());
        wavelengthTextField.setFont(new Font("Arial", Font.BOLD, 16));
        wavelengthTextField.setPreferredSize(new Dimension(80, 30));

        JLabel unitsLabel = new JLabel(String.format("nm", wavelength));
        unitsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        unitsLabel.setPreferredSize(new Dimension(30, 20));

        JSlider wavelengthSlider = new JSlider(200, 26000, 10600);
        wavelengthSlider.setFont(new Font("Arial", Font.BOLD, 14));

        wavelengthSlider.setMajorTickSpacing(10000);
        wavelengthSlider.setMinorTickSpacing(2500);
        wavelengthSlider.setPaintTicks(true);
        wavelengthSlider.setPaintLabels(true);

        wavelengthSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                wavelength = (double) wavelengthSlider.getValue();
                controller.setWavelength(wavelength * 1e-9);
                wavelengthTextField.setText(wavelength.toString());
                gases = controller.getGasesByWavelength();
                updateGasComboBox();
            }
        });
        wavelengthTextField.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                wavelengthTextField.setText("");
            }

            public void focusLost(FocusEvent e) {
                String typed = wavelengthTextField.getText();
                int value = (typed.isEmpty()) ? 200 : Integer.parseInt(typed);
                wavelengthSlider.setValue(value);
                wavelength = (typed.isEmpty()) ? 200.0 : Double.parseDouble(typed);
                controller.setWavelength(wavelength * 1e-9);
                gases = controller.getGasesByWavelength();
            }
        });

        wavelengthPanel.add(wavelengthLabel);
        wavelengthPanel.add(wavelengthTextField);
        wavelengthPanel.add(unitsLabel);
        wavelengthPanel.add(wavelengthSlider);

        return wavelengthPanel;
    }

    /**
     * Creates the panel for gas.
     *
     * @return gas panel
     */
    private JPanel createGasPanel() {
        JPanel gasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        gasesComboBox = new JComboBox<>();
        gasesComboBox.setPreferredSize(new Dimension(150, 20));
        gasesComboBox.setFont(new Font("Arial", Font.BOLD, 16));
        updateGasComboBox();

        gasesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                gas = (Gas) gasesComboBox.getSelectedItem();
                controller.setGas(gas);
            }
        });

        JLabel gasLabel = new JLabel("Gás:");
        gasLabel.setPreferredSize(new Dimension(50, 20));
        gasLabel.setFont(new Font("Arial", Font.BOLD, 16));

        gasPanel.add(gasLabel);
        gasPanel.add(gasesComboBox);

        return gasPanel;
    }

    /**
     * Creastes the panel for focal point.
     *
     * @return focal point panel
     */
    private JPanel createFocalPointPanel() {
        JPanel focalPointPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        focalPointDiameter = 4d;
        JLabel focalPointLabel = new JLabel(String.format("Diâmetro do corte:   %.0f mm", focalPointDiameter));
        focalPointLabel.setPreferredSize(new Dimension(210, 20));
        focalPointLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JSlider focalPointSlider = new JSlider(1, 8, 4);
        focalPointSlider.setFont(new Font("Arial", Font.BOLD, 14));
        focalPointSlider.setSnapToTicks(true);

        focalPointSlider.setMajorTickSpacing(1);
        focalPointSlider.setPaintTicks(true);
        focalPointSlider.setPaintLabels(true);

        focalPointSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                focalPointDiameter = (double) focalPointSlider.getValue();
                controller.setFocalPointDiameter(focalPointDiameter * 1E-3);
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
        JPanel materialPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JComboBox<Material> materialComboBox = new JComboBox<>();
        materialComboBox.setPreferredSize(new Dimension(150, 30));
        materialComboBox.setFont(new Font("Arial", Font.BOLD, 16));
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

        JLabel materialLabel = new JLabel("Material:");
        materialLabel.setPreferredSize(new Dimension(80, 20));
        materialLabel.setFont(new Font("Arial", Font.BOLD, 16));

        materialPanel.add(materialLabel);
        materialPanel.add(materialComboBox);

        return materialPanel;
    }

    /**
     * Creates the panel for material thickness.
     *
     * @return material tickness panel
     */
    private JPanel createMaterialThickness() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        materialThicknessTextField = new DoubleJTextField(6);
        materialThicknessTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    double testValue = Double.parseDouble(materialThicknessTextField.getText() + e.getKeyChar());
                    if (testValue > 0) {
                        materialThickness = testValue;
                        controller.setMaterialThickness(materialThickness * 1E-3);
                        calculateMaxPowerButton.setEnabled(true);
                    } else {
                        calculateMaxPowerButton.setEnabled(false);
                    }
                } catch (NumberFormatException exception) {
                    calculateMaxPowerButton.setEnabled(false);
                }
            }
        });
        materialThicknessTextField.setPreferredSize(new Dimension(70, 30));
        materialThicknessTextField.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel materialThicknessTextFieldLabel = new JLabel("Espessura do material:");
        materialThicknessTextFieldLabel.setPreferredSize(new Dimension(180, 20));
        materialThicknessTextFieldLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel mesureUnitsLabel = new JLabel("mm");
        mesureUnitsLabel.setFont(new Font("Arial", Font.BOLD, 16));

        buttonPanel.add(materialThicknessTextFieldLabel);
        buttonPanel.add(materialThicknessTextField);
        buttonPanel.add(mesureUnitsLabel);

        return buttonPanel;
    }

    /**
     * Creates the panel for calculate button.
     *
     * @return calculate button panel
     */
    private JPanel createCalculateButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        calculateMaxPowerButton = new JButton("Calcular corte");
        calculateMaxPowerButton.setPreferredSize(new Dimension(140, 40));
        calculateMaxPowerButton.setFont(new Font("Arial", Font.BOLD, 16));
        calculateMaxPowerButton.setEnabled(false);
        calculateMaxPowerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (gas == null) {
                    gas = new Gas("CO2", 4600E-9, 5800E-9);
                    gasesComboBox.addItem(gas);
                    controller.setGas(gas);
                }
                controller.newLaser();
                controller.initiateCut();
                simulatorFrame.initiateCutPanel(controller.getCalculateLaserCutController());
                maxPower = controller.getMaxPower();
                maxPowerLabel.setText(String.format("Poder máximo:   %.4e W", maxPower));
                simulatorFrame.getExperience();
                simulatorFrame.enableExport();
            }
        });

        buttonPanel.add(calculateMaxPowerButton);

        return buttonPanel;
    }

    /**
     * Creates the panel for max power.
     *
     * @return max power panel
     */
    private JPanel createMaxPowerPanel() {
        JPanel maxPowerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        maxPowerLabel = new JLabel("Poder máximo:");
        maxPowerLabel.setPreferredSize(new Dimension(250, 40));
        maxPowerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        maxPowerPanel.add(maxPowerLabel);

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
        this.controller.setFocalPointDiameter(focalPointDiameter * 1E-3f);
    }

    /**
     * Updates the fas combo box.
     */
    private void updateGasComboBox() {
        gasesComboBox.removeAllItems();

        Iterator<Gas> gasesIterator = gases.iterator();
        while (gasesIterator.hasNext()) {
            gasesComboBox.addItem(gasesIterator.next());
        }

        gasesComboBox.setRenderer(new GasRenderer());
        gas = (Gas) gasesComboBox.getSelectedItem();
        controller.setGas(gas);
    }
}
