package com.netty.webscoket.nettyDemo.v14.util;

import java.io.IOException;

import org.jboss.marshalling.ByteOutput;
import org.jboss.marshalling.Marshaller;

import com.netty.webscoket.nettyDemo.v14.pojo.ChannelBufferByteOutput;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class MarshallingEncoder {
	
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	
	Marshaller marshaller;

	public MarshallingEncoder() throws IOException {
		this.marshaller = MarshallingCodeCFactory.buildMarshalling();
	}



	protected void encode(Object msg,ByteBuf out) throws IOException{
		try {
			//1. 获取写入位置
			int lengthPos = out.writerIndex();
			//2. 先写入4个bytes，用于记录Object对象编码后长度
			out.writeBytes(LENGTH_PLACEHOLDER);
			//3. 使用代理对象，防止marshaller写完之后关闭byte buf
			ByteOutput output = new ChannelBufferByteOutput(out);
			//4. 开始使用marshaller往bytebuf中编码
			marshaller.start(output);
			marshaller.writeObject(msg);
			//5. 结束编码
			marshaller.finish();
			//6. 设置对象长度
			out.setIndex(lengthPos, out.writerIndex() -lengthPos - 4);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			marshaller.close();
		}
	}
}
