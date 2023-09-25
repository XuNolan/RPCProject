package service.impl;

import api.ServiceApi;


public class ServiceImpl implements ServiceApi {
    @Override
    public String hello(String content, int id) {
        return content + id + "serverResponse";
    }
}
