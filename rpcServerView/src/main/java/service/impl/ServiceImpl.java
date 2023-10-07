package service.impl;

import github.xunolan.rpcproject.annotation.server.RpcService;
import github.xunolan.rpcproject.api.ServiceApi;

@RpcService(ImplementClazz = ServiceApi.class)
public class ServiceImpl implements ServiceApi {
    @Override
    public String hello(String content, int id) {
        return content + id + "serverResponse";
    }
}
