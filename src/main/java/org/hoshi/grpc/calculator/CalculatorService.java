package org.hoshi.grpc.calculator;

import com.google.protobuf.Int32Value;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import org.hoshi.grpc.calculator.generated.CalculatorGrpc;
import org.hoshi.grpc.calculator.generated.CalculatorRequest;
import org.hoshi.grpc.calculator.generated.CalculatorResponse;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public final class CalculatorService extends CalculatorGrpc.CalculatorImplBase {
    @Override
    public void add(final CalculatorRequest request, final StreamObserver<CalculatorResponse> responseObserver) {
        final CalculatorResponse response =
                CalculatorResponse
                        .newBuilder()
                        .setResult(request.getX() + request.getY())
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }



    @Override
    public void sub(final CalculatorRequest request, final StreamObserver<CalculatorResponse> responseObserver) {
        final CalculatorResponse response =
                CalculatorResponse
                        .newBuilder()
                        .setResult(request.getX() - request.getY())
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }



    @Override
    public void mul(final CalculatorRequest request, final StreamObserver<CalculatorResponse> responseObserver) {
        final CalculatorResponse response =
                CalculatorResponse
                        .newBuilder()
                        .setResult(request.getX() * request.getY())
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }



    @Override
    public void div(final CalculatorRequest request, final StreamObserver<CalculatorResponse> responseObserver) {
        final CalculatorResponse response =
                CalculatorResponse
                        .newBuilder()
                        .setResult(request.getX() / request.getY())
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }



    private static final Random RANDOM_GENERATOR = new Random();

    @Override
    public void random(final Int32Value request, final StreamObserver<Int32Value> responseObserver) {
        // stream requested number of random numbers
        RANDOM_GENERATOR
                .ints(request.getValue())
                .forEach(i -> {
                    responseObserver.onNext(Int32Value.newBuilder().setValue(i).build());

                    try {
                        Thread.sleep(RANDOM_GENERATOR.nextInt(1000));
                    } catch (InterruptedException e) {
                        // empty
                    }
                });

        // tell client streaming is over
        responseObserver.onCompleted();
    }



    @Override
    public StreamObserver<Int32Value> max(final StreamObserver<Int32Value> responseObserver) {
        return new StreamObserver<Int32Value>() {
            final AtomicInteger max = new AtomicInteger(0);

            @Override
            public void onNext(Int32Value int32Value) {
                if (int32Value.getValue() > max.get()) {
                    max.set(int32Value.getValue());
                    responseObserver.onNext(int32Value);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                // ignore
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
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
