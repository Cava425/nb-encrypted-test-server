package com.simit.data;

import com.simit.entity.BatchRecord;
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
 * @Date: 2020/12/23 21:20
 */
@Repository
public class JdbcBatchRecordRepository implements BatchRecordRepository{

    private final static Logger logger = LoggerFactory.getLogger(JdbcBatchRecordRepository.class);


    @Autowired
    JdbcTemplate jdbc;

    @Override
    public BatchRecord find(String gasId) {
        String sql = "SELECT deviceid, batch_id, sampling_flag, is_tested FROM t_batch_record WHERE deviceid=? AND sampling_flag IS NOT NULL AND is_tested IS NULL";

        try{
            return jdbc.queryForObject(sql, this::mapToBatchRecord, gasId);
        }catch (EmptyResultDataAccessException e){
            logger.info("未查询到燃气表的批次信息");
        }

        return null;
    }

    public BatchRecord save(BatchRecord bd){
        String sql = "UPDATE t_batch_record SET is_tested=? WHERE deviceid=?";

        if(1 == jdbc.update(sql, bd.getIsTested(), bd.getDeviceId())){
            return bd;
        }

        return null;
    }

    private BatchRecord mapToBatchRecord(ResultSet rs, int rowNum) throws SQLException{
        return new BatchRecord(
                rs.getString("deviceid"),
                rs.getString("batch_id"),
                rs.getString("sampling_flag"),
                rs.getString("is_tested")
        );
    }
}
