package com.wx.demo.frameWork.proto;

import io.grpc.stub.ClientCalls;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
        value = "by gRPC proto compiler (version 1.3.0)",
        comments = "Source: WechatProto.proto")
public final class WechatGrpc {

    private WechatGrpc() {
    }

    public static final String SERVICE_NAME = "WechatProto.Wechat";

    // Static method descriptors that strictly reflect the proto.
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
    public static final io.grpc.MethodDescriptor<WechatMsg,
            WechatMsg> METHOD_HELLO_WECHAT =
            io.grpc.MethodDescriptor.create(
                    io.grpc.MethodDescriptor.MethodType.UNARY,
                    generateFullMethodName(
                            "WechatProto.Wechat", "HelloWechat"),
                    io.grpc.protobuf.ProtoUtils.marshaller(WechatMsg.getDefaultInstance()),
                    io.grpc.protobuf.ProtoUtils.marshaller(WechatMsg.getDefaultInstance()));

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static WechatStub newStub(io.grpc.Channel channel) {
        return new WechatStub(channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static WechatBlockingStub newBlockingStub(
            io.grpc.Channel channel) {
        return new WechatBlockingStub(channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
     */
    public static WechatFutureStub newFutureStub(
            io.grpc.Channel channel) {
        return new WechatFutureStub(channel);
    }

    /**
     */
    public static abstract class WechatImplBase implements io.grpc.BindableService {

        /**
         */
        public void helloWechat(WechatMsg request,
                                io.grpc.stub.StreamObserver<WechatMsg> responseObserver) {
            asyncUnimplementedUnaryCall(METHOD_HELLO_WECHAT, responseObserver);
        }

        @Override
        public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            METHOD_HELLO_WECHAT,
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            WechatMsg,
                                            WechatMsg>(
                                            this, METHODID_HELLO_WECHAT)))
                    .build();
        }
    }

    /**
     */
    public static final class WechatStub extends io.grpc.stub.AbstractStub<WechatStub> {
        private WechatStub(io.grpc.Channel channel) {
            super(channel);
        }

        private WechatStub(io.grpc.Channel channel,
                           io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected WechatStub build(io.grpc.Channel channel,
                                   io.grpc.CallOptions callOptions) {
            return new WechatStub(channel, callOptions);
        }

        /**
         */
        public void helloWechat(WechatMsg request,
                                io.grpc.stub.StreamObserver<WechatMsg> responseObserver) {
            ClientCalls.asyncUnaryCall(
                    getChannel().newCall(METHOD_HELLO_WECHAT, getCallOptions()), request, responseObserver);
        }
    }

    /**
     */
    public static final class WechatBlockingStub extends io.grpc.stub.AbstractStub<WechatBlockingStub> {
        private WechatBlockingStub(io.grpc.Channel channel) {
            super(channel);
        }

        private WechatBlockingStub(io.grpc.Channel channel,
                                   io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected WechatBlockingStub build(io.grpc.Channel channel,
                                           io.grpc.CallOptions callOptions) {
            return new WechatBlockingStub(channel, callOptions);
        }

        /**
         */
        public WechatMsg helloWechat(WechatMsg request) {
            return blockingUnaryCall(getChannel(), METHOD_HELLO_WECHAT, getCallOptions(), request);
        }
    }

    /**
     */
    public static final class WechatFutureStub extends io.grpc.stub.AbstractStub<WechatFutureStub> {
        private WechatFutureStub(io.grpc.Channel channel) {
            super(channel);
        }

        private WechatFutureStub(io.grpc.Channel channel,
                                 io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected WechatFutureStub build(io.grpc.Channel channel,
                                         io.grpc.CallOptions callOptions) {
            return new WechatFutureStub(channel, callOptions);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<WechatMsg> helloWechat(
                WechatMsg request) {
            return futureUnaryCall(
                    getChannel().newCall(METHOD_HELLO_WECHAT, getCallOptions()), request);
        }
    }

    private static final int METHODID_HELLO_WECHAT = 0;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final WechatImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(WechatImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_HELLO_WECHAT:
                    serviceImpl.helloWechat((WechatMsg) request,
                            (io.grpc.stub.StreamObserver<WechatMsg>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(
                io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    private static final class WechatDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
        @Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return wechat.getDescriptor();
        }
    }

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (WechatGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                            .setSchemaDescriptor(new WechatDescriptorSupplier())
                            .addMethod(METHOD_HELLO_WECHAT)
                            .build();
                }
            }
        }
        return result;
    }
}
