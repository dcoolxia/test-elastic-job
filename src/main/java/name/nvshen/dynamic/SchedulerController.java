package name.nvshen.dynamic;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.schedule.JobRegistry;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import name.nvshen.simplejob.MySimpleJob;

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

    @Resource
    private JobEventConfiguration jobEventConfiguration;

    private void add(final SimpleJob simpleJob, final String cron, final int shardingTotalCount,
            final String shardingItemParameters) {
        new SpringJobScheduler(simpleJob, regCenter,
                getLiteJobConfiguration(simpleJob.getClass(), cron, shardingTotalCount, shardingItemParameters),
                jobEventConfiguration).init();
    }

    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
            final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration
                .newBuilder(
                        new SimpleJobConfiguration(
                                JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount)
                                        .shardingItemParameters(shardingItemParameters).build(),
                                jobClass.getCanonicalName()))
                .overwrite(true).build();
    }

    /**
     * 增加一个作业调度
     * 
     * @return 成功标识
     */
    @RequestMapping("/add")
    public boolean add() {
        add(new MySimpleJob(), "0/5 * * * * ?", 3, "0=Beijing,1=Shanghai,2=Guangzhou");
        return true;
    }

    /**
     * 修改一个作业调度
     * 
     * @return 成功标识
     */
    @RequestMapping("/update")
    public boolean update() {
        JobRegistry.getInstance().getJobScheduleController(SimpleJob.class.getName())
                .rescheduleJob("0/20 * * * * ?");
        return true;
    }

    /**
     * 删除一个作业调度
     * 
     * @return 成功标识
     */
    @RequestMapping("/delete")
    public boolean delete() {
        JobRegistry.getInstance().getJobScheduleController(SimpleJob.class.getName()).shutdown();
        return true;
    }
}