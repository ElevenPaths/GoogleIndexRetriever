/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.elevenpaths.googleindexretriever;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import com.elevenpaths.googleindexretriever.exceptions.CaptchaException;
import com.elevenpaths.googleindexretriever.exceptions.EmptyQueryException;
import com.elevenpaths.googleindexretriever.exceptions.ManyResultsException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebView;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;


/**
 * The <code>GoogleSearch</code> class se usa para realizar las consultas sobre
 * google y extraer los datos necesarios.
 *
 * @author Juan Manuel Tirado
 * @since 1.0
 */

public class GoogleSearch {
    private String query = "";
    private String continueCaptcha = "";
    private String q = "";
    private String idCaptcha = "";
    private String imgCaptcha = "";
    private String tokenCookie = "";
    private String referer = "";


    InputStream image;
    private static final String[] buzzWordsDefault = { "time","person","year","way","day","thing","man","world","life","hand","part","child","eye","woman","place","work","week","case","point","government","company","number","group","problem","fact","have","say","get","make","know","take","see","come","think","look","want","give","use","find","tell","ask","work","seem","feel","try","leave","call","good","new","first","last","long","great","little","own","other","old","right","big","high","different","small","large","next","early","young","important","few","public","bad","same","able","for","with","from","about","into","over","after","beneath","under","above","the","and","that","not","you","this","but","his","they","her","she","will","one","all","would","there","their" };

    private static final String[] buzzWordsSpamDefault = { "cialis","orgasms","viagra","shipping","milf","valium","pharmacy","xanax","increase","vicodin","orgasm","online","disclaimer","rolex","required","remove","prescription","hydrocodone","guaranteed","cheap","adobe","ambien","free","price","discount" };

    private ArrayList<String> keywords;
    private ArrayList<String> keywordsSpam;

    /**
     * Constructor with no query.
     *
     */
    public GoogleSearch() {
        this.keywords = new ArrayList<String>();
        this.keywordsSpam = new ArrayList<String>();
    }

    /**
     * Constructor with a query. The query is the field to search.
     *
     * @param q
     *            query to search
     */
    public GoogleSearch(String q) {
        this.query = q;
        this.keywords = new ArrayList<String>();
        this.keywordsSpam = new ArrayList<String>();
    }
    /**
     * Strips any potential XSS threats out of the value
     * @param value
     * @return
     */
    public String stripXSS( String value )
    {
        if( value == null )
            return null;

        // Avoid null characters
        value = value.replaceAll("\0", "");

        // Clean out HTML
        value = Jsoup.clean( value, Whitelist.none() );

        return value;
    }

    /**
     * Make the query to google and return the data.
     *
     * @param query
     *            textfield for google
     * @return webpage in Document format
     */
    private Document getData(String query) throws CaptchaException, EmptyQueryException, UnsupportedEncodingException {
        if (this.query.isEmpty() || this.query == null) {
            throw new EmptyQueryException();
        }

        Connection conn = null;
        Document doc = null;

        String request = "https://www.google.com/search?q=" + URLEncoder.encode( stripXSS(query), "UTF-8");
        if(!tokenCookie.isEmpty()){
            request = request + "&google_abuse=" + URLEncoder.encode(tokenCookie, "UTF-8");
        }

        try {
            conn = Jsoup
                    .connect(request)
                    .method(Method.GET)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:36.0) Gecko/20100101 Firefox/48.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Cookie", tokenCookie)
                    .header("Connection", "keep-alive")
                    .ignoreHttpErrors(true)
                    .timeout(5000);

            if(!referer.isEmpty()){
                conn.header("Referer", referer);
            }

            Connection.Response response = conn.execute();

            if (response.statusCode() == 503) {

                referer = response.url().toString();
                idCaptcha = getIDCaptcha(response.parse());

                getCaptcha("https://ipv4.google.com/sorry/image?id=" + idCaptcha + "&hl=es&" + referer.substring(referer.indexOf('?')+1));

                throw new CaptchaException();

            }

            doc = Jsoup.parse(response.body());

            // Clean the response
            Whitelist wl = new Whitelist().basic();
            wl.addAttributes("span", "class");
            Cleaner clean = new Cleaner(wl);
            doc = clean.clean(doc);
        } catch (IOException e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return doc;
    }

    public String getResults() throws EmptyQueryException, ManyResultsException, CaptchaException, UnsupportedEncodingException {
        if (this.query.isEmpty()) {
            throw new EmptyQueryException();
        }
        Document doc = getData(this.query);

        Elements data = doc.select(".st");

        if (data.size() > 1) {
            throw new ManyResultsException();
        }

        return data.text();
    }

    public Elements getResultSpam() throws EmptyQueryException, ManyResultsException, CaptchaException, UnsupportedEncodingException {
        if (this.query.isEmpty()) {
            throw new EmptyQueryException();
        }
        Document doc = getData(this.query);

        Elements data = doc.select(".st");

        return data;
    }

    public String getImageCaptcha(Document doc) {
        String img = "";
        Elements image = doc.select("img");
        if (image.size() == 1) {
            img = doc.select("img").first().attr("src");
        }

        return img;
    }

    public String getIDCaptcha(Document doc) throws UnsupportedEncodingException {
        continueCaptcha = URLEncoder.encode(doc.select("input[name=continue]").first().attr("value"), "UTF-8");
        q = URLEncoder.encode(doc.select("input[name=q]").first().attr("value"),"UTF-8");
        return doc.select("input[value~=^\\d+$]").first().attr("value");
    }

    public String responseCaptcha(String responseCaptcha) {
        String url = "https://ipv4.google.com/sorry/CaptchaRedirect";
        String request = url + "?q=" + q + "&hl=es&continue="+continueCaptcha+"&id="
                + idCaptcha + "&captcha=" + responseCaptcha + "&submit=Enviar";
        String newCookie = "";

        try {
            URL obj = new URL(request);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            // add request header
            con.setRequestProperty("Host", "ipv4.google.com");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            con.setRequestProperty("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3");
            con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            con.setRequestProperty("Cookie", tokenCookie+"; path=/; domain=google.com");
            con.addRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Referer", referer);
            //con.connect();
            boolean redirect = false;
            con.setInstanceFollowRedirects(false);
            int status = con.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = con.getHeaderField("Location");

                // open the new connnection again
                con = (HttpURLConnection) new URL(newUrl).openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
                con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                con.setRequestProperty("Referer", referer);
                con.setRequestProperty("Accept-Encoding",  "gzip, deflate");
                con.setRequestProperty("Cookie", tokenCookie);
                con.addRequestProperty("Connection", "keep-alive");


                // Find the cookie
                String nextURL = URLDecoder.decode(newUrl, "UTF-8");
                String[] k = nextURL.split("&");
                for(String a : k){
                    if(a.startsWith("google_abuse=")){
                        String temp = a.replace("google_abuse=", "");
                        String[] c = temp.split(";");
                        for(String j : c){
                            if(j.startsWith("GOOGLE_ABUSE_EXEMPTION")){
                                newCookie = j;
                            }
                        }

                    }
                }
            }

            con.connect();

            if (con.getResponseCode() == 200) {
                newCookie = tokenCookie;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

        return newCookie;

    }

    public void getCaptcha(String image) {

        try {
            URL obj = new URL(image);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            // add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
            con.addRequestProperty("Connection", "keep-alive");

            con.getResponseCode();
            tokenCookie = con.getHeaderField("Set-Cookie");

            // creating the input stream from google image
            BufferedInputStream in = new BufferedInputStream(
                    con.getInputStream());
            // my local file writer, output stream
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream("Captcha.png"));
            // until the end of data, keep saving into file.
            int i;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
            out.flush();
            in.close();
            out.close();
            con.disconnect();
        } catch (MalformedURLException e) {

        } catch (IOException e) {
            // TODO: handle exception
        }


    }

    public InputStream getImage() {
        return image;
    }

    public String getImgCaptcha() {
        return imgCaptcha;
    }

    public void setImgCaptcha(String imgCaptcha) {
        this.imgCaptcha = imgCaptcha;
    }

    public String getIdCaptcha() {
        return idCaptcha;
    }

    public void setIdCaptcha(String idCaptcha) {
        this.idCaptcha = idCaptcha;
    }

    public String getQuery() {
        return query;
    }

    public String getTokenCookie() {
        return tokenCookie;
    }

    public void setTokenCookie(String tokenCookie) {
        this.tokenCookie = tokenCookie;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void export(ArrayList<ArrayList <String>> data, String dork, String nameFile) throws IOException {

        File file = new File(nameFile);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("<style type=\"text/css\"> table.sample { border-width: 1px;  border-spacing: 2px;  border-style: outset;   border-color: black; border-collapse: separate; background-color: white; } table.sample th {   border-width: 1px;  padding: 5px; border-style: inset; border-color: green;  background-color: white; } table.sample td { border-width: 1px;  padding: 5px;  border-style: inset;   border-color: green;    background-color: white;    }</style>");
        bw.newLine();
        bw.write("<h1>#Dork: <font color=blue>" + dork + "</font><br>#Total: <font color=blue>" + data.size() + "</font></h1>");
        bw.write("<table class=sample><tr><td><font color=blue>Words</font></td><td><font color=blue>Sentences</font></td></tr>");// Start table
        for (ArrayList<String> ds : data) {
            bw.write("<tr>");
            for(String row : ds){
                if(!row.isEmpty()) {
                    bw.write("<td>" + escapeHtml4(row) + "</td>");
                    bw.newLine();
                }
            }
            bw.write("</tr>");
        }

        bw.write("</table>");
        bw.close();
        fw.close();

    }
    public void loadSpamKeywords() throws FileNotFoundException, IOException {
        FileReader file = new FileReader("spamKeywords.txt");
        BufferedReader reader = new BufferedReader(file);
        String line;

        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("#")) {
                keywordsSpam.add(line);
            }
        }

        reader.close();
        file.close();
    }

    public void loadKeywords() throws FileNotFoundException, IOException {
        FileReader file = new FileReader("keywords.txt");
        BufferedReader reader = new BufferedReader(file);
        String line;

        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("#")) {
                keywords.add(line);
            }
        }

        reader.close();
        file.close();
    }

    public void loadDefaultKeywords() {
        keywords = new ArrayList<String>(Arrays.asList(buzzWordsDefault));
    }
    public void loadDefaultSpamKeywords() {
        keywordsSpam = new ArrayList<String>(Arrays.asList(buzzWordsSpamDefault));
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keyw) {
        keywords = keyw;
    }
    public ArrayList<String> getSpamKeywords() {
        return keywordsSpam;
    }

    public void setSpamKeywords(ArrayList<String> keyw) {
        keywordsSpam = keyw;
    }

    public void saveKeywords() throws IOException {
        File file = new File("keywords.txt");
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        for (String k : keywords) {
            bw.write(k);
            bw.newLine();
        }
        bw.close();
        fw.close();

    }
    public void saveSpamKeywords() throws IOException {
        File file = new File("spamKeywords.txt");
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        for (String k : keywordsSpam) {
            bw.write(k);
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

}