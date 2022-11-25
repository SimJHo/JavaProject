package BoardNet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread{
    Socket socket = null;

    public Client(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        InputStream in = null;
        DataInputStream fromServer = null;

        try {
            in = socket.getInputStream();
            fromServer = new  DataInputStream(in);

            while (true){
                System.out.println(fromServer.readUTF());
            }
        }catch (Exception e){
            System.out.println("접속을 종료했습니다.");
            System.exit(0);
        }finally {
            try {
                if(fromServer != null){
                    fromServer.close();
                }
                if(socket != null){
                    socket.close();
                }
            }catch (Exception e){
                System.out.println("접속을 종료했습니다.");
                System.exit(0);
            }
        }
    }

    public static void main(String[] args){
        Socket socket = null;

        try {
            socket = new Socket("192.168.0.33", 9999);
            System.out.println(socket);

            OutputStream out = socket.getOutputStream();
            DataOutputStream toServer = new DataOutputStream(out);

            Client Client = new Client(socket);

            Client.start();

            Scanner scanner = new Scanner(System.in);

            while (true){
                toServer.writeUTF(scanner.nextLine());
            }

        }catch (Exception e){
            System.out.println("접속을 종료했습니다.");
            System.exit(0);
        }finally {
            try {
                if(socket != null){
                    socket.close();
                }
            }catch (Exception e){
                System.out.println("접속을 종료했습니다.");
                System.exit(0);
            }
        }
    }
}