package name.nvshen.dataflowjob.process;

/**
 * 具体处理工厂类
 * 
 * @author David
 */
public class DataProcessFactory {
    private static DataProcess dataProcess = new DataProcess();

    public static DataProcess getDataProcess() {
        return dataProcess;
    }

}