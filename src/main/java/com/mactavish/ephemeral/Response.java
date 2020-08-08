package com.mactavish.ephemeral;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Response {
    private DefaultFullHttpResponse response;

    public boolean text(String text,String contentType,int statusCode){
        if(this.response!=null){
            return false;
        }

        var buf=ByteBufAllocator.DEFAULT.buffer();
        var bytes=text.getBytes(StandardCharsets.UTF_8);
        buf.writeBytes(bytes);
        this.response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode),buf);
        this.response.headers().add(HttpHeaderNames.CONTENT_TYPE,String.format("%s; charset=utf-8", contentType));
        this.response.headers().add(HttpHeaderNames.CONTENT_LENGTH,bytes.length);
        return true;
    }

    public boolean html(String html,int statusCode){
        return text(html,"text/html",statusCode);
    }

    public boolean html(String html){
        return html(html,200);
    }

    public boolean json(Object any,int statusCode){
        return text(JSON.toJSONString(any),"application/json",statusCode);
    }

    public boolean json(Object any){
        return json(any,200);
    }

    public ChannelFuture writeTo(ChannelHandlerContext ctx){
        return ctx.writeAndFlush(this.response);
    }
}
