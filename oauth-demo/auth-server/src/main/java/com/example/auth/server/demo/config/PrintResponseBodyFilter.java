package com.example.auth.server.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class PrintResponseBodyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CustomResponseWrapper responseWrapper = new CustomResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);
        // 응답 본문을 디버그 콘솔에 출력
        String responseBody = responseWrapper.getResponseBody();
        log.debug("print:" + responseBody); // 또는 디버그 창에서 확인
        // 원래 응답 객체에 동일한 응답 본문을 다시 작성
        response.getOutputStream().write(responseBody.getBytes("UTF-8"));
        response.getOutputStream().flush();
    }

    public static class CustomResponseWrapper extends HttpServletResponseWrapper {
        private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        private PrintWriter writer = new PrintWriter(byteArrayOutputStream);

        public CustomResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() {
            return writer;
        }


        public String getResponseBody() {
            writer.flush();
            return byteArrayOutputStream.toString();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener listener) {

                }

                @Override
                public void write(int b) throws IOException {
                    byteArrayOutputStream.write(b);
                }
            };
        }
    }

}
