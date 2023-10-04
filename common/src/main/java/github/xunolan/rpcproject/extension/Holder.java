package github.xunolan.rpcproject.extension;

//目的是为了在value上包一层，使其具有volatile的特性，防止初始化对象时重排导致双重锁校验时返回无效的对象；
public class Holder<T> {
    private volatile T value;
    public T get(){
        return value;
    }
    public void set(T value){
        this.value = value;
    }
}
