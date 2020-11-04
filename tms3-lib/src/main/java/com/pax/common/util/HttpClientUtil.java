package com.pax.common.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
	
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	private static final String CLOSE_HTTP_EXCEPTION = "关闭CloseableHttpClient异常！";
	private static final String CHARSET = "UTF-8";
	
	public static String doGet(String url){
		return HttpClientUtil.doGet(url,CHARSET);
	}
	/**
	 * @Description: http的get请求
	 * @param url
	 * @param charsetName
	 * @return
	 * @return: String
	 */
	public static String doGet(String url,String charsetName){
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			
			//设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(10000).build();
			httpGet.setConfig(requestConfig);
			
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = new String(EntityUtils.toByteArray(entity),charsetName);
			}
			httpGet.abort();
		} catch (Exception e) {
			logger.error("HttpClient请求出错！请求地址：{}",url,e);
		} finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error(CLOSE_HTTP_EXCEPTION,e);
			}
		}
		return result;
	}
	/**
	 * @Description: http的post请求
	 * @param url
	 * @param param
	 * @return
	 * @return: String
	 */
	public static String doPost(String url,Map<String,String> param){
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(url);
			
			//设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(10000).build();
			httpPost.setConfig(requestConfig);
			
			List<NameValuePair> nvps = new ArrayList<>();  
			for(Map.Entry<String, String> entry : param.entrySet()){
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
			}
			StringEntity e = new UrlEncodedFormEntity(nvps, CHARSET);
	        httpPost.setEntity(e);
			
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = new String(EntityUtils.toByteArray(entity),CHARSET);
			}
			httpPost.abort();
		} catch (Exception e) {
			logger.error("HttpClient请求出错！",e);
		} finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error(CLOSE_HTTP_EXCEPTION,e);
			}
		}
		return result;
	}
	
	/**
	 * @Description: http的post请求，参数为json格式
	 * @param url
	 * @param reqJson
	 * @return
	 * @return: String
	 */
	public static String doPost(String url,String reqJson){
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(url);
			
			//设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(600000).setConnectTimeout(10000).build();
			httpPost.setConfig(requestConfig);
			
			httpPost.setEntity(new StringEntity(reqJson, CHARSET));
			httpPost.setHeader("Content-Type", "application/json");
			
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = new String(EntityUtils.toByteArray(entity),CHARSET);
			}
			httpPost.abort();
		} catch (Exception e) {
			logger.error("HttpClient请求出错！",e);
		} finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error(CLOSE_HTTP_EXCEPTION,e);
			}
		}
		return result;
	}
	
	/**
     * @Description: http的post请求，参数为json格式
     * @param url
     * @param reqJson
     * @return
     * @return: String
     */
    public static String doPostAuth(String url,String reqJson,String authToken){
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            
            //设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(600000).setConnectTimeout(10000).build();
            httpPost.setConfig(requestConfig);
            
            httpPost.setEntity(new StringEntity(reqJson, CHARSET));
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", authToken);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = new String(EntityUtils.toByteArray(entity),CHARSET);
            }
            httpPost.abort();
        } catch (Exception e) {
            logger.error("url={},reqJson={},authToken={}",url,reqJson,authToken);
            logger.error("HttpClient请求出错！",e);
        } finally{
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(CLOSE_HTTP_EXCEPTION,e);
            }
        }
        return result;
    }
    /**
     * @Description: 使用客户端证书通过服务器的验证
     * @param url
     * @param charset
     * @param pfxPath
     * @param pfxPwd
     * @return
     * @return: String
     */
    public static String doCertSSLGet(String url, String charset,String pfxPath,String pfxPwd) {
        String result = null;
        CloseableHttpClient httpclient = null;
        try {
            KeyStore keyStore = getKeyStore(pfxPath, pfxPwd, "PKCS12");
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    })
                    .loadKeyMaterial(keyStore, pfxPwd.toCharArray())
                    .build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"},null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            logger.error("发送https请求失败",ex);
        } finally {
            try {
            	if(null != httpclient) {
                    httpclient.close();
            	}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * @Description: 使用客户端证书通过服务器的验证
     * @param url
     * @param reqJson
     * @param charset
     * @param pfxPath
     * @param pfxPwd
     * @return
     * @return: String
     */
    @SuppressWarnings("deprecation")
    public static String doCertSSLPost(String url, String reqJson, String charset,byte[] keyStoreBytes,String pfxPwd) {
        String result = null;
        CloseableHttpClient httpClient = null;
        try {
            KeyStore keyStore = getKeyStoreFromBytes(keyStoreBytes, pfxPwd, "PKCS12");
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                        //忽略掉对服务器端证书的校验
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    })
                    .loadKeyMaterial(keyStore, pfxPwd.toCharArray())
                    .build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1","SSLv3","TLSv1.1","TLSv1.2"}, null,  SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            HttpPost httpPost = null;
            httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(reqJson, charset));
            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            logger.error("发送https请求失败",ex);
        } finally {
            try {
            	if(null != httpClient) {
                    httpClient.close();
            	}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    
    /**
     * 获得KeyStore
     *
     * @param keyStorePath
     * @param password
     * @return
     * @throws Exception
     */
    private static KeyStore getKeyStore(String keyStorePath, String password,String type)
            throws Exception {
        FileInputStream is = new FileInputStream(keyStorePath);
        KeyStore ks = KeyStore.getInstance(type);
        ks.load(is, password.toCharArray());
        is.close();
        return ks;
    }
    
    /**
     * 从数据库的byte[]流 获得KeyStore 
     *
     * @param keyStorePath
     * @param password
     * @return
     * @throws Exception
     */
    public static KeyStore getKeyStoreFromBytes(byte[] keyStoreBytes, String password,String type)
            throws Exception {
    	InputStream is = new ByteArrayInputStream(keyStoreBytes);
        KeyStore ks = KeyStore.getInstance(type);
        ks.load(is, password.toCharArray());
        is.close();
        return ks;
    }
    
	
    public static String doCertSSLPost2(String url, Map<String,String> param, String charset,String pfxPath,String pfxPwd) {
        String result = null;
        CloseableHttpClient httpClient = null;
        try {
            KeyStore keyStore = getKeyStore(pfxPath, pfxPwd, "PKCS12");
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                        //忽略掉对服务器端证书的校验
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    })
                    .loadKeyMaterial(keyStore, pfxPwd.toCharArray())
                    .build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1","SSLv3","TLSv1.1","TLSv1.2"}, null,  SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            HttpPost httpPost = null;
            httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<>();  
            for(Map.Entry<String, String> entry : param.entrySet()){
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
            }
            StringEntity e = new UrlEncodedFormEntity(nvps, CHARSET);
            httpPost.setEntity(e);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            logger.error("发送https请求失败",ex);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public static void main(String[] args) {
        String url = "http://18.234.78.119:8088/services/collector/event";
        String authToken = "Splunk 0ebb10d1-5529-4de7-a34c-ac067b9d56fa";
        String content ="{\"event\": \"test from PostMan\"}";
        String result = doPostAuth(url, content, authToken);
        System.out.println(result);
    }
}
