package com.simit.data;

import com.simit.entity.EncryptResult;

/**
 * @Author: ys xu
 * @Date: 2020/10/28 9:14
 */
public interface EncryptResultRepository {

    EncryptResult findOne(String gasId);

    EncryptResult save(EncryptResult result);
}
