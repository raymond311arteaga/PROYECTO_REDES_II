package hilosTest;

import Utilidades.ManejadorArchivo;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket s;
    private String fileName;
    private Scanner scn;
    
    private String clientUsername = "[NONAME]";
    private String clientPassword = "[NOPASSWORD]";
    
    public Client(String serverIP, int serverPort) {
        try {
            // Establecer la conexion con el servidor
            // IP del servidor y puerto del servidor
            this.s = new Socket(serverIP, serverPort);
            // Obtener streams de entrada y salida
            this.dis = new DataInputStream(s.getInputStream());
            this.dos = new DataOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Error en constrcutor de clase Client");
        }
        
    }
    
    public void startClient(){
        
        scn = new Scanner(System.in);
        
        try { 
            
            // El siguiente ciclo realiza el intercambio de
            // informacion entre el cliente y el manejador de cliente (clientHandler)
            while (true) {
                String received;
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);

                // Si el cliente envia "Exit", se cierra la conexion
                // y luego se escapa (break) del ciclo while
                
                switch (tosend) {
                    case "Exit":
                        System.out.println("Cerrando esta conexion : " + s);
                        s.close();
                        System.out.println("Conexion cerrada");
                        
                        // Cerrar recursos
                        scn.close();
                        dis.close();
                        dos.close();         
                        break;
                        
                    case "sendFile":
                        System.out.println("Inserte nombre con el que sera guardado el archivo final y su formato");
                        System.out.print("--->");
                        fileName = scn.nextLine();
                        dos.writeUTF(fileName);
                        sendFile("C:\\txtTest\\juan.txt"); //temporal de prueba
                        break;
                        
                    case "sendSharedFile":
                        System.out.println("Inserte nombre con el que sera guardado el archivo final y su formato");
                        System.out.print("--->");
                        fileName = scn.nextLine();
                        dos.writeUTF(fileName);
                        sendFile("C:\\txtTest\\juan.txt"); //temporal de prueba
                        break;
                        
                    case "getSharedFile":
                        // se muestran archivos para descargar
                        System.out.println("Inserte nombre del archivo con su formato");
                        System.out.print("--->");
                        fileName = scn.nextLine();
                        dos.writeUTF(fileName);
                        receiveFile(ManejadorArchivo.usersDownloadDirectory + "\\" + clientUsername + "\\" + fileName);
                        System.out.println("Descargado correctamente");
                        break;
                }

                // Imprimir informacion recibida por el cliente
                received = dis.readUTF();
                System.out.println(received);
            }        
          
        } catch (SocketException se) {
            //System.out.println("Socket error" + se.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void loginClient(){
        scn = new Scanner(System.in);
        boolean logedIn = false;
        boolean closed = false;
        
        try { 
            
            while (logedIn == false) {
                
                if (!closed) {
                    System.out.println("Bienvenido usa 'login' para conectarte..");
                    System.out.println("o usa 'Exit' para salir..");
                }             
                
                String received;
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);
                
                switch (tosend) {
                    case "Exit":
                        System.out.println("Cerrando esta conexion : " + s);
                        s.close();
                        System.out.println("Conexion cerrada");
                        
                        // Cerrar recursos
                        scn.close();       
                        dis.close();
                        dos.close();
                        closed = true;
                        //System.exit(0);
                        break;
                        
                    case "login":
                        System.out.println("Inserte nombre de usuario");
                        System.out.print("--->");          
                        tosend = scn.nextLine();
                        setClientUsername(tosend);
                        dos.writeUTF(tosend);
                        
                        System.out.println("Inserte contraseña de usuario");
                        System.out.print("--->");   
                        tosend = scn.nextLine();
                        setClientPassword(tosend);
                        dos.writeUTF(tosend);
                        
                        if ((dis.readUTF()).equals("pass")) {
                            System.out.println("Login correcto");
                            logedIn = true;
                            ManejadorArchivo.initializeUserFolder(clientUsername);
                        } else {
                            System.out.println("Login incorrecto");
                        }
                        
                        break;
                        
                }
            }
            //scn.close();
            
        } catch (SocketException se) {
            //System.out.println("Socket error" + se.getMessage());
        }
        catch (Exception e) {
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

    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    public static void main(String[] args) throws IOException {
        
        // Inicializar carpetas de proyecto
        ManejadorArchivo.initializeFolders();
        
        Client micliente = new Client("localhost", 7777);
        micliente.loginClient();
        micliente.startClient();
        
    }
    
}
