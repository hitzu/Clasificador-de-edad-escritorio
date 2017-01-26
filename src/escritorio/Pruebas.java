/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package escritorio;

import java.awt.image.BufferedImage;
import com.atul.JavaOpenCV.Imshow;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.bytedeco.javacpp.opencv_face;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

/**
 *
 * @author Hitzu
 */
public class Pruebas extends javax.swing.JPanel {

    String ruta_archivo = null;
    String ruta_imagen = null;
    /**
     * Creates new form Pruebas
     */
    public Pruebas() {
        initComponents();
    }

    public void probando_imagen(String path)
    {
        Mat aux = Highgui.imread(path, Highgui.CV_LOAD_IMAGE_COLOR);
        if(isFace(aux))
        {
            aux = DetectFace(aux);
            aux = Convert2Gray(aux);
            aux = Equalize(aux);
            Size sz = new Size(200,200);
            Imgproc.resize(aux, aux, sz);

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
            opencv_face.FaceRecognizer faceRecognizer1 = createFisherFaceRecognizer();
            faceRecognizer1.load(ruta.getText());
            int predictedLabel = faceRecognizer1.predict(resultado);
            jTextArea1.setText(jTextArea1.getText() + "El archivo: " + path + " obtuvo una edad estimada de: " + predictedLabel + "\n");
        }
        else
        {
            jTextArea1.setText(jTextArea1.getText() + "No se detecto un rostro en la imagen");
        }
        Imshow image = new Imshow("Imagen");
        image.showImage(aux);
    }
    
    public void probando_archivo(String path) throws FileNotFoundException, IOException
    {
        //Probando la prueba
        int mayores = 0;
        int menores = 0;
        int aciertos = 0;
        String cadena;
        FileReader f = new FileReader(path);
        BufferedReader b = new BufferedReader(f);
        String[] separador;
        int suma, contador;
        contador = 0; suma = 0;
        while((cadena = b.readLine())!=null)
        {
            separador = cadena.split(",");
            Mat aux = Highgui.imread(separador[0], Highgui.CV_LOAD_IMAGE_COLOR);
            if(isFace(aux))
            {
                aux = DetectFace(aux);
                aux = Convert2Gray(aux);
                aux = Equalize(aux);
                Size sz = new Size(200,200);
                Imgproc.resize(aux, aux, sz);

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
                opencv_face.FaceRecognizer faceRecognizer1 = createFisherFaceRecognizer();
                //faceRecognizer1.load("C:\\Users\\Hitzu\\Documents\\Eigenfaces.yml");
                faceRecognizer1.load(ruta.getText());
                int predictedLabel = faceRecognizer1.predict(resultado);
                jTextArea1.setText(jTextArea1.getText() + "La edad real es: " + separador[1] + " El resultado estimado es: " + predictedLabel + "\n");
                suma += Math.abs(predictedLabel - Integer.parseInt(separador[1]));
                if(Integer.parseInt(separador[1]) < 18)
                {
                    menores++;
                    if(predictedLabel < 18)
                        aciertos++;
                }
                else if(Integer.parseInt(separador[1]) > 18)
                {
                    mayores++;
                    if(predictedLabel > 18)
                    {
                        aciertos++;
                    }
                }
                contador++;
            }
            else
            {
                jTextArea1.setText(jTextArea1.getText() + "No existe un rostro en la imagen" + "\n");
            }
        }   
        jTextArea1.setText(jTextArea1.getText() + "La diferencia promedio es de: " + suma/contador + "\n");
        jTextArea1.setText(jTextArea1.getText() + "El sistema tiene una confiabilidad del: " + (aciertos*100)/contador + " al diferenciar la mayoria o minoria de edad\n");
    }
    
    public boolean isFace(Mat imagen)
    {
        //cargando el clasificador en cascada para la deteccion de rostros
        CascadeClassifier detectorRostros = new CascadeClassifier("C://opencv//sources//data//haarcascades//haarcascade_frontalface_alt2.xml");
        Rect[] facesArray;
        MatOfRect rostros = new MatOfRect();
        
        detectorRostros.detectMultiScale(imagen, rostros, 1.1, 2, 0|CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(imagen.height(), imagen.width() ) );
        facesArray = rostros.toArray();
        return facesArray.length >= 1;
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
        for(int i = 0; i < facesArray.length; i++)
        {
            Core.rectangle(aux,
            new Point(facesArray[i].x,facesArray[i].y),
            new Point(facesArray[i].x+facesArray[i].width,facesArray[i].y+facesArray[i].height),
            new Scalar(123, 213, 23, 220));
        }
        return faceROI;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        ruta = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        jLabel1.setText("Ruta del archivo de descripción:");

        jButton1.setText("Buscar Imágen");
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

        jButton3.setText("Buscar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setText("Metodos de verificación:");

        jButton4.setText("Buscar carpeta");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1)
                        .addComponent(jLabel1)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(ruta, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ruta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        ShowFileChoose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        ShowFileChoose("archivo");
        try 
        {
            probando_archivo(ruta_archivo);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Pruebas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        ShowFileChoose(1);
        probando_imagen(ruta_imagen);
    }//GEN-LAST:event_jButton1ActionPerformed

    public void ShowFileChoose()
    {
        File fichero = null;
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.YML", "yml");
        fc.setFileFilter(filtro);
        int seleccion = fc.showOpenDialog(this);
        
        if(seleccion == JFileChooser.APPROVE_OPTION) 
        {
            fichero = fc.getSelectedFile();
            ruta.setText(fichero.getAbsolutePath());
        }
    }
    
    public void ShowFileChoose(String archivo)
    {
        File fichero = null;
        JFileChooser fc = new JFileChooser();
        int seleccion = fc.showOpenDialog(this);
        
        if(seleccion == JFileChooser.APPROVE_OPTION) 
        {
            fichero = fc.getSelectedFile();
            ruta_archivo = fichero.getAbsolutePath();
        }
    }
    
    public void ShowFileChoose(int numero)
    {
        File fichero = null;
        JFileChooser fc = new JFileChooser();
        int seleccion = fc.showOpenDialog(this);
        
        if(seleccion == JFileChooser.APPROVE_OPTION) 
        {
            fichero = fc.getSelectedFile();
            ruta_imagen = fichero.getAbsolutePath();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField ruta;
    // End of variables declaration//GEN-END:variables
}
