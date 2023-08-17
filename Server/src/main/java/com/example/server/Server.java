package com.example.server;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedWriter bw;
    private BufferedReader br;
    connectJDBC connectJDBC = new connectJDBC();
    Connection connection = connectJDBC.connection();
    private ObjectOutputStream oos;
    private ArrayList<String> list;
    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        try {
            this.socket = serverSocket.accept();
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.list = new ArrayList<String>();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void receiveMessageFromClient(VBox vBox) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()){
                    try {
                        String message = br.readLine();
                        saveMessageReceive(message);
                        HelloController.addLabel(message,vBox);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void sendMessageToClient(String message) {
        try {
            saveMessageSend(message);
            bw.write(message);
            bw.newLine();
            bw.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void saveMessageSend(String message){
        if (message != null){
            String query = "INSERT INTO message(messageSend)VALUES (?)";
            PreparedStatement pstm = null;
            try {
                pstm = connection.prepareStatement(query);
                pstm.setString(1,message);
                pstm.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    public void saveMessageReceive(String message){
        if (message != null){
            String query = "INSERT INTO message(messageReceive) VALUES (?)";
            PreparedStatement pstm = null;
            try {
                pstm = connection.prepareStatement(query);
                pstm.setString(1,message);
                pstm.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    public void displayHistory(VBox vBox){
        String query = "SELECT * FROM message";
        Statement stm = null;
        try {
            stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()){
                String send = rs.getString("messageSend");
                String receive = rs.getString("messageReceive");
                list.add(send + "//" + receive);
                if (send != null){
                    HBox hBox = new HBox();

                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5,5,5,10));

                    Text text = new Text(send);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-color: rgb(239,242,255); -fx-background-color: rgb(15,125,242); -fx-background-radius: 20px");
                    textFlow.setPadding(new Insets(5,10,5,10));
                    text.setFill(Color.color(0.934,0.945,0.996));

                    hBox.getChildren().add(textFlow);
                    vBox.getChildren().add(hBox);
                }
                if (receive !=  null){
                    HBox hBox = new HBox();

                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setPadding(new Insets(5,5,5,10));

                    Text text = new Text(receive);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-background-color: rgb(233,233,235); -fx-background-radius: 20px");
                    textFlow.setPadding(new Insets(5,10,5,10));

                    hBox.getChildren().add(textFlow);
                    vBox.getChildren().add(hBox);
                }
            }
            oos.writeObject(list);
            oos.flush();
        }catch (SQLException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
