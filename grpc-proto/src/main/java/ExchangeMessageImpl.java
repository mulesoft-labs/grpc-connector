import io.grpc.stub.StreamObserver;
import org.mule.grpc.server.MuleMessage;

public class ExchangeMessageImpl extends org.mule.grpc.server.ExchangeMessageGrpc.ExchangeMessageImplBase {
    @Override
    public void onMuleMessage(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        System.out.println("Request received in server " + request);
        String payload = request.getPaylaod().getValue().toStringUtf8();
        MuleMessage response = MuleMessage.newBuilder()
                .setPaylaod(request.getPaylaod())
                .putAllAttributes(request.getAttributesMap())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void onMuleError(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        super.onMuleError(request, responseObserver);
    }

    @Override
    public void onRefresh(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        super.onRefresh(request, responseObserver);
    }

    @Override
    public void onLoadCache(MuleMessage request, StreamObserver<MuleMessage> responseObserver) {
        super.onLoadCache(request, responseObserver);
    }
}
