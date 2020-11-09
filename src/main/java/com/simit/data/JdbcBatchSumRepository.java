package com.simit.data;

import com.simit.entity.BatchSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author: ys xu
 * @Date: 2020/11/2 16:37
 */

@Repository
public class JdbcBatchSumRepository implements BatchSumRepository {

    private JdbcTemplate jdbc;

    @Autowired
    public JdbcBatchSumRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    @Override
    public BatchSum findOne(String batchId, String samplingFlag) {
        String sql = "SELECT id, batch_id, sampling_flag, successful_num, failure_num, total_num, ts FROM t_batch_sum WHERE batch_id=? AND sampling_flag=?";
        try{
            return jdbc.queryForObject(sql, this::mapToBatchSum, batchId, samplingFlag);
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BatchSum save(BatchSum bs) {
        String sql = "UPDATE t_batch_sum SET successful_num=?, failure_num=?, total_num=? WHERE id=?";

        if(1 == jdbc.update(sql, bs.getSuccessfulNum(), bs.getFailureNum(), bs.getTotalNum(), bs.getId())){
            return bs;
        }

        return null;
    }


    private BatchSum mapToBatchSum(ResultSet rs, int rowNum) throws SQLException {
        return new BatchSum(rs.getLong("id"),
                rs.getString("batch_id"),
                Integer.valueOf(rs.getString("sampling_flag")),
                rs.getInt("successful_num"),
                rs.getInt("failure_num"),
                rs.getInt("total_num"),
                rs.getLong("ts")
                );
    }
}
