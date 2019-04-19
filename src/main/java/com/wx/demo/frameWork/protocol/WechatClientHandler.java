package com.wx.demo.frameWork.protocol;

import com.wx.demo.tools.WechatUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class WechatClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(WechatClientHandler.class);
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private WechatSocket wechatSocket;

    private byte[] packageData;


    public WechatClientHandler(WechatSocket wechatSocket) {
        this.wechatSocket = wechatSocket;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        int length = bytes.length;

        if (packageData != null) {
            byte[] temp = new byte[packageData.length + length];
            System.arraycopy(packageData, 0, temp, 0, packageData.length);
            System.arraycopy(bytes, 0, temp, packageData.length, length);
            packageData = temp;
        } else {
            packageData = new byte[length];
            System.arraycopy(bytes, 0, packageData, 0, length);
        }

        while (packageData != null && packageData.length > 16) {
            int newPackageLength = WechatUtil.byteArrayToInt(packageData, 0);
            if (newPackageLength <= 0 || newPackageLength > 8000000) {//包不合法
                packageData = null;
                break;
            }

            if (newPackageLength > packageData.length) {//包没接受完
                break;
            }

            if (newPackageLength <= packageData.length) {
                byte[] newPackage = new byte[newPackageLength];
                System.arraycopy(packageData, 0, newPackage, 0, newPackageLength);
                if (newPackageLength < packageData.length) {
                    byte[] tempData = new byte[packageData.length - newPackageLength];
                    System.arraycopy(packageData, newPackageLength, tempData, 0, packageData.length - newPackageLength);
                    packageData = tempData;
                } else {
                    packageData = null;
                }

                receiveData(newPackage);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info(dateFormat.format(new Date())+"-channelInactive");

        wechatSocket.connect("",0);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
        logger.info(dateFormat.format(new Date())+"-exceptionCaught");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {//发送心跳
                wechatSocket.sendWechatHeartbeat();
            }
        }

    }

    private void receiveData(byte[] data) {
        if (data.length == 20 && data[3] == 20 && data[5] == 16 && data[7] == 1) {
            // 新消息通知包
            wechatSocket.sendWechatNewMsg();
            return;
        }

        if (data.length >= 16 && data[16] != -65
                && !(data[3] == 58 && data[5] == 16 && data[7] == 1 && data.length == 58)
                && !(data[3] == 47 && data[5] == 16 && data[7] == 1 && data.length == 47)) {
            return;
        }

        wechatSocket.handleCallback(data);
    }
}
