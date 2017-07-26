package dataflowjob.entity;  
  
public class Foo {  
    private int id;  
    private Status status;  
    public Foo(final int id,final Status status) {  
        this.id = id;  
        this.status = status;  
    }  
    public int getId() {  
        return id;  
    }  
    public void setId(int id) {  
        this.id = id;  
    }  
    public Status getStatus() {  
        return status;  
    }  
    public void setStatus(Status status) {  
        this.status = status;  
    }  
    public enum Status{  
        TODO,  
        DONE  
    }   
  
}  