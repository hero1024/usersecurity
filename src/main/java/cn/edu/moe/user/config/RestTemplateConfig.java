package cn.edu.moe.user.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean("noSSLRestTemplate")
    public RestTemplate noSSLRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        // 超时
        factory.setConnectionRequestTimeout(5000);
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        try {
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(createIgnoreVerifySSL(),
                    // 指定TLS版本
                    null,
                    // 指定算法
                    null,
                    // 取消域名验证
                    (string, ssls) -> true);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            factory.setHttpClient(httpClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RestTemplate(factory);
    }

    private SSLContext createIgnoreVerifySSL() throws Exception {
        SSLContext sc = SSLContext.getInstance("TLS");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }
}

