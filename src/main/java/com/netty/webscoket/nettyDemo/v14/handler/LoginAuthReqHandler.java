package com.netty.webscoket.nettyDemo.v14.handler;

import com.netty.webscoket.nettyDemo.v14.pojo.Header;
import com.netty.webscoket.nettyDemo.v14.pojo.MessageType;
import com.netty.webscoket.nettyDemo.v14.pojo.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.fireExceptionCaught(cause);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(buildLoginReq());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage message = (NettyMessage)msg;
		//如果是握手应答消息，需要判断是否认证成功
		if(message.getHeader() != null 
				&& message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
			byte loginResult = (byte)message.getBody();
			if(loginResult != (byte)0) {
				//握手失败，关闭连接
				ctx.close();
			}else {
				log.info("login is ok :{}",message);
				ctx.fireChannelRead(message);
			}
		}else {
			ctx.fireChannelRead(message);
		}
	}
	
	private NettyMessage buildLoginReq() {
		NettyMessage nettyMessage = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_REQ.value());
		nettyMessage.setHeader(header);
		return nettyMessage;
	}

}
