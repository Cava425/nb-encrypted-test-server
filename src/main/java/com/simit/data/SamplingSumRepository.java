package com.simit.data;

import com.simit.entity.SamplingSum;

/**
 * @Author: ys xu
 * @Date: 2020/11/2 16:32
 */
public interface SamplingSumRepository {

    SamplingSum findOne(String batchId, String samplingFlag);

    SamplingSum save(SamplingSum bs);

}
