import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.mule.grpc.server.ExchangeMessageGrpc;
import org.mule.grpc.server.MuleMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GrpcProtoTest {
    private ManagedChannel channel;
    public static void main(String[] args) throws InterruptedException {
        GrpcProtoTest client = new GrpcProtoTest();
        client.sendMessage();
        client.shutDown();
    }

    public void sendMessage() {
        Map<String, String> hobbies = new HashMap<String, String>();
        hobbies.put("sprots", "kick-asses");
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext(true)
                .build();
        ExchangeMessageGrpc.ExchangeMessageBlockingStub newBlockingStub = ExchangeMessageGrpc.newBlockingStub(channel);
        MuleMessage request = MuleMessage.newBuilder()
                .setPaylaod(Any.newBuilder().setValue(ByteString.copyFromUtf8("Hello Grpc")))
                .putAllAttributes(hobbies)
                .build();
        MuleMessage response = newBlockingStub.onMuleMessage(request);
        System.out.println("Finished" + response);
    }

    public void shutDown() throws InterruptedException {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
}
