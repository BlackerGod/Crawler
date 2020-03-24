package crowler;

import dao.DBUtil;
import dao.Project;
import dao.ProjectDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadCrawler extends Crawler {
    /**
     * 使用多线程的方式重新组织核心逻辑，访问Github
     * @param args
     */
    private static final int THREADCOUNT = 10;
    public static void main(String[] args) throws IOException {
        ThreadCrawler crawler = new ThreadCrawler();
        //1.获取首页内容
        String html = crawler.getUrl("https://github.com/akullpp/awesome-java/blob/master/README.md");
        //2.分析项目列表
        List<Project> projects = crawler.parseProjectList(html);
        //3.遍历项目列表，使用多线程方式,创建一个固定线程池；
        ExecutorService executorService = Executors.newFixedThreadPool(THREADCOUNT);

        //executorServices两种提交操作
        //1.execute不关注任务结果
        //2.sumbit关注任务结果
        List<Future<?>> taskResults = new ArrayList<>();
        for (Project project: projects){
            Future<?> taskResult = executorService.submit(new CrawlerTask(project,crawler));
            taskResults.add(taskResult);
        }
        //所有线程结束才可执行下一条
        for (Future<?> taskResult : taskResults){

            //调用Get方法就会阻塞，阻塞到改任务执行完毕，get才会返回
            try {
                taskResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        //所有任务都执行完毕了,关闭线程池,回收资源
        executorService.shutdown();

        System.out.println("开始插入数据库");
        ProjectDao projectDao = new ProjectDao();
        for (Project project : projects){
            projectDao.save(project);
        }
        System.out.println("插入完成");
    }

    static class CrawlerTask implements Runnable{

        private Project project;
        private ThreadCrawler threadCrawler;

        public CrawlerTask(Project project, ThreadCrawler threadCrawler) {
            this.project = project;
            this.threadCrawler = threadCrawler;
        }

        @Override
        public void run() {
            //1.调用API获取项目数据
            //2.解析项目数据
            try {
                System.out.println("craw"+project.getName()+"...");
                String repoName = threadCrawler.getRepoName(project.getUrl());
                String jsonString = threadCrawler.getRepoInfo(repoName);
                threadCrawler.parseRepoInfo(jsonString,project);
            } catch (IOException e) {
                throw new RuntimeException("插入失败"+project.getUrl());
            }

        }
    }

}
