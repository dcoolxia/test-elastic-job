package name.nvshen.simplejob;

import java.util.Date;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

public class MySimpleJob implements SimpleJob {
    
    @Override
    public void execute(ShardingContext context) {
        System.out.println(context.getJobName()+"：开始任务"+new Date());
        /*switch (context.getShardingItem()) {
            case 0: 
                // do something by sharding item 0
                System.out.println(context.getShardingItem()+","+
                        context.getJobName()+","+
                        context.getJobParameter()+","+
                        context.getShardingParameter()+","+
                        context.getShardingTotalCount());
                break;
            case 1: 
                // do something by sharding item 1
                System.out.println(context.getShardingItem()+","+
                        context.getJobName()+","+
                        context.getJobParameter()+","+
                        context.getShardingParameter()+","+
                        context.getShardingTotalCount());
                break;
            case 2: 
                // do something by sharding item 2
                System.out.println(context.getShardingItem()+","+
                        context.getJobName()+","+
                        context.getJobParameter()+","+
                        context.getShardingParameter()+","+
                        context.getShardingTotalCount());
                break;
            // case n: ...
        }*/
    }
}