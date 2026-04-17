package com.example.microproxy.service;

public interface BusinessService<T> {
    String getServiceId();
    T execute(String operation, Object... params);
}
