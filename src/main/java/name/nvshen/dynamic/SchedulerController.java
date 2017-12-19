package name.nvshen.dynamic;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Resource
    private ZookeeperRegistryCenter regCenter;

//    @Resource
//    private JobEventConfiguration jobEventConfiguration;

    private void add(SimpleJob simpleJob, String jobName, String cron, int shardingTotalCount,
            String jobParameter) {
        new SpringJobScheduler(simpleJob, regCenter,
                getLiteJobConfiguration(simpleJob.getClass(), cron, shardingTotalCount, jobParameter, jobName)).init();
    }

    private LiteJobConfiguration getLiteJobConfiguration(Class<? extends SimpleJob> jobClass, String cron,
            int shardingTotalCount, String jobParameter, String jobName) {
        return LiteJobConfiguration
                .newBuilder(
                        new SimpleJobConfiguration(
                                JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount)
                                        .jobParameter(jobParameter)
                                        //.shardingItemParameters(shardingItemParameters)
                                        .build(),
                                jobClass.getCanonicalName()))
                .overwrite(true)
                .build();
    }

    /**
     * 增加一个作业调度
     * 
     * @return 成功标识
     */
    @SuppressWarnings("deprecation")
    @RequestMapping("/add/{jobName}")
    public boolean add(@PathVariable("jobName")String jobName) {
        // cron表达式：30 20 18 30 12 ? 2017
        // 执行时间：2017-12-30 18:20:30
        // 0/5 * * * * ?
        Date date = new Date();
        int minutes = date.getMinutes()+1;
        date.setMinutes(minutes);
        String cron = CronUtil.date2Cron(date);
        add(new MySimpleJob(), jobName, cron, 1, "0=Beijing,1=Shanghai,2=Guangzhou");
        return true;
    }

    /**
     * 修改一个作业调度
     * 
     * @return 成功标识
     */
    @RequestMapping("/update/{jobName}")
    public boolean update(@PathVariable("jobName")String jobName) {
        JobRegistry.getInstance().getJobScheduleController(jobName)
                .rescheduleJob("0/10 * * * * ?");
        return true;
    }

    /**
     * 删除一个作业调度
     * 
     * @return 成功标识
     */
    @RequestMapping("/delete/{jobName}")
    public boolean delete(@PathVariable("jobName")String jobName) {
        JobRegistry.getInstance().getJobScheduleController(jobName).shutdown();
        return true;
    }
}