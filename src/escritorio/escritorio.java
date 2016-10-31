/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package escritorio;
import com.atul.JavaOpenCV.Imshow;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.bytedeco.javacpp.helper.opencv_core;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
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
     MatVector images;
     org.bytedeco.javacpp.opencv_core.Mat labels;

    /**
     * Creates new form escritorio
     */
    public escritorio() {
        initComponents();
        JButton3.setEnabled(false);
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
                        Size sz = new Size(200,200);
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
    
    public void convertMatToMat()
    {
        
        //para hacer la conversion de Mat a javacv.Mat y llenar el MatVector
        //Se realizo una conversion 
        //Mat >> BufferedImage y luego
        //bufferedImage >> javacv.Mat
        BufferedImage image;
        int type = BufferedImage.TYPE_BYTE_GRAY;
        images = new MatVector(imagenes.size()); 
        
        long cont = 0;
        for(int i = 0; i< imagenes.size(); i++)
        {
            //primera conversion
            if(imagenes.get(i).channels() > 1)
            {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            image = new BufferedImage(imagenes.get(i).cols(), imagenes.get(i).rows(), type);
            imagenes.get(i).get(0,0, ((DataBufferByte)image.getRaster().getDataBuffer()).getData());
            //segunda conversion
            OpenCVFrameConverter.ToMat cv = new OpenCVFrameConverter.ToMat(); 
            org.bytedeco.javacpp.opencv_core.Mat resultado = cv.convertToMat(new Java2DFrameConverter().convert(image));
            images.put(cont,resultado);
            cont++;
        }
    }
    
    public void convertArrayIntToOpencvMat()
    {
        jTextArea1.setText(jTextArea1.getText() + "\n" + "tamaño de etiquetas: " + etiquetas.size());
        labels = new org.bytedeco.javacpp.opencv_core.Mat(etiquetas.size(),1,CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();
        for(int i = 0; i < etiquetas.size(); i++)
        {
            labelsBuf.put(i,Integer.parseInt(etiquetas.get(i)));
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

        jPanel1 = new javax.swing.JPanel();
        ruta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        JButton3 = new javax.swing.JButton();

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

        JButton3.setText("Exportar modelo");
        JButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(ruta, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(14, 14, 14)
                        .addComponent(JButton3)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(ruta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
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
        JButton3.setEnabled(true);
        //Imshow image = new Imshow("Imagen");
        //image.showImage(imagenes.getFirst());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void JButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButton3ActionPerformed
        // TODO add your handling code here:
        
        convertMatToMat();
        convertArrayIntToOpencvMat();
        FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
        faceRecognizer.train(images, labels);
        
        //Probando la prueba
        Mat test;
        String path = "C:\\Users\\Hitzu\\Documents\\proyectosQT\\clasificador\\test\\071A27.jpg";        
        Mat aux = Highgui.imread(path, Highgui.CV_LOAD_IMAGE_COLOR);
        if(isFace(aux))
        {
            aux = DetectFace(aux);
            aux = Convert2Gray(aux);
            aux = Equalize(aux);
            Size sz = new Size(200,200);
            Imgproc.resize(aux, aux, sz);
        }
        
        //algoritmo para convertir una sola imagen
        BufferedImage image;
        int type = BufferedImage.TYPE_BYTE_GRAY;

        if(aux.channels() > 1)
        {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        image = new BufferedImage(aux.cols(), aux.rows(), type);
        aux.get(0,0, ((DataBufferByte)image.getRaster().getDataBuffer()).getData());
        //segunda conversion
        OpenCVFrameConverter.ToMat cv = new OpenCVFrameConverter.ToMat(); 
        org.bytedeco.javacpp.opencv_core.Mat resultado = cv.convertToMat(new Java2DFrameConverter().convert(image));
        
        
        int predictedLabel = faceRecognizer.predict(resultado);
        System.out.println("El resultado es: " + predictedLabel);
    }//GEN-LAST:event_JButton3ActionPerformed

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
    private javax.swing.JButton JButton3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField ruta;
    // End of variables declaration//GEN-END:variables
}
