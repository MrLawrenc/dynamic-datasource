package com.huize.migrationcore.entity;

import com.github.mrlawrenc.filter.entity.Request;
import com.huize.migrationcommon.entity.Job;
import com.huize.migrationcommon.reader.Reader;
import com.huize.migrationcommon.writer.Writer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author hz20035009-逍遥
 * date   2020/8/6 14:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class JobContext extends Request {
    private Job job;

    private Reader reader;
    private Writer writer;


    private Error error=new Error();

    @Data
    public static class Error {
        private boolean isContinue=true;
        private String msg;
    }
}