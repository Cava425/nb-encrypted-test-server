package com.simit.data;

import com.simit.entity.Result;

import java.util.List;

/**
 * @Author: ys xu
 * @Date: 2020/11/6 15:59
 */
public interface ResultRepository {

    List<Result> findAll(String gasId);

    List<Result> findAllSuccessful(String gasId);

    Result save(Result result);

    int[] delete(List<Result> results);

    int[] saveToSuccessful(List<Result> results);

    int[] saveToFailed(List<Result> results);
}
