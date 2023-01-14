package hilosTest;

import Utilidades.ManejadorArchivo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientHandler extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    private String fileName;
    private String clientUsername = "[NONAME]";

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String received;
        String toreturn;
        while (true) {
            try {

                // Preguntarle al usuario que quiere
                dos.writeUTF("[ESPERANDO INSTRUCCION]");
                
                System.out.println("HI " + clientUsername);

                // Recibir la respuesta del cliente
                received = dis.readUTF();        

                if (received.equals("Exit")) {
                    System.out.println("Cliente " + this.s + " envia exit...");
                    System.out.println("cerrando esta conexion.");
                    this.s.close();
                    System.out.println("Conexion cerrada");
                    break;
                }

                // creating Date object
                Date date = new Date();

                // Escribir un stream de salida
                // con base en la entrada del cliente
                switch (received) {

                    case "Date":
                            toreturn = fordate.format(date);
                            dos.writeUTF(toreturn);
                        break;

                    case "Time":
                            toreturn = fortime.format(date);
                            dos.writeUTF(toreturn);
                        break;

                    case "login":
                            String userName = dis.readUTF();
                            System.out.println("recibido:" + userName);
                            String userPassword = dis.readUTF();
                            System.out.println("recibido:" + userPassword);
                                                       
                            //verificar que exista
                            dos.writeUTF("pass");
                            setClientUsername(userName);
                        break;
                        
                    case "sendFile":
                            fileName = dis.readUTF();
                            receiveFile(ManejadorArchivo.usersPrivateDirectory + "\\" + "jose" + "\\" + fileName);
                            dos.writeUTF("Archivo recibio exitosamente");
                        break;

                    case "sendSharedFile":
                            fileName = dis.readUTF();
                            receiveFile(ManejadorArchivo.sharedDirectory + "\\" + fileName);
                            dos.writeUTF("Archivo recibio exitosamente");
                        break;

                    case "getSharedFile":
                            // mandar lista de archivos (GUI)
                            fileName = dis.readUTF();
                            sendFile(ManejadorArchivo.sharedDirectory + "\\" + fileName);
                            dos.writeUTF("Archivo enviado exitosamente");
                        break;

                    default:
                            dos.writeUTF("Entrada invalida");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            // Cerrar recursos
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Funcion para recibir archivo a partir de un stream de entrada
    private void receiveFile(String fileName) throws Exception {
        int bytes = 0;
        //String rutaGuardado = dis.readUTF();
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        long size = dis.readLong(); // Leer el tamano de archivo
        byte[] buffer = new byte[4 * 1024];
        while (size > 0 && (bytes = dis.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                // Escribir el archivo usando el metodo write
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes; // read upto file size
        }
        // Aqui ya se ha recibido el archivo
        //System.out.println("El archivo fue recibido exitosamente");
        //System.out.println("Guardado en la ruta " + "C:\\txtTest\\joseHelp.txt");
        fileOutputStream.close();
    }

    // Funcion para enviar archivo en un stream de salida
    private void sendFile(String ruta) throws Exception {
        int bytes = 0;
        // Open the File where he located in your pc
        File file = new File(ruta);
        FileInputStream fileInputStream = new FileInputStream(file);

        // Here we send the File to Server
        dos.writeLong(file.length());
        // Here we break file into chunks
        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) {
            // Send the file to Server Socket
            dos.write(buffer, 0, bytes);
            dos.flush();
        }
        // close the file here
        fileInputStream.close();
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

}
