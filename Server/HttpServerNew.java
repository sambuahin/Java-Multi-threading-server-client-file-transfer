
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * Created by sam on 9/12/17.
 */



public class HttpServerNew {
    private static ServerSocket serverSocket;
    private static int port;

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter port number: \n");
        port=in.nextInt();
        serverSocket=new ServerSocket(port);


        System.out.println("WAITING FOR CLIENT TO CONNECT");
        while (true) {
            try {
                Socket client=serverSocket.accept();
                new HttpRequest(client);
            }
            catch (Exception x) {
                System.out.println(x);
            }
        }
    }
    }


class HttpRequest extends Thread {
    private static Socket socket;
    private static String filename;
    private static String command;
    static FileInputStream fis = null;
    static BufferedInputStream bis = null;
    static OutputStream os = null;

    private final static int filesize = 6022386;
    static int bytesRead;
    static int current = 0;
    static FileOutputStream fos = null;
    static boolean fileExists = true;
    static String httpStatus;
    static String fileType;
    static String filecheck;
    static String filecheckThere;

    static BufferedOutputStream bos = null;



    public HttpRequest(Socket client) {
        socket=client;
        start();
        System.out.println("Client Connected");
    }


    public void run() {
        try {
            DataInputStream din=new DataInputStream(socket.getInputStream());
            DataOutputStream dout=new DataOutputStream(socket.getOutputStream());

            command= din.readUTF();//receive comand from client
            command=command.toUpperCase();//change command to uppercase

            filename= din.readUTF();//receive filename from client

            filename= System.getProperty("user.dir")+"/"+filename;


            //checks of the file is availbale by trying to open it and set bool to false if it is not
            try {
                fis = new FileInputStream(filename);
                System.out.println("File found on Server\n");
                filecheck="yes";

            } catch (FileNotFoundException e) {
                System.out.println("File not found on server\n");
                filecheck="no";
            }
            dout.writeUTF(filecheck);
            dout.flush();

            if (command.equals("GET")&&(filecheck.equals("yes")))
            {
                httpStatus = "HTTP/1.1 200 OK"; //common success message
                System.out.println(httpStatus);//print out status

                fileType = "Content-type: " + fileType(filename) + " \n";
                System.out.println(fileType);

                dout.writeUTF(httpStatus);
                dout.flush();
                dout.writeUTF(fileType);
                dout.flush();

            }

            if (command.equals("GET")&&(filecheck.equals("no")))
            {
                httpStatus = "HTTP/1.1 404 Not Found"; //common success message
                System.out.println(httpStatus);//print out status

                fileType = "Content-type: " + fileType(filename) + " \n";
                System.out.println(fileType);

                dout.writeUTF(httpStatus);
                dout.flush();
                dout.writeUTF(fileType);
                dout.flush();

            }




            System.out.println("Command is : " + command +".\n");
            System.out.println("Filename is: " + filename +".\n");

            dout.writeUTF("Command received by server");
            System.out.println("A " + command + " command has been received to send/receive filename " +filename + "\n");
            dout.flush();


           // DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            byte[] nameInBytes = filename.getBytes("UTF-8");

            dout.writeInt(nameInBytes.length);
            dout.flush();

            dout.write(nameInBytes);
            dout.flush();
            //dout.close();

            //check if command it is valid
            if (command.equals("GET")){

                System.out.println("Does file exist? "+filecheck);

                if (filecheck.equals("yes")) {

                    try {
                        System.out.println("About to send file");
                        sendFile();

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("File sending error\n");
                    }

                }
                else {
                    httpStatus = "HTTP/1.1 404 Not Found";//common error message
                    fileType = "Content-type: " + fileType(filename) + " \n";

                    //printing status on server
                    System.out.println(httpStatus);
                    System.out.println(fileType);

                }

            }

            //end os get
            if (command.equals("PUT")){

                filecheckThere=din.readUTF();

                if (filecheckThere.equals("yes")){
                try {
                    receiveFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Receiving file failed");
                }}
                else{
                    System.out.println("File not found on client");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    System.out.println("Client Closing");
           //dout.close();
    }

    @SuppressWarnings("Duplicates")
    //return the file types
    private static String fileType(String fileName)
    {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";}
        if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";}
        if(fileName.endsWith(".gif")) {
            return "image/gif";}
        if(fileName.endsWith(".pdf")) {
            return "doc/pdf";}
        if(fileName.endsWith(".txt")) {
            return "doc/txt";}

        return "Maybe an application/Unknown file";

    }


    //set up input output streams

    @SuppressWarnings("Duplicates")
    private static void sendFile() throws Exception
    {
       try {
            // send file

            File myFile = new File (filename);
            byte [] mybytearray  = new byte [(int)myFile.length()];
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
            os = socket.getOutputStream();
            System.out.println("Sending " + filename + "(" + mybytearray.length + " bytes)");
            os.write(mybytearray,0,mybytearray.length);
            os.flush();
            System.out.println("File Sent.");
       }
        finally {
           if (bis != null) bis.close();
           if (os != null) os.close();
           if (socket!=null) socket.close();
       }
    }

    @SuppressWarnings("Duplicates")

    private static void receiveFile() throws Exception
    {
        try {
            // receive file
            byte [] mybytearray  = new byte [filesize];
            InputStream is = socket.getInputStream();
            fos = new FileOutputStream(filename);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;

            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(mybytearray, 0 , current);
            bos.flush();
            System.out.println("File " + filename
                    + " downloaded (" + current + " bytes read)");
        }
        finally {
            if (fos != null) fos.close();
            if (bos != null) bos.close();
            if (socket != null) socket.close();
        }
    }
}
