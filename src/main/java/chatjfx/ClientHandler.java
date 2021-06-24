package chatjfx;

import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private String login;
    private Logger logger;

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public ClientHandler(MyServer myServer, Socket socket, Logger logger) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            this.login = "";
            this.logger = logger;

            ExecutorService timeConnectES = Executors.newSingleThreadExecutor();
            ExecutorService readMessageES = Executors.newSingleThreadExecutor();

            timeConnectES.execute(() -> {
                long endTime = System.currentTimeMillis() + 2 * 60 * 1000;
                logger.info("Start of waiting authentification (2 minutes)");

                while (System.currentTimeMillis() < endTime) {
                    try {
                        Thread.sleep(5 * 1000);
                        if (!this.getName().isEmpty()) {
                            logger.info("Authentification completed successfully");
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }
                }
                if (this.getName().isEmpty()) {
                    sendMsg(ChatConstants.AUTH_TIMEOUT);

                    logger.debug("AUTH_TIMEOUT");
                    closeConnection();
                }
            });
            timeConnectES.shutdown();

            readMessageES.execute(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException | SQLException e) {
                    logger.error("Ошибка в ClientHandler");
                    e.printStackTrace();

                } finally {
                    closeConnection();
                }
            });
            readMessageES.shutdown();

        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public void authentication() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith(ChatConstants.AUTH_COMMAND)) {
                String[] parts = str.split("\\s");

                if (parts.length < 3) {
                    sendMsg("Неверные логин/пароль");
                    logger.debug("Неверные логин/пароль");
                    continue;
                }
                Optional<String> oNick = myServer.getAuthService().getUserNick(parts[1], parts[2]);
                if (oNick.isPresent()) {
                    String nick = oNick.get();
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg(ChatConstants.AUTH_OK + " " + nick);
                        name = nick;
                        login = parts[1];
                        myServer.subscribe(this);
                        myServer.broadcastMsg(name + " зашел в чат");
                        logger.debug(name + " зашел в чат");
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется");
                        logger.debug("Учетная запись уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                    logger.debug("Неверные логин/пароль");
                }
            }
        }
    }

    public void readMessages() throws IOException, SQLException {
        while (true) {
            String strFromClient = in.readUTF();

            logger.trace("от " + name + ": " + strFromClient);

            if (strFromClient.equals(ChatConstants.STOP_WORD)) {
                return;
            } else if (strFromClient.startsWith(ChatConstants.SEND_TO_LIST)) {
                String[] parts = strFromClient.split("\\s");
                if (parts.length == 1) {
                    continue;
                }

                List<String> nickTo = new ArrayList<>();
                List<String> messagePartsToClient = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    if (parts[i].startsWith("/")) {
                        String[] sNick = parts[i].split("/");
                        if (myServer.isNickBusy(sNick[1])) {
                            nickTo.add(sNick[1]);
                        }
                    } else {
                        messagePartsToClient.add(parts[i]);
                    }
                }

                String messageToClient = messagePartsToClient.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(" "));

                myServer.sendMsgToClient(this, nickTo, messageToClient);

            } else if (strFromClient.startsWith(ChatConstants.AUTH_CHANGENICK)) {
                String[] parts = strFromClient.split("\\s");
                if (parts.length < 2) {
                    continue;
                }

                AuthService authService = myServer.getAuthService();
                if (authService.updateUserInfo(this.getLogin(), "nick", parts[1])) {
                    sendMsg(ChatConstants.AUTH_CHANGENICK_OK + " " + parts[1]);
                    myServer.broadcastMsg(name + " сменил ник на " + parts[1]);
                    logger.info(name + " сменил ник на " + parts[1]);
                    name = parts[1];
                }

                myServer.broadcastClientsList();

            } else {
                myServer.broadcastMsg(name + ": " + strFromClient);
            }


        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " вышел из чата");
        logger.debug(name + " вышел из чата");

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
