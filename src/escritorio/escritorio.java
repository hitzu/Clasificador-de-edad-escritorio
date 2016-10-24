/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package escritorio;
import com.atul.JavaOpenCV.Imshow;
import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

/**
 *
 * @author Hitzu
 */
public class escritorio extends javax.swing.JFrame {
    
    //variables globales del programa
     File fichero = null;      
     LinkedList<Mat> imagenes = new LinkedList<Mat>();
     LinkedList<String> etiquetas = new LinkedList<String>();

    /**
     * Creates new form escritorio
     */
    public escritorio() {
        initComponents();
    }
    
    
    
    public void ShowFileChoose()
    {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.TXT", "txt");
        fc.setFileFilter(filtro);
        int seleccion = fc.showOpenDialog(jPanel1);
        
        if(seleccion == JFileChooser.APPROVE_OPTION) 
        {
            
            fichero = fc.getSelectedFile();
            ruta.setText(fichero.getAbsolutePath());
        }
    }
    
    public void ReadFile()
    {
        Mat aux,faceROI;        
        String[] separador;
        try
            {
                BufferedReader br = new BufferedReader (new FileReader (fichero));
                String cadena;
                while((cadena = br.readLine())!=null)
                {   
                    separador = split(cadena);
                    aux = Highgui.imread(separador[0], Highgui.CV_LOAD_IMAGE_COLOR);
                    if(isFace(aux))
                    {
                        aux = DetectFace(aux);
                        aux = Convert2Gray(aux);
                        faceROI = Equalize(aux);
                        Size sz = new Size(211,211);
                        Imgproc.resize(faceROI, aux, sz);
                        FillVectors(aux,separador[1]);
                    }
                }
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(jPanel1, "No se pudo abrir el archivo", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Error: " + e.toString());
            }
    }
    
    public String[] split(String cadena)
    {
        String[] separador = cadena.split(",");
        return separador;
    }
    
    public void FillVectors(Mat imagen,String clase)
    {
        imagenes.add(imagen);
        etiquetas.add(clase);
    }
    
    public Mat DetectFace(Mat aux)
    {
        //cargando el clasificador en cascada para la deteccion de rostros
        CascadeClassifier detectorRostros = new CascadeClassifier("C://opencv//sources//data//haarcascades//haarcascade_frontalface_alt2.xml");
        Rect[] facesArray;
        MatOfRect rostros = new MatOfRect();
        Mat faceROI;
        
        detectorRostros.detectMultiScale(aux, rostros, 1.1, 2, 0|CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(aux.height(), aux.width() ) );
        facesArray = rostros.toArray();

        faceROI = aux.submat(facesArray[0]);
        //dibujarlas
        /*for(int i = 0; i < facesArray.length; i++)
        {
            Core.rectangle(aux,
            new Point(facesArray[i].x,facesArray[i].y),
            new Point(facesArray[i].x+facesArray[i].width,facesArray[i].y+facesArray[i].height),
            new Scalar(123, 213, 23, 220));
        }*/
        return faceROI;
    }
    
    public boolean isFace(Mat imagen)
    {
        //cargando el clasificador en cascada para la deteccion de rostros
        CascadeClassifier detectorRostros = new CascadeClassifier("C://opencv//sources//data//haarcascades//haarcascade_frontalface_alt2.xml");
        Rect[] facesArray;
        MatOfRect rostros = new MatOfRect();
        
        detectorRostros.detectMultiScale(imagen, rostros, 1.1, 2, 0|CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(imagen.height(), imagen.width() ) );
        facesArray = rostros.toArray();
        if(facesArray.length >= 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public Mat Convert2Gray(Mat imagen)
    {
        Mat aux = new Mat();
        Imgproc.cvtColor(imagen, aux, Imgproc.COLOR_BGR2GRAY);
        return aux;
    }
    
    public Mat Equalize(Mat imagen)
    {
        Mat aux = new Mat();
        Imgproc.equalizeHist(imagen, aux);
        return aux;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        ruta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Ruta hacia el archivo de descripcion:");

        jButton1.setText("Cargar archivo");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Buscar archivo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(ruta, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(49, 49, 49)
                .addComponent(jButton1)
                .addGap(80, 80, 80))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(ruta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        ShowFileChoose();
    }//GEN-LAST:event_jButton2ActionPerformed

    
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
        if(fichero == null)
        {
            JOptionPane.showMessageDialog(jPanel1, "Selecciona un archivo", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            ReadFile();
        }
        jTextArea1.setText( jTextArea1.getText() + "Carga de imagenes completa");
        Imshow image = new Imshow("Imagen");
        image.showImage(imagenes.getFirst());
    }//GEN-LAST:event_jButton1ActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(escritorio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(escritorio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(escritorio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(escritorio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new escritorio().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField ruta;
    // End of variables declaration//GEN-END:variables
}
