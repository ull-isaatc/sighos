package es.ull.iis.simulation.port.portYardEarth;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.TreeMap;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.Toolkit;
import javax.swing.SwingConstants;

public class TiemposConcluyentes extends JFrame {
	
	private JPanel contentPane;
	public JTextField textField;
	public JTextField textField_1;
	public JTextField textField_2;
	public JTextField textField_3;
	public JTextField textField_6;
	TreeMap<Integer, Long> esperaInicio = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> reservaGrua = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo1 = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo2 = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo3 = new TreeMap<Integer, Long>();
	TreeMap<Integer, Long> tramo4 = new TreeMap<Integer, Long>();
	private JTextField SDTIMETOTALtextField_4;
	private JTextField SDTIMEPROMEDIOtextField_5;
	private JTextField SDTIMEMINtextField_7;
	private JTextField SDTIMEMAXtextField_8;
	private JTextField SDNOFINALIZADAStextField_9;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TiemposConcluyentes frame = new TiemposConcluyentes();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @return 
	 */
	
	public TiemposConcluyentes() {
		
		super("DatosEntrada");
	
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Daniel\\Desktop\\TFG General\\logotipo-secundario-ULL.png"));
		setTitle("TIEMPO DE EXTRACCI\u00D3N DE CONTENEDORES");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 611, 387);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblGruasDisponibles = new JLabel("TIEMPO TOTAL PROMEDIO DE EXTRACCI\u00D3N ");
		lblGruasDisponibles.setBounds(22, 50, 258, 14);
		contentPane.add(lblGruasDisponibles);
	
		JLabel lblPeticionesCamiones = new JLabel("TIEMPO PROMEDIO DE EXTRACCI\u00D3N  ");
		lblPeticionesCamiones.setBounds(22, 78, 235, 14);
		contentPane.add(lblPeticionesCamiones);
		
		JLabel lblAparcamientosDelBloque = new JLabel("TIEMPO MINIMO DE EXTRACCI\u00D3N");
		lblAparcamientosDelBloque.setBounds(22, 108, 235, 14);
		contentPane.add(lblAparcamientosDelBloque);
		
		JLabel lblCallesTramo = new JLabel("TIEMPO M\u00C1XIMO DE EXTRACCI\u00D3N");
		lblCallesTramo.setBounds(22, 144, 235, 14);
		contentPane.add(lblCallesTramo);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("NUMERO DE CONFLICTOS POR RECURSO");
		chckbxNewCheckBox.setBounds(52, 282, 306, 23);
		contentPane.add(chckbxNewCheckBox);
		
		JCheckBox chckbxTiempoDeConflicto = new JCheckBox("TIEMPO DE CONFLICTO POR RECURSO (PROMEDIO)");
		chckbxTiempoDeConflicto.setBounds(52, 308, 322, 23);
		contentPane.add(chckbxTiempoDeConflicto);
		
		JButton btnVerificar = new JButton("OBTENER ");
		btnVerificar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxNewCheckBox.isSelected() == true){
					GrafNumConflictos Conflictos = new GrafNumConflictos();
					Conflictos.setVisible(true);
				}
				CloseFrame();
				if (chckbxTiempoDeConflicto.isSelected() == true){
					GrafTimeConflictos Promedio = new GrafTimeConflictos();
					Promedio.setVisible(true);
					}
				CloseFrame();			
			}
			private void CloseFrame() {
				
				
			}
		});
		btnVerificar.setBounds(425, 274, 147, 23);
		contentPane.add(btnVerificar);
		
		textField = new JTextField();
		textField.setBounds(290, 48, 86, 17);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.setText("" + String.format("%.2f",TiempoEstanciaListener.getSumaGlobal()/60));//tiempo total
		textField.setHorizontalAlignment(JTextField.RIGHT);
	
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(290, 76, 86, 17);
		contentPane.add(textField_1);
		textField_1.setText(""+ String.format("%.2f", TiempoEstanciaListener.getPromedioGlobal()/60));//tiempo promedio
//		System.out.println(TiempoEstanciaListener.getSDPromedioGlobal());
		textField_1.setHorizontalAlignment(JTextField.RIGHT);
	
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(290, 106, 86, 17);
		contentPane.add(textField_2);
		textField_2.setText(""+ String.format("%.2f",TiempoEstanciaListener.getMinGlobal()/60)); //minimo
		textField_2.setHorizontalAlignment(JTextField.RIGHT);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(290, 142, 86, 17);
		contentPane.add(textField_3);
		textField_3.setText(""+ String.format("%.2f",TiempoEstanciaListener.getMaxGlobal()/60));//maximo
		textField_3.setHorizontalAlignment(JTextField.RIGHT);
	
		textField_6 = new JTextField();
		textField_6.setColumns(10);
		textField_6.setBounds(299, 203, 59, 23);
		contentPane.add(textField_6); // no acabaron
		textField_6.setText(""+ String.format("%.2f",TiempoEstanciaListener.getContadorGlobal()));
		textField_6.setHorizontalAlignment(JTextField.RIGHT);
		
		JButton btnCancelar = new JButton("CANCELAR");
		btnCancelar.setBounds(425, 308, 147, 23);
		contentPane.add(btnCancelar);
		
		JLabel lblMinutos = new JLabel("MINUTOS");
		lblMinutos.setBounds(306, 25, 59, 14);
		contentPane.add(lblMinutos);
		
		JLabel lblGrficasseleccioneLa = new JLabel("GR\u00C1FICAS : (Seleccione la gr\u00E1fica y pulse el bot\u00F3n obtener)");
		lblGrficasseleccioneLa.setBounds(22, 250, 427, 14);
		contentPane.add(lblGrficasseleccioneLa);
		
		JLabel lblPeticionesNoFinalizadas = new JLabel("PETICIONES NO FINALIZADAS");
		lblPeticionesNoFinalizadas.setBounds(22, 212, 167, 14);
		contentPane.add(lblPeticionesNoFinalizadas);
		
		JLabel lblPromedio = new JLabel("PROMEDIO");
		lblPromedio.setBounds(300, 11, 74, 14);
		contentPane.add(lblPromedio);
		
		SDTIMETOTALtextField_4 = new JTextField();
		SDTIMETOTALtextField_4.setText(""+ String.format("%.2f",TiempoEstanciaListener.getSDSumaGlobal()/60));
		SDTIMETOTALtextField_4.setHorizontalAlignment(SwingConstants.RIGHT);
		SDTIMETOTALtextField_4.setColumns(10);
		SDTIMETOTALtextField_4.setBounds(425, 47, 86, 17);
		contentPane.add(SDTIMETOTALtextField_4);
		
		SDTIMEPROMEDIOtextField_5 = new JTextField();
		SDTIMEPROMEDIOtextField_5.setText(""+ String.format("%.2f",TiempoEstanciaListener.getSDPromedioGlobal()/60));
		SDTIMEPROMEDIOtextField_5.setHorizontalAlignment(SwingConstants.RIGHT);
		SDTIMEPROMEDIOtextField_5.setColumns(10);
		SDTIMEPROMEDIOtextField_5.setBounds(425, 75, 86, 17);
		contentPane.add(SDTIMEPROMEDIOtextField_5);
		
		SDTIMEMINtextField_7 = new JTextField();
		SDTIMEMINtextField_7.setText(""+ String.format("%.2f",TiempoEstanciaListener.getSDMinGlobal()/60));
		SDTIMEMINtextField_7.setHorizontalAlignment(SwingConstants.RIGHT);
		SDTIMEMINtextField_7.setColumns(10);
		SDTIMEMINtextField_7.setBounds(425, 105, 86, 17);
		contentPane.add(SDTIMEMINtextField_7);
		
		SDTIMEMAXtextField_8 = new JTextField();
		SDTIMEMAXtextField_8.setText(""+ String.format("%.2f",TiempoEstanciaListener.getSDMaxGlobal()/60));
		SDTIMEMAXtextField_8.setHorizontalAlignment(SwingConstants.RIGHT);
		SDTIMEMAXtextField_8.setColumns(10);
		SDTIMEMAXtextField_8.setBounds(425, 141, 86, 17);
		contentPane.add(SDTIMEMAXtextField_8);
		
		JLabel lblDesEstndar = new JLabel("DES. EST\u00C1NDAR");
		lblDesEstndar.setBounds(426, 11, 97, 14);
		contentPane.add(lblDesEstndar);
		
		JLabel lblUnidad = new JLabel("UNIDAD");
		lblUnidad.setBounds(312, 182, 46, 14);
		contentPane.add(lblUnidad);
		
		JLabel label = new JLabel("DES. EST\u00C1NDAR");
		label.setBounds(426, 182, 97, 14);
		contentPane.add(label);
		
		SDNOFINALIZADAStextField_9 = new JTextField();
		SDNOFINALIZADAStextField_9.setText(""+ String.format("%.2f",TiempoEstanciaListener.getSDContadorGlobal()));
		SDNOFINALIZADAStextField_9.setHorizontalAlignment(SwingConstants.RIGHT);
		SDNOFINALIZADAStextField_9.setColumns(10);
		SDNOFINALIZADAStextField_9.setBounds(425, 209, 86, 17);
		contentPane.add(SDNOFINALIZADAStextField_9);
		
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				
				CloseFrame();
			}
		});
	}
	private void CloseFrame() {
		super.dispose();
	}
}

