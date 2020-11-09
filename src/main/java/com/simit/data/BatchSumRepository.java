package com.simit.data;

import com.simit.entity.BatchSum;

/**
 * @Author: ys xu
 * @Date: 2020/11/2 16:32
 */
public interface BatchSumRepository {

    BatchSum findOne(String batchId, String samplingFlag);

    BatchSum save(BatchSum bs);

}
