package com.huize.migrationcommon;

import java.util.Collection;

/**
 * @author hz20035009-逍遥
 * date   2020/7/1 16:01
 * 当reader读取到数据时，会回调通知writer，可以借用此接口实现流式查询，需要各自reader、writer实现
 */
public interface NotifyWriterListener extends Listener {

    /**
     * 发送一行数据
     */
    void sendData(Collection<String> row);
}