import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;



public class GrpcServer {
    private Server server;


    public static void main(String[] args) throws IOException, InterruptedException {
        GrpcServer grpcServer = new GrpcServer();
        grpcServer.start(grpcServer);
        grpcServer.blockUntilShutdown();

    }

    private void start(GrpcServer grpcServer) throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new ExchangeMessageImpl())
                .build()
                .start();

        MyThread thread = new MyThread(grpcServer);
        Runtime.getRuntime().addShutdownHook(thread);

    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();

        }
    }


    class MyThread extends Thread {
        GrpcServer grpcServer;

        MyThread(GrpcServer grpcServer) {
            this.grpcServer = grpcServer;
        }
        @Override
        public void run() {
            grpcServer.stop();
        }
    }
}
