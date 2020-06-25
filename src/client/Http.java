package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
 
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
 
public class Http {
    private static final String DEFAULT_ENCODING = "UTF-8";
     
    private String url;
    private MultipartEntityBuilder params;
     
    /**
     * @param url ������ url
     */
    public Http(String url){
        this.url = url;
         
        params = MultipartEntityBuilder.create();
    }
     
    /**
     * Map ���� �Ѳ����� �Ķ���� �� �߰��ϴ� �޼ҵ�
     * @param param �Ķ���͵��� ��� ��, �Ķ���͵��� UTF-8�� ���ڵ� ��
     * @return
     */
    public Http addParam(Map<String, Object> param){
        return addParam(param, DEFAULT_ENCODING);
    }
     
    /**
     * Map ���� �Ѳ����� �Ķ���� �� �߰��ϴ� �޼ҵ�
     * @param param �Ķ���͵��� ��� ��
     * @param encoding �Ķ���� encoding charset
     * @return
     */
    public Http addParam(Map<String, Object> param, String encoding){
        for( Map.Entry<String, Object> e : param.entrySet() ){
            if (e.getValue() instanceof File) {
                addParam(e.getKey(), (File)e.getValue(), encoding);
            }else{
                addParam(e.getKey(), (String)e.getValue(), encoding);
            }
        }
        return this;
    }
     
    /**
     * ���ڿ� �Ķ���͸� �߰��Ѵ�.
     * @param name �߰��� �Ķ���� �̸�
     * @param value �Ķ���� ��
     * @return
     */
    public Http addParam(String name, String value){
        return addParam(name, value, DEFAULT_ENCODING);
    }
     
    public Http addParam(String name, String value, String encoding){
        params.addPart(name, new StringBody(value, ContentType.create("text/plain", encoding)));
         
        return this;
    }
     
    /**
     * ���ε��� ���� �Ķ���͸� �߰��Ѵ�.
     * @param name
     * @param file
     * @return
     */
    public Http addParam(String name, File file){
        return addParam(name, file, DEFAULT_ENCODING);
    }
     
    public Http addParam(String name, File file, String encoding){
        if( file.exists() ){
            try{
                params.addPart(
                        name,
                        new FileBody(file, ContentType.create("application/octet-stream"),
                        URLEncoder.encode(file.getName(), encoding)));
            }catch( Exception ex ){ ex.printStackTrace(); }
             
        }
         
        return this;
    }
 
    /**
     * Ÿ�� URL �� POST ��û�� ������.
     * @return ��û���
     * @throws Exception
     */
    public String submit() throws Exception{
        CloseableHttpClient http = HttpClients.createDefault();
        StringBuffer result = new StringBuffer();
         
        try{
            HttpPost post = new HttpPost(url);
            post.setEntity(params.build());
             
            CloseableHttpResponse response = http.execute(post);
             
            try{
                HttpEntity res = response.getEntity();
                BufferedReader br = new BufferedReader(
                                    new InputStreamReader(res.getContent(), Charset.forName("UTF-8")));
                 
                String buffer = null;
                while( (buffer=br.readLine())!=null ){
                    result.append(buffer).append("\r\n");
                }
            }finally{
                response.close();
            }
        }finally{
            http.close();
        }
 
        return result.toString();
    }
}

