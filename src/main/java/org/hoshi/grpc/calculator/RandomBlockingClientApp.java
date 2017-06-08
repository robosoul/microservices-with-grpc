package org.hoshi.grpc.calculator;

import com.google.protobuf.Int32Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.hoshi.grpc.calculator.generated.CalculatorGrpc;

import java.util.concurrent.TimeUnit;

public class RandomBlockingClientApp {
    public static void main(String[] args) {
        final ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 1307)
                        .usePlaintext(true)
                        .idleTimeout(1, TimeUnit.MINUTES)
                        .build();

        final CalculatorGrpc.CalculatorBlockingStub client =
                CalculatorGrpc.newBlockingStub(channel);

        final Int32Value howMany =
                Int32Value
                        .newBuilder()
                        .setValue(13)
                        .build();

        System.out.println("Sending request.");

        // blocks until all of the number are streamed
        client.random(howMany).forEachRemaining(int32Value -> {
            System.out.println("Got = " + int32Value.getValue());
        });

        System.out.println("Streaming done.");
    }
}
