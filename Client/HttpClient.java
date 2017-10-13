

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Created by Samuel Buahin on 9/12/17.
 */

public class HttpClient {

    static Socket myclient;
    static int port;
    private static String filename;//this will be the name of the file to be sent or received
    private final static int filesize = 6022386;
    static int bytesRead;
    static int current = 0;
    static FileOutputStream fos = null;
    static BufferedOutputStream bos = null;
    static FileInputStream fis = null;
    static BufferedInputStream bis = null;
    static OutputStream os = null;
    static InputStream is = null;
    static String filecheck;
    static String filecheckHere;

    //Main Method Begins
    public static void main(String[]args) throws ClassNotFoundException {

        //Socket sock = null;

        Scanner in = new Scanner(System.in);
        try
        {
            String command;//ths will bw sent to the server - it will be either a GET or PUT
            //String filename;
            System.out.println("Enter host followed by space, port number, space, command, space and filename");

            String host = in.next();
            port = in.nextInt();
            command = in.next();
            filename = in.next();

            command=command.toUpperCase();//change command to uppercase

            //myclient = new Socket("localhost",9999);
            myclient = new Socket(host,port);//
            System.out.println("Connecting to Server...");


            DataInputStream din=new DataInputStream(myclient.getInputStream());//for reading from server
            DataOutputStream dout=new DataOutputStream(myclient.getOutputStream());//outputs to server

            dout.writeUTF(command);//sends the command to server
            dout.flush();//fushes the writer

            while (filename.indexOf("/")==0)
                filename=filename.substring(1);

            dout.writeUTF(filename);//sends the filename to the server
            dout.flush();

            filecheck=din.readUTF();

            //print status from server

            if (command.equals("GET")&&(filecheck.equals("yes"))) {
                System.out.println(din.readUTF());
                System.out.println(din.readUTF());
                System.out.println(din.readUTF());
            }

            if (command.equals("GET")&&(filecheck.equals("no"))) {
                System.out.println(din.readUTF());
                System.out.println(din.readUTF());
                System.out.println(din.readUTF());
            }



            System.out.println(din.readUTF());

          //  if (filecheck.equals("yes")) {

                System.out.println("Waiting on Server");

                //gets the correct file path
                filename = System.getProperty("user.dir") + "/" + filename;

                //check if command it is valid
                if (command.equals("GET")) {
                    if(filecheck.equals("yes"))
                    {
                    System.out.println("Does file exist? " + filecheck);

                        try {
                            receiveFile();
                            System.out.println("Receiving File");

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("File receiving error\n");
                        }}

                    }
              //  }

                //for a PUT command
                if (command.equals("PUT")) {

                    //checks of the file is availbale by trying to open it and set bool to false if it is not

                    try {
                        fis = new FileInputStream(filename);
                        System.out.println("File found on client\n");
                        filecheckHere="yes";

                    } catch (FileNotFoundException e) {
                        System.out.println("File not found on client\n");
                        filecheckHere="no";
                    }
                    //if file
                    //send file found info.
                    dout.writeUTF(filecheckHere);
                    dout.flush();

                    if (filecheckHere.equals("yes"))
                    {
                    try {
                        sendFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("File sending error\n");
                    }}

                    //if not
                    else{

                        System.out.println("HTTP/1.1 404 Not Found");//print out status
                        System.out.println("Content-type: " + fileType(filename) + " \n");
                    }

                }


            //closes the client socket  reader and writer
            System.out.println("Closing...");
            myclient.close();
            dout.close();//closes the writer;
        }

        //exception for try
        catch (IOException IOex)
        {
            System.out.println("\nSorry things didn't go so well. Better Luck next time.");
        }
    }
    @SuppressWarnings("Duplicates")
    //this sends a file
    private static void sendFile() throws Exception
    {
        System.out.println("Ready to send file");
        try {
            // send file
            File myFile = new File (filename);
            byte [] mybytearray  = new byte [(int)myFile.length()];

            fis = new FileInputStream(myFile);

            bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
            os = myclient.getOutputStream();
            System.out.println("Sending " + filename + "(" + mybytearray.length + " bytes)");
            os.write(mybytearray,0,mybytearray.length);
            os.flush();
            System.out.println("File Sent.");

        }
        finally {
            if (bis != null) bis.close();
            if (os != null) os.close();
            if (fis != null) fis.close();
            if (myclient!=null) myclient.close();
        }
    }

    //this receives a file
    private static void receiveFile() throws Exception
    {
        try {
            // receive file
            System.out.println("Ready to receive file");
            byte [] mybytearray  = new byte [filesize];

            is = myclient.getInputStream();
          //  System.out.println("1");
            fos = new FileOutputStream(filename);
            bos = new BufferedOutputStream(fos);
            //System.out.println("2");
            bytesRead = is.read(mybytearray,0,mybytearray.length);
            current = bytesRead;
          //  System.out.println("3");
            do {
                bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);
          //  System.out.println("4");
            bos.write(mybytearray, 0 , current);
           // System.out.println("4");
            bos.flush();
          //  System.out.println("4");
            System.out.println("File " + filename + " downloaded (" + current + " bytes read)");
           // System.out.println("5");
       }
       finally {
           // System.out.println("6");
            if (fos != null) fos.close();
            if (bos != null) bos.close();
          //  System.out.println("7");
            if (myclient != null) myclient.close();
         //   System.out.println("8");
        }
    }
    @SuppressWarnings("Duplicates")
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

}
