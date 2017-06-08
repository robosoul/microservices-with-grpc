package org.hoshi.grpc.calculator;

import com.google.protobuf.Int32Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.hoshi.grpc.calculator.generated.CalculatorGrpc;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MaxAsyncClientApp {
    public static void main(String[] args) throws Exception {
        final ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 1307)
                        .usePlaintext(true)
                        .idleTimeout(1, TimeUnit.MINUTES)
                        .build();

        final CalculatorGrpc.CalculatorStub client =
                CalculatorGrpc.newStub(channel);

        // sed initial request (simply to setup response and request stream observers / monitors)
        final AtomicBoolean isDone = new AtomicBoolean(false);
        final StreamObserver<Int32Value> requests = client.max(new StreamObserver<Int32Value>() {
            @Override
            public void onNext(final Int32Value int32Value) {
                System.out.println("Max = " + int32Value.getValue());
            }

            @Override
            public void onError(final Throwable th) {
                isDone.set(true);
                System.out.println("Error = " + th);
            }

            @Override
            public void onCompleted() {
                isDone.set(true);
                System.out.println("Streaming responses done.");
            }
        });


        System.out.println("Streaming requests.");
        Arrays.asList(3, 2, 1, 4, 5, 9, 6, 8, 7, 10).forEach(i -> {
            requests.onNext(Int32Value.newBuilder().setValue(i).build());
        });

        System.out.println("Streaming requests done.");
        requests.onCompleted();

        while (!isDone.get()) {
            Thread.sleep(50L);
        }
    }
}
