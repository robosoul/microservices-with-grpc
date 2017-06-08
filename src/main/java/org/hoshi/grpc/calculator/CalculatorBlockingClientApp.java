package org.hoshi.grpc.calculator;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.hoshi.grpc.calculator.generated.CalculatorGrpc;
import org.hoshi.grpc.calculator.generated.CalculatorRequest;

import java.util.concurrent.TimeUnit;

public class CalculatorBlockingClientApp {
    public static void main(String[] args) {
        final ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 1307)
                        .usePlaintext(true)
                        .idleTimeout(1, TimeUnit.MINUTES)
                        .build();

        final CalculatorGrpc.CalculatorBlockingStub client =
                CalculatorGrpc.newBlockingStub(channel);

        final CalculatorRequest request =
                CalculatorRequest
                        .newBuilder()
                        .setX(8)
                        .setY(2)
                        .build();

        System.out.println("Add = " + client.add(request).getResult());
        System.out.println("Sub = " + client.sub(request).getResult());
        System.out.println("Mul = " + client.mul(request).getResult());
        System.out.println("Div = " + client.div(request).getResult());
    }
}
