package com.simit.data;

import com.simit.entity.SamplingSum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class JdbcSamplingSumRepository implements SamplingSumRepository {

    private final static Logger logger = LoggerFactory.getLogger(JdbcSamplingSumRepository.class);


    private JdbcTemplate jdbc;

    @Autowired
    public JdbcSamplingSumRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    @Override
    public SamplingSum findOne(String batchId, String samplingFlag) {
        String sql = "SELECT id, batch_id, sampling_flag, successful_num, failure_num, total_num, ts FROM t_sampling_sum WHERE batch_id=? AND sampling_flag=?";
        try{
            return jdbc.queryForObject(sql, this::mapToBatchSum, batchId, samplingFlag);
        }catch (EmptyResultDataAccessException e){
            logger.warn(e.toString());
        }
        return null;
    }

    @Override
    public SamplingSum save(SamplingSum bs) {
        String sql = "UPDATE t_sampling_sum SET successful_num=?, failure_num=?, ts=? WHERE id=?";

        if(1 == jdbc.update(sql, bs.getSuccessfulNum(), bs.getFailureNum(), bs.getTs(), bs.getId())){
            return bs;
        }

        return null;
    }


    private SamplingSum mapToBatchSum(ResultSet rs, int rowNum) throws SQLException {
        return new SamplingSum(rs.getLong("id"),
                rs.getString("batch_id"),
                Integer.valueOf(rs.getString("sampling_flag")),
                rs.getInt("successful_num"),
                rs.getInt("failure_num"),
                rs.getInt("total_num"),
                rs.getLong("ts")
                );
    }
}
