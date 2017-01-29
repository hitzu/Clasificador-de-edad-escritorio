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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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

    List<Integer> edades;
    String ruta_cascada_detecta_rostros = "C://opencv//sources//data//haarcascades//haarcascade_frontalface_alt2.xml";
    opencv_face.FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
    /**
     * Creates new form Pruebas
     */
    public Pruebas() {
        initComponents();
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
        jButton4.setEnabled(false);
    }
    
    public List<Integer> CalculaEdad(String path)
    {
        Mat aux = Highgui.imread(path, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat[] rostros = null;
        edades = new ArrayList<Integer>();
        Size sz = new Size(200,200);
        BufferedImage image;
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if(DetectaRostros(aux) > 0)
        {
            rostros = DetectMultiFace(aux);
            for(int i = 0; i < rostros.length; i++)
            {
                rostros[i] = Convert2Gray_Equalize(rostros[i]);
                Imgproc.resize(rostros[i], rostros[i], sz);
                
                if(rostros[i].channels() > 1)
                    type = BufferedImage.TYPE_3BYTE_BGR;
                
                image = new BufferedImage(rostros[i].cols(), rostros[i].rows(), type);
                rostros[i].get(0,0, ((DataBufferByte)image.getRaster().getDataBuffer()).getData());
                
                OpenCVFrameConverter.ToMat cv = new OpenCVFrameConverter.ToMat(); 
                org.bytedeco.javacpp.opencv_core.Mat resultado = cv.convertToMat(new Java2DFrameConverter().convert(image));
                
                edades.add(faceRecognizer.predict(resultado));
            }
        }
        return edades;
    }
    
    public int DetectaRostros(Mat imagen)
    {
        CascadeClassifier detectorRostros = new CascadeClassifier(ruta_cascada_detecta_rostros);
        Rect[] facesArray;
        MatOfRect rostros = new MatOfRect();
        detectorRostros.detectMultiScale(imagen, rostros, 1.1, 2, CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(imagen.height(), imagen.width() ) );
        facesArray = rostros.toArray();
        return facesArray.length;
    }
    
    public Mat[] DetectMultiFace(Mat aux)
    {
        CascadeClassifier detectorRostros = new CascadeClassifier(ruta_cascada_detecta_rostros);
        Rect[] facesArray;
        MatOfRect rostros = new MatOfRect();
        detectorRostros.detectMultiScale(aux, rostros, 1.1, 2, CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(aux.height(), aux.width() ) );
        facesArray = rostros.toArray();
        Mat[] faceROI = new Mat[facesArray.length];
        for(int i = 0; i < facesArray.length; i++)
        {
            faceROI[i] = aux.submat(facesArray[i]);
        }
        return faceROI;
    }
    
    public Mat DibujaRostro(Mat aux)
    {
        CascadeClassifier detectorRostros = new CascadeClassifier(ruta_cascada_detecta_rostros);
        Rect[] facesArray;
        MatOfRect rostros = new MatOfRect();        
        detectorRostros.detectMultiScale(aux, rostros, 1.1, 2, CASCADE_SCALE_IMAGE, new Size(30, 30), new Size(aux.height(), aux.width() ) );
        facesArray = rostros.toArray();
        for(int i = 0; i < facesArray.length; i++)
        {
            Core.rectangle(aux,
            new Point(facesArray[i].x,facesArray[i].y),
            new Point(facesArray[i].x+facesArray[i].width,facesArray[i].y+facesArray[i].height),
            new Scalar(255,0,0));
        }
        return aux;
    }
    
    public Mat Convert2Gray_Equalize(Mat imagen)
    {
        Mat aux = new Mat();
        Imgproc.cvtColor(imagen, aux, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(aux, aux);
        return aux;
    }
    
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
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

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
        Cargar_Modelo();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        ProbarArchivo();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        ProbarImagen();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        ProbarCarpeta();
    }//GEN-LAST:event_jButton4ActionPerformed

    public void ProbarCarpeta()
    {
        jTextArea1.setText(jTextArea1.getText() + " Leyendo carpeta" + "\n");
        String path = JOptionPane.showInputDialog(null, "Ingresa la ruta de la carpeta");
        File f = new File(path);
        File[] ficheros = f.listFiles();
        FileWriter escritura = null;
        PrintWriter pw = null;
        try
        {
            escritura = new FileWriter(path+"\\archivo.txt");
            pw = new PrintWriter(escritura);
            for(File fichero : ficheros)
            {
                edades = CalculaEdad(fichero.getAbsolutePath());
                pw.print(fichero.getAbsolutePath());
                for(Integer edad : edades)
                {
                    pw.print(","+edad);
                    jTextArea1.setText(jTextArea1.getText() + "Las edades calculadas en la foto son: "+ edad + "\n");
                }    
                pw.println();
            }
            escritura.close();
        }
        catch(Exception e)
        {
            jTextArea1.setText(jTextArea1.getText() + "Error: " + e.toString() + "\n");
        }
    }
    
    public void ProbarArchivo()
    {
        String cadena = null;
        String[] separador = null;
        try
        {
            FileReader f = new FileReader(ShowFileChoose("archivo"));
            BufferedReader b = new BufferedReader(f);
            while((cadena = b.readLine())!=null)
            {
                separador = cadena.split(",");
                edades = CalculaEdad(separador[0]);
                for(Integer edad : edades)
                {
                    jTextArea1.setText(jTextArea1.getText() + "La edad real es: " + separador[1] + " El resultado estimado es: " + edad + "\n");
                }
            }
        }
        catch(Exception e)
        {
            jTextArea1.setText(jTextArea1.getText() + "Error: " + e.toString() + "\n");
        }
    }
    
    public void ProbarImagen()
    {
        String path = ShowFileChoose(1);
        edades = CalculaEdad(path);
        for(Integer edad : edades)
        {
            jTextArea1.setText(jTextArea1.getText() + "Las edades calculadas en la foto son: "+ edad + "\n");
        }
        MuestraImagen(path);
    }
    
    public void MuestraImagen(String imagen)
    {
        Mat aux = Highgui.imread(imagen, Highgui.CV_LOAD_IMAGE_COLOR);
        Imshow image = new Imshow("Imagen");
        image.showImage(DibujaRostro(aux));
    }
    
    public void Cargar_Modelo()
    {
        jTextArea1.setText(jTextArea1.getText() + "Cargando Modelo de datos... " + "\n");
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
        faceRecognizer.load(ruta.getText());
        jTextArea1.setText(jTextArea1.getText() + "Se ha cargado el modelo de datos " + "\n");
        jButton1.setEnabled(true);
        jButton2.setEnabled(true);
        jButton4.setEnabled(true);
    }
    
    public String ShowFileChoose(String archivo)
    {
        String ruta_archivo = null;
        File fichero = null;
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.TXT", "txt");
        int seleccion = fc.showOpenDialog(this);
        
        if(seleccion == JFileChooser.APPROVE_OPTION) 
        {
            fichero = fc.getSelectedFile();
            ruta_archivo = fichero.getAbsolutePath();
        }
        return ruta_archivo;
    }
    
    public String ShowFileChoose(int numero)
    {
        String ruta_imagen = null;
        File fichero = null;
        JFileChooser fc = new JFileChooser();
        int seleccion = fc.showOpenDialog(this);
        
        if(seleccion == JFileChooser.APPROVE_OPTION) 
        {
            fichero = fc.getSelectedFile();
            return ruta_imagen = fichero.getAbsolutePath();
        }
        return ruta_imagen;
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
