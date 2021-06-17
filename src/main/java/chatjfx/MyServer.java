package chatjfx;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyServer {

    private List<ClientHandler> clients;
    private AuthService authService;
    private static final Logger logger = LogManager.getLogger(MyServer.class.getName());

    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try (ServerSocket serverSocket = new ServerSocket(ChatConstants.PORT)) {
            logger.info("Сервер запущен");

            authService = new DbAuthService(logger);
            authService.start();

            clients = new ArrayList<>();
            while (true) {
                logger.info("Сервер ожидает подключение");

                Socket socket = serverSocket.accept();
                logger.info("Соединение с клиентом установленно");

                new ClientHandler(this, socket, logger);
            }
        } catch (IOException e) {
            e.printStackTrace();

            logger.fatal("Ошибка в работе сервера");
            logger.fatal(e.getMessage());
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public synchronized void sendMsgToClient(ClientHandler from, List<String> nickTo, String msg) {
        List<ClientHandler> listClients = clients.stream().filter(e -> nickTo.contains(e.getName())).collect(Collectors.toList());

        for (ClientHandler ls : listClients) {
            ls.sendMsg("личное сообщение от " + from.getName() + ": " + msg);
        }
        from.sendMsg("личное сообщение для " + Arrays.toString(listClients.stream().map(ClientHandler::getName).toArray()) + ": " + msg);
    }

    public synchronized void broadcastClientsList() {
        String listClients = ChatConstants.CLIENTS_LIST + " " + clients.stream().map(ClientHandler::getName).collect(Collectors.joining(" "));
        broadcastMsg(listClients);
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
        broadcastClientsList();
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
        broadcastClientsList();
    }


}
