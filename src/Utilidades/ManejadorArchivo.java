package Utilidades;

import java.io.File;

public class ManejadorArchivo {

    public final static String mainDirectory = "C:\\CloudNetwork";
    public final static String configDirectory = "C:\\CloudNetwork\\config";
    public final static String usersPrivateDirectory = "C:\\CloudNetwork\\usersFiles";
    public final static String usersDownloadDirectory = "C:\\CloudNetwork\\usersDownloads";
    public final static String sharedDirectory = "C:\\CloudNetwork\\sharedFiles";

    public static void initializeFolders() {
        File f = new File(mainDirectory);
        f.mkdir();

        f = new File(configDirectory);
        f.mkdir();
        
        f = new File(usersPrivateDirectory);
        f.mkdir();

        f = new File(usersDownloadDirectory);
        f.mkdir();

        f = new File(sharedDirectory);
        f.mkdir();
    }
    
    public static void initializeUserFolder(String userName) {
        File f = new File(usersPrivateDirectory + "\\" + userName);
        f.mkdir();
        
        f = new File(usersDownloadDirectory + "\\" + userName);
        f.mkdir();
    }

}
