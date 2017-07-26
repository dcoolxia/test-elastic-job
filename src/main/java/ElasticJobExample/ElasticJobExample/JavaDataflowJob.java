package ElasticJobExample.ElasticJobExample;  
  
import java.util.ArrayList;  
import java.util.Date;  
import java.util.List;  
  
import com.dangdang.ddframe.job.api.ShardingContext;  
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;  
  
import dataflowjob.entity.Foo;  
import dataflowjob.process.DataProcess;  
import dataflowjob.process.DataProcessFactory;  
  
public class JavaDataflowJob implements DataflowJob<Foo> {  
    private DataProcess dataProcess = DataProcessFactory.getDataProcess();  
      
    @Override  
    public List<Foo> fetchData(ShardingContext context) {  
        List<Foo> result = new ArrayList<Foo>();  
        result = dataProcess.getData(context.getShardingParameter(), context.getShardingTotalCount());  
        System.out.println(String.format("------Thread ID: %s, Date: %s, Sharding Context: %s, Action: %s, Data: %s", Thread.currentThread().getId(), new Date(), context, "fetch data",result));  
        return result;  
    }  
      
    @Override  
    public void processData(ShardingContext shardingContext, List<Foo> data) {  
        System.out.println(String.format("------Thread ID: %s, Date: %s, Sharding Context: %s, Action: %s, Data: %s", Thread.currentThread().getId(), new Date(), shardingContext, "finish data",data));  
        for(Foo foo:data){  
            dataProcess.setData(foo.getId());  
        }  
    }  
  
} 