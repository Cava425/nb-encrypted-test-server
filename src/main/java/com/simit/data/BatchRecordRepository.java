package com.simit.data;

import com.simit.entity.BatchRecord;

/**
 * @Author: ys xu
 * @Date: 2020/12/23 21:20
 */
public interface BatchRecordRepository {

    BatchRecord find(String gasId);

    BatchRecord save(BatchRecord bd);
}
