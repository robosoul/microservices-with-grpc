package org.hoshi.grpc.calculator;

import com.google.protobuf.Int32Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.hoshi.grpc.calculator.generated.CalculatorGrpc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomAsyncClientApp {
    public static void main(String[] args) throws Exception {
        final ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 1307)
                        .usePlaintext(true)
                        .idleTimeout(1, TimeUnit.MINUTES)
                        .build();

        final CalculatorGrpc.CalculatorStub client =
                CalculatorGrpc.newStub(channel);

        final Int32Value howMany =
                Int32Value
                        .newBuilder()
                        .setValue(13)
                        .build();

        System.out.println("Sending request.");

        // execute async request
        final AtomicBoolean isDone = new AtomicBoolean(false);
        client.random(howMany, new StreamObserver<Int32Value>() {
            @Override
            public void onNext(Int32Value int32Value) {
                System.out.println("Got = " + int32Value.getValue());
            }

            @Override
            public void onError(final Throwable th) {
                isDone.set(true);
                System.err.println("Error = " + th.getMessage());

            }

            @Override
            public void onCompleted() {
                System.out.println("Streaming responses done.");
                isDone.set(true);
            }
        });

        System.out.println("Request sent!");

        while (!isDone.get()) {
            Thread.sleep(50L);
        }
    }
}
