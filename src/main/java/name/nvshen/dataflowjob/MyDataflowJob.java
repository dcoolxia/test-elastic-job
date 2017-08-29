package name.nvshen.dataflowjob;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;

import name.nvshen.dataflowjob.entity.Foo;
import name.nvshen.dataflowjob.process.DataProcess;
import name.nvshen.dataflowjob.process.DataProcessFactory;

/**
 * 作业的逻辑处理部分
 * 
 * @author David
 */
public class MyDataflowJob implements DataflowJob<Foo> {
    private DataProcess dataProcess = DataProcessFactory.getDataProcess();

    @Override
    public List<Foo> fetchData(ShardingContext shardingContext) {
        List<Foo> result = new ArrayList<Foo>();
        result = dataProcess.getData(shardingContext.getShardingParameter(), shardingContext.getShardingTotalCount());
        System.out.println(String.format("fetchData------Thread ID: %s, Date: %s, Sharding Context: %s, Action: %s, Data: %s",
                Thread.currentThread().getId(), new Date(), shardingContext, "fetch data", result));
        return result;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<Foo> data) {
        System.out.println(String.format("processData------Thread ID: %s, Date: %s, Sharding Context: %s, Action: %s, Data: %s",
                Thread.currentThread().getId(), new Date(), shardingContext, "finish data", data));
        for (Foo foo : data) {
            dataProcess.setData(foo.getId());
        }
    }

}