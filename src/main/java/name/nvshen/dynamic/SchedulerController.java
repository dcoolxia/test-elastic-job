package name.nvshen.dynamic;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.quartz.CronExpression;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
//import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.schedule.JobRegistry;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import name.nvshen.simplejob.MySimpleJob;
import name.nvshen.utils.CronUtil;

/**
 * 作业动态配置
 * 
 * @author David
 *
 */
@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    private static CuratorFramework client = null;
    private static String ZK_ADDRESS = "localhost:2181";
    private static String NAME_SPACE = "test-elastic-job";
    
    @Resource
    private ZookeeperRegistryCenter regCenter;

//    @Resource
//    private JobEventConfiguration jobEventConfiguration;

    
    @PostConstruct
    public synchronized void init() {
        if (client != null)
            return;

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder().connectString(ZK_ADDRESS).sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy).namespace(NAME_SPACE).build();
        client.start();
        
        JSONObject jsonObj = null;
        SimpleJob simpleJob = null;
        try {
            List<String> nodeList = client.getChildren().forPath("/");
            for (String node : nodeList) {
                String dataStr = new String(client.getData().forPath("/"+ node +"/config"), "UTF-8");
                jsonObj = JSON.parseObject(dataStr);
                simpleJob = (SimpleJob) Class.forName(jsonObj.getString("jobClass")).newInstance();
                boolean isAfter = CronUtil.cron2Date(jsonObj.getString("cron")).after(new Date());
                System.out.println(jsonObj.getString("jobName")+"===="+isAfter+"========================="+jsonObj.getString("cron"));
                if (isAfter) {
                    add(simpleJob, jsonObj.getString("jobName"), jsonObj.getString("cron"), 1,
                            jsonObj.getString("jobParameter"), jsonObj.getString("shardingItemParameters"));
                }
            }
        } catch (ParseException e) {
            System.out.println(jsonObj.getString("jobName")+"==========非指定日期类型："+jsonObj.getString("cron"));
            add(simpleJob, jsonObj.getString("jobName"), jsonObj.getString("cron"), 1,
                    jsonObj.getString("jobParameter"), jsonObj.getString("shardingItemParameters"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void add(SimpleJob simpleJob, String jobName, String cron, int shardingTotalCount, String jobParameter,
            String shardingItemParameters) {
        new SpringJobScheduler(simpleJob, regCenter, getLiteJobConfiguration(simpleJob.getClass(), cron,
                shardingTotalCount, jobName, jobParameter, shardingItemParameters)).init();
    }

    private LiteJobConfiguration getLiteJobConfiguration(Class<? extends SimpleJob> jobClass, String cron,
            int shardingTotalCount, String jobName, String jobParameter, String shardingItemParameters) {
        return LiteJobConfiguration
                .newBuilder(new SimpleJobConfiguration(
                        JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount)
                                .jobParameter(jobParameter)
                                .shardingItemParameters(shardingItemParameters)
                                .build(),
                        jobClass.getCanonicalName()))
                .overwrite(true)
                .build();
    }

    /**
     * 动态增加job
     */
    @RequestMapping("/add/{jobName}")
    public boolean add(@PathVariable("jobName")String jobName,
            String cron) {
        // cron表达式：30 20 18 30 12 ? 2017
        // 执行时间：2017-12-30 18:20:30
        // 0/5 * * * * ?
        /*Date date = new Date();
        int minutes = date.getMinutes()+1;
        date.setMinutes(minutes);
        String cron = CronUtil.date2Cron(date);*/
        
        // http://localhost:8080/test-elastic-job/scheduler/add/job4?cron=00 28 20 25 12 ? 2017
        // http://localhost:8080/test-elastic-job/scheduler/add/job4?cron=*/5 * * * * ?
        
        add(new MySimpleJob(), jobName, cron, 1, "param", "0=Beijing,1=Shanghai");
        System.out.println("add："+cron);
        return true;
    }

    /**
     * 动态修改job
     */
    @RequestMapping("/update/{jobName}")
    public boolean update(@PathVariable("jobName")String jobName,
            String cron) {
        JobRegistry.getInstance().getJobScheduleController(jobName)
                .rescheduleJob(cron);
        System.out.println("update");
        return true;
    }

    /**
     * 动态删除job
     */
    @RequestMapping("/delete/{jobName}")
    public boolean delete(@PathVariable("jobName")String jobName) {
        JobRegistry.getInstance().getJobScheduleController(jobName).shutdown();
        System.out.println("delete");
        return true;
    }
    
    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder().connectString(ZK_ADDRESS).sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy).namespace(NAME_SPACE).build();
        client.start();
        
        try {
            // {"jobName":"job4","jobClass":"name.nvshen.simplejob.MySimpleJob","jobType":"SIMPLE",
            // "cron":"31 01 18 19 12 ? 2017",
            // "shardingTotalCount":1,"shardingItemParameters":"","jobParameter":"0\u003dBeijing,1\u003dShanghai,2\u003dGuangzhou","failover":false,"misfire":true,"description":"","jobProperties":{"job_exception_handler":"com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler","executor_service_handler":"com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler"},"monitorExecution":true,"maxTimeDiffSeconds":-1,"monitorPort":-1,"jobShardingStrategyClass":"","reconcileIntervalMinutes":10,"disabled":false,"overwrite":true}
            List<String> nodeList = client.getChildren().forPath("");
            for (String node : nodeList) {
                String dataStr = new String(client.getData().forPath("/"+ node +"/config"), "UTF-8");
                JSONObject jsonObj = JSON.parseObject(dataStr);
                System.out.println(jsonObj.getString("jobName"));
                System.out.println(jsonObj.getString("cron"));
                System.out.println(jsonObj.getString("jobParameter"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}