package hilosTest;

import Utilidades.ManejadorArchivo;
import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

public class Server {

    public static void main(String[] args) throws IOException {
        
        // Inicializar carpetas de proyecto
        ManejadorArchivo.initializeFolders();
        
        // El server esta escuchando en el puerto 5056
        ServerSocket ss = new ServerSocket(7777);

        // Ciclo infinito para obtener respuestas de clientes
        while (true) {
            Socket s = null;

            try {
                // Objeto Socket para recibir request de clientes
                s = ss.accept();

                System.out.println("Un nuevo cliente esta conectado : " + s);

                // Obtener streams de entrada y salida
                DataInputStream dis = new DataInputStream(s.getInputStream()); //Entrada
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); //Salida

                System.out.println("Asignando nuevo hilo a este cliente");

                // Crear un nuevo objeto Thread
                Thread t = new ClientHandler(s, dis, dos);

                // Invocar el metodo start
                t.start();

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }
}
