package name.nvshen.dataflowjob;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * 基于流式数据的处理job
 * 
 * @author David
 */
public class MyDataflowJobApp {
    public static void main(String[] args) {
        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(
                new ZookeeperConfiguration("localhost:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }

    private static LiteJobConfiguration createJobConfiguration() {
        // 创建作业配置
        JobCoreConfiguration dataFlowCoreConfig = JobCoreConfiguration.newBuilder("demoDataflowJob", "0/5 * * * * ?", 3)
                .shardingItemParameters("0=0,1=1,2=2").build();
        DataflowJobConfiguration dataflowJobConfig = new DataflowJobConfiguration(dataFlowCoreConfig,
                MyDataflowJob.class.getCanonicalName(), true);// 第三个参数isStreaming，用于控制是否流式不停歇的处理数据
        LiteJobConfiguration result = LiteJobConfiguration.newBuilder(dataflowJobConfig).overwrite(true)// 覆盖配置
                .build();
        return result;
    }
}