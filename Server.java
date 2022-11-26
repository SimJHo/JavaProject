package BoardNet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Server extends Thread{

    public static ArrayList<Socket> clients = new ArrayList<>(5);
    private Socket socket;
    static ArrayList<HashMap<String, Object>> boardTable;
    InputStream in = null;
    DataInputStream fromClient = null;
    OutputStream out = null;
    DataOutputStream toClient = null;

    int page = 1;
    int pageSize = 5;

    public Server(Socket socket){
        this.socket = socket;
    }

    public void remove(Socket socket){
        for (Socket s : Server.clients) {
            if(socket == s){
                Server.clients.remove(socket);
                break;
            }
        }
    }

    public void read(ArrayList<HashMap<String, Object>> boardTable){
        try {
            in = socket.getInputStream();
            fromClient = new DataInputStream(in);

            while(true){
                for (Socket s : Server.clients) {
                    if(socket == s){
                        out = s.getOutputStream();
                        toClient = new DataOutputStream(out);

                        toClient.writeUTF("조회할 글 번호 >>");

                        int boardNo = Integer.parseInt(fromClient.readUTF()) -1 ;

                        toClient.writeUTF("==========================================================================================");
                        toClient.writeUTF("글제목 : " + boardTable.get(boardNo).get("Title"));
                        toClient.writeUTF("------------------------------------------------------------------------------------------");
                        toClient.writeUTF("작성자 : " + boardTable.get(boardNo).get("User_Name"));
                        toClient.writeUTF("------------------------------------------------------------------------------------------");
                        toClient.writeUTF("작성일 : " + boardTable.get(boardNo).get("Date"));
                        toClient.writeUTF("------------------------------------------------------------------------------------------");
                        toClient.writeUTF("글내용 : " + boardTable.get(boardNo).get("Content"));
                        toClient.writeUTF("==========================================================================================");
                        toClient.writeUTF("1.목록으로");

                        int input = Integer.parseInt(fromClient.readUTF());

                        if(input == 1){
                            break;
                        }
                    }
                }
                break;
            }
        }catch (Exception e){}

    }

    public void write(ArrayList<HashMap<String, Object>> boardTable){
        try {
            in = socket.getInputStream();
            fromClient = new DataInputStream(in);


            while(true){
                for (Socket s : Server.clients) {
                    if(socket == s){
                        out = s.getOutputStream();
                        toClient = new DataOutputStream(out);
                        HashMap<String, Object> board = new HashMap<>();
                        int max = 0;

                        for(int i = 0; i < boardTable.size(); i++){
                            if(max < (int)boardTable.get(i).get("Board_NO")){
                                max = (int)boardTable.get(i).get("Board_NO");
                            }
                        }
                        board.put("Board_NO", max+1);

                        toClient.writeUTF("글제목 >> ");
                        board.put("Title", fromClient.readUTF());

                        toClient.writeUTF("글내용 >> ");
                        board.put("Content", fromClient.readUTF());

                        toClient.writeUTF("작성자 >> ");
                        board.put("User_Name", fromClient.readUTF());

                        board.put("Date", new Date());

                        boardTable.add(board);
                        break;
                    }
                }
                break;
            }
        }catch (Exception e){}
    }

    public void update(ArrayList<HashMap<String, Object>> boardTable){
        try {
            in = socket.getInputStream();
            fromClient = new DataInputStream(in);

            while(true){
                for (Socket s : Server.clients) {
                    if(socket == s){
                        out = s.getOutputStream();
                        toClient = new DataOutputStream(out);
                        HashMap<String, Object> board;

                        toClient.writeUTF("수정할 글 번호 >>");

                        int boardNo = Integer.parseInt(fromClient.readUTF()) -1;

                        board = boardTable.get(boardNo);

                        toClient.writeUTF("수정할 글제목 >>");
                        board.put("Title", fromClient.readUTF());

                        toClient.writeUTF("수정할 글내용 >>");
                        board.put("Content", fromClient.readUTF());

                        break;
                    }
                }
                break;
            }
        }catch (Exception e){}
    }

    public void delete(ArrayList<HashMap<String, Object>> boardTable){
        try {
            in = socket.getInputStream();
            fromClient = new DataInputStream(in);

            while(true){
                for (Socket s : Server.clients) {
                    if(socket == s){
                        out = s.getOutputStream();
                        toClient = new DataOutputStream(out);
                        HashMap<String, Object> board;

                        toClient.writeUTF("삭제할 글 번호 >>");

                        int boardNo = Integer.parseInt(fromClient.readUTF()) -1;

                        boardTable.remove(boardNo);

                        toClient.writeUTF(boardNo + "번 글이 삭제되었습니다.");

                        break;
                    }
                }
                break;
            }
        }catch (Exception e){}
    }

    @Override
    public void run(){

        try {
            System.out.println(socket);

            in = socket.getInputStream();
            fromClient = new DataInputStream(in);

            out = socket.getOutputStream();
            toClient = new DataOutputStream(out);

            while(true){
                for (Socket s : Server.clients) {
                    if(socket == s){

                        out = s.getOutputStream();
                        toClient = new DataOutputStream(out);

                        toClient.writeUTF("==========================================================================================");
                        toClient.writeUTF(" NO |              Title             |         Name         |             date            ");
                        toClient.writeUTF("------------------------------------------------------------------------------------------");
                        for (int i = boardTable.size() - (1+(pageSize * (page - 1))); i >= boardTable.size() - (pageSize * (page)); i-- ){
                            if(i < 0){
                                break;
                            }
                            toClient.writeUTF(String.format("%-3s", boardTable.get(i).get("Board_NO")) + " | "
                                                + String.format("%-30s", boardTable.get(i).get("Title")) + " | "
                                                + String.format("%-20s", boardTable.get(i).get("User_Name")) + " | "
                                                + boardTable.get(i).get("Date"));
                        }
                        toClient.writeUTF("------------------------------------------------------------------------------------------");
                        toClient.writeUTF(" 현재 페이지 : " + page + " | 총 페이지 수 : " + ((boardTable.size() / pageSize) + 1)          );
                        toClient.writeUTF("==========================================================================================");
                        toClient.writeUTF("1.조회  2.작성  3.수정  4.삭제  5.페이지이동  6.새로고침  0.종료");

                        try {
                            int input = Integer.parseInt(fromClient.readUTF());

                            if(input == 1){
                                read(boardTable);
                                break;
                            }else if(input == 2){
                                write(boardTable);
                                break;
                            }else if(input == 3){
                                update(boardTable);
                                break;
                            }else if(input == 4){
                                delete(boardTable);
                                break;
                            }else if(input == 5){
                                toClient.writeUTF("이동할 페이지 >>");
                                page = Integer.parseInt(fromClient.readUTF());
                                if(page <= 0 || page > ((boardTable.size() / pageSize) + 1)){
                                    toClient.writeUTF("잘못된 페이지 번호입니다.");
                                    page = 1;
                                    run();
                                }else {
                                    run();
                                }
                            }else if(input == 6){
                                run();
                            }else if(input == 0){
                                toClient.writeUTF("연결을 종료합니다.");
                                s.close();
                                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\sim\\BoardData.txt"));
                                oos.writeObject(boardTable);
                                oos.close();
                            }else {
                                toClient.writeUTF("잘못 입력하셨습니다.");
                                run();
                            }
                        }catch (Exception e){
                            toClient.writeUTF("잘못 입력하셨습니다.");
                            run();
                        }
                    }
                }
            }
        }catch (Exception e){
        }finally {
            try {
                if(socket != null){
                    socket.close();
                    remove(socket);
                }
                fromClient = null;
                toClient = null;
            }catch (Exception e){}
        }
    }

    public static void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println(serverSocket);

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\sim\\BoardData.txt"));
            boardTable = (ArrayList<HashMap<String, Object>>) ois.readObject();
            ois.close();

            while (true){
                Socket client = serverSocket.accept();
                clients.add(client);

                Server myServer = new Server(client);

                myServer.start();
            }
        }
        catch (Exception e){}
    }
}
