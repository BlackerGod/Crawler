package crowler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dao.Project;
import dao.ProjectDao;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Crawler {
    private HashSet<String> htmlBlackList = new HashSet<>();

    private Gson gson = new GsonBuilder().create();

    {
        htmlBlackList.add("https://github.com/events");
        htmlBlackList.add("https://github.community");
        htmlBlackList.add("https://github.com/about");
        htmlBlackList.add("https://github.com/contact");
        htmlBlackList.add("https://github.com/pricing");
    }
    private  OkHttpClient okHttpClient = new OkHttpClient();

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler();
        //获取我们要抓的页面
        String html = crawler.getUrl("https://github.com/akullpp/awesome-java/blob/master/README.md");
        //从这个页面获得每一个项目保存到result；
        List<Project> result = crawler.parseProjectList(html);
        ProjectDao projectDao = new ProjectDao();

        for (int i = 0;i < result.size();i++){
            try {
                Project tmp = result.get(i);
                System.out.println("抓取:"+tmp.getName()+"...");
                String repoName = crawler.getRepoName(tmp.getUrl());
                String jsonString = crawler.getRepoInfo(repoName);
                crawler.parseRepoInfo(jsonString,tmp);
                projectDao.save(tmp);
            } catch (Exception e) {
               throw new RuntimeException("抓取失败"+result.get(i).getUrl());
            }

        }
    }

    public  String getUrl(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        if(!response.isSuccessful()){
            System.out.println("请求失败");
            return null;
        }
        return response.body().string();
    }

    public  List<Project> parseProjectList(String html){
        ArrayList<Project> result = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("li");

        for (Element li : elements){
            Elements allLink = li.getElementsByTag("a");
            if(allLink.size() == 0){
                continue;
            }
            Element link = allLink.get(0);
            String url = link.attr("href");
            if(!url.startsWith("https://github.com")){
                continue;
            }
            if(htmlBlackList.contains(url)){
                continue;
            }
            Project project = new Project();
            project.setName(link.text());
            project.setUrl(link.attr("href"));
            project.setDescription(li.text());
            result.add(project);
        }
        return result;

    }
//
    /**
    * respoName仓库名/项目名
    * 调用Github的API获取指定项目仓库的信息；
    * */
    public String getRepoInfo(String respoName) throws IOException {
        String username = "";
        String password = "";
        //身份认证,把用户名密码加密后，得到一个字符串，把这个字符串放在HTTP header里面
        String credential = Credentials.basic(username,password);

        String url = "https://api.github.com/repos/"+respoName;
        Request request = new Request.Builder().url(url).header("Authorization",credential).build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        if(!response.isSuccessful()){
            System.out.println("获取项目失败");
            return null;
        }
        return response.body().string();
    }
    /**
     * 提取出来仓库名字和项目名字
     * */
    public String getRepoName(String url){
       int lastOne = url.lastIndexOf("/");
       int lastTwo = url.lastIndexOf("/",lastOne-1);
       if(lastOne == -1 || lastTwo == -1){
           System.out.println("url不是一个项目的url"+url);
           return null;
       }
       return url.substring(lastTwo+1);
    }


    /**
     * 第一个参数，表示Github获取到的结果;
     * 第二个参数,每个项目;
     * */
    public void parseRepoInfo(String jsonString,Project project){
        Type type = new TypeToken<HashMap<String,Object>>(){}.getType();
        HashMap<String,Object> hashMap = gson.fromJson(jsonString,type);
        project.setStarCount(((Double) hashMap.get("stargazers_count")).intValue());
        project.setForkCount(((Double) hashMap.get("forks_count")).intValue());
        project.setOpenIssueCount(((Double) hashMap.get("open_issues_count")).intValue());
    }
}
