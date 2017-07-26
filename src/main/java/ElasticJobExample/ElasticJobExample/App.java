package ElasticJobExample.ElasticJobExample;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * Hello world!
 * 
 */
public class App {
    public static void main(String[] args) {
        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(
                new ZookeeperConfiguration("localhost:2191,localhost:2192,localhost:2193", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }

    private static LiteJobConfiguration createJobConfiguration() {
        // 创建作业配置
        JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder("myDataFlowTest", "0/10 * * * * ?", 3)
                .shardingItemParameters("0=0,1=1,2=2").build();
        DataflowJobConfiguration dataflowJobConfig = new DataflowJobConfiguration(coreConfig,
                JavaDataflowJob.class.getCanonicalName(), true);
        LiteJobConfiguration result = LiteJobConfiguration.newBuilder(dataflowJobConfig).build();
        return result;
    }
}