package github.mobai.remoting.transport.netty.client;

import github.mobai.enums.CompressTypeEnum;
import github.mobai.enums.SerializationTypeEnum;
import github.mobai.extension.ExtensionLoader;
import github.mobai.factory.SingletonFactory;
import github.mobai.registry.ServiceDiscovery;
import github.mobai.remoting.constants.RpcConstants;
import github.mobai.remoting.dto.RpcMessage;
import github.mobai.remoting.dto.RpcRequest;
import github.mobai.remoting.dto.RpcResponse;
import github.mobai.remoting.transport.RpcRequestTransport;
import github.mobai.remoting.transport.netty.codec.RpcMessageDecoder;
import github.mobai.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Netty 客户端传输类
 *
 * @author mobai
 * @date 2022/3/2
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;
    /**
     * 请求缓存，通过requestId设置对应的请求的响应结果
     */
    private final UnprocessedRequests unprocessedRequests;
    /**
     * Channel缓存，通过目标ip查找对应的Channel连接
     */
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接的超时时间，超过这个时间还是建立不上则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                // 如果5秒内没有数据发送到服务端，则发送一个心跳请求
                                .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                                .addLast(new RpcMessageEncoder())
                                .addLast(new RpcMessageDecoder())
                                .addLast(new NettyRpcClientHandler());
                    }
                });
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // 构建返回值
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // 获取服务端地址
        InetSocketAddress serverAddress = serviceDiscovery.lookupService(rpcRequest);
        // 获取服务端对应的Channel
        Channel channel = getChannel(serverAddress);
        if (channel.isActive()) {
            // 缓存未返回的结果
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            // 构建请求消息
            RpcMessage rpcRequestMessage = RpcMessage.builder()
                    .data(rpcRequest)
                    .codec(SerializationTypeEnum.KYRO.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcRequestMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcRequestMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }

    /**
     * 存在Channel直接返回，不存在则进行连接然后缓存Channel
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (null == channel) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
