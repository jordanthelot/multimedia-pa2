import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
//Java classes used to read and write data types into machine ready binary format
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;

//Code and documentation from https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html

public class server {
    public static void main(String[] args) {
        if(args.length != 1){
            System.err.println("Please input correct command");
            return;
        }
        //retrieving portnumber
        int portNumber;
        try{
            portNumber = Integer.parseInt(args[0]);
            if(portNumber < 1 || portNumber > 65535){
                throw new NumberFormatException();
            }
        }catch( NumberFormatException e){
            System.err.println("Port must be an integer");
            return;
        }
        //If the server successfully binds to its port, then the ServerSocket object is successfully created and the server continues to the next step—accepting a connection from a client (the next statement in the try-with-resources statement):
    try(ServerSocket serverSocket = new ServerSocket(portNumber)){
        System.out.println("Server listening on port " + portNumber);
        //keep accepting clients until one says bye; a client that drops without bye does not stop the server
        while(true){
            try(
                Socket clientSocket = serverSocket.accept();
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            ){
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                out.writeUTF("Hello!");
                out.flush();
                String inputline;
                while(true){
                    inputline = in.readUTF();
                    System.out.println("Client Command: " + inputline);
                    if (inputline.equals("bye")){
                        out.writeUTF("disconnected");
                        out.flush();
                        System.out.println("Client said bye, shutting down server");
                        //returning closes the client socket and the server socket (try-with-resources), then the program exits
                        return;
                    }

                    String filename;

                    switch(inputline){
                        case "JPEG 1" :
                            filename = "image1.jpg";
                            break;
                        case "JPEG 2" :
                            filename = "image2.jpg";
                            break;
                        case "JPEG 3" :
                            filename = "image3.jpg";
                            break;
                        default:
                            out.writeUTF("Error: invalid command");
                            out.flush();
                            continue;
                    }
                    byte[] image;
                    long start = System.nanoTime();
                    try{
                        image = Files.readAllBytes(Paths.get(filename));
                    }catch(IOException e){
                        out.writeUTF("Error: could not read " + filename);
                        out.flush();
                        continue;
                    }
                    double time = (System.nanoTime() - start) / 1_000_000.0;
                    System.out.printf("Took %.3f ms%n", time);

                    out.writeUTF("OK");
                    out.writeInt(image.length);
                    out.write(image);
                    out.flush();
                }
            }catch(IOException e){
                //client closed the connection without saying bye, wait for the next one
                System.out.println("Client disconnected");
            }
        }
    }catch(IOException e){
        System.err.println("Server error: " + e.getMessage());
    }
    }
}
