package es.ull.iis.simulation.port.portYardEarth;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.ImageIcon;


public class DatosEntrada extends JFrame {
private static final int NGRUAS = 2;
private static final int NCALLESA = 10;
private static final int NCAMIONES = 15;
private static final int NCALLESINT1 = 1;
private static final int NCALLESINT2 = 1;
private static final int NCALLESINT3 = 1;
private static final int NCALLESINT4 = 1;
//TIEMPO EN MINUTOS
private static final long MEDIA_TRAMO_PATIO = 1*60; //Tramo1
private static final long MEDIA_TRAMO_CENTRO = 3*60; //Tramo2
private static final long MEDIA_TRAMO_TIERRA = 3*60; //Tramo3
private static final long MEDIA_CENTRO_VUELTA = 1*60; //Tramo4
private static final long MEDIA_DESCARGA_CONTENEDOR = 6*60;//Descarga
private static final long MEDIA_TRANSFERENCIA = 3*60;//Transferencia
private static final long MEDIA_VUELTA_PATIO1 = 1*60; //Tramo1Vuelta

private static final int NSIM = 1;
private final double ERROR = 0.25;

	private JPanel contentPane;
	protected static JTextField textField;
	protected static JTextField textField_1;
	protected static JTextField textField_2;
	protected static JTextField textField_3;
	protected static JTextField textField_4;
	protected static JTextField textField_5;
	protected static JTextField textField_6;
	protected static JTextField textField_7;
	protected static JTextField textField_8;
	protected static JTextField textField_9;
	protected static JTextField textField_10;
	protected static JTextField textField_11;
	protected static JTextField textField_12;
	protected static JTextField textField_13;
	protected static JTextField textField_14;
	private JButton btnCancelar;
	private JLabel lblNewLabel;
	private JLabel lblTiempo;
	protected static JTextField textField_15;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DatosEntrada frame = new DatosEntrada();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DatosEntrada(){
		
		super("IdeaPort1Main");
		
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Daniel\\Desktop\\TFG General\\logotipo-secundario-ULL.png"));
		setBackground(Color.WHITE);
		setForeground(Color.WHITE);
		setTitle("DATOS DEL MODELO A SIMULAR");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 753, 544);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnAceptar = new JButton("ACEPTAR");
		btnAceptar.addActionListener(new Aceptar());
		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CloseFrame();
			}
		});
		btnAceptar.setBounds(614, 17, 113, 23);
		contentPane.add(btnAceptar);
		
		JLabel lblGruas = new JLabel("N\u00BA GRUAS");
		lblGruas.setBounds(23, 21, 59, 14);
		contentPane.add(lblGruas);
		
		JLabel lblCallesBloque = new JLabel("N\u00BA APARCAMIENTOS BLOQUE");
		lblCallesBloque.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCallesBloque.setBounds(23, 70, 153, 14);
		contentPane.add(lblCallesBloque);
		
		JLabel lblCamiones = new JLabel("N\u00BA PETICIONES / HORA");
		lblCamiones.setBounds(23, 45, 125, 14);
		contentPane.add(lblCamiones);
		
		JLabel lblCALLESTRAMO1 = new JLabel("TRAMO 1");
		lblCALLESTRAMO1.setBounds(23, 132, 94, 14);
		contentPane.add(lblCALLESTRAMO1);
		
		JLabel lblNewLabel_1 = new JLabel("TRAMO 2");
		lblNewLabel_1.setBounds(23, 157, 94, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("TRAMO 3");
		lblNewLabel_2.setBounds(23, 182, 94, 14);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("TRAMO 4");
		lblNewLabel_3.setBounds(23, 207, 94, 14);
		contentPane.add(lblNewLabel_3);
		
		textField = new JTextField();
		textField.setBounds(219, 21, 86, 14);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.setText(""+ NGRUAS);
		textField.setHorizontalAlignment(JTextField.RIGHT);

		textField_1 = new JTextField();
		textField_1.setBounds(219, 45, 86, 14);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		textField_1.setText(""+ NCAMIONES);
		textField_1.setHorizontalAlignment(JTextField.RIGHT);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(219, 70, 86, 14);
		contentPane.add(textField_2);
		textField_2.setText(""+ NCALLESA);
		textField_2.setHorizontalAlignment(JTextField.RIGHT);
		
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(97, 132, 86, 14);
		contentPane.add(textField_3);
		textField_3.setText(""+ NCALLESINT1);
		textField_3.setHorizontalAlignment(JTextField.RIGHT);
		
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(97, 157, 86, 14);
		contentPane.add(textField_4);
		textField_4.setText(""+ NCALLESINT2);
		textField_4.setHorizontalAlignment(JTextField.RIGHT);
		
		
		textField_5 = new JTextField();
		textField_5.setColumns(10);
		textField_5.setBounds(97, 182, 86, 14);
		contentPane.add(textField_5);
		textField_5.setText(""+ NCALLESINT3);
		textField_5.setHorizontalAlignment(JTextField.RIGHT);
		
		
		textField_6 = new JTextField();
		textField_6.setColumns(10);
		textField_6.setBounds(97, 207, 86, 14);
		contentPane.add(textField_6);
		textField_6.setText(""+ NCALLESINT4);
		textField_6.setHorizontalAlignment(JTextField.RIGHT);
		
		
		btnCancelar = new JButton("CANCELAR");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					Thread.sleep(100);
					System.exit(0);
				}catch(Exception excep){
					System.exit(0);
				}
				CloseFrame();
			}

	
		});
		btnCancelar.setBounds(614, 51, 113, 23);
		contentPane.add(btnCancelar);
		
		lblNewLabel = new JLabel("CALLES");
		lblNewLabel.setBounds(117, 107, 46, 14);
		contentPane.add(lblNewLabel);
		
		lblTiempo = new JLabel("TIEMPO");
		lblTiempo.setBounds(229, 95, 46, 14);
		contentPane.add(lblTiempo);
		
		textField_7 = new JTextField();
		textField_7.setColumns(10);
		textField_7.setBounds(219, 132, 86, 14);
		contentPane.add(textField_7);
		textField_7.setText(""+ MEDIA_TRAMO_PATIO);
		textField_7.setHorizontalAlignment(JTextField.RIGHT);
		
		
		textField_8 = new JTextField();
		textField_8.setColumns(10);
		textField_8.setBounds(219, 157, 86, 14);
		contentPane.add(textField_8);
		textField_8.setText(""+ MEDIA_TRAMO_CENTRO);
		textField_8.setHorizontalAlignment(JTextField.RIGHT);
		
		textField_9 = new JTextField();
		textField_9.setColumns(10);
		textField_9.setBounds(219, 182, 86, 14);
		contentPane.add(textField_9);
		textField_9.setText(""+ MEDIA_TRAMO_TIERRA);
		textField_9.setHorizontalAlignment(JTextField.RIGHT);
		
		
		textField_10 = new JTextField();
		textField_10.setColumns(10);
		textField_10.setBounds(219, 207, 86, 14);
		contentPane.add(textField_10);
		textField_10.setText(""+ MEDIA_CENTRO_VUELTA);
		textField_10.setHorizontalAlignment(JTextField.RIGHT);
				
		JLabel lblTiempoTramoVuelta = new JLabel("TRAMO 1 VUELTA");
		lblTiempoTramoVuelta.setBounds(23, 252, 133, 14);
		contentPane.add(lblTiempoTramoVuelta);
		
		JLabel lblTiempoDescargaPatio = new JLabel("DESCARGA PATIO");
		lblTiempoDescargaPatio.setBounds(23, 277, 140, 14);
		contentPane.add(lblTiempoDescargaPatio);
		
		JLabel lblTiempoTransferencia = new JLabel("TIEMPO TRANSFERENCIA");
		lblTiempoTransferencia.setBounds(23, 302, 160, 14);
		contentPane.add(lblTiempoTransferencia);
		
		textField_11 = new JTextField();
		textField_11.setColumns(10);
		textField_11.setBounds(219, 249, 86, 14);
		contentPane.add(textField_11);
		textField_11.setText(""+ MEDIA_VUELTA_PATIO1);
		textField_11.setHorizontalAlignment(JTextField.RIGHT);
		
		
		textField_12 = new JTextField();
		textField_12.setColumns(10);
		textField_12.setBounds(219, 274, 86, 14);
		contentPane.add(textField_12);
		textField_12.setText(""+ MEDIA_DESCARGA_CONTENEDOR);
		textField_12.setHorizontalAlignment(JTextField.RIGHT);
		
		
		textField_13 = new JTextField();
		textField_13.setColumns(10);
		textField_13.setBounds(219, 299, 86, 14);
		contentPane.add(textField_13);
		textField_13.setText(""+ MEDIA_TRANSFERENCIA);
		textField_13.setHorizontalAlignment(JTextField.RIGHT);
				
		JLabel label = new JLabel("");
		label.setBounds(412, 182, 46, 14);
		contentPane.add(label);
		
		JLabel lblNewLabel_4 = new JLabel("");
		lblNewLabel_4.setIcon(new ImageIcon("C:\\Users\\Daniel\\Desktop\\TFG General\\ESQUEMATFG.png"));
		lblNewLabel_4.setBounds(326, 90, 401, 405);
		contentPane.add(lblNewLabel_4);
		
		JLabel lblNmeroDeSimulaciones = new JLabel("N\u00DAMERO DE SIMULACIONES");
		lblNmeroDeSimulaciones.setBounds(23, 387, 160, 14);
		contentPane.add(lblNmeroDeSimulaciones);
		
		textField_14 = new JTextField();
		textField_14.setBounds(193, 387, 37, 19);
		contentPane.add(textField_14);
		textField_14.setColumns(10);
		textField_14.setText(""+ NSIM);
		textField_14.setHorizontalAlignment(JTextField.RIGHT);
		
		JLabel lblError = new JLabel("ERROR");
		lblError.setBounds(23, 420, 46, 14);
		contentPane.add(lblError);
		
		textField_15 = new JTextField();
		textField_15.setBounds(193, 417, 37, 20);
		contentPane.add(textField_15);
		textField_15.setColumns(10);
		textField_15.setText(""+ ERROR);
		textField_15.setHorizontalAlignment(JTextField.RIGHT);
		
		JLabel lblsegundos = new JLabel("(SEGUNDOS)");
		lblsegundos.setBounds(219, 107, 86, 14);
		contentPane.add(lblsegundos);
		
	}

	protected void CloseFrame() {
		super.dispose();
		
	}
}
