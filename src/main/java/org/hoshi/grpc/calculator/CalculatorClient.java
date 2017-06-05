package org.hoshi.grpc.calculator;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.hoshi.grpc.calculator.generated.AddRequest;
import org.hoshi.grpc.calculator.generated.AddResponse;
import org.hoshi.grpc.calculator.generated.CalculatorGrpc;

import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    public static void main(String[] args) {
        final ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 1307)
                        .usePlaintext(true)
                        .idleTimeout(1, TimeUnit.MINUTES)
                        .build();

        final CalculatorGrpc.CalculatorBlockingStub client =
                CalculatorGrpc.newBlockingStub(channel);

        final AddRequest request =
                AddRequest
                        .newBuilder()
                        .setX(10)
                        .setY(-3)
                        .build();

        final AddResponse response = client.add(request);

        System.out.println(response.getZ());
    }
}
