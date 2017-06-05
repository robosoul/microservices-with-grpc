package org.hoshi.grpc.calculator;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import org.hoshi.grpc.calculator.generated.AddRequest;
import org.hoshi.grpc.calculator.generated.AddResponse;
import org.hoshi.grpc.calculator.generated.CalculatorGrpc;

public final class CalculatorService extends CalculatorGrpc.CalculatorImplBase {
    @Override
    public void add(final AddRequest request, final StreamObserver<AddResponse> responseObserver) {
        try {
            final AddResponse response =
                    AddResponse
                            .newBuilder()
                            .setZ(request.getX() + request.getY())
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Throwable th) {
            responseObserver.onError(th);
        }
    }

    public static void main(String[] args) throws Exception {
        final Server server =
                NettyServerBuilder
                        .forPort(1307)
                        .addService(new CalculatorService())
                        .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server != null && !server.isShutdown()) {
                server.shutdown();
            }
        }));

        server.start().awaitTermination();
    }
}
