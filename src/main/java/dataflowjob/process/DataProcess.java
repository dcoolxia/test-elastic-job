package dataflowjob.process;  
  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Map;  
import java.util.concurrent.ConcurrentHashMap;  
  
import dataflowjob.entity.Foo;  
  
public class DataProcess {  
  
    private Map<Integer, Foo> data = new ConcurrentHashMap<>(30, 1);  
    public DataProcess()  
    {  
        for(int i=0;i<30;i++){  
            data.put(i, new Foo(i,Foo.Status.TODO));  
        }  
    }  
    public List<Foo> getData(String tailId,int shardNum)  
    {  
        int intId  = Integer.parseInt(tailId);  
        List<Foo> result = new ArrayList<Foo>();  
        for (Map.Entry<Integer, Foo> each : data.entrySet()) {  
            Foo foo = each.getValue();  
            int key = each.getKey();  
            if (key % shardNum == intId && foo.getStatus() == Foo.Status.TODO) {  
                result.add(foo);  
            }  
        }  
        return result;  
    }  
    public void setData(int i){  
        data.get(i).setStatus(Foo.Status.DONE);  
    }  
  
}  