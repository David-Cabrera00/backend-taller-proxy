package com.example.microproxy.proxy;

public interface MicroserviceProxy<T> {
    T execute(String operation, Object... params);
}