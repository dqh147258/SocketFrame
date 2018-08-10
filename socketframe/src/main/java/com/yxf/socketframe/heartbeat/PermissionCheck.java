package com.yxf.socketframe.heartbeat;

public interface PermissionCheck<D> {

    byte[] encode(D d);

    D decode(byte[] bytes);

    D generateNewData();

    D change(D d);

    boolean equals(D d1, D d2);


}
