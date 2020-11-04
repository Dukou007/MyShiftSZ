package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import com.pax.common.util.RegexMatchUtils;
import com.pax.tms.group.model.Group;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.codec.Base64;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

public class Files {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        byte[] content = FileUtils.readFileToByteArray(
                new File("E:\\PAX\\TMS3.0\\workspace\\tms3\\tms3-webapp\\PXMaster package\\Uninstall_All_Forms.zip"));
        String text = new String(Base64.encode(content), "ISO8859-1");
        System.out.println(text);
    

    }

    private long crc32Hash(String installedApps) {
        CRC32 crc32 = new CRC32();
        crc32.update(installedApps.getBytes(Charset.forName("UTF-8")));
        return crc32.getValue();
    }

    @Test
    public void testTimeZone() throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("timeZone.json");
        String timeZoneJson = IOUtils.toString(in, Charsets.toCharset("UTF-8"));
        System.out.println(timeZoneJson);
    }

    @Test
    public void testZonedDateTime() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Canada/Mountain"));

        ZonedDateTime zdt = ZonedDateTime.parse("2018-12-25 12:0010", formatter);
        System.out.println(zdt);
    }

    @Test
    public void testLocalDateTime() {
        ZonedDateTime zd = ZonedDateTime.now(ZoneId.of("UTC+3"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Canada/Mountain"));

        System.out.println(zd);
        System.out.println(zd.format(formatter));
        Date dd = Date.from(zd.toInstant());
        System.out.println(dd);
    }

    @Test
    public void testZoneIds() {
        Set<String> zoneIds = ZoneId.getAvailableZoneIds();
        System.out.println(zoneIds);
    }
    
    @Test
    public void testPattern(){
    	System.out.println(RegexMatchUtils.isMatcher(" sdsa", "\\s+"));
    	
     
    }

}
