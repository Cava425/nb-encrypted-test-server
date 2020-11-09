package com.simit.data;

import com.simit.entity.AutoResult;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: ys xu
 * @Date: 2020/10/11 21:11
 */

public interface AutoResultRepository {

    List<AutoResult> findAll(String gasId);

    List<AutoResult> findAllSuccessful(String gasId);

    AutoResult save(AutoResult result);



    int[] delete(List<AutoResult> results);

    int[] saveToSuccessful(List<AutoResult> results);

    int[] saveToFailed(List<AutoResult> results);

}
